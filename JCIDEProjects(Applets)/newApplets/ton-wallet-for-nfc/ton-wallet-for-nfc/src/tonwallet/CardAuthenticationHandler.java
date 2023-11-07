package tonwallet ;

import javacard.framework.*;
import static tonwallet.Constants.*;
import static tonwallet.Utils.*;


public class CardAuthenticationHandler  
{
	private static short PASSWORD_SIZE = 128;
	
    private byte[] password;    
    private AesHelper aesHelper;
   
    public CardAuthenticationHandler(){
	     password = new byte[PASSWORD_SIZE];  
	     aesHelper = new AesHelper();
    }
    
    public void setPassword(APDU apdu){
        byte[] buffer = apdu.getBuffer();
        short numBytes = (short) (buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        
        if((numBytes != byteRead) || (numBytes != PASSWORD_SIZE)){
	        ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
                      
		printArray(buffer, ISO7816.OFFSET_CDATA, PASSWORD_SIZE);	
        
        aesHelper.getCipherEnc().doFinal(buffer, ISO7816.OFFSET_CDATA, PASSWORD_SIZE, 
				password, (short) 0x00);
		
		printArray(password, (short) 0x00, PASSWORD_SIZE);		
				                            		       
        //passwordLen = numBytes;
        
        //Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, 
			//password, (short)0, PASSWORD_SIZE);     
    }
    
    public void verifyPassword(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
        short numBytes = (short) (buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        
        if((numBytes != byteRead) || (numBytes != PASSWORD_SIZE)){
	        ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        printArray(buffer, ISO7816.OFFSET_CDATA, PASSWORD_SIZE);
        
        aesHelper.getCipherEnc().doFinal(buffer, ISO7816.OFFSET_CDATA, PASSWORD_SIZE, 
				buffer, ISO7816.OFFSET_CDATA);
        
		printArray(buffer, ISO7816.OFFSET_CDATA, PASSWORD_SIZE);
		
        byte res = Util.arrayCompare(password, (short) 0, buffer, ISO7816.OFFSET_CDATA, PASSWORD_SIZE);
        
        if(res != 0) {
	        ISOException.throwIt(SW_INCORRECT_PASSWORD_FOR_CARD_AUTHENICATION);
        }
        
        //clear password
        Util.arrayFillNonAtomic(password, (short) 0, PASSWORD_SIZE, (byte)0);
    }
        
    /*public void getPassword(APDU apdu){
	    PinHandler.checkPin(apdu);
	    PinHandler.resetPinFlag();
	     
	    byte[] buffer = apdu.getBuffer();       
        Util.arrayCopyNonAtomic(password, (short)0, 
			buffer, (short)0, passwordLen);
			
		apdu.setOutgoingAndSend((short) 0x00, passwordLen);    
     }*/
}
