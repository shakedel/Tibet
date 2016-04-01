package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.IOException;
import java.util.Map;

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
import tau.cs.wolf.tibet.percentage_apbt.main.spark.UniqueSyllables.EntryToPair;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.UniqueSyllables.FlatMap;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.UniqueSyllables.ReduceAdd;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.UniqueSyllables.StringToWordCount;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FilterPairKeyFilenamePattern;

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
			JavaRDD<String> text = filteredFiles.values();
			JavaRDD<Map<String, Integer>> wordCount = text.map(new StringToWordCount());
			JavaPairRDD<String, Integer> words = wordCount.flatMapToPair(new FlatMap());
			JavaPairRDD<String, Integer> reducedWordCount = words.reduceByKey(new ReduceAdd());
			JavaPairRDD<Integer, String> swapped = reducedWordCount.mapToPair(new EntryToPair());
			JavaPairRDD<Integer, String> sorted = swapped.sortByKey(false);
			sorted.cache();
			JavaPairRDD<Integer, String> coalesced = sorted.coalesce(1);
			coalesced.saveAsTextFile(this.args.getOutFile().toString());
			JavaRDD<Integer> counts = sorted.keys();

			
			
//		JavaRDD<FileContent<R>> fileContent = fileRDD.map(new ReadFile<R>(broadcastArgs.getValue().getDataType()));
//		JavaPairRDD<FileContent<R>, FileContent<R>> crossedFiles = fileContent.cartesian(fileContent);
//		JavaRDD<FileContentPair<R>> allPairs = crossedFiles.map(new CartesFileContent<R>());
//		JavaRDD<FileContentPair<R>> filteredPairs = allPairs.filter(new FilterPairUniqueFile<R>());
//		JavaPairRDD<FileContentPair<R>, Long> indexedPairs = filteredPairs.zipWithIndex();
//		JavaRDD<FilePairsToMatch<R>> pairsToMatch = indexedPairs.map(new ConsolidateIndex<R>(this, broadcastMatchesDir));
//		pairsToMatch.cache();
//
//		pairsToMatch.foreachAsync(new RunApbtFile<R>(this.broadcastArgs.getValue()));
//
//		pairsToMatch.saveAsTextFile(outFile.getPath());
//
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String genOutFileName(long id) {
		return "out" + id + ".txt";
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
