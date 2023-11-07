package com.apdunfcmanagertest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.Log;
import android.provider.Settings;
import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import android.app.PendingIntent;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.TagTechnology;
import android.nfc.tech.IsoDep;
import android.os.Parcelable;
import android.os.Bundle;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.*;

public class NfcManager extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {
    private static final String LOG_TAG = "ReactNativeNfcManager";
    private final List<IntentFilter> intentFilters = new ArrayList<IntentFilter>();
    private final ArrayList<String[]> techLists = new ArrayList<String[]>();
    private Context context;
    private ReactApplicationContext reactContext;
    private Boolean isForegroundEnabled = false;
    private Boolean isResumed = false;

    private TagTechnologyRequest techRequest = null;
    private Tag tag = null;
    // Use NFC reader mode instead of listening to a dispatch
    private Boolean isReaderModeEnabled = false;
    private int readerModeFlags = 0;

    public NfcManager(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
        this.reactContext = reactContext;
        reactContext.addActivityEventListener(this);
        reactContext.addLifecycleEventListener(this);
        Log.d(LOG_TAG, "NfcManager created");
    }

    @Override
    public String getName() {
        return "NfcManager";
    }

    private boolean hasPendingRequest() {
        return techRequest != null;
    }

    @ReactMethod
    public void cancelTechnologyRequest(Callback callback) {
        synchronized(this) {
            if (techRequest != null) {
                techRequest.close();
                try {
                    techRequest.getPendingCallback().invoke("cancelled");
                } catch (RuntimeException ex) {
                    // the pending callback might already been invoked when there is an ongoing
                    // connected tag, bypass this case explicitly
                }
                techRequest = null;
                callback.invoke();
            } else {
                // explicitly allow this
                callback.invoke();
            }
        }
    }

    @ReactMethod
    public void requestTechnology(/*ReadableArray techs,*/ Callback callback) {
        synchronized(this) {
            if (!isForegroundEnabled) {
                callback.invoke("you should requestTagEvent first");
                return;
            }

            if (hasPendingRequest()) {
                callback.invoke("You can only issue one request at a time");
            } else {
                techRequest = new TagTechnologyRequest(/*techs.toArrayList(),*/ callback);
            }
        }
    }

    @ReactMethod
    public void closeTechnology(Callback callback) {
        synchronized(this) {
            if (techRequest != null) {
                techRequest.close();
                techRequest = null;
                callback.invoke();
            } else {
                // explicitly allow this
                callback.invoke();
            }
        }
    }

    @ReactMethod
    public void getTag(Callback callback) {
        synchronized(this) {
            if (techRequest != null) {
                try {
                    Tag tag = techRequest.getTagHandle();
                    WritableMap parsed = tag2React(tag);
                    callback.invoke(null, parsed);
                } catch (Exception ex) {
                    Log.d(LOG_TAG, "getTag fail");
                    callback.invoke("getTag fail");
                }
            } else {
                callback.invoke("no tech request available");
            }
        }
    }

    @ReactMethod
    public void setTimeout(int timeout, Callback callback) {
        synchronized (this) {
            if (techRequest != null) {
                try {
                   // String tech = techRequest.getTechType();
                    TagTechnology baseTechHandle = techRequest.getTechHandle();
                    // TagTechnology is the base class for each tech (ex, NfcA, NfcB, IsoDep ...)
                    // but it doesn't provide transceive in its interface, so we need to explicitly cast it
                    IsoDep techHandle = (IsoDep) baseTechHandle;
                    techHandle.setTimeout(timeout);
                    callback.invoke();
                    Log.d(LOG_TAG, "setTimeout not supported");
                } catch (Exception ex) {
                    Log.d(LOG_TAG, "setTimeout fail");
                }
                callback.invoke("setTimeout fail");
            } else {
                callback.invoke("no tech request available");
            }
        }
    }

    @ReactMethod
    public void connect(ReadableArray techs, Callback callback){
        synchronized(this) {
          try {
            techRequest = new TagTechnologyRequest(callback);
            techRequest.connect(this.tag);
            callback.invoke(null, null);
            return;
          } catch (Exception ex) {
              callback.invoke(ex.toString());
          }
        }
    }

    @ReactMethod
    public void close(Callback callback){
        synchronized(this) {
          try {
            techRequest.close();
            callback.invoke(null, null);
            return;
          } catch (Exception ex) {
            callback.invoke(ex.toString());
          }
        }
    }

    @ReactMethod
    public void transceive(ReadableArray rnArray, Callback callback) {
        synchronized(this) {
            if (techRequest != null) {
                try {
                    String tech = techRequest.getTechType();
                    byte[] bytes = rnArrayToBytes(rnArray);

                    TagTechnology baseTechHandle = techRequest.getTechHandle();
                    // TagTechnology is the base class for each tech (ex, NfcA, NfcB, IsoDep ...)
                    // but it doesn't provide transceive in its interface, so we need to explicitly cast it
                    IsoDep techHandle = (IsoDep)baseTechHandle;
                    byte[] resultBytes = techHandle.transceive(bytes);
                    WritableArray resultRnArray = bytesToRnArray(resultBytes);
                    callback.invoke(null, resultRnArray);
                    return;
                   // Log.d(LOG_TAG, "transceive not supported");
                } catch (Exception ex) {
                    Log.d(LOG_TAG, "transceive fail");
                }

                callback.invoke("transceive fail");
            } else {
                callback.invoke("no tech request available");
            }
        }
    }

    @ReactMethod
    public void getMaxTransceiveLength(Callback callback) {
        synchronized(this) {
            if (techRequest != null) {
                try {
                    String tech = techRequest.getTechType();

                    TagTechnology baseTechHandle = techRequest.getTechHandle();
                    // TagTechnology is the base class for each tech (ex, NfcA, NfcB, IsoDep ...)
                    // but it doesn't provide transceive in its interface, so we need to explicitly cast it
                    IsoDep techHandle = (IsoDep)baseTechHandle;
                    int max = techHandle.getMaxTransceiveLength();
                    callback.invoke(null, max);
                    return;
                } catch (Exception ex) {
                    Log.d(LOG_TAG, "getMaxTransceiveLength fail");
                }
                callback.invoke("getMaxTransceiveLength fail");
            } else {
                callback.invoke("no tech request available");
            }
        }
    }


    @ReactMethod
    public void start(Callback callback) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter != null) {
            Log.d(LOG_TAG, "start");

            IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
            Activity currentActivity = getCurrentActivity();
            if (currentActivity == null) {
                callback.invoke("fail to get current activity");
                return;
            }

            currentActivity.registerReceiver(mReceiver, filter);
            callback.invoke();
        } else {
            Log.d(LOG_TAG, "not support in this device");
            callback.invoke("no nfc support");
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive " + intent);
            final String action = intent.getAction();

            if (action.equals(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {
                final int state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE,
                        NfcAdapter.STATE_OFF);
                String stateStr = "unknown";
                switch (state) {
                    case NfcAdapter.STATE_OFF:
                        stateStr = "off";
                        break;
                    case NfcAdapter.STATE_TURNING_OFF:
                        stateStr = "turning_off";
                        break;
                    case NfcAdapter.STATE_ON:
                        stateStr = "on";
                        break;
                    case NfcAdapter.STATE_TURNING_ON:
                        stateStr = "turning_on";
                        break;
                }

                try {
                    WritableMap writableMap = Arguments.createMap();
                    writableMap.putString("state", stateStr);
                    sendEvent("NfcManagerStateChanged", writableMap);
                } catch (Exception ex) {
                    Log.d(LOG_TAG, "send nfc state change event fail: " + ex);
                }
            }
        }
    };

    @ReactMethod
    public void isEnabled(Callback callback) {
        Log.d(LOG_TAG, "isEnabled");
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter != null) {
            callback.invoke(null, nfcAdapter.isEnabled());
        } else {
            callback.invoke(null, false);
        }
    }

    @ReactMethod
    public void goToNfcSetting(Callback callback) {
        Log.d(LOG_TAG, "goToNfcSetting");
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            callback.invoke("fail to get current activity");
            return;
        }

        currentActivity.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        callback.invoke();
    }

    @ReactMethod
    public void getLaunchTagEvent(Callback callback) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            callback.invoke("fail to get current activity");
            return;
        }

        Intent launchIntent = currentActivity.getIntent();
        WritableMap nfcTag = parseNfcIntent(launchIntent);
        callback.invoke(null, nfcTag);
    }

    @ReactMethod
    private void registerTagEvent(ReadableMap options, Callback callback) {
        this.isReaderModeEnabled = options.getBoolean("isReaderModeEnabled");
        this.readerModeFlags = options.getInt("readerModeFlags");

        Log.d(LOG_TAG, "registerTag");
        isForegroundEnabled = true;

        // for those without NDEF, get them as tags
        intentFilters.add(new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED));

        if (isResumed) {
            enableDisableForegroundDispatch(true);
        }
        callback.invoke();
    }

    @ReactMethod
    private void unregisterTagEvent(Callback callback) {
        Log.d(LOG_TAG, "registerTag");
        isForegroundEnabled = false;
        intentFilters.clear();
        if (isResumed) {
            enableDisableForegroundDispatch(false);
        }
        callback.invoke();
    }

    @ReactMethod
    private void hasTagEventRegistration(Callback callback) {
        Log.d(LOG_TAG, "isSessionAvailable");
        callback.invoke(null, isForegroundEnabled);
    }

    @Override
    public void onHostResume() {
        Log.d(LOG_TAG, "onResume");
        isResumed = true;
        if (isForegroundEnabled) {
            enableDisableForegroundDispatch(true);
        }
    }

    @Override
    public void onHostPause() {
        Log.d(LOG_TAG, "onPause");
        isResumed = false;
        enableDisableForegroundDispatch(false);
    }

    @Override
    public void onHostDestroy() {
        Log.d(LOG_TAG, "onDestroy");
    }

    private void enableDisableForegroundDispatch(boolean enable) {
        Log.i(LOG_TAG, "enableForegroundDispatch, enable = " + enable);
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        Activity currentActivity = getCurrentActivity();
        final NfcManager manager = this;
        if (nfcAdapter != null && currentActivity != null && !currentActivity.isFinishing()) {
            try {
                if (isReaderModeEnabled) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                        throw new RuntimeException("minSdkVersion must be Honeycomb (19) or later.");
                    }

                    if (enable) {
                        Log.i(LOG_TAG, "enableReaderMode");
                        Bundle readerModeExtras = new Bundle();
                        readerModeExtras.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 10000);
                        nfcAdapter.enableReaderMode(currentActivity, new NfcAdapter.ReaderCallback() {
                            @Override
                            public void onTagDiscovered(Tag tag) {
                                manager.tag = tag;
                                Log.d(LOG_TAG, "readerMode onTagDiscovered");
                                WritableMap nfcTag = null;
                                // if the tag contains NDEF, we want to report the content

                                nfcTag = tag2React(tag);


                                if (nfcTag != null) {
                                    sendEvent("NfcManagerDiscoverTag", nfcTag);
                                }
                            }
                        }, readerModeFlags, readerModeExtras);
                    } else {
                        Log.i(LOG_TAG, "disableReaderMode");
                        nfcAdapter.disableReaderMode(currentActivity);
                    }
                } else {
                    if (enable) {
                        nfcAdapter.enableForegroundDispatch(currentActivity, getPendingIntent(), getIntentFilters(), getTechLists());
                    } else {
                        nfcAdapter.disableForegroundDispatch(currentActivity);
                    }
                }
            } catch (IllegalStateException | NullPointerException e) {
                Log.w(LOG_TAG, "Illegal State Exception starting NFC. Assuming application is terminating.");
            }
        }
    }

    private PendingIntent getPendingIntent() {
        Activity activity = getCurrentActivity();
        Intent intent = new Intent(activity, activity.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(activity, 0, intent, 0);
    }

    private IntentFilter[] getIntentFilters() {
        return intentFilters.toArray(new IntentFilter[intentFilters.size()]);
    }

    private String[][] getTechLists() {
        return techLists.toArray(new String[0][0]);
    }

    private void sendEvent(String eventName,
                           @Nullable WritableMap params) {
        getReactApplicationContext()
                .getJSModule(RCTNativeAppEventEmitter.class)
                .emit(eventName, params);
    }

    private void sendEventWithJson(String eventName,
                                   JSONObject json) {
        try {
            WritableMap map = JsonConvert.jsonToReact(json);
            sendEvent(eventName, map);
        } catch (JSONException ex) {
            Log.d(LOG_TAG, "fireNdefEvent fail: " + ex);
        }
    }


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "onActivityResult");
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d(LOG_TAG, "onNewIntent " + intent);
        WritableMap nfcTag = parseNfcIntent(intent);
        if (nfcTag != null) {
            sendEvent("NfcManagerDiscoverTag", nfcTag);
        }
    }

    private WritableMap parseNfcIntent(Intent intent) {
        Log.d(LOG_TAG, "parseIntent " + intent);
        String action = intent.getAction();
        Log.d(LOG_TAG, "action " + action);
        if (action == null) {
            return null;
        }

        WritableMap parsed = null;
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        // Parcelable[] messages = intent.getParcelableArrayExtra((NfcAdapter.EXTRA_NDEF_MESSAGES));

        synchronized(this) {
            if (techRequest != null) {
                if (!techRequest.isConnected()) {
                    boolean result = techRequest.connect(tag);
                    if (result) {
                        techRequest.getPendingCallback().invoke(null, techRequest.getTechType());
                    } else {
                        techRequest.getPendingCallback().invoke("fail to connect tag");
                        techRequest = null;
                    }
                }

                // explicitly return null, to avoid extra detection
                return null;
            }
        }

        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            parsed = tag2React(tag);
        }

        return parsed;
    }

    private WritableMap tag2React(Tag tag) {
        try {
            JSONObject json = Util.tagToJSON(tag);
            return JsonConvert.jsonToReact(json);
        } catch (JSONException ex) {
            return null;
        }
    }

    private static byte[] rnArrayToBytes(ReadableArray rArray) {
        byte[] bytes = new byte[rArray.size()];
        for (int i = 0; i < rArray.size(); i++) {
            bytes[i] = (byte)(rArray.getInt(i) & 0xff);
        }
        return bytes;
    }

    private static WritableArray bytesToRnArray(byte[] bytes) {
        return appendBytesToRnArray(Arguments.createArray(), bytes);
    }

    private static WritableArray appendBytesToRnArray(WritableArray value, byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            value.pushInt((bytes[i] & 0xFF));
        }
        return value;
    }
}