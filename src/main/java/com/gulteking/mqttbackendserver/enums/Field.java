package com.gulteking.mqttbackendserver.enums;

import java.util.HashMap;
import java.util.Map;

public enum Field {
    HEADER(1),
    DID(3),
    DSUBID(1),
    CMD(1),
    LEN(1),
    DATA(1),
    XORSUM(1);

    private final int byteLength;
    private static final Map<Field, Byte> fieldValues = new HashMap<>();

    static {
        fieldValues.put(HEADER, (byte) 0);
        fieldValues.put(DID, (byte) 1);
        fieldValues.put(DSUBID, (byte) 2);
        fieldValues.put(CMD, (byte) 3);
        fieldValues.put(LEN, (byte) 4);
        fieldValues.put(DATA, (byte) 5);
        fieldValues.put(XORSUM, (byte) 6);
    }

    Field(int byteLength) {
        this.byteLength = byteLength;
    }

    public int getByteLength() {
        return byteLength;
    }

    public byte getByteValue() {
        return fieldValues.get(this);
    }
}
