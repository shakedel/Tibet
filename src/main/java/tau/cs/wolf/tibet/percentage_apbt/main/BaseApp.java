package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
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
	
	BaseApp(Args args, Props _props, boolean writeResults) {
		this.args = args;
		if (_props != null) {
			this.props = _props;
		} else if (System.getProperty(CFG_PATH_VM_PROP_NAME) != null) {
			File propsFile = new File(System.getProperty(CFG_PATH_VM_PROP_NAME));
			if (!propsFile.isFile()) {
				throw new IllegalArgumentException("vm argument '"+CFG_PATH_VM_PROP_NAME+"' does not point to an existing file!");
			}
			try {
				this.props = PropsBuilder.newProps(propsFile);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			this.props = PropsBuilder.defaultProps();
		}
		
		this.writeResults = writeResults;
		
		ArgsUtils.overrideArgsWithProps(args, this.props);
		
	}
	public BaseApp(Args args, boolean writeResults) {
		this(args, PropsBuilder.defaultProps(), writeResults);
	}

	public List<MatchResult> getResults() {
		List<MatchResult> results = _getResults();
		if (results == null) {
			throw new IllegalStateException("Cannot get results before run() was called");
		}
		return results;
	}

	
	abstract protected List<MatchResult> _getResults();
}
