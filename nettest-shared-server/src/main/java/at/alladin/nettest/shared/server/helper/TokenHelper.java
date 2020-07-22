package at.alladin.nettest.shared.server.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class TokenHelper {

    private static String shaSum(final String toConvert) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            return byteArrayToHexString(md.digest(toConvert.getBytes()));
        }
        catch (final NoSuchAlgorithmException | NullPointerException e) {
            return null;
        }
    }
    
    public static String byteArrayToHexString(byte[] b) {
    	String result = "";
    	for (int i=0; i < b.length; i++) {
    		result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
    	}	
    	return result;
    }

    public static String generateToken(final long timestamp, final String secretKey) {
        return shaSum(timestamp+secretKey);
    }
}
