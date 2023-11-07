package tonwallet ;

import static tonwallet.Constants.*;
import javacard.security.*;
import javacardx.crypto.*;

public class AesHelper  
{
	private AESKey aesKey; 
    private RandomData random;
    private byte[] aesKeyBytes;		
	private Cipher cipherEnc;
    private Cipher cipherDec;
    
    public Cipher getCipherEnc(){
	    return cipherEnc;
    }
    
    public Cipher getCipherDec(){
	    return cipherDec;
    } 
    
    public AesHelper(){
	    random = RandomData.getInstance(RandomData.ALG_SECURE_RANDOM);	
		aesKeyBytes = new byte[AES_KEY_LEN];
        random.generateData(aesKeyBytes, (short)0, AES_KEY_LEN);		
		aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);
        aesKey.setKey(aesKeyBytes, (short) 0);	
		
		//todo: maybe, need to use another algorithm
		cipherEnc = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, false);
		cipherEnc.init(aesKey, Cipher.MODE_ENCRYPT);
		
		cipherDec = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, false);
		cipherDec.init(aesKey, Cipher.MODE_DECRYPT);
    }
	
}
