package com.gulteking.mqttbackendserver.decoder;

public class LightStatusResponseData {

    //???????

    private int DID; //너도 바이트겠찌..
    private byte DSUBID;
    private byte CMD = (byte) 0x81;
    private byte LEN = 0x01;
    private byte DATA0;

    public LightStatusResponseData(int DID, byte DSUBID, byte DATA0) {
        this.DID = DID;
        this.DSUBID = DSUBID;
        this.DATA0 = DATA0;
    }

    public byte[] toByteArray() {
        return new byte[] {
                (byte) 0xF7,
                (byte) ((DID >> 16) & 0xFF),
                (byte) ((DID >> 8) & 0xFF),
                (byte) (DID & 0xFF),
                DSUBID,
                CMD,
                LEN,
                DATA0,
                calculateXor()
        };
    }

    private byte calculateXor() {
        byte xorSum = 0;
        byte[] data = new byte[] {
                (byte) ((DID >> 16) & 0xFF),
                (byte) ((DID >> 8) & 0xFF),
                (byte) (DID & 0xFF),
                DSUBID,
                CMD,
                LEN,
                DATA0
        };
        for (byte b : data) {
            xorSum ^= b;
        }
        return xorSum;
    }
}
