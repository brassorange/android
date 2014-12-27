package com.brassorange.eventapp.services;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.brassorange.eventapp.AgendaFragment;
import com.brassorange.eventapp.EventApp;
import com.brassorange.eventapp.ProgramFragment;
import com.brassorange.eventapp.R;
import com.brassorange.eventapp.model.Person;
import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.model.ProgramItem;
import com.brassorange.eventapp.services.FileUtils;

public class Updater extends AsyncTask<String, Void, Program> {

	private Activity activity;

	public Updater(Activity activity) {
		this.activity = activity;
	}

	@Override
	protected Program doInBackground(String... params) {
		// Prepare the event list
		String programXml = "";
		String peopleXml = "";

		// Read from file
        try {
            InputStream in_s = activity.getResources().openRawResource(R.raw.agenda);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            programXml = new String(b);

            in_s = activity.getResources().openRawResource(R.raw.people);
            b = new byte[in_s.available()];
            in_s.read(b);
            peopleXml = new String(b);

            //FileRetriever fileRetriever = new FileRetriever();
			//responseXml = fileRetriever.retrieve(getBaseContext().getFilesDir().getPath() + FILE_AGENDA);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Get from HTTP
        if (programXml == "" || (params.length > 0 && params[0] == "http")) {
	        HttpRetriever httpRetriever = new HttpRetriever();
	        Log.d(this.getClass().getSimpleName(), "update from http ...");
	        String programXmlHttp = httpRetriever.retrieve(((EventApp)activity.getApplication()).getUrlAgenda());
	        Log.d(this.getClass().getSimpleName(), "update from http done");
	        if (programXmlHttp != null && programXmlHttp != "")
	        	programXml = programXmlHttp;
        }

        FileUtils fileUtils = new FileUtils(activity.getApplicationContext());
        fileUtils.writeFileToInternalStorage("program.xml", programXml);
        fileUtils.writeFileToInternalStorage("people.xml", peopleXml);

		XmlParser xmlParser = new XmlParser();
		Program program = xmlParser.parseProgramResponse(programXml);
		ArrayList<Person> people = xmlParser.parsePeopleResponse(peopleXml);
		// Attach presenters to program items
		if (program.programItems != null) {
			for (int i=0; i<program.programItems.size(); i++) {
				ProgramItem programItem = program.programItems.get(i);
				if (programItem.presenter != null) {
					for (int p=0; p<people.size(); p++) {
						if (people.get(p).uid.equals(programItem.presenter.uid)) {
							programItem.presenter = people.get(p);
						}
					}
				}
			}
		}

		return program;
	}

	@Override
	protected void onPostExecute(final Program program) {
		activity.runOnUiThread(new Runnable() {
	    	@Override
	    	public void run() {
				ProgramFragment programFragment = (ProgramFragment)activity.getFragmentManager().findFragmentById(R.id.fragPrg);
				if (programFragment != null && program != null)
					programFragment.updateProgram(activity.getApplicationContext(), program);
				AgendaFragment agendaFragment = (AgendaFragment)activity.getFragmentManager().findFragmentById(R.id.fragAgenda);
				if (agendaFragment != null && program != null)
					agendaFragment.updateProgram(program);
		    	((CompletionListener)activity).onTaskCompleted();
	    	}
		});
	}

}
