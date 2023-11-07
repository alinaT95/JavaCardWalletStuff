package ton.nfc;


import java.security.KeyStore;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class HmacHelper {
    public static final String HMAC_KEY_ALIAS = "hmac_key_alias";

    private static HmacHelper hmacHelper;

    public static HmacHelper getInstance() {
        if (hmacHelper == null) hmacHelper = new HmacHelper();
        return hmacHelper;
    }

    private HmacHelper(){}

    public static byte[] computeMac(byte[] key, byte[] data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
        sha256_HMAC.init(secretKey);
        return sha256_HMAC.doFinal(data);
    }

    public byte[] computeMac(byte[] data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(HMAC_KEY_ALIAS, null);
        if (!(entry instanceof KeyStore.SecretKeyEntry)) {
            System.out.println("Not an instance of a SecretKeyEntry");
            return null;
        }
        sha256_HMAC.init(((KeyStore.SecretKeyEntry) entry).getSecretKey());
        return sha256_HMAC.doFinal(data);
    }
}

