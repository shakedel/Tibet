package general;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class RandomStringGenerator {

   public static String getRandomString(int len, char[] alphabet) {
      char[] seqAsArr = new char[len];
      Random rand = new Random();

      for(int i = 0; i < seqAsArr.length; ++i) {
         seqAsArr[i] = alphabet[rand.nextInt(alphabet.length)];
      }

      return new String(seqAsArr);
   }

   public static void main(String[] args) throws Exception {
      short N = 10000;
      char[] alphabet = new char[]{'a', 'c', 'g', 't'};
      String randfilename = "rand10000_2";
      String seq = getRandomString(N, alphabet);
      BufferedWriter bwout = new BufferedWriter(new FileWriter(randfilename));
      bwout.write(seq);
      bwout.close();
   }
}
