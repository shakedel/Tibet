package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.VoidFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;

public class LogResults implements VoidFunction<Matches> {
	private static final long serialVersionUID = 1L;

	@Override
	public void call(Matches matches) throws Exception {
		Logger logger = LoggerFactory.getLogger("matches");
		logger.info(matches.toString());
	}

}
