package tau.cs.wolf.tibet.percentage_apbt.matching;

import java.util.List;

import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public interface Apbt<R> extends Runnable {
	public List<Interval> getMaximalSolutions();
	public void setup(R seq1, R seq2, ProcessType processBy, Args args, Props props);
}
