package com.tonnfccardreactnativetest.smartcard.apdu;
import java.util.ArrayList;
import java.util.List;

import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.*;
import static com.tonnfccardreactnativetest.api.utils.ByteArrayHelper.*;

import com.tonnfccardreactnativetest.smartcard.cryptoUtils.HmacHelper;
import com.tonnfccardreactnativetest.smartcard.wrappers.CAPDU;

public class TonWalletAppletApduCommands {
    public static HmacHelper HMAC_HELPER = HmacHelper.getInstance();

    public static void setHmacHelper(HmacHelper hmacHelper) {
        HMAC_HELPER = hmacHelper;
    }

    //00 A4 04 00 0C 31 31 32 32 33 33 34 34 35 35 36 36 00
    public final static CAPDU SELECT_TON_WALLET_APPLET_APDU = new CAPDU(0x00, 0xA4, 0x04, 0x00, TON_WALLET_APPLET_AID, 0x00 );
    public final static CAPDU GET_APP_INFO_APDU = new CAPDU(WALLET_APPLET_CLA, INS_GET_APP_INFO, 0x00, 0x00, GET_APP_INFO_LE);
    public final static CAPDU GET_HASH_OF_ENCRYPTED_PASSWORD_APDU = new CAPDU(WALLET_APPLET_CLA, INS_GET_HASH_OF_ENCRYPTED_PASSWORD, 0x00, 0x00, SHA_HASH_SIZE);
    public final static CAPDU GET_HASH_OF_COMMON_SECRET_APDU = new CAPDU(WALLET_APPLET_CLA, INS_GET_HASH_OF_COMMON_SECRET, 0x00, 0x00, SHA_HASH_SIZE);
    public final static CAPDU GET_PUB_KEY_WITH_DEFAULT_PATH_APDU = new CAPDU(WALLET_APPLET_CLA, INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH, 0x00, 0x0, PUBLIC_KEY_LEN);
    public final static CAPDU GET_SAULT_APDU  = new CAPDU(WALLET_APPLET_CLA, INS_GET_SAULT, 0x00, 0x0, SAULT_LENGTH);

    public static final List<CAPDU> GET_APPLET_STATE_APDU_LIST = new ArrayList<>();

    static {
        GET_APPLET_STATE_APDU_LIST.add(SELECT_TON_WALLET_APPLET_APDU);
        GET_APPLET_STATE_APDU_LIST.add(GET_APP_INFO_APDU);
    }

    public static CAPDU getVerifyPasswordAPDU(byte[] passwordBytes, byte[] initialVector) {
        if (passwordBytes == null || passwordBytes.length != PASSWORD_SIZE)
            throw new IllegalArgumentException("Activation password byte array must have length " + PASSWORD_SIZE + ".");
        if (initialVector == null || initialVector.length != IV_SIZE)
            throw new IllegalArgumentException("Initial vector byte array must have length " + IV_SIZE + ".");
        byte[] data = bConcat(passwordBytes, initialVector);
        return new CAPDU(WALLET_APPLET_CLA, INS_VERIFY_PASSWORD, 0x00, 0x00, data);
    }

    public static CAPDU getVerifyPinAPDU(byte[] pinBytes, byte[] sault) throws Exception {
        if (pinBytes == null || pinBytes.length != PIN_SIZE)
            throw new IllegalArgumentException("PIN byte array must have length " + PIN_SIZE + ".");
        checkSault(sault);
        byte[] data = prepareApduData(bConcat(pinBytes, sault));
        return new CAPDU(WALLET_APPLET_CLA, INS_VERIFY_PIN, 0x00, 0x00,  data);
    }

    public static CAPDU getSignShortMessageWithDefaultPathAPDU(byte[] dataForSigning, byte[] sault) throws Exception {
        if (dataForSigning == null || dataForSigning.length == 0 || dataForSigning.length > DATA_FOR_SIGNING_MAX_SIZE)
            throw new IllegalArgumentException("Data for signing byte array must have length > 0 and <= " + DATA_FOR_SIGNING_MAX_SIZE + ".");
        checkSault(sault);
        byte[] data = prepareApduData(bConcat(new byte[]{0x00, (byte)(dataForSigning.length)}, dataForSigning, sault));
        return new CAPDU(WALLET_APPLET_CLA, INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH, 0x00, 0x00, data, SIG_LEN);
    }

    public static CAPDU getSignShortMessageAPDU(byte[] dataForSigning, byte[] ind, byte[] sault) throws Exception {
        if (dataForSigning == null || dataForSigning.length == 0 || dataForSigning.length > DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH)
            throw new IllegalArgumentException("Data for signing byte array must have length > 0 and <= " + DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH + ".");
        checkSault(sault);
        checkHdIndex(ind);
        byte[] data = prepareApduData(bConcat(new byte[]{ 0x00, (byte)(dataForSigning.length)}, dataForSigning, new byte[]{(byte)ind.length},ind, sault));
        return new CAPDU(WALLET_APPLET_CLA, INS_SIGN_SHORT_MESSAGE, 0x00, 0x00, data, SIG_LEN);
    }

    public static CAPDU getPublicKeyAPDU(byte [] ind) {
        checkHdIndex(ind);
        return new CAPDU(WALLET_APPLET_CLA, INS_GET_PUBLIC_KEY,
                0x00, 0x00,
                ind,
                PUBLIC_KEY_LEN);
    }

    public static CAPDU getResetKeyChainAPDU(byte[] sault) throws Exception {
        byte[] data = prepareSaultBasedApduData(sault);
        return new CAPDU(WALLET_APPLET_CLA, INS_RESET_KEYCHAIN, 0x00, 0x00, data);
    }

    public static CAPDU getNumberOfKeysAPDU(byte[] sault) throws Exception {
        byte[] data = prepareSaultBasedApduData(sault);
        return new CAPDU(WALLET_APPLET_CLA, INS_GET_NUMBER_OF_KEYS, 0x00, 0x00,
                data, GET_NUMBER_OF_KEYS_LE);
    }

    public static CAPDU getGetOccupiedSizeAPDU(byte[] sault) throws Exception {
        byte[] data = prepareSaultBasedApduData(sault);
        return new CAPDU(
                WALLET_APPLET_CLA,
                INS_GET_OCCUPIED_STORAGE_SIZE,
                0x00, 0x00,
                data, GET_OCCUPIED_SIZE_LE);
    }

    public static CAPDU getGetFreeSizeAPDU(byte[] sault) throws Exception {
        byte[] data = prepareSaultBasedApduData(sault);
        return new CAPDU(
                WALLET_APPLET_CLA,
                INS_GET_FREE_STORAGE_SIZE,
                0x00, 0x00,
                data, GET_FREE_SIZE_LE);
    }

    public static CAPDU getCheckAvailableVolForNewKeyAPDU(short keySize, byte[] sault) throws Exception {
        checkSault(sault);
        byte[] data = prepareApduData(bConcat(new byte[]{(byte)(keySize >> 8), (byte)(keySize)}, sault));
        return new CAPDU(
                WALLET_APPLET_CLA,
                INS_CHECK_AVAILABLE_VOL_FOR_NEW_KEY,
                0x00, 0x00,
                data);
    }

    public static CAPDU getCheckKeyHmacConsistencyAPDU(byte[] keyHmac, byte[] sault) throws  Exception {
        checkHmac(keyHmac);
        checkSault(sault);
        byte[] data = prepareApduData(bConcat(keyHmac, sault));
        return new CAPDU(WALLET_APPLET_CLA, INS_CHECK_KEY_HMAC_CONSISTENCY, 0x00, 0x00, data);
    }

    public static CAPDU getInitiateChangeOfKeyAPDU(byte[] index, byte[] sault) throws Exception {
        checkKeyChainKeyIndex(index);
        checkSault(sault);
        byte[] data = prepareApduData(bConcat(new byte[]{index[0], index[1]}, sault));
        return new CAPDU(
                WALLET_APPLET_CLA,
                INS_INITIATE_CHANGE_OF_KEY,
                0x00, 0x00,
                data);
    }

    public static CAPDU  getGetIndexAndLenOfKeyInKeyChainAPDU(byte[] keyHmac, byte[] sault) throws Exception {
        checkHmac(keyHmac);
        checkSault(sault);
        byte[] data = prepareApduData(bConcat(keyHmac, sault));
        return new CAPDU(
                WALLET_APPLET_CLA,
                INS_GET_KEY_INDEX_IN_STORAGE_AND_LEN,
                0x00, 0x00,
                data,
                GET_KEY_INDEX_IN_STORAGE_AND_LEN_LE);
    }

    public static CAPDU  getInitiateDeleteOfKeyAPDU(byte[] index, byte[] sault) throws Exception {
        checkKeyChainKeyIndex(index);
        checkSault(sault);
        byte[] data = prepareApduData(bConcat(new byte[]{index[0], index[1]}, sault));
        return new CAPDU(
                WALLET_APPLET_CLA,
                INS_INITIATE_DELETE_KEY,
                0x00, 0x00,
                data,
                INITIATE_DELETE_KEY_LE);
    }

    public static CAPDU  getDeleteKeyChunkAPDU(byte[] sault) throws Exception {
        byte[] data = prepareSaultBasedApduData(sault);
        return new CAPDU(
                WALLET_APPLET_CLA,
                INS_DELETE_KEY_CHUNK,
                0x00, 0x00,
                data,
                DELETE_KEY_CHUNK_LE);
    }

    public static CAPDU getDeleteKeyRecordAPDU(byte[] sault) throws Exception {
        byte[] data = prepareSaultBasedApduData(sault);
        return new CAPDU(
                WALLET_APPLET_CLA,
                INS_DELETE_KEY_RECORD,
                0x00, 0x00,
                data,
                DELETE_KEY_RECORD_LE);
    }

    public static CAPDU getGetHmacAPDU(byte[] index, byte[] sault) throws  Exception {
        checkKeyChainKeyIndex(index);
        checkSault(sault);
        byte[] data = prepareApduData(bConcat(index, sault));
        return new CAPDU(
                WALLET_APPLET_CLA,
                INS_GET_HMAC,
                0x00, 0x00,
                data,
                GET_HMAC_LE);
    }

    public static CAPDU getGetKeyChunkAPDU(byte[] index, short startPos, byte[] sault, byte le) throws Exception {
        checkKeyChainKeyIndex(index);
        checkSault(sault);
        byte[] data = prepareApduData(bConcat(index, new byte[]{(byte)(startPos >> 8), (byte)(startPos)}, sault));
        return new CAPDU(
                WALLET_APPLET_CLA,
                INS_GET_KEY_CHUNK,
                0x00, 0x00,
                data,
                le);
    }

    public static CAPDU getAddKeyChunkAPDU(byte p1, byte[] keyChunkOrMacBytes, byte[] sault) throws Exception {
        return getSendKeyChunkAPDU(INS_ADD_KEY_CHUNK, p1, keyChunkOrMacBytes, sault);
    }

    public static CAPDU getChangeKeyChunkAPDU(byte p1, byte[] keyChunkOrMacBytes, byte[] sault) throws Exception {
        return getSendKeyChunkAPDU(INS_CHANGE_KEY_CHUNK, p1, keyChunkOrMacBytes, sault);
    }

    public static CAPDU getSendKeyChunkAPDU(byte ins, byte p1, byte[] keyChunkOrMacBytes, byte[] sault) throws Exception {
        checkSault(sault);
        if (p1 < 0 || p1 > 2)
            throw new IllegalArgumentException("p1 APDU param takes value from {0, 1, 2}.");
        if (p1 < 2 && (keyChunkOrMacBytes == null || keyChunkOrMacBytes.length == 0 || keyChunkOrMacBytes.length > DATA_PORTION_MAX_SIZE))
            throw new IllegalArgumentException("Byte array representation of keyChunkOrMacBytes must have length > 0 and <= " + DATA_PORTION_MAX_SIZE + ".");
        if (p1 == 2 && (keyChunkOrMacBytes == null || keyChunkOrMacBytes.length != HMAC_SHA_SIG_SIZE))
            throw new IllegalArgumentException("Byte array representation of key mac must have length " + HMAC_SHA_SIG_SIZE + ".");
        byte[] data  =  (p1 == 2) ? prepareApduData(bConcat(keyChunkOrMacBytes, sault))
                : prepareApduData(bConcat(new byte[]{(byte) keyChunkOrMacBytes.length}, keyChunkOrMacBytes, sault));
        return (p1 == 2) ? new CAPDU(WALLET_APPLET_CLA, ins, p1, 0x00, data, SEND_CHUNK_LE) :
                new CAPDU(WALLET_APPLET_CLA, ins, p1, 0x00, data);
    }

    private static byte[] prepareSaultBasedApduData(byte[] sault) throws Exception {
        checkSault(sault);
        return bConcat(sault, HMAC_HELPER.computeMac(sault));
    }

    private static byte[] prepareApduData(byte[] dataChunk) throws Exception {
        return bConcat(dataChunk, HMAC_HELPER.computeMac(dataChunk));
    }

    private static void checkHmac(byte[] hmac){
        if (hmac == null || hmac.length != HMAC_SHA_SIG_SIZE)
            throw new IllegalArgumentException("Hmac byte array must have length " + HMAC_SHA_SIG_SIZE + ".");
    }

    private static void checkSault(byte[] sault){
        if (sault == null || sault.length != SAULT_LENGTH)
            throw new IllegalArgumentException("Sault byte array must have length " + SAULT_LENGTH + ".");
    }

    private static void checkHdIndex(byte[] ind){
        if (ind == null || ind.length == 0 || ind.length > MAX_IND_SIZE)
            throw new IllegalArgumentException("Byte array representation of hdIndex must have length > 0 and <= " + MAX_IND_SIZE + ".");
    }

    private static void checkKeyChainKeyIndex(byte[] ind){
        if (ind == null || ind.length != KEYCHAIN_KEY_INDEX_LEN)
            throw new IllegalArgumentException("Byte array representation of key (from keyChain) index must have length = " + KEYCHAIN_KEY_INDEX_LEN + ".");
    }
}
