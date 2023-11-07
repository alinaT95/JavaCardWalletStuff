package wallet;

import javacard.framework.*;

import org.globalplatform.GPSystem;

public class CommonHandler {

    public static void finishPersonalization(APDU apdu) {
    	GPSystem.setCardContentState(Constants.APP_PERSONALIZED);
    }

    public static void getAppInfo(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte outputSize = (byte) 1;
        apdu.setOutgoing();
        apdu.setOutgoingLength(outputSize);
        buffer[0] = GPSystem.getCardContentState();
        apdu.sendBytes((short) 0, outputSize);
    }
}
