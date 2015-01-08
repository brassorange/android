package com.brassorange.eventapp;

import java.util.Timer;
import java.util.TimerTask;

import com.brassorange.eventapp.services.CompletionListener;
import com.brassorange.eventapp.services.Updater;
import com.brassorange.eventapp.services.FileUtils;
import com.brassorange.eventapp.util.PrefTools;

import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, CompletionListener {

	private boolean isHttpSynched;

    private String notificationService;
    private NotificationManager notificationManager;
    private Intent notificationIntent;
    private PendingIntent contentIntent;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    public AgendaFragment agendaFragment;
    public PresentersFragment presentersFragment;

    // The serialization (saved instance state) Bundle key representing the current dropdown position.
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        agendaFragment = new AgendaFragment();
        presentersFragment = new PresentersFragment();
        updateAgenda(); // First-time update.
        //updateAgendaFromCloud(); // Update from cloud, if possible.

        setupActionBar();
        if (savedInstanceState == null)
            selectItem(0);

		/*
		// If not already registered on this device, auto-generate a uid based on the device id
		String uid = ((EventApp)this.getApplication()).getUid();
		if (uid != null || uid == "") {
			uid = Secure.getString(this.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
			((EventApp)this.getApplication()).setUid(uid);
		}
		*/

        ((EventApp)getApplication()).fileUtils = new FileUtils(getApplicationContext());
        ((EventApp)getApplication()).prefTools = new PrefTools(getApplicationContext());


        scheduleUpdate(0); // Update every n sec. If set to 0, then it never updates.
        scheduleNotifications();

        //AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        //Calendar calendar = Calendar.getInstance();
	}

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            System.out.println("*** savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM) = " + savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
//            getSupportActionBar().setSelectedNavigationItem(
//                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
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

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(this.getClass().getSimpleName(), "onOptionsItemSelected: " + item.getItemId());

        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

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
            case R.id.home:
                System.out.println("home");
                return true;
            case R.id.homeAsUp:
                System.out.println("homeAsUp");
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    private void setupActionBar() {
        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();

//        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE
                                    | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setIcon(R.drawable.ic_launcher);

        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.drawer_item_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void selectItem(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        if (position == 0)
            fragmentManager.beginTransaction().replace(R.id.content_frame, agendaFragment).commit();
        else
            fragmentManager.beginTransaction().replace(R.id.content_frame, presentersFragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
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

    private void scheduleNotifications() {
        notificationService = Context.NOTIFICATION_SERVICE;
        notificationManager = (NotificationManager) getSystemService(notificationService);
        notificationIntent = new Intent(getApplicationContext(), AgendaFragment.class);
        contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        // TODO: Schedule event notifications
        /*
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 2014);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 14);
        (new Timer(true)).schedule(new TimerNotification("Title 1", "Text 1"), cal.getTime());

        cal.set(Calendar.MINUTE, 15);
        (new Timer(true)).schedule(new TimerNotification("Title 2", "Text 2"), cal.getTime());

        cal.set(Calendar.MINUTE, 16);
        (new Timer(true)).schedule(new TimerNotification("Title 3", "Text 3"), cal.getTime());
        */
    }

    private void scheduleUpdate(int periodInSec) {
        // Schedule a regular update
        TimerTask timerTask = new TimerUpdateChecker();
        Timer timer = new Timer(true);
        if (periodInSec > 0)
            timer.scheduleAtFixedRate(timerTask, 0, 1000*periodInSec);
    }

    protected void updateAgenda() {
        Updater updater = new Updater(this);
        updater.execute();
    }

    protected void updateAgendaFromCloud() {
        Updater updater = new Updater(this);
        updater.execute("http");
    }

    public class TimerNotification extends TimerTask {
        String title = "";
        String text = "";
        public TimerNotification(String title, String text) {
            this.title = title;
            this.text = text;
        }
        @Override
        public void run() {
            Notification notification = new Notification(R.drawable.ic_launcher, "Hello Notification!", System.currentTimeMillis());
            notification.setLatestEventInfo(getApplicationContext(), title, text, contentIntent);
            notificationManager.notify(1, notification);
        }
    }

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
