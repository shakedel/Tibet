package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import java.io.File;

import scala.Serializable;
import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.data.Slicable;

public final class FileContent<R> extends Tuple2<File, Slicable<R>> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public FileContent(File f1, Slicable<R> content) {
		super(f1, content);
	}
	
	public File getFile() {
		return this._1;
	}
	public Slicable<R> getContent() {
		return this._2;
	}
}