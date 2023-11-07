package com.tonnfccardreactnativetest.api;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants;
import com.tonnfccardreactnativetest.smartcard.wrappers.ApduRunner;
import com.tonnfccardreactnativetest.smartcard.wrappers.RAPDU;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.tonnfccardreactnativetest.api.utils.ByteArrayHelper.*;
import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.*;
import static com.tonnfccardreactnativetest.smartcard.apdu.TonWalletAppletApduCommands.*;
import static com.tonnfccardreactnativetest.api.utils.ExceptionHelper.*;
import static com.tonnfccardreactnativetest.api.utils.StringHelper.*;

public class CardKeyChainApi extends TonWalletApi {
    private static final String TAG = "CardKeyChainNfcApi";

    private List<String> keyMacs = new ArrayList<>();

    public CardKeyChainApi(Context activity, String tag, ApduRunner apduRunner){
        super(activity, tag, apduRunner);
    }

    public void getAllHmacs(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String response = getAllHmacs();
                    promise.resolve(response);
                    Log.d(TAG, "getAllHmacs response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void resetKeyChain(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    resetKeyChain();
                    promise.resolve("done");
                    Log.d(TAG, "resetKeyChain status : done");
                    keyMacs.clear();
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getKeyChainInfo(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    RAPDU response = getNumberOfKeys();
                    int numOfKeys = makeShort(response.getData(), 0);
                    response = getOccupiedStorageSize();
                    int occupiedStorageSize = makeShort(response.getData(), 0);
                    response = getFreeStorageSize();
                    int freeStorageSize = makeShort(response.getData(), 0);
                   // String finalResponse = "Number of keys = " + numOfKeys + ", Occupied size = " + occupiedStorageSize + ", Free size = " + freeStorageSize;

                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("numberOfKeys", numOfKeys);
                    jsonResponse.put("occupiedSize", occupiedStorageSize);
                    jsonResponse.put("freeSize", freeStorageSize);

                    promise.resolve(jsonResponse.toString());
                    Log.d(TAG, "getKeyChainInfo response : " + jsonResponse.toString());
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getNumberOfKeys(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Integer response = makeShort(getNumberOfKeys().getData(), 0);
                    promise.resolve(response);
                    Log.d(TAG, "getNumberOfKeys response : " + response.toString());
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void checkKeyHmacConsistency(String keyHmac, Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (keyHmac.length() != 2 * HMAC_SHA_SIG_SIZE)
                        throw new Exception("Incorrect keyHmac: keyHmac is a hex string of length 64.");
                    if (!isHexString(keyHmac))
                        throw new Exception("Key hmac is not in hex.");
                    checkKeyHmacConsistency(bytes(keyHmac));
                    promise.resolve("done");
                    Log.d(TAG, "checkKeyHmacConsistency status : done");
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void checkAvailableVolForNewKey(Short keySize, Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    checkAvailableVolForNewKey(keySize);
                    promise.resolve("done");
                    Log.d(TAG, "checkAvailableVolForNewKey status : done");
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getIndexAndLenOfKeyInKeyChain(String keyHmac, Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (keyHmac.length() != 2 * HMAC_SHA_SIG_SIZE)
                        throw new Exception("Incorrect keyHmac: keyHmac is a hex string of length 64.");
                    if (!isHexString(keyHmac))
                        throw new Exception("Key hmac is not in hex.");
                    String response = hex(getIndexAndLenOfKeyInKeyChain(bytes(keyHmac)).getData());
                    promise.resolve(response);
                    Log.d(TAG, "getIndexAndLenOfKeyInKeyChain response : " + response);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void deleteKeyFromKeyChain(String keyHmac, Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (keyHmac.length() != 2 * HMAC_SHA_SIG_SIZE)
                        throw new Exception("Incorrect keyHmac: keyHmac is a hex string of length 64.");
                    if (!isHexString(keyHmac))
                        throw new Exception("Key hmac is not in hex.");
                    Integer response = deleteKeyFromKeyChain(bytes(keyHmac));
                    promise.resolve(response);
                    Log.d(TAG, "deleteKeyFromKeyChain response (number of remained keys) : " + response.toString());
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void finishDeleteKeyFromKeyChainAfterInterruption(String keyHmac, Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (keyHmac.length() != 2 * HMAC_SHA_SIG_SIZE)
                        throw new Exception("Incorrect keyHmac: keyHmac is a hex string of length 64.");
                    if (!isHexString(keyHmac))
                        throw new Exception("Key hmac is not in hex.");
                    Integer response = finishDeleteKeyFromKeyChainAfterInterruption(bytes(keyHmac));
                    promise.resolve(response);
                    Log.d(TAG, "finishDeleteKeyFromKeyChainAfterInterruption response (number of remained keys) : " + response.toString());
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getOccupiedStorageSize(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Integer response = makeShort(getOccupiedStorageSize().getData(), 0);
                    promise.resolve(response);
                    Log.d(TAG, "getOccupiedStorageSize response : " + response.toString());
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getFreeStorageSize(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Integer response = makeShort(getFreeStorageSize().getData(), 0);
                    promise.resolve(response);
                    Log.d(TAG, "getFreeStorageSize response : " + response.toString());
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getKeyFromKeyChain(String keyHmac, Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (keyHmac.length() != 2 * HMAC_SHA_SIG_SIZE)
                        throw new Exception("Incorrect keyHmac: keyHmac is a hex string of length 64.");
                    if (!isHexString(keyHmac))
                        throw new Exception("Key hmac is not in hex.");
                    String key = hex(getKeyFromKeyChain(bytes(keyHmac)));
                    promise.resolve(key);
                    Log.d(TAG, "getKey response : " + key);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void addKeyIntoKeyChain(String newKey, Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (newKey.length() > 2 * MAX_KEY_SIZE_IN_KEYCHAIN)
                        throw new Exception("Incorrect key: keyHmac is a hex string of length <= 2 * 8192.");
                    if (!isHexString(newKey))
                        throw new Exception("Key is not in hex.");
                    String keyHmac = addKeyIntoKeyChain(bytes(newKey));
                    promise.resolve(keyHmac);
                    Log.d(TAG, "addKey response (hmac of new key) : " + keyHmac);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void changeKeyInKeyChain(String newKey, String oldKeyHMac, Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (newKey.length() > 2 * MAX_KEY_SIZE_IN_KEYCHAIN)
                        throw new Exception("Incorrect key: keyHmac is a hex string of length <= 2 * 8192.");
                    if (oldKeyHMac.length() != 2 * HMAC_SHA_SIG_SIZE)
                        throw new Exception("Incorrect keyHmac: keyHmac is a hex string of length 64.");
                    if (!isHexString(newKey))
                        throw new Exception("Key is not in hex.");
                    if (!isHexString(oldKeyHMac))
                        throw new Exception("Key hmac is not in hex.");
                    String newKeyHmac = changeKeyInKeyChain(bytes(newKey), bytes(oldKeyHMac));
                    promise.resolve(newKeyHmac);
                    Log.d(TAG, "changeKey response (hmac of new key) : " + newKeyHmac);
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public void getKeyChainDataAboutAllKeys(Promise promise) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Map<String, Integer> map = getAllHmacsOfKeysFromCard();
                    JSONArray jArray = new JSONArray();
                    //WritableArray writableArray = new WritableNativeArray();
                    for (String hmac : map.keySet()) {
                       /* WritableMap elem = new WritableNativeMap();
                        elem.putString("hmac", hmac);
                        elem.putString("length", map.get(hmac).toString());
                        writableArray.pushMap(elem);*/
                        JSONObject jObject = new JSONObject();
                        jObject.put("hmac", hmac);
                        jObject.put("length", map.get(hmac).toString());
                        jArray.put(jObject);
                    }
                    promise.resolve(jArray.toString());
                    Log.d(TAG, " getKeyChainDataAboutAllKeys response : " + jArray.toString());
                } catch (Exception e) {
                    handleException(e, promise, TAG);
                }
            }
        }).start();
    }

    public RAPDU resetKeyChain() throws Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        return apduRunner.sendAPDU(getResetKeyChainAPDU(sault));
    }

    public RAPDU getNumberOfKeys() throws Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        return apduRunner.sendAPDU(getNumberOfKeysAPDU(sault));
    }

    public RAPDU checkKeyHmacConsistency(byte[] keyHmac) throws  Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        return apduRunner.sendAPDU(getCheckKeyHmacConsistencyAPDU(keyHmac, sault));
    }

    public RAPDU checkAvailableVolForNewKey(short keySize) throws Exception {
        byte appletState = selectTonWalletAppletAndGetTonAppletState().getData()[0];
        if (appletState != APP_PERSONALIZED) throw new Exception("Applet must be in personalized mode. Now it is " + TonWalletAppletConstants.getStateName(appletState));
        byte[] sault = getSaultBytes();
        return apduRunner.sendAPDU(getCheckAvailableVolForNewKeyAPDU(keySize, sault));
    }

    private RAPDU initiateChangeOfKey(byte[] index) throws Exception {
        byte appletState = selectTonWalletAppletAndGetTonAppletState().getData()[0];
        if (appletState != APP_PERSONALIZED) throw new Exception("Applet must be in personalized mode. Now it is " + TonWalletAppletConstants.getStateName(appletState));
        byte[] sault = getSaultBytes();
        return apduRunner.sendAPDU(getInitiateChangeOfKeyAPDU(index, sault));
    }

    public RAPDU getIndexAndLenOfKeyInKeyChain(byte[] keyHmac) throws Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        return apduRunner.sendAPDU(getGetIndexAndLenOfKeyInKeyChainAPDU(keyHmac, sault));
    }

    public int deleteKeyFromKeyChain(byte[] macBytes) throws Exception {
//        System.out.println("\n\n\n Delete key from keychain: ");
//        String mac = ByteArrayHelper.hex(macBytes);
//        int lenOfKey = keyChainData.get(mac).length() / 2;
//        System.out.println("mac to delete = " + mac);
//        System.out.println("key to delete = " + keyChainData.get(mac));
//        System.out.println("key length to delete = " + lenOfKey + "\n\n");

        byte[] data = getIndexAndLenOfKeyInKeyChain(macBytes).getData();
        byte[] index  = bSub(data, 0, 2);

        initiateDeleteOfKey(index);

        int deleteKeyChunkIsDone = 0;
        while (deleteKeyChunkIsDone == 0) {
            deleteKeyChunkIsDone = deleteKeyChunk().getData()[0];
        }

        int deleteKeyRecordIsDone = 0;
        while (deleteKeyRecordIsDone == 0) {
            deleteKeyRecordIsDone = deleteKeyRecord().getData()[0];
        }

        int numOfKeysInKeyChain = makeShort(getNumberOfKeys().getData(), 0);

        int ind = keyMacs.indexOf(hex(macBytes));
        keyMacs.remove(ind);
        return numOfKeysInKeyChain;
    }

    public int finishDeleteKeyFromKeyChainAfterInterruption(byte[] macBytes) throws Exception {
        byte appletState = selectTonWalletAppletAndGetTonAppletState().getData()[0];
        if (appletState != APP_DELETE_KEY_FROM_KEYCHAIN_MODE) throw new Exception("Applet must be in mode for deleting key. Now it is " + TonWalletAppletConstants.getStateName(appletState));

        int deleteKeyChunkIsDone = 0;
        while (deleteKeyChunkIsDone == 0) {
            deleteKeyChunkIsDone = deleteKeyChunk().getData()[0];
        }

        int deleteKeyRecordIsDone = 0;
        while (deleteKeyRecordIsDone == 0) {
            deleteKeyRecordIsDone = deleteKeyRecord().getData()[0];
        }

        int numOfKeysInKeyChain = makeShort(getNumberOfKeys().getData(), 0);

        int ind = keyMacs.indexOf(hex(macBytes));
        keyMacs.remove(ind);

        return numOfKeysInKeyChain;
    }

    private RAPDU initiateDeleteOfKey(byte[] index) throws Exception {
        byte appletState = selectTonWalletAppletAndGetTonAppletState().getData()[0];
        if (appletState != APP_PERSONALIZED) throw new Exception("Applet must be in personalized mode. Now it is " + TonWalletAppletConstants.getStateName(appletState));
        byte[] sault = getSaultBytes();
        return apduRunner.sendAPDU(getInitiateDeleteOfKeyAPDU(index, sault));
    }

    private RAPDU deleteKeyChunk() throws Exception {
        byte[] sault = getSaultBytes();
        return apduRunner.sendAPDU(getDeleteKeyChunkAPDU(sault));
    }

    private RAPDU deleteKeyRecord() throws Exception {
        byte[] sault = getSaultBytes();
        return apduRunner.sendAPDU(getDeleteKeyRecordAPDU(sault));
    }

    public RAPDU getOccupiedStorageSize() throws Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        return apduRunner.sendAPDU(getGetOccupiedSizeAPDU(sault));
    }

    public RAPDU getFreeStorageSize() throws Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        return apduRunner.sendAPDU(getGetFreeSizeAPDU(sault));
    }

    private RAPDU getHmac( byte[] ind) throws  Exception {
        byte[] sault = selectTonWalletAppletAndGetSaultBytes();
        return apduRunner.sendAPDU(getGetHmacAPDU(ind, sault));
    }

    public byte[] getKeyFromKeyChain(byte[] macBytes) throws Exception{
        //System.out.println("\n Get key from keychain:");
        byte[] data = getIndexAndLenOfKeyInKeyChain(macBytes).getData();
        int keyLen = makeShort( data, 2);
        byte[] keyFromCard = getKeyFromKeyChain(keyLen, bSub(data, 0, 2));
        String mac = hex(macBytes);
//        System.out.println("mac = " + mac);
//        System.out.println("key from keyChainData = " + keyChainData.get(mac));
//        System.out.println("keyFromCard = " + ByteArrayHelper.hex(keyFromCard));
//        System.out.println("key length = " + keyLen  + "\n\n\n");
        return keyFromCard;
    }

    private byte[] getKeyFromKeyChain(int keyLen, byte[] ind) throws Exception {
        if(ind.length  != 2 || keyLen > KEY_CHAIN_SIZE || keyLen <= 0) {
            throw new IllegalArgumentException("Bad length.");
        }
        byte[] key = new byte[keyLen];
        byte[] dataChunk, sault, res;
        int numberOfPackets = keyLen / DATA_PORTION_MAX_SIZE;
        short startPos = 0;

        //System.out.println("numberOfPackets = " + numberOfPackets);
        for(int i = 0; i < numberOfPackets; i++) {
            //System.out.println("packet " + i);
            sault = getSaultBytes();
            RAPDU response = apduRunner.sendAPDU(getGetKeyChunkAPDU(ind, startPos, sault, (byte) DATA_PORTION_MAX_SIZE));
            arrayCopy(response.getBytes(), 0, key, startPos, DATA_PORTION_MAX_SIZE);
            startPos += DATA_PORTION_MAX_SIZE;
        }

        int tailLen = keyLen % DATA_PORTION_MAX_SIZE;
        if(tailLen > 0) {
            sault = getSaultBytes();
            RAPDU response = apduRunner.sendAPDU(getGetKeyChunkAPDU(ind, startPos, sault, (byte) tailLen));
            arrayCopy(response.getBytes(), 0, key, startPos, tailLen);
        }

        return key;
    }

    private RAPDU addKey(byte[] keyBytes) throws Exception {
        return sendKey(keyBytes, INS_ADD_KEY_CHUNK);
    }

    private RAPDU changeKey(byte[] keyBytes) throws Exception {
        return sendKey(keyBytes, INS_CHANGE_KEY_CHUNK);
    }

    private RAPDU sendKey(byte[] keyBytes, byte ins) throws Exception {
        if(keyBytes.length > KEY_CHAIN_SIZE || keyBytes.length  <= 0) {
            throw new IllegalArgumentException("Bad length!");
        }

        int numberOfPackets = keyBytes.length / DATA_PORTION_MAX_SIZE;

        byte[] keyChunk, sault;

        // System.out.println("numberOfPackets = " + numberOfPackets);

        for(int i = 0; i < numberOfPackets; i++) {
            //System.out.println("packet " + i);
            sault = getSaultBytes();
            keyChunk = bSub(keyBytes, i * DATA_PORTION_MAX_SIZE, DATA_PORTION_MAX_SIZE);
            apduRunner.sendAPDU(getSendKeyChunkAPDU(ins, i == 0 ? (byte)0x00 : (byte)0x01, keyChunk, sault));
        }

        int tailLen = keyBytes.length % DATA_PORTION_MAX_SIZE;

        if(tailLen > 0) {
            sault = getSaultBytes();
            keyChunk =  bSub(keyBytes, numberOfPackets * DATA_PORTION_MAX_SIZE, tailLen);
            apduRunner.sendAPDU(getSendKeyChunkAPDU(ins, numberOfPackets == 0 ? (byte)0x00 : (byte)0x01, keyChunk, sault));
        }

        byte[] mac = HMAC_HELPER.computeMac(keyBytes);
        sault = getSaultBytes();
        return apduRunner.sendAPDU(getSendKeyChunkAPDU(ins, (byte)0x02, mac, sault));
    }

    public /*RAPDU*/ String addKeyIntoKeyChain(byte[] keyBytes) throws Exception{
//        System.out.println("\n Add new key into keychain: ");
//        System.out.println("key to add = " + ByteArrayHelper.hex(keyBytes));
//        byte[] mac = computeMac(keyBytes);
//        System.out.println("mac to add = " + ByteArrayHelper.hex(mac));
//        System.out.println("key size to add = " + keyBytes.length);
////        if (isHmacExistOnCard(mac)){
////            System.out.println("Key with such mac already exists! Skip it");
////            return;
////        }
        checkAvailableVolForNewKey((short) keyBytes.length);
        RAPDU response = addKey(keyBytes);
        keyMacs.add(hex(HMAC_HELPER.computeMac(keyBytes)));
        return hex(HMAC_HELPER.computeMac(keyBytes));
        //return response;
    }

    public /*RAPDU*/ String changeKeyInKeyChain(byte[] newKeyBytes, byte[] macBytesOfOldKey) throws Exception {
//        System.out.println("\n Change key in keychain: ");
//        String macOld = ByteArrayHelper.hex(macBytesOfOldKey);
//        int lenOfOldKey = keyChainData.get(macOld).length() / 2;
//        byte[] mac = computeMac(newKeyBytes);
//        System.out.println("mac of key to change = " + macOld);
//        System.out.println("key to change = " + keyChainData.get(macOld));
//        System.out.println("key length to change = " + lenOfOldKey);
//        System.out.println("new mac = " + ByteArrayHelper.hex(mac));
//        System.out.println("new key = " + ByteArrayHelper.hex(newKeyBytes));

        byte[] data = getIndexAndLenOfKeyInKeyChain(macBytesOfOldKey).getData();
        int indexIntStorage = makeShort(data, 0);
        int keyLen = makeShort(data,2);
        if(keyLen != newKeyBytes.length) {
            throw new IllegalArgumentException("Bad new key length.");
        }
        initiateChangeOfKey(bSub(data, 0, 2));
        RAPDU response = changeKey(newKeyBytes);
        int ind = keyMacs.indexOf(hex(macBytesOfOldKey));
        keyMacs.set(ind, hex(HMAC_HELPER.computeMac(newKeyBytes)));
        return hex(HMAC_HELPER.computeMac(newKeyBytes));
        //return response;
    }

    public String getAllHmacs() {
        return keyMacs.toString();
    }

    public Map<String, Integer> getAllHmacsOfKeysFromCard() throws  Exception {
        System.out.println("\n Get hmacs of all keys stored in card keychain:");
        Map<String, Integer> hmacs = new LinkedHashMap<>();
        keyMacs.clear();

        int numOfKeys = makeShort(getNumberOfKeys().getData(), 0);
        byte[] ind = new byte[2];
        for(short i = 0; i < numOfKeys; i++){
            setShort(ind, (short)0, i);
            byte data[] = getHmac(ind).getData();
            byte mac[] = bSub(data, 0, HMAC_SHA_SIG_SIZE);
            int len = makeShort(data, HMAC_SHA_SIG_SIZE);
            hmacs.put(hex(mac), len);
            keyMacs.add(hex(mac));
        }
        return hmacs;
    }

}
