package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsSpark;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.CartesPathContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FilterPairKeyFilenamePattern;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FilterPairUniquePath;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FindMatches;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.PathContentAdapter;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContentPair;

public class AppSpark<R> implements Runnable {

	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(AppSpark.class);
	
	private final JavaSparkContext ctx;
	private final ArgsSpark args;
	
	public AppSpark(ArgsSpark args, JavaSparkContext ctx) throws IOException {
		this.args = args;
		this.ctx = ctx;
	}

	public void run() {

		try {
			FileInputFormat.setInputDirRecursive(Job.getInstance(), true);
			JavaPairRDD<String, String> fileAndText = ctx.wholeTextFiles(this.args.getInDir().toString());
			JavaPairRDD<String, String> filteredFiles = fileAndText.filter(new FilterPairKeyFilenamePattern(this.args.getFilenamePattern()));
			JavaRDD<PathContent<R>> pathContent = filteredFiles.map(new PathContentAdapter<R>(this.args.getDataType()));
			JavaPairRDD<PathContent<R>, PathContent<R>> crossedPaths = pathContent.cartesian(pathContent);
			JavaRDD<PathContentPair<R>> allPairs = crossedPaths.map(new CartesPathContent<R>());
			JavaRDD<PathContentPair<R>> filteredPairs = allPairs.filter(new FilterPairUniquePath<R>());
			JavaRDD<Matches> matches = filteredPairs.map(new FindMatches<R>(this.args));
			JavaRDD<Matches> coalesced = matches.coalesce(1);
			coalesced.saveAsTextFile(this.args.getOutFile().toString());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException, CmdLineException {
		// Local mode
		SparkConf sparkConf = new SparkConf().setAppName("AppSpark").setMaster("local");
		try (JavaSparkContext ctx = new JavaSparkContext(sparkConf)) {
			new AppSpark(new ArgsSpark(args), ctx).run();
		}
	}

}
