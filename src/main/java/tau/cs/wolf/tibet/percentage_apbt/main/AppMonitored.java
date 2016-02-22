package tau.cs.wolf.tibet.percentage_apbt.main;

import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.concurrent.ThreadTimeMonitor;
import tau.cs.wolf.tibet.percentage_apbt.data.AppResults;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsUtils;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public class AppMonitored extends AppBase {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) {
		try {
			new AppMonitored(args).run();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private final Props props;
	
	public AppMonitored(Args args, Props props, boolean writeResults) {
		super(args, props, writeResults);
		this.props = props;
		ArgsUtils.overrideArgsWithProps(args, this.props);
	}

	private AppMonitored(String[] args) throws CmdLineException {
		this(new Args(args), null, true);
	}

	@Override
	protected AppResults calcResults() {
		AppBase app = new AppMain(this.args, this.props, true);
		Thread appThread = new Thread(app, "App");
		final ThreadTimeMonitor monitor = new ThreadTimeMonitor(logger, appThread, args.getPollDuration(), appThread.getName());
		Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
		    public void uncaughtException(Thread th, Throwable ex) {
		    	monitor.interrupt();
		    	ex.printStackTrace();
		    }
		};
		appThread.setUncaughtExceptionHandler(handler);
		
		monitor.start();
		appThread.start();
		try {
			appThread.join();
			return app.getResults();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		} finally {
			monitor.interrupt();
			try {
				monitor.join();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

}
