package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.Function;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.AppStage;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.DataType;
import tau.cs.wolf.tibet.percentage_apbt.main.AppMain;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContentPair;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;

public final class CalcApbt<R> implements Function<FileContentPair<R>, Matches<R>>, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Matches<R> call(FileContentPair<R> pair) throws Exception {
		FileContent<R> f1 = pair.getFileContent1();
		FileContent<R> f2 = pair.getFileContent2();
		
		Args args = new Args(f1.getFile(), f2.getFile(), null, AppStage.ALIGNMENT, DataType.INT);
		
		AppMain app = new AppMain(args, null, false);
		app.setup(f1.getContent(), f2.getContent());
		app.run();
		return new Matches<R>(f1, f2, app.getResults());
	}
}