package com.tonnfccardreactnativetest.api.nfc;

import android.nfc.tech.IsoDep;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;



import static org.junit.Assert.*;

public class NfcApduRunnerTest {

    private NfcApduRunner nfcApduRunner;

    @Before
    public void init() {
        nfcApduRunner = NfcApduRunner.getInstance(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void transmitCommandTest1() {



    }



    @Test
    public void disconnectCardTestForNullTag() throws Exception {
        IsoDep tag = null;
        nfcApduRunner.setCardTag(tag);
        try {
            nfcApduRunner.disconnectCard();
        }
        catch (Exception e) {
            assertEquals(NfcApduRunner.ERROR_MSG_NO_TAG, e.getMessage());
        }
    }

    @Test
    public void disconnectCardTestForDisconnectError() throws Exception {
        IsoDep tag = mock(IsoDep.class);
        nfcApduRunner.setCardTag(tag);
        try {
            nfcApduRunner.disconnectCard();
        }
        catch (Exception e) {
            assertEquals(NfcApduRunner.ERROR_MSG_NO_TAG, e.getMessage());
        }
    }

}