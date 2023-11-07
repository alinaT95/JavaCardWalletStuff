package com.ftsafe.CoinPassBIO;

import javacard.framework.APDU;
import javacard.framework.ISOException;
import javacard.framework.Util;
import javacard.framework.*;
public class IO
{
	/**
	 * <p>Title: apduSend</p>
	 * 
	 * @param  apdu	  
	 * @param  inBuff
	 * @param  inOffset
	 * @param  inLength		
	 * @return NULL
	 */
	public static short dumpByteNum = 0x00;
	public static final byte[] _1 = {
		0x00,0x00,0x00,0x01
	};
	
	public static final short APUDU_BUFFER_MAX_SEND = 252;
	public static void apdusend(APDU apdu,byte[] inBuff,short inOffset,short inLength)
	{
		
		short Le = apdu.setOutgoing();
		if (Le==256||Le==0  || Le == 0x7fff)
		{
			Le = inLength;
		}
		else if(Le>inLength)
		{
			if(APDU.getProtocol() == APDU.PROTOCOL_T1)	//接触式
			{
				ISOException.throwIt((short)0x6700);
			}
			else if(APDU.getProtocol() == APDU.PROTOCOL_T0  )
			{
				ISOException.throwIt(Util.makeShort((byte)0x6C,(byte)inLength));
			}
			else
			{
				Le = inLength;
			}
		}
		else
		{
			Le = inLength;
		}
		
		apdu.setOutgoingLength(Le);
		apdu.sendBytesLong(inBuff, inOffset, Le);
	}

	private static short getUnsignedByte(byte[] a, byte aoffset, byte length)
	{
		return (short) (a[(short) ((length + aoffset) & 0x00FF)] & 0x00FF);
	}

	public static boolean add(byte[] a, byte aoffset, byte[] b, byte boffset, byte[] c, byte coffset, byte length)
	{
		short result = (short) 0x00;

		for (length = (byte) (length - 1); length >= (byte) 0x00; length--)
		{
			result = (short) (getUnsignedByte(a, (byte) aoffset, (byte) length)
					+ getUnsignedByte(b, (byte) boffset, (byte) length) + result);
			c[(short) ((length + coffset) & 0x00FF)] = (byte) (result & 0xFF);
			if (result > (short) 0x00FF)
			{
				result = (short) 0x01;
			}
			else
			{
				result = (short) 0x00;
			}
		}

		if (result == (short) 0x01)
		{
			return false;
		}

		return true;
	}




	// bit参数从左开始，bit 7叫第0位，所以是反过来的
	public static short getbit( byte input[], short inoffset, short bit_offset )
	{
		short offset = (short)( bit_offset / 8 );
		short bit = (short)( bit_offset % 8 );
		return (short)( ( input[ inoffset + offset ] >> ( 7 - bit ) ) & 0x01 );
	}

	public static short split_index( byte input[], short inoff, short len, short the_index[], short index_basoffset )
	{
		short count = 0;
		short index;
		short the_end = (short)( inoff + len );
		short base_offset = 0;
		short tmp;
		short start = 0;
		short end = 0;

		int i;

		do
		{
			start = (short)( inoff + count * 11 / 8 );
			end = (short)(inoff + ( count + 1 ) * 11 / 8);
			index = 0;

			for( i = 0; i < 11; i++ )
			{
				tmp = getbit( input, inoff, (short)( base_offset + i ) );
				index |= ( tmp << ( 10 - i ) );
			}
			base_offset += 11;
			the_index[ index_basoffset + count++ ] = index;
		} while( start < the_end && end < the_end );

		return count;
	}

	// bit参数从左开始，bit 7叫第0位，所以是反过来的
	public static void setbit( byte buf[], short offset, short bit_offset, byte value )
	{
		short offset1 = (short)( bit_offset / 8 );
		short bit = (short)(bit_offset % 8);
		buf[ offset + offset1 ] &= ~( 1 << ( 7 - bit ) );
		if( 0 != value )
		{
			buf[ offset + offset1 ] |= ( 1 << ( 7 - bit ) );
		}
	}

	public static void merge_index( short index[], short count, byte output[], short outoff )
	{
		short i;
		short base_offset = 0;
		short j;
		byte tmp;
		short index1;

		for( i = 0; i < count; i++ )
		{
			index1 = index[ i ];
			for( j = 0; j < 11; j++ )
			{
				tmp = (byte)( ( index1 >> ( 10 - j ) ) & 0x01 );
				setbit( output, outoff, (short)( base_offset + j ), tmp );
			}
			base_offset += 11;
		}
	}

	/**
	 * <p>Title: get_tlv</p>
	 * @desc  遍历buf的TLV对象，查找指定tag的数据，并将Value设置于outbuf中
	 * @param buf	   被查找的源数组
	 * @param offset    被查找的源数组起始偏移值
	 * @param length 被查找的源数组长度范围
	 * @param tag      被查找数据对象的tag,单字节tag的低8位为0x00
	 * @param outbuf   保存查找到对象的Value的数组
	 * @param outoff   保存查找到对象相对数组outbuf起始位置的偏移量
	 *
	 * @return 	       被查找对象的数据长度
	 */
	public static short get_tlv( byte buf[], short offset, short length, short tag, byte outbuf[], short outoff )
	{
		short tlv_len = 0;
		short tmp_tag;
		short next_byte_abs_value;
		short offset_end = (short)( offset + length );

		if( (byte)0x70 == (byte)buf[ offset ] )
		{
			next_byte_abs_value = (short)(buf[(short)(offset+1)] & 0xff);
			if( next_byte_abs_value < (short)0x80 )
			{
				length     = next_byte_abs_value;
				offset     += 2;
			}
			else if( 0x81 == (short)next_byte_abs_value )
			{
				length     = (short)((short)(buf[(short)(offset+2)] & 0xff));
				offset     += 3;
			}
			else if( 0x82 == (short)next_byte_abs_value )
			{
				length     = (short)(Util.getShort( buf, (short)( offset + 2 ) ) );
				offset     += 4;
			}
			else
			{
				//Error.wrong6403_s_TlvFormatError( (byte)0 );
			}
		}

		while( offset < offset_end )
		{
			if((byte)0x1f == (byte)(buf[offset] & 0x1F))
			{
				tmp_tag    = Util.getShort( buf, offset );
				offset     += 2;
			}
			else
			{
				tmp_tag    = Util.makeShort((byte)0x00, buf[offset]);
				offset     += 1;
			}
			next_byte_abs_value = (short)(buf[offset] & 0xff);

			//处理单字节Len与多字节Len的情况
			if( next_byte_abs_value < (short)0x80 )
			{
				tlv_len    = buf[offset];
			}
			else if( 0x81 == next_byte_abs_value )
			{
				tlv_len    = (short)( buf[(offset+1)] & 0xff );
				offset     += 1;
			}
			else if( 0x82 == (short)next_byte_abs_value )
			{
				tlv_len    = Util.getShort(buf, (short)(offset + 1));
				offset     += 2;
			}
			else
			{
				//Error.wrong6403_s_TlvFormatError( (byte)1 );
			}

			offset++;

			if(tag == tmp_tag)
			{
				if( null != outbuf )
				{
					Util.arrayCopyNonAtomic( buf, offset, outbuf, outoff, tlv_len );
				}
				break;
			}
			else
			{
				offset     += tlv_len;
				tlv_len    = 0x00;
			}

		}

		return tlv_len;
	}

	/**
	 * <p>Title: get_tlv_value_offset</p>
	 * @desc  遍历buf的TLV对象，查找指定tag的数据，并将Value设置于outbuf中
	 * @param buf	   被查找的源数组
	 * @param offset    被查找的源数组起始偏移值
	 * @param length 被查找的源数组长度范围
	 * @param tag      被查找数据对象的tag,单字节tag的低8位为0x00
	 *
	 * @return 	       被查找对象的数据的偏移量
	 */
	public static short get_tlv_value_offset( byte buf[], short offset, short length, short tag )
	{
		short tlv_len = 0;
		short tmp_tag;
		short next_byte_abs_value;
		short offset_end = (short)( offset + length );

		if( (byte)0x70 == (byte)buf[ offset ] )
		{
			next_byte_abs_value = (short)(buf[(short)(offset+1)] & 0xff);
			if( next_byte_abs_value < (short)0x80 )
			{
				length     = next_byte_abs_value;
				offset     += 2;
			}
			else if( 0x81 == (short)next_byte_abs_value )
			{
				length     = (short)((short)(buf[(short)(offset+2)] & 0xff));
				offset     += 3;
			}
			else if( 0x82 == (short)next_byte_abs_value )
			{
				length     = (short)(Util.getShort( buf, (short)( offset + 2 ) ) );
				offset     += 4;
			}
			else
			{
				//Error.wrong6403_s_TlvFormatError( (byte)0 );
			}
		}

		while( offset < offset_end )
		{
			if((byte)0x1f == (byte)(buf[offset] & 0x1F))
			{
				tmp_tag    = Util.getShort( buf, offset );
				offset     += 2;
			}
			else
			{
				tmp_tag    = Util.makeShort((byte)0x00, buf[offset]);
				offset     += 1;
			}
			next_byte_abs_value = (short)(buf[offset] & 0xff);

			//处理单字节Len与多字节Len的情况
			if( next_byte_abs_value < (short)0x80 )
			{
				tlv_len    = buf[offset];
			}
			else if( 0x81 == next_byte_abs_value )
			{
				tlv_len    = (short)( buf[(offset+1)] & 0xff );
				offset     += 1;
			}
			else if( 0x82 == (short)next_byte_abs_value )
			{
				tlv_len    = Util.getShort(buf, (short)(offset + 1));
				offset     += 2;
			}
			else
			{
				//Error.wrong6403_s_TlvFormatError( (byte)1 );
			}

			offset++;

			if(tag == tmp_tag)
			{
				return offset;
			}
			else
			{
				offset     += tlv_len;
				tlv_len    = 0x00;
			}

		}

		return (short)-1;
	}

	public static short get_tlv_multi( byte buf[], short offset, short length, byte tag_list[], short tag_offset, byte outbuf[], short outoff )
	{
		short tag;
		short len;
		boolean copy_and_return = false;
		short value_offset;
		while( 0 != tag_list[ tag_offset ] )
		{
			if((byte)0x1f == (byte)( tag_list[ tag_offset ] & 0x1F ) )
			{
				tag    = Util.getShort( tag_list, tag_offset );
				tag_offset += 2;
			}
			else
			{
				tag    = Util.makeShort( (byte)0x00, tag_list[ tag_offset ] );
				tag_offset += 1;
			}
			if( 0 == tag_list[ tag_offset ] )
			{
				copy_and_return = true;
			}

			len = get_tlv( buf, offset, length, tag, null, (short)0 );
			if( len > 0 )
			{
				// 有这个tag
				value_offset = get_tlv_value_offset( buf, offset, length, tag );
				if( copy_and_return )
				{
					if( null != outbuf )
					{
						Util.arrayCopyNonAtomic( buf, value_offset, outbuf, outoff, len );
					}
					return (short)( ( value_offset << 8 ) | len );
				}
				else
				{
					return get_tlv_multi( buf, value_offset, len, tag_list, tag_offset, outbuf, outoff );
				}
			}
			else
			{
				return (short)0;
			}
		}

		return (short)0;
	}


/**
	 * <p>Title: receiveAPDU</p>
	 * @author Christina
	 * @version 2.0
	 *
	 * @param  apdu
	 * @return Lc
	 */
	public static short receiveAPDUData(APDU apdu, byte dest[],short bytesReceived, short destOffset) {

		byte buf[] = apdu.getBuffer();
		short lc = Util.makeShort((byte)0x00, buf[ISO7816.OFFSET_LC]);

		if (lc == (short)0x00) {
			short received;

			short dataLength = Util.getShort(buf, ISO7816.OFFSET_CDATA);
			if ((dataLength < (short)0)
					|| (dataLength < bytesReceived)){
				ISOException.throwIt((short)0x6700);
			}
			if( (dataLength  + destOffset) > dest.length){
				ISOException.throwIt((short)0x6700);
			}

			Util.arrayCopyNonAtomic(buf, ISO7816.OFFSET_EXT_CDATA, dest, destOffset, bytesReceived);


			while (bytesReceived < dataLength){
				received = apdu.receiveBytes((short)0);
				Util.arrayCopyNonAtomic(buf, (short)0, dest, (short) (bytesReceived + destOffset), received);
				bytesReceived += received;
				if ((apdu.getCurrentState() == APDU.STATE_FULL_INCOMING) &&
						((short)bytesReceived != dataLength)){
					ISOException.throwIt((short)0x6700);
				}
			}

			lc = dataLength;
		}
		else {
			Util.arrayCopyNonAtomic(buf, ISO7816.OFFSET_CDATA, dest, destOffset, lc);
		}

		return lc;
	}
}
