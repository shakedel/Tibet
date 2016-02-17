package tau.cs.wolf.tibet.percentage_apbt.main;

import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.concurrent.ThreadTimeMonitor;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsMonitored;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsUtils;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public class AppMonitored implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) {
		try {
			new AppMonitored(args).run();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private final ArgsMonitored args;
	private final Props props;

	public AppMonitored(ArgsMonitored args, Props props) {
		this.args = args;
		this.props = props;
		ArgsUtils.overrideArgsWithProps(args, this.props);
	}

	private AppMonitored(String[] args) throws CmdLineException {
		this(new ArgsMonitored(args), PropsBuilder.defaultProps());
	}

	@Override
	public void run() {
		BaseApp app = AppFactory.getMain(this.args.getAppType(), this.args, this.props, true);
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
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		} finally {
			monitor.interrupt();
			try {
				monitor.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
