package com.brassorange.eventapp.services;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpPoster {

	private DefaultHttpClient client = new DefaultHttpClient();		
	public String post(String url, List<NameValuePair> params) {

		Log.d(getClass().getSimpleName(), "retrieve " + url);
        HttpPost postRequest = new HttpPost(url);

		try {
	        postRequest.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse getResponse = client.execute(postRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();
			Log.d(getClass().getSimpleName(), "statusCode=" + statusCode);

			if (statusCode != HttpStatus.SC_OK) { 
	            Log.w(getClass().getSimpleName(), "Error " + statusCode + " for URL " + url); 
	            return null;
	        }

			HttpEntity getResponseEntity = getResponse.getEntity();
			if (getResponseEntity != null) {
				String response = EntityUtils.toString(getResponseEntity);
				response = response.substring(0, response.indexOf("<!-- Hosting24 Analytics Code -->"));
				Log.d(getClass().getSimpleName(), "response: " + String.valueOf(response).length() + " bytes.");
				return response;
			}
		} 
		catch (IOException e) {
			postRequest.abort();
	        Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
		}

		return null;
	}

}
