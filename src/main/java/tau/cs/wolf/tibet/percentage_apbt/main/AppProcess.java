package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Level;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsProcess;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsProcessGroup;
import tau.cs.wolf.tibet.percentage_apbt.misc.BoundedExecutor;
import tau.cs.wolf.tibet.percentage_apbt.misc.LoggerWriter;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;
import tau.cs.wolf.tibet.percentage_apbt.preprocess.PreprocessDirStructureMarshaller;
import tau.cs.wolf.tibet.percentage_apbt.preprocess.PreprocessDirStructureMarshallerImpl;
import tau.cs.wolf.tibet.percentage_apbt.process.ProcessDirStructureMarshaller;
import tau.cs.wolf.tibet.percentage_apbt.process.ProcessDirStructureMarshallerImpl;

public class AppProcess extends AppBase {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected final ArgsProcess args;
	
	private final PreprocessDirStructureMarshaller inMarshaller;
	private final ProcessDirStructureMarshaller outMarshaller;

	public AppProcess(ArgsProcess args, Props _props) {
		super(args, _props);
		this.args = args;
		this.inMarshaller = new PreprocessDirStructureMarshallerImpl(args.getInDir());
		this.outMarshaller = new ProcessDirStructureMarshallerImpl(args.getOutDir());
	}

	@Override
	public void run() {
		try {
			runWithIOException();
		} catch(IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void runWithIOException() throws IOException {
		int numThreads = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
		BoundedExecutor boundedExecutor = new BoundedExecutor(threadPool, numThreads);
		
		try {
			List<Integer> grpsList = this.inMarshaller.readGrpIds();
			int[] perm = new RandomDataGenerator().nextPermutation(grpsList.size(), grpsList.size());
			for (int i: perm) {
				int grpId = grpsList.get(i);
				
				if (this.outMarshaller.isFinishedGrp(grpId)) {
					logger.info("grp ID: "+grpId+" is already finished");
					continue;
				}
				AppProcessGroup groupProcessor = new GroupProcessAndWriteResults(new ArgsProcessGroup(this.args, grpId), this.outMarshaller);
				FileLock lock = this.outMarshaller.tryLockGrp(grpId);
				if (lock == null) {
					logger.info("grp ID: "+grpId+" is currently locked");
					continue;
				}
				try {
					
					boundedExecutor.submitTask(new LoggingExceptionRunnable(new ClosingRunnable(groupProcessor, lock), this.logger));
					logger.info("grp ID: "+grpId+" submitted");
				} catch (RejectedExecutionException | InterruptedException e) {
					throw new IOException("failed running process on grp ID: "+grpId, e);
				}
				
			}
			
		} finally {
			threadPool.shutdown();
		}
		

	}

	public static void main(String[] args) {
		try {
			AppProcess app = new AppProcess(new ArgsProcess(args), null);
			app.run();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public static class GroupProcessAndWriteResults extends AppProcessGroup {

		private static Logger logger = LoggerFactory.getLogger(GroupProcessAndWriteResults.class);
		private final ProcessDirStructureMarshaller outMarshaller;
		private final int grpId;
		
		public GroupProcessAndWriteResults(ArgsProcessGroup args, ProcessDirStructureMarshaller outMarshaller) {
			super(args);
			this.outMarshaller = outMarshaller;
			this.grpId = args.getGrpId();
		}
		
		@Override
		public void run() {
			super.run();
			try {
				this.outMarshaller.writeGroupResults(this.grpId, super.getMatchesList());
				logger.info("finished writing results for grp ID: "+this.grpId);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	public static class LoggingExceptionRunnable implements Runnable {

		private final Runnable runnable;
		private final Logger logger;
		
		public LoggingExceptionRunnable(Runnable runnable, Logger logger) {
			this.runnable = runnable;
			this.logger = logger;
		}
		
		@Override
		public void run() {
			try {
				this.runnable.run();
			} catch(Exception e) {
				logger.error("Reporting caught exception: ");
				e.printStackTrace(new LoggerWriter(this.logger, Level.ERROR));
			}
		}
		
	}
	
	public static class ClosingRunnable implements Runnable {
		private final Runnable runnable;
		private final AutoCloseable closeable;
		
		public ClosingRunnable(Runnable runnable, AutoCloseable closeable) {
			this.runnable = runnable;
			this.closeable = closeable;
		}
		
		@Override
		public void run() {
			try {
				this.runnable.run();
			} finally {
				try {
					this.closeable.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		
	}
	
	
	
}
