package tonwallet ;

import javacard.framework.*;

public abstract class CryptoHandler  {
	
	public final static short DATA_FOR_SIGNING_MAX_SIZE_IN_BYTES = (short) 2048;
    public final static short SIGNATURE_MAX_SIZE_IN_BYTES  = (short) 100;
    public final static short TMP_BUFFER_SIZE = (short) 512;
    public final static short HASH_SIZE = (short) 32;
   
    protected byte[] dataForSigning;
    protected byte[] tmpBuffer;
    
	protected short sigLen = 0x00;
    protected short numberOfBytesReceived = 0x00;
    protected short dataForSigningTotalLen = 0x00;
    
    protected short numberOfKeyShareBytesReceived = 0x00;
    protected short numberOfKeyShareBytesSent = 0x00;
    protected short remainingLen = 0x00;
    
	public CryptoHandler(){       
        try {
            dataForSigning = JCSystem.makeTransientByteArray(DATA_FOR_SIGNING_MAX_SIZE_IN_BYTES, JCSystem.CLEAR_ON_DESELECT);
        } catch (SystemException e) {
            dataForSigning = new byte[DATA_FOR_SIGNING_MAX_SIZE_IN_BYTES];
        }
        
        try {
            tmpBuffer = JCSystem.makeTransientByteArray(TMP_BUFFER_SIZE, JCSystem.CLEAR_ON_DESELECT);
        } catch (SystemException e) {
            tmpBuffer = new byte[TMP_BUFFER_SIZE];
        }      
    }
    
    /**
     * This function receives message for signing. 
     If data length is > 255 bytes we should call multiple times.
        CLA: 0xB0
        INS: 0xC0
        P1: 0x00 (otherwise) or 0x01 (the only or last data block)
        P2: 0x00
        Lc: Length of data field
        Data: Data portion for signing (<= 255 bytes)
     */
    
    public void setDataForSigning(APDU apdu){
        byte[] buffer = apdu.getBuffer();
        short numBytes = (short) (buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        
        if(numberOfBytesReceived == 0x00){
	        numberOfBytesReceived = 0x02;
        }

        if (numBytes != byteRead ||  numberOfBytesReceived > DATA_FOR_SIGNING_MAX_SIZE_IN_BYTES){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, dataForSigning, numberOfBytesReceived, numBytes);

        byte endOfMessage = buffer[ISO7816.OFFSET_P1];
        numberOfBytesReceived += numBytes;
        
        if(endOfMessage == (byte) 0x01){
            dataForSigningTotalLen = numberOfBytesReceived;
            
            if (dataForSigningTotalLen > DATA_FOR_SIGNING_MAX_SIZE_IN_BYTES)
                ISOException.throwIt(Constants.SW_DATA_FOR_SIGNING_IS_TOO_LONG);
                
            
            Util.setShort(dataForSigning, (short) 0x00, (short)(dataForSigningTotalLen - 2));

			//com.ftsafe.javacard.debug.DebugHelper.print(dataForSigning[0]);
			//com.ftsafe.javacard.debug.DebugHelper.print(dataForSigning[1]);

            numberOfBytesReceived = 0; //end of receiving and ready for another message
        } 
     }
   
     public abstract void getPublicKey(APDU apdu);
     public abstract void getPublicKeyWithDefaultHDPath(APDU apdu);

     public abstract void signShortMessage(APDU apdu);
     public abstract void signStoredMessage(APDU apdu);
     public abstract void signShortMessageWithDefaultPath(APDU apdu);
     public abstract void signStoredMessageWithDefaultPath(APDU apdu);
     
     public abstract void verifySignature(APDU apdu);
     public abstract void verifySignatureWithDefaultPath(APDU apdu);
}

