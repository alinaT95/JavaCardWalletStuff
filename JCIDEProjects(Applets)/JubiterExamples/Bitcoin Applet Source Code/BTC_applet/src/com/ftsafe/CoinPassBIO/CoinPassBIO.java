/**
 * 
 */
package com.ftsafe.CoinPassBIO;

import com.ftsafe.javacardx.IOUtil.Buttons;
import com.ftsafe.javacardx.IOUtil.Timer;
import com.ftsafe.javacardx.btcmgr.CoinManager;


import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.JCSystem;
import javacard.framework.ISOException;
import javacard.framework.Util;
import javacard.security.MessageDigest;
import javacard.framework.*;
import javacardx.apdu.ExtendedLength;

/**
 * @author wang ri bin
 *
 */
public class CoinPassBIO extends Applet implements AppletEvent ,ExtendedLength
{
	

	
	static byte[] change_addr_path = {(byte) 0x6D,(byte) 0x2F,(byte) 0x34,(byte) 0x34,(byte) 0x27,(byte) 0x2F,(byte) 0x30,(byte) 0x27,(byte) 0x2F,(byte) 0x30,(byte) 0x27,(byte) 0x2F,(byte) 0x30,(byte) 0x2F,(byte) 0x30};

	static byte[] COIND_TYPE = {
		0x02,'0','\'',
		0x02,'2','\'',
		0x04,'1','4','5','\'',
		
	};
	public  static final short _0 = 0x00;
	
	public  static final short _4 = 0x04;
	
	public  static final short _32 = 32;
  
	


	public  static final byte[]  mainnet_private_prefix= {
		
		 0x04, (byte)0x88, (byte)0xAD, (byte)0xE4
		 
	 };

	public  static final byte[]  x_mainnet_public_prefix= {
		
		  0x04, (byte)0x88, (byte)0xB2, (byte)0x1E
		  
	 };
	
	// 0x04 9d 7c b2;
	public  static final byte[]  y_mainnet_public_prefix= {
			  0x04, (byte)0x9d, (byte)0x7c, (byte)0xb2
	};
	public  static final byte[]  testnet_private_prefix= {
		 0x04, (byte)0x35, (byte)0x83, (byte)0x94
	 	 
	 };
	 

	public  static final byte[]  testnet_public_prefix = {
		
		  0x04, (byte)0x35, (byte)0x87, (byte)0xCF
		  
	};
	static final byte INPUT_NUM_LIMIT = 0X04;

	private final byte internal_version[] =
	{
		0x00, 0x00,		// java card
		0x00, 0x01,		// 1
		0x14, 0x01,		// Bitcoin wallet, subversion 1
		0x00, 0x00, 0x38, (byte)0x34,	// svn version
		0x18, 0x09, 0x21, 0x16, 0x07, 0x00
	};
	//：Three, such as 1.0.00, the first in the architecture, the interface has a big change or a milestone version of the modification, the second in the new add function modification, the third in the fix bug modification.
	private final byte version[] ={
		0x01,
		0x08,
		0x00,0x02
	};
	private final byte get_version[] =
	{
		(byte)0x80,
		(byte)0xE2,
		(byte)0x80,
		(byte)0x00,
		(byte)0x05,
		(byte)0xDF,
		(byte)0xFF,
		(byte)0x02,
		(byte)0x80,
		(byte)0x01
	};
		
	static private final byte[] COIN_KEY_TYPE_PRELOAD_DATA = {
		0x6D , 0x2F, 0x34, 0x34, 0x27, 0x2F, 0x30, 0x27 
	};
	
   
 
	

	private  MessageDigest sha256Assist;
	private  MessageDigest sha256;
	private  MessageDigest _RIPEMD160;
	
	public  static final byte AMOUNT_V_LEN = 0x08;
	private boolean[] random_mark;
	

	byte[] g_larger_buf_in_RAM;

	// Used to store the required data length, and data offset
	short[] short_buf_in_RAM;
		
	// The length of the data that has been accepted
	static final byte REMAIN_DATA_LEN                           = 0x00;
	
 	static final byte AMOUNT_PUB_PATH_DATA_LEN_OFFSET           = 0X01;
 	
    // Record the offset of the transaction data in the buf
    static final byte TRANSATION_DATA_LEN_OFFSET                = 0X02;

    static final byte DATA_LEN_IN_RAM_OFFSET          = 0X03;
    static final byte CHANGE_ADDR_DATA_LEN_OFFSET                = 0X03;
    static final byte WLEN_IN_READ_SIGNED_DATA_OFFS          = 0X03;
    
    static final byte TIMER_COUNT          = 0X03;
    
    short max_timer_count;
    
  public byte[]  flash_transation__buf;
  public byte[]  flash_amount_pubkey_path_buf;	
  public byte[]  flash_preimage_hash_buf;	
  public byte[]  g_temp_buffer_in_RAM;	
   public byte[] store_TL_buffer;
  byte[]  addr_idx_buf;

  Address    base_addres;
  CashAddres cash_addres;

  
  // Store parse state,input number, and wignet  flage     
  public byte[] byte_buf_in_RAM ;
  static final byte TRANSATION_INPUT_COUNTS_OFFSET        	 = 0X00;
  static final byte TRANSATION_WIGNET_TYPE_OFFSET            = 0X01;
  static final byte TRANSATION_PARSE_STATE_OFFSET            = 0X02;
  static final byte TRANSATION_STORAGE_SCHEME_OFFSET         = 0X03;
  
  static final byte STATE_NONE = 0x00;
  static final byte STATE_INPUT_DONE = 0x08;
  static final byte STATE_AMOUNT = 0x01;
  
  static final byte STATE_AMOUNT_DONE = 0x02;
   
  static final byte STATE_PATH = 0x03;
  
  static final byte STATE_PATH_DONE = 0x04;
    
  static final byte STATE_TRANSACTION = 0x05;
  
  static final byte STATE_TRANSACTION_DONE = 0x06;
  
  static final byte STATE_CHANGE_ADDR = 0x07;
  
  
  static final byte STATE_DONE = 0x08;
  

   static final byte[] _AID = {(byte)0xD1, 0x56 ,0x00, 0x01, 0x32 ,(byte)0x83 ,0x00, 0x42 ,0x4C ,0x44, 0x00 ,0x00 ,0x42, 0x54 ,0x43,0x01};
	static final byte P1_ONLY   = 0X00;
	static final byte P1_FIRSET = 0X01;
	static final byte P1_NEXT = (byte)0x02;
	static final byte P1_LAST = (byte)0x03;
	
	static final byte SIG_LEN = 65;
	/*
	 * Hash160  = Hash256 + RIPEMD160
	*/
	static final byte  PIN_MODE_CLARE_9GRIDS = (byte)0X80;


	UI  ui_mgr;
	public static void install(byte[] bArray, short bOffset, byte bLength)
	{
		// GP-compliant JavaCard applet registration
		CoinPassBIO app = new CoinPassBIO(bArray,bOffset,bLength);
		//app.register(bArray, (short) (bOffset + 1), bArray[bOffset]);
		app.register();
		
	}

	public void uninstall(){
		JCSystem.requestObjectDeletion();	
	}

    /*
     * 
     *  Strategy: store signed values first, followed by the original hash, followed by unsigned transaction data
     */
	  // Only   signature value  store in RAM buf
	  static final byte  STORAGE_ONLY_SIGN_DATA= 0x01;
	  
	  //  signed values and unsigned transaction data store in RAM buf
	  static final byte  STORAGE_SIGN_AND_TRANSATION= 0x03;
	  
	  //  signed values, unsigned transaction data, premage  hash values store in RAM buf
	  static final byte  STORAGE_SIGN_TRANSATION_HASH= 0x04;
	  
	  //  signed values, unsigned transaction data, premage hash values, and amount_pubkey values 
	  static final byte  STORAGE_SIGN_TRANSATION_HASH_AMOUNT_PUBKEY= 0x05;
	void recycle_storage(){
		
		boolean need_requestObjectDeletion = false;
		
		if(flash_transation__buf != null){
			flash_transation__buf = null;
			need_requestObjectDeletion = true;
		}
		if(flash_amount_pubkey_path_buf != null){
			flash_amount_pubkey_path_buf = null;
			need_requestObjectDeletion = true;
		}
		if(flash_preimage_hash_buf != null){
			flash_preimage_hash_buf = null;
			need_requestObjectDeletion = true;
		}
		
		if(need_requestObjectDeletion){
			JCSystem.requestObjectDeletion();
		}	
	}
	
	// Allocate storage
	void allocate_storage()
	{
		 if(byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET] == STORAGE_SIGN_TRANSATION_HASH_AMOUNT_PUBKEY){
			 return;
		 }
		 recycle_storage();
		 short nInputCount = (short)(byte_buf_in_RAM[ TRANSATION_INPUT_COUNTS_OFFSET ] & 0xff);
		switch (byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET]) {
		  // Only   signature value  stored in RAM buf
		case  STORAGE_ONLY_SIGN_DATA:
			flash_transation__buf= new byte[byte_buf_in_RAM[ TRANSATION_INPUT_COUNTS_OFFSET ]];
			flash_amount_pubkey_path_buf = new byte[short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET]];
			flash_preimage_hash_buf = new byte[(short)(nInputCount*33)];
		  break;
		  //   signed values and unsigned transaction data store in RAM buf
		case  STORAGE_SIGN_AND_TRANSATION:
			flash_amount_pubkey_path_buf = new byte[short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET]];
			flash_preimage_hash_buf = new byte[(short)(nInputCount*33)];
			break;
		  
		  // signed values, unsigned transaction data, premage  hash values store in RAM buf
		case  STORAGE_SIGN_TRANSATION_HASH:
			flash_amount_pubkey_path_buf = new byte[short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET]];
			break;
		  
		  //  signed values, unsigned transaction data, premage hash values, and amount_pubkey values 
		case STORAGE_SIGN_TRANSATION_HASH_AMOUNT_PUBKEY:
			break;

		default:
			break;
		}	
	}
	// Calculate the scheme used for storage
	void calculate_storage_scheme(){
		   
		   short nInputCount = (short)(byte_buf_in_RAM[ TRANSATION_INPUT_COUNTS_OFFSET ] & 0xff);
		   
		   short  len = (short)(short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ] + 
				   short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET] + nInputCount*99 );// 1 + 65 + 33 = 99
		   
		  
		   if(len <= g_larger_buf_in_RAM.length){
			   byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET] = STORAGE_SIGN_TRANSATION_HASH_AMOUNT_PUBKEY;
			   return;
		   }
		   
		   len = (short)(short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ]  + nInputCount*99 );// 1 + 65 + 33 = 99
		   if(len <= g_larger_buf_in_RAM.length){
			   byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET] = STORAGE_SIGN_TRANSATION_HASH;
			   return;
		   }
		   len = (short)(short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ]  + nInputCount*66 );// 1 + 65 
		   if(len <= g_larger_buf_in_RAM.length){
			   byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET] = STORAGE_SIGN_AND_TRANSATION;
			   return;
		   }
		   byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET] = STORAGE_ONLY_SIGN_DATA;
		   
	}

   void get_pub_key_when_process_path_state()
   {	
   	    //Data storage:  amount data || pubkey data  || path data
	    short nInputCount =  get_input_count();
		short  amount_pubkey_path_buf_offset = (short)(8*nInputCount);
		short  path_offset = (short)(41*nInputCount +2);// 8 + 33 
		
		short len ;
		for(short i = 0; i < nInputCount; ++i){
			
		   len  = g_larger_buf_in_RAM[path_offset++];
		   

			
			if(0 != CoinManager.getCoinPubData(g_larger_buf_in_RAM,path_offset, len,g_temp_buffer_in_RAM,_0,null,(short)0)){
				Error.wrong6A9F_GET_COIN_DATA_FAILED();
			}
		   
		
				
			if((g_temp_buffer_in_RAM[(short)64] & 0x01) > 0){//The compressed form of the public key
				g_temp_buffer_in_RAM[0] = 0x03; 
			}
			else{
				g_temp_buffer_in_RAM[0] = 0x02; 
			}
			path_offset += len;
			Util.arrayCopyNonAtomic(g_temp_buffer_in_RAM, _0,g_larger_buf_in_RAM,amount_pubkey_path_buf_offset,(short)33);
			amount_pubkey_path_buf_offset += 33;
		}
		Util.arrayFillNonAtomic(g_temp_buffer_in_RAM, _0, (short) g_temp_buffer_in_RAM.length, (byte)0);
		
   }
   
   
   
   byte[] get_transation_data_buf()
   {
	  if(STORAGE_ONLY_SIGN_DATA != byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET]){
	     return g_larger_buf_in_RAM;
	  }
	  else{
		  return flash_transation__buf;
	  }
   }
	short get_script_sig_buf_offs(){
		
		if(STORAGE_ONLY_SIGN_DATA != byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET]){
			return short_buf_in_RAM[TRANSATION_DATA_LEN_OFFSET];
		}
		else{
			return _0;
		}
	}
	
	byte[] get_preimage_hash_buf(){
		switch(byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET]){
		case STORAGE_SIGN_TRANSATION_HASH:
		case STORAGE_SIGN_TRANSATION_HASH_AMOUNT_PUBKEY:
			return g_larger_buf_in_RAM;
		default:
			return flash_preimage_hash_buf;
		}
	   
	}
	short get_preimage_hash_buf_offs(){
		
		switch(byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET]){
		case STORAGE_SIGN_TRANSATION_HASH:
		case STORAGE_SIGN_TRANSATION_HASH_AMOUNT_PUBKEY:
		{
			short nInputCount = get_input_count();
			return (short)(short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ] + nInputCount*66 );// 1 + 65
		}
		default:
			return _0;
		}
	}

	void store_amount_pubkey_and_path()
	{
		if( byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET] == STORAGE_SIGN_TRANSATION_HASH_AMOUNT_PUBKEY){
			
			  short nInputCount = get_input_count();

			 short  amount_pubkey_path_buf_offset = (short)(short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ] + nInputCount*99);// 1+ 65 + 33 = 99
		
			Util.arrayCopyNonAtomic(g_larger_buf_in_RAM, _0, g_larger_buf_in_RAM, amount_pubkey_path_buf_offset, short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET]);
		  
		}
		else{
			
			Util.arrayCopyNonAtomic(g_larger_buf_in_RAM, _0, flash_amount_pubkey_path_buf, _0, short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET]);
		}	
    }
	byte[] get_amount_pubkey_path_buf(){
		

		if( byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET] == STORAGE_SIGN_TRANSATION_HASH_AMOUNT_PUBKEY){
			
			return g_larger_buf_in_RAM;
		}
		else{
			
			return flash_amount_pubkey_path_buf;
		}	
			
	}
	
	short get_amount_pubkey_path_offs(){
		
		if( byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET] == STORAGE_SIGN_TRANSATION_HASH_AMOUNT_PUBKEY){
			
			short nInputCount = get_input_count();
			short  amount_pubkey_path_buf_offset = (short)(short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ] + nInputCount*99);// 1 + 65 + 33 = 99
		    return amount_pubkey_path_buf_offset;
		  
		}
		else{
			// AMOUNT_PUB_PATH  offset on  flash buf;
			return _0;
		}	
	}
	byte[] get_change_addr_buf(){
		
		short count = (short)(g_temp_buffer_in_RAM[0] & 0xff);
		if(count > (g_temp_buffer_in_RAM.length - 1)){
			if(addr_idx_buf == null){
				addr_idx_buf =new byte[256];
			}
			return addr_idx_buf;
		}
		return g_temp_buffer_in_RAM;
		
	}
	
	short getOutputOffs(){
	    boolean isWignet = is_withness();
	    short nInputCount  = get_input_count();
	    short offs;
	    
	    if(isWignet == false )
	    {
		    // only for non-segwit
			// [nVersion] [nInputCount] [txInputs] [nOutputCount] [txOutputs] [nLockTime]
			offs = (short)(5 + 0x29*nInputCount);
	    }
	    else
	    {
			// only for segwit
			// signed transaction: 
			//[nVersion][marker][flag][txins][txouts][witness][nLockTime]
			offs = (short)(7 + 0x29*nInputCount);
	    }
		return offs;
	}
	
	short get_output_offs_for_one_index(short index){  	
	    short  output_offs = getOutputOffs();
	    byte[] transation_buf = get_transation_data_buf();
	    short  output_count = transation_buf[output_offs++];
	    
	    for(short i = 0 ; i < output_count ; ++i){
	    	
		    if(index == i){
		    	return output_offs;
		    }
		    output_offs += 8;
		    byte pubkey_len = transation_buf[output_offs++];
		    output_offs += pubkey_len;
	    } 	
		Error.wrong6A98_NOT_FOUND_OUTPUT();		
	    return -1;
   }


	CoinPassBIO(byte[] bArray, short bOffset, byte bLength )
	{
	
		short len;
		len = CoinManager.registerCoinType( (short)3, COIND_TYPE, _0, (short)COIND_TYPE.length);
		if(len != 0){
			Error.wrong6A9E_REGISTER_FAILED();
		}
		cash_addres = new CashAddres();
		base_addres = new Address();
		ui_mgr  = new UI(base_addres);
		
		
		
		random_mark = JCSystem.makeTransientBooleanArray((short)1, JCSystem.CLEAR_ON_DESELECT);
	
		sha256 = MessageDigest.getInstance(MessageDigest.ALG_SHA_256,false);
		
		sha256Assist = MessageDigest.getInstance(MessageDigest.ALG_SHA_256,false);
		
		_RIPEMD160 = MessageDigest.getInstance(MessageDigest.ALG_RIPEMD160,false);
			
		flash_amount_pubkey_path_buf = null;
		
		flash_transation__buf= null;
		flash_preimage_hash_buf = null;
		
	 
		byte_buf_in_RAM = JCSystem.makeTransientByteArray((short) 4, JCSystem.CLEAR_ON_DESELECT);
		
		short_buf_in_RAM = JCSystem.makeTransientShortArray((short)4, JCSystem.CLEAR_ON_DESELECT);
		
		
		g_temp_buffer_in_RAM = JCSystem.makeTransientByteArray((short) 72, JCSystem.CLEAR_ON_DESELECT);
		
		store_TL_buffer = JCSystem.makeTransientByteArray((short)4, JCSystem.CLEAR_ON_DESELECT);
		
		addr_idx_buf = null;
		
		
		len = JCSystem.getAvailableMemory(JCSystem.CLEAR_ON_DESELECT);
		
		g_larger_buf_in_RAM= JCSystem.makeTransientByteArray(len, JCSystem.CLEAR_ON_DESELECT);
	
	    max_timer_count = 240;
	}
	// get length   of a TLV struct data
	private short getLength(byte[] buf, short offs) {

		short length = (short)(buf[offs] & 0xff);
		if(length <= 0x7f) {

		} else if(length == 0x81) {
			length = (short)(buf[(short)(offs + 1)] & 0xff);
		} else if(length == 0x82) {
			length = Util.getShort(buf, (short) (offs + 1));
		} else {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}
		return length;
	}
	// get length  bytes of a TLV struct data
	private short getLengthBytes(short len) {
		if(len < (short)0x0080) {
			return 1;
		} else if(len <= (short)0x00ff) {
			return 2;
		} else {
			return 3;
		}
	}
	
    // help funtion of get public address
	short get_public_addr_help(byte addr_type,byte[] buf,
		     byte[] path_buf,	short path_offs,short path_len){
		// 在这里获取pubkey的数据
		short len = CoinManager.getCoinPubData(path_buf,path_offs, path_len,g_larger_buf_in_RAM,_0,null,(short)0);
	    if(len != 0){
		   Error.wrong6A9F_GET_COIN_DATA_FAILED();
	    }
	    
	    if((g_larger_buf_in_RAM[(short)64] & 0x01) > 0){// The compressed form of the public key
	    	g_larger_buf_in_RAM[0] = 0x03; 
	    }
	    else{
	    	g_larger_buf_in_RAM[0] = 0x02; 
	    }
	    
		len = _0;
	    if(addr_type ==  Address.P2SH_P2WPKH)
	    {
			// scriptPubKey: OP_HASH160    [Hash160(redeemScript)]    OP_EQUAL
			// reedemScript：0x00  +  0x14  +  [Hash160(pubkey)] 
			len = sha256.doFinal(g_larger_buf_in_RAM, _0, (short) 33, buf, _0);
			len = _RIPEMD160.doFinal(buf, _0, len,g_larger_buf_in_RAM, (short)2);
			g_larger_buf_in_RAM[0] = 0x00;
			g_larger_buf_in_RAM[1] = 0x14;
			len = 22; // 0x00  +  0x14  +  [Hash160(pubkey)] 
	    }
	    else{
	    	len = 33;
	    }
	    
		if(addr_type !=  Address.P2WPKH)
		{
			   
			buf[0] =  base_addres.get_pubkey_version(addr_type);
			len = sha256.doFinal(g_larger_buf_in_RAM, _0, len, buf, (short)1);
			len = _RIPEMD160.doFinal(buf, (short)1, len,buf, (short)1);
			
			 if(!base_addres.is_BCH())// BTC,LTC,USDT
			{	
				short  offsetBeforeChecksum = (short)(len+1);
				
				len = sha256.doFinal(buf,_0,offsetBeforeChecksum,buf,offsetBeforeChecksum);
				
				len = sha256.doFinal(buf,offsetBeforeChecksum,len,buf,offsetBeforeChecksum);

				offsetBeforeChecksum += 4;
				len =  Base58.encode(buf,_0, offsetBeforeChecksum, g_larger_buf_in_RAM,_0);
			}

		   else 
		   {

			   len = cash_addres.cash_addr_encode(     g_larger_buf_in_RAM,_0, 
												  cash_addres.hrp,_0, (short)cash_addres.hrp.length, 
												  buf, _0,(short)21 , g_larger_buf_in_RAM, (short)200);
		   }
	
		 }
		

		return len;
	}
   // get public address 
	void get_public_addr(APDU apdu)
	{
        apdu.setIncomingAndReceive();
       
		byte[] buf = apdu.getBuffer();
		byte p1 = buf[ISO7816.OFFSET_P1];
		byte addr_type  = buf[ISO7816.OFFSET_P2];
		
		short offs = ISO7816.OFFSET_CDATA;
			
		if(buf[offs++] != Common.BIP32PATH_TAG) {
			
			ISOException.throwIt(Error.NOT_FOUND_BIP32PATH_TAG);
		}

		short len = getLength(buf, offs);
		
		offs += getLengthBytes(len);
		
	    short out_len = get_public_addr_help(addr_type,buf,buf,offs,len);
		
		if(0x00 ==   p1)
		{
			IO.apdusend(apdu, g_larger_buf_in_RAM, _0, out_len);
			return;
		}
		else if(0x02 == p1){
            base_addres.add_coin_info(g_larger_buf_in_RAM,out_len);
			IO.apdusend(apdu, g_larger_buf_in_RAM, _0, out_len);
			return;
		}
		if(0x01 != p1){
			Error.wrong6A86_incorrect_P1P2();
		}
		short keyPress;
		g_larger_buf_in_RAM[out_len] = 0x00;
		short_buf_in_RAM[TIMER_COUNT] = 0;
		short addr_off = _0;
		short addr_len =(short)(out_len+1);
		
	    if(base_addres.is_BCH()){
		    addr_off = (short)(cash_addres.hrp.length + 1);
		    addr_len -= addr_off; 
	    }
		while(true)
		{
			
		    ui_mgr.show_addr(g_larger_buf_in_RAM,addr_off,addr_len); 
			while(true){
				
				 // Wait for the button, if it is OK, enter the confirmation page and jump out of the current loop
				 
				 keyPress = Buttons.getKeysPressed();
				
				 if((keyPress& Buttons.BUTTON_DOWN_PRESSED) == Buttons.BUTTON_DOWN_PRESSED){
					  break;//jump out of the loop
				 }
				 else if((keyPress& Buttons.BUTTON_OK_PRESSED) == Buttons.BUTTON_OK_PRESSED){
					
					 break;//jump out of the loop
				 }
				 else if((keyPress& Buttons.BUTTON_CANCEL_PRESSED) == Buttons.BUTTON_CANCEL_PRESSED){
				 
					 break;//jump out of the loop
			     }
			     else{
				      timer_wait();
			     } 
		 }
		 
		 if((keyPress& Buttons.BUTTON_DOWN_PRESSED) == Buttons.BUTTON_DOWN_PRESSED)
		 {
			 ui_mgr.show_QR_Code(g_larger_buf_in_RAM,_0,addr_len);
			 
			 while(true){
				 
				 keyPress = Buttons.getKeysPressed();
				 if((keyPress& Buttons.BUTTON_OK_PRESSED) == Buttons.BUTTON_OK_PRESSED){
					
					 break;//jump out of the loop
				 }
				 else if((keyPress& Buttons.BUTTON_CANCEL_PRESSED) == Buttons.BUTTON_CANCEL_PRESSED){
					 break;//jump out of the loop
				 }
				 else  if((keyPress& Buttons.BUTTON_UP_PRESSED) == Buttons.BUTTON_UP_PRESSED){
					
					 break;//jump out of the loop
				 }
				 else{
					  timer_wait();
				 }
			 }
		 }
		 
		 if((keyPress& Buttons.BUTTON_OK_PRESSED) == Buttons.BUTTON_OK_PRESSED){	
				
				break ;
		 }
		 else if((keyPress& Buttons.BUTTON_CANCEL_PRESSED) == Buttons.BUTTON_CANCEL_PRESSED){
				
				break ;
		 }
		 
		}
		UI.showMainUI(true);
		IO.apdusend(apdu, g_larger_buf_in_RAM, _0, out_len);	
	}
	
	void get_index_in_bp32path(byte s[] , short s_offset , short len, byte[] o , short o_offset)  
	{  
		short i;  
		int n = 0;  
		byte item;
		boolean hardened  = false;
		for (i = 0; i < len; ++i)  { 
			item =  s[i + s_offset];
			if(item == '\''){
				if(++i != len){
					// incorrect format
					 ISOException.throwIt(Error.NO_CORRECT_BIP32PATH_FORMAT);
				}
				hardened = true;
				break ;
			}
			if(item < '0' || item > '9'){
		   	  // incorrect format
				 ISOException.throwIt(Error.NO_CORRECT_BIP32PATH_FORMAT);
	   	    } 
	   	    
			n = 10 * n + (item - '0');  
		}  

		byte high_byte =(byte)(n >> 24);

		if(hardened){
			high_byte = (byte)((high_byte + 0x80) & 0xff);
		}
		o[o_offset++] = high_byte;

		o[o_offset++] = (byte)(n >> 16);
		o[o_offset++] = (byte)(n >> 8);
		o[o_offset++] = (byte)(n);
		
		
	} 
	short get_child_index(byte[] pathBuf, short pathOffset, short endOffset,byte[] child_index, short child_index_offs){  
		   short i  = (short)(g_larger_buf_in_RAM.length - 20);
		   short vPathOffsetEnd = parse_bip32path(pathBuf,pathOffset,endOffset,g_larger_buf_in_RAM,i,(short)g_larger_buf_in_RAM.length); 
		   short curHeyDepth;
	       if(0 == vPathOffsetEnd ){
	    	   curHeyDepth =  _0;
	    	   Util.arrayFillNonAtomic(child_index, child_index_offs, _4, (byte)0);
	       }
	       else{
	    	     curHeyDepth= (short)((vPathOffsetEnd - i) >> 2);
	    	     Util.arrayCopyNonAtomic(g_larger_buf_in_RAM, (short)(vPathOffsetEnd - 4), child_index, child_index_offs, _4);
	       }
		   return curHeyDepth;
    }
	
	short parse_bip32path(byte[] pathBuf, short pathOffset, short endOffset ,
			byte[] output, short out_offset,short out_endOffset)
	   {
		
	   	   if(pathBuf[pathOffset]  != 'm'){
		   	  // incorrect format
			   ISOException.throwIt(Error.NO_CORRECT_BIP32PATH_FORMAT);
	   	   }
	   	  
	   	   if( (pathOffset + 1) == endOffset){
		   	   return  _0;
	   	   }
	   	   
		   pathOffset+=2;
	   	   short  tempIndex = 0x00;
	   	   byte item ;
	   	   
	   	   // In vPathOffset, store any possible path values after m
	   	   for(; pathOffset < endOffset; ++pathOffset){
	   	   	   item =  pathBuf[pathOffset];
		   	   if(item == '/'){
		   	   	
		   	   	  
		   	   	  if(tempIndex > 0x0B){
			   	   	  // out of range
			   	   	 
					  ISOException.throwIt(Error.NO_CORRECT_BIP32PATH_FORMAT);
		   	   	  }
		   	   
		   	   	  if( (out_offset + 4) > out_endOffset){
			   	   	    // out of range
			   	   	  
					  ISOException.throwIt(Error.NO_CORRECT_BIP32PATH_FORMAT);
		   	   	  }

			
			   	  get_index_in_bp32path(g_larger_buf_in_RAM,_0,tempIndex,g_larger_buf_in_RAM,out_offset);
			
			   	  out_offset += 4;
			   	  tempIndex = 0x00;
		   	   }
		   	   else{
		   		  g_larger_buf_in_RAM[tempIndex] = item;
				  tempIndex += 1;   
		   	   }	   
		   	   
	   	   }

		
		   if(tempIndex == 0){
		   	
			   ISOException.throwIt(Error.NO_CORRECT_BIP32PATH_FORMAT);
		   }

		   get_index_in_bp32path(g_larger_buf_in_RAM,_0,tempIndex,g_larger_buf_in_RAM,out_offset);
		
		   out_offset += 4;

		   return out_offset;
		   
	   }
	   
	void get_public_node(APDU apdu){
		 apdu.setIncomingAndReceive();
		byte[] buf = apdu.getBuffer();
		short offs = ISO7816.OFFSET_CDATA;
		byte p1 = buf[ISO7816.OFFSET_P1];
        if(p1 != 0x00)
        {
		    Error.wrong6A86_incorrect_P1P2();
	    }
        byte p2 = buf[ISO7816.OFFSET_P2];
        if(p2 != 0x00  &&  p2 != 0x01){
        	 Error.wrong6A86_incorrect_P1P2();
        }
		if(buf[offs++] != Common.BIP32PATH_TAG) {
			ISOException.throwIt(Error.NOT_FOUND_BIP32PATH_TAG);
		}
		short len = getLength(buf, offs);

		offs += getLengthBytes(len);
		// Get the parent public key, get the fingerprint
		short endoff = (short)(offs + len);
		short i ;
		
		byte curHeyDepth = (byte) get_child_index(buf, offs, endoff, g_larger_buf_in_RAM,(short)(200));
		
		
		if(curHeyDepth != _0)// not m
		{
			for(i = endoff; i >= offs; --i){
				if(buf[i] == 0x2f){
					break ;
				}
			}
			if(0 != CoinManager.getCoinPubData(buf,offs, (short)(i - offs),g_larger_buf_in_RAM,_0,g_larger_buf_in_RAM,(short)65)){		 
				Error.wrong6A9F_GET_COIN_DATA_FAILED();
			}
			
			if((g_larger_buf_in_RAM[(short)64] & 0x01) > 0){// last bit of y // The compressed form of the public key
				g_larger_buf_in_RAM[0] = 0x03; 
			}
			else{
				g_larger_buf_in_RAM[0] = 0x02; 
			}
			
			sha256.doFinal(g_larger_buf_in_RAM,(short)0,(short)33,g_larger_buf_in_RAM,(short)0);
			_RIPEMD160.doFinal(g_larger_buf_in_RAM, (short)0, _32, g_larger_buf_in_RAM, (short)160);	// 这里会覆盖一些child_index的数据
		}
		else{//  it's m
			Util.arrayFillNonAtomic(g_larger_buf_in_RAM,(short)160,_4,(byte)0);
		}
		
		if(0 != CoinManager.getCoinPubData(buf,offs, len,g_larger_buf_in_RAM,_0,g_larger_buf_in_RAM,(short)65)){
			Error.wrong6A9F_GET_COIN_DATA_FAILED();
		}
	    if((g_larger_buf_in_RAM[(short)64] & 0x01) > 0){// last bit of y // The compressed form of the public key
	    	g_larger_buf_in_RAM[0] = 0x03; 
	    }
	    else{
	    	g_larger_buf_in_RAM[0] = 0x02; 
	    }
		
		offs = 0x00;
		if(_0 == p2){
			Util.arrayCopyNonAtomic(x_mainnet_public_prefix,_0,buf,offs,(short)4);
		}
		else{
			Util.arrayCopyNonAtomic(y_mainnet_public_prefix,_0,buf,offs,(short)4);
		}
		offs+= 4;

		buf[offs ++] = curHeyDepth;

       // ParentFingerPrint
		Util.arrayCopyNonAtomic( g_larger_buf_in_RAM, (short)160,buf,offs,(short)4);
		offs+=4;


	
       // child_number
		Util.arrayCopyNonAtomic( g_larger_buf_in_RAM, (short)200,buf,offs,(short)4);
		offs+=4;

       // chain_code
		Util.arrayCopyNonAtomic( g_larger_buf_in_RAM, (short)65,buf,offs,(short)32);
		offs+=32;

       // publicKeyData
		Util.arrayCopyNonAtomic( g_larger_buf_in_RAM,_0,buf,offs,(short)33);
		offs+= 33;

		
		len = sha256.doFinal(buf, _0, (short) offs,g_larger_buf_in_RAM, _0);
				

		len = sha256.doFinal(g_larger_buf_in_RAM, _0, (short) len,g_larger_buf_in_RAM, _0);
				

		Util.arrayCopyNonAtomic(g_larger_buf_in_RAM,_0, buf,offs,(short)4);
		offs += 4;



		len =  Base58.encode(buf,_0, offs, g_larger_buf_in_RAM,_0);

		IO.apdusend(apdu, g_larger_buf_in_RAM, _0, len);
		
	}
	
    void timer_wait(){
	    short_buf_in_RAM[TIMER_COUNT] +=1;
	    if(max_timer_count < short_buf_in_RAM[TIMER_COUNT]){
	    	ui_mgr.Time_out();
		    Error.wrong6AA4_Timer_out();
	    }
	   
		Timer.wait((short)500);
	    
    }
	void ecc_sign(APDU apdu){
		
		short nInputCount  = get_input_count();
		short  amount_pubkey_path_buf_offset = get_amount_pubkey_path_offs();
		short  path_offset = (short)(41*nInputCount + amount_pubkey_path_buf_offset);// 8 + 33 
		byte[] amount_pubkey_path_buf = get_amount_pubkey_path_buf();
		
		 //Data storage:  amount data || pubkey data  || path data
		short len ;
		byte[] apdu_buf = apdu.getBuffer();
	    if(byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] != STATE_DONE)
	    {
		    Error.wrong6AA3_TRANSATION_STATE();
	    } 
	    short_buf_in_RAM[TIMER_COUNT] = 0;
	    short keyPress;
	    while (true) {
	    	
	    	if(base_addres.is_USDT())
				keyPress = confirm_usdt_output(apdu_buf);
		    else
		    	keyPress = confirm_output(apdu_buf);
		    	
		    if((keyPress& Buttons.BUTTON_OK_PRESSED) == Buttons.BUTTON_OK_PRESSED){
		    	keyPress = calculate_fees(apdu);//Calculation fee
		    	if((keyPress& Buttons.BUTTON_UP_PRESSED) == Buttons.BUTTON_UP_PRESSED){
		    		continue;
		    	}
		    	else{
		    		break;
		    	}
		    }
		    break;
		}

	 
	   
	    if((keyPress& Buttons.BUTTON_OK_PRESSED) != Buttons.BUTTON_OK_PRESSED){
	    	
			Util.setShort(apdu_buf,_0,keyPress);
			apdu.setOutgoingAndSend((short)0, (short)2);
	    	Error.wrong6F09_REJECT();
	    }
	    
	    if(nInputCount >= INPUT_NUM_LIMIT){
		    ui_mgr.show_working();
	    }
	    
	    short total_len =  Util.getShort(amount_pubkey_path_buf, path_offset);
	    path_offset += 2;
	    
		short preimage_hash_offs = get_preimage_hash_buf_offs();
	    byte[] preimage_hash_buf = get_preimage_hash_buf();
	    short  sig_buf_offs = get_script_sig_buf_offs();
	    
	    total_len = 
	    		CoinManager.signCoinData(
	    		nInputCount,
	    		preimage_hash_buf,preimage_hash_offs,(short)(33*nInputCount),
	    		g_larger_buf_in_RAM,sig_buf_offs,
	    		amount_pubkey_path_buf,path_offset,total_len
	    		);
	    if(total_len != 0){
		    Error.wrong6F08_sign_failed();
	    }
	   
	    
		for(short i = 0; i < nInputCount; ++i)
		{			
			
			len  = amount_pubkey_path_buf[path_offset++];	
			
			preimage_hash_offs += 33;
									
			sig_buf_offs +=1;
			
			
			if( Common.greater( g_larger_buf_in_RAM, (short)(sig_buf_offs + 32), Common.top, (short)0, (short)32 ) ){
				 // Reorganize S and code der
				 Common.big_sub( Common.sum, (short)0, g_larger_buf_in_RAM, (short)(sig_buf_offs + 32), apdu_buf, (short)(64), (short)32 );
				 Util.arrayCopyNonAtomic( apdu_buf, (short)(64), g_larger_buf_in_RAM, (short)(sig_buf_offs + 32), (short)32 );

			}
			
			
			path_offset += len;
			sig_buf_offs += SIG_LEN;
			
		}
	
	
		
		switch(byte_buf_in_RAM[TRANSATION_WIGNET_TYPE_OFFSET]){
			case 	 Address.P2PKH :// 0x00;       // pay to public key  hash
				get_transation_len_for_p2pkh();	
				break;
			case	 Address.P2WPKH :// 0x02;      // pay to witness public key hash
				get_transation_len_for_p2wpkh();
				break;
			case	 Address.P2SH_P2WPKH :// 0x04;       // pay to witness public key  hash nest in Common.P2SH
				get_transation_len_for_p2sh_p2wphk();
			    break;
			    
			default:
				Error.wrong6A99_NOT_KNOWND_ADDRE_TYPE();
				break;
		}

		Util.setShort(apdu_buf,_0,short_buf_in_RAM[ REMAIN_DATA_LEN ]);
		apdu.setOutgoingAndSend((short)0, (short)2);
		ui_mgr.show_transation_approved();
	}
	
	void verify_PIN(APDU apdu)
	{
		byte[] buf = apdu.getBuffer();
     
		if(buf[0] != 0x00)
		{
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		}
		apdu.setIncomingAndReceive();
		
		byte mode = (buf[ISO7816.OFFSET_P1]);
		if(CoinManager.PIN_MODE_9GRIDS != mode && CoinManager.PIN_MODE_INPUT != mode && mode != PIN_MODE_CLARE_9GRIDS){
			Error.wrong6A86_incorrect_P1P2();
		}
		short lc = (short)(buf[ISO7816.OFFSET_LC] & 0xff);
		short ret = CoinManager.verifyPIN(mode, buf, ISO7816.OFFSET_CDATA, lc) ;
		UI.showMainUI(true);
		if(_0 == ret){
			random_mark[0] = true;
			return;
		}
		
		random_mark[0] = false;
		if(_0  > ret)
		{
			ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
		}
		else{
			
			ISOException.throwIt((short)(0x63C0 + (byte)(ret & 0x0F)));	//还可再试X次
			
		}
		
	}
	
	public void deselect(){
		
		recycle_storage();
	}
    void process_FCI(APDU apdu){
    	
			byte[] buf = apdu.getBuffer();
			short offs = _0;
			buf[offs++] = Common.FCI_TAG_6F;
			buf[offs++] = 0;
			buf[offs++] = Common.FCI_VERSION_TAG_84;
			buf[offs++] = (byte)( version.length );
			Util.arrayCopyNonAtomic(version, _0, buf, offs, (short) version.length);
			offs += version.length;
			buf[offs++] = Common.FCI_TIMER_OUT_TAG_85;
			buf[offs++] = 0X02;
			Util.setShort(buf,offs,max_timer_count);
			offs += 2;
			buf[offs++] = Common.FCI_BTC_UNIT_TAG_86;
			buf[offs++] = 0X01;
			buf[offs++] = ui_mgr.coin_unit;
			
			buf[offs++] = Common.FCI_BITCOIN_TAG_87;
			buf[offs++] = 0X01;
			buf[offs++] = base_addres.get_coind_type();
			
			
			buf[1] = (byte)(offs - 2);
			
			apdu.setOutgoingAndSend((short)0, offs);
    }
    void process_select(APDU apdu)
    {
    	
		byte[] buf = apdu.getBuffer();
		
		if(buf[ISO7816.OFFSET_CLA] != 0x00)
		{
			Error.wrong6E00_CLA_not_supported();
		}
		if((buf[ISO7816.OFFSET_P1] != 0x04) || (buf[ISO7816.OFFSET_P2] != 0x00))
		{
			Error.wrong6A86_incorrect_P1P2();	
		}
		short lc = (short)(buf[ISO7816.OFFSET_LC] & 0xff);
		if((lc > 16) || (lc < 5) ||  _AID.length != lc)
		{
			Error.wrong6700_P3Error();
		}
		
		if(Util.arrayCompare(buf, ISO7816.OFFSET_CDATA, _AID, (short)0, lc) != 0)
		{
			Error.wrong6A82_file_not_found();
		}
		
	    ui_mgr.showMainUI(true); 
		boolean _r = CoinManager.isCoinInfoExist(UI.btcNick, _0, (short)UI.btcNick.length);
		if(false == _r)
		{
			short len = get_public_addr_help(Address.P2PKH, buf,change_addr_path,_0,(short)(change_addr_path.length));
           
            base_addres.add_coin_info(g_larger_buf_in_RAM,len);
		}
		_r = CoinManager.isCoinInfoExist(UI.bchNick, _0, (short)UI.bchNick.length);
		if(false == _r)
		{
			base_addres.set_BCH();
			short len = get_public_addr_help(Address.P2PKH, buf,change_addr_path,_0,(short)(change_addr_path.length));
			base_addres.add_coin_info(g_larger_buf_in_RAM,len);
			base_addres.set_BTC();
		}
		process_FCI(apdu);
		
    }
	public void process(APDU apdu)
	{

		byte[] buf = apdu.getBuffer();
		byte ins = buf[ISO7816.OFFSET_INS];

		switch (ins)
		{
			
		case Common.SELECT____________A4:
			process_select(apdu);
			break;	
				
		case Common.GET_VERSION_______E2:
			get_version( apdu );
			break;
					
		case Common.SIGN______________2A:
		{
			
			short p1 =  (byte)(buf[ISO7816.OFFSET_P1] & 0xff);
			if(p1 != 0){
				Error.wrong6A86_incorrect_P1P2();
			}
			if(random_mark[0] == true)
			{
				random_mark[0] = false;
				try
				{
					ecc_sign(apdu);
				}catch(ISOException e)
				{
					if(e.getReason() != Error.TIMER_OUT_ERROR_CODE){
						  ui_mgr.show_transation_rejected(); 
					}
					 
					  throw e;
				}
			}
				
			else{
				Error.wrong6985_conditions_not_satisfied();
			}	
			

		}
		break ;
		case Common.EXPORT_X_PUB______E6: {
			get_public_node(apdu);
		}
		break;
		case Common.EXPORT_X_PUB_ADDR___F6:{
			short p1 =  (byte)(buf[ISO7816.OFFSET_P1] & 0xff);
			if(p1 == 2)
			{
				if(random_mark[0] == true){
					random_mark[0] = false;
					get_public_addr(apdu);
				}
				else{
					Error.wrong6985_conditions_not_satisfied();
				}
			}
			else{
				get_public_addr(apdu);
			}
		}

		break ;
		
		case Common.INS_RECEIVE_BCASH_TRASATION_DATA_F5:
		{
		   base_addres.set_coin_info(buf);
		  	
		}
		break ;
		
	    case  Common.INS_RECEIVE_BTC_TRASATION_DATA_F8:
	    {
	    	  try {
	    		  processTransationBlock(apdu);
			} catch (CardRuntimeException e1) {
                 reset_transation_state();
				 ISOException.throwIt(e1.getReason());
				return;
			}
	    }    	
	    break;
	    
	    case Common.INS_READ_TRASATION_DATA_F9:
	    	read_signed_transation_data(apdu);
	    	break ;
	    	
	    case Common.INS_SET_BTC_UNIT__FA:
	    	ui_mgr.set_coin_unit(buf[ISO7816.OFFSET_P1]);
	    	break;
	    case Common.INS_SET_TIMER_OUT_NUM__FB:
	    	{
		    	short num = Util.getShort(buf,ISO7816.OFFSET_P1);
		    	if(num < 0){
			    	Error.wrong6A86_incorrect_P1P2();
		    	}
		    	max_timer_count = num;
	    	}
	    	break ;
	    case Common.VERIFY_PIN________20:
	    	verify_PIN(apdu);
	    	break;
	    	
	    case Common.SHOW_NineGrids_______29:
	    	{
	    		short p1 = (short)(buf[ISO7816.OFFSET_P1] & 0xff);
	    		if(p1 == 0x00){
					CoinManager.showNineGrids();
					UI.showMainUI(false);
	    		}
				else if(p1 == 0x80){
					verify_PIN(apdu);
				}
				else{
					Error.wrong6A86_incorrect_P1P2();
				}
	    	}
;
	    	break;
	    	
		default:
			// good practice: If you don't know the INStruction, say so:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
		
	
	}
	
	
  void get_version( APDU apdu )
  {
		byte buf[] = apdu.getBuffer();
		apdu.setIncomingAndReceive();
		short lc = (short)(buf[ISO7816.OFFSET_LC] & 0xff);
		
		if( (short)( lc + 5 ) == get_version.length
				&&
			0x00 == Util.arrayCompare( buf, (short)0, get_version, (short)0, (short)( lc + 5 ) )
			)
		{
			// Get version number
			IO.apdusend( apdu, internal_version, (short)0, (short)internal_version.length );
			return ;
		}

	}
	     


	
  boolean is_withness(){
		
		switch(byte_buf_in_RAM[TRANSATION_WIGNET_TYPE_OFFSET]){
		case 	 Address.P2PKH :// 0x00;       // pay to public key  hash
		case	 Address.P2SH :// 0x01;        // pay  to script hash
			return false;
		case	 Address.P2WPKH :// 0x02;      // pay to witness public key hash
		case	 Address.P2WSH :// 0x03;       //  pay to witness script hash
		case	 Address.P2SH_P2WPKH :// 0x04;       // pay to witness public key  hash nest in Common.P2SH
		case	 Address.P2SH_P2WSH :// (byte)0x05;  // pay to witness script hash nesst in Common.P2SH
			return true;
		default:
			Error.wrong6AA2_TRANSATION_TYPE();
			break;
		}
		return true;
		
	}

  public   void transation_arrayCopy(short start_offs,
		    short cur_offs,byte[] src, short srcOff,short length)

    {

	  if(cur_offs < start_offs){
		   if(cur_offs + length > start_offs){
			   short remain = (short)(start_offs - cur_offs);
			   cur_offs += remain;
			   srcOff += remain;
			   length -= remain;
		   }
		   else{
			   return ;
		   }
	   }
	   short wLen = short_buf_in_RAM[WLEN_IN_READ_SIGNED_DATA_OFFS];
	   if(cur_offs > (start_offs +wLen) ){
		   // unexpected
		   Error.wrong6A96_API_USAGE_WRONG();
	   }
	   
	   
	   short destOff = (short)(cur_offs - start_offs);
	   if(destOff >= wLen){
		   // unexpected
		   Error.wrong6A96_API_USAGE_WRONG();
	   }
	   
	   if(destOff + length > wLen){
		   length = (short)(wLen - destOff);  
	   }
	   APDU apdu = APDU.getCurrentAPDU();
	   byte[] apdu_buf = apdu.getBuffer();
	   apdu.sendBytesLong(src, srcOff, length);
	  	
	
	   if(destOff + length == wLen){
		   Error.stepOut6A95();
	   }    
	}
	short get_input_count(){
		return (short)(byte_buf_in_RAM[ TRANSATION_INPUT_COUNTS_OFFSET ]&0xff);
	}

	
	void read_signed_transation_data(APDU apdu)//0xB0
	{
		byte buf[] = apdu.getBuffer();
		short offset = Util.makeShort(buf[2], buf[3]);
		short wLen = Util.makeShort((byte) 0, buf[4]);
		short total_count = short_buf_in_RAM[ REMAIN_DATA_LEN ];
	
		if( offset >= total_count )
		{
			Error.wrongOffset_6B00();
		}		
		if( 0 == wLen )
		{
			if(total_count >= 256)
			{
				wLen = (short)256;
				
			}
			else
			{
				wLen = (short)((total_count-offset) & 0xFF);
				
			}
		}
	
		
		if( offset + wLen > total_count )
		{
			short len = (short)(total_count - offset);
			Error.wrongLength_6C00((byte)( len & 0xff));
		}
		short_buf_in_RAM[WLEN_IN_READ_SIGNED_DATA_OFFS] = wLen;
		apdu.setOutgoing();
		apdu.setOutgoingLength(wLen);
		
		try {
			
			switch(byte_buf_in_RAM[TRANSATION_WIGNET_TYPE_OFFSET]){
			case 	 Address.P2PKH :// 0x00;       // pay to public key  hash
				generate_transation_for_p2pkh(buf);
				break;
			case	 Address.P2WPKH :// 0x02;      // pay to witness public key hash
				generate_segwit_transation_for_p2wkph(buf);
				break;
			case	 Address.P2SH_P2WPKH :// 0x04;       // pay to witness public key  hash nest in Common.P2SH
				generate_transation_for_p2sh_p2wphk(buf);
			    break;			    
			default:
				Error.wrong6A99_NOT_KNOWND_ADDRE_TYPE();
				break;
			}
			
		} catch (ISOException e) {
			// TODO: handle exception
			if(e.getReason() == Error._6A95_STEPOUT){
				
				return;
			}
			throw e;
		}

	}

	void get_transation_len_for_p2sh_p2wphk(){    	
			// [nVersion] [marker] [flag] [nInputCount] [txins] [nOutputCount] [txouts] [witness] [nLockTime] 
		    // scriptPubKey：    OP_HASH160    [Hash160(redeemScript)]    OP_EQUAL
            // scriptSig：        0x16 + reedemScript
            // reedemScript: 0x00  +  0x14  +  [Hash160(pubkey)]  
            // Witness： [signature][pubkey]

		    //Input格式：[preTXID 32] [preOutputIndex 4] [scriptSig 0x6c ] [0xffffffff  4]
		    // scriptSig： 0x16 + reedemScript
	        // Output：[amount] [scriptPubKey]
		  
			short nInputCount  = get_input_count();
		    short total_count = short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ];
		    short offs = 7 ;
			  
		    short scriptSigBuf_offs = get_script_sig_buf_offs();
		    short apdubuf_offs = 7;//[nVersion] [marker] [flag] [nInputCount]
		    short total_offs =_0;
		  
		    for(short i = 0 ; i < nInputCount ; ++i)
		    {
			    
			     offs += 37;
			      
			     apdubuf_offs += 36;
			     apdubuf_offs++;// scriptSigLen
		         // scriptSig：        0x16 + reedemScript
		         // reedemScript: 0x00  +  0x14  +  [Hash160(pubkey)]  	    	   		      
				 apdubuf_offs++;// = 0x16;
				 apdubuf_offs++; //= 0x00;
				 apdubuf_offs++;// = 0x14;
				
				 apdubuf_offs  += 20;
	
		      
			     offs += _4;// nSequence
			     apdubuf_offs += _4;
			     
			     total_offs += apdubuf_offs;
			     apdubuf_offs = _0;	      
		    }
		      
		    offs = (short)(7 + 0x29*nInputCount);
		    short remainDataLen = (short)(total_count - offs - 5);
		    total_offs += remainDataLen;
		     
		    apdubuf_offs = _0;
	
			//  {{<signature>}{...}...}
		
		    for(short i = 0 ; i < nInputCount ; ++i){
			     
			    
		    	  apdubuf_offs++;// 0x02;
			   
			       
			      apdubuf_offs++;//(byte)(signatureLen + 1);//signatureLen  ; 1byte是SighashType_ALL
			    
				
			      apdubuf_offs +=  Common.get_ECC_R_S_der_encode_len(g_larger_buf_in_RAM,++scriptSigBuf_offs);
			      
			      apdubuf_offs++; //SighashType_ALL;//
			      
			  
			      scriptSigBuf_offs += SIG_LEN;
		
			      apdubuf_offs++;//pubKeyLen
						
				  apdubuf_offs   += 0x21;
			           
			    
		
			      total_offs += apdubuf_offs;
			      
			      apdubuf_offs = _0;
			      
		   }
		    
		   // [nLockTime]
		 
		   total_offs += _4;
		   
		   short_buf_in_RAM[ REMAIN_DATA_LEN ] = total_offs;
		   
		
			
	}
	static byte[]  p2sh_p2wph_scriptSig_pre = {0x16,0x00,0x14};
	
	short send_withness(short total_offs,byte[] buf,short start_offs){
		
		short nInputCount  = get_input_count();
		
	    short scriptSigBuf_offs = get_script_sig_buf_offs();
	 
	    byte[] scriptPubKeyBuf = get_amount_pubkey_path_buf();
		//  {{<signature>}{...}...}
	    short pubKeyBuf_offs = get_amount_pubkey_path_offs();
        pubKeyBuf_offs += (short)(nInputCount*AMOUNT_V_LEN);// 
        short buf_off;
        byte hash_type = base_addres.get_hash_type();
	    for(short i = 0 ; i < nInputCount ; ++i){
	    	
	    	 byte signatureLen = Common.get_ECC_R_S_der_encode_len(g_larger_buf_in_RAM,++scriptSigBuf_offs);
		        // 1 + 1 + signatureLen + 1 + 1 + 33
	    	  total_offs += (37 + signatureLen);
		      if(total_offs < start_offs){
			     
			      scriptSigBuf_offs += SIG_LEN;
			      pubKeyBuf_offs += 0x21;
			      continue;
		      }
		      buf_off = _0;
		      total_offs -= (37 + signatureLen);	  
		      			      	      
	    	  buf[buf_off++] = 0x02;
	    	
	    	  buf[buf_off++] = (byte)(signatureLen + 1);//signatureLen  ; 1byte是SighashType_ALL
		       
			 
			  Common.asn1c_der_encode(g_larger_buf_in_RAM, scriptSigBuf_offs, g_larger_buf_in_RAM, (short)(scriptSigBuf_offs + 32), _32, buf, buf_off);
								  
			  buf_off+=signatureLen;// signature
			  buf[buf_off++] = hash_type;//
			  
		      scriptSigBuf_offs += SIG_LEN;
				
	    	
		      	    	  

		      buf[buf_off++] = 0x21;//pubKeyLen
	    	 
					    
		      Util.arrayCopyNonAtomic(scriptPubKeyBuf, pubKeyBuf_offs, buf, buf_off, (short)0x21);
		      
		      buf_off   += 0x21;
		      
		      transation_arrayCopy(start_offs,total_offs, buf, _0, buf_off);
		      pubKeyBuf_offs += 0x21;
		      
		      total_offs += buf_off;
		               
	   }
		
	    return total_offs;
	}
	

	
	void generate_transation_for_p2sh_p2wphk(byte[] buf){
			// [nVersion] [marker] [flag] [nInputCount] [txins] [nOutputCount] [txouts] [witness] [nLockTime] 
		    // scriptPubKey：    OP_HASH160    [Hash160(redeemScript)]    OP_EQUAL
            // scriptSig：        0x16 + reedemScript
            // reedemScript: 0x00  +  0x14  +  [Hash160(pubkey)]  
            // Witness： [signature][pubkey]

		    //Input格式：[preTXID 32] [preOutputIndex 4] [scriptSig 0x6c ] [0xffffffff  4]
		    // scriptSig： 0x16 + reedemScript
	        // Output格式：[amount] [scriptPubKey]
		    byte[] transation_buf = get_transation_data_buf();
			short nInputCount  = get_input_count();
		  
		    short offs = 7 ;
		    byte[] scriptPubKeyBuf = get_amount_pubkey_path_buf();
		  
		    // nVersion  +  nInputCount
		    short total_offs =_0;
		    short start_offs  = Util.makeShort(buf[2], buf[3]);
	        transation_arrayCopy(start_offs,total_offs,transation_buf,_0,offs);
	        total_offs += offs;
	        	        
	        short pubKeyBuf_offs = get_amount_pubkey_path_offs();
	        pubKeyBuf_offs += (short)(nInputCount*AMOUNT_V_LEN);// 
	        
	 		short buf_offs;      
		    for(short i = 0 ; i < nInputCount ; ++i){
		    	  
		    	  total_offs += 64;
			      if(total_offs < start_offs){
			    	  offs += 41;// 跳过 占位scriptsig位置的0x00 和 nSequence
			    	  pubKeyBuf_offs += 0x21;
				      continue;
			      }
			      total_offs -= 64;	  
			      buf_offs = _0;		
				  Util.arrayCopyNonAtomic(transation_buf, offs, buf, buf_offs, (short)36); // 36 
				  buf_offs+=36;
				  offs += 37;// 跳过 占位scriptsig位置的0x00
				  buf[buf_offs++] = 0x17;
		          // scriptSig：        0x16 + reedemScript
		          // reedemScript: 0x00  +  0x14  +  [Hash160(pubkey)]  
				  Util.arrayCopyNonAtomic(p2sh_p2wph_scriptSig_pre,_0, buf,buf_offs,(short)3);// 0x16 0x00 0x14
				  buf_offs += 3;
			
				  
				  sha256Assist.reset();
				  sha256Assist.doFinal(scriptPubKeyBuf, pubKeyBuf_offs,(short) 0x21, buf, buf_offs);// 33
				  pubKeyBuf_offs += 0x21;	
				  
				  _RIPEMD160.doFinal(buf, buf_offs, _32, buf, buf_offs);
				  buf_offs += 0x14;
				  
			      Util.arrayCopyNonAtomic(transation_buf,offs, buf,buf_offs,_4);// nSequence
			      offs += _4;
			      buf_offs+=_4;
			      
			      transation_arrayCopy(start_offs,total_offs,buf,_0,buf_offs);
				  total_offs  += buf_offs;	
		    }
		      
		    offs = (short)(7 + 0x29*nInputCount);
		    short total_count = short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ];
		    short remainDataLen = (short)(total_count - offs - 5);//未签名的签名数据中withness用1byte00来占位
		    transation_arrayCopy(start_offs,total_offs,transation_buf, offs,remainDataLen);
		    total_offs += remainDataLen;
		    
		    
		    total_offs = send_withness(total_offs,buf,start_offs);
		    
		   // [nLockTime]
		    transation_arrayCopy(start_offs,total_offs, transation_buf,(short)(total_count -4),  _4);
	
	}
	void get_transation_len_for_p2pkh()
	{   	
	    	// only for non-segwit
			// [nVersion] [nInputCount] [txInputs] [nOutputCount] [txOutputs] [nLockTime]
			short nInputCount  = get_input_count();
		    short total_count = short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ];
		 

		    short scriptSigBuf_offs = get_script_sig_buf_offs();
		    short apdubuf_offs = 5;// nVersion  +  nInputCount
		    short total_offs =_0;
			
		   //Input格式：[preTXID 32] [preOutputIndex 4] [scriptSig 0x6c ] [0xffffffff  4]
		   // scriptSig：<sig> <pubkey> 
	       // Output格式：[amount] [scriptPubKey]
		    for(short i = 0 ; i < nInputCount ; ++i){
			      	      
			    apdubuf_offs += 36;
			    			 		     
			     apdubuf_offs++;// SighashType_ALL(1) + scriptSigLen(1) + pubKeyLen(1)  +  pub key(0x21) = 0x23
			      
			     apdubuf_offs++ ;//signatureLen
			    
			      
			     apdubuf_offs += Common.get_ECC_R_S_der_encode_len(g_larger_buf_in_RAM,++scriptSigBuf_offs);
			      
			     apdubuf_offs++;// SighashType_ALL;
			      
			      // scriptSigBuf 是LVLV...的结构
			     scriptSigBuf_offs += SIG_LEN;
			     		    	   		      
				 apdubuf_offs++ ;//pubKeyLen
				  								 
				 apdubuf_offs   += 0x21;
			      			     
			     apdubuf_offs += _4;
			      
			     total_offs += apdubuf_offs;
			     apdubuf_offs = _0;	      
		    }
		      
		    short remainDataLen = (short)(total_count - 5 - 0x29*nInputCount);	   
		    total_offs += remainDataLen;
		    short_buf_in_RAM[ REMAIN_DATA_LEN ] = total_offs;
		    		
		
						
	}	  
    void generate_transation_for_p2pkh(  byte[] buf ){	
    	// only for non-segwit
		// [nVersion] [nInputCount] [txInputs] [nOutputCount] [txOutputs] [nLockTime]
		short nInputCount  = get_input_count();
	    short total_count = short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ];
	    short offs ;
	    byte[] pathBuf = get_amount_pubkey_path_buf();
	    byte[] scriptPubKeyBuf = pathBuf;
	  
	    short scriptSigBuf_offs = get_script_sig_buf_offs();
	    byte[] transation_buf = get_transation_data_buf();
	   
	   //Format of input ：[preTXID 32] [preOutputIndex 4] [scriptSig 0x6c ] [0xffffffff  4]
	   // scriptSig：<sig> <pubkey> 
       // Format of output：[amount] [scriptPubKey]
        offs = 0x05;// nVersion  +  nInputCount
        short total_offs =5;// nVersion  +  nInputCount
        short start_offs  = Util.makeShort(buf[2], buf[3]);
        transation_arrayCopy(start_offs,_0,transation_buf,_0, offs);

        short pubKeyBuf_offs = get_amount_pubkey_path_offs();
        pubKeyBuf_offs += (short)(nInputCount*AMOUNT_V_LEN);// 
      
        short apdu_offs;
        byte hash_type = base_addres.get_hash_type();
	    for(short i = 0 ; i < nInputCount ; ++i){
	    	 
	    	 byte signatureLen = Common.get_ECC_R_S_der_encode_len(g_larger_buf_in_RAM,++scriptSigBuf_offs);
		     // 36 + 1 + 1 + signatureLen + 1 + 1 + 33 + 4 
	    	 total_offs += (77 + signatureLen);
		     if(total_offs < start_offs){
			      scriptSigBuf_offs += SIG_LEN;
			      pubKeyBuf_offs += 0x21;
			      offs += 41;
			    
			      continue;
		     }
		     
		     total_offs -= (77 + signatureLen);	
		     apdu_offs =_0;
		     
		     Util.arrayCopyNonAtomic(transation_buf,offs,buf, apdu_offs, (short)36);
		     offs += 37;
		     apdu_offs+= 36;	       
		     buf[apdu_offs++] = (byte)(signatureLen   + 0x24);// SighashType_ALL(1) + scriptSigLen(1) + pubKeyLen(1)  +  pub key(0x21) = 0x23
		      
		     buf[apdu_offs++] = (byte)(signatureLen + 1);//signatureLen
		          
			 Common.asn1c_der_encode(g_larger_buf_in_RAM, scriptSigBuf_offs, g_larger_buf_in_RAM, (short)(scriptSigBuf_offs + 32), _32, buf, apdu_offs);
				
			 apdu_offs += signatureLen;
			 
		     scriptSigBuf_offs += SIG_LEN;
			 buf[apdu_offs++] = hash_type; 
	      
	     		    	   		      
			 buf[apdu_offs++]  = 0x21;//pubKeyLen		
			 Util.arrayCopyNonAtomic(scriptPubKeyBuf,pubKeyBuf_offs, buf, apdu_offs, (short)0x21);// pubKey
			 apdu_offs+= 0x21;
				  
		     pubKeyBuf_offs += 0x21;
		     
		     Util.arrayCopyNonAtomic(transation_buf,offs, buf, apdu_offs, _4);// pubKey
		     offs += _4;
		     apdu_offs+= _4;
		     
		     transation_arrayCopy(start_offs,total_offs,buf,_0, apdu_offs);// nSequence
		     
		     total_offs     += apdu_offs;     
	    }
	      
	    offs = (short)(5 + 0x29*nInputCount);
	    short remainDataLen = (short)(total_count - offs);
	    transation_arrayCopy(start_offs,total_offs, transation_buf,offs, remainDataLen);

		
    }
	

  void get_transation_len_for_p2wpkh( )
	{	

		// only for segwit
		// signed transaction: 
		// [nVersion][marker][flag][txins][txouts][witness][nLockTime]
		//                        [txins]: [nInputCount]{[txin1]}{...}...
		//                                                [txin]: [preTXID][preOutputIndex][0x00][nSequence]
		//                               [txouts]: [nOutputCount]{[txout1]}{...}...
		//                                                        [txout]: [amount][InputIndex][OP_EQUALVERIFY][OP_CHECKSIG]
		//                                       witness:      <signature> <pubkey>
		// marker value is 0x00，flag value is 0x01
		short nInputCount  =  get_input_count();
	    short total_count = short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ];
	  
		// Input:[preTXID] [preOutputIndex] [0x00] [0xffffffff]
		// Witness: [signature][pubkey]
		
	 
	    
	
	    short apdubuf_offs ;
	   
	    short remainDataLen = (short)(total_count - 5); // [witness][nLockTime]
     
	    short  total_offs =remainDataLen;
	   
	   //Input格式：[preTXID 32] [preOutputIndex 4] [scriptSig 0x6c ] [0xffffffff  4]
	   // scriptSig：<sig> <pubkey> 
       // Output格式：[amount] [scriptPubKey]
      
        short scriptSigBuf_offs = get_script_sig_buf_offs();
  
        apdubuf_offs = _0;
	
		//  {{<signature>}{...}...}
		
	    for(short i = 0 ; i < nInputCount ; ++i){
		     		    
	    	  apdubuf_offs++;// 0x02;
		    		       
		      apdubuf_offs++;//signatureLen  
		    		
		      apdubuf_offs +=  Common.get_ECC_R_S_der_encode_len(g_larger_buf_in_RAM,scriptSigBuf_offs);
		      
		      apdubuf_offs++; // SighashType_ALL;//
		      
		      // scriptSigBuf 是LVLV...的结构
		      scriptSigBuf_offs += SIG_LEN;
	
		      apdubuf_offs++;//pubKeyLen
			 
			  apdubuf_offs   += 0x21;
		  
		            
		      total_offs += apdubuf_offs;
		      
		      apdubuf_offs = _0;
		      
	   }
	    
	   // [nLockTime]	  
	   total_offs += _4;
	   
	   short_buf_in_RAM[ REMAIN_DATA_LEN ] = total_offs;

    }
  
  void generate_segwit_transation_for_p2wkph( byte[] buf )
  {	
		//only for segwit
		//signed transaction: 
		//[nVersion][marker][flag][txins][txouts][witness][nLockTime]
		//                    [txins]: [nInputCount]{[txin1]}{...}...
		//                                            [txin]: [preTXID][preOutputIndex][0x00][nSequence]
		//                           [txouts]: [nOutputCount]{[txout1]}{...}...
		//                                                    [txout]: [amount][InputIndex][OP_EQUALVERIFY][OP_CHECKSIG]
		//                                   witness:      <signature> <pubkey>
		// 其中marker固定为0x00，flag为0x01
		
	    short total_count = short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ];
	    byte[] transation_buf = get_transation_data_buf();
	 
		// Input:[preTXID] [preOutputIndex] [0x00] [0xffffffff]
		// Witness: [signature][pubkey]
	    short total_offs = (short)(total_count - 5); // [witness][nLockTime]
	    //Input格式：[preTXID 32] [preOutputIndex 4] [scriptSig 0x6c ] [0xffffffff  4]
	    // scriptSig：<sig> <pubkey> 
	    // Output格式：[amount] [scriptPubKey]
	  
	    // [nVersion][marker][flag][txins][txouts]
	    short start_offs  = Util.makeShort(buf[2], buf[3]);
	    transation_arrayCopy(start_offs,_0,transation_buf,_0,  total_offs);
			
	    total_offs = send_withness(total_offs, buf,start_offs);
	    
	    // [nLockTime]
	    transation_arrayCopy(start_offs,total_offs, transation_buf,(short)(total_count -4),_4);
   }	

    short generate_NoSegwit_premage(byte[] apdu_buf,byte[] preimage_hash_buf,short preimage_hash_offs)
    {
    	// only for non-segwit
		// preimage: [nVersion][nInputCount][txInputs][nOutputCount][txOutputs][nLockTime][nHashType]
		// [preTXID] [preOutputIndex] [preOutput_ScriptPubKey] [0xffffffff] 签名原文]
    	//     32    +     4            +            1        +       4   = 41 (0x29)
		short nInputCount  =  get_input_count();
	    short total_count = short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ];
	    short tempLen ;
	    byte[] transation_buf = get_transation_data_buf();
	    byte[] _amount_pubkey_path_buf = get_amount_pubkey_path_buf();    
	    short  pubkey_offset = get_amount_pubkey_path_offs()  ;
	    pubkey_offset += (short)(AMOUNT_V_LEN*nInputCount);
	    pubkey_offset -= 33;
	    
	    apdu_buf[0] = 0x19;// 长度
	    apdu_buf[1] = Address.OP_DUP;
	    apdu_buf[2] = Address.OP_HASH160;
	    apdu_buf[3] = 20;// hashkey的长度: 做过hash160
	    
	    apdu_buf[24] = Address.OP_EQUALVERIFY;
	    apdu_buf[25] = Address.OP_CHECKSIG;
	    
		base_addres.get_HASH_TYPE_buf(store_TL_buffer);
	    for(short i = 0 ; i < nInputCount ; ++i)
	    {
		    sha256.reset();
		    tempLen = (short)(5 + 41*i + 36);

		    sha256.update(transation_buf,_0,tempLen);
		    

            // script_code : OP_DUP OP_HASH160 <PubKeyHash> OP_EQUALVERIFY OP_CHECKSIG
			pubkey_offset += 33;
			
			sha256Assist.reset();
			sha256Assist.doFinal(_amount_pubkey_path_buf,pubkey_offset,(short)33,apdu_buf,(short)28);
			  
			_RIPEMD160.reset();
            _RIPEMD160.doFinal(apdu_buf,(short)28,(short)32,apdu_buf,(short)4);

		    sha256.update(apdu_buf,_0,(short)26);
		    
		    
		    tempLen +=1;// 跳过0x00这个byte
	
		    sha256.update(transation_buf,tempLen,(short)(total_count - tempLen));
	
		    g_larger_buf_in_RAM[preimage_hash_offs++]= _32;
		    sha256.doFinal(store_TL_buffer, _0, _4,preimage_hash_buf,preimage_hash_offs);
			
			 
			sha256.doFinal(preimage_hash_buf, preimage_hash_offs, _32,preimage_hash_buf,preimage_hash_offs);
				
		    preimage_hash_offs += _32;	    

	    }
	    return (short)(nInputCount*33);
    }
   
   
    short generate_segwit_premage(byte[] apdu_buf,byte[] preimage_hash_buf,short preimage_hash_offs){
    	
	    short nInputCount  =  get_input_count();
	    short total_count = short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ];
	 
		byte[] transation_buf = get_transation_data_buf();
	
		byte[] _amount_pubkey_path_buf = get_amount_pubkey_path_buf();
	    short  _amount_pubkey_path_buf_offs = get_amount_pubkey_path_offs() ;
	   
	  
        short offs = _0;
   
		apdu_buf[40] = 0x19;// length
		apdu_buf[41] = Address.OP_DUP;
		apdu_buf[42] = Address.OP_HASH160;
		apdu_buf[43] = 20;// length of hashkey
	    
		apdu_buf[64] = Address.OP_EQUALVERIFY;
		apdu_buf[65] = Address.OP_CHECKSIG;
  
        base_addres.get_HASH_TYPE_buf(store_TL_buffer);
	  //segwit preimage: [nVersion][hashPrevouts][hashSequence][outpoint][scriptCode][amount][nSequence][hashOutputs][nLocktime][nHashType]
		short pubkey_offset = (short)(_amount_pubkey_path_buf_offs + AMOUNT_V_LEN*nInputCount );
		short amount_offset = (short)(_amount_pubkey_path_buf_offs );
	    for(short for_sign_input_index = 0 ; for_sign_input_index < nInputCount ; ++for_sign_input_index)
	    {
			//[nVersion] [marker] [flag] [nInputCount] [txins] [nOutputCount] [txouts] [witness] [nLockTime]
			// only for segwit
			// [BIP0143]:
			//  1. nVersion of the transaction(4 - byte little endian)
			//  2. hashPrevouts(32 - byte hash)
			//  3. hashSequence(32 - byte hash)
			//  4. outpoint(32 - byte hash + 4 - byte little endian)
			//  5. scriptCode of the input(serialized as scripts inside CTxOuts)
			//  6. value of the output spent by this input(8 - byte little endian)
			//  7. nSequence of the input(4 - byte little endian)
			//  8. hashOutputs(32 - byte hash)
			//  9. nLocktime of the transaction(4 - byte little endian)
			// 10. sighash type of the signature(4 - byte little endian)
			// preimage: [nVersion][hashPrevouts][hashSequence][outpoint][scriptCode][amount][nSequence][hashOutputs][nLockTime][nHashType]	
			

			// [preTXID] [preOutputIndex] [0x00][0xffffffff]
			// 0x20          4               1         4
			
			sha256.reset();
			
			//[nVersion]
			sha256.update(transation_buf,_0,_4);

			// [hashPrevouts]: dSHA256({[preTXID1][preOutputIndex1]}{}...)
			sha256Assist.reset();

	    	offs = (short)(7);
			for(short i = 0 ; i < nInputCount ; ++i){
			  		
				sha256Assist.update(transation_buf,offs,(short)0x24);   

		    	offs += 0x29;
			}  
			sha256Assist.doFinal(transation_buf,_0,_0,apdu_buf,_0);
			sha256Assist.doFinal(apdu_buf, _0, _32,apdu_buf, _0);
			
			
			sha256.update(apdu_buf,_0,_32);
				

			// [hashSequence]: dSHA256([nSequence1][nSequence2][[nSequenceX]...])
			sha256Assist.reset();
			
			offs = (short)(44);//7  + 0x25
			for(short i = 0 ; i < nInputCount ; ++i){
			  
				sha256Assist.update(transation_buf,offs,(short)0x4);   
	
		    	offs += 0x29;
			} 
			 
			sha256Assist.doFinal(transation_buf,_0,_0,apdu_buf,_0);
			sha256Assist.doFinal(apdu_buf, _0, _32,apdu_buf, _0);
			
			sha256.update(apdu_buf,_0,_32);
			
		
			// [outpoint] (32 - byte hash + 4 - byte little endian)
			offs = (short)(7 + 0x29*for_sign_input_index);
			sha256.update(transation_buf,offs,(short)0x24); 

			// [scriptCode]  preOutput_ScriptPubKey
			// script_code : OP_DUP OP_HASH160 <PubKeyHash> OP_EQUALVERIFY OP_CHECKSIG
			
			sha256Assist.reset();
			sha256Assist.doFinal(_amount_pubkey_path_buf,pubkey_offset,(short)33,apdu_buf,(short)68);
			
			pubkey_offset += 33;
			
			_RIPEMD160.reset();		
			_RIPEMD160.doFinal(apdu_buf,(short)68,(short)32,apdu_buf,(short)44);
			   
			sha256.update(apdu_buf,(short)40,(short)26);
			
	
			// [amount]  value of the output spent by this input(8 - byte little endian)
			sha256.update(_amount_pubkey_path_buf,amount_offset,(short)8);
			amount_offset += 	AMOUNT_V_LEN;
		
		    
			// [nSequence]
			offs = (short)(7 + 0x29*for_sign_input_index + 0x25);
			sha256.update(transation_buf,offs,(short)0x4); 
	
	
			
			// [hashOutputs]: dSHA256({[amount][InputIndex]}{...}...)
			
		    // [txins]: [nInputCount]{[txin1]}{...}...
		    //[nVersion (4 byte) ][marker (1 byte) ][flag (1 byte) ][txins][nOutputCount  (1 byte) ]
			offs = (short)(8 + 0x29*nInputCount) ;

			sha256Assist.reset();
			sha256Assist.doFinal(transation_buf, offs, (short)(total_count - offs - 5),apdu_buf,_0);// [witness] [nLockTime]    1 + 4 = 5
			sha256Assist.doFinal(apdu_buf, _0, _32,apdu_buf, _0);
			
			sha256.update(apdu_buf,_0,_32);
			
	
		    
			// [nLockTime]
			offs = (short)(total_count - 4);// [nLockTime] 4 bytes
			sha256.update(transation_buf, offs,_4);

		    preimage_hash_buf[preimage_hash_offs++]= _32;
			// [nHashType]
			sha256.doFinal(store_TL_buffer,_0,_4,preimage_hash_buf,preimage_hash_offs);
		
			sha256.doFinal(preimage_hash_buf, preimage_hash_offs, _32,preimage_hash_buf,preimage_hash_offs);
			

            preimage_hash_offs += _32;
 
	    }
        return (short)(nInputCount*33);
    }

   short generate_BCH_premage(byte[] apdu_buf,byte[] preimage_hash_buf,short preimage_hash_offs){
    	
	    short nInputCount  =  get_input_count();
	    short total_count = short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ];
	 
		byte[] transation_buf = get_transation_data_buf();
	
		byte[] _amount_pubkey_path_buf = get_amount_pubkey_path_buf();
	    short  _amount_pubkey_path_buf_offs = get_amount_pubkey_path_offs() ;
	   
	  
        short offs = _0;
   
		apdu_buf[40] = 0x19;// 长度
		apdu_buf[41] = Address.OP_DUP;
		apdu_buf[42] = Address.OP_HASH160;
		apdu_buf[43] = 20;// hashkey的长度: 做过hash160
	    
		apdu_buf[64] = Address.OP_EQUALVERIFY;
		apdu_buf[65] = Address.OP_CHECKSIG;
  
        base_addres.get_HASH_TYPE_buf(store_TL_buffer);
	  // preimage: [nVersion][hashPrevouts][hashSequence][outpoint][scriptCode][amount][nSequence][hashOutputs][nLocktime][nHashType]
		short pubkey_offset = (short)(_amount_pubkey_path_buf_offs + AMOUNT_V_LEN*nInputCount );
		short amount_offset = (short)(_amount_pubkey_path_buf_offs );
	    for(short for_sign_input_index = 0 ; for_sign_input_index < nInputCount ; ++for_sign_input_index)
	    {
			//[nVersion][nInputCount] [txins] [nOutputCount] [txouts]  [nLockTime]
			// 
			//  1. nVersion of the transaction(4 - byte little endian)
			//  2. hashPrevouts(32 - byte hash)
			//  3. hashSequence(32 - byte hash)
			//  4. outpoint(32 - byte hash + 4 - byte little endian)
			//  5. scriptCode of the input(serialized as scripts inside CTxOuts)
			//  6. value of the output spent by this input(8 - byte little endian)
			//  7. nSequence of the input(4 - byte little endian)
			//  8. hashOutputs(32 - byte hash)
			//  9. nLocktime of the transaction(4 - byte little endian)
			// 10. sighash type of the signature(4 - byte little endian)
			// preimage: [nVersion][hashPrevouts][hashSequence][outpoint][scriptCode][amount][nSequence][hashOutputs][nLockTime][nHashType]	
			

			// [preTXID] [preOutputIndex] [0x00][0xffffffff]
			// 0x20          4               1         4
			
			sha256.reset();
			
			//[nVersion]
			sha256.update(transation_buf,_0,_4);

			// [hashPrevouts]: dSHA256({[preTXID1][preOutputIndex1]}{}...)
			sha256Assist.reset();

	    	offs = (short)(5);
			for(short i = 0 ; i < nInputCount ; ++i){
			  		
				sha256Assist.update(transation_buf,offs,(short)0x24);   

		    	offs += 0x29;
			}  
			sha256Assist.doFinal(transation_buf,_0,_0,apdu_buf,_0);
			sha256Assist.doFinal(apdu_buf, _0, _32,apdu_buf, _0);
			
			
			sha256.update(apdu_buf,_0,_32);
				

			// [hashSequence]: dSHA256([nSequence1][nSequence2][[nSequenceX]...])
			sha256Assist.reset();
			
			offs = (short)(42);//5  + 0x25
			for(short i = 0 ; i < nInputCount ; ++i){
			  
				sha256Assist.update(transation_buf,offs,(short)0x4);   
	
		    	offs += 0x29;
			} 
			 
			sha256Assist.doFinal(transation_buf,_0,_0,apdu_buf,_0);
			sha256Assist.doFinal(apdu_buf, _0, _32,apdu_buf, _0);
			
			sha256.update(apdu_buf,_0,_32);
			
		
			// [outpoint] (32 - byte hash + 4 - byte little endian)
			offs = (short)(5 + 0x29*for_sign_input_index);
			sha256.update(transation_buf,offs,(short)0x24); 

			// [scriptCode]  preOutput_ScriptPubKey
			// script_code : OP_DUP OP_HASH160 <PubKeyHash> OP_EQUALVERIFY OP_CHECKSIG
			
			sha256Assist.reset();
			sha256Assist.doFinal(_amount_pubkey_path_buf,pubkey_offset,(short)33,apdu_buf,(short)68);
			
			pubkey_offset += 33;
			
			_RIPEMD160.reset();		
			_RIPEMD160.doFinal(apdu_buf,(short)68,(short)32,apdu_buf,(short)44);
			   
			sha256.update(apdu_buf,(short)40,(short)26);
			
	
			// [amount]  value of the output spent by this input(8 - byte little endian)
			sha256.update(_amount_pubkey_path_buf,amount_offset,(short)8);
			amount_offset += 	AMOUNT_V_LEN;
		
		    
			// [nSequence]
			offs = (short)(5 + 0x29*for_sign_input_index + 0x25);
			sha256.update(transation_buf,offs,(short)0x4); 
	
	
			
			// [hashOutputs]: dSHA256({[amount][InputIndex]}{...}...)
			
		    // [txins]: [nInputCount]{[txin1]}{...}...
		    //[nVersion (4 byte) ][txins][nOutputCount  (1 byte) ]
			offs = (short)(6 + 0x29*nInputCount) ;

			sha256Assist.reset();
			sha256Assist.doFinal(transation_buf, offs, (short)(total_count - offs - 4),apdu_buf,_0);// [nLockTime]    4 byte
			sha256Assist.doFinal(apdu_buf, _0, _32,apdu_buf, _0);
			
			sha256.update(apdu_buf,_0,_32);
			
	
		    
			// [nLockTime]
			offs = (short)(total_count - 4);// [nLockTime] 4 byte
			sha256.update(transation_buf, offs,_4);

		    preimage_hash_buf[preimage_hash_offs++]= _32;
			// [nHashType]
			sha256.doFinal(store_TL_buffer,_0,_4,preimage_hash_buf,preimage_hash_offs);
			
			sha256.doFinal(preimage_hash_buf, preimage_hash_offs, _32,preimage_hash_buf,preimage_hash_offs);
			

            preimage_hash_offs += _32;
 
	    }
        return (short)(nInputCount*33);
    }


    // 判断是否为找零地址
    boolean is_not_change_addr(short idx)
    {
    	short count = g_temp_buffer_in_RAM[0];
    	if(count == 0){
    		return false;
    	}
    	byte[] temp_buf = get_change_addr_buf();
    	count +=1;
    	for(short i = 1; i < count; ++i){
    		if(idx == temp_buf[(short)(i)]){
    			return true;
    		}
    	}
    	
    	return false;
    }
    
   
   
	short calculate_show_address(byte[]  transation_buf, short output_offs,
			byte[] apdu_buf,
			byte[] addr_buf, short addr_buf_offs){
		output_offs += 8;//amount
		byte addr_type =  Address.get_addr_type(transation_buf[output_offs], transation_buf[ (short)(output_offs  + 1 )]);
		short len = -1;
    	// Address.P2PKH 的 scriptPubKeyLen (值为0x19) =  OP_DUP(1byte)  OP_HASH160(1byte)  hashLen(值为0x14，1byte)  pubkeyHash(0x14byte)  OP_EQUALVERIFY(1byte)  OP_CHECKSIG(1byte)
    	
    	// Address.Common.P2WPKH 的 scriptPubKeyLen (值为0x16) = OP_0(1byte)  hashLen(值为0x14，1byte)  pubkeyHash(0x14byte)
    	
    	// Address.P2SH 的 scriptPubKeyLen    (值为0x17) =    OP_HASH160(1byte)  hashLen(值为0x14，1byte)  pubkeyHash(0x14byte) OP_EQUAL
 	
    	// Address.P2WSH 的 scriptPubKeyLen    (值为0x22) =  OP_0 (1byte)  hashLen(值为0x20，1byte)  pubkeyHash(0x20byte)
		switch (addr_type) {
		case Address.P2PKH:
		{
			output_offs +=4;		
		}	
		break;
		
		case Address.P2WPKH:
		{
			// 需要bench32的处理
			output_offs +=3;
		}
			break;
			
		case Address.P2SH:{
			output_offs +=3;
			
		}
			break;
			
		case Address.P2WSH:
		{
			output_offs += 3;
			
		}
			break;
			
		default:
			Error.wrong6A99_NOT_KNOWND_ADDRE_TYPE();
			break;
		}
		switch (addr_type) 
		{
			case Address.P2PKH:
			case Address.P2SH:
			{

				
				apdu_buf[0] = base_addres.get_pubkey_version(addr_type);
					
				Util.arrayCopyNonAtomic(transation_buf, output_offs, apdu_buf, (short)1, (short)20);
				
			
				if(!base_addres.is_BCH()){  // BTC,LTC,USDT
					/*
					[1 - byte address version]
					[20 - byte - public key hash]
					[4 - byte checksum]
					0x19 = 1 + 20 + 4;
					*/
					len = sha256.doFinal(apdu_buf,_0,(short)21,apdu_buf,(short)21);		
					len = sha256.doFinal(apdu_buf,(short)21,len,apdu_buf,(short)21);
					
					len =  Base58.encode(apdu_buf,_0, (short)25, apdu_buf,(short)25);	
						
					len += 1;
					apdu_buf[(short)(len+ 25)] = 0x00;
					
					Util.arrayCopyNonAtomic(apdu_buf, (short)25, addr_buf, addr_buf_offs, len);
					
			   }
			   else 
			   {
				   len = cash_addres.cash_addr_encode(     addr_buf,addr_buf_offs, 
													  cash_addres.hrp,_0, (short)cash_addres.hrp.length, 
													  apdu_buf, _0,(short)21 , apdu_buf, (short)60);
													  
					len -= (short)(cash_addres.hrp.length + 1); 								  
					Util.arrayCopyNonAtomic(addr_buf, (short)(addr_buf_offs + cash_addres.hrp.length + 1)
												, apdu_buf, (short)60, len);	
										  
					Util.arrayCopyNonAtomic( apdu_buf, (short)60, addr_buf, addr_buf_offs,len);			  
													  
			   }	
			}
				
			break;

			default:
				Error.wrong6A99_NOT_KNOWND_ADDRE_TYPE();
				break;
		}
		return len;
	}
  
   
  
    // Calculation amount
    byte get_change_addr_count(){
    	return g_temp_buffer_in_RAM[0];
    }

    short confirm_usdt_output( byte[] buf)
    {
		 short change_count = get_change_addr_count();
		 change_count = (short)(change_count/2);
		 //  output_count - change_count
		 short keyPress;
		 short pageIndex = 1;
		 boolean show_up = false,  show_down;
		 if(change_count > 2){
			 show_down = true;
		 }
		 else{
			 show_down = false;
		 }
		 
	
	    byte[] temp_addr_buf = get_change_addr_buf();
	    byte[] transation_buf = get_transation_data_buf();
	    
		short output_offs = get_output_offs_for_one_index(temp_addr_buf[(short)(pageIndex)]);
		
		short addr_buf_offs = get_script_sig_buf_offs();	
		short addr_buf_len =  calculate_show_address(transation_buf, output_offs, buf, g_larger_buf_in_RAM, addr_buf_offs);
		
		output_offs = get_output_offs_for_one_index(temp_addr_buf[(short)(pageIndex + change_count)]);
		
		/* 23 = 
		 8(BTC output amout) +
		 1(script len) +
		 1(OP_RETURN) +
		 1(usdt script len )
		 4(omni) +
		 2(交易版本) +
		 2(交易类型) +
		 4(代表USDT) 
		*/
		output_offs+=23;
		ui_mgr.show_page(transation_buf,output_offs,
				g_larger_buf_in_RAM,addr_buf_offs,addr_buf_len,
						false,show_down,buf);
		Buttons.startWaitKeysPress();				
		Timer.wait((short)50); 
		
		while(true){
			
			 // Wait for the button, if it is OK, enter the confirmation page and jump out of the current loop
			 
			 keyPress = Buttons.getKeysPressed();
			 
			 if((keyPress& Buttons.BUTTON_UP_PRESSED) == Buttons.BUTTON_UP_PRESSED){
				 
				 if(1 == pageIndex){
					 // Do nothing, current page
					 timer_wait();
					 continue;
				 }
				 else {
					 pageIndex -= 1;
				 }
			 }
			 else if((keyPress& Buttons.BUTTON_DOWN_PRESSED) == Buttons.BUTTON_DOWN_PRESSED){
				 if(change_count == pageIndex){
					// Do nothing, current page
					timer_wait();
					 continue;
				 }
				 else{
					 pageIndex += 1;
				 }
			 }
			 else if((keyPress& Buttons.BUTTON_OK_PRESSED) == Buttons.BUTTON_OK_PRESSED){
				 
				 break;//jump out of the loop
			 }
			 else if((keyPress& Buttons.BUTTON_CANCEL_PRESSED) == Buttons.BUTTON_CANCEL_PRESSED){
			 
				 break;//jump out of the loop
			 }
			 else{
			 	timer_wait();
				continue;
			 }
			 
			
			
			 if(pageIndex > change_count || pageIndex < 1){
				 Error.wrong6A9C_confirm_page_index();// unexpected
			 }
			 
			 if(1 == pageIndex){
				 show_up = false;
			 }
			 else{
				 show_up = true;
			 }
			 if(change_count == pageIndex){
				 show_down = false;
			 }
			 else{
				 show_down = true;
			 }
			 output_offs = get_output_offs_for_one_index(temp_addr_buf[(short)(pageIndex )]);
			 // dust的output的地址
			 addr_buf_len =  calculate_show_address(transation_buf, output_offs, buf, g_larger_buf_in_RAM, addr_buf_offs);
			 
			 output_offs = get_output_offs_for_one_index(temp_addr_buf[(short)(pageIndex + change_count)]);//USDT的output地址
			/* 23 = 
			 8(BTC output amout) +
			 1(script len) +
			 1(OP_RETURN) +
			 1(usdt script len )
			 4(omni) +
			 2(交易版本) +
			 2(交易类型) +
			 4(代表USDT) 
			*/
			output_offs+=23;
			 ui_mgr.show_page(transation_buf,output_offs,
					          g_larger_buf_in_RAM,addr_buf_offs,addr_buf_len,
							  show_up,show_down,buf);
			 
		 }
		 return keyPress;	 
  }
    short confirm_output( byte[] buf)
    {
		 byte change_count = get_change_addr_count();
		 // 共有 output_count - change_count
		 short keyPress;
		 short pageIndex = 1;
		 boolean show_up = false,  show_down;
		 if(change_count > 2){
			 show_down = true;
		 }
		 else{
			 show_down = false;
		 }
		 
		
	    byte[] temp_addr_buf = get_change_addr_buf();
	    byte[] transation_buf = get_transation_data_buf();
		short output_offs = get_output_offs_for_one_index(temp_addr_buf[(short)(pageIndex )]);
		short addr_buf_offs = get_script_sig_buf_offs();	
		short addr_buf_len =  calculate_show_address(transation_buf, output_offs, buf, g_larger_buf_in_RAM, addr_buf_offs);
		
		ui_mgr.show_page(transation_buf,output_offs,
				g_larger_buf_in_RAM,addr_buf_offs,addr_buf_len,
						false,show_down,buf);
		Buttons.startWaitKeysPress();				
		Timer.wait((short)50); 
		
		while(true){
			  // Wait for the button, if it is OK, enter the confirmation page and jump out of the current loop
			 
			 
			 keyPress = Buttons.getKeysPressed();
			 
			 if((keyPress& Buttons.BUTTON_UP_PRESSED) == Buttons.BUTTON_UP_PRESSED){
				 
				 if(1 == pageIndex){
					 // Do nothing, current page
					 timer_wait();
					 continue;
				 }
				 else {
					 pageIndex -= 1;
				 }
			 }
			 else if((keyPress& Buttons.BUTTON_DOWN_PRESSED) == Buttons.BUTTON_DOWN_PRESSED){
				 if(change_count == pageIndex){
					// Do nothing, current page
					timer_wait();
					 continue;
				 }
				 else{
					 pageIndex += 1;
				 }
			 }
			 else if((keyPress& Buttons.BUTTON_OK_PRESSED) == Buttons.BUTTON_OK_PRESSED){
				 
				 break;//jump out of the loop
			 }
			 else if((keyPress& Buttons.BUTTON_CANCEL_PRESSED) == Buttons.BUTTON_CANCEL_PRESSED){
			 
				 break;//jump out of the loop
			 }
			 else{
			 	timer_wait();
				continue;
			 }
			 
			
			
			 if(pageIndex > change_count || pageIndex < 1){
				 Error.wrong6A9C_confirm_page_index();// unexpected 
			 }
			 
			 if(1 == pageIndex){
				 show_up = false;
			 }
			 else{
				 show_up = true;
			 }
			 if(change_count == pageIndex){
				 show_down = false;
			 }
			 else{
				 show_down = true;
			 }
			
			 output_offs = get_output_offs_for_one_index(temp_addr_buf[(short)(pageIndex)]);
			 
			 addr_buf_len =  calculate_show_address(transation_buf, output_offs, buf, g_larger_buf_in_RAM, addr_buf_offs);
			 ui_mgr.show_page(transation_buf,output_offs,
					          g_larger_buf_in_RAM,addr_buf_offs,addr_buf_len,
							  show_up,show_down,buf);
			 
		 }
		 return keyPress;	 
  }
  short calculate_fees(APDU apdu){
    	byte[] transation_buf = get_transation_data_buf();
	    byte[] _amount_pubkey_path_buf = get_amount_pubkey_path_buf();
	    short  _amount_pubkey_path_buf_offs = get_amount_pubkey_path_offs() ;
	    // Inputs的金额减去Outputs的金额为手续费
	    short nInputCount  =  get_input_count();
	    byte[] buf = apdu.getBuffer();
	    
	    Util.arrayFillNonAtomic(buf,_0,(short)buf.length, (byte)0);
	    byte carry = 0;
	    for(short i = 0 ; i < nInputCount; ++i){
	    	
		    carry =  Common.Amount_Add_LE(buf,UI.TOTAL_AMOUNT_OFFSET_IN_APDU_BUF, _amount_pubkey_path_buf, _amount_pubkey_path_buf_offs ,buf,_0);
		    Util.arrayCopyNonAtomic(buf,(short)0,buf,UI.TOTAL_AMOUNT_OFFSET_IN_APDU_BUF,(short)8);
		    _amount_pubkey_path_buf_offs += 8; 
		    if(carry == 1){
		    	 Error.wrong6A9D_ADD_OR_SUB_OVERFLOW();
		    }
	    }
	    
	    // 清空来计算output的amount的值
	    Util.arrayFillNonAtomic(buf,(short)16,(short)16, (byte)0);
		
	    short  output_offs = getOutputOffs();
	    short  output_count = transation_buf[output_offs++];
	    
	    _amount_pubkey_path_buf_offs = output_offs;
	    for(short i = 0 ; i < output_count ; ++i){
	    	
		    carry =  Common.Amount_Add_LE( buf,(short)24, 
		    							   transation_buf, output_offs,
										   buf,(short)16);											    
		    
		    Util.arrayCopyNonAtomic(buf,(short)16,  buf,(short)24,(short)8);
		    if(carry == 1){
		    	 Error.wrong6A9D_ADD_OR_SUB_OVERFLOW();
		    }
		    output_offs += 8;
		    byte pubkey_len = transation_buf[output_offs++];
		    output_offs += pubkey_len;
		 
	    } 
         
       

	    carry =  Common.Amount_Sub_LE(
									  buf,UI.TOTAL_AMOUNT_OFFSET_IN_APDU_BUF,
									  buf,(short)16,
									  buf,UI.FEE_OFFSET_IN_APDU_BUF
									 ); 
		if(carry == 1){
			 Error.wrong6A9D_ADD_OR_SUB_OVERFLOW();
		} 
		
	    if(!base_addres.is_USDT())
	    {
			Util.arrayFillNonAtomic(buf,(short)16,(short)16, (byte)0);
			output_offs = _amount_pubkey_path_buf_offs ;
			for(short i = 0 ; i < output_count ; ++i){
				
				if(is_not_change_addr(i) == false)
				{
					output_offs += 8;
					byte pubkey_len = transation_buf[output_offs++];
					output_offs += pubkey_len;
					continue;
				}
			
				
				carry =  Common.Amount_Add_LE( buf,(short)24, 
											   transation_buf, output_offs,
											   buf,(short)16);											    
				
				Util.arrayCopyNonAtomic(buf,(short)16,  buf,(short)24,(short)8);
				if(carry == 1){
					 Error.wrong6A9D_ADD_OR_SUB_OVERFLOW();
				}
				output_offs += 8;
				byte pubkey_len = transation_buf[output_offs++];
				output_offs += pubkey_len;
			 
			} 
			Util.arrayFillNonAtomic(buf,UI.TOTAL_AMOUNT_OFFSET_IN_APDU_BUF,(byte)0x08,(byte)0x00);
			
			carry =  Common.Amount_Add_LE( buf,UI.FEE_OFFSET_IN_APDU_BUF, 
						buf, (short)16,
					   buf,UI.TOTAL_AMOUNT_OFFSET_IN_APDU_BUF);	
			if(carry == 1){
				 Error.wrong6A9D_ADD_OR_SUB_OVERFLOW();
			}
	    }
	    else{
			  _amount_pubkey_path_buf_offs = get_change_addr_count();
			 _amount_pubkey_path_buf_offs = (short)(_amount_pubkey_path_buf_offs/2);
			
			byte[] temp_addr_buf = get_change_addr_buf();
			
			output_offs = get_output_offs_for_one_index(temp_addr_buf[(short)(1  + _amount_pubkey_path_buf_offs)]);
			/* 23 = 
			 8(BTC output amout) +
			 1(script len) +
			 1(OP_RETURN) +
			 1(usdt script len )
			 4(omni) +
			 2(交易版本) +
			 2(交易类型) +
			 4(代表USDT) 
			*/
			output_offs+=23;
			Util.arrayCopyNonAtomic(transation_buf,(short)output_offs,  
									buf,UI.TOTAL_AMOUNT_OFFSET_IN_APDU_BUF,(short)8);
			
	    }
	   
		
		short keyPress;
		ui_mgr.show_doulble_confirm_page(buf);
		while(true){
			 
			 keyPress = Buttons.getKeysPressed();
			 
			 if((keyPress& Buttons.BUTTON_OK_PRESSED) == Buttons.BUTTON_OK_PRESSED){			
				 break;
			 }
			 else if((keyPress& Buttons.BUTTON_CANCEL_PRESSED) == Buttons.BUTTON_CANCEL_PRESSED){
			 	
				 break;
			 }
			 else if((keyPress& Buttons.BUTTON_UP_PRESSED) == Buttons.BUTTON_UP_PRESSED){
				 break;
			 }
			 timer_wait();
		}
		return keyPress;
    }
    
	void reset_transation_state(){
		for(short i = _0; i < short_buf_in_RAM.length; ++i){
			short_buf_in_RAM[i] = _0;
		}
		Util.arrayFillNonAtomic(store_TL_buffer, _0, (short) store_TL_buffer.length, (byte)0);
		Util.arrayFillNonAtomic(byte_buf_in_RAM, _0, (short) byte_buf_in_RAM.length, (byte)0);
		Util.arrayFillNonAtomic(g_temp_buffer_in_RAM, _0, (short) g_temp_buffer_in_RAM.length, (byte)0);
	}

   
    byte check_and_get_state(byte p1){
    	short state = byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ];
		switch(state){
		  case  STATE_NONE :// 0x00:
		  
		  case  STATE_AMOUNT :// 0x01:
		  
		  case  STATE_AMOUNT_DONE :// 0x02:
		   
		  case  STATE_PATH :// 0x03:
		  
		  case  STATE_PATH_DONE :// 0x04:
			
		  case  STATE_TRANSACTION :// 0x05:
		        break ;
		        
		  case  STATE_TRANSACTION_DONE :// 0x05:
		        break ;	     
		  case STATE_CHANGE_ADDR:
			  break;
			  
		  case  STATE_DONE :// 0x06:
			  byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] = STATE_NONE;
		  	 break ;
		  	 
		  default:
		  	Error.wrong6A86_incorrect_P1P2();
		  	break;
      }
      switch(p1){
		case  P1_ONLY   :// 0X00;
		case  P1_FIRSET :// 0X01;
		    UI.showMainUI(false);
			byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] = STATE_NONE;
			break ;
			
		case  P1_NEXT :// (byte)0x80;
		case  P1_LAST :// (byte)0xFF;
			 break ;
		default:
			Error.wrong6A86_incorrect_P1P2();
			break;
      }
      
      return byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ];
    }
   
    
    /*
     * we need at lest 4 bytes for an TLV data , so when the data less than 4 bytes,we store them , wait for next apdu. 
     */
    short store_surplus_data_or_assemble_TL(short offs,short apdu_data_len,byte[] buf){

    	short remain_len  = store_TL_buffer[0];
    	if(remain_len != 0)
    	{
        	if(buf[ISO7816.OFFSET_P1] == P1_ONLY){
        		Error.wrong6A86_incorrect_P1P2();
        	} 	
    	}
    	else{
    		
        	if(buf[ISO7816.OFFSET_P1] == P1_ONLY){
        		return offs;
        	} 	
        	
    	}
    	
    	
     	if( offs + _4 <= (apdu_data_len + remain_len)  || buf[ISO7816.OFFSET_P1] == P1_LAST){
     		if(remain_len == 0){
     			return offs;
     		}
        	short _new_offs = (short)(offs - remain_len);
        	if(_new_offs < 0){
        		Error.wrong6A96_API_USAGE_WRONG();
        	}
        	
        	Util.arrayCopyNonAtomic(store_TL_buffer, (short)1, buf, _new_offs, remain_len);
        	store_TL_buffer[0] = 0;
        	return _new_offs;
     	}
     	else{
     		//Not enough. I'm going to store it. I'm going to wait for the next APDU
     		apdu_data_len = (byte)(apdu_data_len - offs);
    		remain_len += 1;// 
    		Util.arrayCopyNonAtomic(buf, offs, store_TL_buffer, remain_len, apdu_data_len); 
    		store_TL_buffer[0] += apdu_data_len;
    		return -1;
     	}
      	
    }
    void add_change_address_index(short index){
    	/*
    	 *  g_largerBuf[sig_offs     ]  stores how many total chane addresses there are
    	 *  g_largerBuf[sig_offs + 1 ] Stores the current number of chane addresses
    	 *  
    	 */
		short sig_offs = get_script_sig_buf_offs();
		short len =  g_larger_buf_in_RAM[++sig_offs] ;
		g_larger_buf_in_RAM[sig_offs++] += 1;
		g_larger_buf_in_RAM[(short)(sig_offs + len)] = (byte)index;
		
    }
       short process_NONE_STAE(byte[] buf, short offs , short apdu_data_len,byte p1){
		if(p1 != P1_FIRSET && p1 != P1_ONLY ){
			Error.wrong6A86_incorrect_P1P2();
		}
		//Must be first or only pack
		byte p2 =  buf[ISO7816.OFFSET_P2];
		short	nInputCount = (short)(buf[offs++] & 0xff);
		
		if((SIG_LEN+1)*nInputCount > g_larger_buf_in_RAM.length){
			Error.wrong6A97_OUT_OF_STORAGE();//It's too big to store
		}
		if(nInputCount >= INPUT_NUM_LIMIT){
			ui_mgr.show_data_transmit();
		}
		byte_buf_in_RAM[ TRANSATION_INPUT_COUNTS_OFFSET ] = (byte)(nInputCount);	
		
		byte_buf_in_RAM[ TRANSATION_WIGNET_TYPE_OFFSET] = (p2);//withness flag

	    byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] = STATE_INPUT_DONE;

		return offs;
    }
    short  process_AMOUNT_STATE(byte[] buf, short offs , short apdu_data_len,byte p1){

		byte state = byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ];
		short remainLen ;
		
		if(state == STATE_INPUT_DONE){	
			offs = store_surplus_data_or_assemble_TL(offs,apdu_data_len,buf);
			if(offs == -1){
		        return -1;
		    }
	    	if( buf[offs++] != Common.AMOUNTS_DATA__TAG ) {
				ISOException.throwIt(Error.NOT_FOUND_AMOUNTS_DATA__TAG);
			}
			remainLen = getLength(buf, offs);
			offs += getLengthBytes(remainLen);
			short_buf_in_RAM[REMAIN_DATA_LEN] = remainLen;
		    short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET] = 0;
			byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] = STATE_AMOUNT;		
		}
		else{
			 remainLen = short_buf_in_RAM[REMAIN_DATA_LEN];
		}	
		short temp_buf_offset = short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET];
	
		if(apdu_data_len < (remainLen+ offs))
		{
			if(p1 ==  P1_LAST || p1 == P1_ONLY){
				
				Error.wrong6A86_incorrect_P1P2();
			}
			 apdu_data_len = (short)(apdu_data_len - offs);
			
			 Util.arrayCopyNonAtomic(buf, offs, g_larger_buf_in_RAM, temp_buf_offset, apdu_data_len);
			
			 short_buf_in_RAM[REMAIN_DATA_LEN] -= apdu_data_len;
			 short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET] += apdu_data_len;
			 return -1;
		}	
		Util.arrayCopyNonAtomic(buf, offs, g_larger_buf_in_RAM, temp_buf_offset, remainLen);
						
		offs += remainLen;	
		short_buf_in_RAM[REMAIN_DATA_LEN] = 0;
		byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] = STATE_AMOUNT_DONE;
		
		short nInputCount =  get_input_count();
		// amount  (8 bytes)  pubkey (33 bytes)
		temp_buf_offset += (short)( nInputCount*33 + remainLen ) ;
		short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET]  = temp_buf_offset;

		return offs;
    }
	
    short  process_PATH_STATE(byte[] buf, short offs,short apdu_data_len, byte p1){

		short temp_buf_offset = short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET];
		byte state = byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ];
		short remainLen;
		if(state == STATE_AMOUNT_DONE){		
			offs = store_surplus_data_or_assemble_TL(offs,apdu_data_len,buf);
			if(offs == -1){
		        return -1;
		    }
			
			// data of path
			if( buf[offs++] != Common.PATHS_DATA__TAG ) {
				ISOException.throwIt(Error.NOT_FOUND_BIP32PATH_TAG);
			}
			remainLen = getLength(buf, offs);
			offs += getLengthBytes(remainLen);
		    Util.setShort(g_larger_buf_in_RAM, temp_buf_offset, remainLen);
			byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] = STATE_PATH;
			short_buf_in_RAM[REMAIN_DATA_LEN] = remainLen;
			
			temp_buf_offset += 2;
			short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET] = temp_buf_offset;
				
		}
		else{
			remainLen = short_buf_in_RAM[REMAIN_DATA_LEN];
		}
		
		if(apdu_data_len < (remainLen+ offs))
		{
			if(p1 ==  P1_LAST || p1 == P1_ONLY){
	
				Error.wrong6A86_incorrect_P1P2();
			}
			 apdu_data_len = (short)(apdu_data_len - offs);
			
			 Util.arrayCopyNonAtomic(buf, offs, g_larger_buf_in_RAM, temp_buf_offset, apdu_data_len);
		
			 short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET] += apdu_data_len;
			 short_buf_in_RAM[REMAIN_DATA_LEN] = (short)(remainLen - apdu_data_len);
			 return -1;
		}
		Util.arrayCopyNonAtomic(buf, offs, g_larger_buf_in_RAM, temp_buf_offset, remainLen);
		offs += remainLen;

		short_buf_in_RAM[AMOUNT_PUB_PATH_DATA_LEN_OFFSET] += remainLen;
		short_buf_in_RAM[REMAIN_DATA_LEN] = 0;
		
	
        get_pub_key_when_process_path_state();
        
        byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] = STATE_PATH_DONE;
        
		return offs;
	
    }

    short process_TRANSATATION_STATE(byte[] buf, short offs , short apdu_data_len,byte p1){
    	byte state = byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ];
    	short remainLen;
    	if(state == STATE_PATH_DONE){	
			offs = store_surplus_data_or_assemble_TL(offs,apdu_data_len,buf);
			if(offs == -1){
		        return -1;
		    }	
			// data of transation 
			if( buf[offs++] != Common.TRANSATION_DATA_TAG ) {
				ISOException.throwIt(Error.NOT_FOUND_TRANSCATION_RAW_TEXT_TAG);
			}
			remainLen = getLength(buf, offs);
			offs += getLengthBytes(remainLen);
			
			short_buf_in_RAM[ REMAIN_DATA_LEN ] = remainLen;	
			short_buf_in_RAM[ TRANSATION_DATA_LEN_OFFSET ] = remainLen;			
			byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] = STATE_TRANSACTION ;	
			short_buf_in_RAM[DATA_LEN_IN_RAM_OFFSET] = _0;
			calculate_storage_scheme();
			allocate_storage();
			store_amount_pubkey_and_path();
		}
    	else{
    		remainLen = short_buf_in_RAM[REMAIN_DATA_LEN];
    	}
 	   if(STORAGE_ONLY_SIGN_DATA == byte_buf_in_RAM[TRANSATION_STORAGE_SCHEME_OFFSET]){
 		   state = 0;	   
	   }
 	   else{
 		   state = 1;
 	   }
       short cur_offs_in_ram_buf = short_buf_in_RAM[DATA_LEN_IN_RAM_OFFSET];
	   short flash_buf_offset = (short)(short_buf_in_RAM[TRANSATION_DATA_LEN_OFFSET] - remainLen - cur_offs_in_ram_buf);
		
	   if(apdu_data_len < (remainLen+ offs))
	   {
			if(p1 ==  P1_LAST || p1 == P1_ONLY){
				
				Error.wrong6A86_incorrect_P1P2();
			}
			apdu_data_len = (short)(apdu_data_len - offs);
			
			 if(state == 0){
				 if(cur_offs_in_ram_buf + apdu_data_len > g_larger_buf_in_RAM.length){
					 Util.arrayCopyNonAtomic(g_larger_buf_in_RAM, _0, flash_transation__buf, flash_buf_offset, cur_offs_in_ram_buf);
					 cur_offs_in_ram_buf = _0;
				 }
			 }
			 
			 Util.arrayCopyNonAtomic(buf, offs, g_larger_buf_in_RAM, cur_offs_in_ram_buf, apdu_data_len);
			 cur_offs_in_ram_buf += apdu_data_len;
			 short_buf_in_RAM[REMAIN_DATA_LEN] = (short)(remainLen - apdu_data_len);
			 short_buf_in_RAM[DATA_LEN_IN_RAM_OFFSET] = cur_offs_in_ram_buf;
			 return -1;
	   }
		
	   if(state == 0){
			if(cur_offs_in_ram_buf + apdu_data_len > g_larger_buf_in_RAM.length){
				 Util.arrayCopyNonAtomic(g_larger_buf_in_RAM, _0, flash_transation__buf, flash_buf_offset, cur_offs_in_ram_buf);
				 flash_buf_offset += cur_offs_in_ram_buf;
				 cur_offs_in_ram_buf = _0;
		    }	
	   }
	   Util.arrayCopyNonAtomic(buf, offs, g_larger_buf_in_RAM, cur_offs_in_ram_buf, remainLen);
	    
	   if(state == 0){
		   cur_offs_in_ram_buf+=remainLen;
		   Util.arrayCopyNonAtomic(g_larger_buf_in_RAM, _0,
				   flash_transation__buf, flash_buf_offset,cur_offs_in_ram_buf);		   
	   }
		
	   offs += remainLen;   
	   short_buf_in_RAM[REMAIN_DATA_LEN] = _0;
	   byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] = STATE_TRANSACTION_DONE ;
	   short_buf_in_RAM[DATA_LEN_IN_RAM_OFFSET] = 0;
	
       return offs;
    }
    
    short process_CHANGE_ADDR_STATE(byte[] buf, short offs , short apdu_data_len,byte p1){
    	byte state = byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ];
    	short remainLen;
		if(state == STATE_TRANSACTION_DONE){
			offs = store_surplus_data_or_assemble_TL(offs,apdu_data_len,buf);
			if(offs == -1){
		        return -1;
		    }
			if( buf[offs++] != Common.CHANGE_ADDR_IDX_TAG ) {
				ISOException.throwIt(Error.NOT_FOUND_CHANGE_ADDR_IDX_TAG);
			}
			remainLen = getLength(buf, offs);
			offs += getLengthBytes(remainLen);
			
			short_buf_in_RAM[ CHANGE_ADDR_DATA_LEN_OFFSET ] = remainLen;	
			short_buf_in_RAM[ REMAIN_DATA_LEN ] = remainLen;			
			byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] = STATE_CHANGE_ADDR ;
		}
		else{
			remainLen = short_buf_in_RAM[REMAIN_DATA_LEN];
		}
		
	    if(p1 ==  P1_LAST || p1 == P1_ONLY)
	    {	   	
			if(apdu_data_len != (remainLen+ offs))
			{
				 Error.wrong6A86_incorrect_P1P2();
			}				
	    }
	    else if(p1 == P1_NEXT)
	    {
			if(apdu_data_len >= (remainLen+ offs))
			{
				 Error.wrong6A86_incorrect_P1P2();
			}	
	    }
	    
	    if(short_buf_in_RAM[ CHANGE_ADDR_DATA_LEN_OFFSET ] == short_buf_in_RAM[ REMAIN_DATA_LEN ]){
	    	if(offs < apdu_data_len){
	    		/*
	    		 * The number of index of the change address and the data of index are temporarily stored in the signature data area
	    		 */
	    		short sig_offs = get_script_sig_buf_offs();
	    		g_larger_buf_in_RAM[ sig_offs++ ]  = buf[offs++];// Number of index addresses 
	    		g_larger_buf_in_RAM[ sig_offs   ]  = 0x00; // The number that has been stored
	    		short_buf_in_RAM[ REMAIN_DATA_LEN ] -= 1;
	    	}
	    }
	    
	    apdu_data_len -= offs;
	   	   
	   	// Process the change address in the apdu buf. Finally, if there is not enough change address left, return it and wait for the next apdu
		/*
		 *  data：   
		 *  g_temp_buffer[0]is The length of the remaining bytes
		 *  g_temp_buffer[1]is index
		 *  g_temp_buffer[2]is pathLen
		 *   
		 *  
		 */
		short remain_len = (short)( g_temp_buffer_in_RAM[0] & 0xff );
		while(true)
		{
			if(remain_len == 0){
			 	
				break;
			}
			if(remain_len == 1){
				// we have index ,we need to know pathlen
				g_temp_buffer_in_RAM[2] = buf[offs++];
				apdu_data_len -=1;
				remain_len += 1;
			}
			
			if(remain_len >= 2){
				
				short pathLen = g_temp_buffer_in_RAM[2];
				short need_len = (short) (pathLen - (remain_len  - 2));
				
				// when we know the pathlen ,we need  pathlen bytes to process
				remain_len += 1;
				if(apdu_data_len < need_len){		
					Util.arrayCopyNonAtomic(buf, offs, g_temp_buffer_in_RAM, remain_len, apdu_data_len);
					g_temp_buffer_in_RAM[0] += apdu_data_len;					
					return  -1;//wait for the next APDU for enough data
				}
				else{
					Util.arrayCopyNonAtomic(buf, offs, g_temp_buffer_in_RAM, remain_len, need_len);
					apdu_data_len-=need_len;
					offs +=need_len;
					short index  = (short)(g_temp_buffer_in_RAM[1] & 0xff);
					check_change_address(index,g_temp_buffer_in_RAM,(short)3,pathLen);
					add_change_address_index(index);
				}
			}
						
			break;	
		}
		
		while (apdu_data_len > 0) 
		{
			if(apdu_data_len < 2){// To avoid out of range
			    g_temp_buffer_in_RAM[0] = (byte)apdu_data_len;
			    Util.arrayCopyNonAtomic(buf, offs, g_temp_buffer_in_RAM, (short)1, apdu_data_len);	
			    return -1;
				
			}
			short index  = (short)(buf[offs++] & 0xff);
			short pathLen = buf[offs++];
			
			apdu_data_len -= 2;
			
			if(apdu_data_len >= pathLen){			
				check_change_address(index, buf, offs, pathLen);
				add_change_address_index(index);
				offs += pathLen;
				apdu_data_len -= pathLen;
			}
			else{
				
				offs -= 2;
				apdu_data_len += 2;
				g_temp_buffer_in_RAM[0] = (byte)apdu_data_len;
			    Util.arrayCopyNonAtomic(buf, offs, g_temp_buffer_in_RAM, (short)1, apdu_data_len);	
			    return -1;
			}		
		}
		
	    if(p1 ==  P1_LAST || p1 == P1_ONLY){
	    	process_last_block(buf);	
	    	   	
	    }
	    return offs;
	
    }
    void processTransationBlock(APDU apdu)
	{
		byte[] buf = apdu.getBuffer();	
		short apdu_data_len =  (short)(buf[ISO7816.OFFSET_LC] & 0xff ) ;
		apdu_data_len +=  ISO7816.OFFSET_CDATA;
	    short offs = ISO7816.OFFSET_CDATA;
		byte p1 = buf[ISO7816.OFFSET_P1];
		short state = byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ];
		
		apdu.setIncomingAndReceive();
		
		state = check_and_get_state( p1);
	  
		if(STATE_NONE == state){
			reset_transation_state();
			offs =process_NONE_STAE(buf, offs, apdu_data_len, p1);
			state = byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ];			
		}

		if(STATE_AMOUNT == state || STATE_INPUT_DONE == state){
	        if(offs == apdu_data_len){
	        	return;
	        }	        
			offs = process_AMOUNT_STATE(buf,offs,apdu_data_len,p1);
	        if(offs == -1){
	        	return;
	        }
            state = byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ];          
		}
		

		if(state == STATE_AMOUNT_DONE || STATE_PATH == state){      
	        if(offs == apdu_data_len){
	        	return;
	        }
			offs = process_PATH_STATE(buf,  offs, apdu_data_len,  p1);
	        if(offs == -1){
	        	return;
	        }
            state = byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ];
		}

		if(state == STATE_PATH_DONE || STATE_TRANSACTION == state){	      
	       if(offs == apdu_data_len){
	        	return;
	       }
		   offs = process_TRANSATATION_STATE(buf,  offs ,  apdu_data_len, p1);
	       if(offs == -1){
	        	return;
	       }
           state = byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ];
		}
		
		if(state == STATE_CHANGE_ADDR || STATE_TRANSACTION_DONE == state){
	       if(offs == apdu_data_len){
	        	return;
	       }
			process_CHANGE_ADDR_STATE(buf,  offs ,  apdu_data_len, p1);
			
		}
			
	}
	


void check_change_address(short index,byte[] path_buf, short path_buf_offs, short path_buf_len)
	{
	
		short output_offs = get_output_offs_for_one_index(index);

		output_offs += 8;//amount
		byte[] transation_buf = get_transation_data_buf();
		byte addr_type =  Address.get_addr_type(transation_buf[output_offs], transation_buf[ (short)(output_offs  + 1 )]);
		
		CoinManager.getCoinPubData(path_buf,path_buf_offs, path_buf_len,g_temp_buffer_in_RAM,_0,null,_0);
	   
					

		if((g_temp_buffer_in_RAM[64] & 0x01) > 0){// The compressed form of the public key
			g_temp_buffer_in_RAM[0] = 0x03; 
		}
		else{
			g_temp_buffer_in_RAM[0] = 0x02; 
		}
	
    	// Common.P2PKH 的 scriptPubKeyLen ( value is 0x19) =  OP_DUP(1byte)  OP_HASH160(1byte)  hashLen(value is 0x14，1byte)  pubkeyHash(0x14byte)  OP_EQUALVERIFY(1byte)  OP_CHECKSIG(1byte)
    	
    	// Common.Common.P2WPKH 的 scriptPubKeyLen (value is 0x16) = OP_0(1byte)  hashLen(value is 0x14，1byte)  pubkeyHash(0x14byte)
    	
    	// Common.P2SH 的 scriptPubKeyLen    (value is 0x17) =    OP_HASH160(1byte)  hashLen(value is 0x14，1byte)  pubkeyHash(0x14byte) OP_EQUAL

    	
    	// Common.P2WSH 的 scriptPubKeyLen    (value is 0x22) =  OP_0 (1byte)  hashLen(value is 0x20，1byte)  pubkeyHash(0x20byte)
		
		switch (addr_type)
		{
			case Address.P2PKH:
			case Address.P2WPKH:
			
			{
				sha256Assist.doFinal(g_temp_buffer_in_RAM, _0, (short)33, g_temp_buffer_in_RAM, _0);
				_RIPEMD160.doFinal(g_temp_buffer_in_RAM, _0,(short)32, g_temp_buffer_in_RAM, _0);

						
				if(addr_type == Address.P2PKH){
					output_offs +=4;
				}
				else{
					output_offs +=3;
				}
		
				if(_0 != Util.arrayCompare(g_temp_buffer_in_RAM, _0, transation_buf, output_offs, (short)0x14)){				
					Error.wrong6A9A_CHANGE_ADDR_NOT_MATCH();
				}				
			}
			break;
			 case Address.P2SH:
			 {
				
				//  get  reedemScript: 0x00  +  0x14  +  [Hash160(pubkey)] 
				// then  get hash1
				// then get hash160(hash1)
				sha256Assist.doFinal(g_temp_buffer_in_RAM, _0, (short)33, g_temp_buffer_in_RAM, _0);
				_RIPEMD160.doFinal(g_temp_buffer_in_RAM, _0,(short)32, g_temp_buffer_in_RAM, (short)2);
				g_temp_buffer_in_RAM[0] = 0x00;
				g_temp_buffer_in_RAM[1] = 0x14;
				sha256Assist.doFinal(g_temp_buffer_in_RAM, _0, (short)0x16, g_temp_buffer_in_RAM, _0);
				_RIPEMD160.doFinal(g_temp_buffer_in_RAM, _0,(short)32, g_temp_buffer_in_RAM, (short)0);

				output_offs +=3;
		
				if(_0 != Util.arrayCompare(g_temp_buffer_in_RAM, _0, transation_buf, output_offs, (short)0x14)){				
					Error.wrong6A9A_CHANGE_ADDR_NOT_MATCH();
				}	
			 }
			 break;
			case Address.P2WSH:
			{
				sha256Assist.doFinal(g_temp_buffer_in_RAM, _0, (short)33, g_temp_buffer_in_RAM, _0);
				sha256Assist.doFinal(g_temp_buffer_in_RAM, _0, (short)32, g_temp_buffer_in_RAM, _0);
			
				output_offs += 3;
				
							
				if(_0 != Util.arrayCompare(g_temp_buffer_in_RAM, _0, transation_buf, output_offs, _32)){
					
					Error.wrong6A9A_CHANGE_ADDR_NOT_MATCH();
				}
			}
				break;
			default:
				Error.wrong6A99_NOT_KNOWND_ADDRE_TYPE();
				break;
		}
	}
	
	  
	void process_last_block(byte[] buf)
	{
		// check change address
		short sig_offs = get_script_sig_buf_offs();
		short change_index_count =  g_larger_buf_in_RAM[sig_offs++] ;
		if(g_larger_buf_in_RAM[sig_offs++] !=change_index_count){
			Error.wrong6A9B_CHANGE_ADDR_NUM_NOT_MATCH();// check num match
		}
		

		short output_offs = getOutputOffs();
		byte[] transation_buf = get_transation_data_buf();
	    short  output_count = (short)(transation_buf[output_offs++] & 0xff);
	       
	    output_offs = (short)(output_count - change_index_count);
	    if(output_offs <= 0){
	    	Error.wrong6AA0_OUTPUTS_NUM();
	    }
	    short usdt_buf_ptr = 0;
	    short no_change_buf_off  =_0 ;
	    buf[no_change_buf_off++] =  (byte)(output_offs);
       
	    for(short i = 0 ; i < output_count ; ++i){	
	    	short k ;
			for(k = 0 ; k < change_index_count ; ++k ){
				 if(i  == g_larger_buf_in_RAM[(short)(k + sig_offs)]){
					 break;
				 }
			} 	
			if(k < change_index_count){
				continue;//no match
			}
			if(base_addres.is_USDT()){
				
				output_offs = get_output_offs_for_one_index(i);
				output_offs += 8;//amount
				byte addr_type =  Address.get_addr_type(transation_buf[output_offs], transation_buf[ (short)(output_offs  + 1 )]);
		        if(Address.P2PKH_USDT == addr_type){
			       g_temp_buffer_in_RAM[usdt_buf_ptr++] = (byte)i;
			       continue;
		        }
			}	
			buf[no_change_buf_off++] = (byte)i;	
	   } 
	   
	   if(base_addres.is_USDT()){
		  if(no_change_buf_off != (usdt_buf_ptr +1)){
			Error.wrong6AA8_USDT_NUMBER_NOT_MATCH_DUST();
		  }
	      Util.arrayCopyNonAtomic(g_temp_buffer_in_RAM,_0, buf,no_change_buf_off, usdt_buf_ptr);
	      no_change_buf_off+=no_change_buf_off;	
	   }
	   
	   g_temp_buffer_in_RAM[0] = buf[0];   
	   Util.arrayCopyNonAtomic(buf, _0, get_change_addr_buf(), _0,no_change_buf_off);

	   short preimage_hash_offs = get_preimage_hash_buf_offs();
       byte[] preimage_hash_buf = get_preimage_hash_buf();
       if(preimage_hash_buf != g_larger_buf_in_RAM){
        	preimage_hash_buf = g_larger_buf_in_RAM;
        	preimage_hash_offs = get_script_sig_buf_offs();
       }
       switch(base_addres.get_coind_type())
       {
	   case Address.COIN_TYPE_BTC:
	   case Address.COIN_TYPE_LTC:
	   case Address.COIN_TYPE_USDT:
	   	{
		   if(is_withness()){
			   output_count =  generate_segwit_premage(buf,preimage_hash_buf,preimage_hash_offs);
		   }
		   else{
			   output_count = generate_NoSegwit_premage(buf,preimage_hash_buf,preimage_hash_offs);
		   }
		}
	   	break ;
	   case Address.COIN_TYPE_BCH:
	   	{
		   if(is_withness()){
			 Error.wrong6985_conditions_not_satisfied();
		   }
		   else{
			 output_count =  generate_BCH_premage(buf,preimage_hash_buf,preimage_hash_offs);
		   }
	   	}
	   	break ;
	   	default:
	   		Error.wrong6AA5_NOT_SUPPORT_COIND_TYPE();
	   		break ;
       }

	   buf = get_preimage_hash_buf();
	   if(preimage_hash_buf != buf){
	   	    // move hash value to flash buf
	        Util.arrayCopyNonAtomic(preimage_hash_buf, get_script_sig_buf_offs(), buf, get_preimage_hash_buf_offs(), output_count);
	    }
	  
	   byte_buf_in_RAM[ TRANSATION_PARSE_STATE_OFFSET ] = STATE_DONE ;
	}
}