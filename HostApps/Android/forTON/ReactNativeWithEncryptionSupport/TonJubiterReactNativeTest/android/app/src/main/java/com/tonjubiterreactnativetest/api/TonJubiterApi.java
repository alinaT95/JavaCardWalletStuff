package com.tonjubiterreactnativetest.api;

import android.Manifest;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

//import com.google.protobuf.InvalidProtocolBufferException;
import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.jubiter.sdk.ConnectionStateCallback;
import com.jubiter.sdk.JuBiterBLEDevice;
import com.jubiter.sdk.JuBiterWallet;
import com.jubiter.sdk.ScanResultCallback;
import com.jubiter.sdk.proto.CommonProtos;
import com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands;


import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.GENERATE_SEED_APDU_FOR_DEFAULT_PIN;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.GET_PIN_RTL;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.GET_PIN_TLT;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.GET_ROOT_KEY_STATUS;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.POSITIVE_APDU_RESPONSE_STATUS;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.POSITIVE_ROOT_KEY_STATUS;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.RESET_WALLET;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.SELECT_COIN_MANAGER;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.checkPin;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.getChangePIN;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.getGenerateSeedCommand;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.getVerifyPIN;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.APP_PERSONALIZED_MODE;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.ENCRYPT_DATA;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.GEN_KEY_PAIR;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.GET_APP_STATE;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.GET_COMMON_DH_SECRET_DATA;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.GET_ENCRYPTED_DATA;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.GET_INNER_PUB_KEY;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.GET_PUB_KEY_WITH_DEFAULT_PATH;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.INIT_KEYAGREEMENT;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.SELECT_TON_WALLET_APPLET;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.SET_TON_APPLET_PERSONALIZED_MODE;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.ZERO_BYTE;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.getApduStrForSignWithDefaultPath;
import static com.tonjubiterreactnativetest.api.utils.ByteArrayHelper.hex;

import pub.devrel.easypermissions.EasyPermissions;

public class TonJubiterApi {
    private static final String INCORRECT_PIN_LEN = "PIN should have length 4!";
    private static final String INCORRECT_DATA_LEN_ERROR = "Data should have length <= 256 bytes!";
    private static final String JUBITER_PREFIX = "jubiter";
    private static final String TAG = "JuBiterTest";
    private static final String DEFAULT_PIN = "5555";
    private static final int REQUEST_PERMISSION = 0x1001;

    private int deviceID = 0;
    private StringBuilder apduData = new StringBuilder();
    private JuBiterBLEDevice mConnectedDevice;

    private Activity activity;

    private String pin = DEFAULT_PIN ;

    public Activity getActivity() {
        return activity;
    }

    public TonJubiterApi(Activity activity){
        this.activity = activity;
        if (!hasPermissions()) {
            requestPermissions("Permission request", REQUEST_PERMISSION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            JuBiterWallet.initDevice();
        }
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public boolean hasPermissions() {
        return EasyPermissions.hasPermissions(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public void requestPermissions(@NonNull String rationale,
                                   int requestCode, @Size(min = 1) @NonNull String... perms) {
        EasyPermissions.requestPermissions(activity, rationale, requestCode, perms);
    }

    public void startScan() {
        showToast("Start Jubiter device scanning");
        JuBiterWallet.startScan(new ScanResultCallback() {
            @Override
            public void onScanResult(JuBiterBLEDevice device) {
                Log.d(TAG, "name : " + device.getName() + " mac : " + device.getMac()
                        + " type : " + device.getDeviceType());
                showToast("name : " + device.getName() + " mac : " + device.getMac()
                        + " type : " + device.getDeviceType());

                if (device.getName().toLowerCase().contains(JUBITER_PREFIX)) {
                    connectDevice(device);
                    JuBiterWallet.stopScan();
                }
            }

            @Override
            public void onStop() {
                showToast("onStop");
            }

            @Override
            public void onError(int errorCode) {
                showToast("Error occurred during scanning, code: " + errorCode);
            }
        });
    }

    private void connectDevice(JuBiterBLEDevice device) {
        showToast("connectDevice");
        mConnectedDevice = device;
        JuBiterWallet.connectDeviceAsync(device.getMac(), 15 * 1000, new ConnectionStateCallback() {
            @Override
            public void onConnected(String mac, int handle) {
                showToast(">>> onConnected - handle: " + handle + " mac: " + mac);
                Log.d(TAG, ">>> onConnected - handle: " + handle + " mac: " + mac);
                showToast(mac + " connected");
                deviceID = handle;
            }

            @Override
            public void onDisconnected(String mac) {
                Log.d(TAG, ">>> disconnect: " + mac);
                showToast(mac + " disconnect");
            }

            @Override
            public void onError(int error) {
                Log.d(TAG, ">>> onError: " + error);
            }
        });
    }

    public int disconnectDevice(){
        int rv = JuBiterWallet.disconnectDevice(deviceID);
        Log.d(TAG, ">>>  disconnectDevice: " + rv);
        showToast(">>> disconnectDevice: " + rv);
        return rv;
    }

    public int cancelConnect(){
        int rv = JuBiterWallet.cancelConnect(mConnectedDevice.getMac());
        Log.d(TAG, ">>> cancelConnect: " + rv);
        showToast(">>> cancelConnect: " + rv);
        return rv;
    }

    public boolean isConnected(){
        boolean rv = JuBiterWallet.isConnected(deviceID);
        Log.d(TAG, ">>> isConnected: " + rv);
        showToast(">>> isConnected: " + rv);
        return rv;
    }

    private void clearApduData(){
        apduData.delete(0, apduData.length());
    }

    private CommonProtos.ResultString sendApduCommand(String apdu){
        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(apdu).append(": "). append(result.getValue()).append("\n");
        return result;
    }


   //todo: should return status
    public String turnOnColdWallet(String pin){
        if (checkPin(pin)) {
            Log.e(TAG, INCORRECT_PIN_LEN);
            return INCORRECT_PIN_LEN;
        }

        clearApduData();
        sendApduCommand(SELECT_COIN_MANAGER);
        CommonProtos.ResultString result = sendApduCommand(GET_ROOT_KEY_STATUS);

        if (!result.getValue().toLowerCase().contains(POSITIVE_ROOT_KEY_STATUS)) {
            sendApduCommand(getGenerateSeedCommand(pin));
            Log.d(TAG, "Fresh seed is generated.");
        }
        else {
            Log.d(TAG, "Old seed is used.");
        }

        sendApduCommand(SELECT_TON_WALLET_APPLET);
        result = sendApduCommand(GET_APP_STATE);

        if (!result.getValue().toLowerCase().contains(APP_PERSONALIZED_MODE)) {
            result = sendApduCommand(SET_TON_APPLET_PERSONALIZED_MODE);
        }

        Log.d(TAG, ">>> turnOnColdWallet: " + apduData.toString());

        return result.getValue();
    }

    public String verifyPin(String pin){
        if (checkPin(pin)) {
            Log.e(TAG, INCORRECT_PIN_LEN);
            return INCORRECT_PIN_LEN;
        }

        clearApduData();
        sendApduCommand(SELECT_TON_WALLET_APPLET);
        CommonProtos.ResultString result = sendApduCommand(getVerifyPIN(pin));

        if (result.getValue().endsWith(POSITIVE_APDU_RESPONSE_STATUS)) {
            Log.d(TAG, ">>> verifyPin: pin is correct");
        }
        else {
            Log.d(TAG, ">>> verifyPin: pin is not correct");
        }

        Log.d(TAG, ">>> verifyPin: " + apduData.toString());

        return result.getValue();
    }

    public String verifyDefaultPin() {
        clearApduData();
        sendApduCommand(SELECT_TON_WALLET_APPLET);
        CommonProtos.ResultString result = sendApduCommand(getVerifyPIN(pin));

        if (result.getValue().endsWith(POSITIVE_APDU_RESPONSE_STATUS)) {
            Log.d(TAG, ">>> verifyDefaultPin: pin is correct");
        }
        else {
            Log.d(TAG, ">>> verifyDefaultPin: pin is not correct");
        }


        Log.d(TAG, ">>> verifyDefaultPin: " + apduData.toString());

        return result.getValue();
    }

    public String changePin(String oldPin, String newPin) {
        if (checkPin(oldPin) || checkPin(newPin)) {
            Log.e(TAG, INCORRECT_PIN_LEN);
            return INCORRECT_PIN_LEN;
        }

        clearApduData();
        sendApduCommand(SELECT_COIN_MANAGER);
        CommonProtos.ResultString result = sendApduCommand(getChangePIN(oldPin, newPin));

        if (result.getValue().endsWith(POSITIVE_APDU_RESPONSE_STATUS)) {
            setPin(newPin);
            Log.d(TAG, ">>> changePinDialog: pin is changed");
        }
        else {
            Log.d(TAG, ">>> changePinDialog: pin is not changed");
        }

        Log.d(TAG, ">>> changePinDialog: " + apduData.toString());

        return result.getValue();
    }

    public String generateSeed(String pin) {
        if (checkPin(pin)) {
            Log.e(TAG, INCORRECT_PIN_LEN);
            return INCORRECT_PIN_LEN;
        }
        clearApduData();
        sendApduCommand(SELECT_COIN_MANAGER);
        CommonProtos.ResultString result = sendApduCommand(getGenerateSeedCommand(pin));


        Log.d(TAG, ">>> showVerifyPinForGenerateSeedDialog: " + apduData.toString());

        return result.getValue();
    }

    // todo: dataForSigning should be in hex, check it
    public String sign(String dataForSigning){
        String lc;

        if(dataForSigning.length()/2 > 256){
            Log.e(TAG, ">>> sign: " + apduData.toString());
            return INCORRECT_DATA_LEN_ERROR;
        }

        clearApduData();
        sendApduCommand(SELECT_TON_WALLET_APPLET);

        dataForSigning = ZERO_BYTE + hex(new byte[]{(byte)(dataForSigning.length()/2)})  + dataForSigning;
        lc = hex(new byte[]{(byte)(dataForSigning.length()/2)});
        final String apduStrForSignWithDefaultPath = getApduStrForSignWithDefaultPath(dataForSigning, lc);

        CommonProtos.ResultString result = sendApduCommand(apduStrForSignWithDefaultPath);

        Log.d(TAG, ">>> sign: " + apduData.toString());

        return result.getValue();
    }

    public String getPublicKey() {
        clearApduData();
        sendApduCommand(SELECT_TON_WALLET_APPLET);
        CommonProtos.ResultString result = sendApduCommand(GET_PUB_KEY_WITH_DEFAULT_PATH);

        Log.d(TAG, ">>> getPublicKey: " + apduData.toString());

        return result.getValue();
    }

    public String getMaxPinTries() {
        clearApduData();
        sendApduCommand(SELECT_COIN_MANAGER);
        CommonProtos.ResultString result = sendApduCommand(GET_PIN_TLT);

        Log.d(TAG, ">>> getMaxPinTries: " + apduData.toString());

        return result.getValue();
    }

//    // todo: return error status or RemainingPinTries without redundant bytes
    public String getRemainingPinTries(){
        clearApduData();
        sendApduCommand(SELECT_COIN_MANAGER);
        CommonProtos.ResultString result = sendApduCommand(GET_PIN_RTL);

        Log.d(TAG, ">>> getRemainingPinTries: " + apduData.toString());

        return result.getValue();
    }

    public String getTonAppletState(){
        clearApduData();
        sendApduCommand(SELECT_TON_WALLET_APPLET);
        CommonProtos.ResultString result = sendApduCommand(GET_APP_STATE);
        Log.d(TAG, ">>> getTonAppletState: " + apduData.toString());

        return result.getValue();
    }


    public String turnOffColdWallet(){
        clearApduData();
        sendApduCommand(SELECT_TON_WALLET_APPLET);
        CommonProtos.ResultString result = sendApduCommand(RESET_WALLET);
        Log.d(TAG, ">>> turnOffColdWallet: " + apduData.toString());

        return result.getValue();
    }
//

    public String isSeedInitialized() {
        clearApduData();
        sendApduCommand(SELECT_COIN_MANAGER);
        CommonProtos.ResultString result = sendApduCommand( GET_ROOT_KEY_STATUS);

        Log.d(TAG, ">>> isSeedInitialized: " + apduData.toString());

        return result.getValue();
    }

    public String getDeviceCert() {
      // clearApduData();
      //  sendApduCommand(SELECT_COIN_MANAGER);
        CommonProtos.ResultString result = JuBiterWallet.getDeviceCert(deviceID);
        Log.d(TAG,">>> getDeviceCert - rv : " + result.getStateCode() + " value: " + result.getValue());
        return result.getValue();
    }

//    public String getDeviceInfo() {
//        StringBuffer output = new StringBuffer();
//        CommonProtos.ResultAny result = JuBiterWallet.getDeviceInfo(deviceID);
//        for (com.google.protobuf.Any detail : result.getValueList()) {
//            try {
//                CommonProtos.DeviceInfo deviceInfo = detail.unpack(CommonProtos.DeviceInfo.class);
//                Log.d(TAG, ">>> getDeviceInfo : " + deviceInfo.toString());
//                output.append(deviceInfo.toString()).append("\n");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        }
//        return output.toString();
//    }

    public int queryBattery() {
      //  clearApduData();
      //  sendApduCommand(SELECT_COIN_MANAGER);
        CommonProtos.ResultInt result = JuBiterWallet.queryBattery(deviceID);
        Log.d(TAG, ">>> queryBattery - rv : " + result.getStateCode() + " value: " + result.getValue());
        return result.getValue();
    }

    public String enumApplets(){
        //  clearApduData();
        //  sendApduCommand(SELECT_COIN_MANAGER);
        CommonProtos.ResultString result = JuBiterWallet.enumApplets(deviceID);
        Log.d(TAG, ">>> enumApplets - rv : " + result.getStateCode() + " value: " + result.getValue());
        return result.getValue();
    }

    // Encryption api

    public String initializeCardKeyForDHProtocol() {
        clearApduData();
        sendApduCommand(SELECT_TON_WALLET_APPLET);
        sendApduCommand(GEN_KEY_PAIR);
        CommonProtos.ResultString result = sendApduCommand(INIT_KEYAGREEMENT);

        Log.d(TAG, ">>> initializeCardKeyForDHProtocol: " + apduData.toString());

        return result.getValue();
    }

    public String getCardPublicKeyForDHProtocol() {
        clearApduData();
        sendApduCommand(SELECT_TON_WALLET_APPLET);
        CommonProtos.ResultString result = sendApduCommand(GET_INNER_PUB_KEY);

        return result.getValue();
    }

//    public String setExternalPubKeyData(String externalPubKeyHexStr) {
//        //todo: check externalPubKeyHexStr len, it should be 64
//        apduData.delete(0, apduData.length());
//
//        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_TON_WALLET_APPLET);
//        apduData.append(SELECT_TON_WALLET_APPLET).append(": "). append(result.getValue()).append("\n");
//
//        result = JuBiterWallet.sendApdu(deviceID, TonAppletApduCommands.getApduStrForSetExternalPubKey(externalPubKeyHexStr));
//        apduData.append(TonAppletApduCommands.getApduStrForSetExternalPubKey(externalPubKeyHexStr)).append(": "). append(result.getValue()).append("\n");
//
//        return result.getValue();
//    }

    //only for developer mode
    public String getCommonSecretForDHProtocol() {
        clearApduData();
        sendApduCommand(SELECT_TON_WALLET_APPLET);
        CommonProtos.ResultString result = sendApduCommand(GET_COMMON_DH_SECRET_DATA);

        return result.getValue();
    }

    public String encrypt(String message, String nonce, String theirPublicKey /*, mySecretKey*/) {
        //todo: check length of all input params
        clearApduData();
        sendApduCommand(SELECT_TON_WALLET_APPLET);
        sendApduCommand(TonAppletApduCommands.getApduStrForSetExternalPubKey(theirPublicKey)); // set external pk for Diffie Hellmann
        sendApduCommand(TonAppletApduCommands.getApduStrForSetNonce(nonce));
        sendApduCommand(TonAppletApduCommands.getApduStrForSetDataForEncryption(message));
        sendApduCommand(ENCRYPT_DATA);
        CommonProtos.ResultString result = sendApduCommand(GET_ENCRYPTED_DATA);
        return result.getValue();
    }

    public void showToast(final String tip) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
