package com.ftsafe.CoinPassBIO;
import javacard.framework.Util;
public class Common
{
	//added by wumin in 20160720
	public static final byte INI_APP___________E5 = (byte)0xE5;
	
	
	public static final byte GET_VERSION_______E2  = (byte)0xE2;
	
	public static final byte SIGN______________2A = (byte)0x2a;		// 签名

	public static final byte GET_CHALLENGE_____84 = (byte)0x84;		// 随机数
	public static final byte GENERATE_MNEMONIC_48 = (byte)0x48;	// 助记码产生，跟后面的好像重了，返回一堆字符串（utf8?utf16）。
	public static final byte SELECT_TABLE______14 = (byte)0x14;		// 选择助记码表(多语言设置)

	public static final byte DEVICE_LABEL_INS______16 = (byte)0x16;		// 设备别名
	public static final byte SERIAL_NUMBER_INS______18 = (byte)0x18;		// 序列号

	
	public static final byte WRITE_TABLE_______D6 = (byte)0xd6;		// 设置助记码表
	public static final byte GENERATE_KEY______46 = (byte)0x46;		// 产生主密钥
	public static final byte KEY_TO_MNEMONIC___4C = (byte)0x4c;	    // 根据主密钥产生助记码
	public static final byte MNEMONIC_TO_KEY___C4 = (byte)0xc4;	    // 根据助记码恢复主密钥
	public static final byte EXPORT_X_PUB______E6 = (byte)0xe6;	    // 公钥xpub导出
	public static final byte EXPORT_X_PRIVATE______E8 = (byte)0xe8;	// 公钥xpub导出

	public static final byte INS_RECEIVE_BCASH_TRASATION_DATA_F5 = (byte)0xf5;	// 接受BCH交易数据，存储起来
	public static final byte EXPORT_X_PUB_ADDR___F6 = (byte)0xf6;	// 公钥pub 的地址导出
    public static final byte EXPORT_EXTEND_PUB_ADDR___F7 = (byte)0xf7;	// 外部公钥pub 的地址导出
    public static final byte INS_RECEIVE_BTC_TRASATION_DATA_F8 = (byte)0xf8;	// 接受BTC交易数据，存储起来
    public static final byte INS_READ_TRASATION_DATA_F9 = (byte)0xf9;	// 读签完名后的交易数据
    public static final byte INS_SET_BTC_UNIT__FA = (byte)0xfA;	//设置BTC金额的进制
       public static final byte INS_SET_TIMER_OUT_NUM__FB = (byte)0xfB;	//设置屏幕操作超时时间
 

	public static final byte  PERSONALIZE_ADD_DICTIONARY______E0 = (byte)0xE0;	// 私有指令，写入助记码表相关
	public static final byte  PERSONALIZE_MNEMONIC_ITSELF_____D6 = (byte)0xD6;	// 个人化助记码表(byte)
	public static final byte  PERSONALIZE_MNEMONIC_LEN_INFO_____D7 = (byte)0xD7;	// 个人化助记码表每个单词长度(byte)
	public static final byte  PERSONALIZE_MNEMONIC_OFFSET_INFO_____D8 = (byte)0xD8;	// 个人化助记码表偏移(short)
	public static final byte  PERSONALIZE_MNEMONIC_ORIGINAL_INDEX_MAP_____D9 = (byte)0xD9;	// 个人化助记码表原始索引(short)
	public static final byte  PERSONALIZE_MNEMONIC_RESERVE_ORIGINAL_MAP_____DA = (byte)0xDA;	// 


	// 多账户支持（涉及多账户选择，删除，增加）
	public static final byte SELECT____________A4 = (byte)0xa4;	// 选择
	public static final byte DELETE____________EE = (byte)0xee;	// 删除
	public static final byte ADD_______________E0 = (byte)0xe0;	// 添加


	//指纹管理
	// 指纹录入、删除、验证、列举
	public static final byte FINGERPRINT_______b4 = (byte)0xB4;	// 指纹相关
	// 00 b4 00 01 增加
	// 00 b4 00 02 (指纹索引)删除
	// 00 b4 00 03 列举
	// 00 b4 00 00 (指纹索引)验证
	//	
	//指示灯控制
	public static final byte CONTROL_LED______B5 = (byte)0xB5;

	//pin管理
	//PIN
	public static final byte WRITE_PIN_________2B = (byte)0x2B;	// 写PIN
	public static final byte VERIFY_PIN________20 = (byte)0x20;	// 验证PIN
	public static final byte CHANGE_PIN________24 = (byte)0x24;	// 修改PIN
	public static final byte UNBLOCK_PIN_______28 = (byte)0x28;	// 解锁PIN
	public static final byte SHOW_NineGrids_______29 = (byte)0x29;	// 弹出九宫格
	
	public static final byte GET_MASTER_KEY_STATE____32 = (byte)0x32;		// 获取主密钥是否已经生成
	public static final byte GET_DICTIONARY_STATE_____34 = (byte)0x34;	// 读取个人化字典个人化状态和当前默认字典的信息
	public static final byte PERSONALIZE_STATE_INS_____ED = (byte)0xED;	// 关于个人化状态
	
	public static final byte FCI_TAG_6F = (byte)0x6F;	// FCI
	public static final byte FCI_VERSION_TAG_84 = (byte)0x84;	// FCI
	public static final byte FCI_TIMER_OUT_TAG_85 = (byte)0x85;	// FCI
	public static final byte FCI_BTC_UNIT_TAG_86 = (byte)0x86;	// FCI
	public static final byte FCI_BITCOIN_TAG_87 = (byte)0x86;	// FCI
	

	
	public static final  byte MASTER_KEY_TYPE_TAG = 0x01;
	/*
	密钥类型 01
      值  01 ?C rsa
          02 ?C sm2
          03 - ecc
	*/
	public static final  byte RSA_KEY_TYPE = 0x01;
	public static final  byte SM2_KEY_TYPE = 0x02;
	public static final  byte ECC_KEY_TYPE = 0x03;
	
	
	
	//密钥长度 02
	public static final  byte MASTER_KEY_SIZE_TAG = 0x02;
	
	
	//助记码强度 03
	public static final  byte ENTROPY_SIZE_TAG = 0x03;
	
	//初始盐值 04 
	public static final  byte PASS_PHRASE_TAG = 0x04;
	
	//label 05
	public static final  byte LABLE_TAG = 0x05;
	
	
	// 密码 06
	public static final  byte PIN_TAG = 0x06;
	
	//助记码 07
	public static final  byte MNEMONICS_TAG = 0x07;
	
	
	// bip32Path 08
	public static final  byte BIP32PATH_TAG = 0x08;
	
	
	// 原文    09
	public static final  byte TRANSCATION_RAW_TEXT_TAG = 0x09;


	// 助记码表 状态tag
	public static final  byte MNEMONIC_STATE_TAG = 0x0A;
	// 默认助记码表 状态tag
	public static final  byte DEFAULT_MNEMONIC__TAG = 0x0B;

     //  外部指定的主公钥  非内部生成（扩展xpub格式）
    public static final  byte OUTSIDE_MASTER_PUBKEY_TAG = 0x0C;
	
    public static final  byte TRANSATION_DATA_TAG = 0x0D;
     
    public static final  byte AMOUNTS_DATA__TAG = 0x0E; 
     
    public static final  byte PATHS_DATA__TAG = 0x0F; 
     
    // 找零地址的TLV
    public static final  byte CHANGE_ADDR_IDX_TAG = 0x10;
     


    
  //8字节16进制小端加法（最低字节在最左端）
  //c = a + b,溢出返回1，正常返回0
  static byte Amount_Add_LE(byte[] a, short aOff, byte[] b, short bOff, byte[] c, short cOff)
  {
  	byte i;
  	short wTmp;
  	
  	for(i=0,wTmp=0; i<8; ++i)
  	{
  		wTmp = (short)( (a[aOff + i] & 0xFF) + (b[bOff + i] & 0xFF) + (wTmp / 256));
  		c[cOff+i] = (byte)(wTmp % 256);
  	}
  	
  	if(wTmp >= 256)
  	{
  		return 1;
  	}
  	
  	return 0;
  }

  //8字节16进制小端减法（最低字节在最左端）
  //c = a - b, 不够减返回1，正常返回0
  static  byte Amount_Sub_LE(byte[] a, short aOff, byte[] b, short bOff, byte[] c, short cOff)
  {
  	byte i, byCarry;
  	short wA, wB;
  	
  	for(i=0, byCarry=0; i<8; ++i)
  	{
  		wA = (short)(a[aOff+i] & 0xFF);
  		wB = (short)(b[bOff+i] & 0xFF);
  		
  		if(wA >= (wB + byCarry))
  		{
  			c[cOff+i] = (byte)(wA-wB-byCarry);
  			byCarry = 0;
  		}
  		else
  		{
  			c[cOff+i] = (byte)(wA+256-wB-byCarry);
  			byCarry = 1;
  		}
  	}

  	return byCarry;
  }

	// 大数加法
	static public void big_sub( byte sum[], short offset_sum, byte x[], short offset_x, byte remain[], short offset_r, short len )
	{
		short i;
		short carry = 0;
		short bsum, bx;
		
		offset_sum += len;
		offset_x += len;
		offset_r += len;
		offset_sum--;
		offset_x--;
		offset_r--;
		
		for( i = (short)( len - 1 ); i >= 0; i-- )
		{
			bsum = (short)( (byte)sum[ offset_sum ] & 0xff );
			bx = (short)( (byte)x[ offset_x ] & 0xff );
			carry = (short)( bsum - bx - carry );
			
			remain[ offset_r ] = (byte)( carry & 0xff );
			
			if( carry < 0 )
			{
				carry = 1;
			}
			else
			{
				carry = 0;
			}
			offset_sum--;
			offset_x--;
			offset_r--;
		}
	}
	
	public static final byte SCRATCH_SIZE = 21;
      // 转为8字节的二进制格式的金额为可显示的ascii格式
	 static byte convert_amount_to_displayable_BE(
			byte[] amount, short aOff, 
			byte[] outBuf, short outOff, 
			byte[] scratch, short tmpOff, byte loop2)
	{
	   
	   
	    byte i;

	    byte j;
	    byte nscratch = SCRATCH_SIZE;
	    byte smin = (byte)(nscratch - 2);
	 

	    for (i = 0; i < SCRATCH_SIZE; i++)
	    {
	        scratch[tmpOff + i] = 0;
	    }
	    for (i = 0; i < 8; i++)
	    {
	        for (j = 0; j < 8; j++)
	        {
	            byte k;
	            short shifted_in = (((amount[aOff + i] & 0xff) & ((1 << (7 - j)))) != 0) ? (short)1 : (short)0;
	            for (k = smin; k < nscratch; k++)
	            {
	                scratch[tmpOff + k] += ((scratch[tmpOff + k] >= 5) ? 3 : 0);
	            }
	            if (scratch[tmpOff + smin] >= 8)
	            {
	                smin -= 1;
	            }
	            for (k = smin; k < nscratch - 1; k++)
	            {
	                scratch[tmpOff + k] = (byte)(((scratch[tmpOff + k] << 1) & 0xF) | ((scratch[tmpOff + k + 1] >= 8) ? 1 : 0));
	            }
	            scratch[tmpOff + nscratch - 1] = (byte)(((scratch[tmpOff + nscratch - 1] << 1) & 0x0F) | (shifted_in == 1 ? 1 : 0));
	        }
	    }

	    byte comma = 0;
	    byte targetOffset = 0;
	    byte workOffset;
	    byte offset = 0;
	    byte nonZero = 0;
	    byte LOOP1 = (byte) (SCRATCH_SIZE - loop2);
	    for (i = 0; i < LOOP1; i++)
	    {
	        if ((nonZero == 0) && (scratch[tmpOff + offset] == 0))
	        {
	            offset++;
	        }
	        else
	        {
	            nonZero = 1;
	            outBuf[outOff + targetOffset++] = (byte)(scratch[tmpOff + offset++] + '0');
	        }
	    }

	    if (targetOffset == 0)
	    {
	        outBuf[outOff + targetOffset++] = '0';
	    }
	   
	    workOffset = offset;

	    for (i = 0; i < loop2; i++) 
	    {
	        byte allZero = 1;
	        for (j = i; j < loop2; j++)
	        {
	            if (scratch[tmpOff + workOffset + j] != 0)
	            {
	                allZero = 0;
	                break;
	            }
	        }
	        if (allZero != 0)
	        {
	            break;
	        }
	        if (comma == 0)
	        {
	            outBuf[outOff + targetOffset++] = '.';
	            comma = 1;
	        }
	        outBuf[outOff + targetOffset++] = (byte)(scratch[tmpOff + offset++] + '0');
	    }

	    return targetOffset;
	}
    
	public static byte convert_hex_amount_to_displayable_LE(byte[] amount, short aOff,
    		byte[] outBuf, short outOff, 
    		byte[] scratch, short tmpOff , byte loop2)
    {
    	reserve_buf(amount,aOff,(short)8);
    	return convert_amount_to_displayable_BE(amount, aOff, outBuf, outOff, scratch, tmpOff,loop2);
    }

	    	
	    	
	  // 反转数组	
    static void reserve_buf(byte[] buf, short buf_offs, short len){
    	
    	 short mid = (short)(len/2);
    	 len -= 1;
    	 byte temp;
    	 for(short i = 0 ; i< mid; ++i){
    		 temp = buf[i + buf_offs];
    		 buf[i + buf_offs] = buf[len - i + buf_offs];
    		 buf[len - i + buf_offs] = temp;
    	 }
    }

		//【签名 格式】
		//R,S是原始结果，
		//R补齐
		//if(R[0]>0x80)  R_pad=|0x02|keybytelen+1|0x00|R|
		//else           R_pad=|0x02|keybytelen|R|  
		//
		//S补齐
		//if(S[0]>0x80)  S_pad= |0x02|keybytelen+1|0x00|R|
		//else            S_pad= |0x02|keybytelen|R|  
		//
		//(R,S)=|0x30|totalLen（R_pad+S_pad）|R_pad|S_pad|
		//--------------------------
		//参考实现：TMPRAM是最终结果，sigR,sigS是原始结果
		//        u8 offset=0;
		//        TMPRAM[offset++]=0x30;
		//        TMPRAM[offset++]=(keybyte<<1)+4;
		//        TMPRAM[offset++]=0x02;
		//        if(*sigR>=0x80){
		//            TMPRAM[offset++]=keybyte+1;
		//            TMPRAM[offset++]=0;
		//            TMPRAM[1]++;
		//        }else{
		//            TMPRAM[offset++]=keybyte;
		//        }
		//        memcpy(&TMPRAM[offset],sigR,keybyte);
		//        offset+=keybyte;
		//
		//        TMPRAM[offset++]=0x02;
		//        if(*sigS>=0x80){
		//            TMPRAM[offset++]=keybyte+1;
		//            TMPRAM[offset++]=0;
		//            TMPRAM[1]++;
		//        }else{
		//            TMPRAM[offset++]=keybyte;
		//        }
		//        memcpy(&TMPRAM[offset],sigS,keybyte);
		//-----------------------------------------
		//签名时如hash长度小于密钥长度，hash前面补0
		//如hash长度大于密钥长度，hash后面截断
        
        // 将数组进行 DER 格式的编码
		public static short asn1c_der_encode( byte[]r ,short r_offset, byte[]s ,short s_offset , short keylen, byte[] x ,short x_offset)
		{
			short x_offset_begin =  x_offset;
			x[ x_offset++ ] = 0x30;
			x[ x_offset++ ] = (byte)(( keylen << 1 ) + 4);
			x[ x_offset++ ] = 0x02;
			if( (r[r_offset]&0xff) >= 0x80 )
			{
				x[ x_offset++ ] = (byte)(keylen + 1);
				x[ x_offset++ ] = 0;
				x[ x_offset_begin + 1 ]++;
			}
			else
			{
				x[ x_offset++ ] = (byte)keylen;
			}
			//memcpy( x + offset, r, keylen );
			Util.arrayCopyNonAtomic(r,r_offset,x,x_offset,keylen);
			
			x_offset += keylen;

			x[ x_offset++ ] = 0x02;
			if( (s[s_offset] &0xff)>= 0x80 )
			{
				x[ x_offset++ ] = (byte)(keylen + 1);
				x[ x_offset++ ] = 0;
				x[ x_offset_begin + 1 ]++;
			}else
			{
				x[ x_offset++ ] = (byte)keylen;
			}
			Util.arrayCopyNonAtomic(s,s_offset,x,x_offset,keylen);
			
		  
			return (short)(x_offset - x_offset_begin + keylen);
		}
		
		// 得到将数组进行 DER 格式的编码的长度
		public static byte get_ECC_R_S_der_encode_len(byte[] ecc_sign_data,short offs){
			
			byte temp = 70;
			
			if( (ecc_sign_data[offs]&0xff) >= 0x80 ){
				temp += 1;
			}
			if( (ecc_sign_data[(short)(offs + 32)] &0xff)>= 0x80 ){
				temp += 1;
			}
			return temp;
		}

    // 将DER 格式的数据解码
		static short  asn1c_der_decode(byte[] cr,short cr_offset,short key_len,  byte[]result,short result_offset)
	{

		int roffset;
		int soffset;
		
		if( 0x30 != cr[ 0 + cr_offset]
			||
			0x02 != cr[ 2 + cr_offset ]
			)
		{
			return 0;
		}
		
		if( cr[ 3 + cr_offset] > key_len )
		{
			roffset = 5;
		}
		else
		{
			roffset = 4;
		}
		
		soffset = roffset + key_len + 1;
		
		if( 0x02 != cr[ (short)( soffset - 1  + cr_offset) ] )
		{
			
			return 0;
		}
		if( cr[ (short)( soffset + cr_offset) ] > key_len)
		{
			if( key_len + 1 != cr[  (short)(  soffset  + cr_offset) ]
				||
				0 != cr[ (short)(  soffset + 1 + cr_offset )]
			)
			{
				
				return 0;
			}
			soffset += 2;
		}	
		else
		{
			soffset++;
		}
		
		//restore signature data (R + S)

		
		Util.arrayCopyNonAtomic(cr, (short)(cr_offset + roffset), result,result_offset,key_len);
		
		Util.arrayCopyNonAtomic(cr, (short)(cr_offset + soffset), result,(short)(result_offset+ key_len),key_len);

		
		return (short)(key_len * 2);

		
	}

		static  final byte top[] = { (byte)0x7F, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x5D, (byte)0x57, (byte)0x6E, (byte)0x73, (byte)0x57, (byte)0xA4, (byte)0x50, (byte)0x1D, (byte)0xDF, (byte)0xE9, (byte)0x2F, (byte)0x46, (byte)0x68, (byte)0x1B, (byte)0x20, (byte)0xA0 };
		static  final byte sum[] = { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFE, (byte)0xBA, (byte)0xAE, (byte)0xDC, (byte)0xE6, (byte)0xAF, (byte)0x48, (byte)0xA0, (byte)0x3B, (byte)0xBF, (byte)0xD2, (byte)0x5E, (byte)0x8C, (byte)0xD0, (byte)0x36, (byte)0x41, (byte)0x41 };
		
		// 判断num1是否  大于 num2
		static public boolean greater( byte num1[], short offset1, byte num2[], short offset2, short len )
		{
			short i;
			short t1, t2;
			short o1, o2;
			o1 = offset1;
			o2 = offset2;
			for( i = 0; i < len; i++ )
			{
				t1 = (short)((byte)num1[ o1 ] & 0xff );
				t2 = (short)((byte)num2[ o2 ] & 0xff );
				
				if( t1 > t2 )
				{
					return true;
				}
				else if( t1 < t2 )
				{
					return false;
				}
				o1++;
				o2++;
			}
			
			return false;
		}
		
		
// 左移，左移的时候，是保留低位的，所以左移之后很可能会加大数据的长度
boolean  script_shl( short shift_times,short orilen, byte[]  udata, byte[] t, short t_len)
{
	short i;
	int times;
	byte c;
	int offset;

	
	Util.arrayFillNonAtomic(t,(short)0,t_len,(byte)0x00);
	Util.arrayCopyNonAtomic(udata,(short)0,t,(short)(t_len - orilen),orilen);
	

	// 从左往右，一个字节一个字节的移位
	for( i = (short)(t_len - orilen); i <t_len; i++ )
	{
		// c是要移位的字节
		c = t[ i ];
		t[ i ] = 0;		// 移完了，此位就要置成0，否则就套圈了

		// 每个字节要都考虑超过8的情况，所以还要一个循环
		// offset是目的地址
		offset = ( ( shift_times + 7 ) / 8 );
		// times是剩下的位数
		times = ( shift_times % 8 );

		if( times != 0)
		{
			// 不是整数位
			t[ (short)(i - offset) ] |= ( ( c >> ( 8 - times ) ) & 0xff );
			t[ (short)(i - offset + 1) ] = (byte)( ( c << times ) & 0xff );
		}
		else
		{
			// 整数位
			t[ (short)(i - offset) ] = c;
		}

	}

	for( i = 0; i < ( int )( t_len - orilen ); i++ )
	{
		if( t[ i ] != 0)
		{
			break;
		}
	}
	Util.arrayFillNonAtomic(udata,(short)0,orilen,(byte)0x00);
	
	orilen = (short)( t_len - i );
	
	Util.arrayCopyNonAtomic(udata,(short)0,t, i, orilen);


	return true;
}
	
}
