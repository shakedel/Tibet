package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.data.slicable.SlicableEntry;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps.ClientProps;

public class RunAndLog implements VoidFunction<Iterator<Tuple2<Integer, Integer>>> {
	private static final long serialVersionUID = 1L;

	private final RunApbt runApbt;
	private final SyslogMatchesPartition syslog;

	
	public RunAndLog(Broadcast<? extends ArgsCommon> bcastArgs, Broadcast<? extends Props> bcastProps,
			Broadcast<? extends ClientProps> bcastClientProps, Broadcast<List<SlicableEntry>> bcastEntries) {
		this.runApbt = new RunApbt(bcastArgs, bcastProps, bcastEntries);
		this.syslog = new SyslogMatchesPartition(bcastClientProps);
	}

	
	@Override
	public void call(Iterator<Tuple2<Integer, Integer>> t) throws Exception {
		Iterable<Matches> iter = this.runApbt.call(t);
		this.syslog.call(iter.iterator());
		
	}

}
