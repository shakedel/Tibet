package tau.cs.wolf.tibet.percentage_apbt.concurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.concurrent.ConcurrentLinkedQueue;

import general.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.misc.Misc.Formatter;

public class ResultsContainer {

	private ConcurrentLinkedQueue<MatchResult> matches;
	private double maxIntersection;
	private PrintStream out = null;
	private final Formatter<MatchResult> formatter;
	
	public ResultsContainer(Formatter<MatchResult> formatter, double maxIntersection) {
		matches = new ConcurrentLinkedQueue<MatchResult>();
		this.maxIntersection = maxIntersection;
		this.formatter = formatter;
	}
	
	public ResultsContainer(Formatter<MatchResult> formatter, double maxIntersection, PrintStream out) {
		this(formatter, maxIntersection);
		this.out = out;
		
	}
	
	public void writeResults(File f) {
		if (out != null) {
			throw new IllegalStateException("results were already written on-the-fly");
		}
		try (PrintStream out = new PrintStream(f)) {
			for (MatchResult match: matches) {
				out.println(this.formatter.format(match));
			}
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private synchronized void writeMatch(MatchResult match) {
		this.out.println(this.formatter.format(match));
	}
	
	public synchronized void addResult(MatchResult newMatch) {
		matches.add(newMatch);
		if (this.out != null) {
			this.writeMatch(newMatch);
		}
		
		//System.out.println("www " + newResult.score);
		/*LinkedList<MatchResult> overlapResults = new LinkedList<MatchResult>();
		double bestScore = Double.POSITIVE_INFINITY;
			
		for (MatchResult iResult: results)
		{
			double currentIntersectionScore = computeIntersectionScore(iResult, newResult);
			if (currentIntersectionScore > maxIntersection)
			{
				overlapResults.add(iResult);
				bestScore = Math.min(bestScore, iResult.score);
			}
		}
		
		if (overlapResults.size() == 0)
		{
			results.add(newResult);
		}
		else
		{
			String strRand = Utils.nextSessionId();
			if (newResult.score > bestScore)
			{
				System.out.println(strRand +  " Found better results " + results.size());
				for (MatchResult iResult: overlapResults)
				{
					results.remove(iResult);
				}
				
				System.out.println(strRand + " Found better results after deleteing " + results.size());
				
				results.add(newResult);
				System.out.println(strRand + " Found better results after adding " + results.size());
			}
			
		}
		*/
	}
	
	private static double computeIntersectionScore(MatchResult result1, MatchResult result2)
	{
		IndexPair xIntersection = intervalIntersection(new IndexPair(result1.workInterval.getStart().getIndex1(),result1.workInterval.getEnd().getIndex1()), new IndexPair(result2.workInterval.getStart().getIndex1(),result2.workInterval.getEnd().getIndex1()));
		IndexPair yIntersection = intervalIntersection(new IndexPair(result1.workInterval.getStart().getIndex2(),result1.workInterval.getEnd().getIndex2()), new IndexPair(result2.workInterval.getStart().getIndex2(),result2.workInterval.getEnd().getIndex2()));
		
		if ((xIntersection == null) || (yIntersection == null))
		{
			return 0;
		}
		else
		{
			double intersectionSize = (xIntersection.getIndex2() - xIntersection.getIndex1()) * (yIntersection.getIndex2() - yIntersection.getIndex1()); 
			double square1Size = (result1.workInterval.getEnd().getIndex1() - result1.workInterval.getStart().getIndex1()) * (result1.workInterval.getEnd().getIndex2() - result1.workInterval.getStart().getIndex2());
			double square2Size = (result2.workInterval.getEnd().getIndex1() - result2.workInterval.getStart().getIndex1()) * (result2.workInterval.getEnd().getIndex2() - result2.workInterval.getStart().getIndex2());
			return (intersectionSize) / (square1Size + square2Size - intersectionSize);
		}
	}
	
	private static IndexPair intervalIntersection(IndexPair firstInterval, IndexPair secondInterval)
	{
		if (firstInterval.getIndex1() <= secondInterval.getIndex1())
		{
			if (firstInterval.getIndex2() <= secondInterval.getIndex1())
			{
				return null;
			}
			else
			{
				if (firstInterval.getIndex2() >= secondInterval.getIndex2())
				{
					return new IndexPair(secondInterval.getIndex1(), secondInterval.getIndex2());
				}
				else
				{
					return new IndexPair(secondInterval.getIndex1(), firstInterval.getIndex2());
				}
			}
		}
		else
		{
			return intervalIntersection(secondInterval, firstInterval);
		}
	}
}
