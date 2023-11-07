package com.tonnfccardreactnativetest.smartcard.wrappers;


import com.tonnfccardreactnativetest.api.utils.StringHelper;

import static com.tonnfccardreactnativetest.api.utils.ByteArrayHelper.*;

public class RAPDU {
    private final byte[] bytes;
    private final byte[] data;
    private final byte[] sw;

    public RAPDU(String response) {
        this(bytes(response));
    }

    public RAPDU(byte[] bytes) {
        if (bytes == null || bytes.length < 2 ) throw new IllegalArgumentException("APDU response bytes are incorrect. It must contain at least 2 bytes of status word (SW) from the card.");
        if (bytes.length > 257 ) throw new IllegalArgumentException("APDU response is incorrect. Response from the card can not contain > 255 bytes.");
        int len = bytes.length;
        this.bytes = bytes;
        this.data = new byte[len-2];
        System.arraycopy(bytes, 0, data, 0, len - 2);
        this.sw = new byte[]{bytes[len-2], bytes[len-1]};
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getSW() {
        return sw;
    }

    public byte getSW1() {
        return sw[0];
    }

    public byte getSW2() {
        return sw[1];
    }

    public byte[] getBytes() {
        return bytes;
    }

    public static boolean isSuccess(RAPDU rapdu) {
        return rapdu.getSW1() == (byte)0x90 && rapdu.getSW2() == (byte)0x00;
    }


    @Override
    public String toString() {
        return hex(getSW()) +
                (getData()!=null && getData().length > 0
                ? " '"+   hex(getData())+"'"
                : "");
    }
}
