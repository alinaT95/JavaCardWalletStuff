package com.jubiter.sdk.demo.ble;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.jubiter.sdk.demo.JubApplication;
import com.jubiter.sdk.demo.R;
import com.jubiter.sdk.demo.jubnative.InnerDiscCallback;
import com.jubiter.sdk.demo.jubnative.NativeApi;

/**
 * Copyright © FEITIAN Technologies Co., Ltd. All Rights Reserved.
 * @Date 2018/4/19  11:40
 * @Author ZJF
 * @Version 1.0
 */
public class BluetoothKeyApi {

    private FtBTConnectCallback mConnectCallback;
    private ProgressDialog mDialog;
    private Handler mHandler;
    private Context mContext;

    private InnerDiscCallback callback = new InnerDiscCallback() {
        @Override
        public void onDisconnect(String name) {
            if (mConnectCallback != null) {
                mConnectCallback.disConnected();
            }
        }
    };

    private SelectDeviceDialog.OnSelectedListener selectedListener = new SelectDeviceDialog.OnSelectedListener() {
        @Override
        public void onSelected(final FTBTDevice device) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dismissMsgDialog();
                    if (device == null) {
                        return;
                    }
                    long[] hld = new long[1];
                    final int ret = NativeApi.nativeConnectDevice(device.getMac(), 1, hld, 30 * 1000, callback);
                    if (ret != 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mConnectCallback.connected(null, ret);
                            }
                        });
                        Log.e("selectedListener", String.format(JubApplication.getStringRes(R.string.error_code), ret));
                    } else {
                        Log.e("selectedListener", "connectde handler: " + hld[0]);
                        device.setHandler(hld[0]);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mConnectCallback.connected(device, 0);
                            }
                        });
                    }
                }
            }).start();
        }
    };


    public BluetoothKeyApi(Context context) {
        mContext = context;
        mHandler = new Handler(context.getMainLooper());
        int ret = NativeApi.nativeInitDevice();
        Log.d("JUB_nativeInitDevice", ret + "");
    }

    public void connect(FtBTConnectCallback connectCallback) {
        showMsgDialog(JubApplication.getStringRes(R.string.connecting));
        mConnectCallback = connectCallback;

        SelectDeviceDialog dialog = new SelectDeviceDialog(mContext, selectedListener);
        dialog.show();
    }

    public int disConnect(long handler) {
        return NativeApi.nativeDisconnect(handler);
    }

    private void showMsgDialog(String msg) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
        }
        mDialog.setMessage(msg);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void dismissMsgDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
