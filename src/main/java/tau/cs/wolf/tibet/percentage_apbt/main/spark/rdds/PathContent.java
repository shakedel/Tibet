package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import scala.Serializable;
import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.data.slicable.Slicable;

public final class PathContent<R> extends Tuple2<String, Slicable<R>> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public PathContent(String path, Slicable<R> content) {
		super(path, content);
	}
	
	public String getPath() {
		return this._1;
	}
	public Slicable<R> getContent() {
		return this._2;
	}
}