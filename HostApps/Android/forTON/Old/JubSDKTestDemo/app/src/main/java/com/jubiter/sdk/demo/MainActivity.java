package com.jubiter.sdk.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jubiter.sdk.demo.ble.FTBTDevice;
import com.jubiter.sdk.demo.ble.FtBTConnectCallback;
import com.jubiter.sdk.demo.impl.JubCallback;
import com.jubiter.sdk.demo.impl.JubiterImpl;
import com.jubiter.sdk.demo.utils.ByteArrayHelper;

import java.util.List;

import static com.jubiter.sdk.demo.utils.ByteArrayHelper.hex;

/**
 * Copyright © FEITIAN Technologies Co., Ltd. All Rights Reserved.
 *
 * @Date 2019-05-22  16:56
 * @Author ZJF
 * @Version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private Button mScan;
    private TextView mTxtState, mTxtInfo;
    private boolean isConnect = false;
    private JubiterImpl mJubiter;
    private ProgressDialog mDialog;
    private EditText mEditDataForSigning, mEditPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mJubiter = JubiterImpl.getInstance(this);

        mScan = findViewById(R.id.scan);
        mTxtState = findViewById(R.id.text_state);
        mTxtInfo = findViewById(R.id.info_tv);
        mDialog = new ProgressDialog(this);
        mEditDataForSigning = findViewById(R.id.edit_data);
        mEditPath = findViewById(R.id.edit_path);
    }

    public void onClick(View view) {
        String dataForSigning, dataField, lc, path;
        mTxtInfo.setText(null);
        switch (view.getId()) {
            case R.id.ton_sign_with_default_path:
                dataForSigning = mEditDataForSigning.getText().toString();
                if(dataForSigning.length()/2 > 256){
                    mTxtInfo.setText("length can be represented by two hex digits!");
                    return;
                }
                dataForSigning = hex(new byte[]{(byte)(dataForSigning.length()/2)})  + dataForSigning;
                lc = hex(new byte[]{(byte)(dataForSigning.length()/2)});
                final String apduStrForSignWithDefaultPath = "B0A50000" + lc + dataForSigning;
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                sendApdu(apduStrForSignWithDefaultPath);
                break;
            case R.id.verify_pin:
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                mJubiter.verifyPin(new JubCallback<String, Void>() {
                    @Override
                    public void onSuccess(String s, Void aVoid) {
                        mDialog.dismiss();
                        mTxtInfo.setText(s + "\n");
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        mDialog.dismiss();
                        Log.e("ret:", errorCode + "");
                        mTxtInfo.setText("errorCode:" + errorCode);
                    }
                });
                break;

            case R.id.ton_sign_with_specified_path:
                dataForSigning = mEditDataForSigning.getText().toString();//""20A665A45920422F9D417E4867EFDC4FB8A04A1F3FFF1FA07E998E86F7F7A27AE3";
                if(dataForSigning.length()/2 > 256){
                    mTxtInfo.setText("length can be represented by two hex digits!");
                    return;
                }
                dataForSigning = hex(new byte[]{(byte)(dataForSigning.length()/2)})  + dataForSigning;


                path = ByteArrayHelper.asciiToHex(mEditPath.getText().toString());
                path = hex(new byte[]{(byte)(path.length()/2)})  + path;//"136D2F3434272F313731272F30272F30272F3027"
                if(dataForSigning.length()/2 + path.length()/2 > 256){
                    mTxtInfo.setText("total length can be represented by two hex digits!");
                    return;
                }

                dataField =  dataForSigning + path;

                lc = hex(new byte[]{(byte)(dataField.length()/2)});

                final String apduStrForSignWithSpecifiedPath = "B0A30000" + lc + dataField;
                //"B0 A3 00 00 20A665A45920422F9D417E4867EFDC4FB8A04A1F3FFF1FA07E998E86F7F7A27AE3 136D2F3434272F313731272F30272F30272F3027 00"
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                sendApdu(apduStrForSignWithSpecifiedPath);
                break;
            case R.id.ton_get_public_key_with_default_path:
                String apduStrForGetPubKeyWithDefaultPath = "B0A70000"; // "B0 A7 00 00"
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                sendApdu(apduStrForGetPubKeyWithDefaultPath);
                break;
            case R.id.ton_get_public_key_with_specified_path:
                path = ByteArrayHelper.asciiToHex(mEditPath.getText().toString());
                dataField = hex(new byte[]{(byte)(path.length()/2)})  + path;//"136D2F3434272F313731272F30272F30272F3027"
                if(dataField.length()/2 > 256){
                    mTxtInfo.setText("total length can be represented by two hex digits!");
                    return;
                }

                lc = hex(new byte[]{(byte)(dataField.length()/2)});
                String apduStrForGetPubKeyWithSpecifiedPath = "B0A00000" + lc + dataField;

                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                sendApdu(apduStrForGetPubKeyWithSpecifiedPath);
                break;
            case R.id.create_ton_context:
                String apduSelect = "00A404000C31313232333334343535363600";//00 A4 04 00 0C 31 31 32 32 33 33 34 34 35 35 36 36 00
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                mJubiter.sendApdu(apduSelect, new JubCallback<String, Void>() {
                    @Override
                    public void onSuccess(String s, Void aVoid) {
                        mDialog.dismiss();
                        mTxtInfo.setText(s);
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        mDialog.dismiss();
                        mTxtInfo.setText("sendAPDU failed");
                    }
                });
                break;
            case R.id.scan:
                switchBt();
                break;
           /* case R.id.btn_get_device_info:
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                mJubiter.getDeviceInfo(new JubCallback<JUB_DEVICE_INFO, Void>() {
                    @Override
                    public void onSuccess(JUB_DEVICE_INFO info, Void aVoid) {
                        mDialog.dismiss();
                        Log.d("info", info.toString());
                        mTxtInfo.setText(info.toString());
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        mDialog.dismiss();
                        Log.e("ret:", errorCode + "");
                        mTxtInfo.setText("errorCode:" + errorCode);
                    }
                });
                break;
            case R.id.btn_btc_getAddress:
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                mJubiter.btcGetAddress(new JubCallback<String, String>() {
                    @Override
                    public void onSuccess(String s, String s2) {
                        mTxtInfo.append(s + "\t");
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        mDialog.dismiss();
                        Log.e("ret:", errorCode + "");
                        mTxtInfo.setText("errorCode:" + errorCode);
                    }
                });
                break;
            case R.id.btn_btc_trans:
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                mJubiter.btcTrans(new JubCallback<String, Void>() {
                    @Override
                    public void onSuccess(String s, Void aVoid) {
                        mDialog.dismiss();
                        mTxtInfo.setText(s + "\n");
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        mDialog.dismiss();
                        Log.e("ret:", errorCode + "");
                        mTxtInfo.setText("errorCode:" + errorCode);
                    }
                });
                break;
            case R.id.btn_send_apdu:
                String apduStr = mEditApdu.getText().toString().trim().replace(" ", "");
                if (TextUtils.isEmpty(apduStr)) {
                    return;
                }
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                mJubiter.sendApdu(apduStr, new JubCallback<String, Void>() {
                    @Override
                    public void onSuccess(String s, Void aVoid) {
                        mDialog.dismiss();
                        mTxtInfo.setText(s);
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        mDialog.dismiss();
                        mTxtInfo.setText("sendAPDU failed");
                    }
                });
                break;
            case R.id.btn_applet:
                mJubiter.enumApplets(new JubCallback<List<String>, Void>() {
                    @Override
                    public void onSuccess(List<String> strings, Void aVoid) {
                        getAppletVersion(0, strings);
                    }

                    @Override
                    public void onFailed(int errorCode) {

                    }
                });
                break;
            case R.id.btn_btc_show_address:
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                mJubiter.btcShowAddress(0, 1, new JubCallback<String, Void>() {
                    @Override
                    public void onSuccess(String s, Void aVoid) {
                        mDialog.dismiss();
                        mTxtInfo.setText(s + "\n");
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        mDialog.dismiss();
                        Log.e("ret:", errorCode + "");
                        mTxtInfo.setText("errorCode:" + errorCode);
                    }
                });
                break;
            case R.id.btn_btc_set_my_address:
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                mJubiter.btcSetMyAddress(0, 1, new JubCallback<String, Void>() {
                    @Override
                    public void onSuccess(String s, Void aVoid) {
                        mDialog.dismiss();
                        mTxtInfo.setText(s + "\n");
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        mDialog.dismiss();
                        Log.e("ret:", errorCode + "");
                        mTxtInfo.setText("errorCode:" + errorCode);
                    }
                });
                break;
            case R.id.btn_set_time_out:
                mDialog.setMessage(JubApplication.getStringRes(R.string.communicating));
                mDialog.show();
                mJubiter.setTimeOut(500, new JubCallback<String, Void>() {
                    @Override
                    public void onSuccess(String s, Void aVoid) {
                        mDialog.dismiss();
                        mTxtInfo.setText(s + "\n");
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        mDialog.dismiss();
                        Log.e("ret:", errorCode + "");
                        mTxtInfo.setText("errorCode:" + errorCode);
                    }
                });
                break;*/
        }
    }

    public void sendApdu(final String apdu) {
        mJubiter.sendApdu(apdu, new JubCallback<String, Void>() {
            @Override
            public void onSuccess(String s, Void aVoid) {
                mDialog.dismiss();
                mTxtInfo.setText(s);
            }

            @Override
            public void onFailed(int errorCode) {
                mDialog.dismiss();
                mTxtInfo.setText("sendAPDU failed");
            }
        });
    }

    private void getAppletVersion(final int i, final List<String> strings) {
        if (i == strings.size()) {
            return;
        }
        mJubiter.getAppletVersion(strings.get(i), new JubCallback<String, Void>() {
            @Override
            public void onSuccess(String s, Void aVoid) {
                mTxtInfo.append("appletID:" + strings.get(i) + "\n  version:" + s + "\n\n");
                getAppletVersion(i + 1, strings);
            }

            @Override
            public void onFailed(int errorCode) {

            }
        });
    }

    private void switchBt() {
        if (isConnect) {
            mJubiter.disConnectDevice();
            mScan.setText(JubApplication.getStringRes(R.string.connect));
            mTxtInfo.setText(null);
            isConnect = false;
        } else {
            mJubiter.connect(new FtBTConnectCallback() {
                @Override
                public void connected(FTBTDevice device, int code) {
                    if (code == 0) {
                        mTxtState.setText(device.getName() + "\n" + device.getMac());
                        mScan.setText(JubApplication.getStringRes(R.string.disconnect));
                        isConnect = true;
                    } else {
                        mTxtState.setText(String.format("0x%x", code) + "\n");
                    }
                }

                @Override
                public void disConnected() {
                    mTxtState.setText("");
                    mScan.setText(JubApplication.getStringRes(R.string.connect));
                    isConnect = false;
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mJubiter.onDestroy();
    }
}
