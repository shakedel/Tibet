package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.VoidFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;

public class LogMatches implements VoidFunction<Matches> {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger("MATCHES");
	
	@Override
	public void call(Matches matches) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("START\n").append(matches.toString()).append("\nEND");
		logger.error(sb.toString());
	}

}
