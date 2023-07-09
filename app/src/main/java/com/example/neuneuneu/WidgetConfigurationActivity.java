package com.example.neuneuneu;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;


public class WidgetConfigurationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hier können Sie die Konfiguration für Ihr Widget festlegen
        Log.d("WidgetConfigurationActivity","oncereater");
        new mqttHelper(getApplicationContext());
        // Speichern Sie die Einstellungen und beenden Sie die Aktivität
        finish();
    }
}
