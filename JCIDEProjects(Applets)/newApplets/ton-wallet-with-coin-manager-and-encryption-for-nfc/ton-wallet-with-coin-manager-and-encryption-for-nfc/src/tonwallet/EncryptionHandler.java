package tonwallet ;

import javacard.framework.*;

import javacard.security.*; 
import javacard.security.KeyPair;
import com.ftsafe.security.*;
import javacardx.crypto.Cipher;

public class EncryptionHandler  
{
	private final short PUB_KEY_LEN_FOR_X25519 = 32;
	private final short NONCE_TOTAL_LEN = 24;
	private final byte NONCE_PART1_LEN = 16;
	private final byte NONCE_PART2_LEN = 8;
	private final short MAX_DATA_LEN = 256;
	private final short AES_DATA_BLOCK_LEN = 16;
	private final short DOUBLE_AES_DATA_BLOCK_LEN = 32;

	private KeyAgreement keyAgreement;
	private KeyPair keyPair; 
	private XECPublicKey innerPubkey; 
	private XECPrivateKey innerPrikey;
		
	private byte[] commonSecretByDH; 
	
	private byte[] externalPubkeyData; 
	private short externalPubkeyLen; 
	
	private NamedParameterSpec params; 
	 
	private boolean isExternalPublicKeySet = false;
	
	private byte[] nonce; 
	private byte[] finalAesKeyData;
	private byte[] mac;
	
	private byte[] dataForEncryption, encryptedData; 
	private short dataForEncryptionLen;
	
	//private AESKey aesKey128, tempAesKey128, tempAesKey256;
	
	private AESKey aesKey256;
	
	//private Cipher cipherAES128;
	private Cipher cipherAes256;
	
	private Signature macSignature;
	
	public EncryptionHandler (){
		short attributes;
		params = NamedParameterSpec.getInstance(NamedParameterSpec.X25519);
		
		attributes = KeyBuilderX.ATTR_PUBLIC; 
		attributes |= JCSystem.MEMORY_TYPE_PERSISTENT; 
		innerPubkey = (XECPublicKey)KeyBuilderX.buildXECKey(params, attributes, false); 
		
		attributes = KeyBuilderX.ATTR_PRIVATE; 
		attributes |= JCSystem.MEMORY_TYPE_PERSISTENT; 
		innerPrikey = (XECPrivateKey)KeyBuilderX.buildXECKey(params, attributes, false);
		
		keyPair = new KeyPair((PublicKey)innerPubkey, (PrivateKey)innerPrikey);
		keyAgreement = KeyAgreement.getInstance(KeyAgreementX.ALG_XDH, false);
		
		externalPubkeyData = new byte[PUB_KEY_LEN_FOR_X25519/*256*/]; 
		dataForEncryption = new byte[MAX_DATA_LEN];
		encryptedData  = new byte[MAX_DATA_LEN];
		commonSecretByDH = new byte[PUB_KEY_LEN_FOR_X25519];
		nonce = new byte[NONCE_TOTAL_LEN];
		finalAesKeyData = new byte[DOUBLE_AES_DATA_BLOCK_LEN];
		mac = new byte[AES_DATA_BLOCK_LEN];
		
		externalPubkeyLen = PUB_KEY_LEN_FOR_X25519;
		
		macSignature = Signature.getInstance((byte) 0x00, Signature.ALG_AES_MAC_128_NOPAD, (byte) 0x00, false);	
		
		initAesStuff();
		
	}
	
	//Now we use AES256 in ECB mode, probably we should use CBC?
	private void initAesStuff(){
		//cipherAES128 = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, false);
		cipherAes256 = Cipher.getInstance(Cipher.ALG_AES_BLOCK_256_ECB_NOPAD, false);
		aesKey256 = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
		//tempAesKey128 = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);
		//tempAesKey256 = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
	}
	
	public void generateKeyPairForDHProtocol(){
		keyPair.genKeyPair(); 
	}
	
	public void getInnerPublicKeyForDHProtocol(APDU apdu){
		byte[] buffer = apdu.getBuffer();
		if (!innerPubkey.isInitialized()) { 
			keyPair.genKeyPair(); 
		}
		/*short len =*/ 
		innerPubkey.getEncoded(buffer, (short)0); 
		apdu.setOutgoingAndSend((short)0, /*len*/ PUB_KEY_LEN_FOR_X25519); 
	}
	
	public void initKeyAgreement(){
		keyAgreement.init(innerPrikey); 
	}
	

	public void setExternalPublicKey(APDU apdu){
		byte[] buffer = apdu.getBuffer();
		short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        if (numBytes != byteRead || numBytes != PUB_KEY_LEN_FOR_X25519){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        //todo: make jc transaction here
		Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, externalPubkeyData, (short)0, numBytes);
		isExternalPublicKeySet = true;
	}
	
	public void generateCommonSecretByDHProtocol(){
		if (!isExternalPublicKeySet){
			ISOException.throwIt(Constants.SW_EXTERNAL_PK_IS_NOT_INITIALIZED);
		}			
		keyAgreement.generateSecret(externalPubkeyData, (short)0, externalPubkeyLen, 
									commonSecretByDH, (short)0);			
	}
	
	//only for developer mode
	public void getCommonSecret(APDU apdu){
		//byte[] buffer = apdu.getBuffer();
		
		short le = apdu.setOutgoing();
        if (le != PUB_KEY_LEN_FOR_X25519) {
             ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        apdu.setOutgoingLength(PUB_KEY_LEN_FOR_X25519);
        apdu.sendBytesLong(commonSecretByDH, (short) 0x00, PUB_KEY_LEN_FOR_X25519);  
	}
	
	
	public void setNonce(APDU apdu){
		/* get nonce from APDU buffer */
		byte[] buffer = apdu.getBuffer();
		short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        if (numBytes != byteRead || numBytes != NONCE_TOTAL_LEN){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        //todo: make jc transaction here
		Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, nonce, (short)0, numBytes);
	}
	
	//data for encryption comes from outside in LV format.
	public void setDataForEncryption(APDU apdu){
		byte[] buffer = apdu.getBuffer();
		short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        
        dataForEncryptionLen = (short)(buffer[ISO7816.OFFSET_CDATA]  & 0xFF);
        
        if (numBytes != byteRead || checkDataLen(dataForEncryptionLen)){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        //todo: make jc transaction here
		Util.arrayCopyNonAtomic(buffer, (short)(ISO7816.OFFSET_CDATA + 1), 
								dataForEncryption, (short)0, dataForEncryptionLen);
	}
	
		
	// Salsa20(HSalsa20(HSalsa20(k, 0), n1), n2), k - comon secret by DH, n1 (16 bytes) and 
	// n2 (8 bytes) - parts of nonce
	// Here we attempt to replace Salsa by AES
	public void produceAesKeyData() {
		//HSalsa20(k, 0)
		Util.arrayFillNonAtomic(dataForEncryption, (short)0, DOUBLE_AES_DATA_BLOCK_LEN, (byte)0);
		((AESKey)aesKey256).setKey(commonSecretByDH, (short)0);
		doAesCipher();
		
		//HSalsa20(HSalsa20(k, 0), n1)
		Util.arrayCopyNonAtomic(nonce, (short)0, dataForEncryption, (short)0, NONCE_PART1_LEN);
		//takes first 32 bytes of  encryptedData for key
		((AESKey)aesKey256).setKey(encryptedData, (short)0); 
		doAesCipher();
		
		//Salsa20(HSalsa20(HSalsa20(k, 0), n1), n2)
		Util.arrayFillNonAtomic(dataForEncryption, (short)0, NONCE_PART1_LEN, (byte)0);
		Util.arrayCopyNonAtomic(nonce, (short)NONCE_PART1_LEN, dataForEncryption, 
								(short)0, NONCE_PART2_LEN);
		((AESKey)aesKey256).setKey(encryptedData, (short)0);
		doAesCipher();						
		
		Util.arrayCopyNonAtomic(encryptedData, (short)0, finalAesKeyData, 
								(short)0, DOUBLE_AES_DATA_BLOCK_LEN);
		
		aesKey256.setKey(finalAesKeyData, (short)0);						
	}
	
	public void produceMac() {
		macSignature.init(aesKey256, Signature.MODE_SIGN);
		macSignature.sign(encryptedData, (short)0, dataForEncryptionLen, mac, (short)0);                              
	}
		
	public void encryptData() {
		doAesCipher();
	}
	
	//AES algorithm to encrypt 
	//It takes plain text data from dataForEncryption 
	//and puts encrypted data into encryptedData.
    private void doAesCipher(/*Cipher cipher, AESKey key*/)
    {
       //The byte length to be encrypted/decrypted must be a multiple of 16
        if (checkDataLen(dataForEncryptionLen))
        {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        //Initializes the 'cipher' object with the appropriate Key and algorithm specific parameters.
        //AES algorithms in CBC mode expect a 16-byte parameter value for the initial vector(IV)
        
        cipherAes256.init(aesKey256, Cipher.MODE_ENCRYPT);
        
        //This method must be invoked to complete a cipher operation. Generates encrypted/decrypted output from all/last input data. 
        cipherAes256.doFinal(dataForEncryption, (short)0, dataForEncryptionLen, encryptedData, (short)0);
         
        //apdu.setOutgoingAndSend((short)0, len);
    }
    
    public void getEncryptedData(APDU apdu){
	    byte[] buffer = apdu.getBuffer();
		
		short le = apdu.setOutgoing();
        if (checkDataLen(le)) {
             ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        produceMac();
        
        Util.arrayCopyNonAtomic(encryptedData, (short)0, buffer, 
								(short)0, dataForEncryptionLen);
		Util.arrayCopyNonAtomic(mac, (short)0, buffer, 
								dataForEncryptionLen, AES_DATA_BLOCK_LEN);						
        
		apdu.setOutgoingAndSend((short) 0x00, (short)(dataForEncryptionLen + AES_DATA_BLOCK_LEN));
        
        //apdu.setOutgoingLength(dataForEncryptionLen);
        //apdu.sendBytesLong(encryptedData, (short) 0x00, dataForEncryptionLen);  
    }
     
    private boolean checkDataLen(short len) {
    	//fix it, take mac len into account
    	return  dataForEncryptionLen > MAX_DATA_LEN || dataForEncryptionLen <= 0
			|| dataForEncryptionLen % AES_DATA_BLOCK_LEN != 0;
	    
    }
		
}
