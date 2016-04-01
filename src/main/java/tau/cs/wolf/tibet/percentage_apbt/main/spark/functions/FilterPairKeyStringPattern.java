package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.util.regex.Pattern;

import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

class FilterPairKeyStringPattern implements Function<Tuple2<String, String>, Boolean> {
	private static final long serialVersionUID = 1L;
	
	private final Pattern pattern;
	
	public FilterPairKeyStringPattern(Pattern filenamePattern) {
		this.pattern = filenamePattern;
	}

	@Override
	public Boolean call(Tuple2<String, String> v1) throws Exception {
		if (pattern == null) {
			return true;
		}
		return this.pattern.matcher(v1._1).matches();
	}

}