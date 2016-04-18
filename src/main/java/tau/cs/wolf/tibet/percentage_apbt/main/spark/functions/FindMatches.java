package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.File;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.main.AppMain;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContentPair;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public final class FindMatches<R> implements Function<PathContentPair<R>, Matches>, Serializable {
	private static final long serialVersionUID = 1L;

	private final ArgsCommon args;
	private final Props props;
	
	public FindMatches(Broadcast<? extends ArgsCommon> args, Broadcast<? extends Props> props) {
		this.args = args.getValue();
		this.props = props.getValue();
	}
	
	@Override
	public Matches call(PathContentPair<R> pairToMatch) throws Exception {
		PathContent<R> p1 = pairToMatch.getPathContent1();
		PathContent<R> p2 = pairToMatch.getPathContent2();
		
		Args args = new Args(new File(p1.getPath()), new File(p2.getPath()), null, this.args.getAppStage(), this.args.getDataType(), false);
		
		AppMain app = new AppMain(args, props);
		app.setup(p1.getContent(), p2.getContent());
		app.run();
		return new Matches(p1.getPath(), p2.getPath(), app.getResults());
	}
}