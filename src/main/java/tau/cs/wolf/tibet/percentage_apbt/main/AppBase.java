package tau.cs.wolf.tibet.percentage_apbt.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsUtils;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder;

public abstract class AppBase implements Runnable {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String CFG_PATH_VM_PROP_NAME = "percentage_apbt.cfgPath";
	
	protected final Props props;
	
	public AppBase(ArgsCommon args, Props _props) {
		this.props = _props == null ? PropsBuilder.defaultProps() : _props;
		ArgsUtils.overrideArgsWithProps(args, this.props);
	}
	
	public AppBase(ArgsCommon args) {
		this(args, null);
	}

}
