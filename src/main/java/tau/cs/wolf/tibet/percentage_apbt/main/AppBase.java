package tau.cs.wolf.tibet.percentage_apbt.main;

import org.joda.time.Duration;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder;

public abstract class AppBase implements Runnable {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String CFG_PATH_VM_PROP_NAME = "percentage_apbt.cfgPath";
	
	protected final Props props;
	
	public AppBase(ArgsCommon args, Props _props) {
		this.props = _props == null ? PropsBuilder.defaultProps() : _props;
		try {
			this.overrideArgsWithProps(args, this.props);
		} catch (CmdLineException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public AppBase(ArgsCommon args) {
		this(args, null);
	}

	protected void overrideArgsWithProps(ArgsCommon args, Props props) throws CmdLineException {
		if (args.getMinLength() == null) {
			args.setMinlength(props.getMinLength());
		}
		if (args.getMaxError() == null) {
			args.setMaxError(props.getMaxError());
		}
		if (args.getTimeout() == null) {
			Duration t = props.getTimeout();
			args.setTimeout(t);
		}
		if (args.getMinDistanceUnion() == null) {
			args.setMinDistanceUnion(props.getMinDistanceUnion());
		}
		if (args.getLocalAlignPadRatio() == null) {
			args.setLocalAlignPadRatio(props.getLocalAlignPadRatio());
		}
	}

}
