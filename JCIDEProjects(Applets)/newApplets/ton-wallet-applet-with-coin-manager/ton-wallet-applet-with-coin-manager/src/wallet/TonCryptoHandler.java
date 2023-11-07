package wallet ;

import javacard.framework.*;
import javacard.framework.APDU;
import javacard.security.*;
import com.ftsafe.security.*;

import com.ftsafe.javacardx.btcmgr.CoinManager;

public class TonCryptoHandler extends CryptoHandler
{
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
     * Lc: length of hex representation of HD path
     * data: hex representation of HD path
     * Le: 0x20
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
        
        short le = apdu.setOutgoing();
        if (le != TON_PUBLIC_KEY_SIZE) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
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
     * Le: 0x20
     * Response data:  TON public key bytes
     */
    public void getPublicKeyWithDefaultHDPath(APDU apdu){
        byte[] buffer = apdu.getBuffer();
        
        short le = apdu.setOutgoing();
        if (le != TON_PUBLIC_KEY_SIZE) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        short ret = CoinManager.getCoinPubData(rootPath, (short) 0x00, (short) rootPath.length, 
				buffer, (short) 0x00, buffer, TON_CHAIN_CODE_CONSTANT);
				
        if (ret != 0x00) {
           ISOException.throwIt(Constants.SW_GET_COIN_PUB_DATA_FAILED);
        }
        
        apdu.setOutgoingAndSend((short) 0x00, (short) (/*65 +*/ TON_PUBLIC_KEY_SIZE));
    }
    
    /**
     * This function signs the message from apdu buffer for given bip32 HD path.
      This HD path is a hex representation of string having a form m/44’/171’/0’/0’/ind’.
      
     * cla: 0xB0
     * ins: 0xA3
     * p1: 0x00
     * p2: 0x00
     * Lc: total len of data 
     * Le: SIGNATURE_SIZE + 2 // 66
     * data: [messageLength | message | pathLength | path]
     
     * Response data: [signatureLength | signature]
     */

    public void signShortMessage(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
 
    	short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead ){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
    	
        UserInterface.showWorking();
        
        short dataOffset = ISO7816.OFFSET_CDATA;
        short dataLen = (short)(buffer[ISO7816.OFFSET_CDATA] + 1); // (byte)(buf[lcOff] & 0xFF + 1);
        
        short pathOffset = (short)(dataOffset + dataLen);
        short pathLen = (short)(buffer[pathOffset] + 1);
        
        short result = CoinManager.signCoinData((short) 0x01, buffer, dataOffset, dataLen, tmpBuffer, (short) 0x00,  buffer, pathOffset, pathLen);
        
        if (result != 0x00) {
           ISOException.throwIt( Constants.SW_SIGN_DATA_FAILED);
        }
        
        UserInterface.showMainUI(true);
        
        short le = apdu.setOutgoing();
        if (le != (short)(SIGNATURE_SIZE + 2)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        apdu.setOutgoingLength((short)(SIGNATURE_SIZE + 2));
        apdu.sendBytesLong(tmpBuffer, (short) 0, (short)(SIGNATURE_SIZE + 2));
        
        UserInterface.showMainUI(true); 
    }
    
    /**
     * This function signs the message stored in dataForSigning set by SET_DATA_FOR_SIGNING 
     command for given bip32 HD path. It is used to sign long messages. 
	 This HD path is a hex representation of string having a form m/44’/171’/0’/0’/ind’.
     
     * cla: 0xB0
     * ins: 0xA4
     * p1: 0x00
     * p2: 0x00
     * Lc: total len of data
     * Le: SIGNATURE_SIZE + 2 // 66
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
        
        UserInterface.showWorking();
        
        short pathLen = (byte)(buffer[ISO7816.OFFSET_CDATA + Constants.PIN_LENGTH] & 0xFF);
        short pathOffset = (short)(ISO7816.OFFSET_CDATA + Constants.PIN_LENGTH + 1);
        
        short result = CoinManager.signCoinData((short) 0x00, dataForSigning, (short) 0x00, dataForSigningTotalLen, tmpBuffer, (short) 0x00,  buffer, pathOffset, pathLen);
        
        if (result != 0x00) {
           ISOException.throwIt( Constants.SW_SIGN_DATA_FAILED);
        }
        
        short le = apdu.setOutgoing();
        if (le != (short)(SIGNATURE_SIZE + 2)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        apdu.setOutgoingLength((short)(SIGNATURE_SIZE + 2));
        apdu.sendBytesLong(tmpBuffer, (short) 0x00, (short)(SIGNATURE_SIZE + 2));
        
        UserInterface.showMainUI(true); 
    }
    
     /**
     * This function signs the message from apdu buffer for default bip32 HD path
      m/44’/171’/0’/0’.
      
     * cla: 0xB0
     * ins: 0xA5
     * p1: 0x00
     * p2: 0x00
     * lc: message len + 1
     * Le: SIGNATURE_SIZE + 2 // 66
     * data: [messageLength| message]
     * Response data: [signatureLength | signature]
     */

    public void signShortMessageWithDefaultPath(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
    	
    	short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();

        if (numBytes != byteRead ){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        UserInterface.showWorking();
        
        short dataLen = (short)(buffer[ISO7816.OFFSET_CDATA] + 1);//(buf[ISO7816.OFFSET_CDATA] & 0xFF);
        short dataOffset = (short)(ISO7816.OFFSET_CDATA);
        
        short result = CoinManager.signCoinData((short) 0x01, buffer, dataOffset, dataLen, tmpBuffer, (short) 0x00,  rootPathInLV, (short) 0x00, (short)rootPathInLV.length);

        if (result != 0x00) {
           ISOException.throwIt( Constants.SW_SIGN_DATA_FAILED);
        }
        
        short le = apdu.setOutgoing();
        if (le != (short)(SIGNATURE_SIZE + 2)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        apdu.setOutgoingLength((short)(SIGNATURE_SIZE + 2));
        apdu.sendBytesLong(tmpBuffer, (short) 0x00, (short)(SIGNATURE_SIZE + 2));
        
        UserInterface.showMainUI(true);
    }
    
    /**
     * This function signs the message stored in dataForSigning set by SET_DATA_FOR_SIGNING 
     command for default bip32 HD path m/44’/171’/0’/0’.
     
     * cla: 0xB0
     * ins: 0xA6
     * p1: 0x00
     * p2: 0x00
     * Le: SIGNATURE_SIZE + 2 // 66
     * Response data: [signatureLength | signature]
     */
    
    public void signStoredMessageWithDefaultPath(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
        
        UserInterface.showWorking();
        
        short result = CoinManager.signCoinData((short) 0x00, dataForSigning, (short) 0x00, dataForSigningTotalLen, signatureBytes, (short) 0x00,  rootPath, (short) 0x00, (short) rootPath.length);
        
        if (result != 0x00) {
           ISOException.throwIt(Constants.SW_SIGN_DATA_FAILED);
        }
        
        short le = apdu.setOutgoing();
        if (le != (short)(SIGNATURE_SIZE + 2)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        apdu.setOutgoingLength((short)(SIGNATURE_SIZE + 2));
        apdu.sendBytesLong(signatureBytes, (short) 0x00, (short)(SIGNATURE_SIZE + 2));
        
        UserInterface.showMainUI(true); 
    }
    
        
    /**
     * This function was done to test CoinManager and signature verification.
	   It verifies the signature for specified bip32 HD path. 
	   This HD path is a hex representation of string having a form m/44’/171’/0’/0’/ind’.
	   It will work only for short dataForSigning (< 200 bytes).
	   
	   !!!!Important note: here signature for now is supposed to have format that is produced by
       CoinManager: 0x41 + 64bytes of signature + 0x35
     
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
            
        UserInterface.showVerifying();
        
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
			
			short le = apdu.setOutgoing();
			if (le != VERIFY_RESPONSE_LEN) {
				ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			}
		
			buffer[0] = mark == true ? (byte) 0x01 : (byte) 0x00;
			apdu.setOutgoingAndSend((short) 0x00, VERIFY_RESPONSE_LEN);
        }
        catch(CryptoException e)
        {
            ISOException.throwIt(e.getReason());
        }   
        
        UserInterface.showMainUI(true);
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
        
        UserInterface.showVerifying();
                        
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
			
			short le = apdu.setOutgoing();
			if (le != VERIFY_RESPONSE_LEN) {
				ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			}
			
			buffer[0] = mark == true ? (byte) 0x01 : (byte) 0x00;
			apdu.setOutgoingAndSend((short) 0x00, VERIFY_RESPONSE_LEN);
        }
        catch(CryptoException e)
        {
            ISOException.throwIt(e.getReason());
        }   
        
        UserInterface.showMainUI(true);
    }
    

}