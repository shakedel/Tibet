package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.File;

import org.apache.spark.api.java.function.Function;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.data.FileParser;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.DataType;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContent;

public final class ReadFile<R> implements Function<File, FileContent<R>>, Serializable {
	private static final long serialVersionUID = 1L;

	private final DataType dataType; 
	
	
	public ReadFile(DataType dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public FileContent<R> call(File f) throws Exception {
		@SuppressWarnings("unchecked")
		FileParser<R> parser = (FileParser<R>) this.dataType.getAppClasses().newFileParser();
		return new FileContent<R>(f, parser.parse(f));
	}
	
}