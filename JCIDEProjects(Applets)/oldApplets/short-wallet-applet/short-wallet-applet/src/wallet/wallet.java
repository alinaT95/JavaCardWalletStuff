package wallet ;


import javacard.framework.*;
import org.globalplatform.GPSystem;

public class wallet extends Applet
{
	
	private final static byte WALLET_APPLET_CLA = (byte) 0xB0;

    /****************************************
     * Instruction codes *
     ****************************************
     */
    //Personalization
    private static final byte INS_SETUP_KEY = (byte)0x10;
  //  private static final byte INS_WRITE_PIN = (byte)0x40;
    private static final byte INS_FINISH_PERS = (byte)0x80;
    
    
    private static final byte INS_IMPORT_KEY = (byte)0x20;
    private static final byte INS_GET_PUBLIC_KEY = (byte)0x30;
    private static final byte INS_GET_PUBLIC_KEY_FROM_KEY_AGREEMENT = (byte)0x31;
//    private static final byte INS_VERIFY_PIN = (byte)0x50;
//    private static final byte INS_CHANGE_PIN = (byte)0x60;
//    private static final byte INS_IMPORT_SEED = (byte)0x80;
    private static final byte INS_SIGN_MESSAGE = (byte)0x2A;
    private static final byte INS_VERIFY_SIGNATURE = (byte)0x2B;
    private static final byte INS_SET_DATA_FOR_SIGNING = (byte)0xF8;
//    private static final byte INS_SEND_SIGNED_DATA = (byte)0xF9;

    private static final byte INS_GET_APP_INFO = (byte)0xFA;

//    //Blocked mode
//    private static final byte INS_UNBLOCK_PIN = (byte)0x70;


    private BtcCryptoHandler cryptoHandler;
    
    public wallet(){
        cryptoHandler = new BtcCryptoHandler();
//        commonHandler = new CommonHandler();
    }

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new wallet().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

/*
	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)0x00:
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}*/
	
	public void process(APDU apdu)
    {
        if (selectingApplet())
            return;

        byte[] buffer = apdu.getBuffer();
        byte cla = (byte) (buffer[ISO7816.OFFSET_CLA] & 0xFF);
        byte ins = (byte) (buffer[ISO7816.OFFSET_INS] & 0xFF);

        // check SELECT APDU command
        if ((cla == 0) && (ins == (byte) 0xA4))
            return;

        if (cla != WALLET_APPLET_CLA)
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);


        switch (GPSystem.getCardContentState()) {
            case Constants.APP_INSTALLED: {
                switch (ins) {
                    case INS_SETUP_KEY:
                        cryptoHandler.setupMasterKeyWithoutSeed();
                        break;
                    /*case INS_WRITE_PIN:
                        PinHandler.writePin(apdu);
                        break;*/
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
           /* case Constants.APP_DEVELOPER_MODE: {
                switch (ins) {
                    case INS_IMPORT_KEY:
                        cryptoHandler.importKey(apdu);
                        break;
                    case INS_GET_PUBLIC_KEY:
                        cryptoHandler.getPublicMasterKey(apdu);
                        break;
                    case INS_SET_DATA_FOR_SIGNING:
                        cryptoHandler.setDataForSigning(apdu);
                        break;
                    case INS_SIGN_MESSAGE:
                        cryptoHandler.signShortMessage(apdu);
                        break;
                    case INS_SEND_SIGNED_DATA:
                        cryptoHandler.sendSignedData(apdu);
                        break;
                    case INS_IMPORT_SEED:
                        cryptoHandler.importSeed(apdu);
                        break;
                    case INS_CHANGE_PIN:
                        PinHandler.changePin(apdu);
                        break;
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
                break;
            }*/
            case Constants.APP_PERSONALIZED: {
                switch (ins) {
                    case INS_GET_PUBLIC_KEY:
                        cryptoHandler.getPublicMasterKey(apdu);
                        break;
                    case INS_GET_PUBLIC_KEY_FROM_KEY_AGREEMENT:
                        cryptoHandler.getPublicMasterKeyUsingKeyAgreement(apdu);
                        break;
                    case INS_SET_DATA_FOR_SIGNING:
                        cryptoHandler.setDataForSigning(apdu);
                        break;
                    case INS_SIGN_MESSAGE:
                        cryptoHandler.signShortMessage(apdu);
                        break;
					case INS_VERIFY_SIGNATURE:
						cryptoHandler.verifySignature(apdu);
						break;
                   /* case INS_SEND_SIGNED_DATA:
                        cryptoHandler.sendSignedData(apdu);
                        break;
                    case INS_IMPORT_SEED:
                        cryptoHandler.importSeed(apdu);
                        break;
                    case INS_VERIFY_PIN:
                        PinHandler.verifyPin(apdu);
                        break;*/
                    case INS_GET_APP_INFO:
                        CommonHandler.getAppInfo(apdu);
                        break;
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
                break;
            }
            /*case Constants.APP_BLOCKED: {
                switch (ins) {
                    case INS_UNBLOCK_PIN:
                        PinHandler.unblockPin(apdu);
                        break;
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
                break;
            }*/
            default:
                ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
    }

}
