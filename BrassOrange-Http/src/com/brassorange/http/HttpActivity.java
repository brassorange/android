package com.brassorange.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class HttpActivity extends Activity {
	//private static final String DEBUG_TAG = "HttpExample";
	private TextView textHtml;
	
	public HttpActivity(TextView textHtml) {
		this.textHtml = textHtml;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);   
	}

	public void getHttpResponse(String url) {
		new DownloadWebpageTask().execute(url);
	}

	 private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

		 @Override
		protected String doInBackground(String... urls) {			  
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				return "Unable to retrieve web page: " + e.getMessage();
			}
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			textHtml.setText(result);
		}

		private String downloadUrl(String url) throws MalformedURLException, IOException {
			StringBuffer sb = new StringBuffer();
			HttpURLConnection urlConnection = (HttpURLConnection)new URL(url).openConnection();
			InputStream in = urlConnection.getInputStream();
			InputStreamReader inputStream = new InputStreamReader(in);
			char[] buffer = new char[1024];
			int bufferLength = 0;
			while ((bufferLength = inputStream.read(buffer)) > 0 ) {
				sb.append(String.copyValueOf(buffer, 0, bufferLength));
			}
			return sb.toString();
		}
	}
}
