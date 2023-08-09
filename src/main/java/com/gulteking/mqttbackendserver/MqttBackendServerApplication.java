package com.gulteking.mqttbackendserver;

import com.gulteking.mqttbackendserver.config.Mqtt;
import com.gulteking.mqttbackendserver.model.MqttSubscribeModel;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class MqttBackendServerApplication {

    public static void main(String[] args) throws MqttException {
        SpringApplication.run(MqttBackendServerApplication.class, args);

        String topic = "topic";
        // topic 은 미리 정의해 놓는게 선호하는 방식이다.
        /*
            java.util.regex.Pattern 라이브러리 사용해서
            ^[a-zA-Z0-9]*$ 패턴 써서 영문자+숫자로 들어오는
         */
        try {
            
            Mqtt.getInstance().subscribeWithResponse(topic,(s, mqttMessage) -> {

                // pub 으로 부터 받음
                MqttSubscribeModel mqttSubscribeModel = new MqttSubscribeModel();
                mqttSubscribeModel.setId(mqttMessage.getId());
                mqttSubscribeModel.setMessage(new String(mqttMessage.getPayload())); // ex) F7 4B B2 81 1F 01 00 XX
                mqttSubscribeModel.setQos(mqttMessage.getQos());

                String message =mqttSubscribeModel.getMessage(); // "F7 4B B2 81 1F 01 00"
                System.out.println(message);
                String[] messageParts = message.split(" "); // 문자열을 공백을 기준으로 분리
                String headerStr = messageParts[0]; // "F7"
                System.out.println(headerStr);
                String[] didStr =  {messageParts[1], messageParts[2], messageParts[3]};
                String dsubidStr =  messageParts[4];
                String cmdStr =  messageParts[5];
                String lenStr =  messageParts[6];
                String dataStr =  messageParts[7];

                byte header = (byte) Integer.parseInt(headerStr, 16); // 16진수 문자열을 바이트로 변환
                System.out.println(header);
                System.out.println("header = " + String.format("0x%02X", header)); // "header = 0xF7"

                if (header == (byte) 0xF7) {
                    System.out.println("header = F7");

                    String combinedDidStr = String.join("", didStr); // didStr 배열 값들을 합쳐서 하나의 문자열로 만듦
                    int combinedDidValue = Integer.parseInt(combinedDidStr, 16); // 16진수 문자열을 정수로 변환
                    byte did = (byte) combinedDidValue; // 정수를 바이트로 변환

                    byte cmd =  (byte) Integer.parseInt(cmdStr, 16);
                    byte dsubid = (byte) Integer.parseInt(dsubidStr, 16);
                    byte len = (byte) Integer.parseInt(lenStr, 16);
                    byte data0 = (byte) Integer.parseInt(dataStr, 16);

                    switch (cmd) {
                        case (byte) 0x01:
                            System.out.println("상태요구 (Android -> Device)");

                            if (dsubid == 0x1F) {
                                System.out.println("전체 조명 데이터 상태 요구");
                            } else if (dsubid == 0x2F) {
                                System.out.println("전체 전력 데이터 상태 요구");
                            } else if (dsubid == 0x3F) {
                                System.out.println("전체 환기 데이터 상태 요구");
                            } else if (dsubid == 0x4F) {
                                System.out.println("전체 일괄 소등 스위치 데이터 상태 요구");
                            } else if (dsubid == 0x5F) {
                                System.out.println("전체 타이머 상태 요구");
                            }

                            break;
                        case (byte) 0x41:
                            System.out.println("개별 동작 제어 요구 (Android -> Device)");
                            break;
                        case (byte) 0x42:
                            System.out.println("전체 동작 제어 요구 (Android -> Device)");
                            break;
                        case (byte) 0x43:
                            System.out.println("타이머 제어 요구 (Android -> Device):Wi-Fi 모듈 타이머 등록");
                            break;
                        case (byte) 0x21:
                            System.out.println("MAC 주소 요구 (Android -> Wi-Fi 모듈) : 서버 및 App 과 통신 없음");
                            break;
                        case (byte) 0x22:
                            System.out.println("기기등록 상태 변경 요청 (Device -> Wi-Fi 모듈) : 서버 및 App 과 통신 없음");
                            break;
                        case (byte) 0x31:
                            System.out.println("상태 변화 (Device -> Wi-Fi 모듈) : 물리적인 스위치 상태 변화");
                            break;
                        case (byte) 0x32:
                            System.out.println("전력량 전송 (Device -> Wi-Fi 모듈)");
                            break;
                        case (byte) 0x81:
                            System.out.println("상태 응답 (Device -> Android)");

                            for (int i = 7; i >= 0; i--) {
                                int bit = (data0 >> i) & 1;
                                System.out.print(bit);
                            }
                            //응답 데이터 생성 메서드
//                            MqttMessage responseMessage = new MqttMessage(new byte[]{header,did[0],did[1],did[2],dsubid,cmd,len,data0});
//                            responseMessage.setQos(0);
//                            responseMessage.setRetained(false);
//                            Mqtt.getInstance().publish(topic, responseMessage);

                            break;
                        case (byte) 0xC1:
                            System.out.println("개별 동작 제어 응답 (Device -> Android)");
                            // 위와 일치 -
                            break;
                        case (byte) 0xC2:
                            System.out.println("전체 동작 제어 응답 (Device -> Android)");
                            break;
                        case (byte) 0xC3:
                            System.out.println("타이머 제어 응답 (Device -> Android)");
                            break;
                        case (byte) 0xB1:
                            System.out.println("MAC 주소 응답 (Wi-Fi 모듈 -> Device) : 서버 및 App 과 통신 없음");
                            break;
                        case (byte) 0xB2:
                            System.out.println("기기등록 상태 변경 응답 (Wi-Fi 모듈 -> Device) : 서버 및 App 과 통신 없음");
                            break;
                        case (byte) 0xE1:
                            System.out.println("상태 변화 응답 (Android -> Device)");
                            break;
                        case (byte) 0xE2:
                            System.out.println("전력량 전송 응답 (Wi-Fi 모듈 -> Device)");
                            break;
                        default:
                            // ** 응답 시 요구 받은 값의 CMD 의 최상위 bit 를 "1" 로 변환하여 응답
                            break;
                    }


                } //if 종료


                MqttMessage publishmqttMessage = new MqttMessage(new byte[]{0x11,0x12});
                mqttMessage.setQos(0);
                mqttMessage.setRetained(false);

                Mqtt.getInstance().publish(topic, publishmqttMessage);


            });
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }

    private static JSONObject createJsonPayload(String key, String value) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, value);
        return jsonObject;
    }

}
