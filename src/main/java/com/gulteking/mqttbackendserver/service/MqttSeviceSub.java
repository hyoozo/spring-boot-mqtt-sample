package com.gulteking.mqttbackendserver.service;

import org.eclipse.paho.client.mqttv3.*;

public class MqttSeviceSub implements MqttCallback {

    //broker와 통신하는 역할을 담당 - subscriber, publisher의 역할
    private MqttClient mqttClient;

    // MQTT 프로토콜을 이용해서 broker에 연결하면서 연결 정보를 설정할 수 있는 객체
    private MqttConnectOptions mqttOption;

    // clientId : (중요) broker가 클라이언트를 식별하기 위한 문자열 - 고유
    public MqttSeviceSub init(String server, String clientId) {
        try {
            mqttOption = new MqttConnectOptions();
            mqttOption.setCleanSession(true);
            mqttOption.setKeepAliveInterval(30);

            //broker에 subscribe하기 위한 클라이언트 객체 생성
            mqttClient = new MqttClient(server, clientId);

            //클라이언트 객체에 MqttCallback을 등록
            // - 구독신청 후 적절한 시점에 처리하고 싶은 기능을 구현하고 메소드가 자동으로 그 시점에 호출되도록 할 수있다.
            mqttClient.setCallback(this);
            mqttClient.connect(null);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return this;
    }

    // 커넥션이 종료되면 호출 -  통신오류로 연결이 끊어지는 경우 호출
    @Override
    public void connectionLost(Throwable arg0) {

    }

        // 메시지의 배달이 완료되면 호출

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    // 메시지가 도착하면 호출되는 메소드
    @Override
    public void messageArrived (String topic, MqttMessage message) throws Exception {
        System.out.println("=======메세지도착=====");
        System.out.println(message);
        System.out.println("topic:" + topic + ", id:" + message.getId() +
                ", payload:" + new String(message.getPayload()));
        //getPalyload 는 바이트이기 때문에 String으로 변환
    }

    //구독신청
    public boolean subscribe (String topic){
        boolean result = true;

        try {
            if (topic != null) {
                // topic과 Qos를 전달
                // Qos는 메시지가 도착하기 위한 품질에 값을 설정 - 서비스 품질
                // 0,1,2를 설정
                mqttClient.subscribe(topic, 0);
            }

        } catch (MqttException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

}
