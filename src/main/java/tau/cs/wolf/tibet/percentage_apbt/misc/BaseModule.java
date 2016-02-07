package tau.cs.wolf.tibet.percentage_apbt.misc;

import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public class BaseModule {
	protected final Props props;
	protected final Args args;
	
	protected BaseModule(Props props, Args args) {
		this.props = props;
		this.args = args;
	}
}
