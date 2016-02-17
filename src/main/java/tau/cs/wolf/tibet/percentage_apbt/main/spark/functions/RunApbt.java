package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.VoidFunction;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.main.AppFactory;
import tau.cs.wolf.tibet.percentage_apbt.main.AppFactory.AppType;
import tau.cs.wolf.tibet.percentage_apbt.main.AppPercentageInt;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PairsToMatch;

public final class RunApbt implements VoidFunction<PairsToMatch>, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void call(PairsToMatch pairToMatch) throws Exception {
		FileContent f1 = pairToMatch.getF1();
		FileContent f2 = pairToMatch.getF2();
		
		Args args = new Args(f1.getFile(), f2.getFile(), pairToMatch.getOutFile());
		
		AppPercentageInt app = (AppPercentageInt) AppFactory.getMain(AppType.STEM, args, null, true);
		app.setup(f1.getContent(), f2.getContent());
		app.run();
	}
}