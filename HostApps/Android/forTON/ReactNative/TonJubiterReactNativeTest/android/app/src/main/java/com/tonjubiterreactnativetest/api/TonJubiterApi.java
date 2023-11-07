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
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.GET_APP_STATE;
import static com.tonjubiterreactnativetest.api.apduUtils.TonAppletApduCommands.GET_PUB_KEY_WITH_DEFAULT_PATH;
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
    private static final int REQUEST_PERMISSION = 0x1001;



    private int deviceID = 0;
    private StringBuilder apduData = new StringBuilder();
    private JuBiterBLEDevice mConnectedDevice;

    private Activity activity;

    private String pin = "5555";

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

//    //todo: should return status
    public String turnOnColdWallet(String pin){
        if (checkPin(pin)) {
            Log.e(TAG, INCORRECT_PIN_LEN);
            return INCORRECT_PIN_LEN;
        }

        apduData.delete(0, apduData.length());
        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, GET_ROOT_KEY_STATUS);
        apduData.append(GET_ROOT_KEY_STATUS).append(": "). append(result.getValue()).append("\n");

        if (!result.getValue().toLowerCase().contains(POSITIVE_ROOT_KEY_STATUS)) {
            result = JuBiterWallet.sendApdu(deviceID, getGenerateSeedCommand(pin));
            apduData.append(getGenerateSeedCommand(pin)).append(": "). append(result.getValue()).append("\n");
            Log.d(TAG, "Fresh seed is generated.");
        }
        else {
            Log.d(TAG, "Old seed is used.");
        }

        result = JuBiterWallet.sendApdu(deviceID, SELECT_TON_WALLET_APPLET);
        apduData.append(SELECT_TON_WALLET_APPLET).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, GET_APP_STATE);
        apduData.append(GET_APP_STATE).append(": "). append(result.getValue()).append("\n");

        if (!result.getValue().toLowerCase().contains(APP_PERSONALIZED_MODE)) {
            result = JuBiterWallet.sendApdu(deviceID, SET_TON_APPLET_PERSONALIZED_MODE);
            apduData.append(SET_TON_APPLET_PERSONALIZED_MODE).append(": "). append(result.getValue()).append("\n");
        }

        Log.d(TAG, ">>> turnOnColdWallet: " + apduData.toString());

        return result.getValue();
    }

    public String verifyPin(String pin){
        if (checkPin(pin)) {
            Log.e(TAG, INCORRECT_PIN_LEN);
            return INCORRECT_PIN_LEN;
        }

        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_TON_WALLET_APPLET);
        apduData.append(SELECT_TON_WALLET_APPLET).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, getVerifyPIN(pin));
        apduData.append(getVerifyPIN(pin)).append(": "). append(result.getValue()).append("\n");

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
        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_TON_WALLET_APPLET);
        apduData.append(SELECT_TON_WALLET_APPLET).append(": "). append(result.getValue()).append("\n");


        result = JuBiterWallet.sendApdu(deviceID, getVerifyPIN(pin));
        apduData.append(getVerifyPIN(pin)).append(": "). append(result.getValue()).append("\n");

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

        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, getChangePIN(oldPin, newPin));
        apduData.append(getChangePIN(oldPin, newPin)).append(": "). append(result.getValue()).append("\n");

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

        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, getGenerateSeedCommand(pin));
        apduData.append(getGenerateSeedCommand(pin)).append(": "). append(result.getValue()).append("\n");

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

        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_TON_WALLET_APPLET);
        apduData.append(SELECT_TON_WALLET_APPLET).append(": "). append(result.getValue()).append("\n");

        dataForSigning = ZERO_BYTE + hex(new byte[]{(byte)(dataForSigning.length()/2)})  + dataForSigning;
        lc = hex(new byte[]{(byte)(dataForSigning.length()/2)});

        final String apduStrForSignWithDefaultPath = getApduStrForSignWithDefaultPath(dataForSigning, lc);
        result = JuBiterWallet.sendApdu(deviceID, apduStrForSignWithDefaultPath);
        apduData.append(apduStrForSignWithDefaultPath).append(": "). append(result.getValue()).append("\n");

        Log.d(TAG, ">>> sign: " + apduData.toString());

        return result.getValue();
    }

    public String getPublicKey() {
        apduData.delete(0, apduData.length());

        CommonProtos.ResultString  result = JuBiterWallet.sendApdu(deviceID, SELECT_TON_WALLET_APPLET);
        apduData.append(SELECT_TON_WALLET_APPLET).append(": ").append(result.getValue()).append("\n");;

        result = JuBiterWallet.sendApdu(deviceID, GET_PUB_KEY_WITH_DEFAULT_PATH);
        apduData.append(GET_PUB_KEY_WITH_DEFAULT_PATH).append(": ").append(result.getValue()).append("\n");

        Log.d(TAG, ">>> getPublicKey: " + apduData.toString());

        return result.getValue();
    }

    public String getMaxPinTries() {
        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, GET_PIN_TLT);
        apduData.append(GET_PIN_TLT).append(": "). append(result.getValue()).append("\n");

        Log.d(TAG, ">>> getMaxPinTries: " + apduData.toString());

        return result.getValue();
    }

//    // todo: return error status or RemainingPinTries without redundant bytes
    public String getRemainingPinTries(){
        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, GET_PIN_RTL);
        apduData.append(GET_PIN_RTL).append(": "). append(result.getValue()).append("\n");

        Log.d(TAG, ">>> getRemainingPinTries: " + apduData.toString());

        return result.getValue();
    }

    public String getTonAppletState(){
        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_TON_WALLET_APPLET);
        apduData.append(SELECT_TON_WALLET_APPLET).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, GET_APP_STATE);
        apduData.append(GET_APP_STATE).append(": "). append(result.getValue()).append("\n");

        Log.d(TAG, ">>> getTonAppletState: " + apduData.toString());

        return result.getValue();
    }


    public String turnOffColdWallet(){
        apduData.delete(0, apduData.length());
        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, RESET_WALLET);
        apduData.append(RESET_WALLET).append(": "). append(result.getValue()).append("\n");

        Log.d(TAG, ">>> turnOffColdWallet: " + apduData.toString());

        return result.getValue();
    }
//

    public String isSeedInitialized() {
        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, GET_ROOT_KEY_STATUS);
        apduData.append(GET_ROOT_KEY_STATUS).append(": "). append(result.getValue()).append("\n");

        Log.d(TAG, ">>> isSeedInitialized: " + apduData.toString());

        return result.getValue();
    }

    public String getDeviceCert() {
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
        CommonProtos.ResultInt result = JuBiterWallet.queryBattery(deviceID);
        Log.d(TAG, ">>> queryBattery - rv : " + result.getStateCode() + " value: " + result.getValue());
        return result.getValue();
    }

    public String enumApplets(){
        CommonProtos.ResultString result = JuBiterWallet.enumApplets(deviceID);
        Log.d(TAG, ">>> enumApplets - rv : " + result.getStateCode() + " value: " + result.getValue());
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
