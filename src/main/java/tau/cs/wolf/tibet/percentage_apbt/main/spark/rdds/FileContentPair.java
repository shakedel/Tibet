package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import scala.Serializable;
import scala.Tuple2;

public final class FileContentPair extends Tuple2<FileContent, FileContent> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public FileContentPair(FileContent f1, FileContent f2) {
		super(f1, f2);
	}
	
	public FileContent getFileContent1() {
		return this._1;
	}
	public FileContent getFileContent2() {
		return this._2;
	}
}