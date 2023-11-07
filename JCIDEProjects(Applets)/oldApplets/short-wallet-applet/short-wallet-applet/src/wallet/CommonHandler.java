package wallet ;

import javacard.framework.*;

import org.globalplatform.GPSystem;

public class CommonHandler  
{

	public static void finishPersonalization(APDU apdu) {
//        byte[] buffer = apdu.getBuffer();
//        byte p1 = buffer[ISO7816.OFFSET_P1];
//
//        if(p1 == (byte) 0){
//            GPSystem.setCardContentState(Constants.APP_DEVELOPER_MODE);
//        }
//        else {
            GPSystem.setCardContentState(Constants.APP_PERSONALIZED);
//        }
    }


    public static void getAppInfo(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte outputSize = (byte) 1;
        apdu.setOutgoing();
        apdu.setOutgoingLength(outputSize);
        //buffer[0] = APP_VERSION_MAJOR;
        //buffer[1] = APP_VERSION_MINOR;
        buffer[0] = GPSystem.getCardContentState();
        apdu.sendBytes((short) 0, outputSize);
    }

}
