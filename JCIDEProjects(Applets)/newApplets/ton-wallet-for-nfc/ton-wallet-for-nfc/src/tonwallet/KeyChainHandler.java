package tonwallet ;

import javacard.framework.*;

import static tonwallet.Constants.*;
import static tonwallet.PinHandler.*;

import static tonwallet.Utils.*;

public class KeyChainHandler  
{
	public final static short KEY_CHAINS_TOTAL_AMOUNT = (short) 3; //(short) 10;
	
	private KeyShare[] keyShares;
	private byte[] hashFromHost;
	private AesHelper aesHelper;
		
	public KeyChainHandler(){
		keyShares = new KeyShare[KEY_CHAINS_TOTAL_AMOUNT];
		for(byte i = 0; i < KEY_CHAINS_TOTAL_AMOUNT; i++){
			keyShares[i] = new KeyShare();
		}		
		hashFromHost = new byte[SHA_HASH_SIZE];
		aesHelper = new AesHelper();	
	}
		
	public void setKeyChain(APDU apdu){
        byte[] buffer = apdu.getBuffer();
        short numBytes = (short) (buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        
        byte endOfKeyChainTransmission = buffer[ISO7816.OFFSET_P1];
        short index = (short) (buffer[ISO7816.OFFSET_P2] & 0xFF);
        
        if(numBytes != byteRead){
	        ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        if(index < 0 || index > KEY_CHAINS_TOTAL_AMOUNT){
	        ISOException.throwIt(Constants.SW_WRONG_KEY_CHAIN_INDEX);
        }
        
        KeyShare keyShare = keyShares[index];
        
        if (endOfKeyChainTransmission != 0) {
        	if (numBytes != SHA_HASH_SIZE) {
	        	ISOException.throwIt(Constants.SW_WRONG_HASH_SIZE);
        	}
        	
        	Util.arrayCopyNonAtomic(buffer, (short)(ISO7816.OFFSET_CDATA), 
									hashFromHost, (short)0x00, SHA_HASH_SIZE);
									
			keyShare.makeHash();
				
			byte res = Util.arrayCompare(hashFromHost, (short) 0x00, 
						keyShare.getHash(), (short) 0x00, SHA_HASH_SIZE);
						
			if (res != 0)	{
	        	ISOException.throwIt(Constants.SW_WRONG_HASH);
        	}
        	       	
        	encryptKeyShare(keyShare);
        
        	keyShare.setIsWellFormed(true);	        						        	
        }
        else {
        	short start = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
        	
        	if (start == 0x00)
        		keyShare.resetKeyShare();
	        	
	        short len = (short)(byteRead - 2); 
	        
	        if(start < 0 || len <= 0 || (short)(start + len) > MAX_KEY_SHARE_SIZE){
				ISOException.throwIt(Constants.SW_WRONG_KEY_SHARE_START_OR_LEN);
			}
			
			keyShare.incLen(len);
			
			Util.arrayCopyNonAtomic(buffer, (short)(ISO7816.OFFSET_CDATA + 2), 
					keyShare.getKeyShareBytes(), start, len);    
        }           
    }
    
    private void encryptKeyShare(KeyShare keyShare){
	    aesHelper.getCipherEnc().doFinal(keyShare.getKeyShareBytes(), (short) 0x00, MAX_KEY_SHARE_SIZE, 
				keyShare.getKeyShareBytes(), (short) 0x00);	
        	
        Util.setShort(keyShare.getEncryptedLen(), (short) 0x00, keyShare.getLen());
        	
        aesHelper.getCipherEnc().doFinal(keyShare.getEncryptedLen(), (short) 0x00, AES_BLOCK_LEN, 
			keyShare.getEncryptedLen(), (short) 0x00);
				
		keyShare.resetLen();
    }
    
    
    public void getKeyChainLen(APDU apdu){
     	byte[] buffer = apdu.getBuffer();
     	byte index = buffer[ISO7816.OFFSET_P2];
        
        if(index < 0 || index > KEY_CHAINS_TOTAL_AMOUNT){
	        ISOException.throwIt(Constants.SW_WRONG_KEY_CHAIN_INDEX);
        }
        
        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        
        if (numBytes != byteRead || numBytes != PIN_LENGTH){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        PinHandler.checkPin(buffer, ISO7816.OFFSET_CDATA, PIN_LENGTH);	    
	    PinHandler.resetPinFlag();
	    
	    KeyShare keyShare = keyShares[index];
	    
	    printArray(keyShare.getEncryptedLen(), (short)0, AES_BLOCK_LEN); 
	    
	    aesHelper.getCipherDec().doFinal(keyShare.getEncryptedLen(), (short) 0x00, AES_BLOCK_LEN, 
				buffer, (short) 0x00);
		        
      //  Util.setShort(buffer, (short) 0x00, keyShares[index].getLen());
        
        apdu.setOutgoingAndSend((short) 0x00, (short) 0x02);
    }

	
	public void getKeyChainPart(APDU apdu){
		byte[] buffer = apdu.getBuffer();
    	
    	short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        
        if (numBytes != byteRead || numBytes < PIN_LENGTH){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
	    PinHandler.checkPin(buffer, ISO7816.OFFSET_CDATA, PIN_LENGTH);	    
	    PinHandler.resetPinFlag();
     		
        byte endOfKeyChainTransmission = buffer[ISO7816.OFFSET_P1];
        short index = (short) (buffer[ISO7816.OFFSET_P2] & 0xFF);
        
        if(index < 0 || index > KEY_CHAINS_TOTAL_AMOUNT){
	        ISOException.throwIt(Constants.SW_WRONG_KEY_CHAIN_INDEX);
        }
        
        KeyShare keyShare = keyShares[index];
                     
        if (!keyShare.isWellFormed()) {
	        ISOException.throwIt(Constants.SW_BAD_FORMED_KEY_SHARE);
        }
        
        if (endOfKeyChainTransmission != 0) {						
			short le = apdu.setOutgoing(); 
			if (le != SHA_HASH_SIZE){
	        	ISOException.throwIt(Constants.SW_WRONG_HASH_SIZE);
        	}
        	apdu.setOutgoingLength(le);
        	apdu.sendBytesLong(keyShare.getHash(), (short)0x00, le); 
        }
        else {
        	printArray(keyShare.getKeyShareBytes(), (short)0, MAX_KEY_SHARE_SIZE); 
			      	
	        short start = Util.getShort(buffer, (short)(ISO7816.OFFSET_CDATA + PIN_LENGTH));
	      	       
	        short le = apdu.setOutgoing();
	       
	        if((start % KEY_SHARE_SLICE_LEN != 0) || (start < 0) || (le <= 0) 
				 || le > KEY_SHARE_SLICE_LEN || (short)(start + le) > MAX_KEY_SHARE_SIZE){
				 ISOException.throwIt(Constants.SW_WRONG_KEY_SHARE_START_OR_LEN);
		    } 
		   
		    aesHelper.getCipherDec().doFinal(keyShare.getKeyShareBytes(), start, KEY_SHARE_SLICE_LEN, 
				buffer, (short) 0x00);
			
			printArray(buffer, (short)0, KEY_SHARE_SLICE_LEN); 		
		   
		    apdu.setOutgoingLength(le);
		    apdu.sendBytes((short) 0x00, le); /**/ 		   
		  // apdu.sendBytesLong(keyShare.getKeyShareBytes(), start, le);  
        }        
     }        
}	


