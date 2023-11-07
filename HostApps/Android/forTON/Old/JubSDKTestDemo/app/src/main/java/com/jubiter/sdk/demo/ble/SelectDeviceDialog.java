package com.jubiter.sdk.demo.ble;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jubiter.sdk.demo.JubApplication;
import com.jubiter.sdk.demo.R;
import com.jubiter.sdk.demo.jubnative.InnerScanCallback;
import com.jubiter.sdk.demo.jubnative.NativeApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright © FEITIAN Technologies Co., Ltd. All Rights Reserved.
 *
 * @Date 2018/1/8  18:50
 * @Author ZJF
 * @Version 1.0
 */
public class SelectDeviceDialog extends Dialog implements View.OnClickListener, OnItemClickListener, OnShowListener, OnCancelListener {

    public static final int TYPE_BLUETOOTH_3 = 1;
    public static final int TYPE_BLUETOOTH_4 = 2;

    // Debugging
    protected static final String TAG = SelectDeviceDialog.class.getSimpleName();
    protected static final boolean D = true;

    private TextView tvTitle;
    private ProgressBar progressBar;
    private ListView mListView;
    private Button btnClose;
    private Button btnRrfresh;

    private OnSelectedListener mOnSelectedListener;

    private static BtDeviceAdapter mDeviceListAdapter;
    private List<FTBTDevice> mDeviceNameList;

    public SelectDeviceDialog(Context context,
                              OnSelectedListener selectedListener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        intiUI(context);


        mDeviceNameList = new ArrayList<>();
        this.mOnSelectedListener = selectedListener;

        mDeviceListAdapter = new BtDeviceAdapter(context, mDeviceNameList);
        mListView.setAdapter(mDeviceListAdapter);
        mListView.setOnItemClickListener(this);
        setCancelable(false);
        this.setOnShowListener(this);
        this.setOnCancelListener(this);
    }

    private void intiUI(Context context) {

        ViewGroup.LayoutParams lp1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams lp2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams lp5 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        lp5.setMargins(10, 10, 10, 10);
        LinearLayout.LayoutParams lp6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);


        // ========================= top layout =========================

        LinearLayout titleLayout = new LinearLayout(context);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setLayoutParams(lp1);

        tvTitle = new TextView(context);
        tvTitle.setText(JubApplication.getStringRes(R.string.select_device));
        tvTitle.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
        tvTitle.setTextSize(22);

        progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(lp2);
        progressBar.setVisibility(View.GONE);

        titleLayout.addView(tvTitle);
        titleLayout.addView(progressBar);


        // ========================= bottom layout =========================

        LinearLayout buttonsLayout = new LinearLayout(context);
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonsLayout.setLayoutParams(lp1);
        btnClose = new Button(context);
        btnRrfresh = new Button(context);


        btnClose.setLayoutParams(lp5);
        btnRrfresh.setLayoutParams(lp5);

        btnClose.setText(JubApplication.getStringRes(R.string.close));
        btnClose.setTextColor(Color.WHITE);
        btnClose.setBackgroundResource(R.drawable.bg_btn);

        btnRrfresh.setText(JubApplication.getStringRes(R.string.refresh));
        btnRrfresh.setTextColor(Color.WHITE);
        btnRrfresh.setBackgroundResource(R.drawable.bg_btn);

        btnClose.setOnClickListener(this);
        btnRrfresh.setOnClickListener(this);

        buttonsLayout.addView(btnClose);
        buttonsLayout.addView(btnRrfresh);

        // =====================  ListView layout =================

        mListView = new ListView(context);
        mListView.setLayoutParams(lp6);
        mListView.setHeaderDividersEnabled(true);
        mListView.setFooterDividersEnabled(true);

        // ===================== 根布局 ==============================

        LinearLayout rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundResource(R.drawable.setbar_bg);
        rootLayout.addView(titleLayout);
        rootLayout.addView(mListView);
        rootLayout.addView(buttonsLayout);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(rootLayout);
    }

    public interface OnSelectedListener {
        void onSelected(FTBTDevice device);
    }


    public InnerScanCallback callback = new InnerScanCallback() {
        @Override
        public void onScanResult(String name, String uuid, int devType) {
            Log.d("JUB", "onScanResult" + name);
            FTBTDevice myDevice = new FTBTDevice(name, uuid);
            for (int i = 0; i < mDeviceListAdapter.getCount(); i++) {
                if (mDeviceListAdapter.getItem(i).getMac().equals(myDevice.getMac())) {
                    mDeviceListAdapter.getItem(i).setName(myDevice.getName());
                    mDeviceListAdapter.notifyDataSetChanged();
                    return;
                }
            }

            if (myDevice.getName() == null || "".equals(myDevice.getName())) {
                return;
            }

            mDeviceListAdapter.addItem(myDevice);
            mDeviceListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onStop() {

        }

        @Override
        public void onError(int errorCode) {

        }
    };


    @Override
    public void onClick(View v) {
        if (v == btnClose) {
            this.cancel();
            mOnSelectedListener.onSelected(null);
        } else if (v == btnRrfresh) {
            NativeApi.nativeStopScan();
            mDeviceNameList.clear();
            mDeviceListAdapter.notifyDataSetChanged();
            int ret = NativeApi.nativeStartScan(callback);
            Log.d("JUB", "btnRrfresh  nativeStartScan " + ret);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SelectDeviceDialog.this.cancel();
        NativeApi.nativeStopScan();
        mOnSelectedListener.onSelected(mDeviceListAdapter.getItem(position));
    }

    @Override
    public void onShow(DialogInterface dialog) {
        int ret = NativeApi.nativeStartScan(callback);
        Log.d("JUB", "onShow  nativeStartScan " + ret);
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        NativeApi.nativeStopScan();
    }
}
