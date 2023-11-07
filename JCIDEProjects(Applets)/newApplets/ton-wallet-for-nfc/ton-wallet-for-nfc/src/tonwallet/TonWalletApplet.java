package tonwallet ;

import javacard.framework.*;
import org.globalplatform.GPSystem;
import com.ftsafe.javacardx.btcmgr.CoinManager;

import javacardx.crypto.*;
import javacard.security.*;;

public class TonWalletApplet extends Applet
{
	// code of CLA byte in the command APDU header
    private final static byte WALLET_APPLET_CLA = (byte) 0xB0;

    /****************************************
     * Instruction codes *
     ****************************************
     */
    //Personalization
    private static final byte INS_SET_MODE = (byte)0x90;
    private static final byte INS_SET_PASSWORD_FOR_CARD_AUTHENTICATION = (byte)0x91;
    private static final byte INS_GEN_KEY_PAIR = (byte)0xE1;
    private static final byte INS_INIT_KEYAGREEMENT = (byte)0xE2;
    
    // Main mode
    private static final byte INS_GET_PUBLIC_KEY = (byte)0xA0;
    private static final byte INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH  = (byte)0xA7;
    private static final byte INS_VERIFY_PIN = (byte)0xA2;
    
    private static final byte INS_SET_KEYSHARE = (byte)0xB1;
    private static final byte INS_GET_KEYSHARE_LEN = (byte)0xB2;
    private static final byte INS_GET_KEYSHARE = (byte)0xB3;    


    private static final byte INS_SIGN_SHORT_MESSAGE = (byte)0xA3;
    private static final byte INS_SIGN_STORED_MESSAGE = (byte)0xA4;
    private static final byte INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH = (byte)0xA5;
    private static final byte INS_SIGN_STORED_MESSAGE_WITH_DEFAULT_PATH = (byte)0xA6;
    
    private static final byte INS_SET_DATA_FOR_SIGNING = (byte)0xC0;
    private static final byte INS_GET_APP_INFO = (byte)0xC1;
    
    private static final byte INS_VERIFY_PASSWORD = (byte)0x92;       
       
    private static final byte INS_SET_EXTERNAL_PUB_KEY  = (byte)0xE3;
    private static final byte INS_GET_INNER_PUB_KEY   = (byte)0xE4;
    private static final byte INS_GENERATE_COMMON_DH_SECRET   = (byte)0xE5;
    private static final byte INS_SET_NONCE   = (byte)0xE6;
    private static final byte INS_SET_DATA   = (byte)0xE7;
    private static final byte INS_ENCRYPT_DATA = (byte)0xE8;
    private static final byte INS_SET_MAC_FROM_HOST = (byte)0xE9;
    private static final byte INS_DECRYPT_DATA = (byte)0xEA;
    
    //For developer mode
    private static final byte INS_VERIFY_SIGNATURE = (byte)0xD0;
    private static final byte INS_VERIFY_SIGNATURE_WITH_DEFAULT_PATH = (byte)0xD1;   
    private static final byte INS_GET_FREE_MEMORY_VOL = (byte)0xF1;  

    private CryptoHandler cryptoHandler;  
    private AuthenticatedEncryptionHandler  encryptionHandler;
    private KeyChainHandler keyChainHandler;
    private CardAuthenticationHandler cardAuthenticationHandler;
     
    
    public TonWalletApplet(){
        cryptoHandler = new TonCryptoHandler();
        encryptionHandler = new AuthenticatedEncryptionHandler();
        keyChainHandler = new KeyChainHandler();
        cardAuthenticationHandler = new CardAuthenticationHandler();		
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
                    case INS_SET_MODE:
                        CommonHandler.setMode(apdu);
                        break;
                    case INS_GET_APP_INFO:
                        CommonHandler.getAppInfo(apdu);
                        break;
                    case INS_SET_PASSWORD_FOR_CARD_AUTHENTICATION:
                    	cardAuthenticationHandler.setPassword(apdu);
                    	break;					
                    case INS_GEN_KEY_PAIR:
                        encryptionHandler.generateKeyPairForDHProtocol();
                        break;
					case INS_INIT_KEYAGREEMENT:
						encryptionHandler.initKeyAgreement();
						break; /**/
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
                break;
            }
            case Constants.APP_DEVELOPER_MODE: {
                switch (ins) {
                	 /*Commands for keychain */
                	case INS_SET_KEYSHARE:
                        keyChainHandler.setKeyChain(apdu);
                        break;
                    case INS_GET_KEYSHARE_LEN:
                        keyChainHandler.getKeyChainLen(apdu);
                        break;
                    case INS_GET_KEYSHARE:
                        keyChainHandler.getKeyChainPart(apdu);
                        break; 
                     
                    /* Commands to handle transactions signing */          
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
                    
                    /* For testing */ 
                    case INS_VERIFY_SIGNATURE:
                        cryptoHandler.verifySignature(apdu);
                        break;
                    case INS_VERIFY_SIGNATURE_WITH_DEFAULT_PATH:
                        cryptoHandler.verifySignatureWithDefaultPath(apdu);
                        break;
                    
                    /* Common commands */     
                    case INS_VERIFY_PIN:
                    	PinHandler.checkPin(apdu);
                        break;						
                    case INS_GET_APP_INFO:
                        CommonHandler.getAppInfo(apdu);
                        break;
                        
                    /* Commands to handle password*/                         
                    case INS_VERIFY_PASSWORD:
                    	cardAuthenticationHandler.verifyPassword(apdu);
                    	break;
                    	
                    /* Commands to handle tweetnacl-like encryption*/ 
                    case INS_GET_INNER_PUB_KEY:
						encryptionHandler.getInnerPublicKeyForDHProtocol(apdu);
						break;	
					case INS_SET_EXTERNAL_PUB_KEY:
						encryptionHandler.setExternalPublicKey(apdu);
						break;
					case INS_GENERATE_COMMON_DH_SECRET:
						encryptionHandler.generateCommonSecretByDHProtocol();
						break;
					case INS_SET_NONCE:
						encryptionHandler.setNonce(apdu);
						break; 		
					case INS_SET_DATA:
						encryptionHandler.setData(apdu);
						break;
					case INS_ENCRYPT_DATA:
						encryptionHandler.encrypt(apdu);
						break;
					case INS_SET_MAC_FROM_HOST:
						encryptionHandler.setMac(apdu);
						break; 	/**/
					case INS_DECRYPT_DATA:
						encryptionHandler.decrypt(apdu);
						break; 
					case INS_GET_FREE_MEMORY_VOL:
						CommonHandler.getFreeMemory(apdu);
						break;	
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
                break;
            }
            case Constants.APP_PERSONALIZED: {
                switch (ins) {              
                    /* Commands for keychain*/
                    case INS_SET_KEYSHARE:
                        keyChainHandler.setKeyChain(apdu);
                        break;
                    case INS_GET_KEYSHARE_LEN:
                        keyChainHandler.getKeyChainLen(apdu);
                        break;
                    case INS_GET_KEYSHARE:
                        keyChainHandler.getKeyChainPart(apdu);
                        break; 
                        
                    /* Commands to handle transactions signing */                       
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
                    
                    /* Common commands */    
                    case INS_VERIFY_PIN:
                    	PinHandler.checkPin(apdu);
                        break;		
                    case INS_GET_APP_INFO:
                        CommonHandler.getAppInfo(apdu);
                        break;
                        
                    /* Commands to handle password*/       
                    case INS_VERIFY_PASSWORD:
                    	cardAuthenticationHandler.verifyPassword(apdu);
                    	break;
                    	
                    /* Commands to handle tweetnacl-like encryption*/ 	
                    case INS_GET_INNER_PUB_KEY:
						encryptionHandler.getInnerPublicKeyForDHProtocol(apdu);
						break;
					case INS_SET_EXTERNAL_PUB_KEY:
						encryptionHandler.setExternalPublicKey(apdu);
						break;
					case INS_GENERATE_COMMON_DH_SECRET:
						encryptionHandler.generateCommonSecretByDHProtocol();
						break;                   					
					case INS_SET_NONCE:
						encryptionHandler.setNonce(apdu);
						break;
				    case INS_SET_DATA:
						encryptionHandler.setData(apdu);
						break;
					case INS_ENCRYPT_DATA:
						encryptionHandler.encrypt(apdu);
						break;
					case INS_SET_MAC_FROM_HOST:
						encryptionHandler.setMac(apdu);
						break; 	/**/
					case INS_DECRYPT_DATA:
						encryptionHandler.decrypt(apdu);
						break; 	/**/	
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
