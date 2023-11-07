package com.tonjubiterreactnativetest.api;

import android.Manifest;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

//import com.google.protobuf.InvalidProtocolBufferException;
import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.jubiter.sdk.ConnectionStateCallback;
import com.jubiter.sdk.JuBiterBLEDevice;
import com.jubiter.sdk.JuBiterWallet;
import com.jubiter.sdk.ScanResultCallback;
import com.jubiter.sdk.proto.CommonProtos;

import pub.devrel.easypermissions.EasyPermissions;

public class TonJubiterBleApi extends TonJubiterApi{
    private static final String JUBITER_PREFIX = "jubiter";
    private static final String TAG_BLE = "JuBiterBleTest";
    private static final int REQUEST_PERMISSION = 0x1001;

    private int deviceID = 0;
    private JuBiterBLEDevice mConnectedDevice;

    public TonJubiterBleApi(Activity activity){
        super(activity, TAG_BLE);
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

    public boolean hasPermissions() {
        return EasyPermissions.hasPermissions(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public void requestPermissions(@NonNull String rationale,
                                   int requestCode, @Size(min = 1) @NonNull String... perms) {
        EasyPermissions.requestPermissions(activity, rationale, requestCode, perms);
    }

    public void startScan() {
        showToast("Start Jubiter BLE device scanning");
        JuBiterWallet.startScan(new ScanResultCallback() {
            @Override
            public void onScanResult(JuBiterBLEDevice device) {
                Log.d(TAG_BLE, "name : " + device.getName() + " mac : " + device.getMac()
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
                Log.d(TAG_BLE, ">>> onConnected - handle: " + handle + " mac: " + mac);
                showToast(mac + " connected");
                deviceID = handle;
            }

            @Override
            public void onDisconnected(String mac) {
                Log.d(TAG_BLE, ">>> disconnect: " + mac);
                showToast(mac + " disconnect");
            }

            @Override
            public void onError(int error) {
                Log.d(TAG_BLE, ">>> onError: " + error);
            }
        });
    }

    public int disconnectDevice(){
        int rv = JuBiterWallet.disconnectDevice(deviceID);
        Log.d(TAG_BLE, ">>>  disconnectDevice: " + rv);
        showToast(">>> disconnectDevice: " + rv);
        return rv;
    }

    public int cancelConnect(){
        int rv = JuBiterWallet.cancelConnect(mConnectedDevice.getMac());
        Log.d(TAG_BLE, ">>> cancelConnect: " + rv);
        showToast(">>> cancelConnect: " + rv);
        return rv;
    }

    public boolean isConnected(){
        boolean rv = JuBiterWallet.isConnected(deviceID);
        Log.d(TAG_BLE, ">>> isConnected: " + rv);
        showToast(">>> isConnected: " + rv);
        return rv;
    }

    protected String sendApduBytes(String apdu){
        return JuBiterWallet.sendApdu(deviceID, apdu).getValue();
    }

    public String getDeviceCert() {
        CommonProtos.ResultString result = JuBiterWallet.getDeviceCert(deviceID);
        Log.d(TAG_BLE,">>> getDeviceCert - rv : " + result.getStateCode() + " value: " + result.getValue());
        return result.getValue();
    }

    public int queryBattery() {
        CommonProtos.ResultInt result = JuBiterWallet.queryBattery(deviceID);
        Log.d(TAG_BLE, ">>> queryBattery - rv : " + result.getStateCode() + " value: " + result.getValue());
        return result.getValue();
    }

    public String enumApplets(){
        CommonProtos.ResultString result = JuBiterWallet.enumApplets(deviceID);
        Log.d(TAG_BLE, ">>> enumApplets - rv : " + result.getStateCode() + " value: " + result.getValue());
        return result.getValue();
    }


    //    public String getDeviceInfo() {
//        StringBuffer output = new StringBuffer();
//        CommonProtos.ResultAny result = JuBiterWallet.getDeviceInfo(deviceID);
//        for (com.google.protobuf.Any detail : result.getValueList()) {
//            try {
//                CommonProtos.DeviceInfo deviceInfo = detail.unpack(CommonProtos.DeviceInfo.class);
//                Log.d(TAG_BLE, ">>> getDeviceInfo : " + deviceInfo.toString());
//                output.append(deviceInfo.toString()).append("\n");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        }
//        return output.toString();
//    }
}
