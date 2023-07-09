package com.example.neuneuneu;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class mqttHelper {

    private final MqttAndroidClient mqttAndroidClient;
    private final String serverUri =  "tcp://homeassistant.local:1883";

    private final String topic1 = "co2-tester/sensor/co2_value_-_a403/state";
    private final String topic2 = "co2-tester/sensor/temperatur_-_a403/state";
    private final String clientId = "Test_tafel";
    private final String username = "co2messer";
    private final String password = "p7B7g3CA";

    private final Context context;

    public static final String ACTION_UPDATE_WIDGET = "com.example.neuneuneu.ACTION_UPDATE_WIDGET";

    private static String CO2_Value;
    private static String Temp_Value;
    public static boolean status = false;


    public mqttHelper(Context context) {
        this.context = context;


        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId, Ack.AUTO_ACK);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("MqttHelper", "connectionLost: " + cause.getMessage());
                status = false;
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Erstellen Sie einen Intent mit der Aktion UPDATE_ACTION
                Intent intent = new Intent(context, MyAppWidgetProvider.class);
                intent.setAction(mqttHelper.ACTION_UPDATE_WIDGET);

                if (topic.equals(topic1)) {
                    CO2_Value = new String(message.getPayload());
                }
                else if (topic.equals(topic2)) {
                    Temp_Value = new String(message.getPayload());
                }
                intent.putExtra("CO2_Value", CO2_Value);
                intent.putExtra("Temp_Value", Temp_Value);
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
                status = true;
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                // Verbindungsfehlerbehandlung
                Log.d("MqttHelper", exception.toString());
                status = false;
            }
        });

    }

    private void subscribeToTopic() {
        mqttAndroidClient.subscribe(topic1, 0, null, new IMqttActionListener() {
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
        mqttAndroidClient.subscribe(topic2, 0, null, new IMqttActionListener() {
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
