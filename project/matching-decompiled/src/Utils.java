import java.math.BigInteger;
import java.security.SecureRandom;

public class Utils {

   private static SecureRandom random = new SecureRandom();


   public static String nextSessionId() {
      return (new BigInteger(130, random)).toString(32);
   }
}
