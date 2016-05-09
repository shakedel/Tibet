package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.kohsuke.args4j.CmdLineException;

import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsSpark;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.FilterPairKeyFilenamePattern;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyllableScanner;

public class UniqueSyllables implements Runnable {

	private final JavaSparkContext ctx;
	private final ArgsSpark args;

	public UniqueSyllables(ArgsSpark args, JavaSparkContext ctx) {
		this.args = args;
		this.ctx = ctx;
	}

	public static void main(String[] args) throws IOException, CmdLineException {
		try (JavaSparkContext ctx = new JavaSparkContext(new SparkConf())) {
			new UniqueSyllables(new ArgsSpark(args), ctx).run();
		}
	}

	@Override
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
			System.out.println("XXX: Unique Words Count: " + sorted.count());
			JavaRDD<Integer> counts = sorted.keys();
			int totalTokens = counts.reduce(new ReduceAdd());

			System.out.println("XXX: Total Words Count:" + totalTokens);
		} catch (IOException e) {
			throw new IllegalStateException(e);
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
			return v1 + v2;
		}
	}

	private static class StringToWordCount implements Function<String, Map<String, Integer>> {
		private static final long serialVersionUID = 1L;

		@Override
		public Map<String, Integer> call(String str) throws Exception {
			return new SyllableScanner().scan(str);
		}

	}

	private static class FlatMap implements PairFlatMapFunction<Map<String, Integer>, String, Integer> {
		private static final long serialVersionUID = 1L;

		@Override
		public Iterable<Tuple2<String, Integer>> call(final Map<String, Integer> t) throws Exception {
			return new Iterable<Tuple2<String, Integer>>() {

				@Override
				public Iterator<Tuple2<String, Integer>> iterator() {
					return new Iterator<Tuple2<String, Integer>>() {
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
