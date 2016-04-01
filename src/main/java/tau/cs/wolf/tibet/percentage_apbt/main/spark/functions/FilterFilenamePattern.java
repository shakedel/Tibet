package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.util.regex.Pattern;

import org.apache.hadoop.fs.Path;

public final class FilterFilenamePattern extends FilterStringPattern {
	private static final long serialVersionUID = 1L;
	
	public FilterFilenamePattern(Pattern filenamePattern) {
		super(filenamePattern);
	}

	@Override
	public Boolean call(String v1) throws Exception {
		return super.call(new Path(v1).getName());
	}
}