package com.ftsafe.CoinPassBIO;

import com.ftsafe.util.ByteStringUtil;

import javacard.framework.Util;

/*
### BIP173:
Implementation based on [original BIP](https://github.com/bitcoin/bips/blob/master/bip-0173.mediawiki) by Pieter Wuille and 
Greg Maxwell.

### Tools:
Segwit address format decoder [demo](https://bitcoin.sipa.be/bech32/demo/demo.html).
*/
/*
 * 
 这参考实现C写的代码，来自ledger的开源代码
uint32_t bech32_polymod_step(uint32_t pre) {
    uint8_t b = pre >> 25;
    return ((pre & 0x1FFFFFF) << 5) ^ (-((b >> 0) & 1) & 0x3b6a57b2UL) ^
           (-((b >> 1) & 1) & 0x26508e6dUL) ^ (-((b >> 2) & 1) & 0x1ea119faUL) ^
           (-((b >> 3) & 1) & 0x3d4233ddUL) ^ (-((b >> 4) & 1) & 0x2a1462b3UL);
}

static const char *charset = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

static const int8_t charset_rev[128] = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 15, -1, 10, 17, 21, 20, 26, 30, 7,
    5,  -1, -1, -1, -1, -1, -1, -1, 29, -1, 24, 13, 25, 9,  8,  23, -1, 18, 22,
    31, 27, 19, -1, 1,  0,  3,  16, 11, 28, 12, 14, 6,  4,  2,  -1, -1, -1, -1,
    -1, -1, 29, -1, 24, 13, 25, 9,  8,  23, -1, 18, 22, 31, 27, 19, -1, 1,  0,
    3,  16, 11, 28, 12, 14, 6,  4,  2,  -1, -1, -1, -1, -1};

int bech32_encode(char *output, const char *hrp, const uint8_t *data,
                  size_t data_len) {
    uint32_t chk = 1;
    size_t i = 0;
    while (hrp[i] != 0) {
        if (hrp[i] >= 'A' && hrp[i] <= 'Z')
            return 0;
        if (!(hrp[i] >> 5))
            return 0;
        chk = bech32_polymod_step(chk) ^ (hrp[i] >> 5);
        ++i;
    }
    if (i + 7 + data_len > 90)
        return 0;
    chk = bech32_polymod_step(chk);
    while (*hrp != 0) {
        chk = bech32_polymod_step(chk) ^ (*hrp & 0x1f);
        *(output++) = *(hrp++);
    }
    *(output++) = '1';
    for (i = 0; i < data_len; ++i) {
        if (*data >> 5)
            return 0;
        chk = bech32_polymod_step(chk) ^ (*data);
        *(output++) = charset[*(data++)];
    }
    for (i = 0; i < 6; ++i) {
        chk = bech32_polymod_step(chk);
    }
    chk ^= 1;
    for (i = 0; i < 6; ++i) {
        *(output++) = charset[(chk >> ((5 - i) * 5)) & 0x1f];
    }
    *output = 0;
    return 1;
}

int bech32_decode(char *hrp, uint8_t *data, size_t *data_len,
                  const char *input) {
    uint32_t chk = 1;
    size_t i;
    size_t input_len = strlen(input);
    size_t hrp_len;
    int have_lower = 0, have_upper = 0;
    if (input_len < 8 || input_len > 90) {
        return 0;
    }
    *data_len = 0;
    while (*data_len < input_len && input[(input_len - 1) - *data_len] != '1') {
        ++(*data_len);
    }
    hrp_len = input_len - (1 + *data_len);
    if (hrp_len < 1 || *data_len < 6) {
        return 0;
    }
    *(data_len) -= 6;
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
        chk = bech32_polymod_step(chk) ^ (ch >> 5);
    }
    hrp[i] = 0;
    chk = bech32_polymod_step(chk);
    for (i = 0; i < hrp_len; ++i) {
        chk = bech32_polymod_step(chk) ^ (input[i] & 0x1f);
    }
    ++i;
    while (i < input_len) {
        int v = (input[i] & 0x80) ? -1 : charset_rev[(int)input[i]];
        if (input[i] >= 'a' && input[i] <= 'z')
            have_lower = 1;
        if (input[i] >= 'A' && input[i] <= 'Z')
            have_upper = 1;
        if (v == -1) {
            return 0;
        }
        chk = bech32_polymod_step(chk) ^ v;
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

static int convert_bits(uint8_t *out, size_t *outlen, int outbits,
                        const uint8_t *in, size_t inlen, int inbits, int pad) {
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
    } else if ( ((val << (outbits - bits)) & maxv) || bits >= inbits) {
        return 0;
    }
    return 1;
}

int segwit_addr_encode(char *output, const char *hrp, int witver,
                       const uint8_t *witprog, size_t witprog_len) {
    uint8_t data[65];
    size_t datalen = 0;
    if (witver > 16)
        return 0;
    if (witver == 0 && witprog_len != 20 && witprog_len != 32)
        return 0;
    if (witprog_len < 2 || witprog_len > 40)
        return 0;
    data[0] = witver;
    convert_bits(data + 1, &datalen, 5, witprog, witprog_len, 8, 1);
    ++datalen;
    return bech32_encode(output, hrp, data, datalen);
}

int segwit_addr_decode(int *witver, uint8_t *witdata, size_t *witdata_len,
                       const char *hrp, const char *addr) {
    uint8_t data[84];
    char hrp_actual[84];
    size_t data_len;
    if (!bech32_decode(hrp_actual, data, &data_len, addr))
        return 0;
    if (data_len == 0 || data_len > 65)
        return 0;
    if (strncmp(hrp, hrp_actual, 84) != 0)
        return 0;
    if (data[0] > 16)
        return 0;
    *witdata_len = 0;
    if (!convert_bits(witdata, witdata_len, 8, data + 1, data_len - 1, 5, 0))
        return 0;
    if (*witdata_len < 2 || *witdata_len > 40)
        return 0;
    if (data[0] == 0 && *witdata_len != 20 && *witdata_len != 32)
        return 0;
    *witver = data[0];
    return 1;
}

 * 
 */
public class SegwitAddr {
	// bc
	public static final byte[] bc_hrp = {(byte) 0x62,(byte) 0x63};
	public static final byte[] tb_hrp = {(byte) 0x74,(byte) 0x62};
static	int bech32_polymod_step(int pre) {
	    byte b = (byte) (pre >>> 25);
	    return ((pre & 0x1FFFFFF) << 5) ^ (-((b >> 0) & 1) & 0x3b6a57b2) ^
	           (-((b >> 1) & 1) & 0x26508e6d) ^ (-((b >> 2) & 1) & 0x1ea119fa) ^
	           (-((b >> 3) & 1) & 0x3d4233dd) ^ (-((b >> 4) & 1) & 0x2a1462b3);
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
	 
	 static short convert_bits(byte[] output,short output_offs,int toBits,
			  byte[] data, short data_offs, short data_len, 
			  int fromBits,  boolean pad)    {
	        int acc = 0;
	        int bits = 0;
	        int maxv = (1 << toBits) - 1;
	       
	    	short outlen = 0;
	        for(short i = 0 ; i < data_len; ++i)  {
	            short b = (short)(data[i + data_offs] & 0xff);

	            if (b < 0) {
	                return -1;
	            }
	            else if ((b >> fromBits) > 0) {
	            	return -1;
	            }

	            acc = (acc << fromBits) | b;
	            bits += fromBits;
	            while (bits >= toBits)  {
	                bits -= toBits;
	                output[output_offs+ outlen] = (byte)((acc >> bits) & maxv);
	                outlen++;
	               
	            }
	        }

	        if(pad && (bits > 0))    {
	        	output[output_offs+ outlen] =(byte)((acc << (toBits - bits)) & maxv);
	        	outlen++;
	           
	        }
	        else if (bits >= fromBits || (byte)(((acc << (toBits - bits)) & maxv)) != 0)    {
	        	return -1;
	        }
	        
	        return outlen;
	    }
	 /*
static short convert_bits(byte[] out,short out_offs,  
		short outbits,
		byte[] in, short in_offs, short inlen, 
		short inbits, boolean pad)
                       
                      
{
	short outlen = 0;
    int val = 0;
    int bits = 0;
    
    int maxv = (1 << outbits) - 1;
    
    while (inlen-- != 0) {
        val = (val << inbits) | in[in_offs++];
        bits += inbits;
        while (bits >= outbits) {
            bits -= outbits;
            out[outlen + out_offs] = (byte) ((val >> bits) & maxv);
            outlen++;
        }
    }
    if (pad == true) {
    	
        if (bits != 0) {
            out[ outlen + out_offs ] = (byte) ((val << (outbits - bits)) & maxv);
            
            outlen++;
        }
        
    } else if ( ((val << (outbits - bits)) & maxv) != 0 || (bits >= inbits) == true) {
        return 0;
    }
    return outlen;
}
*/
// help_data_buf 要有65字节
public static short segwit_addr_encode( byte[]output,short output_offs, 
										byte[] hrp, short hrp_offs,short hrp_len,
										byte witver,
										byte[] witprog, short witprog_offs, short witprog_len,
										byte[] help_data_buf, short help_data_buf_offs) {
    
    short datalen = 0;
    if (witver > 16)
        return FAIL_TO_PROCESS;
    if (witver == 0 && witprog_len != 20 && witprog_len != 32)
        return FAIL_TO_PROCESS;
    if (witprog_len < 2 || witprog_len > 40)
        return FAIL_TO_PROCESS;
    help_data_buf[0 + help_data_buf_offs] = witver;
    

  	
    datalen =  convert_bits(help_data_buf, (short)(help_data_buf_offs+ 1) , 
    		(short)5, 
    		witprog,witprog_offs, witprog_len,
    		(short)8, true);
    if(-1 == datalen){
    	return FAIL_TO_PROCESS;
    }
    ++datalen;

	
    witprog_len =  bech32_encode(output, output_offs,hrp, hrp_offs,hrp_len,
    		help_data_buf, help_data_buf_offs,datalen);
    return witprog_len;
    
}
public static final short FAIL_TO_PROCESS = -1;

public static short bech32_encode(byte[]output,short output_offs, byte[] hrp, short hrp_offs,short hrp_len,
		byte[] data,short data_offs,short data_len) {
    int chk = 1;
    short i = 0;
    short temp_value;
    
    short old_output_offs = output_offs;
    for(; i < hrp_len;){
    	temp_value = (short)(hrp[i + hrp_offs] & 0xff);
        if (temp_value >= 'A'  && temp_value <= 'Z')
            return FAIL_TO_PROCESS;
        if (( temp_value >> 5) == 0)
            return FAIL_TO_PROCESS;
        chk = bech32_polymod_step(chk) ^ (temp_value >> 5);
        ++i;
    }
    if (i + 7 + data_len > 90)
    	 return FAIL_TO_PROCESS;
    
    chk = bech32_polymod_step(chk);

    for(i = 0; i < hrp_len;++i){
    	temp_value  =(short)( hrp[hrp_offs] & 0xff );    
        chk = bech32_polymod_step(chk) ^ (temp_value & 0x1f);
        output[output_offs++] = hrp[hrp_offs++];
        
        
    }
    
    output[output_offs++] = '1';//'1';
    
    for (i = 0; i < data_len; ++i) {
    	temp_value = (short)( data[data_offs] & 0xff ); 
        if (temp_value >> 5 != 0)
            return FAIL_TO_PROCESS;
        chk = bech32_polymod_step(chk) ^ (temp_value);
      
        output[output_offs++] = charset[data[data_offs++]];  
    }
    for (i = 0; i < 6; ++i) {
        chk = bech32_polymod_step(chk);
    }
    chk ^= 1;
    for (i = 0; i < 6; ++i) {
    	output[output_offs++] = charset[(chk >> ((5 - i) * 5)) & 0x1f];
    }
    //output[output_offs] = 0;
    
    return (short)(output_offs - old_output_offs );
}

public static short  get_data_len_for_bech32_decode(byte[] input, short input_offs, short input_len){
	short data_len ;
    if (input_len < 8 || input_len > 90) {
        return -1;
    }
    data_len = 0;
    while (data_len < input_len && input[(input_len - 1) - data_len + input_offs] != Alphabet._1) {
        ++(data_len);
    }
    if (data_len < 6) {
        return -1;
    }
    (data_len) -= 6;
    return data_len;
}

public static short  get_hrp_len_for_bech32_decode(short input_len,short data_len ){
	return (short) (input_len - (1 + data_len + 6));
}
public static short bech32_decode( byte[] hrp,short hrp_offs, short hrp_len,
		 							byte[] data, short data_offs,short data_len,
		 							byte[] input, short input_offs, short input_len) 
		
 {
	
    int chk = 1;
    short i;
    int have_lower = 0, have_upper = 0;
    if (input_len < 8 || input_len > 90) {
        return FAIL_TO_PROCESS;
    }
    if (hrp_len < 1 || data_len < 0) {
        return FAIL_TO_PROCESS;
    }
  
    
    short ch;
    for (i = 0; i < hrp_len; ++i) {
        ch = (short)(input[i + input_offs]   & 0xff);
        if (ch < 33 || ch > 126) {
            return FAIL_TO_PROCESS;
        }
        if (ch >= 'a' && ch <= 'z' ) {
            have_lower = 1;
        } else if (ch >= 'A' && ch <= 'Z') {
            have_upper = 1;
            ch =  (short)( ch - Alphabet._A + Alphabet._a );
        }
        hrp[i + hrp_offs] = (byte) ch;
        chk = bech32_polymod_step(chk) ^ (ch >> 5);
    }
    hrp[i + hrp_offs] = 0;
    chk = bech32_polymod_step(chk);
    for (i = 0; i < hrp_len; ++i) {
        chk = bech32_polymod_step(chk) ^ (input[i + input_offs] & 0x1f);
    }
    ++i;
    while (i < input_len) {
    	ch  = (short)(input[i + input_offs] & 0xff );
        short v = (ch & 0x80) != 0 ? -1 : charset_rev[ ch ];
        if (ch >= 'a' && ch <= 'z')
            have_lower = 1;
        if (ch >= 'A' && ch <= 'Z')
            have_upper = 1;
        if (v == -1) {
            return FAIL_TO_PROCESS;
        }
        chk = bech32_polymod_step(chk) ^ v;
        if (i + 6 < input_len) {
            data[i - (1 + hrp_len) + data_offs] = (byte)v;
        }
        ++i;
    }
    if (have_lower !=0 && have_upper != 0) {
        return FAIL_TO_PROCESS;
    }
    if(chk == 1){
    	return 1;
    }
    else{
    	return FAIL_TO_PROCESS;
    }
   
}

// assist_data_buf 需要84字节
// assist_hrp_actual_buf 需要84字节
public static short  segwit_addr_decode( byte[] witdata,short withdata_offs, 
		byte[] hrp, short hrp_offs,short hrp_len,
		byte[] addr,short addr_offs,short addr_len,
		byte[] assist_data_buf, short assist_data_buf_offs, 
		byte[] assist_hrp_actual_buf, short assist_hrp_actual_buf_offs) 
                    
{
    //byte data[] = new byte[84];
    //byte hrp_actual[] = new byte[84];
	short data_len = get_data_len_for_bech32_decode(addr,addr_offs,addr_len);
    if ( data_len == -1)
        return FAIL_TO_PROCESS;
	short assist_hrp_actual_len = get_hrp_len_for_bech32_decode(addr_len, data_len);
	
    if (data_len == 0 || data_len > 65)
    	 return FAIL_TO_PROCESS;
    
    short _r = bech32_decode(assist_hrp_actual_buf,assist_hrp_actual_buf_offs,assist_hrp_actual_len,
    								assist_data_buf, assist_data_buf_offs,data_len,
    								addr,addr_offs,addr_len);

    if(FAIL_TO_PROCESS == _r){
    	 return FAIL_TO_PROCESS;
    }
   
    if (assist_hrp_actual_len != hrp_len || Util.arrayCompare(hrp, hrp_offs, assist_hrp_actual_buf, assist_hrp_actual_buf_offs, hrp_len) != 0)
    	 return FAIL_TO_PROCESS;
    
    if (assist_data_buf[0 + assist_data_buf_offs] > 16)
    	 return FAIL_TO_PROCESS;
    
    short witdata_len = convert_bits(  witdata, withdata_offs,(short) 8,   									
    								   assist_data_buf,  (short)(assist_data_buf_offs + 1), (short)(data_len - 1),
    								   (short)5, false);
    if ( witdata_len == 0)
    	 return FAIL_TO_PROCESS;
    if (witdata_len < 2 || witdata_len > 40)
    	 return FAIL_TO_PROCESS;
    if (assist_data_buf[0 + assist_data_buf_offs] == 0 && witdata_len != 20 && witdata_len != 32)
    	 return FAIL_TO_PROCESS;
   //byte witver = assist_data_buf[0];
    return witdata_len;
}

}
