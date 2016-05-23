package tau.cs.wolf.tibet.percentage_apbt.main.spark;

import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.main.AppMain;
import tau.cs.wolf.tibet.percentage_apbt.main.SerializeData.SlicableEntry;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;

final class RunApbt implements FlatMapFunction<Iterator<Tuple2<Integer, Integer>>, Matches> {
	private static final long serialVersionUID = 1L;
	
	private final Broadcast<? extends ArgsCommon> bcastArgs;
	private final Broadcast<? extends Props> bcastProps;
	private final Broadcast<List<SlicableEntry>> bcastEntries;
	
	public RunApbt(Broadcast<? extends ArgsCommon> bcastArgs, Broadcast<? extends Props> bcastProps, Broadcast<List<SlicableEntry>> bcastEntries) {
		this.bcastArgs = bcastArgs;
		this.bcastProps = bcastProps;
		this.bcastEntries = bcastEntries;
	}

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
}