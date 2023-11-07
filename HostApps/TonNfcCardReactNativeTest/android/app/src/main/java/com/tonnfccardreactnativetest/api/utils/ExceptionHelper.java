package com.tonnfccardreactnativetest.api.utils;

import android.util.Log;

import com.facebook.react.bridge.Promise;

public class ExceptionHelper {
    public static void handleException(Exception e, Promise promise, String tag) {
        promise.reject(e.getMessage());
        Log.e(tag, e.getMessage());
    }
}
