package tonwallet ;

import org.globalplatform.GPSystem;

public class Constants  
{
	
//    // Entered PIN is not correct
    public final static short SW_AUTH_FAILED = (short) 0x9C02;
//
//    // Invalid input parameter to command
    public final static short SW_INVALID_PARAMETER = (short) 0x9C0F;
//
//    // Operation has been blocked for security reason
    public final static short SW_IDENTITY_BLOCKED = (short) 0x9C0C;
//
//    // Incorrect P1 parameter
    public final static short SW_INCORRECT_P1 = (short) 0x9C10;
//
//    // Incorrect P2 parameter
    public final static short SW_INCORRECT_P2 = (short) 0x9C11;
//
//    // Required operation was not authorized because of a lack of privileges
    public final static short SW_UNAUTHORIZED = (short) 0x9C06;
//
//    // Required feature is not (yet) supported
    public final static short SW_UNSUPPORTED_FEATURE = (short) 0x9C05;

    // Algorithm specified is not correct
    public final static short SW_INCORRECT_ALG = (short) 0x9C09;

    public final static short SW_DATA_FOR_SIGNING_IS_TOO_LONG = (short) 0x9C0A;
    
    //    // Required operation is not allowed in actual circumstances
    public final static short SW_OPERATION_NOT_ALLOWED = (short) 0x9C03;
//
//    // For debugging purposes
    public final static short SW_INTERNAL_ERROR = (short) 0x9CFF ;
    
    
    public final static short SW_INCORRECT_PIN = (short) 0x6f01;
    public final static short SW_PIN_TRIES_EXPIRED = (short) 0x6f02;
    public final static short SW_SET_CURVE_FAILED = (short) 0x6f03;
    public final static short SW_GET_COIN_PUB_DATA_FAILED = (short) 0x6f04;
    public final static short SW_SIGN_DATA_FAILED = (short) 0x6f05;
    
    public final static short SW_WRONG_KEY_CHAIN_INDEX  = (short) 0x6f06;
    public final static short SW_KEY_SHARE_TOO_LONG  = (short) 0x6f07;
    public final static short SW_WRONG_KEY_SHARE_START_OR_LEN  = (short) 0x6f08;
    public final static short SW_WRONG_KEY_SHARE_LEN  = (short) 0x6f09;
    public final static short SW_WRONG_HASH_SIZE  = (short) 0x6f0A;
    public final static short SW_WRONG_HASH  = (short) 0x6f0B;
    public final static short SW_BAD_FORMED_KEY_SHARE   = (short) 0x6f0C;
    
    public final static short SW_INCORRECT_PASSWORD_FOR_CARD_AUTHENICATION   = (short) 0x6f0D;
    
    public final static short SW_EXTERNAL_PK_IS_NOT_INITIALIZED = (short) 0x7f01;
    public final static short SW_MAC_INCORRECT = (short) 0x7f02;




    //App states
    public static final byte APP_INSTALLED = (byte) GPSystem.APPLICATION_SELECTABLE;
    public static final byte APP_PERSONALIZED = (byte) 0x17;
    public static final byte APP_DEVELOPER_MODE = (byte) 0x37;
    
    public final static short MAX_KEY_SHARE_SIZE = (short) 4096;
	public final static short SHA_HASH_SIZE = (short) 32;
	
	public final static short AES_BLOCK_LEN = (short) 16;
	public final static short AES_KEY_LEN = (short) 16;
	public final static short KEY_SHARE_SLICE_LEN = (short) 128;
}

