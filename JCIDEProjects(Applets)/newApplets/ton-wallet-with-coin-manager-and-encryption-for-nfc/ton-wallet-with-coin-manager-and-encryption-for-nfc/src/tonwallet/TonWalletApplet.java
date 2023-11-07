package tonwallet ;

import javacard.framework.*;
import org.globalplatform.GPSystem;
import com.ftsafe.javacardx.btcmgr.CoinManager;

public class TonWalletApplet extends Applet
{
	// code of CLA byte in the command APDU header
    private final static byte WALLET_APPLET_CLA = (byte) 0xB0;

    /****************************************
     * Instruction codes *
     ****************************************
     */
    //Personalization
   /* private static final byte INS_WRITE_UNBLOCKING_PIN = (byte)0x10;
    private static final byte INS_WRITE_MAX_PIN_TRIES = (byte)0x20;*/
    private static final byte INS_SET_MODE = (byte)0x90;
    private static final byte INS_GEN_KEY_PAIR = (byte)0xE1;
    private static final byte INS_INIT_KEYAGREEMENT = (byte)0xE2;
    
    // Main mode
    private static final byte INS_GET_PUBLIC_KEY = (byte)0xA0;
    private static final byte INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH  = (byte)0xA7;
    private static final byte INS_VERIFY_PIN = (byte)0xA2;
    

    private static final byte INS_SIGN_SHORT_MESSAGE = (byte)0xA3;
    private static final byte INS_SIGN_STORED_MESSAGE = (byte)0xA4;
    private static final byte INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH = (byte)0xA5;
    private static final byte INS_SIGN_STORED_MESSAGE_WITH_DEFAULT_PATH = (byte)0xA6;
    
    private static final byte INS_SET_DATA_FOR_SIGNING = (byte)0xC0;
    private static final byte INS_GET_APP_INFO = (byte)0xC1;
    
    private static final byte INS_SET_EXTERNAL_PUB_KEY  = (byte)0xE3;
    private static final byte INS_GET_INNER_PUB_KEY   = (byte)0xE4;
    private static final byte INS_SET_NONCE   = (byte)0xE5;
    private static final byte INS_SET_DATA_FOR_ENCRYPTION   = (byte)0xE6;
    private static final byte INS_ENCRYPT_DATA = (byte)0xE7;
    private static final byte INS_GET_ENCRYPTED_DATA = (byte)0xE8;
    
    // private static final byte INS_GET_MAX_PIN_TRIES = (byte)0xE1;
    // private static final byte INS_GET_PIN_TRIES_LEFT = (byte)0xE2;
    // private static final byte INS_GET_UNBLOCKING_PIN_TRIES_LEFT = (byte)0xE3;
    
    //For developer mode
    private static final byte INS_VERIFY_SIGNATURE = (byte)0xD0;
    private static final byte INS_VERIFY_SIGNATURE_WITH_DEFAULT_PATH = (byte)0xD1;
    private static final byte INS_GET_COMMON_DH_SECRET_DATA = (byte)0xD2;

    //Blocked mode
    // private static final byte INS_UNBLOCK_PIN = (byte)0xF0;


    private CryptoHandler cryptoHandler;
    
    private EncryptionHandler encryptionHandler;
    
    public TonWalletApplet(){
        cryptoHandler = new TonCryptoHandler();
        encryptionHandler = new EncryptionHandler();
    }

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new TonWalletApplet().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
        if ((cla == 0x00) && (ins == (byte) 0xA4))
            return;

        if (cla != WALLET_APPLET_CLA)
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);


        switch (GPSystem.getCardContentState()) {
            case Constants.APP_INSTALLED: {
                switch (ins) {
                    // case INS_WRITE_MAX_PIN_TRIES:
                    	// PinHandler.writeMaxPinTries(apdu);
                        // break;
                    // case INS_WRITE_UNBLOCKING_PIN:
                    	// PinHandler.writeUnblockingPin(apdu);
                        // break;
                    case INS_SET_MODE:
                        CommonHandler.setMode(apdu);
                        break;
                    case INS_GET_APP_INFO:
                        CommonHandler.getAppInfo(apdu);
                        break;
                    case INS_GEN_KEY_PAIR:
                        encryptionHandler.generateKeyPairForDHProtocol();
                        break;
					case INS_INIT_KEYAGREEMENT:
						encryptionHandler.initKeyAgreement();
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
					// case INS_GET_MAX_PIN_TRIES:
						// PinHandler.getMaxPinTries(apdu);
						// break;
					// case INS_GET_PIN_TRIES_LEFT:
						// PinHandler.getLeftPinTries(apdu);
						// break;						
                    case INS_GET_APP_INFO:
                        CommonHandler.getAppInfo(apdu);
                        break;
					case INS_GET_COMMON_DH_SECRET_DATA:
						encryptionHandler.getCommonSecret(apdu);
						break ;
                    case INS_SET_EXTERNAL_PUB_KEY:
						encryptionHandler.setExternalPublicKey(apdu);
						break;
					case INS_GET_INNER_PUB_KEY:
						encryptionHandler.getInnerPublicKeyForDHProtocol(apdu);
						break;
					case INS_SET_NONCE:
						encryptionHandler.setNonce(apdu);
						break;
					case INS_SET_DATA_FOR_ENCRYPTION:
						encryptionHandler.setDataForEncryption(apdu);
						break;
					case INS_ENCRYPT_DATA:
						encryptionHandler.produceAesKeyData();
						encryptionHandler.encryptData();
						break;
					case INS_GET_ENCRYPTED_DATA:
						encryptionHandler.getEncryptedData(apdu);
						break; 
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
                break;
            }
            case Constants.APP_PERSONALIZED: {
                switch (ins) {              
                    // case INS_GET_PUBLIC_KEY:
                        // cryptoHandler.getPublicKey(apdu);
                        // break;
                    case INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH:
                        cryptoHandler.getPublicKeyWithDefaultHDPath(apdu);
                        break;
                    case INS_SET_DATA_FOR_SIGNING:
                        cryptoHandler.setDataForSigning(apdu);
                        break;
                    // case INS_SIGN_SHORT_MESSAGE:
                    	// cryptoHandler.signShortMessage(apdu);
                        // break;
                    // case INS_SIGN_STORED_MESSAGE:
                    	// cryptoHandler.signStoredMessage(apdu);
                        // break;
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
                    // case INS_GET_MAX_PIN_TRIES:
						// PinHandler.getMaxPinTries(apdu);
						// break;
					// case INS_GET_PIN_TRIES_LEFT:
						// PinHandler.getLeftPinTries(apdu);
						// break;		
                    case INS_GET_APP_INFO:
                        CommonHandler.getAppInfo(apdu);
                        break;
					case INS_SET_EXTERNAL_PUB_KEY:
						encryptionHandler.setExternalPublicKey(apdu);
						break; 
					case INS_GET_INNER_PUB_KEY:
						encryptionHandler.getInnerPublicKeyForDHProtocol(apdu);
						break;
					case INS_SET_NONCE:
						encryptionHandler.setNonce(apdu);
						break;
					case INS_SET_DATA_FOR_ENCRYPTION:
						encryptionHandler.setDataForEncryption(apdu);
						break;
					case INS_ENCRYPT_DATA:
						encryptionHandler.produceAesKeyData();
						encryptionHandler.encryptData();
						break;
					case INS_GET_ENCRYPTED_DATA:
						encryptionHandler.getEncryptedData(apdu);
						break; 
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
                break;
            }
            // case Constants.APP_BLOCKED: {
                // switch (ins) {
                    // case INS_UNBLOCK_PIN:
                         // PinHandler.unblockPin(apdu);
                         // break;
                    // case INS_GET_APP_INFO:
                        // CommonHandler.getAppInfo(apdu);
                        // break;
                    // case INS_GET_UNBLOCKING_PIN_TRIES_LEFT:
						// PinHandler.getLeftUnblockingPinTries(apdu);
						// break;	
                    // default:
                        // ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                // }
                // break;
            // }
            default:
                ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
	}

}
