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
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsMonitored;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.CalcApbt;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.CartesFileContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.LogResults;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.ReadFile;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.UniqueFilePairFilter;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContentPair;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class AppSparkLog implements Runnable {
	
	private Logger logger = LoggerFactory.getLogger(AppSparkLog.class);
	
	private final SparkConf sparkConf;
	
	public AppSparkLog(SparkConf sparkConf) {
		this.sparkConf = sparkConf;
	}
	
	public void run() {
		try (JavaSparkContext ctx = new JavaSparkContext(this.sparkConf)) {
			this.spark(ctx);
		}

	}
	
	private void spark(JavaSparkContext ctx) {
		File inDir = new File("src/test/resources/stem");
		String fileFilterPattern = "in\\d+\\.txt";
		String dirFilterPattern = null;
		
		IOFileFilter fileFilter = fileFilterPattern==null ? FileFilterUtils.trueFileFilter() : new Utils.PatternFileFilter(Pattern.compile(fileFilterPattern));
		IOFileFilter dirFilter = dirFilterPattern==null ? FileFilterUtils.trueFileFilter(): new Utils.PatternFileFilter(Pattern.compile(dirFilterPattern));
		
		Collection<File> filteredFiles = FileUtils.listFiles(inDir, fileFilter, dirFilter);
		List<File> files = new ArrayList<File>(filteredFiles);
		
		JavaRDD<File> fileRDD = ctx.parallelize(files);
		JavaRDD<FileContent> fileContent  = fileRDD.map(new ReadFile());
		JavaPairRDD<FileContent, FileContent> crossedFiles = fileContent.cartesian(fileContent);
		JavaRDD<FileContentPair> allPairs = crossedFiles.map(new CartesFileContent());
		JavaRDD<FileContentPair> filteredPairs = allPairs.filter(new UniqueFilePairFilter());
		JavaRDD<Matches> matches = filteredPairs.map(new CalcApbt());
		matches.foreach(new LogResults());
	}
	
	public static void main(String[] args) throws IOException, CmdLineException {
		// Local mode
		SparkConf sparkConf = new SparkConf().setAppName("Tibet").setMaster("local");
		new AppSparkLog(sparkConf).run();
	}
	
}
	
