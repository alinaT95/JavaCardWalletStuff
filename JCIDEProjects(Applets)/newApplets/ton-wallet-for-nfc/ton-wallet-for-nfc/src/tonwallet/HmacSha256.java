package tonwallet ;

import javacard.framework.*;
import javacard.security.*;

public class HmacSha256  {
	public final short LEN_HMAC_BLOCK = 64; 

    public final short HASH_SIZE = 32;
    
    public final short KEY_SIZE = 32;
    
    private final byte ipad = 0x36;
	private final byte opad = 0x5C;
	
	private byte[] tmpBuffer = new byte[LEN_HMAC_BLOCK];
	private byte[] ipadK = new byte[LEN_HMAC_BLOCK];
	private byte[] opadK = new byte[LEN_HMAC_BLOCK];
    
    private MessageDigest md;
     
    public HmacSha256(){
    	md = Sha256Helper.getInstance();
	   // md =  MessageDigest.getInstance(MessageDigest.ALG_SHA_256, false); 
	}
	 

    public short getMac(byte[] key, short keyOffset,  
							byte[] msg, short msgOffset, short msgLength,
							byte[] response, short responseOffset)
	{
		
		if (((short)(key.length - keyOffset) < KEY_SIZE) || ((short)(response.length - responseOffset) < HASH_SIZE) 
			|| ((short)(msg.length - msgOffset) < msgLength))
		{
			 ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);	
		}
		
		Util.arrayCopyNonAtomic(key, keyOffset, tmpBuffer, (short)0, KEY_SIZE);
		
		for (short i = 0; i < LEN_HMAC_BLOCK; i++)
		{
			// for each byte of the key, work out the pad byte
			if (i >= KEY_SIZE)
			{
				ipadK[i] = (byte) (0x00 ^ ipad);
				opadK[i] = (byte) (0x00 ^ opad);
			}
			else
			{
				ipadK[i] = (byte) (tmpBuffer[i] ^ ipad);
				opadK[i] = (byte) (tmpBuffer[i] ^ opad);
			}
		}

		// clear the tmpBuffer - it can be used now for digest outputs
		Util.arrayFillNonAtomic(tmpBuffer, (short) 0, KEY_SIZE, (byte)0x00);

		// find H(ipadK || msg)
        md.reset();
        md.update(ipadK, (short) 0, LEN_HMAC_BLOCK);
        md.doFinal(msg, msgOffset, msgLength, tmpBuffer, (short) 0);

		//now find H(opadK || tmpBuffer)
		md.reset();
        md.update(opadK, (short)0, LEN_HMAC_BLOCK);
		short outputLength;
		outputLength = md.doFinal(tmpBuffer, (short) 0, KEY_SIZE, response, responseOffset);
		md.reset();
		// clear the rndBuffer
		Util.arrayFillNonAtomic(tmpBuffer, (short) 0, KEY_SIZE, (byte)0x00);
		return outputLength;
	}
}
