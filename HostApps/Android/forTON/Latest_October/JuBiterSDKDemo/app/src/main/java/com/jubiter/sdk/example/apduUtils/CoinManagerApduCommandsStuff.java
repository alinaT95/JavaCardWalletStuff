package com.jubiter.sdk.example.apduUtils;

import android.text.TextUtils;

import static com.jubiter.sdk.example.utils.ByteArrayHelper.asciiToHex;

public class CoinManagerApduCommandsStuff {
    public final static String SELECT_COIN_MANAGER = "00A40400";
    public final static String GET_ROOT_KEY_STATUS = "80CB800005DFFF028105";
    public final static String POSITIVE_ROOT_KEY_STATUS = "5a";

    public final static String CURVE_TYPE_SUFFIX = "0102";
    public final static String DEFAULT_PIN = "35353535";

    public final static String GENERATE_SEED_APDU_FOR_DEFAULT_PIN = "80CB80000DDFFE0A820304353535350102";
    public final static String GENERATE_SEED_APDU_PREFIX = "80CB80000DDFFE0A820304"; //now we suppose the length of pin is 4

    public final static String GET_PIN_RTL = "80CB800005DFFF028102"; //get remaining retry times of PIN

    public final static String GET_PIN_TLT = "80CB800005DFFF028103"; //get retry maximum times of PIN

    public final static String VERIFY_PIN_DEFAULT = "B0A200000435353535";
    public final static String VERIFY_PIN_PREFIX = "B0A2000004"; //now we suppose the length of pin is 4

    public final static String CHANGE_PIN_PREFIX = "80CB80000FDFFE0C8204"; //now we suppose the length of old and new pin is 4

    public final static String PIN_DEFAULT_LENGTH ="04";

    public final static String RESET_WALLET = "80CB800005DFFE028205";

    private static StringBuilder GENERATE_SEED = new StringBuilder();
    private static StringBuilder VERIFY_PIN = new StringBuilder();
    private static StringBuilder CHANGE_PIN = new StringBuilder();


    //80 CB 80 00 0F DF FE 0C 82 04 04 35 35 35 35 04 36 36 36 36 (CHANGE PIN)

//    private static String pin = DEFAULT_PIN;
//
//    public static String getPin() {
//        return pin;
//    }

//    public static void setPin(String pin) {
//        if(pin.length() % 2 == 0) {
//            CoinManagerApduCommandsStuff.pin = pin;
//        }
//    }


    public static String getGenerateSeedCommand(String pin){
        GENERATE_SEED.delete(0, GENERATE_SEED.length());
        if (checkPin(pin)) {
            return "PIN should have length = 4!";
        }
        return GENERATE_SEED.append(GENERATE_SEED_APDU_PREFIX).append(asciiToHex(pin)).append(CURVE_TYPE_SUFFIX).toString();
    }

    //example: User entered pin=5555. Here we transform it into string 35353535 and wrap into apdu command
    public static String getVerifyPIN(String pin){
        if (checkPin(pin)) {
            return "PIN should have length = 4!";
        }
        VERIFY_PIN.delete(0, VERIFY_PIN.length());
        return VERIFY_PIN.append(VERIFY_PIN_PREFIX).append(asciiToHex(pin)).toString();
    }

    //example: User entered oldpin=5555 and newpin=6666. Here we transform it into strings 35353535 and 36363636 and wrap into apdu command
    public static String getChangePIN(String oldPin, String newPin){
        if (checkPin(oldPin) || checkPin(newPin)) {
            return "PIN should have length = 4!";
        }
        CHANGE_PIN.delete(0, CHANGE_PIN.length());
        return CHANGE_PIN.append(CHANGE_PIN_PREFIX).append(PIN_DEFAULT_LENGTH).append(asciiToHex(oldPin))
                .append(PIN_DEFAULT_LENGTH).append(asciiToHex(newPin)).toString();
    }

    public static boolean checkPin(String pin) {
        return TextUtils.isEmpty(pin) || pin.length() != 4;
    }
}
