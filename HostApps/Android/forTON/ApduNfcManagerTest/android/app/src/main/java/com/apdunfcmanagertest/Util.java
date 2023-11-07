package com.apdunfcmanagertest;

import android.nfc.Tag;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Util {
    static final String TAG = "NfcPlugin";
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    static JSONObject tagToJSON(Tag tag) {
        JSONObject json = new JSONObject();

        if (tag != null) {
            try {
                json.put("id", bytesToHex(tag.getId()));
                json.put("techTypes", new JSONArray(Arrays.asList(tag.getTechList())));
            } catch (JSONException e) {
                Log.e(TAG, "Failed to convert tag into json: " + tag.toString(), e);
            }
        }
        return json;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;

            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }
}



