package tau.cs.wolf.tibet.percentage_apbt.data;

import java.io.File;

import org.apache.hadoop.fs.Path;

public abstract class FileParser<R> {
	
	abstract public Slicable<R> parse(File f);
}
