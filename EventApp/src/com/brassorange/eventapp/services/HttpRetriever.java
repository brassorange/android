package com.brassorange.eventapp.services;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpRetriever {
	private static final String strDeleteSuffix = "<!-- Hosting24 Analytics Code -->";

	private DefaultHttpClient client = new DefaultHttpClient();		
	public String retrieve(String url) {

		Log.d(getClass().getSimpleName(), "retrieve " + url);
        HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();
			Log.d(getClass().getSimpleName(), "statusCode=" + statusCode);

			if (statusCode != HttpStatus.SC_OK) { 
	            Log.w(getClass().getSimpleName(), "Error " + statusCode + " for URL " + url); 
	            return null;
	        }

			HttpEntity getResponseEntity = getResponse.getEntity();
			if (getResponseEntity != null) {
				String response = EntityUtils.toString(getResponseEntity);
				if (response.contains(strDeleteSuffix))
					response = response.substring(0, response.indexOf(strDeleteSuffix));
				Log.d(getClass().getSimpleName(), "response: " + String.valueOf(response).length() + " bytes.");
				return response;
			}
		} 
		catch (IOException e) {
			getRequest.abort();
	        Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
		}

		return null;
	}

}
