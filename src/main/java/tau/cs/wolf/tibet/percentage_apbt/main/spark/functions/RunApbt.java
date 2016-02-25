package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.VoidFunction;
import org.slf4j.LoggerFactory;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.main.AppMain;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsBase;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PairsToMatch;

public final class RunApbt<R> implements VoidFunction<PairsToMatch<R>>, Serializable {
	private static final long serialVersionUID = 1L;

	private final ArgsBase args;
	
	public RunApbt(ArgsBase args) {
		this.args = args;
	}
	
	@Override
	public void call(PairsToMatch<R> pairToMatch) throws Exception {
		FileContent<R> f1 = pairToMatch.getF1();
		FileContent<R> f2 = pairToMatch.getF2();
		
		Args args = new Args(f1.getFile(), f2.getFile(), pairToMatch.getOutFile(), this.args.getAppStage(), this.args.getDataType());
		
		AppMain app = new AppMain(args, null);
		app.setup(f1.getContent(), f2.getContent());
		app.run();
		app.writeResults();
		LoggerFactory.getLogger("matches").error(String.format("Finished matching file: %s, %s", f1.getFile(), f2.getFile()));
	}
}