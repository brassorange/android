package com.solarmapper.smapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.solarmapper.smapp.services.Updater;
import com.solarmapper.smapp.utils.PrefTools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
    Check the PreferenceManager for apiKey.
        If apiKey is set, go to the graph.
        Else go to login activity.

    Update the consumption graph:
        MainActivity.onCreate -> Updater.doInBackground
        -> HttpRetriever.retrieve
        -> Updater.onPostExecute -> MainActivity.buildGraph

*/

public class MainActivity extends ActionBarActivity {
    private Menu mOptionsMenu;
    private static final int LOGIN_REQUEST = 1;
    private PrefTools prefTools = new PrefTools(this);
    private String apiKey;

    private Calendar calDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calDate = Calendar.getInstance();

        apiKey = prefTools.retrievePreference("apiKey");
        if (apiKey == null || apiKey == "") {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        } else {
            getGraphData();
        }

        ImageButton btnBack = (ImageButton)findViewById(R.id.imageButtonBack);
        ImageButton btnFwd = (ImageButton)findViewById(R.id.imageButtonFwd);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calDate.add(Calendar.DATE, -1);
                getGraphData();
            }
        });
        btnFwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calDate.add(Calendar.DATE, 1);
                getGraphData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // Return from Login activity
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
                apiKey = data.getStringExtra("apiKey");
                prefTools.storePreference("apiKey", apiKey);
                adjustMenu();
                getGraphData();
            }
        }
    }

    public void getGraphData() {
        Updater updater = new Updater(this);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String strDate = df.format(calDate.getTime());
        updater.execute(apiKey, strDate);
    }

    public void buildGraph(float[] values) {
        GraphView.GraphViewData[] gvd = new GraphView.GraphViewData[24];
        for (int i=0; i<24; i++)
            gvd[i] = new GraphView.GraphViewData(i, values[i]);
        GraphViewSeries dataSeries = new GraphViewSeries(gvd);
        GraphView graphView = new BarGraphView(this, "");
        graphView.addSeries(dataSeries); // data

        // Horizontal labels
        String[] labels = new String[24];
        for (int i=0; i<24; i++) {
            if (new Integer(i/4).intValue() == ((float)i)/4)
                labels[i] = String.valueOf(i);
            else
                labels[i] = "";
        }
        graphView.setHorizontalLabels(labels);
        // Vertical labels
        graphView.setManualYMinBound(0);
        graphView.setManualYMaxBound(1000);
        graphView.setVerticalLabels(new String[]{"1000", "500", "0"});
        // Title
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        graphView.setTitle("Daily consumption " + df.format(calDate.getTime()));

        LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
        layout.removeAllViews();
        layout.addView(graphView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mOptionsMenu = menu;
        adjustMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        } else if (id == R.id.action_logout) {
            prefTools.storePreference("apiKey", "");
            LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
            layout.removeAllViews();
            adjustMenu();
        } else if (id == R.id.action_settings) {
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void adjustMenu() {
        String apiKey = prefTools.retrievePreference("apiKey");
        if (apiKey == null)
            apiKey = "";
        mOptionsMenu.getItem(0).setVisible(apiKey == "");
        mOptionsMenu.getItem(1).setVisible(apiKey != "");
    }
}
