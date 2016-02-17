package tau.cs.wolf.tibet.percentage_apbt.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.data.Slicable;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.matching.Apbt;
import tau.cs.wolf.tibet.percentage_apbt.matching.ApbtFactory;
import tau.cs.wolf.tibet.percentage_apbt.matching.ProcessType;
import tau.cs.wolf.tibet.percentage_apbt.misc.BaseModule;
import tau.cs.wolf.tibet.percentage_apbt.misc.LevenshteinDistance;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class WorkerThread<R extends Slicable<R>> extends BaseModule implements Runnable {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final R seq1;
	private final R seq2;
	private final Interval workInterval;
	private final MatchesContainer rContainer;
	private final int threadId;

	public WorkerThread(Props props, Args args, R seq1, R seq2, Interval workInterval, MatchesContainer rContainer, int threadId) {
		super(props, args);
		this.workInterval = workInterval;
		this.seq1 = seq1;
		this.seq2 = seq2;
		this.rContainer = rContainer;
		this.threadId = threadId;
	}


	@SuppressWarnings("unchecked")
	private Apbt<R> getApbt() {
		@SuppressWarnings("rawtypes")
		Class<? extends Slicable> rClass = this.seq1.getClass();
		return ApbtFactory.newApbt(rClass);
	}
	
	public void run() {
		long startTime = System.currentTimeMillis();
		logger.info("Thread: "+this.threadId+". Started with work interval: "+this.workInterval);

		Apbt<? super R> apbt =this.getApbt();
		ProcessType type = seq1.length()<seq2.length() ? ProcessType.ROW : ProcessType.COL;
		apbt.setup(seq1, seq2, type, args, props);
		apbt.run();
		Utils.reportComputationTimeByStartTime(logger, startTime, "Thread: "+this.threadId+". Finished processing");
		
		for(Interval curSolution: apbt.getMaximalSolutions()) {
			R seq1Slice = seq1.slice(curSolution.getStart().getIndex1(),curSolution.getEnd().getIndex1()+1);
			R seq2Slice = seq2.slice(curSolution.getStart().getIndex2(),curSolution.getEnd().getIndex2()+1);
			double score = LevenshteinDistance.computeLevenshteinDistance(seq1Slice, seq2Slice);
			curSolution.shiftSpans(workInterval.getStart().getIndex1(), workInterval.getStart().getIndex2());
			MatchResult newResult = new MatchResult(curSolution, score);
			rContainer.addResult(newResult);
		}
		Utils.reportComputationTimeByStartTime(logger, startTime, "Thread: "+this.threadId+". Finished writing results");
	}



}
