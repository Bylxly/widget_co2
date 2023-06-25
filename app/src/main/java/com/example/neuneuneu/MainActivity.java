package com.example.neuneuneu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_UPDATE_WIDGET = "com.example.neuneuneu.ACTION_UPDATE_WIDGET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Intent updateIntent = new Intent(MainActivity.this, WidgetUpdateReceiver.class);
            updateIntent.setAction(MainActivity.ACTION_UPDATE_WIDGET);
            sendBroadcast(updateIntent);
        });
    }
}
