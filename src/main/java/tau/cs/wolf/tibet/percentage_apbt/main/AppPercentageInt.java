package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.kohsuke.args4j.CmdLineException;

import tau.cs.wolf.tibet.percentage_apbt.concurrent.MatchesContainer;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.WorkerThread;
import tau.cs.wolf.tibet.percentage_apbt.data.AppResults;
import tau.cs.wolf.tibet.percentage_apbt.data.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.IntArr;
import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.matching.Alignment;
import tau.cs.wolf.tibet.percentage_apbt.matching.Union;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class AppPercentageInt extends BaseApp {
	
	AppPercentageInt(Args args, Props props, boolean writeResults) {
		super(args, props, writeResults);
		
	}
	
	private int[] seq1Arr = null;
	private int[] seq2Arr = null;
	
	public void setup(int[] seq1, int[] seq2) {
		this.seq1Arr = seq1;
		this.seq2Arr = seq2;
	}
	
	@Override
	public AppResults calcResults() {
		if (this.seq1Arr == null) {
			seq1Arr = Utils.readIntegerFile(this.args.getInFile1(), Pattern.compile("\\s+"));
		}
		if (this.seq2Arr == null) {
			seq2Arr = Utils.readIntegerFile(this.args.getInFile2(), Pattern.compile("\\s+"));
		}
		
		IntArr seq1 = new IntArr(seq1Arr);
		IntArr seq2 = new IntArr(seq2Arr);
		
		Integer[] seq1Obj = ArrayUtils.toObject(seq1Arr);
		Integer[] seq2Obj = ArrayUtils.toObject(seq2Arr);
				
		MatchesContainer matchesContainer = new MatchesContainer(new MatchResult.DefaultFormatter(), -1);
		
		Interval interval = Interval.newIntervalByStartEnd(new IndexPair(0, 0), new IndexPair(seq1.length(), seq2.length()));
		long startTime = System.currentTimeMillis();
		new WorkerThread<IntArr>(props, args, seq1, seq2, interval, matchesContainer, 0).run();
		Utils.reportComputationTimeByStartTime(logger, startTime, "Finished APBT");
		matchesContainer.shutdown();
		List<MatchResult> apbtMatches = matchesContainer.getResults();
		List<MatchResult> unitedMatches = new Union(props, args).uniteMatches(apbtMatches);
		Utils.reportComputationTimeByStartTime(logger, startTime, "Finished Union");
		List<MatchResult> alignedMatches = new Alignment<Integer>(props, args).alignMatches(unitedMatches, seq1Obj, seq2Obj);
		Utils.reportComputationTimeByStartTime(logger, startTime, "Finished Alignment");

		if (writeResults) {
			String outFileBase = FilenameUtils.removeExtension(args.getOutFile().getPath());
			File apbtOutFile = new File(outFileBase + ".apbt.txt");
			File unionFile = new File (outFileBase + ".union.txt");
			
			Utils.writeMatches(apbtOutFile, apbtMatches, null);
			Utils.writeMatches(unionFile, unitedMatches, null);
			Utils.writeMatches(args.getOutFile(), alignedMatches, null);
			Utils.reportComputationTimeByStartTime(logger, startTime, "Finished writing results");
		}
		
		return new AppResults(apbtMatches, unitedMatches, alignedMatches);
	}

	public static void main(String[] args) {
		try {
			new AppPercentageInt(new Args(args), null, true).run();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}


}
