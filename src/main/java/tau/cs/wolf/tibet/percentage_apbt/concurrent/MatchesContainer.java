package tau.cs.wolf.tibet.percentage_apbt.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.data.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class MatchesContainer {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private ConcurrentLinkedQueue<MatchResult> matches;
	private double maxIntersection;
	private boolean shutdown = false;
	
	public MatchesContainer(Utils.Formatter<MatchResult> formatter, double maxIntersection) {
		matches = new ConcurrentLinkedQueue<MatchResult>();
		this.maxIntersection = maxIntersection;
	}
	
	public void shutdown() {
		this.shutdown = true;
	}
	
	private void assertNotShutdown() {
		if (this.shutdown) {
			throw new IllegalStateException("cannot accept results after shutdown!");
		}
	}
	
	private void assertShutdown() {
		if (!this.shutdown) {
			throw new IllegalStateException("must be shutdown after all results has been passed!");
		}
	}
	
	public List<MatchResult> getResults() {
		assertShutdown();
		return Collections.unmodifiableList(new ArrayList<MatchResult>(this.matches));
	}
	
	public synchronized void addResult(MatchResult newMatch) {
		this.assertNotShutdown();
		matches.add(newMatch);
		
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
