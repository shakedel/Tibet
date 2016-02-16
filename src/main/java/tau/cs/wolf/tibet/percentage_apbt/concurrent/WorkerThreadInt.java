package tau.cs.wolf.tibet.percentage_apbt.concurrent;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.matching.Apbt;
import tau.cs.wolf.tibet.percentage_apbt.matching.ApbtInt;
import tau.cs.wolf.tibet.percentage_apbt.matching.ProcessType;
import tau.cs.wolf.tibet.percentage_apbt.misc.BaseModule;
import tau.cs.wolf.tibet.percentage_apbt.misc.LevenshteinDistanceInt;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class WorkerThreadInt extends BaseModule implements Runnable {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final int[] seq1;
	private final int[] seq2;
	private final Interval workInterval;
	private final MatchesContainer rContainer;
	private final int threadId;

	public WorkerThreadInt(Props props, Args args, int[] seq1, int[] seq2, Interval workInterval, MatchesContainer rContainer, int threadId) {
		super(props, args);
		this.workInterval = workInterval;
		this.seq1 = seq1;
		this.seq2 = seq2;
		this.rContainer = rContainer;
		this.threadId = threadId;
	}


	public void run() {
		long startTime = System.currentTimeMillis();
		logger.info("Thread: "+this.threadId+". Started with work interval: "+this.workInterval);

		Apbt<int[]> apbt = new ApbtInt();
		apbt.setup(this.seq1, this.seq2, ProcessType.ROW, args, props);
		apbt.run();
		Utils.reportComputationTimeByStartTime(logger, startTime, "Thread: "+this.threadId+". Finished processing");
		
		for(Interval curSolution: apbt.getMaximalSolutions()) {
			double score = LevenshteinDistanceInt.computeLevenshteinDistance(Arrays.copyOfRange(seq1, curSolution.getStart().getIndex1(), curSolution.getEnd().getIndex1()+1), Arrays.copyOfRange(seq2, curSolution.getStart().getIndex2(), curSolution.getEnd().getIndex2()+1));
			curSolution.shiftSpans(workInterval.getStart().getIndex1(), workInterval.getStart().getIndex2());
			MatchResult newResult = new MatchResult(curSolution, score);
			rContainer.addResult(newResult);
		}
		Utils.reportComputationTimeByStartTime(logger, startTime, "Thread: "+this.threadId+". Finished writing results");
	}



}
