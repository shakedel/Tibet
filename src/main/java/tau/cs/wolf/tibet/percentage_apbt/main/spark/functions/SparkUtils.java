package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.StringPairSet;

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
	
	public static StringPairSet parseExistingFiles(File file) throws IOException {
		StringPairSet res = new StringPairSet();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line=br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				String[] sp = line.split("\\s+");
				if (sp.length != 3) {
					continue;
				}
				res.add(sp[1], sp[2]);
			}
		}
		return res;
	}
}
