package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.CartesFileContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.ConsolidateIndex;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.ReadFile;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.RunApbt;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.UniqueFilePairFilter;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContentPair;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PairsToMatch;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class AppSpark<R> implements Runnable {
	
	public static void main(String[] args) throws IOException {
		new AppSpark<int[]>().run();
	}
	
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(AppSpark.class);
	
	Broadcast<File> broadcastMatchesDir = null;
	Broadcast<Args> broadcastArgs = null;
	
	
	public void run() {
		
		// Local mode
		SparkConf sparkConf = new SparkConf().setAppName("HelloWorld").setMaster("local");
		try (JavaSparkContext ctx = new JavaSparkContext(sparkConf)) {

			File inDir = new File("src/test/resources/stem/");
			File outDir = new File("work/out/spark");
			boolean overwrite = true;
			String fileFilterPattern = "in\\d+\\.txt";
			
			if (outDir.isDirectory()) {
				if (overwrite) {
					FileUtils.deleteDirectory(outDir);
				} else {
					throw new IllegalArgumentException("Output directory already exists");
				}
			}
			
			File outFile = new File(outDir, "index.txt");
			File matchesDir = new File(outDir, "matches");
			matchesDir.mkdirs();
			
			IOFileFilter fileFilter = new Utils.PatternFileFilter(Pattern.compile(fileFilterPattern));
			IOFileFilter dirFilter = FileFilterUtils.trueFileFilter();
			
			Collection<File> filteredFiles = FileUtils.listFiles(inDir, fileFilter, dirFilter);
			List<File> files = new ArrayList<File>(filteredFiles);
			broadcastMatchesDir = ctx.broadcast(matchesDir);
			
			JavaRDD<File> fileRDD = ctx.parallelize(files);
			JavaRDD<FileContent<R>> fileContent  = fileRDD.map(new ReadFile<R>(broadcastArgs.getValue().getDataType()));
			JavaPairRDD<FileContent<R>, FileContent<R>> crossedFiles = fileContent.cartesian(fileContent);
			JavaRDD<FileContentPair<R>> allPairs = crossedFiles.map(new CartesFileContent<R>());
			JavaRDD<FileContentPair<R>> filteredPairs = allPairs.filter(new UniqueFilePairFilter<R>());
			JavaPairRDD<FileContentPair<R>, Long> indexedPairs = filteredPairs.zipWithIndex();
			JavaRDD<PairsToMatch<R>> pairsToMatch = indexedPairs.map(new ConsolidateIndex<R>(this, broadcastMatchesDir));
			pairsToMatch.cache();
			
			pairsToMatch.foreachAsync(new RunApbt<R>());
			
			pairsToMatch.saveAsTextFile(outFile.getPath());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

	}
	
	public static String genOutFileName(long id) {
		return "out"+id+".txt";
	}
	
}
	
