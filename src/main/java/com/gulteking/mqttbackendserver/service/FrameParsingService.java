package com.gulteking.mqttbackendserver.service;

import com.gulteking.mqttbackendserver.decoder.DataDecoder;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Service
public class FrameParsingService {

    /**
     * @author hj
     * @return DataDecoder
     * @// TODO: 2023-08-04 data를 사용할지 고민하기
     * */
    public DataDecoder parseFrameFromString(String frameString) {
        // F7 4B 81 1F 01 00 XX
        // 문자열을 16진수로 표현된 바이트 배열로 변환하는 메서드 호출
        byte[] frameData = parseHexStringToByteArray(frameString);
        //프레임 데이터에서 각 필드를 추출하여 변수에 할당
        byte header = frameData[0];
        int did = parseDID(frameData);
        byte dsubid = frameData[4];
        byte cmd = frameData[5];
        byte len = frameData[6];
        byte data = frameData[7];
        //추출한 필드를 사용하여 DataDecoder 객체 생성하여 반환
        return new DataDecoder(header, did, dsubid, cmd, len, data);
    }

    private int parseDID(byte[] data) {
        // DID 필드는 3바이트가 저정되기에 int 로 파싱
        byte[] didBytes = { data[1], data[2], data[3] };
        return ByteBuffer.wrap(didBytes).getInt();
    }

    private byte[] parseHexStringToByteArray(String hexString) {
        // 16진수 문자열을 바이트 배열로 봔환하는 메서드
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 바이트 배열로 변환
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
