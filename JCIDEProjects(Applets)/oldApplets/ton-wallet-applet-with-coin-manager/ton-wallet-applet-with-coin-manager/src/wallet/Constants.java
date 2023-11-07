package wallet ;

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


//    // Maximum size for the extended APDU buffer
//    public final static short EXT_APDU_BUFFER_SIZE = (short) 268;
//    public final static short TMP_BUFFER_SIZE = (short) 256;


    // JC API 2.2.2 does not define these constants:
   // public final static short LENGTH_EC_FP_256 = (short) 256;
   // public final static byte ALG_EC_SVDP_DH_PLAIN= (byte) 3;

    //App states
    public static final byte APP_INSTALLED = (byte) GPSystem.APPLICATION_SELECTABLE;
    public static final byte APP_PERSONALIZED = (byte) 0x17;
    public static final byte APP_BLOCKED = (byte) 0x27;
    public static final byte APP_DEVELOPER_MODE = (byte) 0x37;
    
    
    public static final byte PIN_LENGTH = (byte) 0x04;
	

}

