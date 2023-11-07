package wallet ;

import javacard.framework.*;
import javacard.framework.APDU;
import javacard.security.*;
import com.ftsafe.security.*;

public class TonCryptoHandler extends CryptoHandler
{

	public static final short MAX_SEED_SIZE = 64;
	
	private static final byte[] BITCOIN_SEED = {'B','i','t','c','o','i','n',' ','s','e','e','d'};
    private static final byte[] BITCOIN_SIGNED_MESSAGE_HEADER = {0x18,'B','i','t','c','o','i','n',' ','S','i','g','n','e','d',' ','M','e','s','s','a','g','e',':','\n'}; //"Bitcoin Signed Message:\n";

	
	private byte priKeyBufForTest[] = { (byte) 0x4c, (byte) 0xcd, (byte) 0x08, (byte) 0x9b, (byte) 0x28, (byte) 0xff, (byte) 0x96,
				(byte) 0xda, (byte) 0x9d, (byte) 0xb6, (byte) 0xc3, (byte) 0x46, (byte) 0xec, (byte) 0x11, (byte) 0x4e,
				(byte) 0x0f, (byte) 0x5b, (byte) 0x8a, (byte) 0x31, (byte) 0x9f, (byte) 0x35, (byte) 0xab, (byte) 0xa6,
				(byte) 0x24, (byte) 0xda, (byte) 0x8c, (byte) 0xf6, (byte) 0xed, (byte) 0x4f, (byte) 0xb8, (byte) 0xa6,
				(byte) 0xfb };
				
	private byte pubKeyBufForTest[] = { (byte) 0x3d, (byte) 0x40, (byte) 0x17, (byte) 0xc3, (byte) 0xe8, (byte) 0x43, (byte) 0x89,
				(byte) 0x5a, (byte) 0x92, (byte) 0xb7, (byte) 0x0a, (byte) 0xa7, (byte) 0x4d, (byte) 0x1b, (byte) 0x7e,
				(byte) 0xbc, (byte) 0x9c, (byte) 0x98, (byte) 0x2c, (byte) 0xcf, (byte) 0x2e, (byte) 0xc4, (byte) 0x96,
				(byte) 0x8c, (byte) 0xc0, (byte) 0xcd, (byte) 0x55, (byte) 0xf1, (byte) 0x2a, (byte) 0xf4, (byte) 0x66,
				(byte) 0x0c };
				
	private byte[] seedBuf;
	
	private byte[] privateExponent;
    private byte[] privateSault;
	
	private KeyPair kp;
    private XECPublicKey publicKey;
    private XECPrivateKey privateKey;
    private NamedParameterSpec params;
    private Signature sig;

    private KeyAgreement keyAgreement;
    private RandomData randomData;
    
    public TonCryptoHandler (){
	    super();
	    seedBuf = new byte[MAX_SEED_SIZE];
	    
	    randomData = RandomData.getInstance(RandomData.ALG_SECURE_RANDOM);
	    //keyAgreement = KeyAgreement.getInstance(Constants.ALG_EC_SVDP_DH_PLAIN, false);
        sig = Signature.getInstance((byte)0, SignatureX.SIG_CIPHER_EDDSA, (byte)0, false);
        
        params = NamedParameterSpec.getInstance(NamedParameterSpec.ED25519);
		
		//create keys
		
		short attributes;
        attributes = KeyBuilderX.ATTR_PRIVATE;
        attributes |= JCSystem.MEMORY_TYPE_PERSISTENT;	
		privateKey = (XECPrivateKey)KeyBuilderX.buildXECKey(params, attributes, false);
		
		attributes = KeyBuilderX.ATTR_PUBLIC;
		attributes |= JCSystem.MEMORY_TYPE_PERSISTENT;
		publicKey = (XECPublicKey)KeyBuilderX.buildXECKey(params, attributes, false); 
		
		SHA512.init();  
    }
    
    public void setupKeys(APDU apdu){
    	privateKey.setEncoded(priKeyBufForTest, (short) 0, KEY_SIZE);
	    publicKey.setEncoded(pubKeyBufForTest, (short) 0, KEY_SIZE);
    }
    
     public void setupMasterKeyWithoutSeed(){
        //kp = new KeyPair(KeyPairX.ALG_ED25519, (short) 256);
        //kp.genKeyPair();
        //privateKey = (ECPrivateKey) kp.getPrivate();
        //publicKey = (ECPublicKey) kp.getPublic();
    }
    
    //buffer contains seed
    public void setupMasterKeyFromRandomSeed(){
        //generate random seed
        randomData.generateData(seedBuf, (short) 0, MAX_SEED_SIZE);
        HmacSha512.computeHmacSha512(BITCOIN_SEED, (short) 0, (short) BITCOIN_SEED.length, seedBuf, (short) 0, MAX_SEED_SIZE, tmpBuffer, (short) 0);
        privateKey.setEncoded(tmpBuffer, (short) 0, KEY_SIZE);
    }


    public void importSeed(byte[] buffer) {
        byte seedSize = buffer[ISO7816.OFFSET_P1];
        if (seedSize < 0 || seedSize > MAX_SEED_SIZE)
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        HmacSha512.computeHmacSha512(BITCOIN_SEED, (short) 0, (short) BITCOIN_SEED.length, buffer, (short) ISO7816.OFFSET_CDATA,
                (short) seedSize, tmpBuffer, (short) 0);
        privateKey.setEncoded(tmpBuffer, (short) 0, KEY_SIZE);
    }
    
    //This is only for personalization for developer needs
    //--------------------------------------------------------------------
    
    public void importPrivateKey(APDU apdu){
    	PinHandler.checkPin();
    	try
        {
			byte[] buffer = apdu.getBuffer();
			short numBytes = (short) (buffer[ISO7816.OFFSET_LC] & 0xFF);
			short byteRead = apdu.setIncomingAndReceive();
			if (numBytes != byteRead && numBytes != KEY_SIZE){
				ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			}
			privateKey.setEncoded(buffer, ISO7816.OFFSET_CDATA, KEY_SIZE);	
		}
		catch(CryptoException e)
        {
            ISOException.throwIt((short)(e.getReason()));
        }
    }
    
    public void importPublicKey(APDU apdu){
    	PinHandler.checkPin();
    	try
        {
			byte[] buffer = apdu.getBuffer();
			short numBytes = (short) (buffer[ISO7816.OFFSET_LC] & 0xFF);
			short byteRead = apdu.setIncomingAndReceive();
			if (numBytes != byteRead && numBytes != KEY_SIZE){
				ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			}
			publicKey.setEncoded(buffer, ISO7816.OFFSET_CDATA, KEY_SIZE);
		}
		catch(CryptoException e)
        {
            ISOException.throwIt((short)(e.getReason()));
        }
    }
       
    
    //--------------------------------------------------------------------
    
    public void getPublicMasterKey(APDU apdu){
    	PinHandler.checkPin();
    	try
        {
			byte[] buffer = apdu.getBuffer();
			short len = publicKey.getEncoded(buffer, (short) 2);
			Util.setShort(buffer, (short) 0, len);
			apdu.setOutgoingAndSend((short) 0, (short) (len + 2));
        }
        catch(CryptoException e)
        {
            ISOException.throwIt((short)(e.getReason()));
        }
    }

    /**
     * This function returns the public key associated with a particular private key stored
     * in the applet. The exact key blob contents depend on the key's algorithm and type.
     *
     * ins: 0x35
     * p1: 0x00
     * p2: 0x00
     * data: none
     * return(): [coordx_size(2b) | pubkey_coordx | sig_size(2b) | sig]
     */

    public void getPublicMasterKeyUsingKeyAgreement(APDU apdu){
        // byte[] buffer = apdu.getBuffer();

        // if (privateKey.getType() == KeyBuilder.TYPE_EC_FP_PRIVATE){
            // if (privateKey.getSize() != Constants.LENGTH_EC_FP_256)
                // ISOException.throwIt(Constants.SW_INCORRECT_ALG);

            // // check the curve param
            // if(!Secp256k1.checkCurveParameters((ECPrivateKey) privateKey, tmpBuffer, (short) 0))
                // ISOException.throwIt(Constants.SW_INCORRECT_ALG);


            // // compute the corresponding partial public key...
            // keyAgreement.init((ECPrivateKey) privateKey);
            // //short coordxSize = keyAgreement.generateSecret(Secp256k1.SECP256K1, Secp256k1.OFFSET_SECP256K1_G, (short) 65, buffer, (short) 2); // compute x coordinate of public key as k*G
            // short coordxSize = keyAgreement.generateSecret(Secp256k1.SECP256K1, Secp256k1.OFFSET_SECP256K1_G, (short) 65, buffer, (short) 2); // compute x coordinate of public key as k*G

            // Util.setShort(buffer, (short) 0, coordxSize);

            // // sign fixed message
            // sig.init(privateKey, Signature.MODE_SIGN);

            // short sign_size  = sig.sign(buffer, (short) 0, (short) (coordxSize + 2), buffer, (short) (coordxSize + 4));

            // Util.setShort(buffer, (short) (coordxSize + 2), sign_size);


            // // return x-coordinate of public key+signatureBytes
            // // the client can recover full public-key from the signatureBytes or
            // // by guessing the compression value () and verifying the signatureBytes...
            // apdu.setOutgoingAndSend((short) 0, (short) (2 + coordxSize + 2 + sign_size));
        // }
        // else {
            // ISOException.throwIt(Constants.SW_INCORRECT_ALG);
        // }
    }
    
     public void signMessage(APDU apdu){
     	PinHandler.checkPin();
        try
        {            
            byte[] buffer = apdu.getBuffer();
            sig.init(privateKey, Signature.MODE_SIGN);
            sigLen = sig.sign(dataForSigning, (short) 0, dataForSigningTotalLen, signatureBytes, (short) 0);
            Util.setShort(buffer, (short) 0, sigLen);
            Util.arrayCopy(signatureBytes, (short) 0, buffer, (short) 2, (short) sigLen);
            apdu.setOutgoingAndSend((short) 0, (short) (sigLen + 2));
        }
        catch(CryptoException e)
        {
            ISOException.throwIt((short)(e.getReason()));
        }
    }
    
    public void verifySignature(APDU apdu){
    	//PinHandler.checkPin();
	    try
        {
			byte[] buffer = apdu.getBuffer();
			sig.init(publicKey, Signature.MODE_VERIFY);
			boolean mark = sig.verify(dataForSigning, (short)0, dataForSigningTotalLen, signatureBytes, (short) 0, sigLen);                                                                   
			buffer[0] = mark == true ? (byte) 0x01 : (byte) 0x00;
			apdu.setOutgoingAndSend((short) 0, (short) 1);
        }
        catch(CryptoException e)
        {
            ISOException.throwIt(e.getReason());
        }   
    }
    
    ///////// It is not used yet, but we need it according to TON specification
    
    private void getPrivateExponentAndSaultFromPrivateKeyBytes(byte[] privateKeyBytes){
	    SHA512.resetUpdateDoFinal(privateKeyBytes, (short) 0, KEY_SIZE, tmpBuffer, (short) 0);
	    Util.arrayCopy(tmpBuffer, (short) 0, privateExponent, (short) 0, KEY_SIZE);
	    // todo: in  privateExponent  bits 255, 2, 1, and 0 cleared, and bit 254 set
	    Util.arrayCopy(tmpBuffer, KEY_SIZE, privateSault, (short) 0, KEY_SIZE);    
    }
}
