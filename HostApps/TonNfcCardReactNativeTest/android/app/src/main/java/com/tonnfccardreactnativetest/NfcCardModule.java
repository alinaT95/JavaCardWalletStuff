package com.tonnfccardreactnativetest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.tonnfccardreactnativetest.api.CardActivationApi;
import com.tonnfccardreactnativetest.api.CardCoinManagerApi;
import com.tonnfccardreactnativetest.api.CardCryptoApi;
import com.tonnfccardreactnativetest.api.CardKeyChainApi;
import com.tonnfccardreactnativetest.api.nfc.NfcApduRunner;

public class NfcCardModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {
    private static final String TAG = "NfcCardModule";
    private ReactApplicationContext reactContext;
    private NfcApduRunner nfcApduRunner;
    private NfcAdapter nfcAdapter;
    private EventEmitter eventEmitter;

    private CardCoinManagerApi cardCoinManagerNfcApi;
    private CardActivationApi cardActivationNfcApi;
    private CardCryptoApi cardCryptoNfcApi;
    private CardKeyChainApi cardKeyChainNfcApi;

    private static String[][] TECHLISTS;
    private static IntentFilter[] TAGFILTERS;

    static {
        try {
            TECHLISTS = new String[][]{{IsoDep.class.getName()},
                    {NfcA.class.getName()},};

            TAGFILTERS = new IntentFilter[]{new IntentFilter(
                    NfcAdapter.ACTION_TECH_DISCOVERED, "*/*")};
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NfcCardModule(ReactApplicationContext reactContext) throws Exception {
        super(reactContext);
        this.reactContext = reactContext;
        nfcApduRunner = NfcApduRunner.getInstance(reactContext);

        getReactApplicationContext().addActivityEventListener(this);
        getReactApplicationContext().addLifecycleEventListener(this);

        if (cardCoinManagerNfcApi == null) {
            cardCoinManagerNfcApi = new CardCoinManagerApi(reactContext, "NFC", /*mApiNfcardOTP*/ nfcApduRunner);
        }

        if (cardActivationNfcApi == null) {
            cardActivationNfcApi = new CardActivationApi(reactContext, "NFC", /*mApiNfcardOTP*/ nfcApduRunner);
        }

        if (cardCryptoNfcApi == null) {
            cardCryptoNfcApi = new CardCryptoApi(reactContext, "NFC", /*mApiNfcardOTP*/ nfcApduRunner);
        }

        if (cardKeyChainNfcApi == null) {
            cardKeyChainNfcApi = new CardKeyChainApi(reactContext, "NFC", /*mApiNfcardOTP*/ nfcApduRunner);
        }

        this.eventEmitter = new EventEmitter(reactContext);

        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        this.reactContext.registerReceiver(mReceiver, filter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
                    Toast.makeText(getCurrentActivity(), "NFC Adapter state: " + stateStr, Toast.LENGTH_SHORT).show();
                    eventEmitter.emit("nfcAdapterStateChanged", writableMap);
                } catch (Exception ex) {
                    Log.d("", "send nfc state change event fail: " + ex);
                }
            }
        }
    };

    @Override
    public void onHostResume() {
        if (nfcAdapter != null) {
            setupForegroundDispatch(getCurrentActivity(), nfcAdapter);
        } else {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this.reactContext);
            setupForegroundDispatch(getCurrentActivity(), nfcAdapter);
        }
        nfcApduRunner.setNfcAdapter(nfcAdapter);
    }

    @Override
    public void onHostPause() {
        if (nfcAdapter != null)
            stopForegroundDispatch(getCurrentActivity(), nfcAdapter);
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (nfcApduRunner.setCardTag(intent)) {
            eventEmitter.emit("nfcTagIsConnected", null);
        }
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
        if (adapter != null && adapter.isEnabled()) {
            adapter.enableForegroundDispatch(activity, pendingIntent, /*, TAGFILTERS, TECHLISTS*/ null, null);
        }
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }


    @Override
    public void onHostDestroy() {
        this.reactContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void onActivityResult(
            final Activity activity,
            final int requestCode,
            final int resultCode,
            final Intent intent) {
    }

    @ReactMethod
    public void setKeyForHmac(String password, String commonSecret, Promise promise) {
        cardCryptoNfcApi.setKeyForHmac(password, commonSecret, promise);
    }

    @ReactMethod
    public void setPin(String pin, Promise promise) {
        cardCryptoNfcApi.setPin(pin, promise);
    }

    @ReactMethod
    public void isNfcSupported(final Promise promise) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            boolean res = currentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
            promise.resolve(Boolean.valueOf(res).toString());
        } else {
            promise.resolve("false");
        }
    }

    @ReactMethod
    public void isNfcEnabled(final Promise promise) {
        if (nfcAdapter != null) {
            promise.resolve(Boolean.valueOf(nfcAdapter.isEnabled()).toString());
        } else {
            promise.resolve("false");
        }
    }

    @ReactMethod
    public void openNfcSettings(final Promise promise) {
        Activity currentActivity = getCurrentActivity();
        currentActivity.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        promise.resolve(true);
    }

    @ReactMethod
    public void disconnectCard(final Promise promise) {
        cardCryptoNfcApi.disconnectCard(promise);
    }

    /** CoinManager commands **/

    @ReactMethod
    public void setDeviceLabel(String deviceLabel, Promise promise){
        cardCoinManagerNfcApi.setDeviceLabel(deviceLabel, promise);
    }

    @ReactMethod
    public void getDeviceLabel(Promise promise){
        cardCoinManagerNfcApi.getDeviceLabel(promise);
    }

    @ReactMethod
    public void getSeVersion(Promise promise){
        cardCoinManagerNfcApi.getSeVersion(promise);
    }

    @ReactMethod
    public void getCsn(Promise promise){
        cardCoinManagerNfcApi.getCsn(promise);
    }

    @ReactMethod
    public void getMaxPinTries(Promise promise) {
        cardCoinManagerNfcApi.getMaxPinTries(promise);
    }

    @ReactMethod
    public void getRemainingPinTries(Promise promise){
        cardCoinManagerNfcApi.getRemainingPinTries(promise);
    }

    @ReactMethod
    public void getRootKeyStatus(Promise promise){
        cardCoinManagerNfcApi.getRootKeyStatus(promise);
    }

    @ReactMethod
    public void resetWallet(Promise promise){
        cardCoinManagerNfcApi.resetWallet(promise);
    }

    @ReactMethod
    public void getAvailableMemory(Promise promise){
        cardCoinManagerNfcApi.getAvailableMemory(promise);
    }

    @ReactMethod
    public void getAppsList(Promise promise){
        cardCoinManagerNfcApi.getAppsList(promise);
    }

    @ReactMethod
    public void generateSeed(String pin, Promise promise){
        cardCoinManagerNfcApi.generateSeed(pin, promise);
    }

    @ReactMethod
    public void changePin(String oldPin, String newPin, Promise promise){
        cardCoinManagerNfcApi.changePin(oldPin, newPin, promise);
    }

    /** TonWalletApplet card activation related stuff **/

    @ReactMethod
    public void turnOnWallet(String newPin, String password, String commonSecret, String initialVector, Promise promise){
        cardActivationNfcApi.turnOnWallet(newPin, password, commonSecret, initialVector, promise);
    }

    @ReactMethod
    public void getHashOfCommonSecret(Promise promise) {
        cardActivationNfcApi.getHashOfCommonSecret(promise);
    }

    @ReactMethod
    public void getHashOfEncryptedPassword(Promise promise) {
        cardActivationNfcApi.getHashOfEncryptedPassword(promise);
    }

    /** TonWalletApplet common commands **/

    @ReactMethod
    public void getTonAppletState(Promise promise) {
        cardCryptoNfcApi.selectTonWalletAppletAndGetTonAppletState(promise);
    }

    @ReactMethod
    public void getSault(Promise promise) {
        cardCryptoNfcApi.getSault(promise);
    }

    /** TonWalletApplet Ed25519 related commands **/

    @ReactMethod
    public void verifyPin(String pin, Promise promise){
        cardCryptoNfcApi.verifyPin(pin, promise);
    }

    @ReactMethod
    public void getPublicKeyForDefaultPath(Promise promise) {
        cardCryptoNfcApi.getPublicKeyForDefaultPath(promise);
    }

    @ReactMethod
    public void getPublicKey(String index,  Promise promise){
        cardCryptoNfcApi.getPublicKey(index, promise);
    }

    @ReactMethod
    public void signForDefaultHdPath(String dataForSigning, Promise promise){
        cardCryptoNfcApi.signForDefaultHdPath(dataForSigning, promise);
    }

    @ReactMethod
    public void sign(String dataForSigning, String index,  Promise promise){
        cardCryptoNfcApi.sign(dataForSigning, index, promise);
    }

    @ReactMethod
    public void verifyPinAndSignForDefaultHdPath(String dataForSigning, String pin, Promise promise){
        cardCryptoNfcApi.verifyPinAndSignForDefaultHdPath(dataForSigning, pin, promise);
    }

    @ReactMethod
    public void verifyPinAndSign(String dataForSigning, String index, String pin, Promise promise){
        cardCryptoNfcApi.verifyPinAndSign(dataForSigning, index, pin, promise);
    }

    /** TonWalletApplet card keychain related commands **/

    @ReactMethod
    public void getAllHmacs(Promise promise) {
        cardKeyChainNfcApi.getAllHmacs(promise);
    }

    @ReactMethod
    public void resetKeyChain(Promise promise) {
        cardKeyChainNfcApi.resetKeyChain(promise);
    }

    @ReactMethod
    public void getKeyChainInfo(Promise promise) {
        cardKeyChainNfcApi.getKeyChainInfo(promise);
    }

    @ReactMethod
    public void getNumberOfKeys(Promise promise) {
        cardKeyChainNfcApi.getNumberOfKeys(promise);
    }

    @ReactMethod
    public void checkKeyHmacConsistency(String keyHmac, Promise promise) {
        cardKeyChainNfcApi.checkKeyHmacConsistency(keyHmac, promise);
    }

    @ReactMethod
    public void checkAvailableVolForNewKey(Short keySize, Promise promise) {
        cardKeyChainNfcApi.checkAvailableVolForNewKey(keySize, promise);
    }

    @ReactMethod
    public void getIndexAndLenOfKeyInKeyChain(String keyHmac, Promise promise) {
        cardKeyChainNfcApi.getIndexAndLenOfKeyInKeyChain(keyHmac, promise);
    }

    @ReactMethod
    public void deleteKeyFromKeyChain(String keyHmac, Promise promise) {
        cardKeyChainNfcApi.deleteKeyFromKeyChain(keyHmac, promise);
    }

    @ReactMethod
    public void finishDeleteKeyFromKeyChainAfterInterruption(String keyHmac, Promise promise) {
        cardKeyChainNfcApi.finishDeleteKeyFromKeyChainAfterInterruption(keyHmac, promise);
    }

    @ReactMethod
    public void getOccupiedStorageSize(Promise promise) {
        cardKeyChainNfcApi.getOccupiedStorageSize(promise);
    }

    @ReactMethod
    public void getFreeStorageSize(Promise promise) {
        cardKeyChainNfcApi.getFreeStorageSize(promise);
    }

    @ReactMethod
    public void getKeyFromKeyChain(String keyHmac, Promise promise) {
        cardKeyChainNfcApi.getKeyFromKeyChain(keyHmac, promise);
    }

    @ReactMethod
    public void addKeyIntoKeyChain(String newKey, Promise promise) {
        cardKeyChainNfcApi.addKeyIntoKeyChain(newKey, promise);
    }

    @ReactMethod
    public void changeKeyInKeyChain(String newKey, String oldKeyHMac, Promise promise) {
        cardKeyChainNfcApi.changeKeyInKeyChain(newKey, oldKeyHMac, promise);
    }

    @ReactMethod
    public void getKeyChainDataAboutAllKeys(Promise promise) {
        cardKeyChainNfcApi.getKeyChainDataAboutAllKeys(promise);
    }

    @Override
    public String getName() {
        return "NfcCardModule";
    }


}
