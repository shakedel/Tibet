package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import java.io.File;

import scala.Serializable;

public final class PairsToMatch implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final FileContent f1;
	private final FileContent f2;
	private final File outFile;
	
	public PairsToMatch(FileContent f1, FileContent f2, File outFile) {
		super();
		this.f1 = f1;
		this.f2 = f2;
		this.outFile = outFile;
	}
	
	public FileContent getF1() {
		return this.f1;
	}

	public FileContent getF2() {
		return this.f2;
	}

	public File getOutFile() {
		return this.outFile;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s, %s)", outFile, f1, f2);
	}
	
}