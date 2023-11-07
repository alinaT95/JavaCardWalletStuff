package com.ftsafe.CoinPassBIO;
import javacard.framework.JCSystem;
import javacard.framework.Util;
// https://github.com/bitcoincashorg/bitcoincash.org/blob/master/spec/cashaddr.md
/*
Reference c implementation source code

#include <stdlib.h>
#include <stdint.h>
#include <string.h>

#include "cash_addr.h"

#define MAX_CASHADDR_SIZE 129
#define MAX_BASE32_SIZE 104
#define MAX_DATA_SIZE 65
#define MAX_HRP_SIZE 20
#define CHECKSUM_SIZE 8

//uint64_t cashaddr_polymod_step(uint64_t pre) {
//    uint8_t b = pre >> 35;
//    return ((pre & 0x7FFFFFFFFULL) << 5) ^
//        (-((b >> 0) & 1) & 0x98f2bc8e61ULL) ^
//        (-((b >> 1) & 1) & 0x79b76d99e2ULL) ^
//        (-((b >> 2) & 1) & 0xf33e5fb3c4ULL) ^
//        (-((b >> 3) & 1) & 0xae2eabe2a8ULL) ^
//        (-((b >> 4) & 1) & 0x1e4f43e470ULL);
//}
uint64_t cashaddr_polymod_step(uint64_t pre) {
    uint8_t b = pre >> 35;
	pre = ((pre & 0x7FFFFFFFFULL) << 5);
	uint64_t temp = -((b >> 0) & 1);
	temp = temp & 0x98f2bc8e61ULL;
	pre ^= temp;
	pre ^= (-((b >> 1) & 1) & 0x79b76d99e2ULL);
	pre ^= (-((b >> 2) & 1) & 0xf33e5fb3c4ULL);
	pre ^= (-((b >> 3) & 1) & 0xae2eabe2a8ULL);
	pre ^= (-((b >> 4) & 1) & 0x1e4f43e470ULL);
	return pre;
       

}
static const char* charset = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

static const int8_t charset_rev[128] = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    15, -1, 10, 17, 21, 20, 26, 30,  7,  5, -1, -1, -1, -1, -1, -1,
    -1, 29, -1, 24, 13, 25,  9,  8, 23, -1, 18, 22, 31, 27, 19, -1,
     1,  0,  3, 16, 11, 28, 12, 14,  6,  4,  2, -1, -1, -1, -1, -1,
    -1, 29, -1, 24, 13, 25,  9,  8, 23, -1, 18, 22, 31, 27, 19, -1,
     1,  0,  3, 16, 11, 28, 12, 14,  6,  4,  2, -1, -1, -1, -1, -1
};

int cash_encode(char *output, const char *hrp, const uint8_t *data, size_t data_len) {
    uint64_t chk = 1;
    size_t i = 0;
    while (hrp[i] != 0) {
        int ch = hrp[i];
        if (ch < 33 || ch > 126) {
            return 0;
        }
        *(output++) = ch;
        chk = cashaddr_polymod_step(chk) ^ (ch & 0x1f);
        ++i;
    }
    if (i + 1 + data_len + CHECKSUM_SIZE > MAX_CASHADDR_SIZE) {
        return 0;
    }
    chk = cashaddr_polymod_step(chk);
    *(output++) = ':';
    for (i = 0; i < data_len; ++i) {
        if (*data >> 5) return 0;
        chk = cashaddr_polymod_step(chk) ^ (*data);
        *(output++) = charset[*(data++)];
    }
    for (i = 0; i < CHECKSUM_SIZE; ++i) {
        chk = cashaddr_polymod_step(chk);
    }
    chk ^= 1;
    for (i = 0; i < CHECKSUM_SIZE; ++i) {
        *(output++) = charset[(chk >> ((CHECKSUM_SIZE - 1 - i) * 5)) & 0x1f];
    }
    *output = 0;
    return 1;
}

int cash_decode(char* hrp, uint8_t *data, size_t *data_len, const char *input) {
    uint64_t chk = 1;
    size_t i;
    size_t input_len = strlen(input);
    size_t hrp_len;
    int have_lower = 0, have_upper = 0;
    if (input_len < CHECKSUM_SIZE || input_len > MAX_CASHADDR_SIZE) {
        return 0;
    }
    *data_len = 0;
    while (*data_len < input_len && input[(input_len - 1) - *data_len] != ':') {
        ++(*data_len);
    }
    hrp_len = input_len - (1 + *data_len);
    if (hrp_len < 1 || hrp_len > MAX_HRP_SIZE ||
        *data_len < CHECKSUM_SIZE || *data_len > CHECKSUM_SIZE + MAX_BASE32_SIZE) {
        return 0;
    }
    // subtract checksum
    *(data_len) -= CHECKSUM_SIZE;
    for (i = 0; i < hrp_len; ++i) {
        int ch = input[i];
        if (ch < 33 || ch > 126) {
            return 0;
        }
        if (ch >= 'a' && ch <= 'z') {
            have_lower = 1;
        } else if (ch >= 'A' && ch <= 'Z') {
            have_upper = 1;
            ch = (ch - 'A') + 'a';
        }
        hrp[i] = ch;
		auto temp = cashaddr_polymod_step(chk);
        chk = temp ^ (ch & 0x1f);
    }
    hrp[i] = 0;
    chk = cashaddr_polymod_step(chk);
    ++i;
    while (i < input_len) {
        int v = (input[i] & 0x80) ? -1 : charset_rev[(int)input[i]];
        if (input[i] >= 'a' && input[i] <= 'z') have_lower = 1;
        if (input[i] >= 'A' && input[i] <= 'Z') have_upper = 1;
        if (v == -1) {
            return 0;
        }
		chk = cashaddr_polymod_step(chk) ;
        chk = chk ^ v;
        if (i + 6 < input_len) {
            data[i - (1 + hrp_len)] = v;
        }
        ++i;
    }
    if (have_lower && have_upper) {
        return 0;
    }
    return chk == 1;
}

static int convert_bits(uint8_t* out, size_t* outlen, int outbits, const uint8_t* in, size_t inlen, int inbits, int pad) {
    uint32_t val = 0;
    int bits = 0;
    uint32_t maxv = (((uint32_t)1) << outbits) - 1;
    while (inlen--) {
        val = (val << inbits) | *(in++);
        bits += inbits;
        while (bits >= outbits) {
            bits -= outbits;
            out[(*outlen)++] = (val >> bits) & maxv;
        }
    }
    if (pad) {
        if (bits) {
            out[(*outlen)++] = (val << (outbits - bits)) & maxv;
        }
    } else if (((val << (outbits - bits)) & maxv) || bits >= inbits) {
        return 0;
    }
    return 1;
}

int cash_addr_encode(char *output, const char *hrp, const uint8_t *data, size_t data_len) {
    uint8_t base32[MAX_BASE32_SIZE];
    size_t base32len = 0;
    if (data_len < 2 || data_len > MAX_DATA_SIZE) return 0;
    convert_bits(base32, &base32len, 5, data, data_len, 8, 1);
    return cash_encode(output, hrp, base32, base32len);
}

int cash_addr_decode(uint8_t* witdata, size_t* witdata_len, const char* hrp, const char* addr) {
    uint8_t data[MAX_BASE32_SIZE];
    char hrp_actual[MAX_HRP_SIZE+1];
    size_t data_len;
    if (!cash_decode(hrp_actual, data, &data_len, addr)) 
        return 0;

    if (data_len == 0 || data_len > MAX_BASE32_SIZE) 
        return 0;

    if (strncmp(hrp, hrp_actual, MAX_HRP_SIZE + 1) != 0)
        return 0;

    *witdata_len = 0;

    if (!convert_bits(witdata, witdata_len, 8, data, data_len, 5, 0))
         return 0;

    if (*witdata_len < 2 || *witdata_len > MAX_DATA_SIZE) return 0;
        return 1;
}

 */


public class CashAddres 
{
	

	
	public static final short  MAX_CASHADDR_SIZE =  129;
	public static final short MAX_BASE32_SIZE =104;
	public static final short MAX_DATA_SIZE =65;
	public static final short MAX_HRP_SIZE =20;
	public static final short CHECKSUM_SIZE =8;
	public static byte[] hrp = { 'b','i','t','c','o','i','n','c','a','s','h'};
	static byte[] _1 = {0x00, 0x00 , 0x00, 0x00 ,0x00, 0x00, 0x00, 0x01 };
	static byte[] _07ffffffff = { 0x00, 0x00 , 0x00, 0x07 ,(byte)0xff, (byte)0xff, (byte)0xff ,(byte)0xff };
	static byte[] _98f2bc8e61 ={0x00, 0x00 , 0x00, (byte)0x98, (byte)0xf2, (byte)0xbc, (byte)0x8e, 0x61};
	static byte[] _79b76d99e2 = {0x00, 0x00 , 0x00, 0x79,  (byte)0xb7, 0x6d,  (byte)0x99,  (byte)0xe2 };
	
	static byte[] _f33e5fb3c4  = {0x00, 0x00 , 0x00,  (byte)0xf3, 0x3e, 0x5f, (byte)0xb3, (byte)0xc4 };
	static byte[] _ae2eabe2a8 ={ 0x00, 0x00 , 0x00,  (byte)0xae, 0x2e,  (byte)0xab, (byte) 0xe2, (byte)0xa8};
	static byte[] _1e4f43e470 = { 0x00, 0x00 , 0x00, 0x1e, 0x4f, 0x43, (byte)0xe4, 0x70 };
	static byte[] _1f = {0x00, 0x00 , 0x00, 0x00 ,0x00, 0x00, 0x00, 0x1f };
	public CashAddres(){
		 temp_buf_for_chk = JCSystem.makeTransientByteArray((short)8, JCSystem.CLEAR_ON_DESELECT);
		 temp_buf_in_RAM = JCSystem.makeTransientByteArray((short)8, JCSystem.CLEAR_ON_DESELECT);
		
	}
	 public static final short FAIL_TO_PROCESS = -1;

	 byte[] temp_buf_for_chk;
	 byte[] temp_buf_in_RAM;
	
	 // shift left
	static void shl( byte[] in_out_buf, short len, byte[]  help_buf, int shift_times )
	{
		short i;
		int offset;
		int times;
		short c1, c2;
		if(in_out_buf == null || help_buf == null){
			return ;
		}
		if(shift_times < 0){
			return ;
		}
		if(len*8  < shift_times){
			Util.arrayFillNonAtomic(in_out_buf,(short)0,(short) len,(byte)0x00);
			return;
			
		}
		Util.arrayFillNonAtomic(help_buf,(short)0,(short) len,(byte)0x00);
		

		// Byte by byte shift from left to right
		for( i = 0; i < len; i++ )
		{
			// c是要移位的字节
			c1 = (short)(in_out_buf[ i ] & 0xff);
			if( i < len - 1 )
			{
				c2 = (short)(in_out_buf[ i + 1 ] & 0xff );
			}
			else
			{
				c2 = 0;
			}

			// 每个字节要都考虑超过8的情况，所以还要一个循环
			// offset是目的地址
			offset = ( ( shift_times + 7 ) / 8 );
			// times是剩下的位数
			times = ( shift_times % 8 );

			if( times != 0)
			{
				// 不是整数位
				if(i - offset + 1 > 0){
					help_buf[ (short)(i - offset + 1) ] = (byte)( ( c1 << times ) & 0xff );
					help_buf[ (short)(i - offset + 1 )] |= (byte)( ( c2 >> ( 8 - times ) ) & 0xff );
				}
			}
			else
			{
				// 整数位
				if(i - offset > 0){
					help_buf[(short)( i - offset) ] = (byte)c1;
				}
			}

		}
		
	}

	// 左移，左移的时候，是保留低位的，所以左移之后很可能会加大数据的长度
	// shift right
	static void shr( byte[] in_buf, int len, byte[] out_buf, int shift_times )
	{
		short i;
		short offset;
		
		short times;
		short c1;

		if(in_buf == null || out_buf == null){
			return ;
		}
		
		if(shift_times < 0){
			return ;
		}
		
		if(len*8  < shift_times){
			Util.arrayFillNonAtomic(in_buf,(short)0,(short) len,(byte)0x00);
			return;
		}
		
		Util.arrayFillNonAtomic(out_buf,(short)0,(short) len,(byte)0x00);
		

		// Byte by byte shift from left to right
		for( i = 0; i < len; i++ )
		{
			// 每个字节要都考虑超过8的情况，所以还要一个循环
			// offset是目的地址
			offset = (short)(i + ( ( shift_times + 7 ) / 8 ) - 1);
			// times是剩下的位数
			times = (short)( shift_times % 8 );

			// c is  the byte to shift
			c1 = (short) (in_buf[ i ] & 0xff);

			if( times  != 0)
			{
				// 不是整数位
				if( offset < len )
				{
					out_buf[ offset ] |= ( ( c1 >> times ) & 0xff );
				}
				if( offset + 1 < len )
				{
					out_buf[ (short)(offset + 1 )] = (byte)( ( c1 << ( 8 - times ) ) & 0xff );
				}
			}
			else
			{
				// 整数位
				if( offset + 1 < len )
				{
					out_buf[ (short)(offset + 1 )] = (byte) c1;
				}
			}
		}
		
	}
	// And operation
	static boolean operate_AND(
			byte[] left, short left_off, 
			byte[] right, short right_off, 
			byte[] out, short out_off, 
			short len)
	{
		if(left == null || right == null || out == null){
			return false;
		}
		if( left_off + len  > left.length){
			return false;
		}
		if( right_off + len  > right.length){
			return false;
		}
		if( out_off + len  > out.length){
			return false;
		}
		short temp;
		for(short i = 0; i < len; ++i)
		{
			temp = (short)(left[ left_off + i ] & 0xff);
			temp &= (short)(right[ right_off + i ] & 0xff);
			out[ out_off + i] = (byte) (temp);
		}
		
		return false;
	}
	// not operation
	static boolean operate_NOT(
			byte[] left, short left_off, 
			byte[] right, short right_off, 
			byte[] out, short out_off, 
			short len)
	{
		if(left == null || right == null || out == null){
			return false;
		}
		if( left_off + len  > left.length){
			return false;
		}
		if( right_off + len  > right.length){
			return false;
		}
		if( out_off + len  > out.length){
			return false;
		}
		short temp;
		for(short i = 0; i < len; ++i)
		{
			temp = (short)(left[ left_off + i ] & 0xff);
			temp ^= (short)(right[ right_off + i ] & 0xff);
			out[ out_off + i] = (byte) (temp);
		}
		
		return false;
	}
	

/**
 * This function will compute what 8 5-bit values to XOR into the last 8 input
 * values, in order to make the checksum 0. These 8 values are packed together
 * in a single 40-bit integer. The higher bits correspond to earlier values.
 */

	
	static void set_int_to_buf(byte[] help_buf, short len, int value){
		if(value >= 0)
			Util.arrayFillNonAtomic(help_buf, (short)0, len, (byte)0);
		else{
			Util.arrayFillNonAtomic(help_buf, (short)0, len, (byte)0xff);
		}
   		for(short i = 0 ; i < 4; ++i){
   			help_buf[len -i  - 1] = (byte)(value  >>> i*8 );	
	    }
		
   		
	}




static void  cashaddr_polymod_step(byte[] pre,byte[] help_buf) 


{
	
    
     shr(pre, (short) 8, help_buf, 35); 
     short b = (short)(help_buf[7] &0xff);
     
     operate_AND(pre, (short)0, _07ffffffff, (short)0, help_buf, (short)0, (short)8);
     shl(help_buf, (short)8, pre, 5);
   
     
     int value = -((b >> 0) & 1);
   
	 set_int_to_buf(help_buf, (short)8, value);
	 operate_AND(help_buf, (short)0, _98f2bc8e61, (short)0, help_buf, (short)0, (short)8);
	 
	 operate_NOT(pre, (short)0, help_buf, (short)0,pre, (short)0,(short)8);
		 
     value = -((b >> 1) & 1);
	 set_int_to_buf(help_buf, (short)8, value);
	 operate_AND(help_buf, (short)0, _79b76d99e2, (short)0, help_buf, (short)0, (short)8);
	 operate_NOT(pre, (short)0, help_buf, (short)0,pre, (short)0,(short)8);
	 
     value = -((b >> 2) & 1);
	 set_int_to_buf(help_buf, (short)8, value);
	 operate_AND(help_buf, (short)0, _f33e5fb3c4, (short)0, help_buf, (short)0, (short)8);
	 operate_NOT(pre, (short)0, help_buf, (short)0,pre, (short)0,(short)8);
	 
     value = -((b >> 3) & 1);
	 set_int_to_buf(help_buf, (short)8, value);
	 operate_AND(help_buf, (short)0, _ae2eabe2a8, (short)0, help_buf, (short)0, (short)8);
	 operate_NOT(pre, (short)0, help_buf, (short)0,pre, (short)0,(short)8);    
	 
     value = -((b >> 4) & 1);
	 set_int_to_buf(help_buf, (short)8, value);
	 operate_AND(help_buf, (short)0, _1e4f43e470, (short)0, help_buf, (short)0, (short)8);
	 operate_NOT(pre, (short)0, help_buf, (short)0,pre, (short)0,(short)8);  
   
	
}

static short charset_rev[] = {
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 15, -1, 10, 17, 21, 20, 26, 30, 7,
	    5,  -1, -1, -1, -1, -1, -1, -1, 29, -1, 24, 13, 25, 9,  8,  23, -1, 18, 22,
	    31, 27, 19, -1, 1,  0,  3,  16, 11, 28, 12, 14, 6,  4,  2,  -1, -1, -1, -1,
	    -1, -1, 29, -1, 24, 13, 25, 9,  8,  23, -1, 18, 22, 31, 27, 19, -1, 1,  0,
	    3,  16, 11, 28, 12, 14, 6,  4,  2,  -1, -1, -1, -1, -1};
//static const char *charset = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";
 static byte[] charset = {(byte) 0x71,(byte) 0x70,(byte) 0x7A,(byte) 0x72,(byte) 0x79,(byte) 0x39,(byte) 0x78,
	(byte) 0x38,(byte) 0x67,(byte) 0x66,(byte) 0x32,(byte) 0x74,(byte) 0x76,(byte) 0x64,
	(byte) 0x77,(byte) 0x30,(byte) 0x73,(byte) 0x33,(byte) 0x6A,(byte) 0x6E,(byte) 0x35,
	(byte) 0x34,(byte) 0x6B,(byte) 0x68,(byte) 0x63,(byte) 0x65,(byte) 0x36,(byte) 0x6D,
	(byte) 0x75,(byte) 0x61,(byte) 0x37,(byte) 0x6C,
	0x00};
 static short convert_bits(byte[] output,short output_offs,int outbits,
		  byte[] data, short data_offs, short data_len, 
		  int inbits,  boolean pad)    {
       int acc = 0;
       int bits = 0;
       int maxv = (1 << outbits) - 1;
      
   	short outlen = 0;
       for(short i = 0 ; i < data_len; ++i)  {
           short b = (short)(data[i + data_offs] & 0xff);

           if (b < 0) {
               return -1;
           }
           else if ((b >> inbits) > 0) {
           	return -1;
           }

           acc = (acc << inbits) | b;
           bits += inbits;
           while (bits >= outbits)  {
               bits -= outbits;
               output[output_offs+ outlen] = (byte)((acc >> bits) & maxv);
               outlen++;
              
           }
       }

       if(pad && (bits > 0))    {
       	output[output_offs+ outlen] =(byte)((acc << (outbits - bits)) & maxv);
       	outlen++;
          
       }
       else if (bits >= inbits || (byte)(((acc << (outbits - bits)) & maxv)) != 0)    {
       	return -1;
       }
       
       return outlen;
   }

 public  short cash_encode(
		 byte[]output,short output_offs,
		 byte[] hrp, short hrp_offs,short hrp_len,
		 byte[] data,short data_offs,short data_len
		 ) 
 {
	   
	    short i = 0;
	    int ch;
	    byte[] chk = temp_buf_for_chk;
	    set_int_to_buf(chk, (short)8, 1);
	    short old_output_offs = output_offs;
	    for(; i < hrp_len;){
	    	ch = (short)(hrp[i + hrp_offs] & 0xff);
	    	if (ch < 33 || ch > 126) {
	            return FAIL_TO_PROCESS;
	    	}
	        output[output_offs++] = (byte)ch;
	        
	        // in C : chk = cashaddr_polymod_step(chk) ^ (ch & 0x1f);
	        cashaddr_polymod_step(chk,temp_buf_in_RAM);
	        set_int_to_buf(temp_buf_in_RAM, (short)8, (ch & 0x1f));
	        operate_NOT(chk, (short)0, temp_buf_in_RAM, (short)0, chk, (short)0, (short)8);
	        
	        ++i;
	    }
        if (i + 1 + data_len + CHECKSUM_SIZE > MAX_CASHADDR_SIZE) {
            return 0;
        }
	    
	    cashaddr_polymod_step(chk,temp_buf_in_RAM);

	    output[output_offs++] = ':';
	    
	    for (i = 0; i < data_len; ++i) {
	    	ch = (data[data_offs] & 0xff);
	        if (ch >> 5 != 0)
	            return FAIL_TO_PROCESS;
	        
	        // in C :： chk = cashaddr_polymod_step(chk) ^ (*data);
	        cashaddr_polymod_step(chk,temp_buf_in_RAM);
	        set_int_to_buf(temp_buf_in_RAM, (short)8, ch);
	        operate_NOT(chk, (short)0, temp_buf_in_RAM, (short)0, chk, (short)0, (short)8);
	        
	        output[output_offs++] = charset[data[data_offs++]];  
	    }
	    for (i = 0; i < CHECKSUM_SIZE; ++i) {
	    	 cashaddr_polymod_step(chk,temp_buf_in_RAM);
	    }
	    operate_NOT(chk, (short)0, _1, (short)0, chk, (short)0,(short) 8);//chk ^= 1;
	    
	    short index ;
	    for (i = 0; i < CHECKSUM_SIZE; ++i) 
	    {
	    	 // in C :  *(output++) = charset[(chk >> ((CHECKSUM_SIZE - 1 - i) * 5)) & 0x1f];
	    	shr(chk, (short)8, temp_buf_in_RAM, (short)((CHECKSUM_SIZE - 1 - i) * 5));
	    	
	    	operate_AND(temp_buf_in_RAM, (short)0, _1f, (short)0, temp_buf_in_RAM , (short)0, (short)8);
	    	index = (short)(temp_buf_in_RAM[6] << 8);
	    	index |= (temp_buf_in_RAM[7] & 0xff) ;
	    	output[output_offs++] = charset[ index ];
	    }
	    //output[output_offs] = 0;
	    
	    return (short)(output_offs - old_output_offs );
}
 	//base32 buf need  MAX_BASE32_SIZE  bytes
	public  short cash_addr_encode( 	    byte[]output,short output_offs, 
											byte[] hrp, short hrp_offs,short hrp_len,
											
											byte[] data, short data_offs, short data_len,
											byte[] base32, short base32_offs)
	{
	  short base32len = 0;
	  
	  if (data_len < 2 || data_len > MAX_DATA_SIZE) return 0;
	  
	 
	  base32len =  convert_bits(base32, (short)(base32_offs) , 
	  		(short)5, 
	  		data,data_offs, data_len,
	  		(short)8, true);
	  
	  if(-1 == base32len){
	  	return FAIL_TO_PROCESS;
	  }
	  
	  data_len =  cash_encode(output, output_offs,hrp, hrp_offs,hrp_len,
	  		base32, base32_offs,base32len);
	  return data_len;
	  
	}
	 
	 public  short cash_decode( byte[] hrp,short hrp_offs, 
				byte[] data, short data_offs,
				byte[] input, short input_offs, short input_len) 
	
	{
		 byte[] chk = temp_buf_for_chk;
		//int chk = 1;
		 set_int_to_buf(chk, (short)8, 1 );
		
		short i;
		int have_lower = 0, have_upper = 0;
	
		if (input_len < CHECKSUM_SIZE || input_len > MAX_CASHADDR_SIZE) {
			return FAIL_TO_PROCESS;
	    }
		short data_len = get_data_len_for_decode(input,input_offs,input_len);
		if(data_len == FAIL_TO_PROCESS)
			return FAIL_TO_PROCESS;
		short hrp_len  = (short) (input_len - (1 + data_len + CHECKSUM_SIZE));
	
	    // subtract checksum
		short ch;
		for (i = 0; i < hrp_len; ++i) 
		{
			ch = (short)(input[i + input_offs] & 0xff);
			if (ch < 33 || ch > 126) {
				return FAIL_TO_PROCESS;
			}
			if (ch >= 'a' && ch <= 'z' ) {
				have_lower = 1;
			} else if (ch >= 'A' && ch <= 'Z') {
				have_upper = 1;
				ch =  (short)( ch - 'A' + 'a' );
			}
			hrp[i + hrp_offs] = (byte) ch;
			
			//chk = cashaddr_polymod_step(chk) ^ (ch & 0x1f);
	        cashaddr_polymod_step(chk,temp_buf_in_RAM);
	        set_int_to_buf(temp_buf_in_RAM, (short)8, (ch & 0x1f));
	        operate_NOT(chk, (short)0, temp_buf_in_RAM, (short)0, chk, (short)0, (short)8);
	        
			
		}
		
		hrp[i + hrp_offs] = 0;
		cashaddr_polymod_step(chk,temp_buf_in_RAM);
	
		++i;
		while (i < input_len) {
			ch  = input[i + input_offs];
			short v = (ch & 0x80) != 0 ? -1 : charset_rev[ ch ];
			if (ch >= 'a' && ch <= 'z')
				have_lower = 1;
			if (ch >= 'A' && ch <= 'Z')
				have_upper = 1;
			if (v == -1) {
				return FAIL_TO_PROCESS;
			}
			
			//chk = bech32_polymod_step(chk) ^ v;
	        cashaddr_polymod_step(chk,temp_buf_in_RAM);
	        set_int_to_buf(temp_buf_in_RAM, (short)8, v);
	        operate_NOT(chk, (short)0, temp_buf_in_RAM, (short)0, chk, (short)0, (short)8);
	        
			if (i + 6 < input_len) {
				data[i - (1 + hrp_len) + data_offs] = (byte)v;
			}
			++i;
		}
		if (have_lower !=0 && have_upper != 0) {
			return FAIL_TO_PROCESS;
		}
		if(chk[7] == 1){
			return 1;
		}
		else{
			return FAIL_TO_PROCESS;
		}
	
	}
	 public static short  get_data_len_for_decode(byte[] input, short input_offs, short input_len)
	 {
			short data_len ;
			if (input_len < CHECKSUM_SIZE || input_len > MAX_CASHADDR_SIZE)  {
		        return FAIL_TO_PROCESS;
		    }
		    data_len = 0;
		    while (data_len < input_len && input[(input_len - 1) - data_len + input_offs] != ':') {
		        ++(data_len);
		    }
		    int  hrp_len = input_len - (1 + data_len);
		    if (hrp_len < 1 || hrp_len > MAX_HRP_SIZE ||
		            data_len < CHECKSUM_SIZE || data_len > CHECKSUM_SIZE + MAX_BASE32_SIZE) {
		            return FAIL_TO_PROCESS;
		    }
		    (data_len) -= CHECKSUM_SIZE;
		    return data_len;
	}
	
	 public static short  get_hrp_len_for_decode(byte[] input, short input_offs, short input_len)
	 {
			short data_len  =get_data_len_for_decode(input,input_offs,input_len) ;
			short  hrp_len = (short) (input_len - (1 + data_len + CHECKSUM_SIZE));
		    return hrp_len;
	 }
	//assist_data_buf  need MAX_BASE32_SIZE= 104 bytes
	//assist_hrp_actual_buf need MAX_HRP_SIZE  + 1bytes
	public  short  cash_addr_decode( 
			byte[] witdata,short withdata_offs, 
			byte[] hrp, short hrp_offs,short hrp_len,
			byte[] addr,short addr_offs,short addr_len,
			byte[] assist_data_buf, short assist_data_buf_offs, 
			byte[] assist_hrp_actual_buf, short assist_hrp_actual_buf_offs) 
	                  
	{
	  //  uint8_t data[MAX_BASE32_SIZE];
	  //  char hrp_actual[MAX_HRP_SIZE+1];
		
		short data_len = get_data_len_for_decode(addr,addr_offs,addr_len);
		
	    if ( data_len == FAIL_TO_PROCESS)
	      return FAIL_TO_PROCESS;
	    
		short assist_hrp_actual_len = (short) (addr_len - (1 + data_len + CHECKSUM_SIZE));
		
		if (data_len == 0 || data_len > MAX_BASE32_SIZE) 
	  	  return FAIL_TO_PROCESS;
	  
	    short _r = cash_decode(assist_hrp_actual_buf,assist_hrp_actual_buf_offs,
	  								assist_data_buf, assist_data_buf_offs,
	  								addr,addr_offs,addr_len);
	
	    if(FAIL_TO_PROCESS == _r){
		  	 return FAIL_TO_PROCESS;
		}
	 
	  if (assist_hrp_actual_len != hrp_len || Util.arrayCompare(hrp, hrp_offs, assist_hrp_actual_buf, assist_hrp_actual_buf_offs, hrp_len) != 0)
	  	 return FAIL_TO_PROCESS;
	  
	
	  
	  short witdata_len = convert_bits(  witdata, withdata_offs,(short) 8,   									
	  								   assist_data_buf,  (short)(assist_data_buf_offs), (short)(data_len),
	  								   (short)5, false);
	  if ( witdata_len == 0)
	  	 return FAIL_TO_PROCESS;
	  
	  if (witdata_len < 2 || witdata_len > MAX_DATA_SIZE) 
	  	 return FAIL_TO_PROCESS;
	  
	  return witdata_len;
	}
	 

}
