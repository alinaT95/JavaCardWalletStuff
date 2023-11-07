package wallet ;

import javacard.framework.*;
import javacard.framework.APDU;
import javacard.security.*;
import com.ftsafe.security.*;

import com.ftsafe.javacardx.btcmgr.CoinManager;

public class TonCryptoHandler extends CryptoHandler
{
	XECPrivateKey privateKey;

	public final static short TON_PUBLIC_KEY_SIZE = (short) 32;
	public final static short CHAIN_CODE_KEY_SIZE = (short) 32;
	public final static short TON_CHAIN_CODE_CONSTANT = (short) 65;
	
	public final static short KEY_SIZE = (short) 32;
	public final static short SIGNATURE_SIZE = (short) 64;
	
	private byte[] keyBuf = new byte[100];
	
	private XECPublicKey publicKey;
    private NamedParameterSpec params;
	
	private byte[] rootPath = new byte[] {0x6D,0x2F,0x34,0x34,0x27,0x2F,0x31,0x37,0x31,0x27,0x2F,0x30,0x27,0x2F,0x30,0x27,0x2F,0x30,0x27};
	private byte[] rootPathInLV = new byte[] {0x13, 0x6D,0x2F,0x34,0x34,0x27,0x2F,0x31,0x37,0x31,0x27,0x2F,0x30,0x27,0x2F,0x30,0x27,0x2F,0x30,0x27};
	
    private Signature sig;
    
    private byte[] tmpout = new byte[2048];
    
    public TonCryptoHandler (){
    	super();
    	
    	
    	     
        params = NamedParameterSpec.getInstance(NamedParameterSpec.ED25519);
		
		// create public key		
		short attributes;
		attributes = KeyBuilderX.ATTR_PUBLIC;
		attributes |= JCSystem.MEMORY_TYPE_PERSISTENT;
		publicKey = (XECPublicKey)KeyBuilderX.buildXECKey(params, attributes, false); 
		
		sig = Signature.getInstance((byte)0, SignatureX.SIG_CIPHER_EDDSA, (byte)0, false);		
    }
    
    

    //--------------------------------------------------------------------
    
     /**
     * This function returns the public key for given HD path.
     *
     * ins: 0xA0
     * p1: 0x00
     * p2: 0x00
     * data: hex representation of HD path
     * return(): [publicKeyLength | publicKey]
     */
    
    public void getPublicKey(APDU apdu){
        
        byte[] buf = apdu.getBuffer();

        short lc = apdu.setIncomingAndReceive();
        short lcOff = apdu.getOffsetCdata();
        
        short ret = CoinManager.getCoinPubData(buf, (short) (lcOff + 1), (short) (lc - 1), buf, (short) 0, buf, TON_CHAIN_CODE_CONSTANT);
        if (ret != 0) {
           ISOException.throwIt(Constants.SW_GET_COIN_PUB_DATA_FAILED);
        }
        apdu.setOutgoingAndSend((short) 0, (short) (/*65 + */ 32));
    }
    
    /**
     * This function signs the message from apdu buffer.
     *
     * ins: 0xA7
     * p1: 0x00
     * p2: 0x00
     * data: [none]
     * return(): [publicKeyLength | publicKey]
     */
    public void getPublicKeyWithDefaultHDPath(APDU apdu){
        
        byte[] buf = apdu.getBuffer();

        short lc = apdu.setIncomingAndReceive();
        short lcOff = apdu.getOffsetCdata();
        
        short ret = CoinManager.getCoinPubData(rootPath, (short) 0, (short) rootPath.length, buf, (short) 0, buf, TON_CHAIN_CODE_CONSTANT);
        if (ret != 0) {
           ISOException.throwIt(Constants.SW_GET_COIN_PUB_DATA_FAILED);
        }
        apdu.setOutgoingAndSend((short) 0, (short) (/*65 +*/ 32));
    }
    
    /**
     * This function signs the message from apdu buffer.
     *
     * ins: 0xA3
     * p1: 0x00
     * p2: 0x00
     * data: [messageLength | message | pathLength | path]
     * return(): [signatureLength | signature]
     */

    public void signShortMessage(APDU apdu){
    	byte[] buf = apdu.getBuffer();
    	short lc = apdu.setIncomingAndReceive();
        short lcOff = apdu.getOffsetCdata();
        
        UserInterface.showWorking();
        
        short dataOffset = lcOff;
        short dataLen = (short)(buf[lcOff] + 1); // (byte)(buf[lcOff] & 0xFF + 1);
        
        short pathOffset = (short)(dataOffset + dataLen);
        short pathLen = (short)(buf[pathOffset] + 1);
        
        short result = CoinManager.signCoinData((short) 1, buf, dataOffset, dataLen, tmpBuffer, (short) 0,  buf, pathOffset, pathLen);
        
        if (result != 0) {
           ISOException.throwIt( Constants.SW_SIGN_DATA_FAILED);
        }
        
        UserInterface.showMainUI(true);
        
        apdu.setOutgoing();
        apdu.setOutgoingLength((short)(SIGNATURE_SIZE + 2));

        apdu.sendBytesLong(tmpBuffer, (short) 0, (short)(SIGNATURE_SIZE + 2));
        
        UserInterface.showMainUI(true); 
    }
    
    /**
     * This function signs the message stored in dataForSigning.
     *
     * ins: 0xA4
     * p1: 0x00
     * p2: 0x00
     * data: [pathLength | path]
     * return(): [signatureLength | signature]
     */
    
    public void signStoredMessage(APDU apdu){
    	byte[] buf = apdu.getBuffer();
    	short lc = apdu.setIncomingAndReceive();
        short lcOff = apdu.getOffsetCdata();
        
        UserInterface.showWorking();
        
        short pathLen = (byte)(buf[lcOff + Constants.PIN_LENGTH] & 0xFF);
        short pathOffset = (short)(lcOff + Constants.PIN_LENGTH + 1);
        
        short result = CoinManager.signCoinData((short) 0, dataForSigning, (short) 0, dataForSigningTotalLen, tmpBuffer, (short) 0,  buf, pathOffset, pathLen);
        
        if (result != 0) {
           ISOException.throwIt( Constants.SW_SIGN_DATA_FAILED);
        }
        
        apdu.setOutgoing();
        apdu.setOutgoingLength((short)(66 * 2));
        apdu.sendBytesLong(tmpBuffer, (short) 0,(short)(66 * 2));
        
        UserInterface.showMainUI(true); 
    }
    
        /**
     * This function signs the message from apdu buffer.
     *
     * ins: 0xA5
     * p1: 0x00
     * p2: 0x00
     * data: [messageLength| message]
     * return(): [signatureLength | signature]
     */

    public void signShortMessageWithDefaultPath(APDU apdu){
    	
    	byte[] buf = apdu.getBuffer();
    	short lc = apdu.setIncomingAndReceive();
        short lcOff = apdu.getOffsetCdata();
        
        UserInterface.showWorking();
        
        short dataLen = (short)(buf[lcOff] + 1);//(buf[lcOff] & 0xFF);
        short dataOffset = (short)(lcOff);
        
        short result = CoinManager.signCoinData((short) 1, buf, dataOffset, dataLen, tmpBuffer, (short) 0,  rootPathInLV, (short) 0, (short)rootPathInLV.length);

        if (result != 0) {
           ISOException.throwIt( Constants.SW_SIGN_DATA_FAILED);
        }
        
        apdu.setOutgoing();
        apdu.setOutgoingLength((short)(SIGNATURE_SIZE + 2));
        apdu.sendBytesLong(tmpBuffer, (short) 0,(short)(SIGNATURE_SIZE + 2));
        
        UserInterface.showMainUI(true);
    }
    
    /**
     * This function signs the message stored in dataForSigning.
     *
     * ins: 0xA6
     * p1: 0x00
     * p2: 0x00
     * data: none
     * return(): [signatureLength | signature]
     */
    
    public void signStoredMessageWithDefaultPath(APDU apdu){
    	byte[] buf = apdu.getBuffer();
    	short lc = apdu.setIncomingAndReceive();
        short lcOff = apdu.getOffsetCdata();
        
        UserInterface.showWorking();
        
        short result = CoinManager.signCoinData((short) 0, dataForSigning, (short) 0, dataForSigningTotalLen, signatureBytes, (short) 0,  rootPath, (short)0, (short)rootPath.length);
        
        if (result != 0) {
           ISOException.throwIt( Constants.SW_SIGN_DATA_FAILED);
        }
        
        apdu.setOutgoing();
        apdu.setOutgoingLength((short)(66));
        apdu.sendBytesLong(signatureBytes, (short) 0,(short)(66));
        
        UserInterface.showMainUI(true); 
    }
    
        
    /**
     * This function verifies signature for given HD path.
     *
     * ins: 0xD0
     * p1: 0x00
     * p2: 0x00
     * data: [dataLength|data|signatureLength|signature|pathLength|path]
     * return(): byte 1 if signature right, 0 otherwise
     */
     // here signature for now is supposed to have format that is produced by Coinmanager: 0x41 + 64bytes of signature + 0x35
    
    public void verifySignature(APDU apdu){
        byte[] buf = apdu.getBuffer();

        short lc = apdu.setIncomingAndReceive();
        short lcOff = apdu.getOffsetCdata();
              
        UserInterface.showVerifying();
        
        short dataForSigningLen = (short) buf[lcOff];
			short dataForSigningStartPos = (short)(lcOff + 1);
			short signatureStartPos = (short)(dataForSigningStartPos + dataForSigningLen + 1);
			short pathStartPos = (short)(signatureStartPos + SIGNATURE_SIZE + 2);
			short pathLength = (short) buf[signatureStartPos + SIGNATURE_SIZE + 1]; // this is because we get signature from coinmanager now and it containd redundant byte 0x35 at the end (we skip it)
			
		short ret = CoinManager.getCoinPubData(buf, pathStartPos, pathLength, keyBuf, (short) 0, null, TON_CHAIN_CODE_CONSTANT);
        if (ret != 0) {
           ISOException.throwIt(Constants.SW_GET_COIN_PUB_DATA_FAILED);
        }
                                
	    try
        {
			publicKey.setEncoded(keyBuf, (short) 1, TON_PUBLIC_KEY_SIZE);
			sig.init(publicKey, Signature.MODE_VERIFY);
			

			boolean mark = sig.verify(buf, dataForSigningStartPos, dataForSigningLen, buf, signatureStartPos, SIGNATURE_SIZE);                                                                 
			buf[0] = mark == true ? (byte) 0x01 : (byte) 0x00;
			apdu.setOutgoingAndSend((short) 0, (short) 1);
        }
        catch(CryptoException e)
        {
            ISOException.throwIt(e.getReason());
        }   
        
        UserInterface.showMainUI(true);
    }
    
    /**
     * This function verifies signature for root HD path.
     *
     * ins: 0xD1
     * p1: 0x00
     * p2: 0x00
     * data: [dataLength|data|signatureLength|signature]
     * return(): byte 1 if signature right, 0 otherwise
     */
     // here signature for now is supposed to have format that is produced by Coinmanager: 0x41 + 64bytes of signature + 0x35
    
    public void verifySignatureWithDefaultPath(APDU apdu){
	    byte[] buf = apdu.getBuffer();

        short lc = apdu.setIncomingAndReceive();
        short lcOff = apdu.getOffsetCdata();
        
        
        UserInterface.showVerifying();
                        
        short ret = CoinManager.getCoinPubData(rootPath, (short) 0, (short) rootPath.length, keyBuf, (short) 0, null, TON_CHAIN_CODE_CONSTANT);
        if (ret != 0) {
           ISOException.throwIt(Constants.SW_GET_COIN_PUB_DATA_FAILED);
        }
     
	    try
        {
			publicKey.setEncoded(keyBuf, (short) 1, TON_PUBLIC_KEY_SIZE);
			sig.init(publicKey, Signature.MODE_VERIFY);
			
			short dataForSigningLen = (short) buf[lcOff];
			short dataForSigningStartPos = (short)(lcOff + 1);
			short signatureStartPos = (short)(dataForSigningStartPos + dataForSigningLen + 1);
			
			boolean mark = sig.verify(buf, dataForSigningStartPos, dataForSigningLen, buf, signatureStartPos, SIGNATURE_SIZE);                                                                 
			buf[0] = mark == true ? (byte) 0x01 : (byte) 0x00;
			apdu.setOutgoingAndSend((short) 0, (short) 1);
        }
        catch(CryptoException e)
        {
            ISOException.throwIt(e.getReason());
        }   
        
        UserInterface.showMainUI(true);
    }
    

}