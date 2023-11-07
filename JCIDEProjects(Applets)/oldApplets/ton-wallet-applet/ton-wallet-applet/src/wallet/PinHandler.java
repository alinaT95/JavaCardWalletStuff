package wallet ;

import javacard.framework.*;
import org.globalplatform.GPSystem;


public class PinHandler  
{
	private final static byte MIN_PIN_TRIES_NUM = (byte) 3;

    private final static byte MAX_PIN_TRIES_NUM = (byte) 10;

    // Minimum PIN size
    private final static byte PIN_MIN_SIZE = (byte) 4;

    // Maximum PIN size
    private final static byte PIN_MAX_SIZE = (byte) 16;

    private final static byte MAX_WRITE_PIN_INPUT_SIZE = 2 * PIN_MAX_SIZE + 2;

    private final static byte MIN_WRITE_PIN_INPUT_SIZE = 2 * PIN_MIN_SIZE + 2;

    public final static short SW_PIN_IS_NOT_INITIALIZED = (short) 0x9C0B;

    // PIN[0] initial value...
    private final static byte[] PIN_INIT_VALUE = {0, 0, 0, 0};

    // PIN and PUK objects, allocated on demand
    private static OwnerPIN pin, unblockingPin;

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

    public static void writePin(APDU apdu)  {
        byte[] buffer = apdu.getBuffer();

        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead || numBytes > MAX_WRITE_PIN_INPUT_SIZE || numBytes < MIN_WRITE_PIN_INPUT_SIZE){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        byte numTries = buffer[ISO7816.OFFSET_P1];

        if (numTries < MIN_PIN_TRIES_NUM || numTries > MAX_PIN_TRIES_NUM)
            ISOException.throwIt(Constants.SW_INVALID_PARAMETER);

        byte pinSize = buffer[ISO7816.OFFSET_CDATA];

        if (!checkPinPolicy(pinSize))
            ISOException.throwIt(Constants.SW_INVALID_PARAMETER);

        byte unblockingPinSize = buffer[(short) (ISO7816.OFFSET_CDATA + 1 + pinSize)];

        if (!checkPinPolicy(unblockingPinSize))
            ISOException.throwIt(Constants.SW_INVALID_PARAMETER);

        pin = new OwnerPIN(numTries, PIN_MAX_SIZE);

        pin.update(buffer, (short) (ISO7816.OFFSET_CDATA + 1), pinSize);

        unblockingPin = new OwnerPIN(MIN_PIN_TRIES_NUM, PIN_MAX_SIZE);

        unblockingPin.update(buffer, (byte) (ISO7816.OFFSET_CDATA + 1 + pinSize + 1), unblockingPinSize);
    }

    /**
     * This function verifies a PIN number sent in the Data field.
     * Multiple consecutive unsuccessful PIN verifications will block the PIN. If a PIN
     * blocks, then an UnblockPIN command can be issued.
     *
        CLA: 0xB0
        INS: 0x50
        P1: 0x00
        P2: 0x00
        Lc: PIN size
        Data: PIN
     */
    public static void verifyPin(APDU apdu) {
        byte[] buffer = apdu.getBuffer();

        validateApduParamsAndPins(buffer);

        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead)
            ISOException.throwIt(Constants.SW_INVALID_PARAMETER);

        if (!checkPinPolicy(numBytes))
            ISOException.throwIt(Constants.SW_INVALID_PARAMETER);

        if (pin.getTriesRemaining() == (byte) 0x00) {
            GPSystem.setCardContentState(Constants.APP_BLOCKED);
            ISOException.throwIt(Constants.SW_IDENTITY_BLOCKED);
        }

        if (!pin.check(buffer, (short) ISO7816.OFFSET_CDATA, (byte) numBytes))
            ISOException.throwIt(Constants.SW_AUTH_FAILED);
    }

    /**
     * This function changes a PIN code. The Data field contains both the old and
     * the new PIN codes.
        CLA: 0xB0
        INS: 0x60
        P1: 0x00
        P2: 0x00
        Lc: Length of data
        Data: oldPinSize | oldPin | newPinSize | newPin
     */
    public static void changePin(APDU apdu) {
        byte[] buffer = apdu.getBuffer();

        validateApduParamsAndPins(buffer);

        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead || numBytes > MAX_WRITE_PIN_INPUT_SIZE || numBytes < MIN_WRITE_PIN_INPUT_SIZE){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        byte pinSize = buffer[ISO7816.OFFSET_CDATA];

        if (!checkPinPolicy(pinSize))
            ISOException.throwIt(Constants.SW_INVALID_PARAMETER);

        byte newPinSize = buffer[(short) (ISO7816.OFFSET_CDATA + 1 + pinSize)];

        if (!checkPinPolicy(newPinSize))
            ISOException.throwIt(Constants.SW_INVALID_PARAMETER);

        if (pin.getTriesRemaining() == (byte) 0x00) {
            GPSystem.setCardContentState(Constants.APP_BLOCKED);
            ISOException.throwIt(Constants.SW_IDENTITY_BLOCKED);
        }

        if (!pin.check(buffer, (short) (ISO7816.OFFSET_CDATA + 1), pinSize)) {
            ISOException.throwIt(Constants.SW_AUTH_FAILED);
        }

        pin.update(buffer, (short) (ISO7816.OFFSET_CDATA + 1 + pinSize + 1), newPinSize);
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

        validateApduParamsAndPins(buffer);

        // If the PIN is not blocked, the call is inconsistent
        if (pin.getTriesRemaining() != 0)
            ISOException.throwIt(Constants.SW_OPERATION_NOT_ALLOWED);

        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead)
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

        if (!checkPinPolicy(numBytes))
            ISOException.throwIt(Constants.SW_INVALID_PARAMETER);

        if (!unblockingPin.check(buffer, ISO7816.OFFSET_CDATA, (byte) numBytes))
            ISOException.throwIt(Constants.SW_AUTH_FAILED);

        pin.resetAndUnblock();
        GPSystem.setCardContentState(Constants.APP_PERSONALIZED);
    }

    // Checks if PIN policies are satisfied for a PIN code
    private static boolean checkPinPolicy(short pinSize) {
        return !((pinSize < PIN_MIN_SIZE) || (pinSize > PIN_MAX_SIZE));
    }
    
    public static void checkPinWithoutReset(){
        if (!pin.isValidated())
            ISOException.throwIt(Constants.SW_UNAUTHORIZED);
    }
    
    public static void resetPin(){
	    pin.reset();
    }

    public static void checkPin(){
        checkPinWithoutReset();
        resetPin();
    }

    private static void validateApduParamsAndPins(byte[] buffer){
        if (pin == null || unblockingPin == null)
            ISOException.throwIt(SW_PIN_IS_NOT_INITIALIZED);
        if (buffer[ISO7816.OFFSET_P1] != (byte) 0x00)
            ISOException.throwIt(Constants.SW_INCORRECT_P1);
        if (buffer[ISO7816.OFFSET_P2] != (byte) 0x00)
            ISOException.throwIt(Constants.SW_INCORRECT_P2);
    }
}
