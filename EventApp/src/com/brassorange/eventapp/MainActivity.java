package com.brassorange.eventapp;

import android.provider.Settings.Secure;
import java.util.TimerTask;

//import com.brassorange.eventapp.services.Responder;
import com.brassorange.eventapp.services.Updater;
import com.brassorange.eventapp.services.FileUtils;
import com.brassorange.eventapp.util.PrefTools;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends Activity {

	public FileUtils fileUtils;
	public PrefTools prefTools;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		EventApp.uid = Secure.getString(this.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

		fileUtils = new FileUtils(getApplicationContext());
		prefTools = new PrefTools(getApplicationContext());
		//loadTabs();
		load();

		//TimerTask timerTask = new TimerUpdateChecker();
		//Timer timer = new Timer(true);
		//timer.scheduleAtFixedRate(timerTask, 0, 3 * 1000);
Updater updater = new Updater(this);
updater.execute();

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
				// refresh
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

	protected void load() {
		setContentView(R.layout.activity_main);
	}

	protected void loadTabs() {
		setContentView(R.layout.activity_main_tabs);

		// Arrange tab menu
		final TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
	    tabHost.setup();
	  
	    TabSpec spec1 = tabHost.newTabSpec("Intro");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Intro");
		tabHost.addTab(spec1);
		
		TabSpec spec2 = tabHost.newTabSpec("Program");
		spec2.setIndicator("Program");
		spec2.setContent(R.id.tab2);
		tabHost.addTab(spec2);
		
		TabSpec spec3 = tabHost.newTabSpec("Personal");
		spec3.setContent(R.id.tab3);
		spec3.setIndicator("Personal");
		tabHost.addTab(spec3);

		// Set the tab menu look and feel
		for (int i=0; i<tabHost.getTabWidget().getChildCount(); i++) {
			View tab = tabHost.getTabWidget().getChildAt(i);
			TextView tabTv = (TextView)tab.findViewById(android.R.id.title);
			tabTv.setTextColor(Color.rgb(128, 128, 0));
			tabTv.setTextSize(20);
		}

        // Listen for tab item clicks
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String arg0) {
				// Set the layout for unselected tabs
		        for (int i=0; i<tabHost.getTabWidget().getChildCount(); i++) {
			        View tab = tabHost.getTabWidget().getChildAt(i);
			        tab.setBackgroundResource(R.drawable.tabwidget_unselected);
		        	TextView tabTv = (TextView)tab.findViewById(android.R.id.title);
		        	tabTv.setTextSize(20);
		        }
				// Set the layout for the selected tab
		        View tab = tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab());
		        tab.setBackgroundResource(R.drawable.tabwidget_selected);
		        TextView tabTv = (TextView)tab.findViewById(android.R.id.title);
		        tabTv.setTextSize(24);
			}
        });

        // Go to tab at position 1
    	View tab = tabHost.getTabWidget().getChildAt(1);
    	tab.performClick();
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
}
