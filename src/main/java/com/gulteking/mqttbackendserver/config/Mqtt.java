package com.gulteking.mqttbackendserver.config;

import org.eclipse.paho.client.mqttv3.*;

public class Mqtt {

    private static final String MQTT_PUBLISHER_ID = "spring-server"; // 어떤 아이디를 써야하지..
//    private static final String MQTT_SERVER_ADDRESS= "tcp://192.168.0.15:1883";
    private static final String MQTT_SERVER_ADDRESS= "tcp://192.168.0.127:1883";
    private static IMqttClient instance;

    public static IMqttClient getInstance() {
        try {
            if (instance == null) {
                instance = new MqttClient(MQTT_SERVER_ADDRESS, MQTT_PUBLISHER_ID);
            }

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            //instance.subscribe("#"); // 모든 토픽 구독

            if (!instance.isConnected()) {
                instance.connect(options);
                System.out.println("----Connected----");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void subscribeMethod(String topic, IMqttMessageListener messageListener) {
        try {
            instance.subscribe(topic, messageListener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private Mqtt() {

    }
}
