package com.solarmapper.smapp.services;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.solarmapper.smapp.MainActivity;
import com.solarmapper.smapp.controllers.HttpRetriever;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bge on 25/12/2014.
 */
public class Updater extends AsyncTask<String, Void, float[]> {
    private Activity activity;

    public Updater(Activity activity) {
        this.activity = activity;
    }
    @Override
    protected float[] doInBackground(String... params) {
        HttpRetriever httpRetriever = new HttpRetriever();
        Log.d(this.getClass().getSimpleName(), "update from http ...");
        String result = httpRetriever.retrieve("http://solarmapper.com/api/v1/cons?apiKey=" + params[0] + "&id=1&date=" + params[1] + "&mode=" + params[2]);
        float[] values = new float[0];
        try {
            JSONObject jObject = new JSONObject(result);
            int mode = new Integer(params[2]).intValue();
            int[] arrSizes = new int[]{24, 7, 31, 12};
            String[] prefixes = new String[]{"H", "DW", "D", "M"};
            values = new float[arrSizes[mode]];
            for (int i = 0; i < values.length; i++) {
                values[i] = 0;
                String idx = prefixes[mode] + String.valueOf(i);
                Log.d("", "idx=" + idx + ", " + jObject.has(idx));
                if (jObject.has(idx))
                    values[i] = new Float(jObject.getString(idx)).floatValue();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(this.getClass().getSimpleName(), "update from http done: " + result);
        return values;
    }

    @Override
    protected void onPostExecute(final float[] values) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(this.getClass().getSimpleName(), "build the graph");
                ((MainActivity) activity).buildGraph(values);
            }
        });
    }
}
