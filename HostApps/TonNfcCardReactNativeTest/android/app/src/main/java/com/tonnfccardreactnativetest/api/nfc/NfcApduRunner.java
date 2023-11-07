package com.tonnfccardreactnativetest.api.nfc;

import android.content.Context;
import android.content.Intent;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;

import com.tonnfccardreactnativetest.smartcard.wrappers.ApduRunner;

import java.io.IOException;

import static android.nfc.NfcAdapter.EXTRA_TAG;

import com.tonnfccardreactnativetest.smartcard.wrappers.CAPDU;
import com.tonnfccardreactnativetest.smartcard.wrappers.RAPDU;

public class NfcApduRunner extends ApduRunner {


    public static final String EXCP_COMM_TRANSCEIVE = "error: transceive except";



    public static final String ERROR_RECV_DATA = "error: receive data error";
    public static final int TIME_OUT = 60000;

    public static final String ERROR_MSG_NFC_CONNECT = "Nfc connection establishing error";
    public static final String ERROR_MSG_NFC_DISABLED = "Nfc disabled";
    public static final String ERROR_MSG_NO_NFC = "Nfc is not found for this smartphone.";
    public static final String ERROR_MSG_NO_TAG = "Nfc tag is not found.";
    public static final String ERROR_MSG_COMM_DISCONNECT = "Error happened during NFC tag disconnection.";
    public static final String ERROR_MSG_NO_CONTEXT = "Context is null";


    private static NfcApduRunner nfcApduRunner;
    private NfcAdapter nfcAdapter;
    private static Context apiContext;
    private IsoDep nfcTag = null;

    private NfcApduRunner() {
        super();
    }

    private NfcApduRunner(Context context) {
        super();
        apiContext = context;
    }

    public static void setNfcApduRunner(NfcApduRunner nfcApduRunner) {
        NfcApduRunner.nfcApduRunner = nfcApduRunner;
    }

    public synchronized static  NfcApduRunner getInstance(Context context) throws Exception{
        if (context == null) throw new Exception(ERROR_MSG_NO_CONTEXT);
        if (nfcApduRunner == null || apiContext == null) nfcApduRunner = new NfcApduRunner(context);
        return nfcApduRunner;
    }

    public synchronized static  NfcApduRunner getInstance() {
        if (nfcApduRunner == null) nfcApduRunner = new NfcApduRunner();
        return nfcApduRunner;
    }

    public void setNfcAdapter(NfcAdapter nfcAdapter){
        this.nfcAdapter = nfcAdapter;
    }

    public boolean setCardTag(Intent intent) {
        Tag tag = intent.getParcelableExtra(EXTRA_TAG);
        if (tag != null) {
            nfcTag = IsoDep.get(tag);
            return true;
        }
        return false;
    }

    public void setCardTag(IsoDep nfcTag){
        this.nfcTag = nfcTag;
    }

    public void connect() throws Exception {
        if (nfcAdapter == null)
            nfcAdapter = NfcAdapter.getDefaultAdapter(apiContext);
        if (nfcAdapter == null) {
            throw new Exception(ERROR_MSG_NO_NFC);
        } else if (!nfcAdapter.isEnabled()) {
            throw new Exception(ERROR_MSG_NFC_DISABLED);
        } else if (nfcTag == null) {
            throw new Exception(ERROR_MSG_NO_TAG);
        }
        try {
            if (!nfcTag.isConnected()) {
                nfcTag.connect();
                nfcTag.setTimeout(TIME_OUT);
            }
        } catch (IOException e) {
           // e.printStackTrace();
            throw new Exception(ERROR_MSG_NFC_CONNECT);
        }
    }

    @Override
    public void disconnectCard() throws Exception {
        if (nfcTag == null) {
            throw new Exception(ERROR_MSG_NO_TAG);
        }
        try {
            nfcTag.close();
        } catch (IOException e) {
            //e.printStackTrace();
            throw new Exception(ERROR_MSG_COMM_DISCONNECT + ", more details: " + e.getMessage());
        }
    }

    @Override
    public RAPDU transmitCommand(CAPDU commandAPDU) throws  Exception {
        return new RAPDU(transceive(commandAPDU.getBytes()));
    }

    private byte[] transceive(byte[] apduCommandBytes) throws Exception{
        connect();
        if (nfcTag == null) {
            throw new Exception(ERROR_MSG_NO_TAG);
        }
        nfcTag.setTimeout(TIME_OUT);
        byte[] response;
        try {
            response = nfcTag.transceive(apduCommandBytes);
        } catch (IOException e) {
            //Log.e("NfcComm", e.getMessage());
            throw new Exception(EXCP_COMM_TRANSCEIVE);
        }
        if (response == null || response.length <= 1) {
            throw new Exception(ERROR_RECV_DATA);
        }
        return response;
    }
}