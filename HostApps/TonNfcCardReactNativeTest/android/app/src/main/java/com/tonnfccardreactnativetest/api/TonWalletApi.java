package com.tonnfccardreactnativetest.api;

import android.content.Context;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.tonnfccardreactnativetest.api.utils.ByteArrayHelper;
import com.tonnfccardreactnativetest.smartcard.cryptoUtils.HmacHelper;
import com.tonnfccardreactnativetest.smartcard.wrappers.ApduRunner;
import com.tonnfccardreactnativetest.smartcard.wrappers.RAPDU;

import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.Enumeration;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.COMMON_SECRET_SIZE;
import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.PASSWORD_SIZE;
import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.PIN_SIZE;
import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.getStateName;
import static com.tonnfccardreactnativetest.smartcard.apdu.TonWalletAppletApduCommands.*;
import static com.tonnfccardreactnativetest.api.utils.ByteArrayHelper.*;
import static com.tonnfccardreactnativetest.api.utils.ExceptionHelper.*;
import static com.tonnfccardreactnativetest.api.utils.StringHelper.*;

public class TonWalletApi {
    private static final String TAG = "TonNfcApi";

    public static MessageDigest digest;

    public final static HmacHelper HMAC_HELPER = HmacHelper.getInstance();

    protected ApduRunner apduRunner = null;

    protected String pin = "5555";

    private Context activity;

    protected static byte[] password;
    protected static byte[] commonSecret;
    protected static byte[] initialVector;

    static {
        try{
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TonWalletApi(Context activity, String tag, ApduRunner apduRunner){
        this.activity = activity;
       // this.tag = tag;
        this.apduRunner = apduRunner;
    }

    public Context getActivity() {
        return activity;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public static void setPassword(byte[] password) {
        TonWalletApi.password = password;
    }

    public static void setCommonSecret(byte[] commonSecret) {
        TonWalletApi.commonSecret = commonSecret;
    }

    public static void setInitialVector(byte[] initialVector) {
        TonWalletApi.initialVector = initialVector;
    }

    public static void setKeyForHmac() throws Exception {
        byte[] key = HmacHelper.computeMac(digest.digest(password), commonSecret);
        final SecretKey hmacSha256Key = new SecretKeySpec(key, 0, key.length, KeyProperties.KEY_ALGORITHM_HMAC_SHA256);
        final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        keyStore.setEntry(
                HmacHelper.HMAC_KEY_ALIAS,
                new KeyStore.SecretKeyEntry(hmacSha256Key),
                new KeyProtection.Builder(KeyProperties.PURPOSE_SIGN)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build());
    }

    public static void setKeyForHmac(byte[] password, byte[] commonSecret) throws Exception {
        byte[] key = HmacHelper.computeMac(digest.digest(password), commonSecret);
        final SecretKey hmacSha256Key = new SecretKeySpec(key, 0, key.length, KeyProperties.KEY_ALGORITHM_HMAC_SHA256);
        final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        keyStore.setEntry(HmacHelper.HMAC_KEY_ALIAS,
                new KeyStore.SecretKeyEntry(hmacSha256Key),
                new KeyProtection.Builder(KeyProperties.PURPOSE_SIGN)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build());
    }

    public void setKeyForHmac(String password, String commonSecret, Promise promise) {
        try {
            if (password.length() != 2 * PASSWORD_SIZE)
                throw new Exception("Incorrect password: password is hex string of length 256. " + password.length());
            if (commonSecret.length() != 2 * COMMON_SECRET_SIZE)
                throw new Exception("Incorrect common secret: common secret is hex string of length 64.");
            if (!isHexString(password)) throw new Exception("Password is not in hex.");
            if (!isHexString(commonSecret)) throw new Exception("Common secret is not in hex.");
            setKeyForHmac(bytes(password), bytes(commonSecret));
            promise.resolve(/*hex(getKey())*/"HmacSha256 Key is generated");
        }
        catch (Exception e) {
            handleException(e, promise, TAG);
        }
    }

    public void setPin(String pin, Promise promise) {
        try {
            if (pin.length() != PIN_SIZE)
                throw new Exception("Incorrect pin: pin is a string of length 4.");
            if (!isNumericString(pin)) throw new Exception("Pin is not in numeric string.");
            setPin(pin);
        } catch (Exception e) {
            handleException(e, promise, TAG);;
        }
    }


    public void selectTonWalletAppletAndGetTonAppletState(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String appletState = getStateName(selectTonWalletAppletAndGetTonAppletState().getData()[0]);
                    promise.resolve(appletState);
                    Log.d(TAG, "appletState : " + appletState);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getSault(Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response = hex(getSault().getData());
                    promise.resolve(response);
                    Log.d(TAG, "getSault response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }


    public void disconnectCard(final Promise promise)  {
        new Thread(new Runnable() {
            public void run() {
                try {
                    apduRunner.disconnectCard();
                    promise.resolve("done");
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public RAPDU selectTonWalletAppletAndGetTonAppletState() throws Exception {
        return apduRunner.sendTonWalletAppletAPDU(GET_APP_INFO_APDU);
    }

    public RAPDU getSault() throws Exception {
        return apduRunner.sendAPDU(GET_SAULT_APDU);
    }

    protected byte[] getSaultBytes() throws Exception {
        return getSault().getData();
        //TODO: check sault length?
    }

    public RAPDU selectTonWalletAppletAndGetSault() throws Exception {
        return apduRunner.sendTonWalletAppletAPDU(GET_SAULT_APDU);
    }

    protected byte[] selectTonWalletAppletAndGetSaultBytes() throws Exception {
        return selectTonWalletAppletAndGetSault().getData();
        //TODO: check sault length?
    }


}
