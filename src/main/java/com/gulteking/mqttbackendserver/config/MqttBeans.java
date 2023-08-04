//package com.gulteking.mqttbackendserver.config;
//
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.core.MessageProducer;
//import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
//import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
//import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
//import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
//import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
//import org.springframework.integration.mqtt.support.MqttHeaders;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHandler;
//
//import java.util.Objects;
//
//@Configuration
//public class MqttBeans {
//
//    @Value("${mqtt.brokerUrl}")
//    private String brokerUrl;
//
//    @Value("${mqtt.clientId}")
//    private String clientId;
//
//    @Bean
//    public MqttClient mqttClient() throws MqttException {
//        MqttClient mqttClient = new MqttClient(brokerUrl, clientId);
//        mqttClient.connect();
//        return mqttClient;
//    }
//
//    public MqttPahoClientFactory mqttPahoClientFactory() {
//        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
//        MqttConnectOptions options = new MqttConnectOptions();
//
//        options.setServerURIs(new String[]{"tcp://localhost:1883"});
//        options.setUserName("admin");
//        String pass = "12345678";
//        options.setPassword(pass.toCharArray());
//        options.setCleanSession(true);
//
//        factory.setConnectionOptions(options);
//        return factory;
//    }
//
//    @Bean
//    public MessageChannel mqttInputChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    public MessageProducer inbound() {
//        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("serverIn",
//                mqttPahoClientFactory(), "$");
//
//        adapter.setCompletionTimeout(5000);
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(2);
//        adapter.setOutputChannel(mqttInputChannel());
//
//        return adapter;
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "mqttInputChannel")
//    public MessageHandler handler() {
//        return message -> {
//            String topic = Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).toString();
//            if (topic.equals("myTopic")) { // pub 에서 보낸 토픽과 일치 했을때,
//                System.out.println("this is out topic");
//            }
//            System.out.println(message.getPayload()); //pub 에서 보낸 메세지
//        };
//    }
//
//    @Bean
//    public MessageChannel mqttOutboundChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "mqttOutboundChannel")
//    public MessageHandler mqttOutbound() {
//        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("serverOut", mqttPahoClientFactory());
//
//        messageHandler.setAsync(true);
//        messageHandler.setDefaultTopic("#");
//        return messageHandler;
//    }
//}
