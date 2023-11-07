package tonwallet ;

import javacard.framework.*;


import com.ftsafe.javacardx.btcmgr.CoinManager;

public class PinHandler  
{

    public static final byte PIN_LENGTH = (byte) 0x04;
    
    private final static byte MAX_PIN_TRIES_NUM = (byte) 10;
 
    private final static byte[] FALSE_PIN = new byte[]{0x00, 0x00, 0x00, 0x00};
    
    private static short numPinFails = (short) 0;
        
    /**
     * This function verifies encoded PIN bytes sent in the data field. 
		Multiple consecutive unsuccessful PIN verifications will block the applet. 
		The limit is defined during personalization of applet by the command WRITE_MAX_PIN_TRIES. 
		If applet is blocked, then UNBLOCK_PIN command can be requested. Now we have PIN size = 4.
		
		This version of applet was written for NFC card. 
		It uses special mode PIN_MODE_FROM_API for entering PIN. 
		PIN  bytes are  given to applet as plain text.

		If PIN code = 5555 plain PIN bytes array = 35353535.
     
        CLA: 0xB0
        INS: 0xA2
        P1: 0x00
        P2: 0x00
        Lc: PIN size
        Data: plain PIN bytes
     */
    
    public static  void checkPin(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
    	
    	short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead || numBytes != PIN_LENGTH){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        checkPin(buffer, ISO7816.OFFSET_CDATA, PIN_LENGTH); 
    }
    
    public static  void checkPin(byte[] buffer, short offset, short len) {              
        short result = CoinManager.verifyPIN(CoinManager.PIN_MODE_FROM_API, 
					buffer, offset, len);
						
        if (result != 0) {
	        numPinFails++;	        
	        if (numPinFails > MAX_PIN_TRIES_NUM) {
		        ISOException.throwIt(Constants.SW_PIN_TRIES_EXPIRED);
	        }	        
		    ISOException.throwIt(Constants.SW_INCORRECT_PIN);     
        }
        else {
	        numPinFails = 0;
        }  /**/  
    }
    
    public static void resetPinFlag(){
	    CoinManager.verifyPIN(CoinManager.PIN_MODE_FROM_API, FALSE_PIN, 
				(short)0x00, PIN_LENGTH);
		numPinFails = 1;
    }    
}


