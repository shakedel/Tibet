package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.File;

import org.apache.spark.api.java.function.VoidFunction;
import org.slf4j.LoggerFactory;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.main.AppMain;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FilePairsToMatch;

public final class RunApbtFile<R> implements VoidFunction<FilePairsToMatch<R>>, Serializable {
	private static final long serialVersionUID = 1L;

	private final ArgsCommon args;
	
	public RunApbtFile(ArgsCommon args) {
		this.args = args;
	}
	
	@Override
	public void call(FilePairsToMatch<R> pairToMatch) throws Exception {
		FileContent<R> f1 = pairToMatch.getF1();
		FileContent<R> f2 = pairToMatch.getF2();
		
		Args args = new Args(new File(f1.getFile().getName()), new File(f2.getFile().getName()), pairToMatch.getOutFile(), this.args.getAppStage(), this.args.getDataType());
		
		AppMain app = new AppMain(args, null);
		app.setup(f1.getContent(), f2.getContent());
		app.run();
		app.writeResults();
		LoggerFactory.getLogger("matches").error(String.format("Finished matching file: %s, %s", f1.getFile().getName(), f2.getFile().getName()));
	}
}