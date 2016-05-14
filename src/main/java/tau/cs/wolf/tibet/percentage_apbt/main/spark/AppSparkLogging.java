package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.File;
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
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsSparkCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.CartesPathContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FilterPairKeyFilenamePattern;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FilterPairUniquePath;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FindMatches;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.PathContentAdapter;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.SyslogMatchesPartition;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContentPair;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps.ClientProps;

public class AppSparkLogging<R> extends AppBase {

	private Logger logger = LoggerFactory.getLogger(AppSparkLogging.class);
	
	private final JavaSparkContext ctx;
	private final ArgsSparkCommon args;
	private final Broadcast<? extends ArgsCommon> bcastArgs;
	private final Broadcast<? extends Props> bcastProps;
	private final Broadcast<? extends ClientProps> bcastClientProps;
	
	public AppSparkLogging(ArgsSparkCommon args, JavaSparkContext ctx) throws IOException {
		super(args, null);
		
		this.args = args;
		File clientPropsFile = new File(this.props.getSyslogClientPropsPath());
		ClientProps clientProps = SyslogProps.clientProps(clientPropsFile);
		
		logger.info("setup with args: "+this.args);
		logger.info("syslogClient props: "+clientProps);
		
		this.bcastArgs = ctx.broadcast(args);
		this.bcastProps = ctx.broadcast(this.props);
		this.bcastClientProps = ctx.broadcast(clientProps);
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
			JavaRDD<PathContentPair<R>> filteredPairs = allPairs.filter(new FilterPairUniquePath<R>(null));
			JavaRDD<Matches> matches = filteredPairs.map(new FindMatches<R>(this.bcastArgs, this.bcastProps));
			matches.foreachPartition(new SyslogMatchesPartition(this.bcastClientProps));
//			matches.foreachPartitionAsync(new SyslogMatchesPartition(this.bcastClientProps));
			logger.info("Number of Matchings: "+matches.count());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException, CmdLineException {
		try (JavaSparkContext ctx = new JavaSparkContext(new SparkConf())) {
			new AppSparkLogging(new ArgsSparkCommon(args), ctx).run();
		}
	}

}
