package tau.cs.wolf.tibet.percentage_apbt.data;

import org.apache.hadoop.fs.Path;

public abstract class PathParser<R> {
	
	abstract public Slicable<R> parse(Path path);
}
