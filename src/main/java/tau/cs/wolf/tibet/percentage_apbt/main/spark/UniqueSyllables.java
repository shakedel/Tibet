package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsBase;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsUtils;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyllableScanner;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class UniqueSyllables implements Runnable {
	
	private static class _Args extends ArgsBase {
		public _Args(String[] args) throws CmdLineException {
			super(args);
		}

		private static final long serialVersionUID = 1L;
		
		private File inDir;
		@SuppressWarnings("deprecation")
		@Option(name = "-d", required = true, metaVar = "DIR", usage = "input dir")
		public void setInDir(File f) throws CmdLineException {
			if (!f.isDirectory()) {
				throw new CmdLineException("input dir is not a directory: "+f.getPath());
			}
			ArgsUtils.assertFileExists(f, "-f2");
			this.inDir = f;
		}
		public File getInDir() {
			return inDir;
		}
		
		private File outFile;
		@SuppressWarnings("deprecation")
		@Option(name = "-out", required = true, metaVar = "FILE", usage = "output file")
		public void setOutFile(File f) throws CmdLineException {
			if (f.isFile()) {
				throw new CmdLineException("output file already exists: "+f.getParent());
			}
			if (!f.getParentFile().isDirectory()) {
				throw new CmdLineException("output file directory does not exist: "+f.getParent());
			}
			this.outFile = f;
		}
		public File getOutFile() {
			return outFile;
		}
		
		private Pattern filenamePattern = Pattern.compile(".+\\.txt");
		@Option(name = "-p", aliases={"--patern"}, metaVar = "REGEX", usage = "pattern to filter files")
		public void setFilenamePattern(String patternStr) throws CmdLineException {
			this.filenamePattern = Pattern.compile(patternStr);
		}
		public Pattern getFilenamePattern() {
			return this.filenamePattern;
		}
	}
	
	private final JavaSparkContext ctx;
	private final _Args args;
	
	public UniqueSyllables(_Args args, JavaSparkContext ctx) {
		this.args = args;
		this.ctx = ctx;
	}	
	
	public static void main(String[] args) throws IOException, CmdLineException {
		// Local mode
		SparkConf sparkConf = new SparkConf().setAppName("SyllableCount").setMaster("local");
		try (JavaSparkContext ctx = new JavaSparkContext(sparkConf)) {
			new UniqueSyllables(new _Args(args), ctx).run();
		}
	}
	
	@Override
	public void run() {
		
		List<File> files = new ArrayList<File>(FileUtils.listFiles(this.args.getInDir(), new Utils.PatternFileFilter(this.args.getFilenamePattern()),
				FileFilterUtils.trueFileFilter()));

		
		JavaRDD<File> fileRDD = ctx.parallelize(files);
		JavaRDD<Map<String, Integer>> wordCount = fileRDD.map(new FileToWordCount());
		JavaPairRDD<String, Integer> words = wordCount.flatMapToPair(new FlatMap());
		JavaPairRDD<String, Integer> reducedWordCount = words.reduceByKey(new ReduceAdd());
		JavaPairRDD<Integer, String> swapped = reducedWordCount.mapToPair(new EntryToPair());
		JavaPairRDD<Integer, String> sorted = swapped.sortByKey(false);
		if (this.args.getOutFile().isDirectory()) {
			try {
				FileUtils.deleteDirectory(this.args.getOutFile());
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		sorted.saveAsTextFile(this.args.getOutFile().getPath());
		System.out.println("Total Count: " + sorted.count());
		JavaRDD<Integer> counts = sorted.map(new PairToKey());
		int totalTokens = counts.reduce(new ReduceAdd());
		
		System.out.println("number of tokens:"+ totalTokens);
	}
	

	private static final class PairToKey implements Function<Tuple2<Integer, String>, Integer> {
		private static final long serialVersionUID = 1L;

		@Override
		public Integer call(Tuple2<Integer, String> v1) throws Exception {
			return v1._1;
		}
	}

	private static final class EntryToPair implements PairFunction<Tuple2<String, Integer>, Integer, String> {
		private static final long serialVersionUID = 1L;

		@Override
		public Tuple2<Integer, String> call(Tuple2<String, Integer> t) throws Exception {
			return new Tuple2<Integer, String>(t._2, t._1);
		}
	}


	private static final class ReduceAdd implements Function2<Integer, Integer, Integer> {
		private static final long serialVersionUID = 1L;

		@Override
		public Integer call(Integer v1, Integer v2) throws Exception {
			return v1+v2;
		}
	}

	private static class FileToWordCount implements Function<File, Map<String, Integer>> {
		private static final long serialVersionUID = 1L;

		@Override
		public Map<String, Integer> call(File f) throws Exception {
			return new SyllableScanner().scan(f);
		}
		
	}
	
	private static class FlatMap implements PairFlatMapFunction<Map<String,Integer>, String, Integer> {
		private static final long serialVersionUID = 1L;

		@Override
		public Iterable<Tuple2<String, Integer>> call(final Map<String, Integer> t) throws Exception {
			return new Iterable<Tuple2<String, Integer>>() {

				@Override
				public Iterator<Tuple2<String, Integer>> iterator() {
					return new Iterator<Tuple2<String,Integer>>() {
						final Iterator<Entry<String, Integer>> iter = t.entrySet().iterator();

						@Override
						public boolean hasNext() {
							return iter.hasNext();
						}

						@Override
						public Tuple2<String, Integer> next() {
							Entry<String, Integer> entry = iter.next();
							return new Tuple2<String, Integer>(entry.getKey(), entry.getValue());
						}

						@Override
						public void remove() {
							iter.remove();
						}
					};
					
				}
			};
		}
	}
}
