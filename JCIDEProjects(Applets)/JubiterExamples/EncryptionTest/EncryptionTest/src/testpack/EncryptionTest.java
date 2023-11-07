package testpack ;

import javacard.framework.*;

import javacard.framework.*; 
import javacard.security.*; 
import javacard.security.KeyPair;
import com.ftsafe.security.*;
import javacardx.crypto.Cipher;


public class EncryptionTest extends Applet
{

	final static byte INS_GEN_KEY_PAIR = 0x01; 
	final static byte INS_GET_PUB_KEY = 0x02; 
	final static byte INS_SET_OTHER_PUB_KEY = 0x03; 
	final static byte INS_INIT_KEYAGRESSMENT = 0x04; 
	final static byte INS_GET_SECERET_DATA = 0x05; 
	final static byte INS_SET_PRI_KEY = 0x06; 
	final static byte INS_GET_PRI_KEY = 0x07; 
	final static byte INS_SET_PUB_KEY = 0x08;
	KeyPair Xkeypair; 
	XECPublicKey pubkey ; 
	XECPrivateKey prikey ; 
	byte[] pubkey_data; 
	short pubkey_len; 
	NamedParameterSpec params; 
	KeyAgreement XDH_keyagress;
	Cipher cipher;

	final static byte[] k1 = { (byte)0xC4,(byte)0x9A,(byte)0x44,(byte)0xBA,(byte)0x44,(byte)0x22,(byte)0x6A,(byte)0x50,( byte)0x18,(byte)0x5A,(byte)0xFC,(byte)0xC1,(byte)0x0A,(byte)0x4C,(byte)0x14,(byte)0x62, (byte)0xDD,(byte)0x5E,(byte)0x46,(byte)0x82,(byte)0x4B,(byte)0x15,(byte)0x16,(byte)0x3B,( byte)0x9D,(byte)0x7C,(byte)0x52,(byte)0xF0,(byte)0x6B,(byte)0xE3,(byte)0x46,(byte)0xA5 }; 
	final static byte[] InU1 = { (byte)0x4C,(byte)0x1C,(byte)0xAB,(byte)0xD0,(byte)0xA6,(byte)0x03,(byte)0xA9,(byte)0x10,( byte)0x3B,(byte)0x35,(byte)0xB3,(byte)0x26,(byte)0xEC,(byte)0x24,(byte)0x66,(byte)0x72, (byte)0x7C,(byte)0x5F,(byte)0xB1,(byte)0x24,(byte)0xA4,(byte)0xC1,(byte)0x94,(byte)0x35,( byte)0xDB,(byte)0x30,(byte)0x30,(byte)0x58,(byte)0x67,(byte)0x68,(byte)0xDB,(byte)0xE6 };
	final static byte[] OutU1 = { (byte)0x52,(byte)0x85,(byte)0xA2,(byte)0x77,(byte)0x55,(byte)0x07,(byte)0xB4,(byte)0x54,( byte)0xF7,(byte)0x71,(byte)0x1C,(byte)0x49,(byte)0x03,(byte)0xCF,(byte)0xEC,(byte)0x32, (byte)0x4F,(byte)0x08,(byte)0x8D,(byte)0xF2,(byte)0x4D,(byte)0xEA,(byte)0x94,(byte)0x8E,( byte)0x90,(byte)0xC6,(byte)0xE9,(byte)0x9D,(byte)0x37,(byte)0x55,(byte)0xDA,(byte)0xC3 };
	
	public EncryptionTest() { 
		short attributes;
		params = NamedParameterSpec.getInstance(NamedParameterSpec.X25519);
		attributes = KeyBuilderX.ATTR_PUBLIC; 
		attributes |= JCSystem.MEMORY_TYPE_PERSISTENT; 
		pubkey = (XECPublicKey)KeyBuilderX.buildXECKey(params, attributes, false); 
		pubkey_data = new byte[256]; pubkey_len = 32;
		attributes = KeyBuilderX.ATTR_PRIVATE; 
		attributes |= JCSystem.MEMORY_TYPE_PERSISTENT; 
		prikey = (XECPrivateKey)KeyBuilderX.buildXECKey(params, attributes, false);
		Xkeypair = new KeyPair((PublicKey)pubkey, (PrivateKey)prikey);
		XDH_keyagress = KeyAgreement.getInstance(KeyAgreementX.ALG_XDH, false);
		cipher = Cipher.getInstance(SignatureX.SIG_CIPHER_EDDSA, false); 
	}

	
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new EncryptionTest().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		short len,len2; 
		if (selectingApplet()) { 
			return; 
		}
		
		byte[] buf = apdu.getBuffer(); 
		
		switch (buf[ISO7816.OFFSET_INS]) { 
			case (byte)INS_GEN_KEY_PAIR: 
				Xkeypair.genKeyPair(); 
				len = pubkey.getEncoded(buf, (short)0); 
				len2 = prikey.getEncoded(buf, (short)len); 
				apdu.setOutgoingAndSend((short)0, (short)(len + len2)); 
				break; 
			case (byte)INS_GET_PUB_KEY: 
				if (!pubkey.isInitialized()) 
					{ Xkeypair.genKeyPair(); }
				len = pubkey.getEncoded(buf, (short)0); 
				//cipher.init(pubkey, Cipher.MODE_ENCRYPT);
				apdu.setOutgoingAndSend((short)0, len); 
				break; 
			case (byte)INS_SET_OTHER_PUB_KEY: 
				pubkey_len = apdu.setIncomingAndReceive(); 
				Util.arrayCopyNonAtomic(buf, ISO7816.OFFSET_CDATA, pubkey_data, (short)0, pubkey_len);
				break; 
			case (byte)INS_INIT_KEYAGRESSMENT: 
				XDH_keyagress.init(prikey); 
				break; 
			case (byte)INS_GET_SECERET_DATA: 
				len = XDH_keyagress.generateSecret(pubkey_data, (short)0, pubkey_len, buf, (short)0);
				apdu.setOutgoingAndSend((short)0, len); 
				break; 
			case (byte)INS_SET_PRI_KEY: 
				len = apdu.setIncomingAndReceive(); 
				if (!prikey.isInitialized()) 
					{ Xkeypair.genKeyPair(); }
				prikey.setEncoded(buf, (short)ISO7816.OFFSET_CDATA, (short)len); 
				//apdu.setOutgoingAndSend((short)0, len); 
				break; 
			case (byte)INS_GET_PRI_KEY:
				if (!prikey.isInitialized()) 
					{ Xkeypair.genKeyPair(); }
				len = prikey.getEncoded(buf, (short)0); 
				apdu.setOutgoingAndSend((short)0, len); 
				break; 
			case (byte)INS_SET_PUB_KEY: 
				len = apdu.setIncomingAndReceive(); 
				if (!pubkey.isInitialized()) 
					{ Xkeypair.genKeyPair(); }
				pubkey.setEncoded(buf, (short)ISO7816.OFFSET_CDATA, (short)len); 
				//apdu.setOutgoingAndSend((short)0, len); 
				break; 
			default: ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED); }

	}

}
