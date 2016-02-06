package tau.cs.wolf.tibet.percentage_apbt;

import algorithms.APBT;
import general.IndexPair;
import general.Interval;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.util.List;

public class Main {

   private static String readFile(String path) throws IOException {
      FileInputStream stream = new FileInputStream(new File(path));

      String var5;
      try {
         FileChannel fc = stream.getChannel();
         MappedByteBuffer bb = fc.map(MapMode.READ_ONLY, 0L, fc.size());
         var5 = Charset.defaultCharset().decode(bb).toString();
      } finally {
         stream.close();
      }

      return var5;
   }

   public static void main(String[] args) {
      byte numOfArgs = 5;
      if(args.length != numOfArgs) {
         System.out.println("Expecting exactly 5 arguments:");
         System.out.println("First input path");
         System.out.println("Second input path");
         System.out.println("Output path");
         System.out.println("Min Length of a match");
         System.out.println("Max Error Distance of a match");
      } else {
         String fileInA = args[0];
         String fileInB = args[1];
         String fileOut = args[2];
         int minLength = Integer.parseInt(args[3]);
         int maxErrorDistance = Integer.parseInt(args[4]);
         String strA = "";

         try {
            strA = readFile(fileInA);
         } catch (IOException var28) {
            System.out.println("Can not find first input file");
            var28.printStackTrace();
            return;
         }

         String strB = "";

         try {
            strB = readFile(fileInB);
         } catch (IOException var27) {
            System.out.println("Can not find second input file");
            var27.printStackTrace();
            return;
         }

         long startTime = System.currentTimeMillis();
         APBT newAPBT = new APBT(strA.toCharArray(), strB.toCharArray(), minLength, maxErrorDistance);
         newAPBT.process();
         newAPBT.processByColumn(strB.toCharArray(), strA.toCharArray());
         List maximalsolutions = newAPBT.getMaximalSolutions();
         long stopTime = System.currentTimeMillis();
         long elapsedTime = stopTime - startTime;
         FileWriter fileOutWriter = null;

         try {
            fileOutWriter = new FileWriter(fileOut, false);
         } catch (IOException var26) {
            System.out.println("Can not open output file");
            var26.printStackTrace();
            return;
         }

         PrintWriter pWriter = new PrintWriter(fileOutWriter);
         pWriter.println(fileInA);
         pWriter.println(fileInB);
         pWriter.println(fileOut);
         pWriter.println("Min Length:" + minLength);
         pWriter.println("Max Error:" + maxErrorDistance);
         pWriter.println("Run Time in ms:" + elapsedTime);
         if(maximalsolutions.size() > 0) {
            for(Integer e = Integer.valueOf(0); e.intValue() < maximalsolutions.size(); e = Integer.valueOf(e.intValue() + 1)) {
               Interval curr = (Interval)maximalsolutions.get(e.intValue());
               double score = (double)LevenshteinDistance.computeLevenshteinDistance(strA.substring(curr.getStart().getIndex1(), curr.getEnd().getIndex1() + 1), strB.substring(curr.getStart().getIndex2(), curr.getEnd().getIndex2() + 1));
               IndexPair firstIndexPair = new IndexPair(curr.getStart().getIndex1(), curr.getStart().getIndex2());
               IndexPair secondIndexPair = new IndexPair(curr.getEnd().getIndex1(), curr.getEnd().getIndex2());
               pWriter.println(Integer.valueOf(firstIndexPair.getIndex1()).toString() + "," + Integer.valueOf(firstIndexPair.getIndex2()).toString() + "," + Integer.valueOf(secondIndexPair.getIndex1()).toString() + "," + Integer.valueOf(secondIndexPair.getIndex2()).toString() + "," + Double.valueOf(score).toString());
            }
         }

         try {
            fileOutWriter.close();
         } catch (IOException var25) {
            var25.printStackTrace();
         }

      }
   }
}
