package tau.cs.wolf.tibet.percentage_apbt.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.matching.APBT;
import tau.cs.wolf.tibet.percentage_apbt.misc.BaseModule;
import tau.cs.wolf.tibet.percentage_apbt.misc.LevenshteinDistance;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class WorkerThread extends BaseModule implements Runnable {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final String firstStr;
	private final String secondStr;
	private final Interval workInterval;
	private final MatchesContainer rContainer;
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

		APBT apbt = new APBT(strA.toCharArray(), strB.toCharArray(), APBT.ProcessBy.ROW, args, props);
		apbt.run();
		Utils.reportComputationTimeByStartTime(logger, startTime, "Thread: "+this.threadId+". Finished processing");
		
		for(Interval curSolution: apbt.getMaximalSolutions()) {
			double score = LevenshteinDistance.computeLevenshteinDistance(strA.substring(curSolution.getStart().getIndex1(),curSolution.getEnd().getIndex1()+1), strB.substring(curSolution.getStart().getIndex2(),curSolution.getEnd().getIndex2()+1));
			curSolution.shiftSpans(workInterval.getStart().getIndex1(), workInterval.getStart().getIndex2());
			MatchResult newResult = new MatchResult(curSolution, score);
			rContainer.addResult(newResult);
		}
		Utils.reportComputationTimeByStartTime(logger, startTime, "Thread: "+this.threadId+". Finished writing results");
	}



}
