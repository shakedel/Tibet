package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import scala.Serializable;
import scala.Tuple2;

public final class FileContentPair<R> extends Tuple2<FileContent<R>, FileContent<R>> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public FileContentPair(FileContent<R> f1, FileContent<R> f2) {
		super(f1, f2);
	}
	
	public FileContent<R> getFileContent1() {
		return this._1;
	}
	public FileContent<R> getFileContent2() {
		return this._2;
	}
}