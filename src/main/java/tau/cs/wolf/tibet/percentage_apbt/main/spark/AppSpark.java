package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.main.AppBase;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsSpark;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.CartesPathContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FilterPairKeyFilenamePattern;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FilterPairUniquePath;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FindMatches;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.PathContentAdapter;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContentPair;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public class AppSpark<R> extends AppBase {

	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(AppSpark.class);
	
	private final JavaSparkContext ctx;
	private final ArgsSpark args;
	private final Broadcast<? extends ArgsCommon> bcastArgs;
	private final Broadcast<? extends Props> bcastProps;
	
	public AppSpark(ArgsSpark args, Props props, JavaSparkContext ctx) throws IOException {
		super(args, props);
		this.args = args;
		this.bcastArgs = ctx.broadcast(args);
		this.bcastProps = ctx.broadcast(props);
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
			JavaRDD<Matches> matches = filteredPairs.map(new FindMatches<R>(this.bcastArgs, this.bcastProps));
			System.out.println("$$$ "+matches.isEmpty()+" $$$");;
			System.out.println("### "+matches.count()+" ###");
//			JavaRDD<Matches> coalesced = matches.coalesce(1);
//			coalesced.saveAsTextFile(this.args.getOutFile().toString());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException, CmdLineException {
		try (JavaSparkContext ctx = new JavaSparkContext(new SparkConf())) {
			new AppSpark(new ArgsSpark(args), null, ctx).run();
		}
	}

}
