package wallet ;

import javacard.framework.*;

import org.globalplatform.GPSystem;

public class CommonHandler  
{
	private static byte APP_INFO_OUTPUT_SIZE = 0x01;
	
    /*
    This command finishes personalization and changes the state of applet depending on 
    param.  Precondition:  WRITE_UNBLOCKING_PIN and WRITE_MAX_PIN_TRIES should be called.
    
    CLA: 0xB0
	INS: 0x90
	P1: 0x00 or 0x01
	P2: 0x00
    */
    public static void finishPersonalization(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte p1 = buffer[ISO7816.OFFSET_P1];
        if(p1 == (byte) 0x00){
            GPSystem.setCardContentState(Constants.APP_DEVELOPER_MODE);
        }
        else {
            GPSystem.setCardContentState(Constants.APP_PERSONALIZED);
        }
    }

	/*
	This command returns the code of applet state.
	
	CLA: 0xB0
	INS: 0xC1
	P1: 0x00 
	P2: 0x00
	Le: 0x01
	*/

    public static void getAppInfo(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
       
        short le = apdu.setOutgoing();
        if (le != APP_INFO_OUTPUT_SIZE) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        apdu.setOutgoingLength(APP_INFO_OUTPUT_SIZE);
        buffer[0] = GPSystem.getCardContentState();
        apdu.sendBytes((short) 0x00, APP_INFO_OUTPUT_SIZE);
    }
}
