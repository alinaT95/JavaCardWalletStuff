package wallet ;

import javacard.framework.ISOException;
import javacard.framework.Util;

// very limited Hmac-SHA512 implementation
public class HmacSha512 {
	public static final short BLOCKSIZE = 128; // 128 bytes 
	public static final short HASHSIZE = 64;
	private static final short SW_UNSUPPORTED_KEYSIZE = (short) 0x9c0E;
	private static final short SW_UNSUPPORTED_MSGSIZE = (short) 0x9c0F;
	private static byte[] data;
	
	
	public static void init(byte[] tmp){
		data = tmp;
	}
	
	public static short computeHmacSha512(byte[] key, short keyOffset, short keyLength, 
			byte[] message, short messageOffset, short messageLength,
			byte[] mac, short macOffset){
		
		if (keyLength > BLOCKSIZE || keyLength < 0){
			ISOException.throwIt(SW_UNSUPPORTED_KEYSIZE); // don't accept keys bigger than block size 
		}
		if (messageLength > HASHSIZE || messageLength < 0){
			ISOException.throwIt(SW_UNSUPPORTED_MSGSIZE); // don't accept messsage bigger than block size (should be sufficient for BIP32)
		}
		
		// compute inner hash
		for (short i = 0; i < keyLength; i++){
			data[i] = (byte) (key[(short)(keyOffset + i)] ^ (0x36));
		}
		Util.arrayFillNonAtomic(data, keyLength, (short)(BLOCKSIZE - keyLength), (byte)0x36);		
		Util.arrayCopyNonAtomic(message, messageOffset, data, BLOCKSIZE, messageLength);
		//Sha512.reset();
		//Sha512.doFinal(data, (short)0, (short)(BLOCKSIZE+messageLength), data, BLOCKSIZE); // copy hash result to data buffer!
		SHA512.resetUpdateDoFinal(data, (short)0, (short)(BLOCKSIZE + messageLength), data, BLOCKSIZE); // copy hash result to data buffer!
		
		// compute outer hash
		for (short i = 0; i < keyLength; i++){
			data[i] = (byte) (key[(short)(keyOffset + i)] ^ (0x5c));
		}
		Util.arrayFillNonAtomic(data, keyLength, (short)(BLOCKSIZE - keyLength), (byte)0x5c);
		// previous hash already copied to correct offset in data
		//Sha512.reset();
		//Sha512.doFinal(data, (short)0, (short)(BLOCKSIZE+HASHSIZE), mac, macOffset);
		SHA512.resetUpdateDoFinal(data, (short)0, (short)(BLOCKSIZE + HASHSIZE), mac, macOffset);
		
		return HASHSIZE;
	}	
	
}

