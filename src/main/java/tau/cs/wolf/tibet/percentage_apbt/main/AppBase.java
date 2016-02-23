package tau.cs.wolf.tibet.percentage_apbt.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.data.AppResults;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsUtils;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public abstract class AppBase implements Runnable {

	public static final String CFG_PATH_VM_PROP_NAME = "percentage_apbt.cfgPath";
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final Args args;
	protected final Props props;
	
	private AppResults results;
	
	AppBase(Args args, Props _props) {
		this.args = args;
		this.props = _props == null ? PropsBuilder.defaultProps() : _props;
		
		ArgsUtils.overrideArgsWithProps(args, this.props);
	}
	
	public AppBase(Args args, boolean writeResults) {
		this(args, null);
	}

	public AppResults getResults() {
		if (this.results == null) {
			throw new IllegalStateException("Cannot get results before run() was called");
		}
		return this.results;
	}
	
	public void writeResults() {
		if (this.results == null) {
			throw new IllegalStateException("Cannot write results before run() was called");
		}
		this._writeResults(this.results);
	}
	
	protected abstract void _writeResults(AppResults results);

	@Override
	public void run() {
		this.results = calcResults();
	}
	
	abstract protected AppResults calcResults();
}
