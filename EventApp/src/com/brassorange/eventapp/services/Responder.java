package com.brassorange.eventapp.services;

/*
import android.app.Activity;
import android.os.AsyncTask;

import com.brassorange.eventapp.model.Program;
public class Responder extends AsyncTask<String, Void, Program> {
	private String URL_RESPOND = "http://brassorange.com/samplepages/respond.php?";
	private Activity activity;

	public Responder(Activity activity) {
		this.activity = activity;
	}

	@Override
	protected Program doInBackground(String... arg0) {
        HttpRetriever httpRetriever = new HttpRetriever();
        String response = httpRetriever.retrieve(URL_RESPOND);
        System.out.println("************ " + response);
		return null;
	}
	@Override
	protected void onPostExecute(final Program program) {
		activity.runOnUiThread(new Runnable() {
	    	@Override
	    	public void run() {
	    	}
		});
	}
}
*/