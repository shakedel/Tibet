package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import java.io.File;

import scala.Serializable;

public class PairsToMatch<R> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected final FileContent<R> f1;
	protected final FileContent<R> f2;
	protected final File outFile;
	
	public PairsToMatch(FileContent<R> f1, FileContent<R> f2, File outFile) {
		super();
		this.f1 = f1;
		this.f2 = f2;
		this.outFile = outFile;
	}
	
	public FileContent<R> getF1() {
		return this.f1;
	}

	public FileContent<R> getF2() {
		return this.f2;
	}

	public File getOutFile() {
		return this.outFile;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s, %s)", outFile, f1.getFile(), f2.getFile());
	}
	
}