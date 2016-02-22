package tau.cs.wolf.tibet.percentage_apbt.data;

import java.io.File;

public abstract class FileParser<R> {
	
	abstract public Slicable<R> parse(File f);
}
