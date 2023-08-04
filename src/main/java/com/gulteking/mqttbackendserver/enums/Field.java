package com.gulteking.mqttbackendserver.enums;

public enum Field {
    HEADER, // 프레임의 시작 의미 'F7' 사용
    DID,    // DEVICE ID, WIFI 모듈의 MAC 주소 뒷 6자리
    DSUBID, // 제어기기의 SUB ID
    CMD,    // 최상위 비트는 프레임의 전송 방향 표시 0: 모바일 > 디바이스 / 1: 디바이스 > 모바일
    LEN,    // DATA 길이
    DATA,   // 전송항 데이터 영역
    XORSUM // HEADER 에서 XOR SUM 이전까지 XOR 결과값
}


// 이넘이 필요가 있는지  .. 생각해 보자.