package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.kohsuke.args4j.CmdLineException;

import tau.cs.wolf.tibet.percentage_apbt.data.slicable.ListConsumer;
import tau.cs.wolf.tibet.percentage_apbt.data.slicable.Slicable;
import tau.cs.wolf.tibet.percentage_apbt.data.slicable.SlicableEntry;
import tau.cs.wolf.tibet.percentage_apbt.data.slicable.SlicableParser;
import tau.cs.wolf.tibet.percentage_apbt.main.AppBase;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.DataType;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.SrcType;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsSparkCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.IndexPairFilter;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.functions.LogHead;
import tau.cs.wolf.tibet.percentage_apbt.misc.Consumer;
import tau.cs.wolf.tibet.percentage_apbt.misc.FsUtils;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps.ClientProps;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class AppSparkLogHead extends AppBase {

	private final JavaSparkContext ctx;
	private final int numEntries;
	private final ArgsSparkCommon args;
	
	private final Broadcast<? extends ArgsCommon> bcastArgs;
	private final Broadcast<? extends Props> bcastProps;
	private final Broadcast<? extends ClientProps> bcastClientProps;
	private final Broadcast<List<SlicableEntry>> bcastEntries;
	
	
	public AppSparkLogHead(ArgsSparkCommon args, JavaSparkContext ctx) throws IOException, ClassNotFoundException {
		super(args, null);
		this.ctx = ctx;
		this.args = args;
		
		File clientPropsFile = new File(this.props.getSyslogClientPropsPath());
		ClientProps clientProps = SyslogProps.clientProps(clientPropsFile);
		
		this.bcastProps = ctx.broadcast(this.props);
		this.bcastArgs = ctx.broadcast(args);
		this.bcastClientProps = ctx.broadcast(clientProps);
		ListConsumer list = new ListConsumer();
		AppSpark.getData(args.getFilenamePattern(), args.getInDir(), args.getDataType(), list);
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
		if (this.args.isAsynced()) {
			filteredIndexPairs.foreachPartitionAsync(new LogHead(this.bcastArgs, this.bcastProps, this.bcastClientProps, this.bcastEntries));
		} else {
			filteredIndexPairs.foreachPartition(new LogHead(this.bcastArgs, this.bcastProps, this.bcastClientProps, this.bcastEntries));
		}
		logger.info("Number of Matchings: "+filteredIndexPairs.count());
	}
	
	public static void main(String[] args) throws IOException, CmdLineException, ClassNotFoundException {
		try (JavaSparkContext ctx = new JavaSparkContext(new SparkConf())) {
			new AppSparkLogHead(new ArgsSparkCommon(args), ctx).run();
		}
	}

}
