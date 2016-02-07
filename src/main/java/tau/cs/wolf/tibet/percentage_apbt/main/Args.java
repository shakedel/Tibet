package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import lombok.Data;

@Data
public class Args {
	@Option(name="-h", aliases={"-help", "--help"}, help=true, usage="print this message")
	private boolean help = false;

	private File inFile1;
	@Option(name="-f1", required=true, metaVar="FILE", usage="1st input file")
	public void setInFile1(File f) throws CmdLineException {
		assertFileExists(f);
		this.inFile1 = f;
	}

	private File inFile2;
	@Option(name="-f2", required=true, metaVar="FILE", usage="2nd input file")
	public void setInFile2(File f) throws CmdLineException {
		assertFileExists(f);
		this.inFile2 = f;
	}
	
	private File outFile;
	@SuppressWarnings("deprecation")
	@Option(name="-out", required=true, metaVar="FILE", usage="output file")
	public void setOutFile(File f) throws CmdLineException {
		if (!f.getParentFile().isDirectory()) {
			throw new CmdLineException("output file directory does not exist: "+f.getParent());
		}
		this.outFile = f;
	}

	private int minLength = 60;
	@Option(name="-minLength", required=true, metaVar="int", usage="minimin length for matches (default: 60)")
	public void setMinlength(int minLength) throws CmdLineException {
		assertIsNonNegative(minLength);
		this.minLength = minLength;
	}

	private int maxError = 10;
	@Option(name="-maxError", metaVar="int", usage="maximum allowed Levenshtein distance (default: 10)")
	public void getMaxError(Integer maxError) throws CmdLineException {
		assertIsNonNegative(maxError);
		this.maxError = maxError;
	}

	public Args(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			// parse the arguments.
			parser.parseArgument(args);
			if (this.help) {
				parser.printUsage(System.out);
				System.out.println();
			}
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();
		}
		
	}
	
	public Args(File inFile1, File inFile2, File outFile) {
		try {
			assertFileExists(inFile1);
			assertFileExists(inFile2);
			this.inFile1 = inFile1;
			this.inFile2 = inFile2;
			this.outFile = outFile;
		} catch (CmdLineException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	public Args(File inFile1, File inFile2, File outFile, int minLength, int maxError) {
		this(inFile1, inFile2, outFile);
		try {
			assertIsNonNegative(minLength);
			assertIsNonNegative(maxError);
			this.minLength = minLength;
			this.maxError = maxError;
		} catch (CmdLineException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}


	@SuppressWarnings("deprecation")
	private static boolean assertFileExists(File f) throws CmdLineException {
		if (!f.isFile()) {
			throw new CmdLineException("input File does not exist: "+f.getPath());
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	private static int assertIsNonNegative(int num) throws CmdLineException {
		if (num < 0) {
			throw new CmdLineException("must be a non-negative integer");
		}
		return num;
	}

}
