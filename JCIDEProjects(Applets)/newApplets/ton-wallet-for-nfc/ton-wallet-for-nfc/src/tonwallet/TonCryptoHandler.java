package tonwallet ;

import javacard.framework.*;
import javacard.framework.APDU;
import javacard.security.*;
import com.ftsafe.security.*;
//import com.ftsafe.javacard.debug.DebugHelper;

import com.ftsafe.javacardx.btcmgr.CoinManager;

public class TonCryptoHandler extends CryptoHandler {
	
	public final static short TON_PUBLIC_KEY_SIZE = (short) 32;
	public final static short CHAIN_CODE_KEY_SIZE = (short) 32;
	public final static short TON_CHAIN_CODE_CONSTANT = (short) 65;
	
	public final static short KEY_BUF_SIZE = (short) 100;
	public final static short KEY_SIZE = (short) 32;
	public final static short SIGNATURE_SIZE = (short) 64;
	
	public final static byte VERIFY_RESPONSE_LEN = 0x01;
		
	private byte[] keyBuf = new byte[KEY_BUF_SIZE];
	
	private XECPublicKey publicKey;
    private NamedParameterSpec params;
    
	
	// m/44’/171’/0’/0’/0’
	private byte[] rootPath = new byte[] {0x6D,0x2F,0x34,0x34,0x27,0x2F,0x31,0x37,0x31,0x27,0x2F,0x30,0x27,0x2F,0x30,0x27,0x2F,0x30,0x27};
	private byte[] rootPathInLV = new byte[] {0x13, 0x6D,0x2F,0x34,0x34,0x27,0x2F,0x31,0x37,0x31,0x27,0x2F,0x30,0x27,0x2F,0x30,0x27,0x2F,0x30,0x27};
	    
    private Signature sig;
    
    public TonCryptoHandler (){
    	super();
       
        params = NamedParameterSpec.getInstance(NamedParameterSpec.ED25519);
				
		short attributes;
		attributes = KeyBuilderX.ATTR_PUBLIC;
		attributes |= JCSystem.MEMORY_TYPE_PERSISTENT;
		publicKey = (XECPublicKey) KeyBuilderX.buildXECKey(params, attributes, false); 
		
		sig = Signature.getInstance((byte) 0x00, SignatureX.SIG_CIPHER_EDDSA, (byte) 0x00, false);			
    }
    
  
           
     /**
     * This function returns the public key for given bip32 HD path.
     This HD path is a hex representation of string having a form m/44’/171’/0’/0’/ind’.
     Only 'ind' is changed for TON.
     
     * cla: 0xB0
     * ins: 0xA0
     * p1: 0x00
     * p2: 0x00
     * Lc: length of hex representation of HD path + 1
     * data: length of hex representation of HD path | hex representation of HD path
     * Response data:  TON public key bytes
     */
    
    public void getPublicKey(APDU apdu){
        byte[] buffer = apdu.getBuffer();

        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead ){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        short ret = CoinManager.getCoinPubData(buffer, (short) (ISO7816.OFFSET_CDATA + 1), (short) (numBytes - 1),
			 buffer, (short) 0x00, buffer, TON_CHAIN_CODE_CONSTANT);
        
        if (ret != 0x00) {
           ISOException.throwIt(Constants.SW_GET_COIN_PUB_DATA_FAILED);
        }
              
        apdu.setOutgoingAndSend((short) 0x00, (short) (/*65 + */ TON_PUBLIC_KEY_SIZE));
    }
    
    /**
     * This function retrieves public key from CoinManager for fixed bip32 HD path 
     m/44’/171’/0’/0’/0’.
     
     * cla: 0xB0
     * ins: 0xA7
     * p1: 0x00
     * p2: 0x00
     * Response data:  TON public key bytes
     */
    public void getPublicKeyWithDefaultHDPath(APDU apdu){
    	byte[] buf = apdu.getBuffer();
        
        short ret = CoinManager.getCoinPubData(rootPath, (short) 0, (short) rootPath.length, buf, (short) 0, buf, TON_CHAIN_CODE_CONSTANT);
        if (ret != 0x00) {
           ISOException.throwIt(Constants.SW_GET_COIN_PUB_DATA_FAILED);
        }
               
        apdu.setOutgoingAndSend((short) 0, (short) (/*65 +*/ TON_PUBLIC_KEY_SIZE));
     }
    
    /**
     * This function signs the message from apdu buffer for given bip32 HD path.
      This HD path is a hex representation of string having a form m/44’/171’/0’/0’/ind’.
      
      !!!!Important note: here signature for now is supposed to have format that is produced by
       CoinManager: 0x41 + 64bytes of signature + 0xD9
      
     * cla: 0xB0
     * ins: 0xA3
     * p1: 0x00
     * p2: 0x00
     * Lc: total len of data 
     * Le: SIGNATURE_SIZE + 2 // 0x42
     * data: [messageLength (2 bytes) | message | pathLength  (1 byte) | path]
     //example: 00 04 01 01 01 01 13 6D 2F 34 34 27 2F 31 37 31 27 2F 30 27 2F 30 27 2F 30 27
     
     * Response data: [signatureLength | signature]
     */

    public void signShortMessage(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
 
    	short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead ){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
    	        
        short dataOffset = ISO7816.OFFSET_CDATA;
        short dataLen = (short)(Util.getShort(buffer, ISO7816.OFFSET_CDATA) + 2);//(short)(buffer[ISO7816.OFFSET_CDATA] + 1); // 
        
        short pathOffset = (short)(dataOffset + dataLen);
        short pathLen = (short)(buffer[pathOffset] + 1);
        
        short result = CoinManager.signCoinDataED25519((short) 0x01, buffer, dataOffset, dataLen, tmpBuffer, (short) 0x00,  buffer, pathOffset, pathLen);
        
        if (result != 0x00) {
           ISOException.throwIt( Constants.SW_SIGN_DATA_FAILED);
        }
        
        short le = apdu.setOutgoing();
        
        if (le != (short)(SIGNATURE_SIZE + 2)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        apdu.setOutgoingLength((short)(SIGNATURE_SIZE + 2));
        apdu.sendBytesLong(tmpBuffer, (short) 0, (short)(SIGNATURE_SIZE + 2));        
    }
    
    /**
     * This function signs the message stored in dataForSigning set by SET_DATA_FOR_SIGNING 
     command for given bip32 HD path. It is used to sign long messages. 
	 This HD path is a hex representation of string having a form m/44’/171’/0’/0’/ind’.
	 
	   !!!!Important note: here signature for now is supposed to have format that is produced by
       CoinManager: 0x41 + 64bytes of signature + 0xD9
     
     * cla: 0xB0
     * ins: 0xA4
     * p1: 0x00
     * p2: 0x00
     * Lc: total len of data
     * Le: SIGNATURE_SIZE + 2 // 0x42
     * data: [pathLength | path]
     * Response data: [signatureLength | signature]
     */
    
    public void signStoredMessage(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
    	
    	short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead ){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        short pathOffset = ISO7816.OFFSET_CDATA;
        short pathLen = numBytes;
        
        short result = CoinManager.signCoinDataED25519((short) 0x01, dataForSigning, (short) 0x00, dataForSigningTotalLen, tmpBuffer, (short) 0x00,  buffer, pathOffset, pathLen);
        
        if (result != 0x00) {
           ISOException.throwIt( Constants.SW_SIGN_DATA_FAILED);
        }
        
        short le = apdu.setOutgoing();
        if (le != (short)(SIGNATURE_SIZE + 2)) {
             ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        apdu.setOutgoingLength((short)(SIGNATURE_SIZE + 2));
        apdu.sendBytesLong(tmpBuffer, (short) 0x00, (short)(SIGNATURE_SIZE + 2));         
    }
    
     /**
     * This function signs the message from apdu buffer for default bip32 HD path
      m/44’/171’/0’/0’.
      
      !!!!Important note: here signature for now is supposed to have format that is produced by
       CoinManager: 0x40 + 64bytes of signature + 0x00
      
     * cla: 0xB0
     * ins: 0xA5
     * p1: 0x00
     * p2: 0x00
     * lc: message len + 1
     * Le: SIGNATURE_SIZE + 2 // 0x42
     * data: [messageLength (2bytes)| message] // like : 00 04 01 01 01 01
     * Response data: [signatureLength | signature]
     */

    public void signShortMessageWithDefaultPath(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
    	
    	short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead ){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        short dataOffset = ISO7816.OFFSET_CDATA;
        short dataLen = (short)(Util.getShort(buffer, ISO7816.OFFSET_CDATA) + 2);
        
        short result = CoinManager.signCoinDataED25519((short) 0x01, buffer, dataOffset, dataLen, tmpBuffer, (short) 0x00,  rootPathInLV, (short) 0x00, (short) rootPathInLV.length);

        if (result != 0x00) {
           ISOException.throwIt(Constants.SW_SIGN_DATA_FAILED);
        }
        
        short le = apdu.setOutgoing();
        if (le != (short)(SIGNATURE_SIZE + 2)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        apdu.setOutgoingLength((short)(SIGNATURE_SIZE + 2));
        apdu.sendBytesLong(tmpBuffer, (short) 0x00, (short)(SIGNATURE_SIZE + 2));
    }
    
    /**
     * This function signs the message stored in dataForSigning set by SET_DATA_FOR_SIGNING 
     command for default bip32 HD path m/44’/171’/0’/0’.
     
     !!!!Important note: here signature for now is supposed to have format that is produced by
       CoinManager: 0x41 + 64bytes of signature + 0xD9
     
     * cla: 0xB0
     * ins: 0xA6
     * p1: 0x00
     * p2: 0x00
     * Le: SIGNATURE_SIZE + 2 // 0x42
     * Response data: [signatureLength | signature]
     */
    public void signStoredMessageWithDefaultPath(APDU apdu){
        short result = CoinManager.signCoinDataED25519((short) 0x01, dataForSigning, (short) 0x00, dataForSigningTotalLen, tmpBuffer, (short) 0x00, rootPathInLV, (short) 0x00, (short) rootPathInLV.length);
        
        if (result != 0x00) {
           ISOException.throwIt(Constants.SW_SIGN_DATA_FAILED);
        }
        
        short le = apdu.setOutgoing();
        if (le != (short)(SIGNATURE_SIZE + 2)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        apdu.setOutgoingLength((short)(SIGNATURE_SIZE + 2));
        apdu.sendBytesLong(tmpBuffer, (short) 0x00, (short)(SIGNATURE_SIZE + 2)); 
    }
    
        
    /**
     * This function was done to test CoinManager and signature verification.
	   It verifies the signature for specified bip32 HD path. 
	   This HD path is a hex representation of string having a form m/44’/171’/0’/0’/ind’.
	   It will work only for short dataForSigning (< 200 bytes).
	   
	   !!!!Important note: here signature for now is supposed to have format that is produced by
       CoinManager: 0x41 + 64bytes of signature + 0xD9
     
     * cla: 0xB0
     * ins: 0xD0
     * p1: 0x00
     * p2: 0x00
     * Lc: total len of data
     * data: [dataLength|data|signatureLength|signature|pathLength|path]
     * Le: 0x01
     * Response data: byte 0x01 if signature is correct, 0x00 otherwise
     */
     // here signature for now is supposed to have format that is produced by Coinmanager: 0x41 + 64bytes of signature + 0x35
    
    public void verifySignature(APDU apdu){
        byte[] buffer = apdu.getBuffer();

        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead && numBytes < (short)(SIGNATURE_SIZE + 3)){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        
        short dataForSigningLen = (short) buffer[ISO7816.OFFSET_CDATA];
			short dataForSigningStartPos = (short)(ISO7816.OFFSET_CDATA + 1);
			short signatureStartPos = (short)(dataForSigningStartPos + dataForSigningLen + 1);
			short pathStartPos = (short)(signatureStartPos + SIGNATURE_SIZE + 2);
			short pathLength = (short) buffer[signatureStartPos + SIGNATURE_SIZE + 1]; // this is because we get signature from coinmanager now and it containd redundant byte 0x35 at the end (we skip it)
			
		short ret = CoinManager.getCoinPubData(buffer, pathStartPos, pathLength, keyBuf, (short) 0x00, null, TON_CHAIN_CODE_CONSTANT);
        if (ret != 0x00) {
           ISOException.throwIt(Constants.SW_GET_COIN_PUB_DATA_FAILED);
        }
                                
	    try
        {
			publicKey.setEncoded(keyBuf, (short) 1, TON_PUBLIC_KEY_SIZE);
			sig.init(publicKey, Signature.MODE_VERIFY);
			

			boolean mark = sig.verify(buffer, dataForSigningStartPos, dataForSigningLen, buffer, signatureStartPos, SIGNATURE_SIZE);                                                                 
			
			buffer[0] = mark == true ? (byte) 0x01 : (byte) 0x00;
			apdu.setOutgoingAndSend((short) 0x00, VERIFY_RESPONSE_LEN);
        }
        catch(CryptoException e)
        {
            ISOException.throwIt(e.getReason());
        }   
    }
    
    /**
     * This function was done to test CoinManager and signature verification.
       It verifies the signature for default bip32 HD path m/44’/171’/0’/0’.
       It will work only for short dataForSigning (< 200 bytes)
       
       !!!!Important note: here signature for now is supposed to have format that is produced by
       CoinManager: 0x41 + 64bytes of signature + 0x35
     
     * cla: 0xB0
     * ins: 0xD1
     * p1: 0x00
     * p2: 0x00
     * Lc: total len of data
     * data: [dataForSigningLength|dataForSigning|signatureLength|signature]
     * Le: 0x01
     * Response data: byte 0x01 if signature is correct, 0x00 otherwise
     */
     
    public void verifySignatureWithDefaultPath(APDU apdu){
	    byte[] buffer = apdu.getBuffer();

        short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead && numBytes < (short)(SIGNATURE_SIZE + 3)){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
                        
        short ret = CoinManager.getCoinPubData(rootPath, (short) 0x00, (short) rootPath.length, keyBuf, (short) 0x00, null, TON_CHAIN_CODE_CONSTANT);
        if (ret != 0) {
           ISOException.throwIt(Constants.SW_GET_COIN_PUB_DATA_FAILED);
        }
     
	    try
        {
			publicKey.setEncoded(keyBuf, (short) 1, TON_PUBLIC_KEY_SIZE);
			sig.init(publicKey, Signature.MODE_VERIFY);
			
			short dataForSigningLen = (short) buffer[ISO7816.OFFSET_CDATA];
			short dataForSigningStartPos = (short)(ISO7816.OFFSET_CDATA + 1);
			short signatureStartPos = (short)(dataForSigningStartPos + dataForSigningLen + 1);
			
			boolean mark = sig.verify(buffer, dataForSigningStartPos, dataForSigningLen, buffer, signatureStartPos, SIGNATURE_SIZE);                                                                 
			
			buffer[0] = mark == true ? (byte) 0x01 : (byte) 0x00;
			apdu.setOutgoingAndSend((short) 0x00, VERIFY_RESPONSE_LEN);
        }
        catch(CryptoException e)
        {
            ISOException.throwIt(e.getReason());
        }   
    }
   
}

