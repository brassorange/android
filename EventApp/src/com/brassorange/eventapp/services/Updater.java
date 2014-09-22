package com.brassorange.eventapp.services;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;

import com.brassorange.eventapp.AgendaFragment;
import com.brassorange.eventapp.ProgramFragment;
import com.brassorange.eventapp.R;
import com.brassorange.eventapp.model.Person;
import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.model.ProgramItem;
import com.brassorange.eventapp.services.FileUtils;

public class Updater extends AsyncTask<String, Void, Program> {

	private Activity activity;
	private String URL_AGENDA = "http://brassorange.com/samplepages/agenda.xml?";

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
        if (programXml == "") {
	        HttpRetriever httpRetriever = new HttpRetriever();
	        programXml = httpRetriever.retrieve(URL_AGENDA);
        }

        FileUtils fileUtils = new FileUtils(activity.getApplicationContext());
        fileUtils.writeFileToInternalStorage("program.xml", programXml);
        fileUtils.writeFileToInternalStorage("people.xml", peopleXml);

		XmlParser xmlParser = new XmlParser();
		Program program = xmlParser.parseProgramResponse(programXml);
		ArrayList<Person> people = xmlParser.parsePeopleResponse(peopleXml);
		// Attach presenters to program items 
		for (int i=0; i<program.programItems.size(); i++) {
			ProgramItem programItem = program.programItems.get(i);
			for (int p=0; p<people.size(); p++) {
				if (people.get(p).id.equals(programItem.presenter.id)) {
					programItem.presenter = people.get(p);
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
	    	}
		});
	}

}
