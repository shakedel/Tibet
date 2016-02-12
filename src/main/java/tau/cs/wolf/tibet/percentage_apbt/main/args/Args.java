package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.io.File;
import java.time.Duration;
import java.time.format.DateTimeParseException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils.OutputStreamGobbler;


public class Args {

	@Option(name = "-h", aliases = { "-help", "--help" }, help = true, usage = "print this message")
	private boolean help = false;
	
	private File inFile1;
	@Option(name = "-f1", required = true, metaVar = "FILE", usage = "1st input file")
	public void setInFile1(File f) throws CmdLineException {
		ArgsUtils.assertFileExists(f, "-f1");
		this.inFile1 = f;
	}
	public File getInFile1() {
		return inFile1;
	}

	private File inFile2;
	@Option(name = "-f2", required = true, metaVar = "FILE", usage = "2nd input file")
	public void setInFile2(File f) throws CmdLineException {
		ArgsUtils.assertFileExists(f, "-f2");
		this.inFile2 = f;
	}
	public File getInFile2() {
		return inFile2;
	}
	
	
	private File outFile;
	@SuppressWarnings("deprecation")
	@Option(name = "-out", required = true, metaVar = "FILE", usage = "output file")
	public void setOutFile(File f) throws CmdLineException {
		if (!f.getParentFile().isDirectory()) {
			throw new CmdLineException("output file directory does not exist: "+f.getParent());
		}
		this.outFile = f;
	}
	public File getOutFile() {
		return outFile;
	}
	
	private Integer minLength = null;
	@Option(name = "-minLength", metaVar = "int", usage = "minimin length for matches")
	public void setMinlength(int minLength) throws CmdLineException {
		ArgsUtils.assertIsNonNegative(minLength, "-minLength");
		this.minLength = minLength;
	}
	public Integer getMinLength() {
		return minLength;
	}
	
	private Integer maxError = null;
	@Option(name = "-maxError", metaVar = "int", usage = "maximum allowed Levenshtein distance")
	public void setMaxError(Integer maxError) throws CmdLineException {
		ArgsUtils.assertIsNonNegative(maxError, "-maxError");
		this.maxError = maxError;
	}
	public Integer getMaxError() {
		return maxError;
	}
	
	private Duration timeout = null;
	@SuppressWarnings("deprecation")
	@Option(name = "-timeout", metaVar = "int", usage = "timeout for execution")
	public void setTimeout(String timeoutStr) throws CmdLineException {
		try {
			this.timeout = Duration.parse(timeoutStr);
		} catch(DateTimeParseException e) {
			throw new CmdLineException(e);
		}
	}
	public void setTimeout(Duration duration) {
		this.timeout = Duration.ZERO.plus(duration);
	}
	public Duration getTimeout() {
		return timeout;
	}
	
	private Integer minDistanceUnion = null;
	@Option(name="-minDistanceUnion", metaVar="int", usage="min distance for union")
	public void setMinDistanceUnion(Integer minDistanceUnion) throws CmdLineException {
		ArgsUtils.assertIsNonNegative(minDistanceUnion, "-minDistanceUnion");
		this.minDistanceUnion = minDistanceUnion;
	}
	public Integer getMinDistanceUnion() {
		return minDistanceUnion;
	}
	
	private Float localAlignPadRatio = null;
	@Option(name="-localAlignPadRatio", metaVar="float", usage="ratio for local align pad")
	public void setLocalAlignPadRatio(Float localAlignPadRatio) throws CmdLineException {
		ArgsUtils.assertIsFraction(localAlignPadRatio, "-localAlignPadRatio");
		this.localAlignPadRatio = localAlignPadRatio;
	}
	public Float getLocalAlignPadRatio() {
		return this.localAlignPadRatio;
	}
	
	
	@SuppressWarnings("deprecation")
	public Args(String[] args) throws CmdLineException {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			// parse the arguments.
			parser.parseArgument(args);
			if (this.help) {
				parser.printUsage(System.out);
				System.out.println();
			}
		} catch (CmdLineException e) {
			OutputStreamGobbler osg = new OutputStreamGobbler();
			parser.printUsage(osg.get());
			throw new CmdLineException(e.getMessage()+"\n"+osg.toString());
		}
		
	}
	
	public Args(String[] args, Props props) throws CmdLineException {
		this(args);
		ArgsUtils.overrideArgsWithProps(this, props);
	}
	
	public Args(File inFile1, File inFile2, File outFile) {
		try {
			ArgsUtils.assertFileExists(inFile1, null);
			ArgsUtils.assertFileExists(inFile2, null);
			this.inFile1 = inFile1;
			this.inFile2 = inFile2;
			this.outFile = outFile;
		} catch (CmdLineException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	
}
