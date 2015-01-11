package com.solarmapper.smapp.services;

import android.appwidget.AppWidgetProvider;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.solarmapper.smapp.SMAppWidgetProvider;
import com.solarmapper.smapp.controllers.HttpRetriever;

public class UpdaterImage extends AsyncTask<String, Void, Bitmap> {
    private AppWidgetProvider appWidgetProvider;

    public UpdaterImage(AppWidgetProvider appWidgetProvider) {
        this.appWidgetProvider = appWidgetProvider;
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        HttpRetriever httpRetriever = new HttpRetriever();
        Log.d(this.getClass().getSimpleName(), "update from http ...");
        Bitmap result = (Bitmap)httpRetriever.retrieve("http://solarmapper.com" + params[0], Bitmap.class);
        Log.d(this.getClass().getSimpleName(), "update from http done: " + result);
        return result;
    }

    @Override
    protected void onPostExecute(final Bitmap bitmap) {
        Log.d(this.getClass().getSimpleName(), "onPostExecute: " + bitmap);
        ((SMAppWidgetProvider)appWidgetProvider).setBitmap(bitmap);
    }
}
