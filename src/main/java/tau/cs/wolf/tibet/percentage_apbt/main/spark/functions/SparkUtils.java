package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

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
