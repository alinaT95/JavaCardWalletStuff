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
    
    private final static byte MAX_WRITE_PIN_INPUT_SIZE = (byte) (1 + PIN_MAX_SIZE);

    // public final static short SW_PIN_IS_NOT_INITIALIZED = (short) 0x9C0B;

    // PIN and PUK objects, allocated on demand
    private static OwnerPIN unblockingPin;
    
    private static short maxPinTries = (short) 10;
    
    private static short numPinFails = (short) 0;
    
    //public PinHandler (short numPinFails){
    	//this.numPinFails = numPinFails;
    //}
    

    /**
     *
     * This command should be called only once during applet personalization
     *
     * CLA: 0xB0
     *
     * INS: 0x40
     *
     * P1: pin tries number
     *
     * P2: 0x00
     *
     * Lc: Length of input Data
     *
     * Data: [PIN_size (1 byte) | PIN | UNBLOCK_PIN_CODE_size (1 byte) | UNBLOCK_PIN_CODE]
     *  (here | means concatenation)

     */

    public static void writeUnblockingPin(APDU apdu)  {
        byte[] buffer = apdu.getBuffer();
        
        UserInterface.showWriteUnblockingPin();

        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead || numBytes > MAX_WRITE_PIN_INPUT_SIZE ){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        //byte unblockingPinSize = buffer[(short) (ISO7816.OFFSET_CDATA)];
        unblockingPin = new OwnerPIN(MIN_PIN_TRIES_NUM, PIN_MAX_SIZE);
        unblockingPin.update(buffer, (byte) (ISO7816.OFFSET_CDATA), (byte)numBytes);
    }
    
    /**
     *
     * This command should be called only once during applet personalization
     *
     *
        CLA: 0xB0
        INS: 0x50
        P1: numPinFails
        P2: 0x00
        Lc: 0x00
        Data: PIN
     */
    
    public static void writeMaxPinTries (APDU apdu)  {
    	byte[] buffer = apdu.getBuffer();

        //short byteRead = apdu.setIncomingAndReceive();
        
        UserInterface.showWriteMaxPinTries();
        
        byte numTries = buffer[ISO7816.OFFSET_P1];

        if (numTries < MIN_PIN_TRIES_NUM || numTries > MAX_PIN_TRIES_NUM)
            ISOException.throwIt(Constants.SW_INVALID_PARAMETER);
            
        numPinFails = numTries;
    }
    
    public static void showPinTable() {
	    CoinManager.showNineGrids ();
    }
    
    /**
     * This function verifies a PIN number sent in the Data field.
     * Multiple consecutive unsuccessful PIN verifications will block the PIN. If a PIN
     * blocks, then an UnblockPIN command can be issued.
     *
        CLA: 0xB0
        INS: 0xA2
        P1: 0x00
        P2: 0x00
        Lc: PIN size
        Data: PIN
     */
    
    public static boolean checkPin(APDU apdu) {
    	byte[] buf = apdu.getBuffer();
    	short lc = apdu.setIncomingAndReceive();
        short lcOff = apdu.getOffsetCdata();
        
        UserInterface.showCheckPin();
        
        
        short result = CoinManager.verifyPIN (CoinManager.PIN_MODE_9GRIDS, buf, lcOff, Constants.PIN_LENGTH);
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
     * This function unblocks a PIN using the UNBLOCK_PIN_CODE specified in the Data field.
     *
     CLA: 0xB0
     INS: 0x70
     P1: 0x00
     P2: 0x00
     Lc: UNBLOCK_PIN_CODE size
     Data: UNBLOCK_PIN_CODE
     */
    public static void unblockPin(APDU apdu) {
        byte[] buffer = apdu.getBuffer();

        // validateApduParamsAndPins(buffer);

        // If the PIN is not blocked, the call is inconsistent
        if (numPinFails<=maxPinTries)
            ISOException.throwIt(Constants.SW_OPERATION_NOT_ALLOWED);

        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead)
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

        // if (!checkPinPolicy(numBytes))
            // ISOException.throwIt(Constants.SW_INVALID_PARAMETER);

        if (!unblockingPin.check(buffer, ISO7816.OFFSET_CDATA, (byte) numBytes))
            ISOException.throwIt(Constants.SW_AUTH_FAILED);
            
        numPinFails = (short) 0;
        GPSystem.setCardContentState(Constants.APP_PERSONALIZED);
    }
    
}

