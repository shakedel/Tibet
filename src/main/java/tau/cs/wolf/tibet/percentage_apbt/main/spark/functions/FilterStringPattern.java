package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.util.regex.Pattern;

import org.apache.spark.api.java.function.Function;

class FilterStringPattern implements Function<String, Boolean> {
	private static final long serialVersionUID = 1L;
	
	private final Pattern pattern;
	
	public FilterStringPattern(Pattern filenamePattern) {
		this.pattern = filenamePattern;
	}

	@Override
	public Boolean call(String v1) throws Exception {
		if (pattern == null) {
			return true;
		}
		return this.pattern.matcher(v1).matches();
	}
}