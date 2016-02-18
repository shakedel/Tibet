package tau.cs.wolf.tibet.percentage_apbt.main;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.data.AppResults;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsUtils;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public abstract class BaseApp implements Runnable {

	public static final String CFG_PATH_VM_PROP_NAME = "percentage_apbt.cfgPath";
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final Args args;
	protected final Props props;
	protected final boolean writeResults;
	
	private AppResults results;
	
	BaseApp(Args args, Props _props, boolean writeResults) {
		this.args = args;
		this.props = _props == null ? PropsBuilder.defaultProps() : _props;
		
		this.writeResults = writeResults;
		
		ArgsUtils.overrideArgsWithProps(args, this.props);
	}
	
	public BaseApp(Args args, boolean writeResults) {
		this(args, null, writeResults);
	}

	public AppResults getResults() {
		if (this.results == null) {
			throw new IllegalStateException("Cannot get results before run() was called");
		}
		return this.results;
	}

	@Override
	public void run() {
		this.results = calcResults();
	}
	
	abstract protected AppResults calcResults();
}
