package com.tonnfccardreactnativetest.api.nfc;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.RemoteException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.io.IOException;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(NfcAdapter.class)
public class NfcApduRunnerTest {

   // @Rule
   // public PowerMockRule rule = new PowerMockRule();

    @Test
    public void getInstanceTestNullAdapter() {
        try {
            NfcApduRunner.getInstance(null);
        }
        catch (Exception e){
            assertEquals(e.getMessage(), NfcApduRunner.ERROR_MSG_NO_CONTEXT);
        }
    }

    @Test
    public void disconnectTestNoTag() {
        NfcApduRunner nfcApduRunner = NfcApduRunner.getInstance();
        IsoDep tag = null;
        nfcApduRunner.setCardTag(tag);
        try {
           nfcApduRunner.disconnectCard();
        }
        catch (Exception e){
            assertEquals(e.getMessage(), NfcApduRunner.ERROR_MSG_NO_TAG);
        }
    }

    @Test
    public void disconnectTest() throws IOException {
        IsoDep tag = mock(IsoDep.class);

        Mockito.doThrow(new IOException()).when(tag).close();

        NfcApduRunner nfcApduRunner = NfcApduRunner.getInstance();
        nfcApduRunner.setCardTag(tag);
        try {
            nfcApduRunner.disconnectCard();
        }
        catch (Exception e){
            e.printStackTrace();
            assertTrue(e.getMessage().contains(NfcApduRunner.ERROR_MSG_COMM_DISCONNECT));
            return;
        }
        fail();
    }

    @Test
    public void connectTestNoNfc() {
        mockStatic(NfcAdapter.class);
        when(NfcAdapter.getDefaultAdapter(any()))
                .thenReturn(null);
        NfcApduRunner nfcApduRunner = NfcApduRunner.getInstance();
        try {
            nfcApduRunner.connect();
        }
        catch (Exception e){
            assertEquals(e.getMessage(), NfcApduRunner.ERROR_MSG_NO_NFC);
        }
    }

    @Test
    public void connectTestNfcDisabled() {
        NfcAdapter nfcAdapterMock = mock(NfcAdapter.class);
        when(nfcAdapterMock.isEnabled())
                .thenReturn(false);
        NfcApduRunner nfcApduRunner = NfcApduRunner.getInstance();
        nfcApduRunner.setNfcAdapter(nfcAdapterMock);
        try {
            nfcApduRunner.connect();
        }
        catch (Exception e){
            assertEquals(e.getMessage(), NfcApduRunner.ERROR_MSG_NFC_DISABLED);
        }
    }

    @Test
    public void connectTestNoTag() {
        NfcAdapter nfcAdapterMock = mock(NfcAdapter.class);
        when(nfcAdapterMock.isEnabled())
                .thenReturn(true);
        NfcApduRunner nfcApduRunner = NfcApduRunner.getInstance();
        nfcApduRunner.setNfcAdapter(nfcAdapterMock);
        IsoDep tag = null;
        nfcApduRunner.setCardTag(tag);
        try {
            nfcApduRunner.connect();
        }
        catch (Exception e){
            assertEquals(e.getMessage(), NfcApduRunner.ERROR_MSG_NO_TAG);
        }
    }

    @Test
    public void connectTestTagConnectError() throws IOException {
        NfcAdapter nfcAdapterMock = mock(NfcAdapter.class);
        when(nfcAdapterMock.isEnabled())
                .thenReturn(true);
        NfcApduRunner nfcApduRunner = NfcApduRunner.getInstance();
        nfcApduRunner.setNfcAdapter(nfcAdapterMock);

        IsoDep tag = mock(IsoDep.class);
        when(tag.isConnected()).thenReturn(false);
        Mockito.doThrow(new IOException()).when(tag).connect();
        nfcApduRunner.setCardTag(tag);
        try {
            nfcApduRunner.connect();
        }
        catch (Exception e){
            assertEquals(e.getMessage(), NfcApduRunner.ERROR_MSG_NFC_CONNECT);
        }
    }

    @Test
    public void connectTestSetTimeOut() throws IOException, RemoteException {
        NfcAdapter nfcAdapterMock = mock(NfcAdapter.class);
        when(nfcAdapterMock.isEnabled())
                .thenReturn(true);
        NfcApduRunner nfcApduRunner = NfcApduRunner.getInstance();
        nfcApduRunner.setNfcAdapter(nfcAdapterMock);

        Tag.CONTENTS_FILE_DESCRIPTOR.

        Tag tag = new Tag();


                createMockTag(byte[] id, int[] techList, Bundle[] techListExtras)//mock(Tag.class);
        IsoDep isodep = IsoDep.get(tag);
        IsoDep mockedIsoDep = Mockito.spy(isodep);


        when(mockedIsoDep.isConnected()).thenReturn(false);
        Mockito.doNothing().when(mockedIsoDep).connect();

        nfcApduRunner.setCardTag(mockedIsoDep);
        try {
            nfcApduRunner.connect();
        }
        catch (Exception e){
            e.printStackTrace();
            fail();
        }
        System.out.println(isodep.getTimeout());
        assertEquals(isodep.getTimeout(), NfcApduRunner.TIME_OUT);
    }

    /*
    PrintStream realSystemOut = System.out;
    realSystemOut.println("XXX");

    PrintStream mockedSystemOut = Mockito.spy(realSystemOut);
    Mockito.doNothing().when(mockedSystemOut).println(Mockito.anyString());
    mockedSystemOut.println("YYY");
     */

}