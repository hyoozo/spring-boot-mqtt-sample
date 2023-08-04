package com.gulteking.mqttbackendserver.decoder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

@Data
@NoArgsConstructor
public class DataDecoder extends MqttMessage {

    private byte header;
    private int did;
    private byte dsubid;
    private byte cmd;
    private byte len;
    private byte data;

    public DataDecoder(byte header, int did, byte dsubid, byte cmd, byte len, byte data) {
        this.header = header;
        this.did = did;
        this.dsubid = dsubid;
        this.cmd = cmd;
        this.len = len;
        this.data = data;
    }

}
