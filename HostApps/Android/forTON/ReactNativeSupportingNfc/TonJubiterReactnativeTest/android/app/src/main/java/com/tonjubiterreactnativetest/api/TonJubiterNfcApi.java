package com.tonjubiterreactnativetest.api;

import android.app.Activity;
import android.util.Log;

import com.jubiter.sdk.JuBiterWallet;
import com.jubiter.sdk.proto.CommonProtos;
import com.tonjubiterreactnativetest.api.nfc.ApiNfc;

import static com.tonjubiterreactnativetest.api.apduUtils.CoinManagerApduCommandsStuff.GET_APPS_LIST;
import static com.tonjubiterreactnativetest.api.utils.ByteArrayHelper.hex;
import static com.tonjubiterreactnativetest.api.utils.Utils.hexString2Bytes;

//import com.google.protobuf.InvalidProtocolBufferException;

public class TonJubiterNfcApi extends TonJubiterApi{
    private static final String TAG_NFC = "JuBiterNfcTest";

    private static final String NFC_TOUCHED_MSG = "NFC hardware touched.";
    private static final String NFC_NOT_OUCHED_MSG = "NFC hardware not touched.";

    private ApiNfc apiNfc;

    public TonJubiterNfcApi(Activity activity){
        super(activity, TAG_NFC);
        apiNfc = ApiNfc.getInstance(activity);

        int re = apiNfc.setCardTag(activity.getIntent());
        if (re == 0) {
            showToast(NFC_TOUCHED_MSG);
        } else {
            showToast(NFC_NOT_OUCHED_MSG);
        }
    }

    public static final int LOG_SEND = 0;
    public static final int LOG_RECV = 1;
    public static final int LOG_ERROR = 2;

    boolean apduing = false;

   /* private String sendApdu(final List<String> apdus) {
        if (apdus.size() == 0 || apduing) {
           showToast("Done.");
            return "";
        }
        apduing = true;
        String res = "";
        String apdu = apdus.remove(0);
      // sendToMain(apdu, LOG_SEND);
        byte[] apduBytes = hexString2Bytes(apdu);
    /*   apiNfc.transInstructions(apduBytes, new ApiAsyncListener<String>() {*/
    /*        @Override
            public void onUiChange() {

            }

            @Override
            public void onResult(int errorCode, String result) {
                apduing = false;
                if (result == null) {
                 //   sendToMain(Utils.getError(errorCode), LOG_ERROR);
                    return;
                }
                if (!result.endsWith("9000")) {
                  //  sendToMain("result:" + result, LOG_ERROR);
                    return;
                }
                sendApdu(apdus);
               // sendToMain(result, LOG_RECV);
            }
        });
    }*/

//    private void sendToMain(final String s, final int logSend) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                long currentTimeMillis = System.currentTimeMillis();
//                mTxtLog.append("Time:\t" + mFormat.format(new Date(currentTimeMillis)) + "\n");
//                if (logSend == LOG_SEND) {
//                    mTxtLog.append(Html.fromHtml("<p><font color=\"#85d46f\">发送:<br>" + s + "</p>"));
//                } else if (logSend == LOG_RECV) {
//                    mTxtLog.append(Html.fromHtml("<p><font color=\"#55c0e4\">接收:<br>" + s + "</p>"));
//                } else if (logSend == LOG_ERROR) {
//                    mTxtLog.append(Html.fromHtml("<p><font color=\"#f92743\">ERROR:<br>" + s + "</p>"));
//                }
//                scrollBottom(mScrollView, mTxtLog);
//            }
//        });
//    }

 /*   private void sendApdu(String apdu) {
        ArrayList<String> list = new ArrayList<>();
        list.add(apdu);
        sendApdu(list);
    }*/

    protected String sendApduBytes(String apdu) {
        return apiNfc.transInstructions(hexString2Bytes(apdu));
    }

    public String enumApplets(){
        String result = sendApduList(GET_APPS_LIST);
        Log.d(TAG_NFC, ">>> enumApplets : "  + result);
        return result;
    }

    /*public String getDeviceCert() {
        String result = sendApduList(GET_CERT_LIST);
        Log.d(TAG_NFC, ">>> getDeviceCert : "  + result);
        return result;
    }*/





    public void startScan() {
        int re = apiNfc.setCardTag(activity.getIntent());
        if (re == 0) {
            // mTxtLog.setText("");
            showToast("NFC hardware touched.");
        }
        else {
            showToast("NFC hardware not touched.");
        }

        /*showToast("Start Jubiter device scanning");
        JuBiterWallet.startScan(new ScanResultCallback() {
            @Override
            public void onScanResult(JuBiterBLEDevice device) {
                Log.d(TAG_NFC, "name : " + device.getName() + " mac : " + device.getMac()
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
        });*/
    }


    /*private void connectDevice(JuBiterBLEDevice device) {
        showToast("connectDevice");
        mConnectedDevice = device;
        JuBiterWallet.connectDeviceAsync(device.getMac(), 15 * 1000, new ConnectionStateCallback() {
            @Override
            public void onConnected(String mac, int handle) {
                showToast(">>> onConnected - handle: " + handle + " mac: " + mac);
                Log.d(TAG_NFC, ">>> onConnected - handle: " + handle + " mac: " + mac);
                showToast(mac + " connected");
                deviceID = handle;
            }

            @Override
            public void onDisconnected(String mac) {
                Log.d(TAG_NFC, ">>> disconnect: " + mac);
                showToast(mac + " disconnect");
            }

            @Override
            public void onError(int error) {
                Log.d(TAG_NFC, ">>> onError: " + error);
            }
        });
    }*/

//    public int disconnectDevice(){
//        int rv = JuBiterWallet.disconnectDevice(deviceID);
//        Log.d(TAG_NFC, ">>>  disconnectDevice: " + rv);
//        showToast(">>> disconnectDevice: " + rv);
//        return rv;
//    }

    public int cancelConnect(){
       /* int rv = JuBiterWallet.cancelConnect(mConnectedDevice.getMac());
        Log.d(TAG_NFC, ">>> cancelConnect: " + rv);
        showToast(">>> cancelConnect: " + rv);
        return rv;*/
       return 0;
    }

//    public boolean isConnected(){
//        boolean rv = JuBiterWallet.isConnected(deviceID);
//        Log.d(TAG_NFC, ">>> isConnected: " + rv);
//        showToast(">>> isConnected: " + rv);
//        return rv;
//    }
}
