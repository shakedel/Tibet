package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.File;

import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContentPair;

public final class UniqueFilePairFilter<R> implements Function<FileContentPair<R>, Boolean>, Serializable {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public Boolean call(FileContentPair<R> fileContentPair) throws Exception {
		File f1 = fileContentPair.getFileContent1().getFile();
		File f2 = fileContentPair.getFileContent2().getFile();
		return f1.getAbsolutePath().compareTo(f2.getAbsolutePath()) < 0;
	}
}