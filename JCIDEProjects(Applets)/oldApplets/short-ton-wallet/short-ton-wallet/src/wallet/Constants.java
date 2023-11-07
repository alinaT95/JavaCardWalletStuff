package wallet;

import org.globalplatform.GPSystem;

public class Constants {

//
//    // Invalid input parameter to command
//    public final static short SW_INVALID_PARAMETER = (short) 0x9C0F;
//
//    // Operation has been blocked for security reason
//    public final static short SW_IDENTITY_BLOCKED = (short) 0x9C0C;
//
//    // Incorrect P1 parameter
//    public final static short SW_INCORRECT_P1 = (short) 0x9C10;
//
//    // Incorrect P2 parameter
//    public final static short SW_INCORRECT_P2 = (short) 0x9C11;
//
//    // Required operation was not authorized because of a lack of privileges
//    public final static short SW_UNAUTHORIZED = (short) 0x9C06;
//
//    // Required feature is not (yet) supported
//    public final static short SW_UNSUPPORTED_FEATURE = (short) 0x9C05;

    // Algorithm specified is not correct
    public final static short SW_INCORRECT_ALG = (short) 0x9C09;


    public final static short SW_DATA_FOR_SIGNING_IS_TOO_LONG = (short) 0x9C0A;


//
//    // Required operation is not allowed in actual circumstances
//    public final static short SW_OPERATION_NOT_ALLOWED = (short) 0x9C03;
//
//    // For debugging purposes
//    public final static short SW_INTERNAL_ERROR = (short) 0x9CFF;



    //App states
    public static final byte APP_INSTALLED = (byte) GPSystem.APPLICATION_SELECTABLE;
    public static final byte APP_PERSONALIZED = (byte) 0x17;

}
