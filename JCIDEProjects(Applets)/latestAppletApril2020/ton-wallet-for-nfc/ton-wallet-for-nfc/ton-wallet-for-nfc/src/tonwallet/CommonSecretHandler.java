package tonwallet ;

import javacard.framework.*;
import javacard.security.*;
import static tonwallet.Constants.*;

public class CommonSecretHandler  
{	
	private byte[] commonSecret;
	
	private boolean isCommonSecretSet = false;
	
	private MessageDigest md;
	
	private static CommonSecretHandler commonSecretHandler;
	
	public static CommonSecretHandler getInstance() {
		if (commonSecretHandler == null)
			commonSecretHandler = new CommonSecretHandler();
		return commonSecretHandler;
	}
	
	private CommonSecretHandler() {
		commonSecret = new byte[COMMON_SECRET_SIZE];
		md = MessageDigest.getInstance(MessageDigest.ALG_SHA_256, false);
	}
	
	public boolean isCommonSecretSet() {
		return isCommonSecretSet;
	}
	
	public void setCommonSecret(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short numBytes = (short) (buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        
        if((numBytes != byteRead) || (numBytes != COMMON_SECRET_SIZE)) {
	        ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
									                            		             
        Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, 
			commonSecret, (short) 0x00, COMMON_SECRET_SIZE);
		
		isCommonSecretSet = true;    
    }
    
    public byte[] getCommonSecret() {
	    return commonSecret;
    }
    
    public void getHashOfCommonSecret(APDU apdu) {
	    byte[] buffer = apdu.getBuffer(); 
	    short le = apdu.setOutgoing();
		if (le != SHA_HASH_SIZE) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		}    
	    md.doFinal(commonSecret, (short)0x00, COMMON_SECRET_SIZE, buffer, (short) 0x00);	  					
		apdu.setOutgoingLength(SHA_HASH_SIZE);
		apdu.sendBytes((short) 0x00, SHA_HASH_SIZE);   
    }
}
