package com.tonjubiterreactnativetest;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.tonjubiterreactnativetest.api.TonJubiterNfcApi;

import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.POSITIVE_APDU_RESPONSE_STATUS;
import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.SEED_IS_INIATILIZED_RESPONSE;

public class JubiterModule extends ReactContextBaseJavaModule {
    private static final String DEVICE_IS_NOT_CONNECTED = "Device is not connected!";
  //  private TonJubiterBleApi tonJubiterApi;
    private TonJubiterNfcApi tonJubiterApi;

    public JubiterModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void scan(){
        if (tonJubiterApi == null)
            tonJubiterApi = new TonJubiterNfcApi(getCurrentActivity());
           // tonJubiterApi = new TonJubiterBleApi(getCurrentActivity());
        tonJubiterApi.startScan();
    }

//    @ReactMethod
//    public void disconnectDevice(Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
//            cb.invoke(null, ""/*tonJubiterApi.disconnectDevice()*/);
//            tonJubiterApi.setDeviceID(0);
//        }
//    }

//    @ReactMethod
//    public void cancelConnect(Callback cb){
//        cb.invoke(tonJubiterApi.cancelConnect());
//    }

//    @ReactMethod
//    public void isConnected(Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
//            cb.invoke(null, ""/*tonJubiterApi.isConnected()*/);
//        }
//    }
//
//    private boolean isDeviceConnected(){
//        return tonJubiterApi != null && tonJubiterApi.getDeviceID() != 0;
//    }

//    @ReactMethod
//    public void getDeviceName(Callback cb) {
//        tonJubiterApi = new TonJubiterBleApi(getCurrentActivity());
//        // try{
//        cb.invoke(null,"!Activity = " + tonJubiterApi.getActivity().toString() /*getCurrentActivity().toString()*//*reactApplicationContext.toString()/*android.os.Build.MODEL /*getReactApplicationContext()*/);
//        //}catch (Exception e){
//        //  cb.invoke(e.toString(), null);
//        //}
//    }

    @ReactMethod
    public void turnOnWallet(String pin, Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
            String result = tonJubiterApi.turnOnColdWallet(pin);
            processCardResult(cb, result);
        //}
    }

    @ReactMethod
    public void getPublicKey(Callback cb){
        /*if (!isDeviceConnected())
            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
        else {*/
            String response = tonJubiterApi.getPublicKey();
            processCardResult(cb, response);
        //}
    }

    @ReactMethod
    public void getMaxPinTries(Callback cb){
      //  if (!isDeviceConnected())
         //   cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
       // else {
            String result = tonJubiterApi.getMaxPinTries();
            processCardResult(cb, result);
      //  }
    }

    @ReactMethod
    public void getRemainingPinTries(Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
            String result = tonJubiterApi.getRemainingPinTries();
            processCardResult(cb, result);
       // }
    }

    @ReactMethod
    public void getTonAppletState(Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
            String result = tonJubiterApi.getTonAppletState();
            processCardResult(cb, result);
        //}
    }

    @ReactMethod
    public void isSeedInitialized(Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
            String result = tonJubiterApi.isSeedInitialized();
            if (result.toUpperCase().equals(SEED_IS_INIATILIZED_RESPONSE)) {
                cb.invoke(null, true);
            } else { //some error happened
                cb.invoke(result, false);
            }
       // }
    }

    @ReactMethod
    public void verifyDefaultPin(Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
            String result = tonJubiterApi.verifyDefaultPin();
            processCardStatus(cb, result);
      //  }
    }

    @ReactMethod
    public void verifyPin(String pin, Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
            String result = tonJubiterApi.verifyPin(pin);
            processCardStatus(cb, result);
        //}
    }

    @ReactMethod
    public void changePin(String oldPin, String newPin, Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
            String result = tonJubiterApi.changePin(oldPin, newPin);
            processCardStatus(cb, result);
       // }
    }

    @ReactMethod
    public void generateSeed(String pin, Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
            String result = tonJubiterApi.generateSeed(pin);
            processCardStatus(cb, result);
        //}
    }

    @ReactMethod
    public void sign(String dataForSigning, Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
            String result = tonJubiterApi.sign(dataForSigning);
            processCardResult(cb, result);
       // }
    }

    @ReactMethod
    public void turnOffColdWallet(Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
            String result = tonJubiterApi.turnOffColdWallet();
            processCardStatus(cb, result);
        //}
    }

//    @ReactMethod
//    public void getDeviceCert(Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
//            String result = tonJubiterApi.getDeviceCert();
//            cb.invoke(null, result);
       // }
    //}

//    @ReactMethod
//    public void queryBattery(Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
//            int result = tonJubiterApi.queryBattery();
//            cb.invoke(null, result);
        //}
//    }

    @ReactMethod
    public void enumApplets(Callback cb){
//        if (!isDeviceConnected())
//            cb.invoke(DEVICE_IS_NOT_CONNECTED, null);
//        else {
            String result = tonJubiterApi.enumApplets();
            cb.invoke(null, result);
       // }
    }

    @Override
    public String getName() {
        return "JubiterModule";
    }

    private void processCardStatus(Callback cb, String result){
//        if(result.equals(POSITIVE_APDU_RESPONSE_STATUS)){
//            cb.invoke(null, true);
//        }
      //  else { //some error happened
            cb.invoke(result, false);
       // }
    }

    private void processCardResult(Callback cb, String result){
        if(result.endsWith(POSITIVE_APDU_RESPONSE_STATUS) && result.length() > POSITIVE_APDU_RESPONSE_STATUS.length()){
            cb.invoke(null, result.substring(0, result.length() - POSITIVE_APDU_RESPONSE_STATUS.length()));
        }
        else { //some error happened
            cb.invoke(result, null);
        }
    }
}
