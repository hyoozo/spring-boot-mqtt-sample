package com.gulteking.mqttbackendserver.decoder;

public class StateRequestData {

    private int DID;
    private byte DSUBID;
    private byte CMD = 0x01;
    private byte LEN = 0x00;

    public StateRequestData(int DID, byte DSUBID) {
        this.DID = DID;
        this.DSUBID = DSUBID;
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
                LEN
        };
        for (byte b : data) {
            xorSum ^= b;
        }
        return xorSum;
    }
}

