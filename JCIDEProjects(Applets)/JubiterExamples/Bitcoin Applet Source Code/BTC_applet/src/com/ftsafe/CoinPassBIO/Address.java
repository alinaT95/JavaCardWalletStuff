package com.ftsafe.CoinPassBIO ;
import com.ftsafe.javacardx.btcmgr.CoinManager;
import javacard.framework.ISO7816;
import javacard.framework.JCSystem;



public class Address  
{
	
  static final byte 	OP_0 			    =   (byte)0x00;
  static final byte 	OP_HASH160 			=   (byte)0xa9;
  static final byte 	OP_EQUAL   			=   (byte)0x87;
  static final byte 	OP_DUP    			=   (byte)0x76;
    static final byte 	OP_RETURN           =  (byte)0x6a;
  static final byte     OP_EQUALVERIFY 	    =   (byte)0x88;
  static final byte     OP_CHECKSIG 		=   (byte)0xac;


	
	// [BIP0013]: 1-byte address version
	public static final byte   mainnet_p2pkh = 0x00;
	public static final byte   mainnet_p2sh = 0x05;
	public static final byte   mainnet_p2wpkh = 0x06;
	public static final byte   mainnet_p2wsh = 0x0A;
  
	public static final short  CASHADDR_P2KH =(0);
	public static final short  CASHADDR_P2SH =(8);
	public static final short  CASHADDR_160  =(0);
	
	public static final short  LTC_ADDRTYPE_P2PKH =(48);
	public static final short  LTC_ADDRTYPE_P2SH =(50);
	
		static final byte P2PKH = 0x00;       // pay to public key  hash
	static final byte P2SH = 0x01;        // pay  to script hash
	static final byte P2WPKH = 0x02;      // pay to witness public key hash
	static final byte P2WSH = 0x03;       //  pay to witness script hash
	static final byte P2SH_P2WPKH = 0x04;       // pay to witness public key  hash nest in P2SH
	static final byte P2SH_P2WSH = (byte)0x05;  // pay to witness script hash nesst in P2SH
	
	static final byte P2PKH_USDT = (byte)0xFE;  // pay to witness script hash nesst in P2SH
    static final byte UNABLE_DECODE_SCRIPT = (byte)0xFF;  // pay to witness script hash nesst in P2SH
    
	
   static final byte SIGHASH_DEFAULT_IS_ALL = 0;
    static final byte SIGHASH_ALL = 1;
    static final byte SIGHASH_NONE = 2;
    static final byte SIGHASH_SINGLE = 3;
  
    static final byte SIGHASH_ANYONECANPAY = (byte)0x80;
  
	public  final static  byte FORKID_TYPE_BTC =0X00;
	public  final  static byte FORKID_TYPE_BCH =0X40;
	
	public  final  static byte COIN_TYPE_BTC =0X00;
	public  final  static byte COIN_TYPE_BCH =0X01;
	public  final  static byte COIN_TYPE_LTC =0X02;
	public  final  static byte COIN_TYPE_USDT =0X03;
	
	static final byte COIN_TYPE_OFFSET = 0X00;

    
    byte[] coin_info_buf;
	
	byte get_coind_type(){
		return coin_info_buf[COIN_TYPE_OFFSET];
	}
	
	void set_coin_info(byte [] buf)
	{
		byte coid_type  = buf[ ISO7816.OFFSET_P1];
		switch(coid_type)
		{
			case COIN_TYPE_BTC:
			case COIN_TYPE_BCH:
			case COIN_TYPE_LTC:
			case COIN_TYPE_USDT:
				coin_info_buf[COIN_TYPE_OFFSET] = coid_type;
				break ;	
			default:
				Error.wrong6AA5_NOT_SUPPORT_COIND_TYPE();
				break ;
		}	
	}
	
    
	
	Address(){
		coin_info_buf = JCSystem.makeTransientByteArray((short)2, JCSystem.CLEAR_ON_DESELECT);
	}
    boolean is_BTC(){
	   return coin_info_buf[COIN_TYPE_OFFSET] == COIN_TYPE_BTC;
    }
    boolean is_LTC(){
    	return coin_info_buf[COIN_TYPE_OFFSET] == COIN_TYPE_LTC;
	    
    }
    boolean is_BCH(){
	    return coin_info_buf[COIN_TYPE_OFFSET] ==  COIN_TYPE_BCH;
    }
   
	void set_BCH(){
		   coin_info_buf[COIN_TYPE_OFFSET] =  COIN_TYPE_BCH;
	}
    void set_BTC(){
	    coin_info_buf[COIN_TYPE_OFFSET] =  COIN_TYPE_BTC;
    }
     void set_USDT(){
	    coin_info_buf[COIN_TYPE_OFFSET] =  COIN_TYPE_USDT;
    }
    boolean is_USDT(){
	   return coin_info_buf[COIN_TYPE_OFFSET] == COIN_TYPE_USDT;
    }
     void get_HASH_TYPE_buf(byte[] hash_type_buf){
   	
   	    int forkid;
   	    if(is_BCH()){
	   	     forkid = FORKID_TYPE_BCH;
   	    }
		else{
			 forkid = FORKID_TYPE_BTC;
		}   
		byte hash_type = SIGHASH_ALL;	
   	   
   	    forkid |= ((hash_type & 0xff));
   		for(short i = 0 ; i < 4; ++i){
		    hash_type_buf[i] = (byte)(forkid  >> i*8 );	
	    }
	   
   }
   // get hash type of transation.
   byte get_hash_type()
   {
	  	byte hash_type = SIGHASH_ALL;	
	  	 int forkid;
   	    if(is_BCH()){
	   	     forkid = FORKID_TYPE_BCH;
   	    }
		else{
			 forkid = FORKID_TYPE_BTC;
		} 
   	    forkid |= ((hash_type & 0xff));
   	    return (byte)forkid;
   }
   //  get public key version
    public   byte  get_pubkey_version(byte addr_type){
   			
   		byte pubkey_version = 0x00;	
   		if(is_BCH()){
			switch (addr_type) {
			
			case  P2PKH :// 0x00;       // pay to public key  hash
				pubkey_version = CASHADDR_P2KH;
				break;
			case  P2SH :// 0x01;        // pay  to script hash
				pubkey_version = CASHADDR_P2SH;
				break;

			default:
				Error.wrong6A99_NOT_KNOWND_ADDRE_TYPE();
				break;
			}
	   		return pubkey_version;
   		}	
   		if(is_LTC()){
			switch (addr_type) {
			
			case  P2PKH :// 0x00;       // pay to public key  hash
				pubkey_version = LTC_ADDRTYPE_P2PKH;
				break;
			case  P2SH :// 0x01;        // pay  to script hash
			case  P2SH_P2WSH:
			case  P2SH_P2WPKH:
				pubkey_version = LTC_ADDRTYPE_P2SH;
				break;

			default:
				Error.wrong6A99_NOT_KNOWND_ADDRE_TYPE();
				break;
			}
	   		return pubkey_version;
   		}
   		
   	    pubkey_version  = mainnet_p2pkh;
   	    
		switch (addr_type) {
		
		case  P2PKH :// 0x00;       // pay to public key  hash
			pubkey_version = mainnet_p2pkh;
			break;
		case  P2SH :// 0x01;        // pay  to script hash
			pubkey_version = mainnet_p2sh;
			break;
		case  P2WPKH :// 0x02;      // pay to witness public key hash
			pubkey_version = mainnet_p2wpkh;
			break;
		case  P2WSH :// 0x03;       //  pay to witness script hash
			pubkey_version = mainnet_p2wsh;
			break;
		case  P2SH_P2WPKH :// 0x04;       // pay to witness public key  hash nest in Common.P2SH
			pubkey_version = mainnet_p2sh;
			break;
		case  P2SH_P2WSH :// (byte)0x05;  // pay to witness script hash nesst in Common.P2SH
			pubkey_version = mainnet_p2sh;
			break;

		default:
			Error.wrong6A99_NOT_KNOWND_ADDRE_TYPE();
			break;
		}
	   return pubkey_version;
   }
    
    static byte get_addr_type(byte scriptPubKeyLen,byte next_byte_to_pub_key_len){
    	
    	// Common.P2PKH 的 scriptPubKeyLen (值为0x19) =  OP_DUP(1字节)  OP_HASH160(1字节)  hashLen(值为0x14，1字节)  pubkeyHash(0x14字节)  OP_EQUALVERIFY(1字节)  OP_CHECKSIG(1字节)
    	
    	// Common.P2WPKH 的 scriptPubKeyLen (值为0x16) = OP_0(1字节)  hashLen(值为0x14，1字节)  pubkeyHash(0x14字节)
    	
    	// Common.P2SH 的 scriptPubKeyLen    (值为0x17) =    OP_HASH160(1字节)  hashLen(值为0x14，1字节)  pubkeyHash(0x14字节) OP_EQUAL

    	// Common.P2WSH 的 scriptPubKeyLen    (值为0x22) =  OP_0 (1字节)  hashLen(值为0x20，1字节)  pubkeyHash(0x20字节)
    	if(0x19 == scriptPubKeyLen && OP_DUP == next_byte_to_pub_key_len){
    		return Address.P2PKH;
    	}
    	else if(0x22 == scriptPubKeyLen && OP_0 == next_byte_to_pub_key_len){
    		return Address.P2WSH;
    	}
    	else if(0x16 == scriptPubKeyLen ){
    		if(OP_0 == next_byte_to_pub_key_len)
				return Address.P2WPKH;
			else if(OP_RETURN == next_byte_to_pub_key_len){
				return P2PKH_USDT;
			}
    	}
    	else if(0x17 == scriptPubKeyLen && OP_HASH160 == next_byte_to_pub_key_len){
    		return Address.P2SH;
    	}
    	
    	return Address.UNABLE_DECODE_SCRIPT;
    }
    
    // add coin information to adrres list
    void add_coin_info(byte[] g_largerBuf, short out_len){
			byte[] nick = null;
			byte[] icon = null;
			
             if(is_BTC()){
	             nick = UI.btcNick;
	             icon = UI.btcIco;
             }
             else if(is_BCH()){
	             nick = UI.bchNick;
	             icon = UI.ico_bch; 
             }
             g_largerBuf[out_len] = 0x00;
			 short ret = CoinManager.addCoinInfo(nick, (short)0, (short)nick.length, 
					icon, (short)0, (short)icon.length, (byte)24, (byte)24,
					g_largerBuf, (short)0, (short)(out_len + 1));

            if(0 != ret){
	            Error.wrong6AA1_AddCoinInfo_FAILED();
            }
    }
}
