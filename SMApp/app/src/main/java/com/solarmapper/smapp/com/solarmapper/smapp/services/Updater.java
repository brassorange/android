package com.solarmapper.smapp.com.solarmapper.smapp.services;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.solarmapper.smapp.MainActivity;
import com.solarmapper.smapp.com.solarmapper.smapp.controllers.HttpRetriever;

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
        String result = httpRetriever.retrieve("http://solarmapper.com/api/v1/cons?apiKey=abcd&id=1&date=20141222");
        result = "{\"H0\":485,\"H1\":525,\"H2\":360,\"H3\":281,\"H4\":223,\"H5\":220,\"H6\":209,\"H7\":187,\"H8\":551,\"H9\":902,\"H10\":754,\"H11\":457,\"H12\":579,\"H13\":396,\"H14\":584,\"H15\":574,\"H16\":526,\"H17\":357,\"H18\":242,\"H19\":408,\"H20\":517,\"H21\":521,\"H22\":453,\"H23\":337}";
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
