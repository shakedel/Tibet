package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.kohsuke.args4j.CmdLineException;

import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.main.AppBase;
import tau.cs.wolf.tibet.percentage_apbt.main.AppMain;
import tau.cs.wolf.tibet.percentage_apbt.main.SerializeData.SlicableEntry;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsSparkCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.SyslogMatchesPartition;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps.ClientProps;

public class AppSparkTemp extends AppBase {

	private final JavaSparkContext ctx;
	private final int numEntries;
	
	private final Broadcast<? extends ArgsCommon> bcastArgs;
	private final Broadcast<? extends Props> bcastProps;
	private final Broadcast<? extends ClientProps> bcastClientProps;
	private final Broadcast<List<SlicableEntry>> bcastEntries;
	
	
	public AppSparkTemp(ArgsSparkCommon args, JavaSparkContext ctx) throws IOException, ClassNotFoundException {
		super(args, null);
		this.ctx = ctx;
		
		File clientPropsFile = new File(this.props.getSyslogClientPropsPath());
		ClientProps clientProps = SyslogProps.clientProps(clientPropsFile);
		
		this.bcastProps = ctx.broadcast(this.props);
		this.bcastArgs = ctx.broadcast(args);
		this.bcastClientProps = ctx.broadcast(clientProps);
		List<SlicableEntry> entries;
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(args.getInDir()))) {
			this.numEntries = in.readInt();
			entries = new ArrayList<SlicableEntry>(this.numEntries);
			Object obj;
			while ((obj = in.readObject()) != null) {
				entries.add((SlicableEntry) obj);
			}
		}
		bcastEntries = ctx.broadcast(entries);
	}

	@Override
	public void run() {
		List<Integer> indices = new ArrayList<Integer>(this.numEntries);
		for(int i=0; i<this.numEntries; i++) {
			indices.add(i);
		}
		
		JavaRDD<Integer> indicesRdd = ctx.parallelize(indices);
		JavaPairRDD<Integer, Integer> indexPairs = indicesRdd.cartesian(indicesRdd);
		JavaPairRDD<Integer, Integer> filteredIndexPairs = indexPairs.filter(new Function<Tuple2<Integer,Integer>, Boolean>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean call(Tuple2<Integer, Integer> v1) throws Exception {
				return v1._1 < v1._2;
			}
		});
		JavaRDD<Matches> matches = filteredIndexPairs.mapPartitions(new FlatMapFunction<Iterator<Tuple2<Integer,Integer>>, Matches>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Iterable<Matches> call(final Iterator<Tuple2<Integer, Integer>> t) throws Exception {
				return new Iterable<Matches>() {
					@Override
					public Iterator<Matches> iterator() {
						return new Iterator<Matches>() {

							@Override
							public boolean hasNext() {
								return t.hasNext();
							}

							@Override
							public Matches next() {
								Tuple2<Integer, Integer> indexPair = t.next();
								SlicableEntry entry1 = bcastEntries.getValue().get(indexPair._1);
								SlicableEntry entry2 = bcastEntries.getValue().get(indexPair._2);
								
								Args args = new Args(entry1.getKey(), entry2.getKey(), null, bcastArgs.getValue().getAppStage(), bcastArgs.getValue().getDataType(), false);
								
								AppMain app = new AppMain(args, bcastProps.getValue());
								app.setup(entry1.getValue(), entry2.getValue());
								try {
									app.run();
								} catch (Exception e) {
									throw new RuntimeException("Algorithm failed on "+entry1.getKey().getPath()+" and "+entry2.getKey().getPath(), e);
								}
								return new Matches(entry1.getKey().getPath(), entry2.getKey().getPath(), app.getResults(), bcastArgs.getValue().getAppStage());
							}

							@Override
							public void remove() {
								throw new UnsupportedOperationException();
							}
							
						};
					}
				};
			}
		});
		matches.foreachPartition(new SyslogMatchesPartition(this.bcastClientProps));
//		matches.foreachPartitionAsync(new SyslogMatchesPartition(this.bcastClientProps));
		logger.info("Number of Matchings: "+matches.count());
	}

	public static void main(String[] args) throws IOException, CmdLineException, ClassNotFoundException {
		try (JavaSparkContext ctx = new JavaSparkContext(new SparkConf())) {
			new AppSparkTemp(new ArgsSparkCommon(args), ctx).run();
		}
	}

}
