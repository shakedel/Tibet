package tau.cs.wolf.tibet.percentage_apbt.main;

import java.util.List;

import org.kohsuke.args4j.CmdLineException;

import tau.cs.wolf.tibet.percentage_apbt.concurrent.MatchesContainer;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.WorkerThread;
import tau.cs.wolf.tibet.percentage_apbt.data.CharArr;
import tau.cs.wolf.tibet.percentage_apbt.data.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class AppAbsolute extends BaseApp {
	
	private List<MatchResult> results = null;
	
	AppAbsolute(Args args, Props props, boolean writeResults) {
		super(args, props, writeResults);
		
	}
	
	@Override
	public void run() {
		String seq1Str = Utils.readFile(this.args.getInFile1());
		String seq2Str = Utils.readFile(this.args.getInFile2());
		
		CharArr seq1 = new CharArr(seq1Str.toCharArray());
		CharArr seq2 = new CharArr(seq2Str.toCharArray());
		
		MatchesContainer matchesContainer = new MatchesContainer(new MatchResult.DefaultFormatter(), -1);
		
		Interval interval = Interval.newIntervalByStartEnd(new IndexPair(0, 0), new IndexPair(seq1.length(), seq2.length()));
		new WorkerThread<CharArr>(props, args, seq1, seq2, interval, matchesContainer, 0).run();
		
		matchesContainer.shutdown();
		List<MatchResult> res = matchesContainer.getResults();
		if (writeResults) {
			Utils.writeMatches(args.getOutFile(), res, null);
		}
		this.results = res;
	}

	public static void main(String[] args) {
		try {
			new AppAbsolute(new Args(args), null, true).run();
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
