package com.ftsafe.CoinPassBIO;
import javacard.framework.Util;
/*
**
 * Base58 is a way to encode Bitcoin addresses (or arbitrary data) as alphanumeric strings.
 * <p>
 * Note that this is not the same base58 as used by Flickr, which you may find referenced around the Internet.
 * <p>
 * You may want to consider working with {@link VersionedChecksummedBytes} instead, which
 * adds support for testing the prefix and suffix bytes commonly found in addresses.
 * <p>
 * Satoshi explains: why base-58 instead of standard base-64 encoding?
 * <ul>
 * <li>Don't want 0OIl characters that look the same in some fonts and
 *     could be used to create visually identical looking account numbers.</li>
 * <li>A string with non-alphanumeric characters is not as easily accepted as an account number.</li>
 * <li>E-mail usually won't line-break if there's no punctuation to break at.</li>
 * <li>Doubleclicking selects the whole number as one word if it's all alphanumeric.</li>
 * </ul>
 * <p>
 * However, note that the encoding/decoding runs in O(n&sup2;) time, so it is not useful for large data.
 * <p>
 * The basic idea of the encoding is to treat the data bytes as a large number represented using
 * base-256 digits, convert the number to be represented using base-58 digits, preserve the exact
 * number of leading zeros (which are otherwise lost during the mathematical operations on the
 * numbers), and finally represent the resulting base-58 digits as alphanumeric ASCII characters.
 */
public class Base58 {
    public static final byte[] ALPHABET = {
(byte)0x31 ,// 1
(byte)0x32 ,// 2
(byte)0x33 ,// 3
(byte)0x34 ,// 4
(byte)0x35 ,// 5
(byte)0x36 ,// 6
(byte)0x37 ,// 7
(byte)0x38 ,// 8
(byte)0x39 ,// 9
(byte)0x41 ,// A
(byte)0x42 ,// B
(byte)0x43 ,// C
(byte)0x44 ,// D
(byte)0x45 ,// E
(byte)0x46 ,// F
(byte)0x47 ,// G
(byte)0x48 ,// H
(byte)0x4a ,// J
(byte)0x4b ,// K
(byte)0x4c ,// L
(byte)0x4d ,// M
(byte)0x4e ,// N
(byte)0x50 ,// P
(byte)0x51 ,// Q
(byte)0x52 ,// R
(byte)0x53 ,// S
(byte)0x54 ,// T
(byte)0x55 ,// U
(byte)0x56 ,// V
(byte)0x57 ,// W
(byte)0x58 ,// X
(byte)0x59 ,// Y
(byte)0x5a ,// Z
(byte)0x61 ,// a
(byte)0x62 ,// b
(byte)0x63 ,// c
(byte)0x64 ,// d
(byte)0x65 ,// e
(byte)0x66 ,// f
(byte)0x67 ,// g
(byte)0x68 ,// h
(byte)0x69 ,// i
(byte)0x6a ,// j
(byte)0x6b ,// k
(byte)0x6d ,// m
(byte)0x6e ,// n
(byte)0x6f ,// o
(byte)0x70 ,// p
(byte)0x71 ,// q
(byte)0x72 ,// r
(byte)0x73 ,// s
(byte)0x74 ,// t
(byte)0x75 ,// u
(byte)0x76 ,// v
(byte)0x77 ,// w
(byte)0x78 ,// x
(byte)0x79 ,// y
(byte)0x7a // z
    };
    //private static final short ENCODED_ZERO = ALPHABET[0];
    private static final short[] INDEXES = {
	    (short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)0, // 1 
	(short)1, // 2 
	(short)2, // 3 
	(short)3, // 4 
	(short)4, // 5 
	(short)5, // 6 
	(short)6, // 7 
	(short)7, // 8 
	(short)8, // 9 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)9, // A 
	(short)10, // B 
	(short)11, // C 
	(short)12, // D 
	(short)13, // E 
	(short)14, // F 
	(short)15, // G 
	(short)16, // H 
	(short)-1, 
	(short)17, // J 
	(short)18, // K 
	(short)19, // L 
	(short)20, // M 
	(short)21, // N 
	(short)-1, 
	(short)22, // P 
	(short)23, // Q 
	(short)24, // R 
	(short)25, // S 
	(short)26, // T 
	(short)27, // U 
	(short)28, // V 
	(short)29, // W 
	(short)30, // X 
	(short)31, // Y 
	(short)32, // Z 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)33, // a 
	(short)34, // b 
	(short)35, // c 
	(short)36, // d 
	(short)37, // e 
	(short)38, // f 
	(short)39, // g 
	(short)40, // h 
	(short)41, // i 
	(short)42, // j 
	(short)43, // k 
	(short)-1, 
	(short)44, // m 
	(short)45, // n 
	(short)46, // o 
	(short)47, // p 
	(short)48, // q 
	(short)49, // r 
	(short)50, // s 
	(short)51, // t 
	(short)52, // u 
	(short)53, // v 
	(short)54, // w 
	(short)55, // x 
	(short)56, // y 
	(short)57, // z 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1, 
	(short)-1
		};
  
	/*
	     这一段代码要是在java中执行后，就是 INDEXES的值
		 static {
			Arrays.fill(INDEXES, -1);
			for (int i = 0; i < ALPHABET.length; i++) {
				INDEXES[ALPHABET[i]] = i;
			}
		}
     */
  
 public static  boolean is_base58( byte c )
{
	if( c >= '1' && c <= '9' )
	{
		return true;
	}
	if( c >= 'A' && c <=  'z' )
	{
		if(  'I' != c
			&&
			'O'  != c
			)
		{
			return false;
		}
	}
	if( c >= 'a'  && c <= 'z')
	{
		if(   '1' != c )
		{
			return true;
		}
	}
	return false;
}

 public static short search_first_nonzero(byte[] input, short offset,short len )
{
	short i = offset;
	short _end = (short)(offset + len);
	while( i < _end )
	{
		if( input[i] != 0){
			break;
		}
		i++;
	}
    return i;
}

//
// number -> number / 58, returns number % 58
//
public static byte divmod58( byte[] number, short num_len, short startAt)
{
	short i;
	short remainder = 0;
	short digit256;
	short temp;
	for( i = startAt; i < num_len; i++)
	{
		digit256  = (short) (number[i] & 0xFF);
		temp  = (short)(remainder * 256 + digit256);
		
		number[i] = (byte)(temp / 58);
		
		remainder = (short)(temp % 58);
	}
	
	return (byte)remainder;
}

//
// number -> number / 256, returns number % 256
//

public static byte divmod256( byte[] number58, short offset, short len, short startAt)
{
	short remainder = 0;
	short i;
	for( i = startAt; i < len; i++)
	{
		short digit58 = (short)(number58[i + offset] & 0xFF);
		short temp = (short)(remainder * 58 + digit58);
		
		number58[i+offset] = (byte)(temp / 256);
		
		remainder = (short)(temp % 256);
	}
	
	return (byte)remainder;
}

public static short encode( byte[] input, short input_offs, short input_len,  byte[] output, short output_offs )
{
	

	if( output != null)
	{
		output[ 0 + output_offs] = 0;
	}

	if( 0 == input_len)
	{
		return 0;
	}

	
	// Count leading zeroes.
	short zeroCount = 0;
	while( zeroCount < input_len
			&&
			0 == input[ (short)(zeroCount + input_offs)]
		)
	{
		++zeroCount;
	}

	// The actual encoding.
	short j = (short)(input_len * 2);
	output[(short)( j + output_offs)] = 0;
	
	short startAt = (short)(zeroCount);
	byte mod;
	while( startAt < input_len )
	{
		mod = divmod58( input, input_len, (short)(startAt + input_offs));
		if (input[ (short)(startAt + input_offs)] == 0)
		{
			++startAt;
		}
		--j;
		output[ (short)(j + output_offs) ] = ALPHABET[mod];
	}
	
	// Strip extra '1' if there are some after decoding.
	while(
		j < input_len * 2
		&&
		output[ (short)(j + output_offs)  ] == ALPHABET[0]
		)
	{
		++j;
	}
	// Add as many leading '1' as there were leading zeros.
	while (--zeroCount >= 0){
		--j;
		output[(short)(j + output_offs)] = ALPHABET[0];
	}
	
	Util.arrayCopyNonAtomic(output, (short)(j+output_offs) ,output,(short)(0x00 + output_offs), (short)( input_len * 2 + 2 ));
	return (short)(input_len * 2 - j);
}


	public static short decode( byte[] input,short input_offset, short input_len,  byte[] output ,byte[] base58_tchar_tmp,short base58_tchar_tmp_offset)
	{
		short i;
		byte c;
		byte[] input58 = base58_tchar_tmp;
	
		// Transform the String to a base58 byte sequence
		for( i = 0; i < input_len; ++i )
		{
			c = input[ i + input_offset ];
			
			short digit58 = -1;
			if( c >= 0 && c < 128)
			{
				digit58 = INDEXES[c];
			}
			if( digit58 < 0)
			{
				return -1;
			}
			
			input58[ i + base58_tchar_tmp_offset] = (byte)digit58;
		}
		
		// Count leading zeroes
		short zeroCount = 0;
		while(
			zeroCount < input_len
			&&
			0 == input58[zeroCount + base58_tchar_tmp_offset]
			)
		{
			++zeroCount;
		}
		short j = ( short )input_len;
		short startAt = zeroCount;
		 byte mod;
		while (startAt < input_len )
		{
			mod = divmod256(input58,base58_tchar_tmp_offset, input_len, startAt);
			if (input58[startAt + base58_tchar_tmp_offset] == 0)
			{
				++startAt;
			}
			--j;
			output[j] = mod;
		}
		
		// Do no add extra leading zeroes, move j to first non null byte.
		while( j < input_len
			&&
			output[j] == 0
			)
		{
			++j;
		}
		//	memmove( output, output + j, len );
		Util.arrayCopyNonAtomic(output,  (short)(j), output, (short)0, input_len);
		output[ input_len - j  ] = 0;
		return  (short)( input_len - j);
		
		
	}
}
