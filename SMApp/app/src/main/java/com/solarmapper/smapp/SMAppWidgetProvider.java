package com.solarmapper.smapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.widget.RemoteViews;

import com.solarmapper.smapp.services.Updater;
import com.solarmapper.smapp.services.UpdaterImage;
import com.solarmapper.smapp.utils.PrefTools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SMAppWidgetProvider extends AppWidgetProvider {
    private Context context;
    private AppWidgetManager appWidgetManager;
    private RemoteViews remoteViews;
    private String apiKey = "";
    private PrefTools prefTools;
    private int widgetWidth;
    private int widgetHeight;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.prefTools = new PrefTools(context);

        this.apiKey = prefTools.retrievePreference("apiKey");

        // Get all ids
        this.remoteViews = new RemoteViews(context.getPackageName(), R.layout.sm_appwidget_layout);

        // Set the text
        this.remoteViews.setTextViewText(R.id.update, "Retrieving...");

        // Register an onClickListener - button launches an update
        Intent intent = new Intent(context, SMAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.remoteViews.setOnClickPendingIntent(R.id.button, pendingIntent);

        // Clicking on the layout opens the app
        intent = new Intent(context, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        this.remoteViews.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);

        // Update widgets
        ComponentName thisWidget = new ComponentName(context, SMAppWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            appWidgetManager.updateAppWidget(widgetId, this.remoteViews);
            widgetWidth = appWidgetManager.getAppWidgetInfo(widgetId).minWidth;
            widgetHeight = appWidgetManager.getAppWidgetInfo(widgetId).minHeight;
        }
        if (widgetWidth <= 190)
            widgetWidth = 190;
        if (widgetHeight <= 140)
            widgetHeight = 140;

        // Finally, run the updater
        Updater updater = new Updater(this);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar calDate = Calendar.getInstance();
        String strDate = df.format(calDate.getTime());
        updater.execute(apiKey, strDate, "0");
    }

    public void setData(float[] data) {
        this.remoteViews = new RemoteViews(context.getPackageName(), R.layout.sm_appwidget_layout);
        if (data == null) {
            remoteViews.setTextViewText(R.id.update, "No connection");
        } else {
            float total = 0;
            String url = "/graphs/bars-consumption?imageW=" + widgetWidth + "&imageH=" + widgetHeight + "&data=";
            for (float hourly : data) {
                total += hourly;
                url += String.valueOf((double) Math.round(hourly * 100) / 100) + ",";
            }

            if (total > 0) {
                remoteViews.setTextViewText(R.id.update, "Total: " + (double) Math.round(total * 100) / 100 + " kWh");
                UpdaterImage updater = new UpdaterImage(this);
                updater.execute(url);
            } else {
                remoteViews.setTextViewText(R.id.update, "No data found");
            }
        }
        updateWidget();
    }

    public void setBitmap(Bitmap bitmap) {
        this.remoteViews = new RemoteViews(context.getPackageName(), R.layout.sm_appwidget_layout);
        remoteViews.setImageViewBitmap(R.id.graph_widget, bitmap);
        updateWidget();
    }

    private void updateWidget() {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, SMAppWidgetProvider.class);
        int[] allWidgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        for (int widgetId : allWidgetIds)
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
}