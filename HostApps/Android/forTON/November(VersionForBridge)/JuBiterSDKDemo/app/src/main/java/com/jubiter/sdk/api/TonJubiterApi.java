package com.jubiter.sdk.api;

import android.Manifest;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.jubiter.sdk.ConnectionStateCallback;
import com.jubiter.sdk.JuBiterBLEDevice;
import com.jubiter.sdk.JuBiterWallet;
import com.jubiter.sdk.ScanResultCallback;
import com.jubiter.sdk.api.dialogs.ChangePinDialog;
import com.jubiter.sdk.api.dialogs.VerifyPinDialog;
import com.jubiter.sdk.proto.CommonProtos;

import java.util.ArrayList;
import java.util.List;

import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.DEFAULT_PIN;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.GENERATE_SEED_APDU_FOR_DEFAULT_PIN;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.GET_PIN_RTL;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.GET_PIN_TLT;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.GET_ROOT_KEY_STATUS;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.POSITIVE_APDU_RESPONSE_STATUS;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.POSITIVE_ROOT_KEY_STATUS;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.RESET_WALLET;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.SELECT_COIN_MANAGER;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.checkPin;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.getChangePIN;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.getGenerateSeedCommand;
import static com.jubiter.sdk.api.apduUtils.CoinManagerApduCommandsStuff.getVerifyPIN;
import static com.jubiter.sdk.api.apduUtils.TonAppletApduCommands.APP_PERSONALIZED_MODE;
import static com.jubiter.sdk.api.apduUtils.TonAppletApduCommands.GET_APP_STATE;
import static com.jubiter.sdk.api.apduUtils.TonAppletApduCommands.GET_PUB_KEY_WITH_DEFAULT_PATH;
import static com.jubiter.sdk.api.apduUtils.TonAppletApduCommands.SELECT_TON_WALLET_APPLET;
import static com.jubiter.sdk.api.apduUtils.TonAppletApduCommands.SET_TON_APPLET_PERSONALIZED_MODE;
import static com.jubiter.sdk.api.apduUtils.TonAppletApduCommands.ZERO_BYTE;
import static com.jubiter.sdk.api.apduUtils.TonAppletApduCommands.getApduStrForSignWithDefaultPath;
import static com.jubiter.sdk.api.utils.ByteArrayHelper.hex;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import pub.devrel.easypermissions.EasyPermissions;

public class TonJubiterApi {
    private static final String TAG = "JuBiterTest";
    private final static int REQUEST_PERMISSION = 0x1001;


    private int deviceID;
    private StringBuilder apduData = new StringBuilder();
   // private Context mContext;
    private List<JuBiterBLEDevice> mDeviceList;
    private BaseAdapter mAdapter;
    private JuBiterBLEDevice mConnectedDevice;

    private Activity activity;

    private String pin = DEFAULT_PIN;

    public TonJubiterApi(Activity activity){
        this.activity = activity;
      //  mContext = activity;

        mDeviceList = new ArrayList<>();

        if (!hasPermissions()) {
            requestPermissions("Permission request", REQUEST_PERMISSION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            JuBiterWallet.initDevice();
        }
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


//    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//        JuBiterWallet.initDevice();
//    }
//
//
//    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
//        if (EasyPermissions.somePermissionPermanentlyDenied(activity, perms)) {
//            new AppSettingsDialog.Builder(activity).build().show();
//        }
//    }
//
//
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        activity.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }

    public void scanDevice() {
        final DeviceListDialog dialog = new DeviceListDialog(activity, new DeviceListDialog.DeviceCallback() {
            @Override
            public void onShow() {
                Log.d(TAG, ">>> onShow");
                showToast(">>> onShow");
                startScan();
            }

            @Override
            public void onRefresh() {
                Log.d(TAG, ">>> onRefresh");
                showToast(">>> onRefresh");
                mDeviceList.clear();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, ">>> onCancel");
                showToast(">>> onCancel");
                JuBiterWallet.stopScan();
            }
        });
        dialog.setAdapter(mAdapter = new BleDeviceAdapter(activity, mDeviceList));
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, ">>> onItemClick: " + position);
                showToast(">>> onItemClick: " + position);
                JuBiterWallet.stopScan();
                dialog.dismiss();
                connectDevice(mDeviceList.get(position));
            }
        });
        dialog.show();
    }

    private void startScan() {
        JuBiterWallet.startScan(new ScanResultCallback() {
            @Override
            public void onScanResult(JuBiterBLEDevice device) {
                Log.d(TAG, "name : " + device.getName() + " mac : " + device.getMac()
                        + " type : " + device.getDeviceType());
                showToast("name : " + device.getName() + " mac : " + device.getMac()
                        + " type : " + device.getDeviceType());
                mDeviceList.add(device);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onStop() {

            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }


    private void connectDevice(JuBiterBLEDevice device) {
        mConnectedDevice = device;
        JuBiterWallet.connectDeviceAsync(device.getMac(), 15 * 1000, new ConnectionStateCallback() {
            @Override
            public void onConnected(String mac, int handle) {
                showToast(">>> onConnected - handle: " + handle + " mac: " + mac);
                Log.d(TAG, ">>> onConnected - handle: " + handle + " mac: " + mac);
                showToast(mac + " connected");
              //  updateStateInfo(mConnectedDevice.getName() + '\n' + mac);
                deviceID = handle;
            }

            @Override
            public void onDisconnected(String mac) {
                Log.d(TAG, ">>> disconnect: " + mac);
                showToast(mac + " disconnect");
               // updateStateInfo("");
            }

            @Override
            public void onError(int error) {
                Log.d(TAG, ">>> onError: " + error);
            }
        });
    }

    public void disconnectDevice(){
        JuBiterWallet.disconnectDevice(deviceID);
    }

    public void cancelConnect(){
        int rv = JuBiterWallet.cancelConnect(mConnectedDevice.getMac());
        Log.d(TAG, ">>> cancelConnect: " + rv);
        showToast(">>> cancelConnect: " + rv);
    }

    public void isConnected(){
        boolean rv = JuBiterWallet.isConnected(deviceID);
        Log.d(TAG, ">>> isConnected: " + rv);
        showToast(">>> isConnected: " + rv);
    }

    //todo: should return status
    public void turnOnColdWallet(){
        apduData.delete(0, apduData.length());
        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, GET_ROOT_KEY_STATUS);
        apduData.append(GET_ROOT_KEY_STATUS).append(": "). append(result.getValue()).append("\n");

        if (!result.getValue().toLowerCase().contains(POSITIVE_ROOT_KEY_STATUS)) {
            result = JuBiterWallet.sendApdu(deviceID, GENERATE_SEED_APDU_FOR_DEFAULT_PIN); // todo: fix pin issue later
            apduData.append(GENERATE_SEED_APDU_FOR_DEFAULT_PIN).append(": "). append(result.getValue()).append("\n");
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
    }


    //todo: should provide status of pin verifcation for outside
    public void showVerifyPinDialog() {
        VerifyPinDialog dialog = new VerifyPinDialog(activity, new VerifyPinDialog.Callback() {
            @Override
            public void onClickListener(String pin) {
                verifyPin(pin);
            }
        });
        dialog.show();
    }

    private void verifyPin(String pin){
        if (checkPin(pin)) {
            Log.e(TAG, "PIN should have length 4!");
            return;
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
    }

    //todo: should provide clear status of pin verifcation for outside
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

    //todo: should provide status of pin changing for outside
    public void changePinDialog() {
        ChangePinDialog dialog = new ChangePinDialog(activity, new ChangePinDialog.Callback() {
            @Override
            public void onClickListener(String oldPin, String newPin) {
                if (checkPin(oldPin) || checkPin(newPin)) {
                    Log.e(TAG, "PIN should have length 4!");
                    return;
                }

                apduData.delete(0, apduData.length());

                CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
                apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

                result = JuBiterWallet.sendApdu(deviceID, getChangePIN(oldPin, newPin));
                apduData.append(getChangePIN(oldPin, newPin)).append(": "). append(result.getValue()).append("\n");

                if (result.getValue().endsWith(POSITIVE_APDU_RESPONSE_STATUS)) {
                    pin = newPin;
                    Log.d(TAG, ">>> changePinDialog: pin is changed");
                }
                else {
                    Log.d(TAG, ">>> changePinDialog: pin is not changed");
                }

                Log.d(TAG, ">>> changePinDialog: " + apduData.toString());
            }
        });
        dialog.show();
    }

    //todo: should provide status
    public void showVerifyPinForGenerateSeedDialog() {
        VerifyPinDialog dialog = new VerifyPinDialog(activity, new VerifyPinDialog.Callback() {
            @Override
            public void onClickListener(String pin) {
                apduData.delete(0, apduData.length());

                CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
                apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

                result = JuBiterWallet.sendApdu(deviceID, getGenerateSeedCommand(pin));
                apduData.append(getGenerateSeedCommand(pin)).append(": "). append(result.getValue()).append("\n");

                Log.d(TAG, ">>> showVerifyPinForGenerateSeedDialog: " + apduData.toString());
            }
        });
        dialog.show();
    }

    // todo: dataForSigning should be in hex, check it
    // todo: return error status or signature without redundant bytes
    public String sign(String dataForSigning){

        String lc;

        if(dataForSigning.length()/2 > 256){
            Log.e(TAG, ">>> sign: " + apduData.toString());
            return "";
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

    // todo: return error status or pk without redundant bytes
    public String getPublicKey() {
        apduData.delete(0, apduData.length());

        CommonProtos.ResultString  result = JuBiterWallet.sendApdu(deviceID, SELECT_TON_WALLET_APPLET);
        apduData.append(SELECT_TON_WALLET_APPLET).append(": "). append(result.getValue()).append("\n");;

        result = JuBiterWallet.sendApdu(deviceID, GET_PUB_KEY_WITH_DEFAULT_PATH);
        apduData.append(GET_PUB_KEY_WITH_DEFAULT_PATH).append(": "). append(result.getValue()).append("\n");

        Log.d(TAG, ">>> getPublicKey: " + apduData.toString());

        return result.getValue();
    }

    // todo: return error status or MaxPinTries without redundant bytes
    public String getMaxPinTries() {
        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, GET_PIN_TLT);
        apduData.append(GET_PIN_TLT).append(": "). append(result.getValue()).append("\n");

        Log.d(TAG, ">>> getMaxPinTries: " + apduData.toString());

        return result.getValue();
    }

    // todo: return error status or RemainingPinTries without redundant bytes
    public String getRemainingPinTries(){
        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, GET_PIN_RTL);
        apduData.append(GET_PIN_RTL).append(": "). append(result.getValue()).append("\n");

        Log.d(TAG, ">>> getRemainingPinTries: " + apduData.toString());

        return result.getValue();
    }

    // todo: return error status or state without redundant bytes
    public String getTonAppletState(){
        apduData.delete(0, apduData.length());

        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_TON_WALLET_APPLET);
        apduData.append(SELECT_TON_WALLET_APPLET).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, GET_APP_STATE);
        apduData.append(GET_APP_STATE).append(": "). append(result.getValue()).append("\n");

        Log.d(TAG, ">>> getTonAppletState: " + apduData.toString());

        return result.getValue();
    }

    //todo: return clear status for outside
    public String turnOffColdWallet(){
        apduData.delete(0, apduData.length());
        CommonProtos.ResultString result = JuBiterWallet.sendApdu(deviceID, SELECT_COIN_MANAGER);
        apduData.append(SELECT_COIN_MANAGER).append(": "). append(result.getValue()).append("\n");

        result = JuBiterWallet.sendApdu(deviceID, RESET_WALLET);
        apduData.append(RESET_WALLET).append(": "). append(result.getValue()).append("\n");

        Log.d(TAG, ">>> turnOffColdWallet: " + apduData.toString());

        return result.getValue();
    }

    //todo: return clear status for outside
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

    public String getDeviceInfo() {
        StringBuffer output = new StringBuffer();
        CommonProtos.ResultAny result = JuBiterWallet.getDeviceInfo(deviceID);
        for (com.google.protobuf.Any detail : result.getValueList()) {
            try {
                CommonProtos.DeviceInfo deviceInfo = detail.unpack(CommonProtos.DeviceInfo.class);
                Log.d(TAG, ">>> getDeviceInfo : " + deviceInfo.toString());
                output.append(deviceInfo.toString()).append("\n");
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return output.toString();
    }

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

    private void showToast(final String tip) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
