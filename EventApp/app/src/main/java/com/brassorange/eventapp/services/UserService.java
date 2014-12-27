package com.brassorange.eventapp.services;

/* UserService
 * 
 * Sends POST requests for updating comments and ratings about a Program Item.
 * Used as an AsyncTask. Called in ProgramFragment.java.
 * 
 */


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.brassorange.eventapp.EventApp;
import com.brassorange.eventapp.model.Profile;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

public class UserService extends AsyncTask<String, Void, String> {

	String actionType = "";
	private EventApp eventApp;
	private CompletionListener listener;

	public UserService(Application application, CompletionListener listener) {
		this.eventApp = (EventApp)application;
		this.listener = listener;
	}

	@Override
	protected String doInBackground(String... arg0) {
		HttpPoster httpPoster = new HttpPoster();
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		actionType = arg0[0];

		if (actionType.equals("profile")) {
			params.add(new BasicNameValuePair("uid", arg0[1]));
			String response = httpPoster.post(eventApp.getUrlGetProfile(), params);
			return response;
		} else {
			String programItemId = arg0[1];
			params.add(new BasicNameValuePair("uid", eventApp.getUid()));
			params.add(new BasicNameValuePair("pid", programItemId));
	
			if (actionType.equals("comment")) {
				params.add(new BasicNameValuePair("c", arg0[2]));
				httpPoster.post(eventApp.getUrlPutComment(), params);
			}
			if (actionType.equals("rate")) {
				params.add(new BasicNameValuePair("r", arg0[2]));
		        httpPoster.post(eventApp.getUrlPutRating(), params);
			}
		}
		return null;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (actionType.equals("profile")) {
			Log.d(this.getClass().getSimpleName(), result);

			XmlParser xmlParser = new XmlParser();
			Profile profile = xmlParser.parseProfileResponse(result);
			if (profile != null) {
				eventApp.setUid(profile.uid);
				eventApp.setFirstName(profile.firstName);
				eventApp.setLastName(profile.lastName);
				eventApp.setBiography(profile.biography);
				eventApp.setMailAccount(profile.email);
			}
			listener.onTaskCompleted();
		}
	}
}
