package com.tonnfccardreactnativetest.smartcard.cryptoUtils;

import android.util.Log;

import java.security.KeyStore;
import java.security.Signature;
import java.util.Enumeration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.HMAC_SHA_SIG_SIZE;

public class HmacHelper {
    public static final String HMAC_KEY_ALIAS = "hmac_key_alias";

    private byte[] key = new byte[HMAC_SHA_SIG_SIZE];

    private static HmacHelper hmacHelper;

    public static HmacHelper getInstance() {
        if (hmacHelper == null) hmacHelper = new HmacHelper();
        return hmacHelper;
    }

    private HmacHelper(){}

    public void setKey(byte[] key) {
        this.key = key;
    }

    public static byte[] computeMac(byte[] key, byte[] data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
        sha256_HMAC.init(secretKey);
        return sha256_HMAC.doFinal(data);
    }

    public byte[] computeMac(byte[] data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);

        KeyStore.Entry entry = ks.getEntry(HMAC_KEY_ALIAS, null);
        if (!(entry instanceof KeyStore.SecretKeyEntry)) {
            Log.w("TAG", "Not an instance of a SecretKeyEntry");
            return null;
        }
        sha256_HMAC.init(((KeyStore.SecretKeyEntry) entry).getSecretKey());
        return sha256_HMAC.doFinal(data);
    }

    /*
    public byte[] computeMac(byte[] data) throws  Exception{
       // getKey();
      //  System.out.println("key = " + ByteArrayHelper.hex(key));
      Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
        sha256_HMAC.init(secretKey);
      //  System.out.println("data for hmac = " + ByteArrayHelper.hex(data));
        return sha256_HMAC.doFinal(data);
     */
}
