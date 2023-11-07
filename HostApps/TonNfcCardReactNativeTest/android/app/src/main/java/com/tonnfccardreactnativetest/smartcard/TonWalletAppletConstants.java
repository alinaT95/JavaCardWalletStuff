package com.tonnfccardreactnativetest.smartcard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TonWalletAppletConstants {

    public static final byte[] TON_WALLET_APPLET_AID = {0x31, 0x31, 0x32, 0x32, 0x33, 0x33, 0x34, 0x34, 0x35, 0x35, 0x36, 0x36}; //"31313232333334343535363600";

    // code of CLA byte in the command APDU header
    public final static byte WALLET_APPLET_CLA = (byte) 0xB0;

    /****************************************
     * States *
     ****************************************
     */

    public static final byte APP_INSTALLED = (byte) 0x07;
    public static final byte APP_PERSONALIZED = (byte) 0x17;
    public static final byte APP_WAITE_AUTHORIZATION_MODE = (byte) 0x27;
    public static final byte APP_DELETE_KEY_FROM_KEYCHAIN_MODE = (byte) 0x37;
    public static final byte APP_BLOCKED_MODE = (byte) 0x47;

    public static final List<Byte> ALL_APPLET_STATES = Arrays.asList(APP_INSTALLED, APP_PERSONALIZED, APP_WAITE_AUTHORIZATION_MODE, APP_DELETE_KEY_FROM_KEYCHAIN_MODE, APP_BLOCKED_MODE);
    public static final List<Byte> INSTALLED_STATE = Arrays.asList(APP_INSTALLED);
    public static final List<Byte> PERSONALIZED_STATE = Arrays.asList(APP_PERSONALIZED);
    public static final List<Byte> WAITE_AUTHORIZATION_STATE = Arrays.asList(APP_WAITE_AUTHORIZATION_MODE);
    public static final List<Byte> PERSONALIZED_AND_DELETE_STATE = Arrays.asList(APP_PERSONALIZED, APP_DELETE_KEY_FROM_KEYCHAIN_MODE);

    private static Map<Byte, String> tonWalletAppletCommandsNames = new HashMap<>();
    private static Map<Byte, String> tonWalletAppletStates = new HashMap<>();
    private static Map<Byte, List<Byte>> tonWalletAppletCommandStateMapping = new HashMap<>();

    /****************************************
     * Instruction codes *
     ****************************************
     */

    public static final byte INS_SELECT = (byte)0xA4;

    //Personalization
    public static final byte INS_FINISH_PERS = (byte)0x90;
    public static final byte INS_SET_ENCRYPTED_PASSWORD_FOR_CARD_AUTHENTICATION = (byte)0x91;
    public static final byte INS_SET_COMMON_SECRET  = (byte)0x94;

   /*
    */

    //Waite for authentication mode
    public static final byte INS_VERIFY_PASSWORD = (byte)0x92;
    public static final byte INS_GET_HASH_OF_ENCRYPTED_PASSWORD = (byte)0x93;
    public static final byte INS_GET_HASH_OF_COMMON_SECRET = (byte)0x95;


    // Main mode
    public static final byte INS_GET_PUBLIC_KEY = (byte)0xA0;
    public static final byte INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH  = (byte)0xA7;

    public static final byte INS_VERIFY_PIN = (byte)0xA2;

    public static final byte INS_SIGN_SHORT_MESSAGE = (byte)0xA3;
    public static final byte INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH = (byte)0xA5;

    public static final byte INS_GET_APP_INFO = (byte)0xC1;

    public static final byte INS_CHECK_KEY_HMAC_CONSISTENCY = (byte)0xB0;
    public static final byte INS_GET_KEY_INDEX_IN_STORAGE_AND_LEN = (byte)0xB1;
    public static final byte INS_GET_KEY_CHUNK = (byte)0xB2;
    public static final byte INS_CHECK_AVAILABLE_VOL_FOR_NEW_KEY = (byte)0xB3;
    public static final byte INS_ADD_KEY_CHUNK = (byte)0xB4;
    public static final byte INS_INITIATE_CHANGE_OF_KEY = (byte)0xB5;
    public static final byte INS_CHANGE_KEY_CHUNK = (byte)0xB6;
    public static final byte INS_INITIATE_DELETE_KEY = (byte)0xB7;
    public static final byte INS_GET_NUMBER_OF_KEYS = (byte)0xB8; /**/
    public static final byte INS_GET_FREE_STORAGE_SIZE = (byte)0xB9;
    public static final byte INS_GET_OCCUPIED_STORAGE_SIZE = (byte)0xBA;
    public static final byte INS_GET_HMAC = (byte)0xBB;
    public static final byte INS_RESET_KEYCHAIN = (byte)0xBC;
    public static final byte INS_GET_SAULT = (byte)0xBD;

    public static final byte INS_DELETE_KEY_CHUNK = (byte)0xBE;
    public static final byte INS_DELETE_KEY_RECORD = (byte)0xBF;

    public static final byte GET_APP_INFO_LE = 0x01;
    public static final byte GET_NUMBER_OF_KEYS_LE = 0x02;
    public static final byte GET_KEY_INDEX_IN_STORAGE_AND_LEN_LE = 0x04;
    public static final byte INITIATE_DELETE_KEY_LE = 0x02;
    public static final byte GET_FREE_SIZE_LE = 0x02;
    public static final byte GET_OCCUPIED_SIZE_LE  = 0x02;
    public static final byte SEND_CHUNK_LE  = 0x02;
    public static final byte DELETE_KEY_CHUNK_LE  = 0x01;
    public static final byte DELETE_KEY_RECORD_LE  = 0x01;
    public static final byte GET_HMAC_LE  = 0x22;

    public static final short DATA_PORTION_MAX_SIZE = 128;

    public static final byte PUBLIC_KEY_LEN = 32;
    public static final byte TRANSACTION_HASH_SIZE = 32;
    public static final byte SIG_LEN = 0x40;

    public static final byte[] DEFAULT_PIN = new byte[]{0x35, 0x35, 0x35, 0x35};
    public static final byte PIN_SIZE = (byte) 4;
    public final static byte MAX_PIN_TRIES_NUM = (byte) 10;

    public static final short DATA_FOR_SIGNING_MAX_SIZE = (short) 189;
    public final static short APDU_DATA_MAX_SIZE = (short) 255;

    public static final short DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH = (short) 178;

   // public final static short DATA_FOR_SIGNING_MAX_SIZE_IN_BYTES = (short) 2048;

    public final static byte SHA_HASH_SIZE = (short) 32;

    public static short PASSWORD_SIZE = 128;

    public static short IV_SIZE = 16;

    public static final byte SAULT_LENGTH = (short) 32;

    public final static short HMAC_SHA_SIG_SIZE = (short) 32;

    public static final short MAX_NUMBER_OF_KEYS_IN_KEYCHAIN = 1023;

    public static final short MAX_KEY_SIZE_IN_KEYCHAIN = 8192;

    public static final short KEY_CHAIN_SIZE = 32767;

    public final static short MAX_IND_SIZE = (short) 10;

    public static final short COMMON_SECRET_SIZE = 32;

    public static final short MAX_HMAC_FAIL_TRIES = 20;

    public static final short KEYCHAIN_KEY_INDEX_LEN = 2;

    static {
        addCommand(INS_SELECT, "SELECT_APPLET");
        addCommand(INS_FINISH_PERS, "FINISH_PERS");
        addCommand(INS_SET_ENCRYPTED_PASSWORD_FOR_CARD_AUTHENTICATION, "SET_ENCRYPTED_PASSWORD_FOR_CARD_AUTHENTICATION");
        addCommand(INS_SET_COMMON_SECRET, "SET_COMMON_SECRET");
        addCommand(INS_VERIFY_PASSWORD, "VERIFY_PASSWORD");
        addCommand(INS_GET_HASH_OF_ENCRYPTED_PASSWORD, "GET_HASH_OF_ENCRYPTED_PASSWORD");
        addCommand(INS_GET_HASH_OF_COMMON_SECRET , "GET_HASH_OF_COMMON_SECRET");
        addCommand(INS_VERIFY_PIN, "VERIFY_PIN");
        addCommand(INS_GET_PUBLIC_KEY, "GET_PUBLIC_KEY");
        addCommand(INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH, "GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH");
        addCommand(INS_SIGN_SHORT_MESSAGE, "SIGN_SHORT_MESSAGE");
        addCommand(INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH, "SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH");
        addCommand(INS_GET_APP_INFO, "GET_APP_INFO");
        addCommand(INS_GET_KEY_INDEX_IN_STORAGE_AND_LEN, "GET_KEY_INDEX_IN_STORAGE_AND_LEN");
        addCommand(INS_GET_KEY_CHUNK, "GET_KEY_CHUNK");
        addCommand(INS_CHECK_AVAILABLE_VOL_FOR_NEW_KEY, "CHECK_AVAILABLE_VOL_FOR_NEW_KEY");
        addCommand(INS_ADD_KEY_CHUNK, "ADD_KEY_CHUNK");
        addCommand(INS_INITIATE_CHANGE_OF_KEY, "INITIATE_CHANGE_OF_KEY");
        addCommand(INS_CHANGE_KEY_CHUNK, "CHANGE_KEY_CHUNK");
        addCommand(INS_DELETE_KEY_CHUNK, "DELETE_KEY_CHUNK");
        addCommand(INS_INITIATE_DELETE_KEY, "INITIATE_DELETE_KEY");
        addCommand(INS_DELETE_KEY_RECORD, "DELETE_KEY_RECORD");
        addCommand(INS_GET_NUMBER_OF_KEYS, "GET_NUMBER_OF_KEYS");
        addCommand(INS_GET_FREE_STORAGE_SIZE, "GET_FREE_STORAGE_SIZE");
        addCommand(INS_GET_OCCUPIED_STORAGE_SIZE, "GET_OCCUPIED_STORAGE_SIZE");
        addCommand(INS_GET_HMAC, "GET_HMAC");
        addCommand(INS_RESET_KEYCHAIN, "RESET_KEYCHAIN");
        addCommand(INS_GET_SAULT, "GET_SAULT");
        addCommand(INS_CHECK_KEY_HMAC_CONSISTENCY, "CHECK_KEY_HMAC_CONSISTENCY");

        addState(APP_PERSONALIZED, "TonWalletApplet is personalized.");
        addState(APP_BLOCKED_MODE, "TonWalletApplet is blocked.");
        addState(APP_WAITE_AUTHORIZATION_MODE, "TonWalletApplet waits two-factor authorization.");
        addState(APP_DELETE_KEY_FROM_KEYCHAIN_MODE, "TonWalletApplet is personalized and waits finishing key deleting from keychain.");
        addState(APP_INSTALLED, "TonWalletApplet is invalid (is not personalized)");

        addAppletCommandStatePair(INS_FINISH_PERS, INSTALLED_STATE);
        addAppletCommandStatePair(INS_SET_ENCRYPTED_PASSWORD_FOR_CARD_AUTHENTICATION, INSTALLED_STATE);
        addAppletCommandStatePair(INS_SET_COMMON_SECRET, INSTALLED_STATE);

        addAppletCommandStatePair(INS_VERIFY_PASSWORD, WAITE_AUTHORIZATION_STATE);
        addAppletCommandStatePair(INS_GET_HASH_OF_COMMON_SECRET, WAITE_AUTHORIZATION_STATE);
        addAppletCommandStatePair(INS_GET_HASH_OF_ENCRYPTED_PASSWORD, WAITE_AUTHORIZATION_STATE);

        addAppletCommandStatePair(INS_VERIFY_PIN, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_GET_PUBLIC_KEY, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_SIGN_SHORT_MESSAGE, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_GET_KEY_INDEX_IN_STORAGE_AND_LEN, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_GET_KEY_CHUNK, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_DELETE_KEY_CHUNK, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_DELETE_KEY_RECORD, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_INITIATE_DELETE_KEY, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_GET_NUMBER_OF_KEYS, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_GET_FREE_STORAGE_SIZE, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_GET_OCCUPIED_STORAGE_SIZE, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_GET_HMAC, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_RESET_KEYCHAIN, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_GET_SAULT, PERSONALIZED_AND_DELETE_STATE);
        addAppletCommandStatePair(INS_CHECK_KEY_HMAC_CONSISTENCY, PERSONALIZED_AND_DELETE_STATE);

        addAppletCommandStatePair(INS_CHECK_AVAILABLE_VOL_FOR_NEW_KEY, PERSONALIZED_STATE);
        addAppletCommandStatePair(INS_ADD_KEY_CHUNK, PERSONALIZED_STATE);
        addAppletCommandStatePair(INS_INITIATE_CHANGE_OF_KEY, PERSONALIZED_STATE);
        addAppletCommandStatePair(INS_CHANGE_KEY_CHUNK, PERSONALIZED_STATE);
        addAppletCommandStatePair(INS_GET_APP_INFO, ALL_APPLET_STATES);
    }

    public static String getCommandName(byte ins) {
        return  tonWalletAppletCommandsNames.get(ins);
    }

    private static void addCommand(byte ins, String name) {
        tonWalletAppletCommandsNames.put(ins, name.trim());
    }

    private static void addState(byte state, String name) {
        tonWalletAppletStates.put(state, name.trim());
    }

    public static String getStateName(byte state) {
        return  tonWalletAppletStates.get(state);
    }

    private static void addAppletCommandStatePair(Byte ins, List<Byte> states) {
        tonWalletAppletCommandStateMapping.put(ins, states);
    }

    public static List<Byte> getStateByIns(Byte ins) {
        return  tonWalletAppletCommandStateMapping.get(ins);
    }



}
