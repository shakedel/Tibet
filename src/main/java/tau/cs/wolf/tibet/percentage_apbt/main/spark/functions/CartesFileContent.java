package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.Function;

import scala.Serializable;
import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContent;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContentPair;

public final class CartesFileContent implements Function<Tuple2<FileContent,FileContent>, FileContentPair>, Serializable  {
	private static final long serialVersionUID = 1L;

	@Override
	public FileContentPair call(Tuple2<FileContent, FileContent> tuple) throws Exception {
		return new FileContentPair(tuple._1, tuple._2);
	}
}