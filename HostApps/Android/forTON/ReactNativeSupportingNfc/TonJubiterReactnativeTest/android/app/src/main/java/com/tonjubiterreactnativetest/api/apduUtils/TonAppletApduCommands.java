package com.tonjubiterreactnativetest.api.apduUtils;

import java.util.ArrayList;
import java.util.List;

import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.getVerifyPIN;

public class TonAppletApduCommands {
    public final static String SELECT_TON_WALLET_APPLET = "00A404000C31313232333334343535363600"; //00 A4 04 00 0C 31 31 32 32 33 33 34 34 35 35 36 36 00

    public final static String GET_PUB_KEY_WITH_DEFAULT_PATH = "B0A70000"; //"B0A0000014136D2F3434272F313731272F30272F30272F3027"; // "B0 A7 00 00"
    public final static String SET_TON_APPLET_PERSONALIZED_MODE = "B0900100"; // "B0 90 01 00"
    public final static String SET_TON_APPLET_DEVELOPER_MODE = "B0900000"; // "B0 90 00 00"

    public final static String SIGN_WITH_DEFAULT_PATH_PREFIX = "B0A50000";

    public final static String GET_APP_STATE = "B0C10000";

    public final static String SIGNATURE_LEN = "42";

    public final static String ZERO_BYTE = "00";

    public final static String APP_PERSONALIZED_MODE = "17";
    public final static String APP_DEVELOPER_MODE = "37";

    public static final List<String> GET_PUB_KEY_LIST = new ArrayList<>();
    public static final List<String> GET_APPLET_STATE_LIST = new ArrayList<>();

    static {
        GET_PUB_KEY_LIST.add(SELECT_TON_WALLET_APPLET);
        GET_PUB_KEY_LIST.add(GET_PUB_KEY_WITH_DEFAULT_PATH);

        GET_APPLET_STATE_LIST.add(SELECT_TON_WALLET_APPLET);
        GET_APPLET_STATE_LIST.add(GET_APP_STATE);
    }

    public static List<String> getVerifyPinApduList(String pin){
        List<String> verifyPin = new ArrayList<>();
        verifyPin.add(SELECT_TON_WALLET_APPLET);
        verifyPin.add(getVerifyPIN(pin));
        return verifyPin;
    }

    public static List<String> getSignApduList(String dataForSigning, String lc){
        List<String> sign = new ArrayList<>();
        sign.add(SELECT_TON_WALLET_APPLET);
        sign.add(getApduStrForSignWithDefaultPath(dataForSigning, lc));
        return sign;
    }



    public static String getApduStrForSignWithDefaultPath(String dataForSigning, String lc){
        //return "B0A500000600040101010142";
        return new StringBuffer().append(SIGN_WITH_DEFAULT_PATH_PREFIX).append(lc).append(dataForSigning).append(SIGNATURE_LEN).toString();
    }
}
