package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

final class IndexPairFilter implements Function<Tuple2<Integer, Integer>, Boolean> {
	private static final long serialVersionUID = 1L;

	@Override
	public Boolean call(Tuple2<Integer, Integer> v1) throws Exception {
		return v1._1 < v1._2;
	}
}