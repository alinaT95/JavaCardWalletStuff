package wallet ;

import javacard.framework.*;

public abstract class CryptoHandler  {
	public final static short KEY_SIZE = (short) 32;
	public final static short DATA_FOR_SIGNING_MAX_SIZE_IN_BYTES = (short) 1024;
    public final static short SIGNATURE_MAX_SIZE_IN_BYTES  = (short) 100;
    public final static short TMP_BUFFER_SIZE = (short) 512;
    
    // public final static byte GENERATE_WITHOUT_SEED = (byte) 0x00;
    // public final static byte GENERATE_FROM_USER_SEED = (byte) 0x01;
    // public final static byte GENERATE_WITH_SEED_CREATION = (byte) 0x02;

	// Buffer for storing extended APDUs
    protected byte[] dataForSigning;
    protected byte[] signatureBytes;
    protected byte[] tmpBuffer;

	protected short sigLen = 0;
    protected short numberOfBytesReceived = 0;
    protected short dataForSigningTotalLen = 0;
    
	public CryptoHandler(){
        try {
            signatureBytes = JCSystem.makeTransientByteArray(SIGNATURE_MAX_SIZE_IN_BYTES, JCSystem.CLEAR_ON_DESELECT);
        } catch (SystemException e) {
            signatureBytes = new byte[SIGNATURE_MAX_SIZE_IN_BYTES];
        }
        
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
     * This function receives message for signing
        CLA: 0xB0
        INS: 0xF8
        P1: 0x00 (otherwise) or 0x01 (the only or last data block)
        P2: 0x00
        Lc: Length of data portion for signing (<= 255 bytes)
        Data: Data portion for signing
     */
    ;
    public void setDataForSigning(APDU apdu){
    	PinHandler.checkPinWithoutReset();
    	
        byte[] buffer = apdu.getBuffer();
        short numBytes = (short) (buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, dataForSigning, numberOfBytesReceived, numBytes);

        byte endOfMessage = buffer[ISO7816.OFFSET_P1];
        numberOfBytesReceived += numBytes;

        if(endOfMessage == (byte) 1){
            dataForSigningTotalLen = numberOfBytesReceived;

            if (dataForSigningTotalLen > DATA_FOR_SIGNING_MAX_SIZE_IN_BYTES)
                ISOException.throwIt(Constants.SW_DATA_FOR_SIGNING_IS_TOO_LONG);

            numberOfBytesReceived = 0; //end of receiving and ready for another message
            
            PinHandler.resetPin();
        }     
    }
    
    public void importSeed(APDU apdu) {
        PinHandler.checkPin();
        byte[] buffer = apdu.getBuffer();
        importSeed(buffer);
    }
    
    public abstract void setupKeys(APDU apdu); 
    // {
        // byte[] buffer = apdu.getBuffer();
        // switch (buffer[ISO7816.OFFSET_P1]) {
            // case GENERATE_WITHOUT_SEED:
                // setupMasterKeyWithoutSeed();
                // break;
            // case GENERATE_FROM_USER_SEED:
                // importSeed(buffer);
                // break;
            // case GENERATE_WITH_SEED_CREATION:
                // setupMasterKeyFromRandomSeed();
                // break;
            // default:
                // ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        // }
    // }


    public abstract void importPublicKey(APDU apdu);
    
    public abstract void importPrivateKey(APDU apdu);

    public abstract void getPublicMasterKey(APDU apdu);

    public abstract void signMessage(APDU apdu);
    
    public abstract void verifySignature(APDU apdu);

    public abstract void setupMasterKeyWithoutSeed();

    public abstract void setupMasterKeyFromRandomSeed();

    public abstract void importSeed(byte[] buffer);

}
