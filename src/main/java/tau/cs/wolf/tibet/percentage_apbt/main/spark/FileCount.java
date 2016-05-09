package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.kohsuke.args4j.CmdLineException;

import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsSparkCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.SparkUtils;

public class FileCount implements Runnable {

	 private final JavaSparkContext ctx;
	private final ArgsSparkCommon args;

	public FileCount(ArgsSparkCommon args, JavaSparkContext ctx) {
		this.args = args;
		this.ctx = ctx;
		ctx.setLogLevel("WARN");
	}

	public static void main(String[] args) throws IOException, CmdLineException {
		ArgsSparkCommon argsObj = new ArgsSparkCommon(args);
		 SparkConf sparkConf = new SparkConf();
		 try (JavaSparkContext ctx = new JavaSparkContext(sparkConf)) {
			 new FileCount(argsObj, ctx).run();
		 }
	}

	@Override
	public void run() {
		 JavaPairRDD<String,String> filesAndText = this.ctx.wholeTextFiles(this.args.getInDir().toString());
		 JavaRDD<String> files = filesAndText.keys();
		 JavaRDD<String> filteredFiles = files.filter(new FilterFilenamePattern(this.args.getFilenamePattern()));
		 System.out.println("XXX: Number Of Files: "+SparkUtils.countSafe(filteredFiles));
	}

	private static class FilterStringPattern implements Function<String, Boolean> {
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
	
	
	private static final class FilterFilenamePattern extends FilterStringPattern {
		private static final long serialVersionUID = 1L;
		
		public FilterFilenamePattern(Pattern filenamePattern) {
			super(filenamePattern);
		}

		@Override
		public Boolean call(String v1) throws Exception {
			return super.call(new Path(v1).getName());
		}
	}
	
}
