package com.tonjubiterreactnativetest.api;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.jubiter.sdk.JuBiterWallet;
import com.jubiter.sdk.proto.CommonProtos;

import java.util.List;

import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.GET_AVAILABLE_MEMORY_LIST;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.GET_MAX_PIN_TRIES_LIST;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.GET_REMAINING_PIN_TRIES_LIST;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.GET_ROOT_KEY_STATUS_LIST;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.POSITIVE_APDU_RESPONSE_STATUS;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.POSITIVE_ROOT_KEY_STATUS;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.RESET_WALLET_LIST;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.SELECT_COIN_MANAGER;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.checkPin;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.getChangePinApduList;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.getGenerateSeedApduList;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.getGenerateSeedCommand;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.APP_PERSONALIZED_MODE;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.GET_APPLET_STATE_LIST;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.GET_PUB_KEY_LIST;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.SET_TON_APPLET_PERSONALIZED_MODE;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.ZERO_BYTE;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.getSignApduList;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.getVerifyPinApduList;
import static com.tonjubiterreactnativetest.api.utils.ByteArrayHelper.hex;

public abstract class TonJubiterApi {
    static final String INCORRECT_PIN_LEN = "PIN should have length 4!";
    static final String INCORRECT_DATA_LEN_ERROR = "Data should have length <= 256 bytes!";

    protected String tag;

    protected String pin = "5555";

    protected Activity activity;

    protected StringBuilder apduDataLog = new StringBuilder();

    public TonJubiterApi(Activity activity, String tag){
        this.activity = activity;
        this.tag = tag;
    }

    public Activity getActivity() {
        return activity;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    protected abstract  String sendApduBytes(String apdu);

    protected String sendApdu(String apdu){
        String result = sendApduBytes(apdu);
        apduDataLog.append(apdu).append(": "). append(result).append("\n");
        return result;
    }

    protected String sendApduList(List<String> apduList){
        String result = "";
        for(String apdu : apduList){
            result = sendApdu(apdu);
        }
        return result;
    }

    protected void clearApduDataLog(){
        apduDataLog.delete(0, apduDataLog.length());
    }

    //todo: should return status
    public String turnOnColdWallet(String pin){
        if (checkPin(pin)) {
            Log.e(tag, INCORRECT_PIN_LEN);
            return INCORRECT_PIN_LEN;
        }
        clearApduDataLog();
        String result = sendApduList(GET_ROOT_KEY_STATUS_LIST);

        if (!result.toLowerCase().contains(POSITIVE_ROOT_KEY_STATUS)) {
            result = sendApdu(getGenerateSeedCommand(pin));
            Log.d(tag, "Fresh seed is generated.");
        }
        else {
            Log.d(tag, "Old seed is used.");
        }

        result = sendApduList(GET_APPLET_STATE_LIST);

        if (!result.toLowerCase().contains(APP_PERSONALIZED_MODE)) {
            result = sendApdu(SET_TON_APPLET_PERSONALIZED_MODE);
        }

        Log.d(tag, ">>> turnOnColdWallet: " + apduDataLog.toString());

        return result;
    }

    public String getMaxPinTries() {
        clearApduDataLog();
        String result = sendApduList(GET_MAX_PIN_TRIES_LIST);
        Log.d(tag, ">>> getMaxPinTries: " + apduDataLog.toString());
        return result;
    }

    public String getPublicKey() {
        clearApduDataLog();
        String result = sendApduList(GET_PUB_KEY_LIST);
        Log.d(tag, ">>> getPublicKey: " + apduDataLog.toString());
        return result;
    }

    public String verifyPin(String pin){
        if (checkPin(pin)) {
            Log.e(tag, INCORRECT_PIN_LEN);
            return INCORRECT_PIN_LEN;
        }
        clearApduDataLog();
        String result = sendApduList(getVerifyPinApduList(pin));

        if (result.endsWith(POSITIVE_APDU_RESPONSE_STATUS)) {
            Log.d(tag, ">>> verifyPin: pin is correct");
        }
        else {
            Log.d(tag, ">>> verifyPin: pin is not correct");
        }
        Log.d(tag, ">>> verifyPin: " + apduDataLog.toString());

        return result;
    }

    public String verifyDefaultPin() {
        clearApduDataLog();
        String result = sendApduList(getVerifyPinApduList(pin));
        
        if (result.endsWith(POSITIVE_APDU_RESPONSE_STATUS)) {
            Log.d(tag, ">>> verifyDefaultPin: pin is correct");
        }
        else {
            Log.d(tag, ">>> verifyDefaultPin: pin is not correct");
        }
        
        Log.d(tag, ">>> verifyDefaultPin: " + apduDataLog.toString());
        return result;
    }

    public String changePin(String oldPin, String newPin) {
        if (checkPin(oldPin) || checkPin(newPin)) {
            Log.e(tag, INCORRECT_PIN_LEN);
            return INCORRECT_PIN_LEN;
        }
        clearApduDataLog();
        String result = sendApduList(getChangePinApduList(oldPin, newPin));

        if (result.endsWith(POSITIVE_APDU_RESPONSE_STATUS)) {
            setPin(newPin);
            Log.d(tag, ">>> changePinDialog: pin is changed");
        }
        else {
            Log.d(tag, ">>> changePinDialog: pin is not changed");
        }
        Log.d(tag, ">>> changePinDialog: " + apduDataLog.toString());
        return result;
    }

    // todo: return error status or RemainingPinTries without redundant bytes
    public String getRemainingPinTries(){
        clearApduDataLog();
        String result = sendApduList(GET_REMAINING_PIN_TRIES_LIST);
        Log.d(tag, ">>> getRemainingPinTries: " + apduDataLog.toString());
        return result;
    }

    public String getTonAppletState(){
        clearApduDataLog();
        String result = sendApduList(GET_APPLET_STATE_LIST);
        Log.d(tag, ">>> getTonAppletState: " + apduDataLog.toString());
        return result;
    }

    public String isSeedInitialized() {
        clearApduDataLog();
        String result = sendApduList(GET_ROOT_KEY_STATUS_LIST);
        Log.d(tag, ">>> isSeedInitialized: " + apduDataLog.toString());
        return result;
    }

    public String turnOffColdWallet(){
        clearApduDataLog();
        String result = sendApduList(RESET_WALLET_LIST);
        Log.d(tag, ">>> turnOffColdWallet: " + apduDataLog.toString());
        return result;
    }

    public String getAvailableMemory(){
        clearApduDataLog();
        String result = sendApduList(GET_AVAILABLE_MEMORY_LIST);
        Log.d(tag, ">>> getAvailableMemory: " + apduDataLog.toString());
        return result;
    }

    public String generateSeed(String pin) {
        if (checkPin(pin)) {
            Log.e(tag, INCORRECT_PIN_LEN);
            return INCORRECT_PIN_LEN;
        }
        clearApduDataLog();
        String result = sendApduList(getGenerateSeedApduList(pin));
        Log.d(tag, ">>> generateSeed: " + apduDataLog.toString());
        return result;
    }

    // todo: dataForSigning should be in hex, check it
    public String sign(String dataForSigning){
        String lc;

        if(dataForSigning.length()/2 > 256){
            Log.e(tag, ">>> sign: " + apduDataLog.toString());
            return INCORRECT_DATA_LEN_ERROR;
        }
        clearApduDataLog();

        dataForSigning = ZERO_BYTE + hex(new byte[]{(byte)(dataForSigning.length()/2)})  + dataForSigning;
        lc = hex(new byte[]{(byte)(dataForSigning.length()/2)});

        String result = sendApduList(getSignApduList(dataForSigning, lc));
        Log.d(tag, ">>> sign: " + apduDataLog.toString());
        return result;
    }

    public abstract String enumApplets();



   // public abstract String getDeviceCert();

    //public abstract int queryBattery();

    public void showToast(final String tip) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
