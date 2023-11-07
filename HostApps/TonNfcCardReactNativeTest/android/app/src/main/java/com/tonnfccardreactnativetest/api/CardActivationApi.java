package com.tonnfccardreactnativetest.api;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants;
import com.tonnfccardreactnativetest.smartcard.wrappers.ApduRunner;
import com.tonnfccardreactnativetest.smartcard.wrappers.RAPDU;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.tonnfccardreactnativetest.api.utils.ByteArrayHelper.*;
import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.*;
import static com.tonnfccardreactnativetest.smartcard.apdu.CoinManagerApduCommands.RESET_WALLET_APDU;
import static com.tonnfccardreactnativetest.smartcard.apdu.CoinManagerApduCommands.getChangePinAPDU;
import static com.tonnfccardreactnativetest.smartcard.apdu.CoinManagerApduCommands.getGenerateSeedAPDU;
import static com.tonnfccardreactnativetest.smartcard.apdu.TonWalletAppletApduCommands.*;
import static com.tonnfccardreactnativetest.api.utils.ExceptionHelper.*;
import static com.tonnfccardreactnativetest.api.utils.StringHelper.*;

public class CardActivationApi extends TonWalletApi {
    private static final String TAG = "CardActivationNfcApi";

    public CardActivationApi(Context activity, String tag, ApduRunner apduRunner){
        super(activity, tag, apduRunner);
    }

    public void turnOnWallet(String newPin, String password, String commonSecret, String initialVector, Promise promise){
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (password.length() != 2 * PASSWORD_SIZE)
                        throw new Exception("Incorrect password: password is hex string of length 256. " + password.length());
                    if (newPin.length() != PIN_SIZE)
                        throw new Exception("Incorrect pin: pin is a string of length 4.");
                    if (commonSecret.length() != 2 * COMMON_SECRET_SIZE)
                        throw new Exception("Incorrect common secret: common secret is hex string of length 64.");
                    if (initialVector.length() != 2 * IV_SIZE)
                        throw new Exception("Incorrect iv: iv is hex string of length 32.");
                    if (!isHexString(password)) throw new Exception("Password is not in hex.");
                    if (!isHexString(commonSecret)) throw new Exception("Common secret is not in hex.");
                    if (!isHexString(initialVector)) throw new Exception("Initial vector is not in hex.");
                    if (!isNumericString(newPin)) throw new Exception("Pin is not in numeric string.");
                    RAPDU response = turnOnWallet(bytes(pinToHex(newPin)), bytes(password), bytes(commonSecret), bytes(initialVector));
                    String appletState = getStateName(response.getData()[0]);
                    promise.resolve(appletState);
                    Log.d(TAG, "appletState : " + appletState);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getHashOfCommonSecret(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response =  hex(getHashOfCommonSecret().getData());
                    promise.resolve(response);
                    Log.d(TAG, "getHashOfCommonSecret response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getHashOfEncryptedPassword(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response = hex(getHashOfEncryptedPassword().getData());
                    promise.resolve(response);
                    Log.d(TAG, "getHashOfEncryptedPassword response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }


    public RAPDU turnOnWallet(byte[] newPinBytes, byte[] password, byte[] commonSecret, byte[] initialVector) throws Exception {
        apduRunner.sendCoinManagerAppletAPDU(RESET_WALLET_APDU);
        apduRunner.sendAPDU(getGenerateSeedAPDU(DEFAULT_PIN));
        setPassword(password);
        setCommonSecret(commonSecret);
        setInitialVector(initialVector);
        byte appletState = selectTonWalletAppletAndGetTonAppletState().getData()[0];
        if (appletState != APP_WAITE_AUTHORIZATION_MODE) throw new Exception("Applet must be in mode that waits authorization. Now it is " + TonWalletAppletConstants.getStateName(appletState));
        boolean status = verifyHashOfCommonSecret();
        if (!status) throw new Exception("Card two-factor authorization failed: Hash of common secret is invalid.");
        status = verifyHashOfEncryptedPassword();
        if (!status) throw new Exception("Card two-factor authorization failed: Hash of encrypted password is invalid.");
        verifyPassword();
        apduRunner.sendCoinManagerAppletAPDU(getChangePinAPDU(DEFAULT_PIN, newPinBytes));
        setKeyForHmac();
        return selectTonWalletAppletAndGetTonAppletState();
    }

    private boolean selectTonWalletAppletAndAndVerifyHashOfCommonSecret() throws Exception {
        String hashFromCard = hex(selectTonWalletAppletAndAndGetHashOfCommonSecret().getData());
        String commonSecretHash = hex(digest.digest(commonSecret));
        return  hashFromCard.equals(commonSecretHash);
    }

    private boolean verifyHashOfCommonSecret() throws Exception {
        String hashFromCard = hex(getHashOfCommonSecret().getData());
        String commonSecretHash = hex(digest.digest(commonSecret));
        return  hashFromCard.equals(commonSecretHash);
    }

    private boolean verifyHashOfEncryptedPassword() throws Exception {
        String hashFromCard = hex(getHashOfEncryptedPassword().getData());
        IvParameterSpec ivSpec  = new IvParameterSpec(initialVector);
        byte[] passwordHash = digest.digest(password);
        SecretKeySpec skeySpec = new SecretKeySpec(bSub(passwordHash, 0, 16), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        byte[] encryptedPassword = cipher.doFinal(password);
        String encryptedPasswordHash = hex(digest.digest(encryptedPassword));
        return  hashFromCard.equals(encryptedPasswordHash);
    }

    public RAPDU getHashOfCommonSecret() throws Exception {
        return apduRunner.sendAPDU(GET_HASH_OF_COMMON_SECRET_APDU);
    }

    public RAPDU getHashOfEncryptedPassword() throws Exception {
        return apduRunner.sendAPDU(GET_HASH_OF_ENCRYPTED_PASSWORD_APDU);
    }

    public RAPDU selectTonWalletAppletAndAndGetHashOfCommonSecret() throws Exception {
        return apduRunner.sendTonWalletAppletAPDU(GET_HASH_OF_COMMON_SECRET_APDU);
    }

    public RAPDU selectTonWalletAppletAndAndGetHashOfEncryptedPassword() throws Exception {
        return apduRunner.sendTonWalletAppletAPDU(GET_HASH_OF_ENCRYPTED_PASSWORD_APDU);
    }

    private RAPDU verifyPassword() throws Exception {
        return apduRunner.sendAPDU(getVerifyPasswordAPDU(password, initialVector));
    }

    private RAPDU selectTonWalletAppletAndVerifyPassword() throws Exception {
        return apduRunner.sendTonWalletAppletAPDU(getVerifyPasswordAPDU(password, initialVector));
    }
}
