package tau.cs.wolf.tibet.percentage_apbt.concurrent;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algorithms.APBT;
import ch.qos.logback.core.util.Duration;
import general.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.BaseModule;
import tau.cs.wolf.tibet.percentage_apbt.misc.LevenshteinDistance;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class WorkerThread extends BaseModule implements Runnable {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String firstStr;
	private String secondStr;
	private Interval workInterval;
	private MatchesContainer rContainer;
	private final int threadId;

	public WorkerThread(Props props, Args args, String firstStr, String secondStr, Interval workInterval, MatchesContainer rContainer, int threadId) {
		super(props, args);
		this.workInterval = workInterval;
		this.firstStr = firstStr;
		this.secondStr = secondStr;
		this.rContainer = rContainer;
		this.threadId = threadId;
	}


	public void run() {
		long startTime = System.currentTimeMillis();
		logger.info("Thread: "+this.threadId+". Started with work interval: "+this.workInterval);
		String strA = firstStr.substring(workInterval.getStart().getIndex1(), workInterval.getEnd().getIndex1());
		String strB = secondStr.substring(workInterval.getStart().getIndex2(), workInterval.getEnd().getIndex2());

		APBT newAPBT = new algorithms.APBT(strA.toCharArray(), strB.toCharArray(), args.getMinLength(), args.getMaxError());
		newAPBT.process();
		newAPBT.processByColumn(strB.toCharArray(), strA.toCharArray());
		logger.info("Thread: "+this.threadId+". Finished processing. Time elapsed: "+new Duration(System.currentTimeMillis()-startTime));
		
		List<Interval> maximalsolutions = Utils.convertIntervalsList(newAPBT.getMaximalSolutions());

		for(int solutionIdx=0; solutionIdx<maximalsolutions.size(); solutionIdx++) {
			Interval curSolution = maximalsolutions.get(solutionIdx);
			double score = LevenshteinDistance.computeLevenshteinDistance(strA.substring(curSolution.getStart().getIndex1(),curSolution.getEnd().getIndex1()+1), strB.substring(curSolution.getStart().getIndex2(),curSolution.getEnd().getIndex2()+1));

			IndexPair firstIndexPair = new IndexPair(curSolution.getStart().getIndex1() + workInterval.getStart().getIndex1(), curSolution.getStart().getIndex2() + workInterval.getStart().getIndex2());
			IndexPair secondIndexPair = new IndexPair(curSolution.getEnd().getIndex1() + workInterval.getStart().getIndex1(), curSolution.getEnd().getIndex2() + workInterval.getStart().getIndex2());

			Interval matchInterval = new Interval(firstIndexPair, secondIndexPair);
			MatchResult newResult = new MatchResult(matchInterval, score, threadId, solutionIdx);

			rContainer.addResult(newResult);
		}
		logger.info("Thread: "+this.threadId+". Finished writing results. Time elapsed: "+new Duration(System.currentTimeMillis()-startTime));
	}



}
