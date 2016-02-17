package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyllableScanner;

public class AppSpark {
	public static void main(String[] args) throws IOException {

		// Local mode
		SparkConf sparkConf = new SparkConf().setAppName("HelloWorld").setMaster("local");
		try (JavaSparkContext ctx = new JavaSparkContext(sparkConf)) {

			File inDir = new File("work/clean/");
			File outFile = new File("work/out/test.txt");
			
			String prefixFilterStr = "";
			String suffixFilterStr = ".txt";
			IOFileFilter prefixFilter = FileFilterUtils.prefixFileFilter(prefixFilterStr);
			IOFileFilter suffixFilter = FileFilterUtils.suffixFileFilter(suffixFilterStr);
			IOFileFilter fileFilter = FileFilterUtils.and(prefixFilter, suffixFilter);
			IOFileFilter dirFilter = FileFilterUtils.trueFileFilter();
			
			List<File> files = new ArrayList<File>(FileUtils.listFiles(inDir, fileFilter, dirFilter));

			JavaRDD<File> fileRDD = ctx.parallelize(files);
			JavaRDD<Map<String, Integer>> wordCount  = fileRDD.map(new FileToWordCount());
			JavaPairRDD<String, Integer> words = wordCount.flatMapToPair(new FlatMap());
			JavaPairRDD<String, Integer> reducedWordCount = words.reduceByKey(new ReduceAdd());
			JavaPairRDD<Integer, String> swapped = reducedWordCount.mapToPair(new EntryToPair());
			JavaPairRDD<Integer, String> sorted = swapped.sortByKey(false);
			if (outFile.isDirectory()) {
				FileUtils.deleteDirectory(outFile);
			}
			sorted.saveAsTextFile(outFile.getPath());
			System.out.println("Total Count: " + sorted.count());
			JavaRDD<Integer> counts = sorted.map(new PairToKey());
			int totalTokens = counts.reduce(new ReduceAdd());
			
			System.out.println("number of tokens:"+ totalTokens);
		}

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
	
