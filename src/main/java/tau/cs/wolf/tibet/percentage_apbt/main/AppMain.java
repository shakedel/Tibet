package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.kohsuke.args4j.CmdLineException;

import tau.cs.wolf.tibet.percentage_apbt.concurrent.MatchesContainer;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.WorkerThread;
import tau.cs.wolf.tibet.percentage_apbt.data.AppResults;
import tau.cs.wolf.tibet.percentage_apbt.data.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.data.Slicable;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.matching.Alignment;
import tau.cs.wolf.tibet.percentage_apbt.matching.Union;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class AppMain extends AppBase {

	private final String outFileBase;
	
	public AppMain(Args args, Props props, boolean writeResults) {
		super(args, props, writeResults);
		this.outFileBase = FilenameUtils.removeExtension(args.getOutFile().getPath());
	}
	
	private Slicable<?> seq1 = null;
	private Slicable<?> seq2 = null;
	private long startTime = System.currentTimeMillis(); 
	
	public void setup(Slicable<?> seq1, Slicable<?> seq2) {
		this.seq1 = seq1;
		this.seq2 = seq2;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public AppResults calcResults() {
		if (this.seq1 == null) {
			seq1 = (Slicable<?>) AppUtils.getFileParser(args.getDataType()).parse(args.getInFile1());
		}
		if (this.seq2 == null) {
			seq2 = (Slicable<?>) AppUtils.getFileParser(args.getDataType()).parse(args.getInFile2());
		}
		
		startTime = System.currentTimeMillis();
		
		MatchesContainer matchesContainer = new MatchesContainer(new MatchResult.DefaultFormatter(), -1);
		Interval interval = Interval.newIntervalByStartEnd(new IndexPair(0, 0), new IndexPair(seq1.length(), seq2.length()));
		new WorkerThread(props, args, seq1, seq2, interval, matchesContainer, 0).run();
		matchesContainer.shutdown();
		
		switch (args.getAppStage()) {
			case APBT: {
				return doApbt(matchesContainer, args.getOutFile());
			}
			case UNION: {
				return doUnion(matchesContainer, args.getOutFile());
			}
			case ALIGNMENT: {
				return doAlignment(matchesContainer, args.getOutFile());
			}
			default: {
				throw new IllegalArgumentException("Unknown value: "+args.getAppStage());
			}
				
		}
	}

	private AppResults doApbt(MatchesContainer matchesContainer, File outFile) {
		List<MatchResult> apbtMatches = matchesContainer.getResults();
		if (writeResults) {
			Utils.writeMatches(outFile, apbtMatches, null);
		}
		Utils.reportComputationTimeByStartTime(logger, startTime, "Finished APBT stage");
		return new AppResults(apbtMatches, null, null);
	}
	
	private AppResults doUnion(MatchesContainer matchesContainer, File outFile) {
		AppResults res = doApbt(matchesContainer, new File(outFileBase + ".apbt.txt"));
		List<MatchResult> unitedMatches = new Union(props, args).uniteMatches(res.getApbtMatches());
		if (writeResults) {
			Utils.writeMatches(outFile, unitedMatches, null);
		}
		Utils.reportComputationTimeByStartTime(logger, startTime, "Finished UNION stage");
		res.setUnitedMatches(unitedMatches);
		return res;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private AppResults doAlignment(MatchesContainer matchesContainer, File outFile) {
		AppResults res = doUnion(matchesContainer, new File(outFileBase + ".union.txt"));
		List<MatchResult> alignedMatches = new Alignment(props, args).alignMatches(res.getUnitedMatches(), seq1, seq2);
		if (writeResults) {
			Utils.writeMatches(outFile, alignedMatches, null);
		}
		Utils.reportComputationTimeByStartTime(logger, startTime, "Finished ALIGNMENT stage");
		res.setAlignedMatches(alignedMatches);
		return res;
	}
	
	public static void main(String[] args) {
		try {
			new AppMain(new Args(args), null, true).run();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}


}
