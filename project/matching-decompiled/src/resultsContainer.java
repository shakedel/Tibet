import general.IndexPair;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class resultsContainer {

   private ConcurrentLinkedQueue results = new ConcurrentLinkedQueue();
   private double maxIntersection;


   public resultsContainer(double maxIntersection) {
      this.maxIntersection = maxIntersection;
   }

   public synchronized void writeResults(String path) {
      try {
         FileWriter fstream = new FileWriter(path);
         BufferedWriter out = new BufferedWriter(fstream);
         Iterator var5 = this.results.iterator();

         while(var5.hasNext()) {
            MatchResult iResult = (MatchResult)var5.next();
            out.write(iResult.toString());
            out.newLine();
         }

         out.close();
      } catch (Exception var6) {
         ;
      }

   }

   public synchronized void addResult(MatchResult newResult) {
      this.results.add(newResult);
   }

   private static double computeIntersectionScore(MatchResult result1, MatchResult result2) {
      IndexPair xIntersection = intervalIntersection(new IndexPair(result1.workInterval.getStart().getIndex1(), result1.workInterval.getEnd().getIndex1()), new IndexPair(result2.workInterval.getStart().getIndex1(), result2.workInterval.getEnd().getIndex1()));
      IndexPair yIntersection = intervalIntersection(new IndexPair(result1.workInterval.getStart().getIndex2(), result1.workInterval.getEnd().getIndex2()), new IndexPair(result2.workInterval.getStart().getIndex2(), result2.workInterval.getEnd().getIndex2()));
      if(xIntersection != null && yIntersection != null) {
         double intersectionSize = (double)((xIntersection.getIndex2() - xIntersection.getIndex1()) * (yIntersection.getIndex2() - yIntersection.getIndex1()));
         double square1Size = (double)((result1.workInterval.getEnd().getIndex1() - result1.workInterval.getStart().getIndex1()) * (result1.workInterval.getEnd().getIndex2() - result1.workInterval.getStart().getIndex2()));
         double square2Size = (double)((result2.workInterval.getEnd().getIndex1() - result2.workInterval.getStart().getIndex1()) * (result2.workInterval.getEnd().getIndex2() - result2.workInterval.getStart().getIndex2()));
         return intersectionSize / (square1Size + square2Size - intersectionSize);
      } else {
         return 0.0D;
      }
   }

   private static IndexPair intervalIntersection(IndexPair firstInterval, IndexPair secondInterval) {
      return firstInterval.getIndex1() <= secondInterval.getIndex1()?(firstInterval.getIndex2() <= secondInterval.getIndex1()?null:(firstInterval.getIndex2() >= secondInterval.getIndex2()?new IndexPair(secondInterval.getIndex1(), secondInterval.getIndex2()):new IndexPair(secondInterval.getIndex1(), firstInterval.getIndex2()))):intervalIntersection(secondInterval, firstInterval);
   }
}
