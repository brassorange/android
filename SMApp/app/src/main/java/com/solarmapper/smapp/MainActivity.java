package com.solarmapper.smapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.solarmapper.smapp.com.solarmapper.smapp.services.Updater;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MainActivity.onCreate -> Updater.doInBackground
        //  -> HttpRetriever.retrieve
        //  -> Updater.onPostExecute -> MainActivity.buildGraph

        Updater updater = new Updater(this);
        updater.execute();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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
        graphView.setManualYMaxBound(1000);
        graphView.setVerticalLabels(new String[]{"1000", "500", "0"});
        // Title
        graphView.setTitle("Daily consumption");

        LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
        layout.addView(graphView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
