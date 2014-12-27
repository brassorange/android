package com.brassorange.eventapp;

import java.util.TimerTask;

import com.brassorange.eventapp.services.CompletionListener;
//import com.brassorange.eventapp.services.Responder;
import com.brassorange.eventapp.services.Updater;
import com.brassorange.eventapp.services.FileUtils;
import com.brassorange.eventapp.util.PrefTools;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

public class MainActivity extends Activity implements CompletionListener {

	public FileUtils fileUtils;
	public PrefTools prefTools;
	private boolean isHttpSynched;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		// If not already registered on this device, auto-generate a uid based on the device id
		String uid = ((EventApp)this.getApplication()).getUid();
		if (uid != null || uid == "") {
			uid = Secure.getString(this.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
			((EventApp)this.getApplication()).setUid(uid);
		}
		*/

		fileUtils = new FileUtils(getApplicationContext());
		prefTools = new PrefTools(getApplicationContext());

		// Run a one-time update - at first from local file, then from http 
		Updater updater = new Updater(this);
		updater.execute();

		// Schedule a regular update
		//TimerTask timerTask = new TimerUpdateChecker();
		//Timer timer = new Timer(true);
		//timer.scheduleAtFixedRate(timerTask, 0, 3 * 1000);

/*
String notificationService = Context.NOTIFICATION_SERVICE;
NotificationManager notificationManager = (NotificationManager) getSystemService(notificationService);
Notification notification = new Notification(R.drawable.ic_launcher, "Hello Notification!", System.currentTimeMillis());
Intent notificationIntent = new Intent(getApplicationContext(), PersonFragment.class);
PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
notification.setLatestEventInfo(getApplicationContext(), "Notification Title", "Notification Text", contentIntent);
notificationManager.notify(1, notification);
*/

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
 
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
			case R.id.action_search:
				// search action
				return true;
			case R.id.action_refresh:
				Updater updater = new Updater(this);
				updater.execute("http");
				return true;
			case R.id.action_profile:
				Intent intent = new Intent(this, ProfileActivity.class);
				startActivity(intent); 
				return true;
			case R.id.action_help:
				// help action
				return true;
			case R.id.action_settings:
				// settings action
				return true;
			case R.id.action_check_updates:
				// check for updates action
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/*
	public void provideResponse() {
		Responder responder = new Responder(this); 
		responder.execute();
	}
	*/

	public class TimerUpdateChecker extends TimerTask {
		@Override
		public void run() {
			doUpdate();
		}
		private void doUpdate() {
			//http://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
			        //Updater updater = new Updater();
			        //updater.execute();
				}
			});
		}
	}

	@Override
	public void onTaskCompleted() {
		Log.d(this.getClass().getSimpleName(), "onTaskCompleted " + isHttpSynched);
		if (!isHttpSynched) {
			// It is now synched from file, now do a synch from http
			isHttpSynched = true;
			Updater updater = new Updater(this);
//			updater.execute("http");
		}
	}
}
