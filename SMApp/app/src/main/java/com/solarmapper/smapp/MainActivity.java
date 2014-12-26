package com.solarmapper.smapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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

public class MainActivity extends Activity {
    private Menu mOptionsMenu;
    private static final int LOGIN_REQUEST = 1;
    private PrefTools prefTools = new PrefTools(this);
    private String apiKey;

    private Calendar calDate;
    private int periodMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calDate = Calendar.getInstance();

        Spinner spinner = (Spinner) findViewById(R.id.spinnerPeriod);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.periods_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        apiKey = prefTools.retrievePreference("apiKey");
        if (apiKey == null || apiKey == "") {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        } else {
            getGraphData();
        }

        ImageButton btnBack = (ImageButton)findViewById(R.id.imageButtonBack);
        ImageButton btnFwd = (ImageButton)findViewById(R.id.imageButtonFwd);
        Spinner spinnerPeriod = (Spinner)findViewById(R.id.spinnerPeriod);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (periodMode == 0)
                    calDate.add(Calendar.DATE, -1);
                else if (periodMode == 1)
                    calDate.add(Calendar.DATE, -7);
                else if (periodMode == 2)
                    calDate.add(Calendar.MONTH, -1);
                else if (periodMode == 3)
                    calDate.add(Calendar.YEAR, -1);
                getGraphData();
            }
        });
        btnFwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (periodMode == 0)
                    calDate.add(Calendar.DATE, 1);
                else if (periodMode == 1)
                    calDate.add(Calendar.DATE, 7);
                else if (periodMode == 2)
                    calDate.add(Calendar.MONTH, 1);
                else if (periodMode == 3)
                    calDate.add(Calendar.YEAR, 1);
                getGraphData();
            }
        });
        spinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                periodMode = i;
                getGraphData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                periodMode = 0;
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
        updater.execute(apiKey, strDate, String.valueOf(periodMode));
    }

    public void buildGraph(float[] values) {
        GraphView.GraphViewData[] gvd = new GraphView.GraphViewData[values.length];
        int maxValue = 0;
        float totalKwh = 0;
        for (int i=0; i<values.length; i++) {
            gvd[i] = new GraphView.GraphViewData(i, values[i]);
            if (maxValue < values[i])
                maxValue = 1+(int)values[i];
            totalKwh += values[i];
        }
        maxValue = (int)(1.2*(float)maxValue);

        GraphViewSeries dataSeries = new GraphViewSeries(gvd);
        GraphView graphView = new BarGraphView(this, "");
        graphView.addSeries(dataSeries);

        // Vertical labels
        graphView.setManualYMinBound(0);
        graphView.setManualYMaxBound(maxValue);
        graphView.setVerticalLabels(new String[]{String.valueOf(maxValue), String.valueOf(maxValue/2), "0"});
        // Horizontal labels & Title
        String[] labels = new String[values.length];
        String strTitle = "";
        if (periodMode == 0) {
            for (int i=0; i<values.length; i++) {
                if (new Integer(i/2).intValue() == ((float)i)/2)
                    labels[i] = String.valueOf(i);
                else
                    labels[i] = "";
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            strTitle = "Daily consumption " + df.format(calDate.getTime());
        } else if (periodMode == 1) {
            labels = new String[]{"S", "M", "T", "W", "T", "F", "S"};
            DateFormat df = new SimpleDateFormat("yyyy-MM");
            strTitle = "Weekly consumption " + df.format(calDate.getTime());
        } else if (periodMode == 2) {
            for (int i=0; i<values.length; i++) {
                if (new Integer(i/5).intValue() == ((float)i)/5)
                    labels[i] = String.valueOf(i);
                else
                    labels[i] = "";
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM");
            strTitle = "Monthly consumption " + df.format(calDate.getTime());
        } else if (periodMode == 3) {
            labels = new String[]{"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"};
            DateFormat df = new SimpleDateFormat("yyyy");
            strTitle = "Yearly consumption " + df.format(calDate.getTime());
        }
        graphView.setHorizontalLabels(labels);
        graphView.setTitle(strTitle);

        TextView textTotalKwh = (TextView)findViewById(R.id.textTotalKwh);
        textTotalKwh.setText("Total: " + String.valueOf((int)totalKwh) + " KWh");

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
