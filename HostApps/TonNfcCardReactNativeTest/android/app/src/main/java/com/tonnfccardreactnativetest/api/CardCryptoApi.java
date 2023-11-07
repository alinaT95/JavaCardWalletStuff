package com.tonnfccardreactnativetest.api;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.tonnfccardreactnativetest.smartcard.wrappers.ApduRunner;
import com.tonnfccardreactnativetest.smartcard.wrappers.RAPDU;

import static com.tonnfccardreactnativetest.api.utils.ByteArrayHelper.*;
import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.*;
import static com.tonnfccardreactnativetest.smartcard.apdu.TonWalletAppletApduCommands.*;
import static com.tonnfccardreactnativetest.api.utils.ExceptionHelper.*;
import static com.tonnfccardreactnativetest.api.utils.StringHelper.*;

public class CardCryptoApi extends TonWalletApi {
    private static final String TAG = "CardCryptoNfcApi";

    public CardCryptoApi(Context activity, String tag, ApduRunner apduRunner){
        super(activity, tag, apduRunner);
    }

    public RAPDU getPublicKeyForDefaultPath() throws Exception {
        return apduRunner.sendTonWalletAppletAPDU(GET_PUB_KEY_WITH_DEFAULT_PATH_APDU);
    }

    public RAPDU  getPublicKey(byte[] indBytes) throws Exception {
        return apduRunner.sendTonWalletAppletAPDU(getPublicKeyAPDU(indBytes));
    }

    private RAPDU verifyPin(byte[] pinBytes) throws Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        return apduRunner.sendAPDU(getVerifyPinAPDU(pinBytes, sault));
    }

    private RAPDU verifyPinAndSignForDefaultHdPath(byte[] dataForSigning, byte[] pinBytes) throws Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        apduRunner.sendAPDU(getVerifyPinAPDU(pinBytes, sault));
        sault = getSaultBytes();
        return apduRunner.sendAPDU(getSignShortMessageWithDefaultPathAPDU(dataForSigning, sault));
    }

    private RAPDU signForDefaultPath(byte[] dataForSigning) throws Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        return apduRunner.sendAPDU(getSignShortMessageWithDefaultPathAPDU(dataForSigning, sault));
    }

    private RAPDU verifyPinAndSign(byte[] dataForSigning, byte[] ind,  byte[] pinBytes) throws Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        apduRunner.sendAPDU(getVerifyPinAPDU(pinBytes, sault));
        sault = getSaultBytes();
        return apduRunner.sendAPDU(getSignShortMessageAPDU(dataForSigning, ind, sault));
    }

    private RAPDU sign(byte[] dataForSigning, byte[] ind) throws Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        return apduRunner.sendAPDU(getSignShortMessageAPDU(dataForSigning, ind, sault));
    }

    public void verifyPin(String pin, Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (pin.length() != PIN_SIZE)
                        throw new Exception("Incorrect pin: pin is a string of length 4.");
                    if (!isNumericString(pin))
                        throw new Exception("Pin is not in numeric string.");
                    verifyPin(bytes(pinToHex(pin)));
                    promise.resolve("done");
                    Log.d(TAG, "verifyPin status : done");
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getPublicKeyForDefaultPath(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response = hex(getPublicKeyForDefaultPath().getData());
                    promise.resolve(response);
                    Log.d(TAG, "getPublicKeyForDefaultPath response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getPublicKey(String index, Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response = hex(getPublicKey(bytes(asciiToHex(index))).getData());
                    promise.resolve(response);
                    Log.d(TAG, "getPublicKey response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void signForDefaultHdPath(String dataForSigning, Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (!isHexString(dataForSigning)) throw new Exception("Data is not in hex.");
                    String response = hex(signForDefaultPath(bytes(dataForSigning)).getData());
                    promise.resolve(response);
                    Log.d(TAG, "signForDefaultHdPath response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void sign(String dataForSigning, String index, Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (!isHexString(dataForSigning)) throw new Exception("Data is not in hex.");
                    String response = hex(sign(bytes(dataForSigning), bytes(asciiToHex(index))).getData());
                    promise.resolve(response);
                    Log.d(TAG, "sign response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void verifyPinAndSignForDefaultHdPath(String dataForSigning, String pin, Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (pin.length() != PIN_SIZE)
                        throw new Exception("Incorrect pin: pin is a string of length 4.");
                    if (!isNumericString(pin))
                        throw new Exception("Pin is not in numeric string.");
                    if (!isHexString(dataForSigning)) throw new Exception("Data is not in hex.");
                    String response = hex(verifyPinAndSignForDefaultHdPath(bytes(dataForSigning), bytes(pinToHex(pin))).getData());
                    promise.resolve(response);
                    Log.d(TAG, "signForDefaultHdPath response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void verifyPinAndSign(String dataForSigning, String index, String pin, Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (pin.length() != PIN_SIZE)
                        throw new Exception("Incorrect pin: pin is a string of length 4.");
                    if (!isNumericString(pin))
                        throw new Exception("Pin is not in numeric string.");
                    if (!isHexString(dataForSigning)) throw new Exception("Data is not in hex.");
                    String response = hex(verifyPinAndSign(bytes(dataForSigning), bytes(asciiToHex(index)), bytes(pinToHex(pin))).getData());
                    promise.resolve(response);
                    Log.d(TAG, "sign response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }


}
