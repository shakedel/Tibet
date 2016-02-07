package tau.cs.wolf.tibet.percentage_apbt.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public abstract class BaseApp implements Runnable {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final Args args;
	protected final Props props;

	public BaseApp(Args args, Props props) {
		this.args = args;
		this.props = props;
		
	}
	public BaseApp(Args args) {
		this(args, PropsBuilder.defaultProps());
	}

}
