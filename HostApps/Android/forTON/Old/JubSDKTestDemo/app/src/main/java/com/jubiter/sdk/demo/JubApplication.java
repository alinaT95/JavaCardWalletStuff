package com.jubiter.sdk.demo;

import android.app.Application;

/**
 * Copyright © FEITIAN Technologies Co., Ltd. All Rights Reserved.
 *
 * @Date 2019-05-22  16:56
 * @Author ZJF
 * @Version 1.0
 */
public class JubApplication extends Application {
    public static JubApplication instance;
    public static String getStringRes(int id) {
        return instance.getString(id);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
