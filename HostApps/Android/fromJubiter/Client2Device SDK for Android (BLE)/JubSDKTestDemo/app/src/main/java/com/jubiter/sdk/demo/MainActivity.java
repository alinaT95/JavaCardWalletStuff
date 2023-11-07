package com.jubiter.sdk.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jubiter.sdk.demo.ble.FTBTDevice;
import com.jubiter.sdk.demo.ble.FtBTConnectCallback;
import com.jubiter.sdk.demo.impl.JubCallback;
import com.jubiter.sdk.demo.impl.JubiterImpl;
import com.jubiter.sdk.demo.jubnative.utils.JUB_DEVICE_INFO;

import java.util.List;

/**
 * Copyright © FEITIAN Technologies Co., Ltd. All Rights Reserved.
 *
 * @Date 2019-05-22  16:56
 * @Author ZJF
 * @Version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private Button mBtnScan;
    private TextView mTxtState, mTxtInfo;
    private boolean isConnect = false;
    private JubiterImpl mJubiter;
    private ProgressDialog mDialog;
    private EditText mEditApdu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mJubiter = JubiterImpl.getInstance(this);

        mBtnScan = findViewById(R.id.btn_scan);
        mTxtState = findViewById(R.id.text_state);
        mTxtInfo = findViewById(R.id.info_tv);
        mDialog = new ProgressDialog(this);
        mEditApdu = findViewById(R.id.edit_apdu);
    }

    public void onClick(View view) {
        mTxtInfo.setText(null);
        switch (view.getId()) {
            case R.id.btn_scan:
                switchBt();
                break;
            case R.id.btn_get_device_info:
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
                break;
        }
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
            mBtnScan.setText(JubApplication.getStringRes(R.string.connect));
            mTxtInfo.setText(null);
            isConnect = false;
        } else {
            mJubiter.connect(new FtBTConnectCallback() {
                @Override
                public void connected(FTBTDevice device, int code) {
                    if (code == 0) {
                        mTxtState.setText(device.getName() + "\n" + device.getMac());
                        mBtnScan.setText(JubApplication.getStringRes(R.string.disconnect));
                        isConnect = true;
                    } else {
                        mTxtState.setText(String.format("0x%x", code) + "\n");
                    }
                }

                @Override
                public void disConnected() {
                    mTxtState.setText("");
                    mBtnScan.setText(JubApplication.getStringRes(R.string.connect));
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
