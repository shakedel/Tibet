package general;

import algorithms.APBT;
import algorithms.BYGU;
import general.Interval;
import general.SequenceFileReader;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import suffixtree.SuffixTreeNode;

public class CompareOutputs {

   private static boolean isSolution1ContainedInSolution2(Interval curr, List list2) {
      for(int i = 0; i < list2.size(); ++i) {
         Interval curr2 = (Interval)list2.get(i);
         if(curr2.getStart().getIndex1() == curr.getStart().getIndex1() && curr2.getStart().getIndex2() == curr.getStart().getIndex2() && curr2.getEnd().getIndex1() == curr.getEnd().getIndex1() && curr2.getEnd().getIndex2() == curr.getEnd().getIndex2()) {
            return true;
         }

         if(curr2.getStart().getIndex1() <= curr.getStart().getIndex1() && curr2.getStart().getIndex2() <= curr.getStart().getIndex2() && curr2.getEnd().getIndex1() >= curr.getEnd().getIndex1() && curr2.getEnd().getIndex2() >= curr.getEnd().getIndex2()) {
            return true;
         }
      }

      return false;
   }

   public static void main(String[] args) {
      String file1 = "";
      String file2 = "";
      boolean maxDiff = false;
      boolean minLen = false;
      boolean identical = true;

      try {
         file1 = args[0];
         file2 = args[1];
         int var26 = Integer.parseInt(args[5]);
         int var27 = Integer.parseInt(args[4]);
         String e = args[2];
         String suffixTreeFileName2 = args[3];
         SequenceFileReader reader = new SequenceFileReader(file1);
         String seq1 = reader.getSequence();
         reader = new SequenceFileReader(file2);
         String seq2 = reader.getSequence();
         System.out.println("APBT algorithm");
         long start = System.currentTimeMillis();
         APBT algorithm2 = new APBT(seq1.toCharArray(), seq2.toCharArray(), var27, var26);
         algorithm2.process();
         algorithm2.processByColumn(seq2.toCharArray(), seq1.toCharArray());
         long howlong = System.currentTimeMillis() - start;
         System.out.println("APBT Processed in " + howlong + " ms.");
         List solutions2 = algorithm2.getMaximalSolutions();
         System.out.println("Produced output size=" + solutions2.size());
         SuffixTreeNode tree1 = null;
         SuffixTreeNode tree2 = null;

         FileInputStream algorithm1;
         ObjectInputStream solutions1;
         try {
            algorithm1 = new FileInputStream(e);
            solutions1 = new ObjectInputStream(algorithm1);
            tree1 = (SuffixTreeNode)solutions1.readObject();
            solutions1.close();
            algorithm1.close();
         } catch (Exception var24) {
            System.out.println("File " + e + " not found where expected or is of an invalid type");
            var24.printStackTrace();
            System.exit(1);
         }

         try {
            algorithm1 = new FileInputStream(suffixTreeFileName2);
            solutions1 = new ObjectInputStream(algorithm1);
            tree2 = (SuffixTreeNode)solutions1.readObject();
            solutions1.close();
            algorithm1.close();
         } catch (Exception var23) {
            System.out.println("File " + suffixTreeFileName2 + " not found where expected or is of an invalid type");
            var23.printStackTrace();
            return;
         }

         System.out.println("BYGU algorithm");
         start = System.currentTimeMillis();
         BYGU var28 = new BYGU(tree1, tree2, seq1, "$", seq2, "#", var26, var27);
         var28.process();
         howlong = System.currentTimeMillis() - start;
         System.out.println("BYGU Processed in " + howlong + " ms.");
         List var29 = var28.getMaximalSolutions();
         System.out.println("Output size=" + var29.size());

         int i;
         Interval curr;
         for(i = 0; i < var29.size(); ++i) {
            curr = (Interval)var29.get(i);
            if(!isSolution1ContainedInSolution2(curr, solutions2)) {
               identical = false;
               System.out.println("******************");
               System.out.println("Found by BYGU, missed by APBT: " + curr);
               System.out.println(seq1.substring(curr.getStart().getIndex1(), curr.getEnd().getIndex1() + 1));
               System.out.println(seq2.substring(curr.getStart().getIndex2(), curr.getEnd().getIndex2() + 1));
               System.out.println();
            }
         }

         for(i = 0; i < solutions2.size(); ++i) {
            curr = (Interval)solutions2.get(i);
            if(!isSolution1ContainedInSolution2(curr, var29)) {
               identical = false;
               System.out.println("******************");
               System.out.println("Found by APBT, missed by BYGU: " + curr);
               System.out.println(seq1.substring(curr.getStart().getIndex1(), curr.getEnd().getIndex1() + 1));
               System.out.println(seq2.substring(curr.getStart().getIndex2(), curr.getEnd().getIndex2() + 1));
               System.out.println();
            }
         }
      } catch (Exception var25) {
         System.out.println("Usage: \njava -Xmx512M -Xms512m -classpath classes algorithms.BYGUPerformance \\ \n<filename1> <filename2> \\ \n<suffixtreefilename1> <suffixtreefilename2> \\ \n<minLength> <maxDifferences>");
         System.exit(1);
      }

      if(identical) {
         System.out.println("The outputs are identical.");
      }

   }
}
