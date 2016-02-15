package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;

public class HelloSpark {
	public static void main(String[] args) {

		// Local mode
		SparkConf sparkConf = new SparkConf().setAppName("HelloWorld").setMaster("local");
		try (JavaSparkContext ctx = new JavaSparkContext(sparkConf)) {
			
			String[] arr = new String[]{"John", "Paul", "Gavin", "Rahul", "Angel"};
			List<String> inputList = Arrays.asList(arr);
			JavaRDD<String> inputRDD = ctx.parallelize(inputList);
			inputRDD.foreach(new VoidFunction<String>() {
				
				private static final long serialVersionUID = -8083815007233153332L;

				public void call(String input) throws Exception {
					System.out.println(input);
					
				}
			});
		}

	}
}
