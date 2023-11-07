package tonwallet ;

import javacard.framework.*;

import org.globalplatform.GPSystem;

//import com.ftsafe.javacard.debug.DebugHelper;

public class CommonHandler  
{
	private static byte APP_INFO_OUTPUT_SIZE = 0x01;
	   
    /*
    This command finishes personalization and changes the state of applet depending on 
    param.  
    
    CLA: 0xB0
	INS: 0x90
	P1: 0x00 or 0x01
	P2: 0x00
    */
    public static void setMode(APDU apdu) {
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
	*/

    public static void getAppInfo(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        buffer[0] = GPSystem.getCardContentState();
        apdu.setOutgoingAndSend((short) 0x00, APP_INFO_OUTPUT_SIZE);
    }
    
    public static void getFreeMemory(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short le = apdu.setOutgoing();
        apdu.setOutgoingLength((short) 2);
        Util.setShort(buffer, (short) 0x00, JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT));
        apdu.sendBytes((short) 0, (short) 2);
    }
    
}

