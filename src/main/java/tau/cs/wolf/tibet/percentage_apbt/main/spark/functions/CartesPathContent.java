package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.Function;

import scala.Serializable;
import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContentPair;

public final class CartesPathContent<R> implements Function<Tuple2<PathContent<R>,PathContent<R>>, PathContentPair<R>>, Serializable  {
	private static final long serialVersionUID = 1L;

	@Override
	public PathContentPair<R> call(Tuple2<PathContent<R>, PathContent<R>> tuple) throws Exception {
		return new PathContentPair<R>(tuple._1, tuple._2);
	}
}