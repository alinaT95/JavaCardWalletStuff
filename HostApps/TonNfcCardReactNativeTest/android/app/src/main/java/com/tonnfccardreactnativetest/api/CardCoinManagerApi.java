package com.tonnfccardreactnativetest.api;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.tonnfccardreactnativetest.smartcard.wrappers.ApduRunner;
import com.tonnfccardreactnativetest.smartcard.wrappers.RAPDU;

import static com.tonnfccardreactnativetest.api.utils.ByteArrayHelper.*;
import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.*;
import static com.tonnfccardreactnativetest.smartcard.apdu.CoinManagerApduCommands.*;
import static com.tonnfccardreactnativetest.api.utils.ExceptionHelper.*;
import static com.tonnfccardreactnativetest.api.utils.StringHelper.*;

public class CardCoinManagerApi extends TonWalletApi {
    private static final String TAG = "CardCoinManagerNfcApi";

    public CardCoinManagerApi(Context activity, String tag, ApduRunner apduRunner){
        super(activity, tag, apduRunner);
    }

    public void setDeviceLabel(String deviceLabel, Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (deviceLabel.length() != 2*LABEL_LENGTH)
                        throw new Exception("Incorrect deviceLabel: deviceLabel is a hex string of length 64.");
                    apduRunner.sendCoinManagerAppletAPDU(getSetDeviceLabelAPDU(bytes(deviceLabel)));
                    promise.resolve("done");
                    Log.d(TAG, "setDeviceLabel response : done");
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getDeviceLabel(Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response  = hex(apduRunner.sendCoinManagerAppletAPDU(GET_DEVICE_LABEL).getData());
                    promise.resolve(response);
                    Log.d(TAG, "getDeviceLabel response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getSeVersion(Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response  = hex(apduRunner.sendCoinManagerAppletAPDU(GET_SE_VERSION).getData());
                    promise.resolve(response);
                    Log.d(TAG, "getSeVersion response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getCsn(Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response  = hex(apduRunner.sendCoinManagerAppletAPDU(GET_CSN).getData());
                    promise.resolve(response);
                    Log.d(TAG, "getCsn (CSN=SecureElement id) response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getMaxPinTries(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response = Byte.toString(apduRunner.sendCoinManagerAppletAPDU(GET_PIN_TLT_APDU).getData()[0]);
                    promise.resolve(response);
                    Log.d(TAG, "getMaxPinTries response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getRemainingPinTries(Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response = Byte.toString(apduRunner.sendCoinManagerAppletAPDU(GET_PIN_RTL_APDU).getData()[0]);
                    promise.resolve(response);
                    Log.d(TAG, "getRemainingPinTries response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getRootKeyStatus(Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    RAPDU response = apduRunner.sendCoinManagerAppletAPDU(GET_ROOT_KEY_STATUS_APDU);
                    String seedStatus = hex(response.getData()).equals(POSITIVE_ROOT_KEY_STATUS) ? "generated" : "not generated";
                    promise.resolve(seedStatus);
                    Log.d(TAG, "getRootKeyStatus status : " + seedStatus);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void resetWallet(Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    apduRunner.sendCoinManagerAppletAPDU(RESET_WALLET_APDU);
                    promise.resolve("done");
                    Log.d(TAG, "resetWallet status: done");
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getAvailableMemory(Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response  = hex(apduRunner.sendCoinManagerAppletAPDU(GET_AVAILABLE_MEMORY_APDU).getData());
                    promise.resolve(response);
                    Log.d(TAG, "getAvailableMemory response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getAppsList(Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response  = hex(apduRunner.sendCoinManagerAppletAPDU(GET_APPLET_LIST_APDU).getData());
                    promise.resolve(response);
                    Log.d(TAG, "getAppsList response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void generateSeed(String pin, Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (pin.length() != PIN_SIZE)
                        throw new Exception("Incorrect pin: pin is a string of length 4.");
                    if (!isNumericString(pin)) throw new Exception("Pin is not in numeric string.");
                    apduRunner.sendCoinManagerAppletAPDU(getGenerateSeedAPDU(bytes(pinToHex(pin))));
                    promise.resolve("done");
                    Log.d(TAG, "generateSeed status : done");
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void changePin(String oldPin, String newPin, Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (oldPin.length() != PIN_SIZE || newPin.length() != PIN_SIZE)
                        throw new Exception("Incorrect pin: pin is a string of length 4.");
                    if (!isNumericString(newPin) || !isNumericString(oldPin)) throw new Exception("Pin is not in numeric string.");
                    apduRunner.sendCoinManagerAppletAPDU(getChangePinAPDU(bytes(pinToHex(oldPin)), bytes(pinToHex(newPin))));
                    promise.resolve("done");
                    Log.d(TAG, "changePin status : done");
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }
}
