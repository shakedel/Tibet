package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import scala.Serializable;
import scala.Tuple2;

public final class PathContentPair<R> extends Tuple2<PathContent<R>, PathContent<R>> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public PathContentPair(PathContent<R> p1, PathContent<R> p2) {
		super(p1, p2);
	}
	
	public PathContent<R> getPathContent1() {
		return this._1;
	}
	public PathContent<R> getPathContent2() {
		return this._2;
	}
}