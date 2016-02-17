package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.kohsuke.args4j.CmdLineException;

import tau.cs.wolf.tibet.percentage_apbt.concurrent.MatchesContainer;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.WorkerThread;
import tau.cs.wolf.tibet.percentage_apbt.data.CharArr;
import tau.cs.wolf.tibet.percentage_apbt.data.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.matching.AlignmentGeneric;
import tau.cs.wolf.tibet.percentage_apbt.matching.Union;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class AppPercentage extends BaseApp {
	
	private List<MatchResult> results = null;
	
	AppPercentage(Args args, Props props, boolean writeResults) {
		super(args, props, writeResults);
		
	}
	
	@Override
	public void run() {
		String outFileBase = FilenameUtils.removeExtension(args.getOutFile().getPath());
		File apbtOutFile = new File(outFileBase + ".apbt.txt");
		File unionFile = new File (outFileBase + ".union.txt");
		
		String seq1Str = Utils.readFile(this.args.getInFile1());
		String seq2Str = Utils.readFile(this.args.getInFile2());
		
		CharArr seq1 = new CharArr(seq1Str.toCharArray());
		CharArr seq2 = new CharArr(seq2Str.toCharArray());
		
		MatchesContainer matchesContainer = new MatchesContainer(new MatchResult.DefaultFormatter(), -1);
		
		Interval interval = Interval.newIntervalByStartEnd(new IndexPair(0, 0), new IndexPair(seq1.length(), seq2.length()));
		new WorkerThread<CharArr>(props, args, seq1, seq2, interval, matchesContainer, 0).run();
		
		matchesContainer.shutdown();
		List<MatchResult> apbtMatches = matchesContainer.getResults();
		List<MatchResult> unitedMatches = new Union(props, args).uniteMatches(apbtMatches);
		List<MatchResult> alignedMatches = new AlignmentGeneric<CharArr>(props, args).alignMatches(unitedMatches, seq1, seq2);

		if (writeResults) {
			Utils.writeMatches(apbtOutFile, apbtMatches, null);
			Utils.writeMatches(unionFile, unitedMatches, null);
			Utils.writeMatches(args.getOutFile(), alignedMatches, null);
		}
		
		this.results = alignedMatches;
	}

	public static void main(String[] args) {
		try {
			new AppPercentage(new Args(args), null, true).run();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	@Override
	protected List<MatchResult> _getResults() {
		return this.results;
	}
	
		

	
	

}
