package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.util.regex.Pattern;

import org.apache.hadoop.fs.Path;

import scala.Tuple2;

public class FilterPairKeyFilenamePattern extends FilterPairKeyStringPattern {
	private static final long serialVersionUID = 1L;
	
	public FilterPairKeyFilenamePattern(Pattern filenamePattern) {
		super(filenamePattern);
	}

	@Override
	public Boolean call(Tuple2<String, String> v1) throws Exception {
		Tuple2<String, String> tuple = new Tuple2<String, String>(new Path(v1._1).getName(), v1._2);
		return super.call(tuple);
	}

}