package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.spark.api.java.function.Function;

public final class StringPatternFilter implements Function<String, Boolean> {
	private static final long serialVersionUID = 1L;
	
	private final Pattern pattern;
	
	public StringPatternFilter(Pattern filenamePattern) {
		this.pattern = filenamePattern;
	}

	@Override
	public Boolean call(String v1) throws Exception {
		if (pattern == null) {
			return true;
		}
		Matcher m = this.pattern.matcher(v1);
		while (m.find()) {
			if (m.hitEnd()) {
				return true;
			}
		}
		return false;
	}
}