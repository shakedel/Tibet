package tau.cs.wolf.tibet.percentage_apbt.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.data.AppResults;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public abstract class AppCommon extends AppBase {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private AppResults results;
	
	AppCommon(Args args, Props _props) {
		super(args, _props);
	}
	
	public AppCommon(Args args) {
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
