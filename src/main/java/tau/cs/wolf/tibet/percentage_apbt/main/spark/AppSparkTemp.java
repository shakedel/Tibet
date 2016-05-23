package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.kohsuke.args4j.CmdLineException;

import tau.cs.wolf.tibet.percentage_apbt.data.slicable.Slicable;
import tau.cs.wolf.tibet.percentage_apbt.data.slicable.SlicableParser;
import tau.cs.wolf.tibet.percentage_apbt.main.AppBase;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.DataType;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.SrcType;
import tau.cs.wolf.tibet.percentage_apbt.main.SerializeData.SlicableEntry;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsSparkCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.SyslogMatchesPartition;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.misc.Consumer;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps.ClientProps;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

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
		ListConsumer list = new ListConsumer();
		getData(args.getFilenamePattern(), args.getInDir(), args.getDataType(), list);
		this.numEntries = list.get().size();
		bcastEntries = ctx.broadcast(list.get());
	}

	@Override
	public void run() {
		List<Integer> indices = new ArrayList<Integer>(this.numEntries);
		for(int i=0; i<this.numEntries; i++) {
			indices.add(i);
		}
		
		JavaRDD<Integer> indicesRdd = ctx.parallelize(indices);
		JavaPairRDD<Integer, Integer> indexPairs = indicesRdd.cartesian(indicesRdd);
		JavaPairRDD<Integer, Integer> filteredIndexPairs = indexPairs.filter(new IndexPairFilter());
		JavaRDD<Matches> matches = filteredIndexPairs.mapPartitions(new RunApbt(this.bcastArgs, this.bcastProps, this.bcastEntries));
		matches.foreachPartition(new SyslogMatchesPartition(this.bcastClientProps));
//		matches.foreachPartitionAsync(new SyslogMatchesPartition(this.bcastClientProps));
		logger.info("Number of Matchings: "+matches.count());
	}
	
	private void getData(Pattern filenamePattern, String inDirPath, DataType dataType, Consumer<SlicableEntry> consumer) {
		FileFilter filter = filenamePattern==null? null : new Utils.PatternFileFilter(filenamePattern);
		File[] files = new File(inDirPath).listFiles(filter);
		@SuppressWarnings("unchecked")
		SlicableParser<?, File> parser = (SlicableParser<?, File>) AppUtils.getParser(dataType, SrcType.FILE);
		
		for (File f: files) {
			Slicable<?> slice = parser.parse(f);
			SlicableEntry entry = new SlicableEntry(f, slice);
			consumer.accept(entry);
		}
	}
	
	private static class ListConsumer implements Consumer<SlicableEntry> {
		private final List<SlicableEntry> list = new ArrayList<SlicableEntry>();
		
		@Override
		public void accept(SlicableEntry t) {
			this.list.add(t);
		}
		
		public List<SlicableEntry> get() {
			return Collections.unmodifiableList(this.list);
		}
	}

	public static void main(String[] args) throws IOException, CmdLineException, ClassNotFoundException {
		try (JavaSparkContext ctx = new JavaSparkContext(new SparkConf())) {
			new AppSparkTemp(new ArgsSparkCommon(args), ctx).run();
		}
	}

}
