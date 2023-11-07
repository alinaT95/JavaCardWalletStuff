package JuBiterPackage ;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.Cipher;
 
import com.ftsafe.security.*;


public class JuBiterPackage extends Applet
{
	final byte INS_ED25519_DEMO   = 0x01;
    final byte INS_CREATE_XEC_KEY = 0x02;
	final byte INS_SET_XEC_KEY    = 0x03;
	final byte INS_GET_XEC_KEY    = 0x04;
	final byte INS_SET_MSG        = 0x05;	
	final byte INS_SIGNATURE_SIGN = 0x06;
	final byte INS_SIGNATURE_VERIFY = 0x07;	
	final byte INS_GC               = 0x08;	
		
	Key  myKey;
	Signature sig;
	KeyPair keypair;
	XECPublicKey pubkey ;
	XECPrivateKey prikey ;
	NamedParameterSpec params;

	byte sigBuf[] ;
	short siglen;
	byte msg[] ; 
	short msglen; 
    public  JuBiterPackage()
    {
		sigBuf = JCSystem.makeTransientByteArray((short)100, JCSystem.CLEAR_ON_DESELECT);
		msg = new byte[1024];  
		msglen = 0;	  
		sig = Signature.getInstance((byte)0/*MessageDigest.ALG_NULL*/,SignatureX.SIG_CIPHER_EDDSA,(byte)0/*Cipher.PAD_NULL*/, false);  //jc305 format 
		siglen = 0;
		params = NamedParameterSpec.getInstance(NamedParameterSpec.ED25519);
    }	  
    
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new JuBiterPackage().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		
	    short recvLen = apdu.setIncomingAndReceive();
	    short Lc = apdu.getIncomingLength();
		byte p1 = buf[ISO7816.OFFSET_P1];
		byte p2 = buf[ISO7816.OFFSET_P2];
		
		switch (buf[ISO7816.OFFSET_INS])
		{
		case INS_ED25519_DEMO:  //test 01
			boolean issuccess = Ed22519demo(p1);
			buf[0] = issuccess==true ? (byte)0x01:(byte)0x00;
			apdu.setOutgoingAndSend((short)0, (short)1); 		
			break;
		case INS_CREATE_XEC_KEY:  //02
			short attributes;
			myKey = null;
			if (p1 != 0) 
				attributes = KeyBuilderX.ATTR_PRIVATE;
			else 
				attributes = KeyBuilderX.ATTR_PUBLIC;
	
			attributes |= JCSystem.MEMORY_TYPE_PERSISTENT;
			myKey = KeyBuilderX.buildXECKey(params, attributes, false);		
			break;
		case INS_SET_XEC_KEY:  //03
			if (!(myKey instanceof XECKey)) 
				ISOException.throwIt((short) 0x6e09);
			((XECKey)myKey).setEncoded(buf, (short)ISO7816.OFFSET_CDATA, (short) 32);
			break;
		case INS_GET_XEC_KEY:  //04
			////if (!(myKey instanceof XECKey)) 
				//ISOException.throwIt((short) 0x6e09);
			short len = ((XECKey)myKey).getEncoded(buf, (short) 0);
			apdu.setOutgoingAndSend((short) 0, len);
			break;	
		case INS_SET_MSG:  //05			
			if(false)
			{//JuBier Blade did not support extend APDU.
				short offData = apdu.getOffsetCdata();
				short pointer = (short) 0;
				while (recvLen > (short) 0) 
				{
					Util.arrayCopy(buf, offData, msg, pointer, recvLen);
					pointer += recvLen;
					recvLen = apdu.receiveBytes(offData);
				}
			// send the byte msg
				msglen = Lc ;
				apdu.setOutgoing();
				apdu.setOutgoingLength((short)Lc);
				apdu.sendBytesLong(msg, (short) 0, (short)Lc);
			}
			else
			{
				if(p2 == 0)
				{
					msglen = 0;
					Util.arrayFillNonAtomic(msg, (short)0, (short)msg.length, (byte)0);
				}
				if(p1 == (byte)0x80)
				{
					//		
				}
				Util.arrayCopy(buf, ISO7816.OFFSET_CDATA, msg, msglen, Lc);
				
				msglen += Lc;
			}
			break;				
		case INS_SIGNATURE_SIGN: // sign 06
			if (!(myKey instanceof XECKey)) 
				ISOException.throwIt((short) 0x6e09);		
			try
			{
				sig.init(myKey, Signature.MODE_SIGN);
				siglen = sig.sign(msg, (short) 0, (short)msglen, sigBuf, (short) 0);
				Util.arrayCopy(sigBuf, (short)0, buf, (short)0, (short)siglen);	        
				apdu.setOutgoingAndSend((short)0, siglen);			
			}
			catch(CryptoException e)
			{
				ISOException.throwIt((short)(0x9B00 | e.getReason()));
			}			
			break;
		case INS_SIGNATURE_VERIFY: // verify 07
			if (!(myKey instanceof XECKey)) 
				ISOException.throwIt((short) 0x6e09);				
			Util.arrayCopyNonAtomic(buf, (short) ISO7816.OFFSET_CDATA, sigBuf, (short)0, Lc);
			siglen = Lc;
			boolean mark = false;
			try
			{
				sig.init(myKey, Signature.MODE_VERIFY);
				mark = sig.verify(msg, (short)0, (short)msglen, sigBuf, (short)0, (short)siglen);                                                                   
				buf[0] = mark==true ? (byte)0x01:(byte)0x00;
				//Util.arrayCopy(msg, (short)0, buf, (short)1, (short)msglen);
				//Util.arrayCopy(sigBuf, (short)0, buf, (short)(1+msglen), (short)siglen);
				
				apdu.setOutgoingAndSend((short)0, (short)1); //(1 + msglen + siglen));  	
			}
			catch(CryptoException e)
			{
				ISOException.throwIt(e.getReason());
			}			
			break;
		case INS_GC:
			myKey = null;
			JCSystem.requestObjectDeletion();
			break;												
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	
	boolean Ed22519demo(short testcase) 
	{
		pubkey = null;
		prikey = null;
        boolean mark = false;
		//try
		//{        
		byte priKeyBuf[] = { (byte) 0x4c, (byte) 0xcd, (byte) 0x08, (byte) 0x9b, (byte) 0x28, (byte) 0xff, (byte) 0x96,
				(byte) 0xda, (byte) 0x9d, (byte) 0xb6, (byte) 0xc3, (byte) 0x46, (byte) 0xec, (byte) 0x11, (byte) 0x4e,
				(byte) 0x0f, (byte) 0x5b, (byte) 0x8a, (byte) 0x31, (byte) 0x9f, (byte) 0x35, (byte) 0xab, (byte) 0xa6,
				(byte) 0x24, (byte) 0xda, (byte) 0x8c, (byte) 0xf6, (byte) 0xed, (byte) 0x4f, (byte) 0xb8, (byte) 0xa6,
				(byte) 0xfb };
		byte pubKeyBuf[] = { (byte) 0x3d, (byte) 0x40, (byte) 0x17, (byte) 0xc3, (byte) 0xe8, (byte) 0x43, (byte) 0x89,
				(byte) 0x5a, (byte) 0x92, (byte) 0xb7, (byte) 0x0a, (byte) 0xa7, (byte) 0x4d, (byte) 0x1b, (byte) 0x7e,
				(byte) 0xbc, (byte) 0x9c, (byte) 0x98, (byte) 0x2c, (byte) 0xcf, (byte) 0x2e, (byte) 0xc4, (byte) 0x96,
				(byte) 0x8c, (byte) 0xc0, (byte) 0xcd, (byte) 0x55, (byte) 0xf1, (byte) 0x2a, (byte) 0xf4, (byte) 0x66,
				(byte) 0x0c };
		msg[0] =  0x72 ;
        msglen = 1;

		short attributes;
		try
		{
			if (testcase == 0) // buildkey
			{// buildkey
				// set private key
				attributes = KeyBuilderX.ATTR_PRIVATE;
				attributes |= JCSystem.MEMORY_TYPE_PERSISTENT;
				prikey = (XECPrivateKey)KeyBuilderX.buildXECKey(params, attributes, false);
				prikey.setEncoded(priKeyBuf, (short) 0, (short) 32);

				// set public key
				attributes = KeyBuilderX.ATTR_PUBLIC;
				attributes |= JCSystem.MEMORY_TYPE_PERSISTENT;
				pubkey =  (XECPublicKey)KeyBuilderX.buildXECKey(params, attributes, false);
				pubkey.setEncoded(pubKeyBuf, (short) 0, (short) 32);
			} 
			else if (testcase == 1) // keypair
			{
				// buildkey
				// set private key
				attributes = KeyBuilderX.ATTR_PRIVATE;
				attributes |= JCSystem.MEMORY_TYPE_PERSISTENT;
				prikey = (XECPrivateKey)KeyBuilderX.buildXECKey(params, attributes, false);
				prikey.setEncoded(priKeyBuf, (short) 0, (short) 32);
				prikey.getEncodingLength();

				// set public key
				attributes = KeyBuilderX.ATTR_PUBLIC;
				attributes |= JCSystem.MEMORY_TYPE_PERSISTENT;
				pubkey =  (XECPublicKey)KeyBuilderX.buildXECKey(params, attributes, false);
				pubkey.setEncoded(pubKeyBuf, (short) 0, (short) 32);
				pubkey.getEncodingLength();
				
				// keypair = new KeyPair(KeyPairX.ALG_ED25519, (short) 255);
				// keypair.genKeyPair();
				// prikey = (XECKey) keypair.getPrivate();
				// pubkey = (XECKey) keypair.getPublic();
			}
		}
        catch(CryptoException e)
        {
        	 ISOException.throwIt((short)(0x9B01));
        }		
		// sign
		try
        {
			sig.init(prikey, Signature.MODE_SIGN);
			sig.sign(msg, (short) 0, (short)msglen, sigBuf, (short) 0);
        }
        catch(CryptoException e)
        {
            ISOException.throwIt(e.getReason());
        } 
		// verify
		try
        {		
			sig.init(pubkey, Signature.MODE_VERIFY);
			mark = sig.verify(msg, (short) 0, (short)msglen, sigBuf, (short) 0, (short) 64);
        }
        catch(CryptoException e)
        {	
             ISOException.throwIt(e.getReason());
        }	
        return mark;
	}	

}
