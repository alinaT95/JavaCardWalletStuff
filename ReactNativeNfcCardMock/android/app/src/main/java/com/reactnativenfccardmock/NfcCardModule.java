package com.reactnativenfccardmock;

import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

public class NfcCardModule extends ReactContextBaseJavaModule /*implements ActivityEventListener, LifecycleEventListener*/  {
    public static final String TAG = "NfcMock";

    public static final byte APP_PERSONALIZED = (byte) 0x17;
    public static final byte APP_WAITE_AUTHORIZATION_MODE = (byte) 0x27;
    public static final byte APP_BLOCKED_MODE = (byte) 0x47;

    public final static short SW_WRONG_LENGTH  = (short) 0x6700;
    public final static short SW_INS_NOT_SUPPORTED  = (short) 0x6D00;
    public final static short SW_INCORRECT_PASSWORD_FOR_CARD_AUTHENICATION   = (short) 0x5F00;
    public final static short SW_INCORRECT_PASSWORD_CARD_IS_BLOCKED   = (short) 0x5F01;
    private static Map<Short, String> codeToMsg = new HashMap<>();

    public final static String hashOfCommonSecretStoredByCard = "4C6E76B27D8B5C1AB227E9670915AB077C6DC4D57B0D7125320DABAF19003618";
    public final static String commonSecretStoredByCard = "346243B69CF2F0910BC235CA0DD5605E7077A70D4BEA98B71C840B78B3883471";
    public final static String encryptedPasswordStoredByCard = "F5568AD6FA82AFDA7A4888C5E106FCDB499F864A50506AA329428F1522E9E15919E059DCC1027ECF85BB8BDF4E5A17E2B54F83C684936E70B8AFBFAECA566A73D65212CE51349D2B404C7DC33EFC6602B165B32048827AB36A7600C46639CF4B5CC9D36D36571E1A08294010DA36F2AFFAB10462BABDCD4891365D0D6C9BA3A9";
    public final static String hashOfEncryptedPasswordStoredByCard = "4694955916A4CDB4D68FDD63D38E15B7188D11E15BAED9D624F07FE48FA32272";
    public final static String passwordStoredByCard = "2BE000983E0A60DC9C2483B2EA6913CF6FCF79A5B6856FAA8D8E55A7B5D3F60A4D47C943922E88DAFC58E8405C3695BCFA51A53301BF0329504CB9B15C43A1E4E3B4620BCA6487C4B31809578F12BC71CDC56C9DA2D9B74DD129D5AB9A64C6593DEE829CC12CA6E1DF72B4A2A918DFC71981C068B211479BDE76E6795EC9E67E";
    public final static String hashOfPasswordStoredByCard = "64AA75506D760099F0406D12D7099192B27471021D22F224AD96CEFA69854562";
    public final static String initialVectorStoredByCard = "8EC699896AC80F8B2011146C06C56BA1";

    public final static String  ed25519PK = "01020356A101020356A1235689A100235689A10012A100090112A10009012233";

    public final static short PASSWORD_SIZE = 128;
    public final static short IV_SIZE = 16;

    public final static int MAX_COUNTER = 20;

    private int counter = 0;
    private byte appletState =  APP_WAITE_AUTHORIZATION_MODE;


    static{
        addError(SW_WRONG_LENGTH, "Wrong length.                                                                                   ");
        addError(SW_INS_NOT_SUPPORTED, "INS value not supported.                                                                                                                     ");
        addError(SW_INCORRECT_PASSWORD_FOR_CARD_AUTHENICATION, "Incorrect password for card authentication.                                                                                 ");
        addError(SW_INCORRECT_PASSWORD_CARD_IS_BLOCKED , "Incorrect password, card is locked.                                                                                 ");

    }
    private static void addError(Short sw, String errMsg) {
        codeToMsg.put(sw, errMsg.trim());
    }

    private ReactApplicationContext reactContext;

    public NfcCardModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @ReactMethod
    public void getTonAppletState(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                promise.resolve(getAppletStateMsg());
            }
        }).start();
    }

    @ReactMethod
    public void turnOnWallet(String newPin, String password, String commonSecret, String initialVector, Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (appletState != APP_WAITE_AUTHORIZATION_MODE)
                        throw new Exception("CARD_ERROR: " + SW_INS_NOT_SUPPORTED + "(" + codeToMsg.get(SW_INS_NOT_SUPPORTED) + ")");
                    if (((password.length() / 2) != PASSWORD_SIZE) || ((initialVector.length() /2) != NfcCardModule.IV_SIZE))
                        throw new Exception("CARD_ERROR: " + SW_WRONG_LENGTH + "(" + codeToMsg.get(SW_WRONG_LENGTH) + ")");
                    if (password.equals(passwordStoredByCard)
                            && commonSecret.equals(commonSecretStoredByCard)
                            && initialVector.equals(initialVectorStoredByCard)) {
                        appletState = APP_PERSONALIZED;
                        promise.resolve(getAppletStateMsg());
                    }
                    else {
                        counter++;
                        if (counter < NfcCardModule.MAX_COUNTER) {
                            throw new Exception("CARD_ERROR: " + SW_INCORRECT_PASSWORD_FOR_CARD_AUTHENICATION + "(" + codeToMsg.get(SW_INCORRECT_PASSWORD_FOR_CARD_AUTHENICATION) + ")");

                        }
                        else {
                            appletState = APP_BLOCKED_MODE;
                            throw new Exception("CARD_ERROR: " + SW_INCORRECT_PASSWORD_CARD_IS_BLOCKED + "(" + codeToMsg.get(SW_INCORRECT_PASSWORD_CARD_IS_BLOCKED) + ")");

                        }
                    }


//                    if (password.length() != 2 * PASSWORD_SIZE)
//                        throw new Exception("Incorrect password: password is hex string of length 256. " + password.length());
//                    if (newPin.length() != PIN_SIZE)
//                        throw new Exception("Incorrect pin: pin is a string of length 4.");
//                    if (commonSecret.length() != 2 * COMMON_SECRET_SIZE)
//                        throw new Exception("Incorrect common secret: common secret is hex string of length 64.");
//                    if (initialVector.length() != 2 * IV_SIZE)
//                        throw new Exception("Incorrect iv: iv is hex string of length 32.");
//                    if (!isHexString(password)) throw new Exception("Password is not in hex.");
//                    if (!isHexString(commonSecret)) throw new Exception("Common secret is not in hex.");
//                    if (!isHexString(initialVector)) throw new Exception("Initial vector is not in hex.");
//                    if (!isNumericString(newPin)) throw new Exception("Pin is not in numeric string.")


                    Log.d(TAG, "appletState : " + appletState);
                } catch (Exception e) {
                    handleException(e, promise);
                }
            }
        }).start();
    }



    @ReactMethod
    public void getPublicKeyForDefaultPath(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (appletState ==  APP_PERSONALIZED) {
                        String response = NfcCardModule.ed25519PK;
                        promise.resolve("Dummy public key for ed25519 (just for joy) = " + response);
                        Log.d(TAG, "getPublicKeyForDefaultPath response : " + response);
                    }
                    else throw new Exception("CARD_ERROR: " + SW_INS_NOT_SUPPORTED + "(" + codeToMsg.get(SW_INS_NOT_SUPPORTED) + ")");
                } catch (Exception e) {
                    handleException(e, promise);
                }
            }
        }).start();
    }

    private void handleException(Exception e, Promise promise) {
        promise.reject(e.getMessage());
        Log.e(TAG, e.getMessage());
    }

    @Override
    public String getName() {
        return "NfcCardModule";
    }

    private String getAppletStateMsg() {
        String msg;
        switch (appletState) {
            case NfcCardModule.APP_WAITE_AUTHORIZATION_MODE:
                msg = "Applet state = " + appletState + "  (Waite for to factor authorization)";
                break;
            case NfcCardModule.APP_BLOCKED_MODE:
                msg = "Applet state = " + appletState + "  (Blocked)";
                break;
            default:
                msg = "Applet state = " + appletState + "  (Personalized, ready to work)";
        }
        return msg;
    }
}
