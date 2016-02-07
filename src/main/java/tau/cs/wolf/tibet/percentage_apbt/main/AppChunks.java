package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import general.IndexPair;
import general.Interval;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.ResultsContainer;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.ThreadPoolExecotorMonitor;
import tau.cs.wolf.tibet.percentage_apbt.concurrent.WorkerThread;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class AppChunks extends BaseApp {

	public AppChunks(Args args) {
		super(args);
	}
	
	@Override
	public void run() {

		int maxThreadPoolSize = props.getNumThreads()!=null ? props.getNumThreads() : Runtime.getRuntime().availableProcessors();

		String str1 = Utils.readFile(args.getInFile1());
		String str2 = Utils.readFile(args.getInFile2());

		ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreadPoolSize, maxThreadPoolSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

		// monitor executor active tasks
		ThreadPoolExecotorMonitor monitor = new ThreadPoolExecotorMonitor(System.out, executor, Duration.ofSeconds(10));
		new Thread(monitor).start();

		try (PrintStream ps = new PrintStream(args.getOutFile())) {
			ResultsContainer resContainer = new ResultsContainer(new MatchResult.DefaultFormatter(), 0.8, ps);

			int taskCounter = 0;

			for (int i=0; i < str1.length(); i=i+props.getStepSize()) {
				for (int j=0; j < str2.length(); j=j+props.getStepSize()) {
					IndexPair firstIndexPair = new IndexPair(i, j);
					IndexPair secondIndexPair = new IndexPair(Math.min(i + props.getReadSize(), (str1.length()-1)), Math.min(j + props.getReadSize(), (str2.length()-1)));

					Interval workingInterval = new Interval(firstIndexPair, secondIndexPair);

					Runnable worker = new WorkerThread(str1, str2, workingInterval, args.getMinLength(), args.getMaxError(), resContainer, taskCounter++);
					executor.execute(worker);

				}
			}
			// do not accept additional tasks
			executor.shutdown();


			try {
				executor.awaitTermination(1, TimeUnit.HOURS);
			} catch (InterruptedException e1) {
				throw new IllegalStateException("Should never be interrupted!");
			}
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}

	}



	public static void main(String[] stringArgs) throws IOException {
		new AppChunks(new Args(stringArgs)).run();
	}

}
