package tonwallet ;

import javacard.framework.*;

import javacard.security.*; 
import javacard.security.KeyPair;
import com.ftsafe.security.*;
import javacardx.crypto.Cipher;

import static tonwallet.Utils.*;

import static tonwallet.Constants.*;

public class AuthenticatedEncryptionHandler  
{

	private final short PUB_KEY_LEN_FOR_X25519 = 32;
	private final short NONCE_TOTAL_LEN = 24;
	private final byte NONCE_PART1_LEN = 16;
	private final byte NONCE_PART2_LEN = 8;
	private final short MAX_DATA_LEN = 128;

	private final short DOUBLE_AES_DATA_BLOCK_LEN = 32;
	private final short TRIPLE_AES_DATA_BLOCK_LEN = 48;
	
	private final short HMAC_LEN = 32;

	private KeyAgreement keyAgreement;
	private KeyPair keyPair; 
	private XECPublicKey innerPubkey; 
	private XECPrivateKey innerPrikey;
	
	private NamedParameterSpec params; 
			
	private byte[] commonSecretByDH; 
	
	private byte[] externalPubkeyData; 
	private short externalPubkeyLen; 

	private boolean isExternalPublicKeySet = false;
	
	private byte[] nonce; 
	
	private byte[] data;
	private short dataLen;
	
	private AESKey aesKey;
	private Cipher cipher;
	
	private byte[] keyData;

	private byte[] mac;
	private byte[] macFromHost;
	
	private HmacSha256 hmacSha256;
	
	
	
	public AuthenticatedEncryptionHandler(){
		
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
		
		externalPubkeyData = new byte[PUB_KEY_LEN_FOR_X25519];
		commonSecretByDH = new byte[PUB_KEY_LEN_FOR_X25519];
		externalPubkeyLen = PUB_KEY_LEN_FOR_X25519;
		
		nonce = new byte[NONCE_TOTAL_LEN];
		
		data = new byte[MAX_DATA_LEN];	
		
		keyData = new byte[TRIPLE_AES_DATA_BLOCK_LEN];
		mac = new byte[HMAC_LEN];
		macFromHost = new byte[HMAC_LEN];
		
		hmacSha256 = new HmacSha256();
		
		initAesStuff();		
	}
	
	public void generateKeyPairForDHProtocol(){
		keyPair.genKeyPair(); 
	}
	
	
	private void initAesStuff(){
		aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);        		
		cipher = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, false);
	}
	
	// Salsa20(HSalsa20(HSalsa20(k, 0), n1), n2), k - comon secret by DH, n1 (16 bytes) and 
	// n2 (8 bytes) - parts of nonce
	// Here we attempt to replace Salsa by AES:
	// AES(k2, AES(k1, n || 0))
	private void produceAesKeyData() {
		Util.arrayFillNonAtomic(keyData, (short)0, TRIPLE_AES_DATA_BLOCK_LEN, (byte)0);
		Util.arrayCopyNonAtomic(keyData, (short)0, nonce, (short)0, NONCE_TOTAL_LEN);
		aesKey.setKey(commonSecretByDH, (short)0);
		doAes(Cipher.MODE_ENCRYPT, keyData, (short)0, TRIPLE_AES_DATA_BLOCK_LEN); 
		
		aesKey.setKey(commonSecretByDH, AES_BLOCK_LEN);
		doAes(Cipher.MODE_ENCRYPT, keyData, (short)0, TRIPLE_AES_DATA_BLOCK_LEN);					
	}
	
	public void encrypt(APDU apdu){
		produceAesKeyData();
		aesKey.setKey(keyData, (short)0);
		doAes(Cipher.MODE_ENCRYPT, data, (short)0, dataLen);
		
		hmacSha256.getMac(keyData, AES_BLOCK_LEN, 
							data, (short)0, dataLen, 
							mac, (short)0);
		
		byte[] buffer = apdu.getBuffer();
		
		short le = apdu.setOutgoing();
		
		if (le > (short)(MAX_DATA_LEN + HMAC_LEN)){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        Util.arrayCopyNonAtomic(data, (short)0, buffer, (short)0, dataLen);
        
        Util.arrayCopyNonAtomic(mac, (short)0, buffer,  dataLen, HMAC_LEN);
		 
		apdu.setOutgoingLength((short)(dataLen + HMAC_LEN));
        apdu.sendBytes((short)0x00, (short)(dataLen + HMAC_LEN));
	}
	
	public void decrypt(APDU apdu){
		produceAesKeyData();
		
		hmacSha256.getMac(keyData, AES_BLOCK_LEN, 
							data, (short)0, dataLen, 
							mac, (short)0);
							
		byte compareRes = Util.arrayCompare(mac, (short)0, macFromHost, (short)0, HMAC_LEN) ;
		if (compareRes != 0){
			ISOException.throwIt(SW_MAC_INCORRECT);
		}
		
		
		aesKey.setKey(keyData, (short)0);
		doAes(Cipher.MODE_DECRYPT, data, (short)0, dataLen);
		
		byte[] buffer = apdu.getBuffer();
		
		short le = apdu.setOutgoing();
		
		if (le > MAX_DATA_LEN){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
		 
		apdu.setOutgoingLength(dataLen);
        apdu.sendBytesLong(data, (short)0x00, dataLen);
	}
	
	
	private void doAes(byte mode, byte[] data, short offset, short len) {
		//The byte length to be encrypted/decrypted must be a multiple of 16
        if (checkDataLen(len))
        {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        //Initializes the 'cipher' object with the appropriate Key and algorithm specific parameters.
        //AES algorithms in CBC mode expect a 16-byte parameter value for the initial vector(IV)
        
        cipher.init(aesKey, mode);
        
        //This method must be invoked to complete a cipher operation. Generates encrypted/decrypted output from all/last input data. 
        cipher.doFinal(data, offset, len, data, offset);
         
        //apdu.setOutgoingAndSend((short)0, len);
	}
	

    void setData(APDU apdu){
		byte[] buffer = apdu.getBuffer();
		short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        
        dataLen = numBytes;//(short)(buffer[ISO7816.OFFSET_CDATA]  & 0xFF);
        
        if (numBytes != byteRead || checkDataLen(dataLen)){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        Util.arrayFillNonAtomic(data, (short)0, MAX_DATA_LEN, (byte)0);
        
        //todo: make jc transaction here
		Util.arrayCopyNonAtomic(buffer, (short)(ISO7816.OFFSET_CDATA), 
								data, (short)0, dataLen);
								
		printArray(data, (short)0, dataLen);
	}
	
	void setMac(APDU apdu){
		byte[] buffer = apdu.getBuffer();
		short numBytes = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);
        short byteRead = apdu.setIncomingAndReceive();
        
        if (numBytes != byteRead || numBytes != HMAC_LEN){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
		Util.arrayCopyNonAtomic(buffer, (short)(ISO7816.OFFSET_CDATA), 
								macFromHost, (short)0, HMAC_LEN);
	}
	
	private boolean checkDataLen(short len) {
    	//fix it, take mac len into account
    	return  len > MAX_DATA_LEN || len <= 0
			|| len % AES_BLOCK_LEN != 0;	    
    }
	
	public void initKeyAgreement(){
		keyAgreement.init(innerPrikey); 
	}
	
	public void getInnerPublicKeyForDHProtocol(APDU apdu){
		byte[] buffer = apdu.getBuffer();
		if (!innerPubkey.isInitialized()) { 
			keyPair.genKeyPair(); 
		}
		short le = apdu.setOutgoing();
		
		if (le != PUB_KEY_LEN_FOR_X25519){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
		 
		innerPubkey.getEncoded(buffer, (short)0); 
		apdu.setOutgoingLength(le);
		apdu.sendBytes((short)0, le); 
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
		printArray(externalPubkeyData, (short)0, PUB_KEY_LEN_FOR_X25519);
		isExternalPublicKeySet = true;
	}
	
	public void generateCommonSecretByDHProtocol(){
		if (!isExternalPublicKeySet){
			ISOException.throwIt(Constants.SW_EXTERNAL_PK_IS_NOT_INITIALIZED);
		}			
		keyAgreement.generateSecret(externalPubkeyData, (short)0, externalPubkeyLen, 
									commonSecretByDH, (short)0);	
									
		printArray(commonSecretByDH, (short)0, PUB_KEY_LEN_FOR_X25519);		
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
		printArray(nonce, (short)0, NONCE_TOTAL_LEN);	
	}		
}
