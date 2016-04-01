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

	private SparkUtils() {
		// do nothing
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
