package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.kohsuke.args4j.CmdLineException;

import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsSpark;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FilterFilenamePattern;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.SparkUtils;

public class FileCount implements Runnable {

	 private final JavaSparkContext ctx;
	private final ArgsSpark args;

	public FileCount(ArgsSpark args, JavaSparkContext ctx) {
		this.args = args;
		this.ctx = ctx;
		ctx.setLogLevel("WARN");
	}

	public static void main(String[] args) throws IOException, CmdLineException {
		ArgsSpark argsObj = new ArgsSpark(args);
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

}
