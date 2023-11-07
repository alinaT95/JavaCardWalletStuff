package wallet ;

import com.ftsafe.javacardx.IOUtil.Timer;
import com.ftsafe.javacardx.IOUtil.View;

import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class UserInterface  
{
	
	public static byte[] TON = {'T', 'O', 'N'};
    public static byte[] satoshi = {(byte) 0x73, (byte) 0x61, (byte) 0x74, (byte) 0x6F, (byte) 0x73, (byte) 0x68, (byte) 0x69};

	public static byte[] My_address = {(byte) 0x4D, (byte) 0x79, (byte) 0x20, (byte) 0x61, (byte) 0x64, (byte) 0x64, (byte) 0x72, (byte) 0x65, (byte) 0x73, (byte) 0x73, (byte) 0x3A, 0x00};

    // Confirm output#
    public static byte[] confirmSending = {(byte) 0x43, (byte) 0x6F, (byte) 0x6E, (byte) 0x66, (byte) 0x69, (byte) 0x72, (byte) 0x6D, (byte) 0x20, (byte) 0x73, (byte) 0x65, (byte) 0x6E, (byte) 0x64, (byte) 0x69, (byte) 0x6E, (byte) 0x67};

    // Send
    public static byte[] SEND = {(byte) 0x53, (byte) 0x65, (byte) 0x6E, (byte) 0x64};

    // to
    public static byte[] to = {(byte) 0x74, (byte) 0x6F, 0x00};

    // Double confirm to send
    public static byte[] reConfirmSending = {(byte) 0x52, (byte) 0x65, (byte) 0x2D, (byte) 0x63, (byte) 0x6F, (byte) 0x6E, (byte) 0x66, (byte) 0x69, (byte) 0x72, (byte) 0x6D, (byte) 0x20, (byte) 0x73, (byte) 0x65, (byte) 0x6E, (byte) 0x64, (byte) 0x69, (byte) 0x6E, (byte) 0x67};

    //  including fee:
    public static byte[] includingFee = {(byte) 0x69, (byte) 0x6E, (byte) 0x63, (byte) 0x6C, (byte) 0x75, (byte) 0x64, (byte) 0x69, (byte) 0x6E, (byte) 0x67, (byte) 0x20, (byte) 0x66, (byte) 0x65, (byte) 0x65, (byte) 0x3A, 0x00};

    public static byte[] fromYourWallet = {(byte) 0x66, (byte) 0x72, (byte) 0x6F, (byte) 0x6D, (byte) 0x20, (byte) 0x79, (byte) 0x6F, (byte) 0x75, (byte) 0x72, (byte) 0x20, (byte) 0x77, (byte) 0x61, (byte) 0x6C, (byte) 0x6C, (byte) 0x65, (byte) 0x74, 0x00};

    // Confirm
    public static byte[] CONFIRM = {(byte) 0x43, (byte) 0x6F, (byte) 0x6E, (byte) 0x66, (byte) 0x69, (byte) 0x72, (byte) 0x6D, 0x00};

    // I'm Sure
    public static byte[] Im_Sure = {(byte) 0x49, (byte) 0x27, (byte) 0x6D, (byte) 0x20, (byte) 0x53, (byte) 0x75, (byte) 0x72, (byte) 0x65, 0x00};

    // Approved
    public static final byte [] APPROVED = {(byte) 0x41, (byte) 0x70, (byte) 0x70, (byte) 0x72, (byte) 0x6F, (byte) 0x76, (byte) 0x65, (byte) 0x64, 0x00};

    // Rejected
    public static byte[] Rejected = {(byte) 0x52, (byte) 0x65, (byte) 0x6A, (byte) 0x65, (byte) 0x63, (byte) 0x74, (byte) 0x65, (byte) 0x64, 0x00};

    public static byte[] DATA_TRANSMIT = {'R', 'e', 'c', 'e', 'i', 'v', 'i', 'n', 'g', ' ', 'd', 'a', 't', 'a', 0x00};//receiving
    public static final byte[] SIGN_DATA = {'P', 'r', 'o', 'c', 'e', 's', 's', 'i', 'n', 'g', 0x00};
    public static final byte[] TIMER_OUT = {'T', 'i', 'm', 'e', ' ', 'o', 'u', 't', 0x00};
    
    //public static final byte[] TRANSACTION_APPROVED = {'A', 'p', 'p', 'r', 'o', 'v', 'e', 'd',0x00};
    
    public static final byte[] WRITE_UNBLOCKING_PIN = {'W', 'r', 'i', 't', 'e', 'U', 'n', 'b', 'l', 'o', 'c', 'k', 'i', 'n', 'g', 'P', 'i', 'n',0x00};
    public static final byte[] WRITE_MAX_PIN_TRIES = {'W', 'r', 'i', 't', 'e', ' ', 'm', 'a', 'x', ' ', 'P', 'I', 'N', ' ', 't', 'r', 'i', 'e', 's', 0x00};
    public static final byte[] CHECK_PIN = {'C', 'h', 'e', 'c', 'k', 'i', 'n', 'g', ' ', 'P', 'I', 'N', 0x00};
    public static final byte[] VERIFYING_SIGNATURE  = {'V', 'e', 'r', 'i', 'f', 'y', 'i', 'n', 'g', ' ', 's', 'i', 'g', 'n', 0x00};
    
    short max_X = 0;
    static short max_Y = 0;
    
    //private Address base_address;
    
    // show transation approved text in UI
    static void showTransationApproved() {
        showText(UserInterface.APPROVED);
        Timer.wait((short) 2000);
        showMainUI(true);
    }
    
    // show data receiving text  in UI
    static void showDataTransmit() {
        showText(DATA_TRANSMIT);
    }
    
    // show data working text  in UI
    static void showWorking() {
        showText(SIGN_DATA);
    }
    
    static void showVerifying(){
	    showText(VERIFYING_SIGNATURE);
    }
    
    static void showWriteMaxPinTries(){
	    showText(WRITE_MAX_PIN_TRIES);
    }
    
    static void showWriteUnblockingPin(){
	    showText(WRITE_UNBLOCKING_PIN);
    }
    
    static void showCheckPin(){
	    showText(CHECK_PIN);
    }
    
    //show text as image
    static void showText(byte[] buf) {
        View.clearScreen(false);
        View.drawString((short) 0, (short) (max_Y / 2),
                buf, (short) 0, (short) buf.length,
                true, View.VIEW_STRING_ALGN_CENTER);

        View.refreshScreen();
    }
    
    static void showShort(short number){
	    //byte [] bytesOfNumber = new byte[2];
	    //byte[1] = (byte)(number);
	    //byte[0] = (byte)(8>>number);
	    
	    //showText(bytesOfNumber);
    }
    
    // show time out  text  in UI ,and show main UI
    void timeOut() {
        showText(UserInterface.TIMER_OUT);
        Timer.wait((short) 2000);
        showMainUI(true);
    }
    
    // show transation rejected text in UI
    void showTransationRejected() {

        showText(UserInterface.Rejected);

        Timer.wait((short) 2000);
        showMainUI(true);
    }
    
    /*
    UserInterface(Address _a) {

        byte _type = View.getViewType();
        switch (_type) {
            case View.VIEW_LCD_132X32:
                max_X = (short) 132;
                max_Y = (short) 32;
                break;
            case View.VIEW_LCD_128X64_1:
                max_X = (short) 128;
                max_Y = (short) 64;
                break;
            case View.VIEW_LCD_138X64_2:
                max_X = (short) 138;
                max_Y = (short) 64;
                break;
            case View.VIEW_OLED_132X32:

                max_X = (short) 132;
                max_Y = (short) 64;
                break;
            case View.VIEW_OLED_128X64_1:
            case View.VIEW_OLED_128X64_2:
                max_X = (short) 128;
                max_Y = (short) 64;
                break;
            default:
                ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
                break;
        }
        
        base_address = _a;
        set_coin_unit(U_UNIT);
        LOOP2 = LOOP2_VALUE_buf[coin_unit];
    }*/
    
    static void showMainUI(boolean cmd) {
        View.showMain(cmd, null, (short) 0, (short) 0, (short) 0, (short) 0);
    }
    
    // show address
    short showAddr(byte[] addr_buf, short addr_buf_offs, short addr_len) {

/*
        View.clearScreen(false);

        short baseY = (short) (10);

        View.drawString((short) 0, (short) (2), UserInterface.Address_colon, (short) 0, (short) UserInterface.Address_colon.length, false, View.VIEW_STRING_ALGN_LEFT);

        View.drawString((short) 0, (short) (2 * baseY - 2), addr_buf, addr_buf_offs, addr_len, true, View.VIEW_STRING_ALGN_LEFT);

        byte[] _ico = getCoinIco();
        View.drawBitmap((short) (24), (short) (max_Y - 26), (short) 24, (short) 24, _ico, (short) 0, (short) _ico.length);


        short tempX = (short) (max_X - 24 - 38);
        View.drawSysBitmap((short) tempX, (short) (max_Y - 18), View.VIEW_BMP_DOWN);
        tempX = (short) (max_X - 44);
        View.drawString(tempX, (short) (max_Y - 14), UserInterface.QR_Code, (short) 0, (short) UserInterface.QR_Code.length,
                false, View.VIEW_STRING_ALGN_LEFT);

        View.refreshScreen();*/
        return 0;
    }
    
    
    
    
    public byte[] getCoinIco() {
    	/*
        byte coin_type = base_address.get_coind_type();
        switch (coin_type) {
            case Address.COIN_TYPE_BTC:
                return btcIco;
            case Address.COIN_TYPE_BCH:
                return ico_bch;
            case Address.COIN_TYPE_LTC:
                return ico_ltc;
            case Address.COIN_TYPE_USDT:
                return ico_usdt;
            default:
                Error.wrong6AA5_NOT_SUPPORT_COIND_TYPE();
                break;
        }*/
        return null;
    }

}
