package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.joda.time.Duration;
import org.kohsuke.args4j.CmdLineException;

import tau.cs.wolf.tibet.percentage_apbt.concurrent.MatchesContainer;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.ThreadPoolExecotorMonitor;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.WorkerThread;
import tau.cs.wolf.tibet.percentage_apbt.data.AppResults;
import tau.cs.wolf.tibet.percentage_apbt.data.CharArr;
import tau.cs.wolf.tibet.percentage_apbt.data.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class AppChunks extends BaseApp {
	
	AppChunks(Args args, Props props, boolean writeResults) {
		super(args, props, writeResults);
	}
	
	@Override
	public AppResults calcResults() {

		int maxThreadPoolSize = props.getNumThreads()!=null ? props.getNumThreads() : Runtime.getRuntime().availableProcessors();

		String seq1Str = Utils.readFile(this.args.getInFile1());
		String seq2Str = Utils.readFile(this.args.getInFile2());
		
		CharArr seq1 = new CharArr(seq1Str.toCharArray());
		CharArr seq2 = new CharArr(seq2Str.toCharArray());
		
		ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreadPoolSize, maxThreadPoolSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

		// monitor executor active tasks
		ThreadPoolExecotorMonitor monitor = new ThreadPoolExecotorMonitor(null, executor, Duration.standardSeconds(10));
		new Thread(monitor).start();

		try (PrintStream ps = this.writeResults ? new PrintStream(args.getOutFile()) : null) {
			MatchesContainer matchesContainer = new MatchesContainer(new MatchResult.DefaultFormatter(), 0.8);

			int taskCounter = 0;

			for (int i=0; i < seq1.length(); i=i+props.getStepSize()) {
				for (int j=0; j < seq2.length(); j=j+props.getStepSize()) {
					IndexPair firstIndexPair = new IndexPair(i, j);
					IndexPair secondIndexPair = new IndexPair(Math.min(i + props.getReadSize(), (seq1.length()-1)), Math.min(j + props.getReadSize(), (seq2.length()-1)));

					Interval workingInterval = Interval.newIntervalByStartEnd(firstIndexPair, secondIndexPair);

					Runnable worker = new WorkerThread<CharArr>(props, args, seq1, seq2, workingInterval, matchesContainer, taskCounter++);
					executor.execute(worker);

				}
			}
			// do not accept additional tasks
			executor.shutdown();


			try {
				Duration timeout = args.getTimeout();
				boolean timedOut = !executor.awaitTermination(timeout.getMillis(), TimeUnit.MILLISECONDS);
				if (timedOut) {
					throw new IllegalStateException("");
				}
			} catch (InterruptedException e1) {
				throw new IllegalStateException("Should never be interrupted!");
			}
			
			matchesContainer.shutdown();
			List<MatchResult> res = matchesContainer.getResults();
			if (writeResults) {
				Utils.writeMatches(args.getOutFile(), res, null);
			}
			return new AppResults(res);
			
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}

	}



	public static void main(String[] stringArgs) throws IOException {
		try {
			new AppChunks(new Args(stringArgs), null, true).run();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

}
