package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import org.apache.hadoop.fs.Path;

import scala.Serializable;
import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.data.Slicable;

public final class PathContent<R> extends Tuple2<Path, Slicable<R>> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public PathContent(Path f1, Slicable<R> content) {
		super(f1, content);
	}
	
	public Path getPath() {
		return this._1;
	}
	public Slicable<R> getContent() {
		return this._2;
	}
}