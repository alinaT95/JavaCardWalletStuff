package wallet ;



import javacard.framework.*;

import javacard.framework.APDU;
import javacard.security.*;


public class BtcCryptoHandler {

   // private static final byte[] BITCOIN_SEED = {'B','i','t','c','o','i','n',' ','s','e','e','d'};
    private static final byte[] BITCOIN_SIGNED_MESSAGE_HEADER = {0x18,'B','i','t','c','o','i','n',' ','S','i','g','n','e','d',' ','M','e','s','s','a','g','e',':','\n'}; //"Bitcoin Signed Message:\n";
    private final static short DATA_FOR_SIGNING_MAX_SIZE_IN_BYTES = (short) 1024;
    private final static short SIGNATURE_MAX_SIZE_IN_BYTES  = (short) 74;

    public final static short TMP_BUFFER_SIZE = (short) 268;

    private final static byte ALG_ECDSA_SHA_256 = (byte) 33;

    private static KeyPair kp;

    private ECPublicKey publicKey;

    private ECPrivateKey privateKey;

    private KeyAgreement keyAgreement;

    // Buffer for storing extended APDUs
    private byte[] dataForSigning;
    private byte[] signatureBytes;

    private byte[] tmpBuffer;


    private Signature sig;
    private Signature  verifierSignature;

    private short numberOfBytesReceived = 0;
    private short dataForSigningTotalLen = 0;
    private short sigLen = 0;


    public BtcCryptoHandler(){
        keyAgreement = KeyAgreement.getInstance(Constants.ALG_EC_SVDP_DH_PLAIN, false);
        sig = Signature.getInstance(Signature.ALG_ECDSA_SHA, false);
        verifierSignature = Signature.getInstance(Signature.ALG_ECDSA_SHA, false);
        dataForSigning = JCSystem.makeTransientByteArray(DATA_FOR_SIGNING_MAX_SIZE_IN_BYTES, JCSystem.CLEAR_ON_DESELECT);
        signatureBytes = JCSystem.makeTransientByteArray(SIGNATURE_MAX_SIZE_IN_BYTES, JCSystem.CLEAR_ON_DESELECT);
        try {
            tmpBuffer = JCSystem.makeTransientByteArray((short) TMP_BUFFER_SIZE, JCSystem.CLEAR_ON_DESELECT);
        } catch (SystemException e) {
            tmpBuffer = new byte[TMP_BUFFER_SIZE];
        }
    }

    public void setupMasterKeyWithoutSeed(){
        kp = new KeyPair(KeyPair.ALG_EC_FP, KeyBuilder.LENGTH_EC_FP_192/*(short) 256*/);
        kp.genKeyPair();
        privateKey = (ECPrivateKey) kp.getPrivate();
        publicKey = (ECPublicKey) kp.getPublic();
       // Secp256k1.setCommonCurveParameters(publicKey);
    }

    public void getPublicMasterKey(APDU apdu){
        byte[] buffer = apdu.getBuffer();
        short len = publicKey.getW(buffer, (short) 2);
        Util.setShort(buffer, (short) 0, len);
        apdu.setOutgoingAndSend((short) 0, (short) (len + 2));
    }

    /**
     * This function returns the public key associated with a particular private key stored
     * in the applet. The exact key blob contents depend on the keys algorithm and type.
     *
     * ins: 0x35
     * p1: 0x00
     * p2: 0x00
     * data: none
     * return(SECP256K1): [coordx_size(2b) | pubkey_coordx | sig_size(2b) | sig]
     */

    public void getPublicMasterKeyUsingKeyAgreement(APDU apdu){
        byte[] buffer = apdu.getBuffer();

        if (privateKey.getType() == KeyBuilder.TYPE_EC_FP_PRIVATE){
            if (privateKey.getSize() != Constants.LENGTH_EC_FP_256)
                ISOException.throwIt(Constants.SW_INCORRECT_ALG);

            // check the curve param
            if(!Secp256k1.checkCurveParameters((ECPrivateKey) privateKey, tmpBuffer, (short) 0))
                ISOException.throwIt(Constants.SW_INCORRECT_ALG);


            // compute the corresponding partial public key...
            keyAgreement.init((ECPrivateKey) privateKey);
            short coordxSize = keyAgreement.generateSecret(Secp256k1.SECP256K1, Secp256k1.OFFSET_SECP256K1_G, (short) 65, buffer, (short) 2); // compute x coordinate of public key as k*G

            Util.setShort(buffer, (short) 0, coordxSize);

            // sign fixed message
            sig.init(privateKey, Signature.MODE_SIGN);

            short sign_size  = sig.sign(buffer, (short) 0, (short) (coordxSize + 2), buffer, (short) (coordxSize + 4));

            Util.setShort(buffer, (short) (coordxSize + 2), sign_size);


            // return x-coordinate of public key+signatureBytes
            // the client can recover full public-key from the signatureBytes or
            // by guessing the compression value () and verifying the signatureBytes...
            apdu.setOutgoingAndSend((short) 0, (short) (2 + coordxSize + 2 + sign_size));
        }
        else {
            ISOException.throwIt(Constants.SW_INCORRECT_ALG);
        }
    }


    public void setDataForSigning(APDU apdu){
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
        }
    }

    public void signShortMessage(APDU apdu){
        byte[] buffer = apdu.getBuffer();
        try
        {
            
            sig.init(privateKey, Signature.MODE_SIGN);
            short sigLen = sig.sign(dataForSigning, (short) 0, dataForSigningTotalLen, signatureBytes, (short) 0);
            Util.setShort(buffer, (short) 0, sigLen);
            Util.arrayCopy(signatureBytes, (short) 0, buffer, (short) 2, (short) sigLen);
            apdu.setOutgoingAndSend((short) 0, (short) (sigLen + 2));
        }
        catch(CryptoException e)
        {
            ISOException.throwIt((short)(0x9B00 | e.getReason()));
        }
    }
    
    public void verifySignature(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
	    try
        {
			verifierSignature.init(publicKey, Signature.MODE_VERIFY);
			boolean mark = true;//sig.verify(dataForSigning, (short)0, dataForSigningTotalLen, signatureBytes, (short)0, sigLen);                                                                   
			buffer[0] = mark == true ? (byte)0x01 : (byte)0x00;
			apdu.setOutgoingAndSend((short) 0, (short) 1);
        }
        catch(CryptoException e)
        {
            ISOException.throwIt(e.getReason());
        }
        	
	    
    }
}

