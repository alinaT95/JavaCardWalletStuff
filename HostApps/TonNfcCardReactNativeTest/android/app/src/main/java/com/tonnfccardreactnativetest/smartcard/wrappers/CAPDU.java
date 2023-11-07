package com.tonnfccardreactnativetest.smartcard.wrappers;

import static com.tonnfccardreactnativetest.api.utils.ByteArrayHelper.*;

public class CAPDU {

    private final static byte[] EMPTY_DATA = new byte[0];

    private final static int HEADER_LENGTH = 4;

    private byte[] bytes;

    public CAPDU(int cla, int ins, int p1, int p2) {
        this.bytes = bConcat(new byte[]{(byte)cla, (byte)ins, (byte)p1, (byte)p2});
    }

    public CAPDU(int cla, int ins, int p1, int p2, int le) {
        this.bytes = bConcat(new byte[]{(byte)cla, (byte)ins, (byte)p1, (byte)p2, (byte) le});
    }

    public CAPDU(int cla, int ins, int p1, int p2, byte[] dataField) {
        checkDataField(dataField);
        this.bytes = bConcat(new byte[]{(byte)cla, (byte)ins, (byte)p1, (byte)p2, (byte) dataField.length}, dataField);
    }

    public CAPDU(int cla, int ins, int p1, int p2, byte[] dataField, int le) {
        checkDataField(dataField);
        this.bytes = bConcat(new byte[]{(byte)cla, (byte)ins, (byte)p1, (byte)p2, (byte) dataField.length}, dataField, new byte[]{(byte) le});
    }

    public byte getCla() {
        return bytes[0];
    }

    public byte getIns() {
        return bytes[1];
    }

    public byte getP1() {
        return bytes[2];
    }

    public byte getP2() {
        return bytes[3];
    }

    public int getLc() {
        if(bytes.length <= HEADER_LENGTH + 1) return 0;
        return (0xFF & bytes[4]);
    }

    public int getLe() {
        if (bytes.length <= HEADER_LENGTH ||
                (getLc() != 0 && bytes.length == HEADER_LENGTH + 1 + getLc())) return -1;
        return (0xFF & bytes[bytes.length-1]);
    }

    public byte[] getData() {
        if(getLc()> 0)
            return bSub(bytes, HEADER_LENGTH + 1, getLc());
        return EMPTY_DATA;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return hex(getBytes());
    }

    private static void checkDataField(byte[] dataField) {
        if (dataField == null || dataField.length == 0 || dataField.length > 255) throw new IllegalArgumentException("Data field in APDU must have length >=0 and <= 255 bytes.");
    }
}
