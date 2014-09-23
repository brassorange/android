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

import android.os.AsyncTask;
import android.util.Log;

public class UserService extends AsyncTask<String, Void, String> {

	public UserService() {
	}

	@Override
	protected String doInBackground(String... arg0) {
		HttpPoster httpPoster = new HttpPoster();
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		String actionType = arg0[0];

		if (actionType.equals("profile")) {
			params.add(new BasicNameValuePair("id", arg0[1]));
			String output = httpPoster.post(EventApp.urlGetProfile, params);
			Log.d(this.getClass().getSimpleName(), output);
		} else {
			String programItemId = arg0[1];
			params.add(new BasicNameValuePair("uid", EventApp.uid));
			params.add(new BasicNameValuePair("pid", programItemId));
	
			if (actionType.equals("comment")) {
				params.add(new BasicNameValuePair("c", arg0[2]));
				httpPoster.post(EventApp.urlPutComment, params);
			}
			if (actionType.equals("rate")) {
				params.add(new BasicNameValuePair("r", arg0[2]));
		        httpPoster.post(EventApp.urlPutRating, params);
			}
		}
		return null;
	}

}
