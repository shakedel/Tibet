import algorithms.APBT;
import general.IndexPair;
import general.Interval;
import java.util.List;

public class APBTWorkierThread implements Runnable {

   private String firstStr;
   private String secondStr;
   private Interval workInterval;
   private int minLength;
   private int maxErrorDistance;
   private resultsContainer rContainer;
   private ConcurrentCounter tCounter;
   private int threadId;
   private long startTime;


   public APBTWorkierThread(String firstStr, String secondStr, Interval workInterval, int minLength, int maxErrorDistance, resultsContainer rContainer, ConcurrentCounter tCounter, int threadId) {
      this.workInterval = workInterval;
      this.firstStr = firstStr;
      this.secondStr = secondStr;
      this.minLength = minLength;
      this.maxErrorDistance = maxErrorDistance;
      this.rContainer = rContainer;
      this.tCounter = tCounter;
      this.threadId = threadId;
   }

   public void run() {
      this.startTime = System.currentTimeMillis();
      String strA = this.firstStr.substring(this.workInterval.getStart().getIndex1(), this.workInterval.getEnd().getIndex1());
      String strB = this.secondStr.substring(this.workInterval.getStart().getIndex2(), this.workInterval.getEnd().getIndex2());
      if(strA.isEmpty()) {
         System.out.println("why " + strA.length() + " " + strB.length() + " " + this.firstStr.length() + " " + this.secondStr.length() + "  " + this.workInterval.getStart().getIndex1() + " " + this.workInterval.getEnd().getIndex1() + " " + this.workInterval.getStart().getIndex2() + " " + this.workInterval.getEnd().getIndex2());
      }

      APBT newAPBT = new APBT(strA.toCharArray(), strB.toCharArray(), this.minLength, this.maxErrorDistance);
      newAPBT.process();
      newAPBT.processByColumn(strB.toCharArray(), strA.toCharArray());
      List maximalsolutions = newAPBT.getMaximalSolutions();
      if(maximalsolutions.size() > 0) {
         for(Integer totalTimeInS = Integer.valueOf(0); totalTimeInS.intValue() < maximalsolutions.size(); totalTimeInS = Integer.valueOf(totalTimeInS.intValue() + 1)) {
            Interval curr = (Interval)maximalsolutions.get(totalTimeInS.intValue());
            double score = (double)LevenshteinDistance.computeLevenshteinDistance(strA.substring(curr.getStart().getIndex1(), curr.getEnd().getIndex1() + 1), strB.substring(curr.getStart().getIndex2(), curr.getEnd().getIndex2() + 1));
            IndexPair firstIndexPair = new IndexPair(curr.getStart().getIndex1() + this.workInterval.getStart().getIndex1(), curr.getStart().getIndex2() + this.workInterval.getStart().getIndex2());
            IndexPair secondIndexPair = new IndexPair(curr.getEnd().getIndex1() + this.workInterval.getStart().getIndex1(), curr.getEnd().getIndex2() + this.workInterval.getStart().getIndex2());
            Interval matchInterval = new Interval(firstIndexPair, secondIndexPair);
            MatchResult newResult = new MatchResult(matchInterval, score, this.threadId, totalTimeInS.intValue());
            this.rContainer.addResult(newResult);
         }
      }

      this.tCounter.decrement();
      double totalTimeInS1 = (double)(System.currentTimeMillis() - this.startTime) / 1000.0D;
      System.out.println(this.threadId + ": Total Running Time: " + totalTimeInS1 + ". Only " + this.tCounter.get() + " tasks to go.");
   }
}
