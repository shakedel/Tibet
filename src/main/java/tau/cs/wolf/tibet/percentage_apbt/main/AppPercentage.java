package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import general.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.MatchesContainer;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.WorkerThread;
import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.matching.Alignment;
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
		
		String strA = Utils.readFile(this.args.getInFile1());
		String strB = Utils.readFile(this.args.getInFile2());
		MatchesContainer matchesContainer = new MatchesContainer(new MatchResult.DefaultFormatter(), -1);
		
		Interval interval = new Interval(new IndexPair(0, 0), new IndexPair(strA.length(), strB.length()));
		new WorkerThread(props, args, strA, strB, interval, matchesContainer, 0).run();
		
		matchesContainer.shutdown();
		List<MatchResult> apbtMatches = matchesContainer.getResults();
		List<MatchResult> unitedMatches = new Union(props, args).uniteMatches(apbtMatches);
		List<MatchResult> alignedMatches = new Alignment(props, args).alignMatches(unitedMatches, strA, strB);

		if (writeResults) {
			Utils.writeMatches(apbtOutFile, apbtMatches, null);
			Utils.writeMatches(unionFile, unitedMatches, null);
			Utils.writeMatches(args.getOutFile(), alignedMatches, null);
		}
		
		this.results = alignedMatches;
	}

	public static void main(String[] args) {
		new AppPercentage(new Args(args), null, true).run();
	}

	@Override
	protected List<MatchResult> _getResults() {
		return this.results;
	}
	
		

	
	

}
