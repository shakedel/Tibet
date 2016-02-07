package tau.cs.wolf.tibet.percentage_apbt.concurrent;

import java.io.PrintStream;
import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolExecotorMonitor implements Runnable  {

	private final PrintStream ps;
	private final ThreadPoolExecutor executor;
	private final long pollTime;

	public ThreadPoolExecotorMonitor(PrintStream ps, ThreadPoolExecutor executor, Duration duration) {
		this.ps = ps;
		this.executor = executor;
		this.pollTime = duration.toMillis();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(this.pollTime);
				if (executor.isTerminated()) {
					break;
				}
				ps.println(String.format("Completed %d/%d tasks", this.executor.getCompletedTaskCount(), this.executor.getTaskCount()));
			} catch(InterruptedException e) {
				throw new IllegalStateException("This thread should never be terminated");
			}

		}
	}

}