package tau.cs.wolf.tibet.percentage_apbt.spark_logging;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class HelloSpark {
	
	private final static int LIST_SIZE = 50;
	
	
	public HelloSpark(JavaSparkContext ctx) {
		
		List<Integer> nums = new ArrayList<Integer>(LIST_SIZE);
		for (int i=0; i<LIST_SIZE; ++i) {
			nums.add(i);
		}
		JavaRDD<Integer> numsRDD = ctx.parallelize(nums);
		numsRDD.foreach(new Print());

	}
	
	public static void main(String[] args) {
		
		SparkConf sparkConf = new SparkConf();
		try (JavaSparkContext ctx = new JavaSparkContext(sparkConf)) {
			new HelloSpark(ctx);
		}
		
	}
}
