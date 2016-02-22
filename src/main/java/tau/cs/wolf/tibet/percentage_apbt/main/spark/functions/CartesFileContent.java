package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.Function;

import scala.Serializable;
import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContentPair;

public final class CartesFileContent<R> implements Function<Tuple2<FileContent<R>,FileContent<R>>, FileContentPair<R>>, Serializable  {
	private static final long serialVersionUID = 1L;

	@Override
	public FileContentPair<R> call(Tuple2<FileContent<R>, FileContent<R>> tuple) throws Exception {
		return new FileContentPair<R>(tuple._1, tuple._2);
	}
}