package com.brassorange.eventapp;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import com.brassorange.eventapp.dummy.DummyContent;
import com.brassorange.eventapp.dummy.DummyContent.DummyItem;
import com.brassorange.eventapp.model.Agenda;
import com.brassorange.eventapp.model.Event;
import com.brassorange.eventapp.services.HttpRetriever;
import com.brassorange.eventapp.services.XmlParser;

import android.app.Application;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details
 * (if present) is a {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ItemListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ItemListActivity extends FragmentActivity
        implements ItemListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private String URL_AGENDA = "http://brassorange.com/samplepages/agenda.xml?";
    private boolean isUpdated = false;
    private Drawable drawableSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ItemListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.

        // -----------------------------------------------------------------------
        Log.d(getClass().getSimpleName(), "onCreate");
        if (!isUpdated) {
	        Updater updater = new Updater();
	        updater.execute();
        }
        findViewById(R.id.item_list).setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        // -----------------------------------------------------------------------
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
            detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
	public class Updater extends AsyncTask<String, Void, Agenda> {
		String responseXml = "<agenda><splashUrl>splash.png</splashUrl><event><id>1</id><title>Brad Pitt 1</title><summary>Brad Pitt 1 - Summary</summary><content>One - William Bradley 'Brad' Pitt (born December 18, 1963) is an American actor and film producer. Pitt has received two Academy Award nominations and four Golden Globe Award nominations, winning one. He has been described as one of the world's most attractive men, a label for which he has received substantial media attention.</content><date>22-APR-2014</date><hour>10:00</hour><duration>1:40</duration><popularity>3</popularity><url>http://www.themoviedb.org/person/287</url><images></images><location>Hall 92</location><last_modified_at>2010-10-24 10:22:10</last_modified_at></event><event><id>2</id><title>Brad Pitt 2</title><summary>Brad Pitt 2 - Summary</summary><content>Two - William Bradley 'Brad' Pitt (born December 18, 1963) is an American actor and film producer. Pitt has received two Academy Award nominations and four Golden Globe Award nominations, winning one. He has been described as one of the world's most attractive men, a label for which he has received substantial media attention.</content><date>22-APR-2014</date><hour>10:30</hour><duration>1:50</duration><popularity>3</popularity><url>http://www.themoviedb.org/person/287</url><images></images><location>Hall 92</location><last_modified_at>2010-10-24 10:22:10</last_modified_at></event></agenda>";
		boolean isHttpUpdate = false;

		@Override
		protected Agenda doInBackground(String... params) {
			Log.d(getClass().getSimpleName(), "doInBackground");

			if (isHttpUpdate) {
		        HttpRetriever httpRetriever = new HttpRetriever();
		        responseXml = httpRetriever.retrieve(URL_AGENDA);
			}

			XmlParser xmlParser = new XmlParser();
			return xmlParser.parseAgendaResponse(responseXml);
		}

		@Override
		protected void onPostExecute(final Agenda agenda) {			
			runOnUiThread(new Runnable() {
		    	@Override
		    	public void run() {
					Log.d(getClass().getSimpleName(), "onPostExecute runOnUiThread run()");

		    		// Load the objects from the updater
					DummyContent dc = new DummyContent();
					dc.SPLASH_URL = agenda.splashUrl;
					dc.ITEMS.clear();
					int i = 1;
					ArrayList<Event> eventList = agenda.eventList;
		    		for (Event event: eventList) {
		    			// Set the object fields
		    			DummyItem di = new DummyItem(String.valueOf(i), event.title);
		    			di.summary = event.summary;
		    			di.content = event.content;
		    			dc.addItem(di);
						i++;
		    		}

		    		// Reflect the newly loaded structure in ItemListFragment
		    		((ItemListFragment)getSupportFragmentManager().findFragmentById(R.id.item_list)).createListAdapter();

		    		if (mTwoPane) {
		            	// Show splash screen
		    			Log.d(getClass().getSimpleName(), "Show splash screen");
		            	Bundle arguments = new Bundle();
		                final ItemDetailFragment fragment = new ItemDetailFragment();
		                fragment.setArguments(arguments);
		                
		                getSupportFragmentManager().beginTransaction()
		                        .replace(R.id.item_detail_container, fragment)
		                        .commit();

		                //System.out.println(getFragmentManager().findFragmentById(R.id.fragment_item_detail));

		                //imageSplash = (ImageView)getSupportFragmentManager()
						//		.findFragmentById(R.id.item_detail_container).getView()
						//		.findViewById(R.id.imageSplash);
		                //UpdaterSplash updaterSplash = new UpdaterSplash("http://brassorange.com/samplepages/" + DummyContent.SPLASH_URL);
		    	        //updaterSplash.execute();
		            }

		    		isUpdated = true;
		    	}
			});
		};
	}
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
	// -----------------------------------------------------------------------
}
