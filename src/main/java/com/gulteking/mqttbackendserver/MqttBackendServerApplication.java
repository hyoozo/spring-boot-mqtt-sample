package com.gulteking.mqttbackendserver;

import com.gulteking.mqttbackendserver.config.Mqtt;
import com.gulteking.mqttbackendserver.model.MqttSubscribeModel;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
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

                MqttSubscribeModel mqttSubscribeModel = new MqttSubscribeModel();
                mqttSubscribeModel.setId(mqttMessage.getId());
                mqttSubscribeModel.setMessage(new String(mqttMessage.getPayload())); // ex) F7 4B B2 81 1F 01 00 XX
                mqttSubscribeModel.setQos(mqttMessage.getQos());

                System.out.println(new String(mqttMessage.getPayload()));

                byte[] bytes = mqttMessage.getPayload();

                System.out.println(Arrays.toString(bytes));

                byte header = bytes[0];


                if (header == (byte) 0xF7) {
                    byte cmd = bytes[5];
                    byte[] did = {bytes[1],bytes[2],bytes[3]};  // int ??
                    byte dsubid = bytes[4];
                    byte len = bytes[6];

                    switch (cmd) {
                        case (byte) 0x01:
                            if (dsubid == 0x1F) {
                                System.out.println("전체 조명 데이터 상태 요구");
                            } else if (dsubid == 0x2F) {
                                System.out.println("전체 전력 데이터 상태 요구");
                            } else if (dsubid == 0x3F) {
                                System.out.println("전체 환기 데이터 상태 요구");
                            } else if (dsubid == 0x4F) {
                                System.out.println("전체 일괄 고등 스위치 데이터 상태 요구");
                            } else if (dsubid == 0x5F) {
                                System.out.println("전체 타이머 상태 요구");
                            }
                            System.out.println("상태요구 (Android -> Device)");
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
                            break;
                        case (byte) 0xC1:
                            System.out.println("개별 동작 제어 응답 (Device -> Android)");
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

                /* DATA0 에 대한 비트 단위 행동결과 분석 */
                Byte c = null;
                for (int i = 7; i >= 0; i--) {
                    int bit = (c >> i) & 1;
                    //c는 1byte값
                }

                MqttMessage publishmqttMessage = new MqttMessage(new byte[]{0x11,0x12});
                mqttMessage.setQos(0);
                mqttMessage.setRetained(false);

                Mqtt.getInstance().publish(topic, publishmqttMessage);


            });
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }

}
