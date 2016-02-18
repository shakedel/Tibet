package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.Function;
import org.slf4j.LoggerFactory;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.main.AppFactory;
import tau.cs.wolf.tibet.percentage_apbt.main.AppFactory.AppType;
import tau.cs.wolf.tibet.percentage_apbt.main.AppPercentageInt;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContentPair;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;

public final class CalcApbt implements Function<FileContentPair, Matches>, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Matches call(FileContentPair pair) throws Exception {
		FileContent f1 = pair.getFileContent1();
		FileContent f2 = pair.getFileContent2();
		
		Args args = new Args(f1.getFile(), f2.getFile(), null);
		
		AppPercentageInt app = (AppPercentageInt) AppFactory.getMain(AppType.STEM, args, null, false);
		app.setup(f1.getContent(), f2.getContent());
		app.run();
		return new Matches(f1, f2, app.getResults());
	}
}