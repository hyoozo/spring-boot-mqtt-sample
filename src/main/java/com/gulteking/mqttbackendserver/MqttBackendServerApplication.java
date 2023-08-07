package com.gulteking.mqttbackendserver;

import com.gulteking.mqttbackendserver.config.Mqtt;
import com.gulteking.mqttbackendserver.model.MqttSubscribeModel;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class MqttBackendServerApplication {

    public static void main(String[] args) throws MqttException {
        SpringApplication.run(MqttBackendServerApplication.class, args);

        String topic = "iotTEST";

        try {
            Mqtt.getInstance().subscribeWithResponse(topic, (s, mqttMessage) -> {
                MqttSubscribeModel mqttSubscribeModel = new MqttSubscribeModel();
                mqttSubscribeModel.setId(mqttMessage.getId());
                mqttSubscribeModel.setMessage(new String(mqttMessage.getPayload()));
                mqttSubscribeModel.setQos(mqttMessage.getQos());

                System.out.println(new String(mqttMessage.getPayload()));

                //받은 메세지를 바이트 배열로 만들기.
                //case 문 활용

                MqttMessage publishmqttMessage = new MqttMessage(new byte[]{0x11,0x12});
                mqttMessage.setQos(0);
                mqttMessage.setRetained(false);

                Mqtt.getInstance().publish(messagePublishModel.getTopic(), publishmqttMessage);


            });
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }

}
