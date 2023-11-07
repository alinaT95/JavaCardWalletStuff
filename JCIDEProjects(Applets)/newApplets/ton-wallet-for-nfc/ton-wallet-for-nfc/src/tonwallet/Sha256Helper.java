package tonwallet ;

import javacard.security.*;

public class Sha256Helper  
{
	private static MessageDigest md;
	
	public static MessageDigest getInstance(){
		if (md == null)
			md =  MessageDigest.getInstance(MessageDigest.ALG_SHA_256, false);
		return md;
	}
	

}
