package tau.cs.wolf.tibet.percentage_apbt.main;

import general.IndexPair;
import general.Interval;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.ResultsContainer;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.WorkerThread;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class AppAbsolute extends BaseApp {
	
	public AppAbsolute(Args args) {
		super(args);
		
	}
	
	@Override
	public void run() {
		String strA = Utils.readFile(this.args.getInFile1());
		String strB = Utils.readFile(this.args.getInFile2());
		ResultsContainer rContainer = new ResultsContainer(new MatchResult.DefaultFormatter(), -1);
		
		Interval interval = new Interval(new IndexPair(0, 0), new IndexPair(strA.length(), strB.length()));
		new WorkerThread(strA, strB, interval, this.args.getMinLength(), this.args.getMaxError(), rContainer, 0).run();
		
		rContainer.writeResults(args.getOutFile());
	}

	public static void main(String[] args) {
		new AppAbsolute(new Args(args)).run();
	}

}
