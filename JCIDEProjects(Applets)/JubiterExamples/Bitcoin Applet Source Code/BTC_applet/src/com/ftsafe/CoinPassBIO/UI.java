package com.ftsafe.CoinPassBIO;

import com.ftsafe.javacardx.IOUtil.Timer;
import com.ftsafe.javacardx.IOUtil.View;

import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class UI  
{
	 
	static final byte[] usdtNick = { 'U', 'S', 'D','T',0x00 };
	 // usdt - w: 24, h: 24
   private static final byte[] ico_usdt = { 
    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3f, (byte)0xff, (byte)0xfc, (byte)0x3f, (byte)0xff, (byte)0xfc, (byte)0x3f, (byte)0xff, (byte)0xfc, (byte)0x3f, (byte)0xff, (byte)0xfc, (byte)0x00, 
    (byte)0x7e, (byte)0x00, (byte)0x00, (byte)0x7e, (byte)0x00, (byte)0x00, (byte)0x7e, (byte)0x00, (byte)0x07, (byte)0x7e, (byte)0xe0, (byte)0x70, (byte)0x7e, (byte)0x0e, (byte)0xc0, (byte)0x7e, 
    (byte)0x03, (byte)0xc0, (byte)0x7e, (byte)0x03, (byte)0x70, (byte)0x00, (byte)0x0e, (byte)0x0f, (byte)0xff, (byte)0xf0, (byte)0x00, (byte)0x7e, (byte)0x00, (byte)0x00, (byte)0x7e, (byte)0x00, 
    (byte)0x00, (byte)0x7e, (byte)0x00, (byte)0x00, (byte)0x7e, (byte)0x00, (byte)0x00, (byte)0x7e, (byte)0x00, (byte)0x00, (byte)0x7e, (byte)0x00, (byte)0x00, (byte)0x7e, (byte)0x00, (byte)0x00, 
    (byte)0x7e, (byte)0x00, (byte)0x00, (byte)0x7e, (byte)0x00, (byte)0x00, (byte)0x7e, (byte)0x00, 
   };

	 
	 
	static final byte[] bchNick = { 'B', 'C', 'H',0x00 };
	// bch - w: 24, h: 24
	 static final byte[] ico_bch = { 
		(byte)0x00, (byte)0x30, (byte)0x00, (byte)0x02, (byte)0x70, (byte)0x00, (byte)0x07, (byte)0x38, (byte)0x00, (byte)0x03, (byte)0x3f, (byte)0x80, (byte)0x03, (byte)0xff, (byte)0xc0, (byte)0x07, 
		(byte)0xff, (byte)0xe0, (byte)0x1f, (byte)0xc3, (byte)0xe0, (byte)0x1f, (byte)0x81, (byte)0xe0, (byte)0x03, (byte)0x81, (byte)0xc0, (byte)0x03, (byte)0xc3, (byte)0xc0, (byte)0x03, (byte)0xc7, 
		(byte)0xf0, (byte)0x01, (byte)0xff, (byte)0xf8, (byte)0x01, (byte)0xf8, (byte)0x7c, (byte)0x01, (byte)0xe0, (byte)0x3c, (byte)0x00, (byte)0xf0, (byte)0x3c, (byte)0x00, (byte)0xf0, (byte)0x3c, 
		(byte)0x00, (byte)0xf8, (byte)0x78, (byte)0x00, (byte)0x79, (byte)0xf0, (byte)0x00, (byte)0x7f, (byte)0xe0, (byte)0x00, (byte)0xff, (byte)0xc0, (byte)0x01, (byte)0xfc, (byte)0xc0, (byte)0x00, 
		(byte)0x8c, (byte)0xe0, (byte)0x00, (byte)0x0e, (byte)0x40, (byte)0x00, (byte)0x04, (byte)0x00, 
	 };

	 static final byte[] btcNick = { 'B', 'T', 'C',0x00 };
	 static final byte[] btcIco = { 
		(byte)0x00, (byte)0xcc, (byte)0x00, (byte)0x00, (byte)0xcc, (byte)0x00, (byte)0x00, (byte)0xcc, (byte)0x00, (byte)0x0f, (byte)0xff, (byte)0xc0, (byte)0x0f, (byte)0xff, (byte)0xe0, (byte)0x01, 
		(byte)0xff, (byte)0xe0, (byte)0x01, (byte)0xc0, (byte)0xf0, (byte)0x01, (byte)0xc0, (byte)0x70, (byte)0x01, (byte)0xc0, (byte)0x70, (byte)0x01, (byte)0xc0, (byte)0xe0, (byte)0x01, (byte)0xc1, 
		(byte)0xc0, (byte)0x01, (byte)0xff, (byte)0x80, (byte)0x01, (byte)0xff, (byte)0xe0, (byte)0x01, (byte)0xc1, (byte)0xf0, (byte)0x01, (byte)0xc0, (byte)0xf8, (byte)0x01, (byte)0xc0, (byte)0x78, 
		(byte)0x01, (byte)0xc0, (byte)0x78, (byte)0x01, (byte)0xc0, (byte)0x78, (byte)0x01, (byte)0xc0, (byte)0xf0, (byte)0x0f, (byte)0xff, (byte)0xe0, (byte)0x0f, (byte)0xff, (byte)0xc0, (byte)0x00, 
		(byte)0xcc, (byte)0x00, (byte)0x00, (byte)0xcc, (byte)0x00, (byte)0x00, (byte)0xcc, (byte)0x00 
	};  

	// ltc - w: 24, h: 24
	 static final byte[] ico_ltc = { 
		(byte)0x00, (byte)0xfe, (byte)0x00, (byte)0x00, (byte)0xfe, (byte)0x00, (byte)0x00, (byte)0xfe, (byte)0x00, (byte)0x01, (byte)0xfc, (byte)0x00, (byte)0x01, (byte)0xfc, (byte)0x00, (byte)0x01, 
		(byte)0xfc, (byte)0x00, (byte)0x03, (byte)0xf8, (byte)0x00, (byte)0x03, (byte)0xf8, (byte)0x00, (byte)0x03, (byte)0xf8, (byte)0x00, (byte)0x07, (byte)0xfe, (byte)0x00, (byte)0x07, (byte)0xfe, 
		(byte)0x00, (byte)0x07, (byte)0xfc, (byte)0x00, (byte)0x0f, (byte)0xf8, (byte)0x00, (byte)0x3f, (byte)0xf0, (byte)0x00, (byte)0x3f, (byte)0xf0, (byte)0x00, (byte)0x3f, (byte)0xe0, (byte)0x00, 
		(byte)0x3f, (byte)0xe0, (byte)0x00, (byte)0x1f, (byte)0xe0, (byte)0x00, (byte)0x1f, (byte)0xff, (byte)0xfc, (byte)0x1f, (byte)0xff, (byte)0xfc, (byte)0x3f, (byte)0xff, (byte)0xfc, (byte)0x3f, 
		(byte)0xff, (byte)0xfc, (byte)0x3f, (byte)0xff, (byte)0xf8, (byte)0x00, (byte)0x00, (byte)0x00, 
	 };
	public  static byte[]  USDT = {'U','S','D','T'};
	public  static byte[]  BCH = {'B','C','H'};
	public  static byte[]  LTC = {'L','T','C'};	
	public  static byte[]  BTC = {'B','T','C'};
	public  static byte[]  satoshi = {(byte) 0x73,(byte) 0x61,(byte) 0x74,(byte) 0x6F,(byte) 0x73,(byte) 0x68,(byte) 0x69};

		public static  byte[] Address_colon =  {(byte) 0x41,(byte) 0x64,(byte) 0x64,(byte) 0x72,(byte) 0x65,(byte) 0x73,(byte) 0x73,':' ,0x0};
	public static  byte[] _Address =  {(byte) 0x41,(byte) 0x64,(byte) 0x64,(byte) 0x72,(byte) 0x65,(byte) 0x73,(byte) 0x73,0x0};
	public static  byte[] QR_Code = {(byte) 0x51,(byte) 0x52,(byte) 0x20,(byte) 0x43,(byte) 0x6F,(byte) 0x64,(byte) 0x65,0x00};

	public static  byte[] My_address = {(byte) 0x4D,(byte) 0x79,(byte) 0x20,(byte) 0x61,(byte) 0x64,(byte) 0x64,(byte) 0x72,(byte) 0x65,(byte) 0x73,(byte) 0x73,(byte) 0x3A,0x00};
	
	// Confirm output#
	public static	byte[] Confirm_sending = {(byte) 0x43,(byte) 0x6F,(byte) 0x6E,(byte) 0x66,(byte) 0x69,(byte) 0x72,(byte) 0x6D,(byte) 0x20,(byte) 0x73,(byte) 0x65,(byte) 0x6E,(byte) 0x64,(byte) 0x69,(byte) 0x6E,(byte) 0x67};

    // Send
    public  static byte[]  Send ={(byte) 0x53,(byte) 0x65,(byte) 0x6E,(byte) 0x64};
     
    // to
	public static byte[]  to = {(byte) 0x74,(byte) 0x6F,0x00};
	
	// Double confirm to send
	public static byte[] Re_confirm_sending = {(byte) 0x52,(byte) 0x65,(byte) 0x2D,(byte) 0x63,(byte) 0x6F,(byte) 0x6E,(byte) 0x66,(byte) 0x69,(byte) 0x72,(byte) 0x6D,(byte) 0x20,(byte) 0x73,(byte) 0x65,(byte) 0x6E,(byte) 0x64,(byte) 0x69,(byte) 0x6E,(byte) 0x67};

	//  including fee:
    public static byte[] including_fee = {(byte) 0x69,(byte) 0x6E,(byte) 0x63,(byte) 0x6C,(byte) 0x75,(byte) 0x64,(byte) 0x69,(byte) 0x6E,(byte) 0x67,(byte) 0x20,(byte) 0x66,(byte) 0x65,(byte) 0x65,(byte) 0x3A,0x00};

    public static byte[] from_your_wallet = {(byte) 0x66,(byte) 0x72,(byte) 0x6F,(byte) 0x6D,(byte) 0x20,(byte) 0x79,(byte) 0x6F,(byte) 0x75,(byte) 0x72,(byte) 0x20,(byte) 0x77,(byte) 0x61,(byte) 0x6C,(byte) 0x6C,(byte) 0x65,(byte) 0x74,0x00};
    
    // Confirm
    public static byte[] Confirm =   {(byte) 0x43,(byte) 0x6F,(byte) 0x6E,(byte) 0x66,(byte) 0x69,(byte) 0x72,(byte) 0x6D, 0x00};

    // I'm Sure
    public static byte[] Im_Sure =  {(byte) 0x49,(byte) 0x27,(byte) 0x6D,(byte) 0x20,(byte) 0x53,(byte) 0x75,(byte) 0x72,(byte) 0x65,0x00};

	// Approved
	public static byte[] Approved =  {(byte) 0x41,(byte) 0x70,(byte) 0x70,(byte) 0x72,(byte) 0x6F,(byte) 0x76,(byte) 0x65,(byte) 0x64,0x00};

	// Rejected
	public static byte[] Rejected ={(byte) 0x52,(byte) 0x65,(byte) 0x6A,(byte) 0x65,(byte) 0x63,(byte) 0x74,(byte) 0x65,(byte) 0x64 ,0x00};

    public static byte[] data_transmit   = {'R','e','c','e','i','v','i','n','g',' ','d','a','t','a',0x00};//receiving
     public static byte[] sign_data = {'P','r','o','c','e','s','s','i','n','g',0x00};
     public static byte[] timer_out={'T','i','m','e',' ','o', 'u','t',0x00};
	 short max_X = 0;
	 short max_Y = 0;
	private Address base_address;
	// show transation approved text in UI
	 void show_transation_approved()
	{
		show_text( UI.Approved);
		Timer.wait((short)2000);
		showMainUI(true);
	}
    // show data receiving text  in UI
	void show_data_transmit(){
		show_text( data_transmit);
	}
	// show data working text  in UI
	void show_working(){
		show_text( sign_data);
	}
	void show_text(byte[] buf){
		View.clearScreen(false);
	    View.drawString((short)0, (short)(max_Y/2),
				 buf, (short)0, (short) buf.length, 
				true, View.VIEW_STRING_ALGN_CENTER);
		
		View.refreshScreen();
	}
	// show time out  text  in UI ,and show main UI
	void Time_out(){
		show_text( UI.timer_out);
		Timer.wait((short)2000);
		showMainUI(true);
	} 
	
	// show transation rejected text in UI
	 void show_transation_rejected()
	{

		show_text( UI.Rejected);
	
		Timer.wait((short)2000);
		showMainUI(true);
	}	

	 UI(Address _a)
	 {
	 	
		byte _type = View.getViewType();	
		 switch(_type)
		{
			case  View.VIEW_LCD_132X32:
				max_X = (short)132;
				max_Y  = (short)32;
				break ;
			case View.VIEW_LCD_128X64_1:
				max_X = (short)128;
				max_Y  = (short)64;
				break ;
			case View.VIEW_LCD_138X64_2:
				max_X = (short)138;
				max_Y  = (short)64;
				break ;
			case View.VIEW_OLED_132X32:
					
				max_X = (short)132;
				max_Y  = (short)64;
				break ;
			case View.VIEW_OLED_128X64_1:
			case View.VIEW_OLED_128X64_2:
				max_X = (short)128;
				max_Y  = (short)64;
				break ;
			default:
				ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
				break ;
		 }
		base_address = _a;
		set_coin_unit(U_UNIT);	
		LOOP2 =  LOOP2_VALUE_buf[coin_unit];
	 }
	static void showMainUI(boolean cmd){
		View.showMain (cmd,null,(short)0,(short)0,(short)0,(short)0);
	}
	
	// show address
	short show_addr(byte[] addr_buf,short addr_buf_offs, short addr_len){
		  
		View.clearScreen(false);
		
        short baseY = (short)(10);
	
	    View.drawString((short)0, (short)(2), UI.Address_colon, (short)0, (short)UI.Address_colon.length, false, View.VIEW_STRING_ALGN_LEFT);

		View.drawString((short)0, (short)(2*baseY - 2), addr_buf, addr_buf_offs, addr_len, true, View.VIEW_STRING_ALGN_LEFT);

		byte[] _ico = get_coin_ico();
		View.drawBitmap((short)(24),(short)(max_Y-26),(short)24,(short)24,_ico,(short)0,(short)_ico.length);


	    short tempX =  (short)(max_X-24 - 38);
		View.drawSysBitmap((short)tempX, (short)(max_Y-18),View.VIEW_BMP_DOWN);
	    tempX = (short)( max_X - 44 );
		View.drawString(tempX, (short)(max_Y-14), UI.QR_Code, (short)0, (short)UI.QR_Code.length,
						 false, View.VIEW_STRING_ALGN_LEFT);
	
		View.refreshScreen();
		return  0;
	}	
    // show QR 
	short show_QR_Code(byte[] addr_buf,short addr_buf_offs, short addr_len){
		
	   
		View.clearScreen(false);
		byte[] _ico = get_coin_ico();
		View.drawBitmap((short)(max_X - 48),(short)(20),(short)24,(short)24,_ico,(short)0,(short)_ico.length);
	
		View.drawQRCode ((short)0, (short)(0), addr_buf, addr_buf_offs, addr_len);
	
	    short tempX =  (short)(max_X-24 - 38);
		View.drawSysBitmap((short)tempX, (short)(max_Y-18),View.VIEW_BMP_UP);
	    tempX = (short)( max_X - 44 );
		View.drawString(tempX, (short)(max_Y-14), UI._Address, (short)0, (short)UI._Address.length,
						 false, View.VIEW_STRING_ALGN_LEFT);
	
		View.refreshScreen();
		return  0;
	}	
	public  static final short _0 = 0x00;
	
   // 获取币值单位的字符串
	short get_Unit_string(byte[] dest,short destOff,byte coin_type)
	{	
		
		switch(coin_type)
		{
		case Address.COIN_TYPE_BTC :
		case Address.COIN_TYPE_LTC :
		case Address.COIN_TYPE_BCH :
		case Address.COIN_TYPE_USDT :
			break ;
		default:
			Error.wrong6AA5_NOT_SUPPORT_COIND_TYPE();
			break ;
        }
        if(coin_type != Address.COIN_TYPE_USDT){
			switch (coin_unit) {
			case U_UNIT:
				break ;
			case U_cUNIT:
				dest[destOff++] = 'c';
				break ;
			
			case U_mUNIT:
				dest[destOff++] = 'm';
				break ;
				
			case U_uUNIT:
				dest[destOff++] = 'u';
				break ;
				
			case U_satoshi:
				Util.arrayCopyNonAtomic(satoshi,_0,dest,destOff,(short)satoshi.length);
				return (short)(destOff + satoshi.length);
				
			default:
				Error.wrong6A86_incorrect_P1P2();
				break;
			}
        }

		byte[] data = null;
		switch(coin_type)
		{
		case Address.COIN_TYPE_BTC :
			data = BTC;
			break ;
		case Address.COIN_TYPE_LTC :
			data = LTC;
			break ;
		case Address.COIN_TYPE_BCH :
			data = BCH;
			break ;
		case Address.COIN_TYPE_USDT :
			data = USDT;
			break ;
		default:
			break ;
        }
        
		Util.arrayCopyNonAtomic(data,_0,dest,destOff,(short)data.length);
		return (short)(destOff + data.length);
		
		
	}
	byte LOOP2;
	static final short TOTAL_AMOUNT_OFFSET_IN_APDU_BUF = (short)(220);
	static final short FEE_OFFSET_IN_APDU_BUF = (short)(228);
	
	// 确认收款
	short show_doulble_confirm_page(byte[] apdu_buf ){
	
		View.clearScreen(false);
		short baseY = (short)(10);
		
		short buf_offs = (short)Re_confirm_sending.length;
		// 绘制 Double confirm to send 的字符串
		Util.arrayCopyNonAtomic(Re_confirm_sending, _0, apdu_buf,_0, buf_offs );
								
		apdu_buf[buf_offs++] = 0x00;	
		
		
		View.drawString((short)0, (short)0, apdu_buf, (short)0, (short)buf_offs, false, View.VIEW_STRING_ALGN_LEFT);
		
		byte[] coin_ico = get_coin_ico();
		View.drawBitmap((short)(max_X - 24),(short)0,(short)24,(short)24,coin_ico,_0,(short)coin_ico.length);
	
		
		buf_offs= 0;
		// 绘制总额度的字符串
	
		Util.arrayCopyNonAtomic(apdu_buf, TOTAL_AMOUNT_OFFSET_IN_APDU_BUF, apdu_buf, (short)90, (short)8);
		short len ;
		if(!base_address.is_USDT()){
			len = Common.convert_hex_amount_to_displayable_LE(apdu_buf,(short)90,apdu_buf,buf_offs, 
										 apdu_buf, (short)(buf_offs + 21), LOOP2);
		}
		else{
		 len = Common.convert_amount_to_displayable_BE(apdu_buf,(short)90,apdu_buf,buf_offs, 
											 apdu_buf, (short)(buf_offs + 21), (byte)8);
	    }
		
		buf_offs += len;
		apdu_buf[buf_offs++] = ' ';
		buf_offs =  get_Unit_string(apdu_buf,buf_offs ,base_address.get_coind_type());
		
		apdu_buf[buf_offs++] = 0x00;
	
		
		View.drawString((short)0, (short)(baseY), apdu_buf, (short)0, (short)buf_offs, true, View.VIEW_STRING_ALGN_LEFT);
		
	    View.drawString((short)0, (short)(baseY*2), UI.including_fee, (short)0, (short)UI.including_fee.length, false, View.VIEW_STRING_ALGN_LEFT);
	
	
		// 绘制fee的字符串
		buf_offs = _0;
		Util.arrayCopyNonAtomic(apdu_buf, FEE_OFFSET_IN_APDU_BUF, apdu_buf, (short)90, (short)8);
		 
		len = Common.convert_hex_amount_to_displayable_LE(apdu_buf,(short)90,apdu_buf,buf_offs, 
				 apdu_buf, (short)(buf_offs + 21),LOOP2);
		
		buf_offs += len;
		apdu_buf[buf_offs++] = ' ';
        if(base_address.is_USDT()){
			buf_offs =  get_Unit_string(apdu_buf,buf_offs,base_address.COIN_TYPE_BTC);	
        }
		else{
			buf_offs =  get_Unit_string(apdu_buf,buf_offs,base_address.get_coind_type());	
		}
		
		apdu_buf[buf_offs++] = 0x00;
		
			
		View.drawString((short)0, (short)(3*baseY), apdu_buf, (short)0, (short)buf_offs, true, View.VIEW_STRING_ALGN_LEFT);
		
		
	    short tempX =  (short)(max_X-24 - 38);
		View.drawSysBitmap((short)tempX, (short)(max_Y-16),View.VIEW_BMP_OK);
	    tempX = (short)( max_X - 44 );
		View.drawString(tempX, (short)(max_Y-12), UI.Im_Sure, (short)0, (short)UI.Im_Sure.length,
						 false, View.VIEW_STRING_ALGN_LEFT);
		
		View.refreshScreen();
		return  0;
	
	}
	// 显示地址，金额 在收款页
	short show_page(
	    		byte[]  amnout_buf, short amnout_buf_offs,
	    		byte[]  addr_buf ,  short add_buf_offs,   short addr_buf_len,
	    		boolean show_up, boolean show_down,
	    		byte[] apdu_buf)
	{
	    	View.clearScreen(false);


			short baseY = (short)(10);
		    short buf_offs = _0;
		    buf_offs = (short)UI.Confirm_sending.length;
		    
		    byte[] coin_ico = get_coin_ico();
			View.drawBitmap((short)(max_X - 24),(short)0,(short)24,(short)24,coin_ico,_0,(short)coin_ico.length);

			// 绘制 Confirm outpu#的字符串
			Util.arrayCopyNonAtomic(
									UI.Confirm_sending, _0, apdu_buf,_0, buf_offs );

		    apdu_buf[buf_offs++] = 0x00;	
		 
		    View.drawString((short)0, (short)0, apdu_buf, (short)0, (short)buf_offs, false, View.VIEW_STRING_ALGN_LEFT);

		    Util.arrayFillNonAtomic(apdu_buf,_0,(short)50,(byte)0x00);

		    
		    buf_offs= _0;
			// 绘制amout的字符串
		    Util.arrayCopyNonAtomic(amnout_buf, amnout_buf_offs, apdu_buf, (short)90, (short)8);
			short len =0;
			if(base_address.is_USDT())
				len = Common.convert_amount_to_displayable_BE(apdu_buf,(short)90,apdu_buf,buf_offs, 
													 apdu_buf, (short)(buf_offs + 21),(byte)8);
			else	
				len = Common.convert_hex_amount_to_displayable_LE(apdu_buf,(short)90,apdu_buf,buf_offs, 
									 apdu_buf, (short)(buf_offs + 21),LOOP2);									 
	       
	        buf_offs += len;
	        apdu_buf[buf_offs++] = ' ';
	        	
		    buf_offs =  get_Unit_string(apdu_buf,buf_offs,base_address.get_coind_type());
		    
			apdu_buf[buf_offs++] = 0x00;

			
	        View.drawString((short)0, (short)(baseY), apdu_buf, (short)0, (short)buf_offs, true, View.VIEW_STRING_ALGN_LEFT);

		
	
		    View.drawString((short)0, (short)(baseY*2), UI.to, (short)0, (short)UI.to.length, false, View.VIEW_STRING_ALGN_LEFT);

	       
			// 绘制addr的字符串

			View.drawString((short)0, (short)(3*baseY -1 ), addr_buf, (short)add_buf_offs, (short)addr_buf_len, true, View.VIEW_STRING_ALGN_LEFT);

	        short tempX = (short)(max_X - 24 - 36);      
			View.drawSysBitmap(tempX, (short)(max_Y-16),View.VIEW_BMP_OK);
			

	        tempX = (short)( max_X - 42 );
		    View.drawString(tempX, (short)(max_Y-12), UI.Confirm, (short)0, 
							(short)UI.Confirm.length, false, View.VIEW_STRING_ALGN_LEFT);

				
				if(show_up == true  && show_down == true ){
					// temp = "^ v ok Confirm";
					 View.drawSysBitmap((short)(0), (short)(max_Y-16),View.VIEW_BMP_DOWN);
					 View.drawSysBitmap((short)(24), (short)(max_Y-16),View.VIEW_BMP_UP );
				}
				else if(true == show_up){
					// temp = "^ ok Confirm";
					 View.drawSysBitmap((short)(0), (short)(max_Y-16),View.VIEW_BMP_UP);
				}
			    else if(true == show_down){
			    	// temp = "v ok Confirm";
				    View.drawSysBitmap((short)(0), (short)(max_Y-16),View.VIEW_BMP_DOWN);
			    }
				//System.out.println(temp);
				View.refreshScreen();
				return _0;
	    }
	    
	    
	    
/*
	 * 比特币单位换算关系 1比特币 BTC cBTC mBTC μBTC satoshi

		1比特币（Bitcoins，BTC）  
		
		0.01比特分（Bitcent，cBTC）
		
		0.001毫比特（Milli-Bitcoins，mBTC）
		
		0.000001微比特（Micro-Bitcoins，μBTC或uBTC） 
		
		0.00000001聪（satoshi）（基本单位） 
		
		1 bitcoin (BTC) = 1000 millibitcoins (mBTC) = 1 million microbitcoins (uBTC) = 100 million Satoshi  
	 */
	static final byte U_UNIT  = 0;
	static final byte U_cUNIT = 1;
	static final byte U_mUNIT = 2;
	static final byte U_uUNIT = 3;
	static final byte U_satoshi = 4;
	
    static  byte[] LOOP2_VALUE_buf = {
    		8,6,5,2,0
    };
  
   
	byte coin_unit;
	

	void set_coin_unit(byte _unit)
	{
		
		switch (_unit) {
		case U_UNIT:
			if(coin_unit != U_UNIT){
				coin_unit = U_UNIT;
				LOOP2 =  LOOP2_VALUE_buf[coin_unit];
			}
			break;
		case U_cUNIT:
			if(coin_unit != U_cUNIT){
				coin_unit = U_cUNIT;
				LOOP2 =  LOOP2_VALUE_buf[coin_unit];	
			}
			break;
		case U_mUNIT:
			if(coin_unit != U_mUNIT){
				coin_unit = U_mUNIT;
				LOOP2 =  LOOP2_VALUE_buf[coin_unit];
			}
			break;
		case U_uUNIT:
			if(coin_unit != U_uUNIT){
				coin_unit = U_uUNIT;
				LOOP2 =  LOOP2_VALUE_buf[coin_unit];
			}
			break;
		case U_satoshi:
			if(coin_unit != U_satoshi){
				coin_unit = U_satoshi;	
				LOOP2 =  LOOP2_VALUE_buf[coin_unit];
			}
			break;
		default:
		    Error.wrong6A86_incorrect_P1P2();
			break;
		}
	}	
   
	public void show_coin_ico(){  
	 	View.clearScreen(false);
	    byte[] coin_ico = get_coin_ico();
		View.drawBitmap((short)(max_X /2 - 12),(short)(max_Y /2  - 12),(short)24,(short)24,coin_ico,_0,(short)coin_ico.length);
		View.refreshScreen();
	} 
	
	public  byte[] get_coin_ico()
	{
		byte coin_type = base_address.get_coind_type();
		switch(coin_type)
		{
			case Address.COIN_TYPE_BTC:
				return  btcIco;		
			case Address.COIN_TYPE_BCH:
				return  ico_bch;
			case Address.COIN_TYPE_LTC:
				return  ico_ltc;	
		case Address.COIN_TYPE_USDT:
			    return ico_usdt;
			default:
				Error.wrong6AA5_NOT_SUPPORT_COIND_TYPE();
				break ;
		}
	    return null;
	}  
	 
}



