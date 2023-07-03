package com.example.neuneuneu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private MqttAndroidClient mqttAndroidClient;
    private String serverUri = "tcp://192.168.178.186:1883";

    private String topic1 = "co2-tester/sensor/co2_value_-_a403/state";
    private String topic2 = "co2-tester/sensor/temperatur_-_a403/state";
    private String clientId = "Test_tafel";
    private String username = "co2messer";
    private String password = "p7B7g3CA";

    private static String CO2_Value;
    private static String Temp_Value;

    public static final String ACTION_UPDATE_WIDGET = "com.example.neuneuneu.ACTION_UPDATE_WIDGET";

    public MqttService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Erstelle den Benachrichtigungskanal
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        // Erstelle die Benachrichtigung
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MQTT Service")
                .setContentText("MQTT Service is running")
                .build();

        // Den Service als "Foreground Service" starten
        startForeground(1, notification);
        connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    private void connect() {
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId, Ack.AUTO_ACK);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("MqttHelper", "connectionLost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Intent intent = new Intent(ACTION_UPDATE_WIDGET);
                if (topic.equals(topic1)) {
                    CO2_Value = new String(message.getPayload());
                } else if (topic.equals(topic2)) {
                    Temp_Value = new String(message.getPayload());
                }
                intent.putExtra("CO2_Value", CO2_Value);
                intent.putExtra("Temp_Value", Temp_Value);
                sendBroadcast(intent);
                Log.d("MqttHelper", "messageArrived");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("MqttHelper", "deliveryComplete");
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                subscribeToTopic();
                Log.d("MqttHelper", "onSuccess");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("MqttHelper", exception.toString());
            }
        });
    }

    private void disconnect() {
        try {
            mqttAndroidClient.disconnect();
        } catch (Exception e) {
            Log.e("MqttHelper", "Error while disconnecting from MQTT server.", e);
        }
    }

    private void subscribeToTopic() {
        mqttAndroidClient.subscribe(topic1, 0, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("MqttHelper", "Successfully Subscribed");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("MqttHelper", "Failed to Subscribe");
            }
        });
        mqttAndroidClient.subscribe(topic2, 0, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("MqttHelper", "Successfully Subscribed");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("MqttHelper", "Failed to Subscribe");
            }
        });
    }
}
