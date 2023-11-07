package wallet ;

import javacard.framework.*;
import org.globalplatform.GPSystem;
import com.ftsafe.javacardx.btcmgr.CoinManager;

public class WalletApplet extends Applet
{
		// code of CLA byte in the command APDU header
    private final static byte WALLET_APPLET_CLA = (byte) 0xB0;

    /****************************************
     * Instruction codes *
     ****************************************
     */
    //Personalization
    private static final byte INS_WRITE_UNBLOCKING_PIN = (byte)0x10;
    private static final byte INS_WRITE_MAX_PIN_TRIES = (byte)0x20;
    
    private static final byte INS_FINISH_PERS = (byte)0x90;
    
    // Main mode
    private static final byte INS_GET_PUBLIC_KEY = (byte)0xA0;
    private static final byte INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH  = (byte)0xA7;
    private static final byte INS_SHOW_PIN_TABLE = (byte)0xA1;
    private static final byte INS_VERIFY_PIN = (byte)0xA2;
    

    private static final byte INS_SIGN_SHORT_MESSAGE = (byte)0xA3;
    private static final byte INS_SIGN_STORED_MESSAGE = (byte)0xA4;
    private static final byte INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH = (byte)0xA5;
    private static final byte INS_SIGN_STORED_MESSAGE_WITH_DEFAULT_PATH = (byte)0xA6;
    
    private static final byte INS_SET_DATA_FOR_SIGNING = (byte)0xC0;
    private static final byte INS_GET_APP_INFO = (byte)0xC1;
    
    //For developer mode
    private static final byte INS_VERIFY_SIGNATURE = (byte)0xD0;
    private static final byte INS_VERIFY_SIGNATURE_WITH_DEFAULT_PATH = (byte)0xD1;

    //Blocked mode
    private static final byte INS_UNBLOCK_PIN = (byte)0xF0;


    private CryptoHandler cryptoHandler;
    //private PinHandler pinHandler;

	public WalletApplet(){
        cryptoHandler = new TonCryptoHandler();
        //pinHandler = new PinHandler((short) 5);
    }

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new WalletApplet().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet()){
			short ret = CoinManager.setCurve(CoinManager.CURVE_ED25519); 
	    
			if (ret != CoinManager.SEC_TRUE){
				ISOException.throwIt(Constants.SW_SET_CURVE_FAILED);
			}
				
			return;	
		}
            

        byte[] buffer = apdu.getBuffer();
        byte cla = (byte) (buffer[ISO7816.OFFSET_CLA] & 0xFF);
        byte ins = (byte) (buffer[ISO7816.OFFSET_INS] & 0xFF);

        // check SELECT APDU command
        if ((cla == 0x00) && (ins == (byte) 0xA4)){
	        return;
        }
            

        if (cla != WALLET_APPLET_CLA)
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);


        switch (GPSystem.getCardContentState()) {
            case Constants.APP_INSTALLED: {
                switch (ins) {
                    case INS_WRITE_MAX_PIN_TRIES:
                    	PinHandler.writeMaxPinTries(apdu);
                        break;
                    case INS_WRITE_UNBLOCKING_PIN:
                    	PinHandler.writeUnblockingPin(apdu);
                        break;
                    case INS_FINISH_PERS:
                        CommonHandler.finishPersonalization(apdu);
                        break;
                    case INS_GET_APP_INFO:
                        CommonHandler.getAppInfo(apdu);
                        break;
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
                break;
            }
            case Constants.APP_DEVELOPER_MODE: {
                switch (ins) {    
                    case INS_GET_PUBLIC_KEY:
                        cryptoHandler.getPublicKey(apdu);
                        break;
                    case INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH:
                        cryptoHandler.getPublicKeyWithDefaultHDPath(apdu);
                        break;
                    case INS_SHOW_PIN_TABLE:
                        PinHandler.showPinTable();
                        break;
                    case INS_SET_DATA_FOR_SIGNING:
                        cryptoHandler.setDataForSigning(apdu);
                        break;
                    case INS_SIGN_SHORT_MESSAGE:
                    	cryptoHandler.signShortMessage(apdu);
                        break;
                    case INS_SIGN_STORED_MESSAGE:
                    	cryptoHandler.signStoredMessage(apdu);
                        break;
                    case INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH:
                    	cryptoHandler.signShortMessageWithDefaultPath(apdu);
                        break;
                    case INS_SIGN_STORED_MESSAGE_WITH_DEFAULT_PATH:
                    	cryptoHandler.signStoredMessageWithDefaultPath(apdu);
                        break;   
                        
                    
                    case INS_VERIFY_SIGNATURE:
                        cryptoHandler.verifySignature(apdu);
                        break;
                    case INS_VERIFY_SIGNATURE_WITH_DEFAULT_PATH:
                        cryptoHandler.verifySignatureWithDefaultPath(apdu);
                        break;
                    case INS_VERIFY_PIN:
                    	if(!PinHandler.checkPin(apdu)) {
                    		GPSystem.setCardContentState(Constants.APP_BLOCKED);
                    		ISOException.throwIt(Constants.SW_PIN_TRIES_EXPIRED);
                    	}
                        break;
                    case INS_GET_APP_INFO:
                        CommonHandler.getAppInfo(apdu);
                        break;
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
                break;
            }
            case Constants.APP_PERSONALIZED: {
                switch (ins) {              
                    case INS_GET_PUBLIC_KEY:
                        cryptoHandler.getPublicKey(apdu);
                        break;
                    case INS_SHOW_PIN_TABLE:
                        PinHandler.showPinTable();
                        break;
                    case INS_SET_DATA_FOR_SIGNING:
                        cryptoHandler.setDataForSigning(apdu);
                        break;
                    case INS_SIGN_SHORT_MESSAGE:
                    	cryptoHandler.signShortMessage(apdu);
                        break;
                    case INS_SIGN_STORED_MESSAGE:
                    	cryptoHandler.signStoredMessage(apdu);
                        break;
                    case INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH:
                    	cryptoHandler.signShortMessageWithDefaultPath(apdu);
                        break;
                    case INS_SIGN_STORED_MESSAGE_WITH_DEFAULT_PATH:
                    	cryptoHandler.signStoredMessageWithDefaultPath(apdu);
                        break;
                    case INS_VERIFY_PIN:
                    	if(!PinHandler.checkPin(apdu)) {
                    		GPSystem.setCardContentState(Constants.APP_BLOCKED);
                    		ISOException.throwIt(Constants.SW_PIN_TRIES_EXPIRED);
                    	}
                        break;
                    case INS_GET_APP_INFO:
                        CommonHandler.getAppInfo(apdu);
                        break;
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
                break;
            }
            case Constants.APP_BLOCKED: {
                switch (ins) {
                    case INS_UNBLOCK_PIN:
                         PinHandler.unblockPin(apdu);
                         break;
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
                break;
            }
            default:
                ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
	}

}

