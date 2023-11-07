package com.tonjubiterreactnativetest.api.apduUtils;

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

    /**Stuff for encryption mode**/

    //Commands only for personalization mode
    public static final String GEN_KEY_PAIR = "BOE10000";
    public static final String INIT_KEYAGREEMENT = "B0E20000";

    // Main mode
    public static final String SET_EXTERNAL_PUB_KEY_PREFIX = "B0E3000020"; // 0x20 - length of key
    public static final String GET_INNER_PUB_KEY = "B0E40000";
    public static final String SET_NONCE_PREFIX = "BOE50000";
    public static final String SET_DATA_FOR_ENCRYPTION_PREFIX = "B0E60000";
    public static final String ENCRYPT_DATA = "B0E70000";
    public static final String GET_ENCRYPTED_DATA = "B0E80000";

    //Developer mode
    public static final String GET_COMMON_DH_SECRET_DATA = "B0D20000";


    public static String getApduStrForSetDataForEncryption(String dataHexStr){
        //todo: check dataHexStr len, it should <=256 and be a multiple of 16
        return new StringBuilder().append(SET_DATA_FOR_ENCRYPTION_PREFIX ).append(dataHexStr).toString();
    }

    public static String getApduStrForSetNonce(String nonceHexStr){
        //todo: check nonceHexStr len, it should be 48
        return new StringBuilder().append(SET_NONCE_PREFIX ).append(nonceHexStr).toString();
    }

    public static String getApduStrForSetExternalPubKey(String externalPubKeyHexStr){
        //todo: check externalPubKeyHexStr len, it should be 64
        return new StringBuilder().append(SET_EXTERNAL_PUB_KEY_PREFIX).append(externalPubKeyHexStr).toString();
    }

    public static String getApduStrForSignWithDefaultPath(String dataForSigning, String lc){
        //return "B0A500000600040101010142";
        return new StringBuffer().append(SIGN_WITH_DEFAULT_PATH_PREFIX).append(lc).append(dataForSigning).append(SIGNATURE_LEN).toString();
    }
}
