package wallet ;

import javacard.framework.*;
import org.globalplatform.GPSystem;

import com.ftsafe.javacardx.btcmgr.CoinManager;

public class PinHandler  
{
	private final static byte MIN_PIN_TRIES_NUM = (byte) 3;

    private final static byte MAX_PIN_TRIES_NUM = (byte) 10;

    // Minimum PIN size
    private final static byte PIN_MIN_SIZE = (byte) 4;

    // Maximum PIN size
    private final static byte PIN_MAX_SIZE = (byte) 16;
    
    private static OwnerPIN unblockingPin;
    
    private static short maxPinTries = (short) 10;
    
    private static short numPinFails = (short) 0;
    


    /**
     *
     * This command should be called only once during applet personalization
     It sets UNBLOCK_PIN for applet unblocking.
     UNBLOCK_PIN byte array has size  4 bytes and  16 bytes.
     
     CLA: 0xB0
     INS: 0x10
     P1: 0x00
     P2: 0x00
     Lc: UNBLOCK_PIN size
     Data: UNBLOCK_PIN bytes

     */

    public static void writeUnblockingPin(APDU apdu)  {
        byte[] buffer = apdu.getBuffer();
        
        UserInterface.showWriteUnblockingPin();

        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead || numBytes > PIN_MAX_SIZE || numBytes < PIN_MIN_SIZE){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        unblockingPin = new OwnerPIN(MIN_PIN_TRIES_NUM, PIN_MAX_SIZE);
        unblockingPin.update(buffer, ISO7816.OFFSET_CDATA, (byte) numBytes);
    }
    
    /**
     *
     * This command should be called only once during applet personalization.
      In param P1 we specify the maximum number of consecutive unsuccessful PIN verifications.
      Now this number should be  3 and 10.
        
        CLA: 0xB0
        INS: 0x20
        P1:  number of tries 
        P2: 0x00
     */
    
    public static void writeMaxPinTries(APDU apdu)  {
    	byte[] buffer = apdu.getBuffer();

        //short byteRead = apdu.setIncomingAndReceive();
        
        UserInterface.showWriteMaxPinTries();
        
        byte numTries = buffer[ISO7816.OFFSET_P1];

        if (numTries < MIN_PIN_TRIES_NUM || numTries > MAX_PIN_TRIES_NUM)
            ISOException.throwIt(Constants.SW_INVALID_PARAMETER);
            
        maxPinTries = numTries;
    }
    
    
    /**
     * It shows random 3x3 layout on screen with pin bytes locations, see VERIFY_PIN for details. 
     
		CLA: 0xB0
		INS: 0xA1
		P1: 0x00
		P2: 0x00
     */
    public static void showPinTable() {
	    CoinManager.showNineGrids ();
    }
    
    /**
     * This function verifies encoded PIN bytes sent in the data field. 
		Multiple consecutive unsuccessful PIN verifications will block the applet. 
		The limit is defined during personalization of applet by the command WRITE_MAX_PIN_TRIES. 
		If applet is blocked, then UNBLOCK_PIN command can be requested. Now we have PIN size = 4.
		
		This version of applet was written for Jubiter card with screen. 
		It uses special mode PIN_MODE_9GRIDS for entering PIN. 
		PIN  byte are not given to applet as plain text.

		Before calling VERIFY_PIN you should call APDU command SHOW_PIN_TABLE. 
		You will see a random 3x3 layout on screen with pin bytes locations. for example: 

		1 3 8

		4 9 2

		6 5 7

		If PIN code = 5555 in this example you should encode it 
		into 8888 and send encoded PIN bytes to applet.
     
        CLA: 0xB0
        INS: 0xA2
        P1: 0x00
        P2: 0x00
        Lc: PIN size
        Data: encoded PIN bytes
     */
    
    public static boolean checkPin(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
    	
    	short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead || numBytes != Constants.PIN_LENGTH){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        UserInterface.showCheckPin();
        
        short result = CoinManager.verifyPIN(CoinManager.PIN_MODE_9GRIDS, buffer, ISO7816.OFFSET_CDATA, Constants.PIN_LENGTH);
        if (result != 0) {
	        numPinFails++;
	        
	        if (numPinFails > maxPinTries) {
		        return false;
	        }
	        else {
		        ISOException.throwIt(Constants.SW_INCORRECT_PIN);
	        }
        }
        else {
	        numPinFails = 0;
        }
        UserInterface.showMainUI(true);
        return true;
        
    }

    

    /**
     * This function unblocks applet using the UNBLOCK_PIN bytes specified in the data field.
     UNBLOCK_PIN byte array has size  4 bytes and  16 bytes.
     UNBLOCK_PIN bytes are given to applet as plaintext. 
     
     CLA: 0xB0
     INS: 0xF0
     P1: 0x00
     P2: 0x00
     Lc: UNBLOCK_PIN size
     Data: UNBLOCK_PIN bytes
     */
    public static void unblockPin(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        
        // If the PIN is not blocked, the call is inconsistent
        if (numPinFails <= maxPinTries)
            ISOException.throwIt(Constants.SW_OPERATION_NOT_ALLOWED);

        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead || numBytes > PIN_MAX_SIZE || numBytes < PIN_MIN_SIZE)
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

        if (!unblockingPin.check(buffer, ISO7816.OFFSET_CDATA, (byte) numBytes))
            ISOException.throwIt(Constants.SW_AUTH_FAILED);
            
        numPinFails = (short) 0x00;
        GPSystem.setCardContentState(Constants.APP_PERSONALIZED);
    }
    
}

