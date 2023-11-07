package com.jubiter.sdk.demo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Copyright © FEITIAN Technologies Co., Ltd. All Rights Reserved.
 *
 * @Date 2019-01-15  10:19
 * @Author ZJF
 * @Version 1.0
 */
public class JSONParseUtils {


    private static String getJsonStr(Context context, String name) {
        AssetManager assets = context.getAssets();
        try {
            InputStream open;
            open = assets.open(name);
            BufferedReader br = new BufferedReader(new InputStreamReader(open));
            String bufferLine;
            StringBuilder sb = new StringBuilder();
            while (!TextUtils.isEmpty(bufferLine = br.readLine())) {
                sb.append(bufferLine.replace(" ", ""));
            }
            br.close();
            open.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getBTCTransInfo(Context context) {
        return getJsonStr(context, "test.json");
    }

    public static String getETHTransInfo(Context context) {
        return getJsonStr(context, "testETH.json");
    }
}
