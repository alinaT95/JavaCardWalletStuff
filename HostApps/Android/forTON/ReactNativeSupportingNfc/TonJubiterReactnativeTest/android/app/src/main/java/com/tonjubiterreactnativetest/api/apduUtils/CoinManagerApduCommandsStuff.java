package com.tonjubiterreactnativetest.api.apduUtils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import static com.tonjubiterreactnativetest.api.utils.ByteArrayHelper.asciiToHex;

public class CoinManagerApduCommandsStuff {
    public final static String SELECT_COIN_MANAGER = "00A40400";
    public final static String GET_ROOT_KEY_STATUS = "80CB800005DFFF028105";
    public final static String POSITIVE_ROOT_KEY_STATUS = "5a";

    public static final String GET_APPS = "80CB800005DFFF028106";

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

    public final static String POSITIVE_APDU_RESPONSE_STATUS = "9000";

    public final static String SEED_IS_INIATILIZED_RESPONSE = "5A9000";


   // public static final String IMPORT_MNEMONIC =    "80CB80005DDFFE5A820257043535353510042BA54A95B4E29E89A10F7BFA6F1166400AE3F096B7B0F46AFECCCB1B9B170CA02342CC4948AD9E9D2259262F28783FBDBE16AF85228F9E0945923A4D65FDF5F549115D06E404AE6880048B745B2ACBFC00";
    //public static final String EXPORT_MNEMONIC =    "80CB80000BDFFF08820205043535353500";
   // public static final String GENERATE_SEED =      "80CB80000BDFFE08820305043535353500";
   // public static final String CHANGE_PIN =         "80CB800010DFFE0D82040A0435353535043535353500";
  //  public static final String RESET_WALLET =       "80CB800005DFFE02820500";
    public static final String GET_AVAILABLE_MEMORY =     "80CB800005DFFE028146";
  //  public static final String VERIFY_PIN =         "002003000435353535";

    private static StringBuilder GENERATE_SEED = new StringBuilder();
    private static StringBuilder VERIFY_PIN = new StringBuilder();
    private static StringBuilder CHANGE_PIN = new StringBuilder();

    public static final List<String> GET_MAX_PIN_TRIES_LIST = new ArrayList<>();
    public static final List<String> GET_REMAINING_PIN_TRIES_LIST = new ArrayList<>();
    public static final List<String> GET_ROOT_KEY_STATUS_LIST = new ArrayList<>();
    public static final List<String> RESET_WALLET_LIST = new ArrayList<>();
    public static final List<String> GET_APPS_LIST = new ArrayList<>();
    public static final List<String> GET_CERT_LIST = new ArrayList<>();
    public static final List<String> GET_AVAILABLE_MEMORY_LIST = new ArrayList<>();

    static {
        GET_MAX_PIN_TRIES_LIST.add(SELECT_COIN_MANAGER);
        GET_MAX_PIN_TRIES_LIST.add(GET_PIN_TLT);

        GET_REMAINING_PIN_TRIES_LIST.add(SELECT_COIN_MANAGER);
        GET_REMAINING_PIN_TRIES_LIST.add(GET_PIN_RTL);

        GET_ROOT_KEY_STATUS_LIST.add(SELECT_COIN_MANAGER);
        GET_ROOT_KEY_STATUS_LIST.add(GET_ROOT_KEY_STATUS);

        RESET_WALLET_LIST.add(SELECT_COIN_MANAGER);
        RESET_WALLET_LIST.add(RESET_WALLET);

        GET_APPS_LIST.add(SELECT_COIN_MANAGER);
        GET_APPS_LIST.add(GET_APPS);

        GET_CERT_LIST.add(SELECT_COIN_MANAGER);
        //GET_CERT_LIST.

        GET_AVAILABLE_MEMORY_LIST.add(SELECT_COIN_MANAGER);
        GET_AVAILABLE_MEMORY_LIST.add(GET_AVAILABLE_MEMORY);
    }

    public static List<String> getChangePinApduList(String oldPin,String newPin){
        List<String> changePin = new ArrayList<>();
        changePin.add(SELECT_COIN_MANAGER);
        changePin.add(getChangePIN(oldPin, newPin));
        return changePin;
    }

    public static List<String> getGenerateSeedApduList(String pin){
        List<String> generateSeed = new ArrayList<>();
        generateSeed.add(SELECT_COIN_MANAGER);
        generateSeed.add(getGenerateSeedCommand(pin));
        return generateSeed;
    }


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
