package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.File;

import org.apache.spark.api.java.function.Function;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.main.AppMain;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContentPair;

public final class FindMatches<R> implements Function<PathContentPair<R>, Matches>, Serializable {
	private static final long serialVersionUID = 1L;

	private final ArgsCommon args;
	
	public FindMatches(ArgsCommon args) {
		this.args = args;
	}
	
	@Override
	public Matches call(PathContentPair<R> pairToMatch) throws Exception {
		PathContent<R> p1 = pairToMatch.getPathContent1();
		PathContent<R> p2 = pairToMatch.getPathContent2();
		
		Args args = new Args(new File(p1.getPath()), new File(p2.getPath()), null, this.args.getAppStage(), this.args.getDataType());
		
		AppMain app = new AppMain(args, null);
		app.setup(p1.getContent(), p2.getContent());
		app.run();
		return new Matches(p1.getPath(), p2.getPath(), app.getResults());
	}
}