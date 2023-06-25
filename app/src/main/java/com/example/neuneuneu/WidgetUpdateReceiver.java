package com.example.neuneuneu;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetUpdateReceiver extends BroadcastReceiver {

    public static final String ACTION_UPDATE_WIDGET = "com.example.neuneuneu.ACTION_UPDATE_WIDGET";
    public static int value = 500;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_UPDATE_WIDGET)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, AppWidgetProvider.class));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.homeassistant);
            value = 1000;
            views.setTextViewText(R.id.value, "test");

            // Aktualisiere das Widget
            ComponentName widgetComponent = new ComponentName(context, AppWidgetProvider.class);
            appWidgetManager.updateAppWidget(widgetComponent, views);

            Log.d("WidgetUpdateReceiver", String.valueOf(context.getPackageName()));

        }
    }
}