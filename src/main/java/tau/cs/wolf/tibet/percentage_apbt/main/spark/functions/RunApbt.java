package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.data.slicable.SlicableEntry;
import tau.cs.wolf.tibet.percentage_apbt.main.AppMain;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsMain;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.ApbtMatches;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;

public class RunApbt implements FlatMapFunction<Iterator<Tuple2<Integer, Integer>>, ApbtMatches> {
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
	public Iterable<ApbtMatches> call(final Iterator<Tuple2<Integer, Integer>> t) throws Exception {
		return new Iterable<ApbtMatches>() {
			@Override
			public Iterator<ApbtMatches> iterator() {
				return new Iterator<ApbtMatches>() {

					@Override
					public boolean hasNext() {
						return t.hasNext();
					}

					@Override
					public ApbtMatches next() {
						Tuple2<Integer, Integer> indexPair = t.next();
						SlicableEntry entry1 = bcastEntries.getValue().get(indexPair._1);
						SlicableEntry entry2 = bcastEntries.getValue().get(indexPair._2);
						
						ArgsMain args = new ArgsMain(new File(entry1.getKey()), new File(entry2.getKey()), null, bcastArgs.getValue().getAppStage(), bcastArgs.getValue().getDataType(), false);
						
						AppMain app = new AppMain(args, bcastProps.getValue());
						app.setup(entry1.getValue(), entry2.getValue());
						try {
							app.run();
						} catch (Exception e) {
							throw new RuntimeException("Algorithm failed on "+entry1.getKey()+" and "+entry2.getKey(), e);
						}
						return new ApbtMatches(entry1.getKey(), entry2.getKey(), app.getResults(), bcastArgs.getValue().getAppStage());
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