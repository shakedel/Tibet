import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import general.IndexPair;
import general.Interval;



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
	
	public APBTWorkierThread(String firstStr, String secondStr, Interval workInterval, int minLength, int maxErrorDistance, resultsContainer rContainer, ConcurrentCounter tCounter, int threadId)
	{
			this.workInterval = workInterval;
		    this.firstStr = firstStr;
		    this.secondStr = secondStr;
		    this.minLength = minLength;
		    this.maxErrorDistance = maxErrorDistance;
		    this.rContainer = rContainer;
		    this.tCounter = tCounter;
		    this.threadId = threadId;
	}

	
	@Override
     public void run() 
	 {
	    this.startTime = System.currentTimeMillis();
	    
		String strA = firstStr.substring(workInterval.getStart().getIndex1(), workInterval.getEnd().getIndex1());
		String strB = secondStr.substring(workInterval.getStart().getIndex2(), workInterval.getEnd().getIndex2());
		
		if (strA.isEmpty())
		{
			System.out.println("why " + strA.length() + " " + strB.length() + " " + firstStr.length() + " " + secondStr.length() + "  " + workInterval.getStart().getIndex1() + " " + workInterval.getEnd().getIndex1() + " "  + workInterval.getStart().getIndex2() + " " + workInterval.getEnd().getIndex2());
			
		}
		algorithms.APBT newAPBT = new algorithms.APBT(strA.toCharArray(), strB.toCharArray(), minLength, maxErrorDistance);
		newAPBT.process();
		newAPBT.processByColumn(strB.toCharArray(), strA.toCharArray());
		List maximalsolutions = newAPBT.getMaximalSolutions();
		
		if (maximalsolutions.size() > 0)
		{
			//System.out.println("Found " + ((Integer)maximalsolutions.size()).toString() + " max solution between " + i.toString() + " from A and " + j.toString() + " from B");
			
			for(Integer ms=0;ms<maximalsolutions.size();ms++)
			{
					Interval curr=(Interval)maximalsolutions.get(ms);
					double score = LevenshteinDistance.computeLevenshteinDistance(strA.substring(curr.getStart().getIndex1(),curr.getEnd().getIndex1()+1), strB.substring(curr.getStart().getIndex2(),curr.getEnd().getIndex2()+1));
					
					IndexPair firstIndexPair = new IndexPair(curr.getStart().getIndex1() + workInterval.getStart().getIndex1(), curr.getStart().getIndex2() + workInterval.getStart().getIndex2());
					IndexPair secondIndexPair = new IndexPair(curr.getEnd().getIndex1() + workInterval.getStart().getIndex1(), curr.getEnd().getIndex2() + workInterval.getStart().getIndex2());
					
					Interval matchInterval = new Interval(firstIndexPair, secondIndexPair);
					MatchResult newResult = new MatchResult(matchInterval, score, threadId, ms);
					
					//System.out.println("ThreadId:" + threadId + "_A_" + strA.substring(curr.getStart().getIndex1(), curr.getEnd().getIndex1()));
					//System.out.println("ThreadId:" + threadId + "_B_" + strB.substring(curr.getStart().getIndex2(), curr.getEnd().getIndex2()));
					
					
					
					rContainer.addResult(newResult);
					
//					try
//					{
//						  // Create file 
//						FileWriter fstream = new FileWriter(PathResults + i.toString() + "_" + j.toString() + "_" + ms.toString() + ".txt");
//						BufferedWriter out = new BufferedWriter(fstream);
//						//out.write("Hello Java");
//						//Close the output stream
//						
//						out.write(curr.toString());
//						out.newLine();
//						out.write(strA.substring(curr.getStart().getIndex1(),curr.getEnd().getIndex1()+1) + "\n");
//						out.newLine();
//						out.write(strB.substring(curr.getStart().getIndex2(),curr.getEnd().getIndex2()+1) + "\n");
//						out.newLine();
//						out.close();
//					}
//					catch (Exception e)
//					{//Catch exception if any
//						System.err.println("Error: " + e.getMessage());
//					}
				  
					
					
			}
		}
		
		tCounter.decrement();
		double totalTimeInS = (System.currentTimeMillis() - startTime) / 1000.0;
		System.out.println(threadId +": Total Running Time: " + totalTimeInS + ". Only " + tCounter.get() + " tasks to go.");
		
	 }
	 

	
}
