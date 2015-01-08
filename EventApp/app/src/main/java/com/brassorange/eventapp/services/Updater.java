package com.brassorange.eventapp.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.brassorange.eventapp.AgendaFragment;
import com.brassorange.eventapp.EventApp;
import com.brassorange.eventapp.MainActivity;
import com.brassorange.eventapp.PresentersFragment;
import com.brassorange.eventapp.R;
import com.brassorange.eventapp.model.Person;
import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.model.ProgramItem;

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

        Log.d(this.getClass().getSimpleName(), "start reading from file");

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

        Log.d(this.getClass().getSimpleName(), "write to internal storage");

        FileUtils fileUtils = new FileUtils(activity.getApplicationContext());
        fileUtils.writeFileToInternalStorage("program.xml", programXml);
        fileUtils.writeFileToInternalStorage("people.xml", peopleXml);

		XmlParser xmlParser = new XmlParser();
		Program program = xmlParser.parseProgramResponse(programXml);
		ArrayList<Person> people = xmlParser.parsePeopleResponse(peopleXml);

        // -----------------------------------------------------------------------------------------
        Log.d(this.getClass().getSimpleName(), "randomize");
        RandomStuff rs = new RandomStuff();
        for (int i=0; i<50; i++) {
            ProgramItem programItem = new ProgramItem();
            programItem.id = String.valueOf(1000 + i);
            programItem.title = rs.makeText(30);
            programItem.summary = rs.makeText(200);
            programItem.content = rs.makeText(1000);
            Person person = new Person();
            person.uid = rs.makeText(25);
            person.firstName = rs.makeName();
            person.lastName = rs.makeName();
            person.title = rs.makeText(60);
            person.email = rs.makeName() + "@" + rs.makeName() + "." + rs.makeText(2);
            person.biography = rs.makeText(2000);
            person.imageName = "allen_john";
            programItem.presenter = person;
            program.programItems.add(programItem);
        }
        // -----------------------------------------------------------------------------------------

		// Attach presenters to program items
        Log.d(this.getClass().getSimpleName(), "attach presenters to program items");
        HashMap<String, ArrayList<ProgramItem>> presenterItemsHashMap = new HashMap();

        if (program != null && program.programItems != null) {
			for (int i=0; i<program.programItems.size(); i++) {
				ProgramItem programItem = program.programItems.get(i);
                Person presenter = programItem.presenter;
				if (presenter != null) {
					for (int p=0; p<people.size(); p++) {
						if (people.get(p).uid.equals(presenter.uid)) {
							presenter = people.get(p);
						}
					}
                    ArrayList<ProgramItem> presenterItems = presenterItemsHashMap.get(presenter.uid);
                    if (presenterItems == null)
                        presenterItems = new ArrayList();
                    presenterItems.add(programItem);
                    presenterItemsHashMap.put(presenter.uid, presenterItems);
                    presenter.presenterItems = presenterItemsHashMap.get(programItem.presenter.uid);
                }
                program.programItems.get(i).presenter = presenter;
			}
		}

		return program;
	}

	@Override
	protected void onPostExecute(final Program program) {
		activity.runOnUiThread(new Runnable() {
	    	@Override
	    	public void run() {
                System.out.println("Updater.onPostExecute " + program);
                if (program != null) {
//                    ProgramFragment programFragment = (ProgramFragment)activity.getFragmentManager().findFragmentById(R.id.fragPrg);
//                    if (programFragment != null)
//                        programFragment.updateProgram(activity.getApplicationContext(), program);

                    AgendaFragment agendaFragment = ((MainActivity)activity).agendaFragment;
                    if (agendaFragment != null)
                        agendaFragment.updateProgram(program);

                    PresentersFragment presentersFragment = ((MainActivity)activity).presentersFragment;
                    if (presentersFragment != null)
                        presentersFragment.updatePresenters(program);

                    ((CompletionListener)activity).onTaskCompleted();
                }
	    	}
		});
	}

    public class RandomStuff {

        public String makeWord(int length) {
            char[] symbols;
            Random random = new Random();
            StringBuilder tmp = new StringBuilder();
            //for (char ch = '0'; ch <= '9'; ++ch)
            //    tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
            symbols = tmp.toString().toCharArray();

            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            char[] buf = new char[length];
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }

        public String makeText(int length) {
            Random random = new Random();
            int minLength = 1;
            int maxLength = 10;
            StringBuilder tmp = new StringBuilder();
            while (tmp.length() < length) {
                tmp.append(makeWord(random.nextInt(maxLength) + minLength) + " ");
            }
            return new String(tmp);
        }

        public String makeName() {
            Random random = new Random();
            int minLength = 4;
            int maxLength = 10;
            StringBuilder tmp = new StringBuilder();
            tmp.append(makeWord(random.nextInt(maxLength) + minLength));
            return new String(tmp);
        }
    }
}
