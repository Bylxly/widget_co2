package com.example.neuneuneu;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class mqttHelper {

    private MqttAndroidClient mqttAndroidClient;
    private String serverUri =  "tcp://homeassistant.local:1883";

    private String topic = "co2data/topic";
    private String clientId = "Test_tafel";
    private String username = "co2messer";
    private String password = "p7B7g3CA";

    private Context context;

    public static final String ACTION_UPDATE_WIDGET = "com.example.neuneuneu.ACTION_UPDATE_WIDGET";


    public mqttHelper(Context context) {
        this.context = context;


        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId, Ack.AUTO_ACK);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("MqttHelper", "connectionLost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Erstellen Sie einen Intent mit der Aktion UPDATE_ACTION
                Intent intent = new Intent(context, MyAppWidgetProvider.class);
                intent.setAction(mqttHelper.ACTION_UPDATE_WIDGET);
                intent.putExtra("CO2_Value", new String(message.getPayload()));
                context.sendBroadcast(intent);
                Log.d("MqttHelper", "messageArrived");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("MqttHelper", "deliveryComplete");
            }
        });

        connect();
    }

    private void connect() {
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
                // Verbindungsfehlerbehandlung
                Log.d("MqttHelper", exception.toString());
            }
        });

    }

    private void subscribeToTopic() {
        mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("MqttHelper", "Erfolgreich Abonniert");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                // Fehlgeschlagenes Abonnement
                Log.d("MqttHelper", "Fehlgeschlagenes Abonnement");
            }
        });

    }
}
