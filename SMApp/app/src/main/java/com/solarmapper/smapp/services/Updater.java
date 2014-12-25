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
        String result = httpRetriever.retrieve("http://solarmapper.com/api/v1/cons?apiKey=" + params[0] + "&id=1&date=" + params[1]);
        float[] values = new float[24];
        try {
            JSONObject jObject = new JSONObject(result);
            for (int h=0; h<24; h++)
                values[h] = new Float(jObject.getString("H" + String.valueOf(h))).floatValue();
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
                ((MainActivity) activity).buildGraph(values);
            }
        });
    }
}
