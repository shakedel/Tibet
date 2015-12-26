import java.security.SecureRandom;
import java.math.BigInteger;

public class Utils 
{

	  private static SecureRandom random = new SecureRandom();

	  public static String nextSessionId()
	  {
	    return new BigInteger(130, random).toString(32);
	  }

}

