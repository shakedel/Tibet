package general;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SequenceFileReader {

   private StringBuffer _sbuffer = new StringBuffer("");


   public SequenceFileReader(String filename) {
      try {
         FileInputStream e = new FileInputStream(filename);
         BufferedReader myInput = new BufferedReader(new InputStreamReader(e));

         String thisLine;
         while((thisLine = myInput.readLine()) != null) {
            this._sbuffer.append(thisLine.trim().toLowerCase());
         }
      } catch (Exception var5) {
         System.out.println("File " + filename + " is not found where expected or is of invalid type. ");
         System.exit(1);
      }

   }

   public String getSequence() {
      return this._sbuffer.toString();
   }
}
