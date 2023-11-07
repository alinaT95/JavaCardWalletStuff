package com.tonnfccardreactnativetest.smartcard.wrappers;

import android.util.Log;

import com.tonnfccardreactnativetest.api.utils.ByteArrayHelper;

import java.util.List;

import static com.tonnfccardreactnativetest.api.utils.ByteArrayHelper.*;
import static com.tonnfccardreactnativetest.smartcard.ErrorCodes.*;
import static com.tonnfccardreactnativetest.smartcard.TonWalletAppletConstants.*;
import static com.tonnfccardreactnativetest.smartcard.apdu.CoinManagerApduCommands.*;
import static com.tonnfccardreactnativetest.smartcard.apdu.TonWalletAppletApduCommands.*;

public abstract class ApduRunner {

    private String tag = "ApduRunner";

    public ApduRunner() {}

    public abstract void disconnectCard() throws Exception;

    public abstract RAPDU transmitCommand(CAPDU commandAPDU)  throws Exception;

    private RAPDU sendAPDUList(List<CAPDU> apduList) throws Exception {
        RAPDU result = null;
        for (CAPDU apdu : apduList) {
            result = sendAPDU(apdu);
        }
        return result;
    }

    public RAPDU sendCoinManagerAppletAPDU(CAPDU commandAPDU) throws Exception {
        sendAPDU(SELECT_COIN_MANAGER_APDU);
        return sendAPDU(commandAPDU);
    }

    public RAPDU sendTonWalletAppletAPDU(CAPDU commandAPDU) throws Exception {
        RAPDU response = sendAPDUList(GET_APPLET_STATE_APDU_LIST);
        if (commandAPDU.getIns() == INS_GET_APP_INFO) return response;
        byte appletState = response.getData()[0];
        byte ins = commandAPDU.getIns();
        if (!getStateByIns(ins).contains(appletState))
            throw new Exception("APDU command " + getCommandName(ins) + " is not supported in state " + getStateName(appletState));
        return sendAPDU(commandAPDU);
    }

    public RAPDU sendAPDU(CAPDU commandAPDU) throws Exception {
        if (commandAPDU == null) throw new Exception("APDU command object can not be null.");
        Log.d(tag, "===============================================================");
        Log.d(tag, "===============================================================");
        String apduFormated = hex(commandAPDU.getCla()) + " "
                + hex(commandAPDU.getIns()) + " "
                + hex(commandAPDU.getP1()) + " "
                + hex(commandAPDU.getP2()) + " "
                + hex(commandAPDU.getLc()) + " "
                + hex(commandAPDU.getData()) + " "
                + hex((byte) commandAPDU.getLe()) + " ";
        Log.d(tag, ">>> Send apdu  " + apduFormated);
        if (getCommandName(commandAPDU.getIns()) != null)
            Log.d(tag, "(" + getCommandName(commandAPDU.getIns()) + ")");

        RAPDU responseAPDU = transmitCommand(commandAPDU);

        String sw = hex(new byte[]{responseAPDU.getSW1()})
                + hex(new byte[]{responseAPDU.getSW2()});
        if (getMsg(sw) != null)
            sw += ", " + getMsg(sw);

        if ((responseAPDU.getSW1() != (byte)0x90) || (responseAPDU.getSW2() != (byte)0x00)) {
            throw new Exception("Operation failed (" + sw + "): " + apduFormated);
        }

        StringBuilder msg = new StringBuilder();
        msg.append("SW1-SW2: ").append(sw);

        if (responseAPDU.getData().length > 0)
            msg.append(", bytes: " + ByteArrayHelper.hex(responseAPDU.getData()));

        Log.d(tag, msg.toString());
        Log.d(tag, "===============================================================");

        return responseAPDU;
    }

}
