package tonwallet ;

import static tonwallet.Constants.*;

import javacard.framework.*;

import static tonwallet.Utils.*;

import javacard.security.*;

//stores one key share according to Shamir secret sharing sceme
public class KeyShare  
{		
	private byte[] encryptedLen; 
	private byte[] keyShareBytes; 
	private short len;	
	private byte[] hash;
	
	private boolean isWellFormed = false;	
	private MessageDigest shaMessageDigest;
		
	public KeyShare(){	
		encryptedLen = new byte[AES_BLOCK_LEN];
		keyShareBytes = new byte[MAX_KEY_SHARE_SIZE];     
		hash = new byte[SHA_HASH_SIZE];
		shaMessageDigest = Sha256Helper.getInstance();//MessageDigest.getInstance(MessageDigest.ALG_SHA_256, false);					
	}
	
	public boolean isWellFormed(){
		return isWellFormed;
	}
	
	public void setIsWellFormed(boolean isWellFormed){
		this.isWellFormed = isWellFormed;
	}
		
	public byte[] getHash(){
		return hash;
	}
	
	public byte[] getEncryptedLen(){
		return encryptedLen;
	}
	
	public byte[] getKeyShareBytes(){
		return keyShareBytes;
	}
	
	public short getLen(){
		return len;
	}
	
	public void incLen(short newLenPortion){
		short newLen = (short)(len + newLenPortion);
		if(newLen > MAX_KEY_SHARE_SIZE){
				ISOException.throwIt(Constants.SW_WRONG_KEY_SHARE_LEN);
		}
		this.len = newLen;
	}
	
	public void resetLen(){
		len = 0;
	}
	
	public void resetKeyShare(){
		len = 0;
		isWellFormed = false;		
		Util.arrayFillNonAtomic(keyShareBytes, (short)0, MAX_KEY_SHARE_SIZE, (byte)0);
		Util.arrayFillNonAtomic(encryptedLen, (short)0, AES_BLOCK_LEN, (byte)0);
		Util.arrayFillNonAtomic(hash, (short)0, SHA_HASH_SIZE, (byte)0);		
	}
	
	public void makeHash(){
		short ret = shaMessageDigest.doFinal(keyShareBytes, 
			(short) 0x00, len, hash, (short) 0x00);
		printArray(hash, (short)0, SHA_HASH_SIZE);	;
		shaMessageDigest.reset();/*	*/			
	}
}


