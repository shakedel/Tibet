package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SparkUtils {

	public static final class DirFilter implements PathFilter {
		@Override
		public boolean accept(Path path) {
			try {
				return !path.getFileSystem(new Configuration()).isDirectory(path);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private SparkUtils() {
		// do nothing
	}

	public static JavaPairRDD<String, String> getTextFilesRecursive(JavaSparkContext ctx, Path path, int maxDepth) {
		if (maxDepth < 1) {
			throw new IllegalArgumentException("must be with depth at least 1");
		}
		try {
			final Job job = Job.getInstance();
			FileInputFormat.setInputPaths(job, path);
			job.getConfiguration().setBoolean(FileInputFormat.INPUT_DIR_RECURSIVE, true);
			FileInputFormat.setInputPathFilter(job, DirFilter.class);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		String pathStr = path.toString() + "/*";
		JavaPairRDD<String, String> files = ctx.wholeTextFiles(pathStr);
		for (int i = 2; i <= maxDepth; i++) {
			pathStr += "/*";
			files.union(ctx.wholeTextFiles(pathStr));
		}
		return files;
	}

	public static long countSafe(JavaRDD<?> rdd) {
		try {
			return rdd.count();
		} catch (Exception e) {
			if (e.getMessage().startsWith("Input Pattern ") && e.getMessage().endsWith(" matches 0 files")) {
				return 0;
			}
			throw e;
		}
	}

	public static long countSafe(JavaPairRDD<?, ?> rdd) {
		return countSafe(rdd.keys());
	}
}
