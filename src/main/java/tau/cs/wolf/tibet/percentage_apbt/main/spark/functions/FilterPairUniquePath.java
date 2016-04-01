package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContentPair;

public final class FilterPairUniquePath<R> implements Function<PathContentPair<R>, Boolean>, Serializable {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public Boolean call(PathContentPair<R> pathContentPair) throws Exception {
		String p1 = pathContentPair.getPathContent1().getPath();
		String p2 = pathContentPair.getPathContent2().getPath();
		return p1.compareTo(p2) < 0;
	}
}