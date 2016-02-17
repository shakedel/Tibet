package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.File;

import org.apache.spark.api.java.function.Function;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContent;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public final class ReadFile implements Function<File, FileContent>, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public FileContent call(File f) throws Exception {
		int[] ints = Utils.readIntegerFile(f, Utils.whitespaces);
		return new FileContent(f, ints);
	}
	
}