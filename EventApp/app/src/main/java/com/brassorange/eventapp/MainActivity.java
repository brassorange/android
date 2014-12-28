package com.brassorange.eventapp;

import java.util.Timer;
import java.util.TimerTask;

import com.brassorange.eventapp.services.CompletionListener;
import com.brassorange.eventapp.services.Updater;
import com.brassorange.eventapp.services.FileUtils;
import com.brassorange.eventapp.util.PrefTools;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, CompletionListener {

	public FileUtils fileUtils;
	public PrefTools prefTools;
	private boolean isHttpSynched;
    private Activity mainActivity;

    // The serialization (saved instance state) Bundle key representing the current dropdown position.
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mainActivity = this;

        Log.d(this.getClass().getSimpleName(), "onCreate");

        setupActionBar();

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

        updateAgenda(); // One-time update
        scheduleUpdate(30); // Update every 30 sec
        setupNotificationService();
	}

    private void scheduleUpdate(int periodInSec) {
        // Schedule a regular update
        TimerTask timerTask = new TimerUpdateChecker();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 1000*periodInSec);
    }

    private void setupNotificationService() {
        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(notificationService);
        Notification notification = new Notification(R.drawable.ic_launcher, "Hello Notification!", System.currentTimeMillis());
        Intent notificationIntent = new Intent(getApplicationContext(), PersonFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        notification.setLatestEventInfo(getApplicationContext(), "Notification Title", "Notification Text", contentIntent);
        //notificationManager.notify(1, notification);
    }
    private void setupActionBar() {
        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                getString(R.string.action_check_updates),
                                getString(R.string.action_help),
                                getString(R.string.action_profile),
                        }),
                this);
    }

    protected void updateAgenda() {
        Updater updater = new Updater(this);
        updater.execute();
    }

    protected void updateAgendaFromCloud() {
        Updater updater = new Updater(this);
        updater.execute("http");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(this.getClass().getSimpleName(), "onOptionsItemSelected: " + item.getItemId());
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
			case R.id.action_search:
				// search action
                Log.d(this.getClass().getSimpleName(), "onOptionsItemSelected -> R.id.action_search");
				return true;
			case R.id.action_refresh:
				updateAgendaFromCloud();
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

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        Log.i(this.getClass().getSimpleName(), "onNavigationItemSelected: " + i + ", " + l);
        return false;
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
			        updateAgendaFromCloud();
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
			//updateAgendaFromCloud();
		}
	}
}
