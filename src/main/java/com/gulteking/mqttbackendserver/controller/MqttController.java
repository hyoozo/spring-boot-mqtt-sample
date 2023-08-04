package com.gulteking.mqttbackendserver.controller;

import com.gulteking.mqttbackendserver.config.Mqtt;
import com.gulteking.mqttbackendserver.decoder.DataDecoder;
import com.gulteking.mqttbackendserver.exceptions.ExceptionMessages;
import com.gulteking.mqttbackendserver.exceptions.MqttException;
import com.gulteking.mqttbackendserver.model.MqttPublishModel;
import com.gulteking.mqttbackendserver.model.MqttSubscribeModel;
import com.gulteking.mqttbackendserver.service.FrameParsingService;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/api/mqtt")
public class MqttController {

//    @Autowired
//    MqttGateway mqttGateway;
    // 갑자기 안됨.???

//    @Autowired
//    private Mqtt_Service mqtt_Service;

    @Autowired
    private FrameParsingService frameParsingService;

    /**
     * @author hj
     * @param frameString F7 4B 81 1F 01 00 XX String 으로 들어오는 값 바이트로 잘라서 파싱
     * @return WAS 로 보내기 (?) - 이건 나도 잘 모르겠다
     * @// TODO: 2023-08-04 데이터 받아보기
     * */
    @GetMapping("parseFrame")
    public ResponseEntity<DataDecoder> parseFrame(@RequestBody String frameString) throws org.eclipse.paho.client.mqttv3.MqttException {

        // 파싱 완료
        DataDecoder frame = frameParsingService.parseFrameFromString(frameString);

        System.out.println(frame);
        // MqttMessage 를 사용해야 한다????,,,

        MqttMessage mqttMessage = new MqttMessage();
        // payload 를 바이트 배열로 얻어와야 함.
        byte[] payload = frame.getPayload();
//        frame.getBytes();
        /*
        * String payload = "Hello, MQTT!";
        * message.setPayload(payload.getBytes());
        * 페이로드는 스트링 -> 그걸 바이트로 바꾼다.
        * */
        mqttMessage.setPayload(payload);
        mqttMessage.setQos(0);
        mqttMessage.setRetained(true);

        Mqtt.getInstance().publish("iot", mqttMessage); //모스키토 연결 코드..?


        return ResponseEntity.ok(frame);
    }

//    @PostMapping("/sendMessage")
//    public ResponseEntity<?> publish(@RequestBody String mqttMessage) {
//        try {
//            JsonObject convertObject = new Gson().fromJson(mqttMessage, JsonObject.class);
//            mqttGateway.senToMqtt(convertObject.get("message").toString(), convertObject.get("topic").toString());
//
//            return ResponseEntity.ok("Success");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.ok("fail");
//        }
//    }
    ///////////아진짜 모루겟댱..///////////

//    @PostMapping("/sendStateRequest")
//    public ResponseEntity<String> sendStateRequest(@RequestBody byte[] stateRequestData) throws org.eclipse.paho.client.mqttv3.MqttException {
//        mqtt_Service.publishMessage("device/data", stateRequestData, 1);
//        return ResponseEntity.ok("State request data sent successfully.");
//    }
//    @PostMapping("/publishTEST")
//    public ResponseEntity<String> publishMessage(@RequestBody String message) {
//        try {
//            // topic과 payload를 바이트 배열로 변환하여 MQTT 메시지 발행
//            mqtt_Service.publishMessage("device/data", message.getBytes(), 1);
//            return ResponseEntity.ok("Message published successfully.");
//
//        } catch (MqttException | org.eclipse.paho.client.mqttv3.MqttException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to publish message.");
//

    //.........................................................ㅎ..
    @PostMapping("publish")
    public void publishMessage(@RequestBody @Valid MqttPublishModel messagePublishModel,
                               BindingResult bindingResult) throws org.eclipse.paho.client.mqttv3.MqttException {
        if (bindingResult.hasErrors()) {
            throw new MqttException(ExceptionMessages.SOME_PARAMETERS_INVALID);
        }

        //MqttPublishModel 얘네는 누가 보내주는건데?

        MqttMessage mqttMessage = new MqttMessage(messagePublishModel.getMessage().getBytes());
        mqttMessage.setQos(messagePublishModel.getQos());
        mqttMessage.setRetained(messagePublishModel.getRetained());

        Mqtt.getInstance().publish(messagePublishModel.getTopic(), mqttMessage);
    }

    @GetMapping("subscribe")
    public List<MqttSubscribeModel> subscribeChannel(@RequestParam(value = "topic") String topic,
                                                     @RequestParam(value = "wait_millis") Integer waitMillis)
            throws InterruptedException, org.eclipse.paho.client.mqttv3.MqttException {

        List<MqttSubscribeModel> messages = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);

        Mqtt.getInstance().subscribeWithResponse(topic, (s, mqttMessage) -> {
            MqttSubscribeModel mqttSubscribeModel = new MqttSubscribeModel();
            mqttSubscribeModel.setId(mqttMessage.getId());
            mqttSubscribeModel.setMessage(new String(mqttMessage.getPayload()));
            mqttSubscribeModel.setQos(mqttMessage.getQos());
            messages.add(mqttSubscribeModel);
            countDownLatch.countDown();
        });

        countDownLatch.await(waitMillis, TimeUnit.MILLISECONDS);

        return messages;
    }


}
