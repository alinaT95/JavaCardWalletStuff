package com.tonnfccardreactnativetest.smartcard.apdu;

import com.tonnfccardreactnativetest.api.utils.ByteArrayHelper;
import com.tonnfccardreactnativetest.smartcard.wrappers.CAPDU;
import static com.tonnfccardreactnativetest.api.utils.ByteArrayHelper.*;
import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.*;

import java.util.ArrayList;
import java.util.List;

public class CoinManagerApduCommands {
    public final static Integer LABEL_LENGTH = 32;
    public final static String POSITIVE_ROOT_KEY_STATUS = "5A";
    public final static String CURVE_TYPE_SUFFIX = "0102";

    public final static CAPDU SELECT_COIN_MANAGER_APDU = new CAPDU(0x00, 0xA4, 0x04, 0x00, 0x00); // "00A40400"
    public final static CAPDU GET_ROOT_KEY_STATUS_APDU = new CAPDU(0x80, 0xCB, 0x80, 0x00, ByteArrayHelper.bytes("DFFF028105"), 0x00); // "80CB800005DFFF028105"
    public final static CAPDU GET_APPS_APDU = new CAPDU(0x80, 0xCB, 0x80,  0x00, ByteArrayHelper.bytes("DFFF028106"), 0x00); // "80CB800005DFFF028106"
    public final static CAPDU GET_PIN_RTL_APDU =  new CAPDU(0x80, 0xCB, 0x80, 0x00, ByteArrayHelper.bytes("DFFF028102"), 0x00); // "80CB800005DFFF028102" get remaining retry times of PIN
    public final static CAPDU GET_PIN_TLT_APDU =  new CAPDU(0x80, 0xCB, 0x80, 0x00, ByteArrayHelper.bytes("DFFF028103"), 0x00); // "80CB800005DFFF028103" get retry maximum times of PIN
    public final static CAPDU RESET_WALLET_APDU =  new CAPDU(0x80, 0xCB, 0x80, 0x00, ByteArrayHelper.bytes("DFFE028205"), 0x00);
    public final static CAPDU GET_AVAILABLE_MEMORY_APDU =  new CAPDU(0x80, 0xCB, 0x80, 0x00, ByteArrayHelper.bytes("DFFE028146"), 0x00);
    public final static CAPDU GET_APPLET_LIST_APDU =  new CAPDU(0x80, 0xCB,0x80, 0x00, ByteArrayHelper.bytes("DFFF028106"), 0x00);
    public final static CAPDU GET_SE_VERSION =  new CAPDU(0x80, 0xCB, 0x80, 0x00, ByteArrayHelper.bytes("DFFF028109"), 0x00);
    public final static CAPDU GET_CSN =  new CAPDU(0x80, 0xCB, 0x80, 0x00, ByteArrayHelper.bytes("DFFF028101"), 0x00);
    public final static CAPDU GET_DEVICE_LABEL =  new CAPDU(0x80, 0xCB, 0x80, 0x00, ByteArrayHelper.bytes("DFFF028104"), 0x00);

    //example: User entered oldpin=5555 and newpin=6666. Here we transform it into strings 35353535 and 36363636 and wrap into apdu command
    public static CAPDU getChangePinAPDU(byte[] oldPinBytes, byte[] newPinBytes) {
        checkPin(oldPinBytes);
        checkPin(newPinBytes);
        byte[] data = bConcat(ByteArrayHelper.bytes("DFFE0D82040A"), new byte[]{PIN_SIZE}, oldPinBytes, new byte[]{PIN_SIZE}, newPinBytes);
        return new CAPDU(0x80, 0xCB, 0x80, 0x00,  data, 0x00);
    }

    public static CAPDU getGenerateSeedAPDU(byte[] pinBytes){
        checkPin(pinBytes);
        byte[] data = bConcat(ByteArrayHelper.bytes("DFFE08820305"), new byte[]{PIN_SIZE}, pinBytes);
        return new CAPDU(0x80, 0xCB, 0x80, 0x00,  data, 0x00);
    }

    public static CAPDU getSetDeviceLabelAPDU(byte[] labelBytes){
        if (labelBytes == null || labelBytes.length != LABEL_LENGTH ) {
            throw new IllegalArgumentException("Length of device label must be " + LABEL_LENGTH + ". ");
        }
        byte[] data = bConcat(ByteArrayHelper.bytes("DFFE238104"), new byte[]{0x20}, labelBytes);
        return new CAPDU(0x80, 0xCB,  0x80, 0x00,  data, 0x00);
    }

    private static void checkPin(byte[] pinBytes) {
        if (pinBytes == null || pinBytes.length != PIN_SIZE)
            throw new IllegalArgumentException("PIN byte array must have length " + PIN_SIZE + ".");
    }
}
