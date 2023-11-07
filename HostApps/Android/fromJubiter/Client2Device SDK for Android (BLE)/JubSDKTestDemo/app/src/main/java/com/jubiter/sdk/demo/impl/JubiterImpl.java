package com.jubiter.sdk.demo.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.jubiter.sdk.demo.ble.BluetoothKeyApi;
import com.jubiter.sdk.demo.ble.FTBTDevice;
import com.jubiter.sdk.demo.ble.FtBTConnectCallback;
import com.jubiter.sdk.demo.jubnative.NativeApi;
import com.jubiter.sdk.demo.jubnative.utils.JUB_DEVICE_INFO;
import com.jubiter.sdk.demo.utils.JSONParseUtils;
import com.jubiter.sdk.demo.utils.VerifyPinDialog;

import java.util.Arrays;
import java.util.List;


/**
 * Copyright © FEITIAN Technologies Co., Ltd. All Rights Reserved.
 *
 * @Date 2018/4/19  11:30
 * @Author ZJF
 * @Version 1.0
 */
public class JubiterImpl {

    private Context mContext;
    private BluetoothKeyApi mBluetoothKeyApi;
    private boolean isConnected = false;

    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    private long mDeviceHandle;

    private JubiterImpl(Context context) {
        mContext = context;
        mBluetoothKeyApi = new BluetoothKeyApi(mContext);
    }

    private static JubiterImpl fidoApdu;

    public static JubiterImpl getInstance(Context context) {
        if (fidoApdu == null) {
            fidoApdu = new JubiterImpl(context);
        }

        return fidoApdu;
    }

    public void onDestroy() {
        if (isConnected) {
            mBluetoothKeyApi.disConnect(mDeviceHandle);
        }
        fidoApdu = null;
    }

    public void connect(final FtBTConnectCallback connectCallback) {
        mBluetoothKeyApi.connect(new FtBTConnectCallback() {
            @Override
            public void connected(final FTBTDevice device, int code) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (device == null) {
                            return;
                        }
                    }
                }).start();
                isConnected = true;
                mDeviceHandle = device.getHandler();
                connectCallback.connected(device, code);
            }

            @Override
            public void disConnected() {
                isConnected = false;
                connectCallback.disConnected();
            }
        });
    }

    public int disConnectDevice() {
        return mBluetoothKeyApi.disConnect(mDeviceHandle);
    }

    public void getDeviceInfo(final JubCallback<JUB_DEVICE_INFO, Void> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                final int rv = NativeApi.nativeIsConnected(mDeviceHandle);
                if (rv != 0) {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(rv);
                        }
                    });
                    return;
                }

                final JUB_DEVICE_INFO info = new JUB_DEVICE_INFO();
                final int ret = NativeApi.nativeGetDeviceInfo(info, mDeviceHandle);
                if (ret != 0) {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(ret);
                        }
                    });

                } else {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(info, null);
                        }
                    });
                }
            }
        }).start();
    }

    public void sendApdu(final String apdu,
                         final JubCallback<String, Void> callback) {
        Log.d("sendAPDU", apdu);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String result = NativeApi.nativeSendApdu(mDeviceHandle, apdu);
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(result)) {
                            callback.onFailed(-1);
                        } else {
                            callback.onSuccess(result, null);
                        }
                    }
                });
            }
        }).start();
    }

    public void enumApplets(final JubCallback<List<String>, Void> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String lists = NativeApi.nativeEnumApplets(mDeviceHandle);
                    String[] split = lists.split(" ");
                    final List<String> result = Arrays.asList(split);
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(result, null);
                        }
                    });
                } catch (Exception e) {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(-1);
                        }
                    });
                }
            }
        }).start();
    }

    public void getAppletVersion(final String appID,
                                 final JubCallback<String, Void> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String s = NativeApi.nativeGetAppletVersion(mDeviceHandle, appID);
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(s)) {
                            callback.onFailed(-1);
                        } else {
                            callback.onSuccess(s, null);
                        }
                    }
                });

            }
        }).start();
    }

    public void setTimeOut(final int timeOut, final JubCallback<String, Void> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int[] contextIDs = new int[1];
                NativeApi.nativeCreateContextBTC(contextIDs,
                        JSONParseUtils.getBTCTransInfo(mContext),
                        mDeviceHandle);

                long contextID = contextIDs[0];
                final int ret = NativeApi.nativeSetTimeOut(contextID, timeOut);
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (ret == 0) {
                            callback.onSuccess("set timeout success", null);
                        } else {
                            callback.onFailed(ret);
                        }
                    }
                });

            }
        }).start();
    }

    // btc
    public void btcGetAddress(final JubCallback<String, String> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                int[] contextIDs = new int[1];
                NativeApi.nativeCreateContextBTC(contextIDs,
                        JSONParseUtils.getBTCTransInfo(mContext),
                        mDeviceHandle);
                long contextID = contextIDs[0];
                final StringBuilder builder = new StringBuilder();
                String[] array = NativeApi.nativeBTCGetAddress(contextID, JSONParseUtils.getBTCTransInfo(mContext));
                if (array == null || array.length == 0) {
                    final int finalRet = NativeApi.nativeGetErrorCode();
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(finalRet);
                        }
                    });
                    return;
                }
                for (int i = 0; i < array.length; i++) {
                    builder.append(i % 2 == 0 ? "\nxpub :" : "\naddress:").append(array[i]);
                }

                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(builder.toString(), null);
                    }
                });

            }
        }).start();
    }

    public void btcShowAddress(final int change, final int index, final JubCallback<String, Void> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int[] contextIDs = new int[1];
                NativeApi.nativeCreateContextBTC(contextIDs,
                        JSONParseUtils.getBTCTransInfo(mContext),
                        mDeviceHandle);
                final long contextID = contextIDs[0];

                Log.d("btcTrans contextID:", " " + contextID);
                final String ret = NativeApi.nativeBTCShowAddress(contextID, change, index);
                if (TextUtils.isEmpty(ret)) {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(NativeApi.nativeGetErrorCode());
                        }
                    });
                    return;
                }
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(ret, null);
                    }
                });

            }
        }).start();
    }

    public void btcSetMyAddress(final int change, final int index, final JubCallback<String, Void> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int[] contextIDs = new int[1];
                NativeApi.nativeCreateContextBTC(contextIDs,
                        JSONParseUtils.getBTCTransInfo(mContext),
                        mDeviceHandle);
                final long contextID = contextIDs[0];

                Log.d("btcTrans contextID:", " " + contextID);
                int ret = NativeApi.nativeShowVirtualPwd(contextID);
                if (ret != 0) {
                    final int finalRet = ret;
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(finalRet);
                        }
                    });
                    return;
                }
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final VerifyPinDialog dialog = new VerifyPinDialog(mContext, new VerifyPinDialog.callback() {
                            @Override
                            public void onClickListener(String pin) {
                                int ret = 0;
                                if (TextUtils.isEmpty(pin) || pin.length() != 4) {
                                    final int finalRet = ret;
                                    mUIHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onFailed(finalRet);
                                        }
                                    });
                                    return;
                                }

                                ret = NativeApi.nativeVerifyPIN(contextID, pin.getBytes());

                                if (ret != 0) {
                                    final int finalRet = ret;
                                    mUIHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onFailed(finalRet);
                                        }
                                    });
                                    return;
                                }
                                final String result = NativeApi.nativeBTCSetMyAddress(contextID, change, index);
                                if (TextUtils.isEmpty(result)) {
                                    mUIHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onFailed(NativeApi.nativeGetErrorCode());
                                        }
                                    });
                                    return;
                                }
                                mUIHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess(result, null);
                                    }
                                });
                            }
                        });
                        dialog.show();
                    }
                });


            }
        }).start();
    }


    public void btcTrans(final JubCallback<String, Void> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                int[] contextIDs = new int[1];
                NativeApi.nativeCreateContextBTC(contextIDs,
                        JSONParseUtils.getBTCTransInfo(mContext),
                        mDeviceHandle);
                final long contextID = contextIDs[0];

                Log.d("btcTrans contextID:", " " + contextID);
                int ret = NativeApi.nativeShowVirtualPwd(contextID);
                if (ret != 0) {
                    final int finalRet = ret;
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(finalRet);
                        }
                    });
                    return;
                }

                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final VerifyPinDialog dialog = new VerifyPinDialog(mContext, new VerifyPinDialog.callback() {
                            @Override
                            public void onClickListener(String pin) {
                                int ret = 0;
                                if (TextUtils.isEmpty(pin) || pin.length() != 4) {
                                    final int finalRet = ret;
                                    mUIHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onFailed(finalRet);
                                        }
                                    });
                                    return;
                                }

                                ret = NativeApi.nativeVerifyPIN(contextID, pin.getBytes());

                                if (ret != 0) {
                                    final int finalRet = ret;
                                    mUIHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onFailed(finalRet);
                                        }
                                    });
                                    return;
                                }
                                final String rawString = NativeApi.nativeBTCTransaction(contextID, JSONParseUtils.getBTCTransInfo(mContext));
                                if (TextUtils.isEmpty(rawString)) {
                                    mUIHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onFailed(NativeApi.nativeGetErrorCode());
                                        }
                                    });
                                    return;
                                }
                                mUIHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess(rawString, null);
                                    }
                                });
                            }
                        });
                        dialog.show();
                    }
                });

            }
        }).start();
    }
}
