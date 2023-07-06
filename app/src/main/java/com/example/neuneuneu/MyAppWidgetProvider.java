package com.example.neuneuneu;

import android.app.ActivityManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Arrays;

public class MyAppWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_UPDATE_WIDGET = "com.example.neuneuneu.ACTION_UPDATE_WIDGET";
    public static String CO2_Value = "500";
    public static String Temp_Value =  "20";

    private AppWidgetHost appWidgetHost;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d("MyAppWidgetProvider", "onReceive called");

        if (intent.getAction().equals(ACTION_UPDATE_WIDGET)){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MyAppWidgetProvider.class));
            CO2_Value = intent.getStringExtra("CO2_Value");
            Temp_Value = intent.getStringExtra("Temp_Value");
            onUpdate(context, appWidgetManager, appWidgetIds);

            Log.d("WidgetUpdateReceiver", Arrays.toString(appWidgetIds));
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Intent serviceIntent = new Intent(context, MqttService.class);
        context.startForegroundService(serviceIntent);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.homeassistant);
        views.setTextViewText(R.id.co2, CO2_Value);
        views.setTextViewText(R.id.temp, Temp_Value);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // Enter relevant functionality for when the first widget is created
        Log.d("MyAppWidgetProvider", "onEnabled called");
        //new mqttHelper(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    }
}