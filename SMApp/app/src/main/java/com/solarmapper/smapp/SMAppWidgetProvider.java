package com.solarmapper.smapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.solarmapper.smapp.services.Updater;
import com.solarmapper.smapp.services.UpdaterImage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SMAppWidgetProvider extends AppWidgetProvider {
    private Context context;
    private AppWidgetManager appWidgetManager;
    private RemoteViews remoteViews;
    private String apiKey = "YUFzZWJJa2RvNy9VUG02eHE2WCtQdz09";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.context = context;
        this.appWidgetManager = appWidgetManager;

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, SMAppWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            this.remoteViews = new RemoteViews(context.getPackageName(), R.layout.sm_appwidget_layout);

            // Set the text
            remoteViews.setTextViewText(R.id.update, "Retrieving...");
            Updater updater = new Updater(this);
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            Calendar calDate = Calendar.getInstance();
            String strDate = df.format(calDate.getTime());
            updater.execute(apiKey, strDate, "0");

            // Register an onClickListener
            Intent intent = new Intent(context, SMAppWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.button, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public void setData(float[] data) {
        this.remoteViews = new RemoteViews(context.getPackageName(), R.layout.sm_appwidget_layout);
        float total = 0;
        String url = "/graphs/bars-consumption?data=";
        for (float hourly : data) {
            total += hourly;
            url += String.valueOf((double)Math.round(hourly * 100) / 100) + ",";
        }
        remoteViews.setTextViewText(R.id.update, "Total: " + (double)Math.round(total * 100) / 100 + " kWh");

        if (total > 0) {
            UpdaterImage updater = new UpdaterImage(this);
            updater.execute(url);
            updateWidget();
        }
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