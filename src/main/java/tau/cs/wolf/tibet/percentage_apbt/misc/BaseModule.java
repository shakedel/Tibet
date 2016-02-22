package tau.cs.wolf.tibet.percentage_apbt.misc;

import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsBase;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public class BaseModule {
	protected final Props props;
	protected final ArgsBase args;
	
	protected BaseModule(Props props, ArgsBase args) {
		this.props = props;
		this.args = args;
	}
}
