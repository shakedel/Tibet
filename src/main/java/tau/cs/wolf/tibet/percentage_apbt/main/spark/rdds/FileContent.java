package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import java.io.File;

import scala.Serializable;
import scala.Tuple2;

public final class FileContent extends Tuple2<File, int[]> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public FileContent(File f1, int[] f2) {
		super(f1, f2);
	}
	
	public File getFile() {
		return this._1;
	}
	public int[] getContent() {
		return this._2;
	}
}