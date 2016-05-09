package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

public class ArgsSparkCommon extends ArgsCommon {
	public ArgsSparkCommon(String[] args) throws CmdLineException {
		super(args);
	}

	private static final long serialVersionUID = 1L;
	
	private File existingPairsFile;
//	@Option(name = "-f", required = false, metaVar = "FILE", usage = "existing pairs file")
//	public void setInFile1(File f) throws CmdLineException {
//		ArgsUtils.assertFileExists(f, "-f");
//		this.existingPairsFile = f;
//	}
	public File getExistingPairsFile() {
		return this.existingPairsFile;
	}
	
	private String inDir;
	@Option(name = "-inDir", required = true, metaVar = "PATH", usage = "input directory")
	public void setInDir(String inDir) throws CmdLineException {
		this.inDir = inDir;
	}
	public String getInDir() {
		return inDir;
	}
	
	private Pattern filenamePattern;
	@SuppressWarnings("deprecation")
	@Option(name = "-p", aliases={"--pattern"}, metaVar = "REGEX", usage = "pattern to filter file names")
	public void setFilenamePattern(String patternStr) throws CmdLineException {
		try {
			this.filenamePattern = Pattern.compile(patternStr);
		} catch(PatternSyntaxException e) {
			throw new CmdLineException("Error parsing REGEX for -p option", e);
		}
	}
	public Pattern getFilenamePattern() {
		return this.filenamePattern;
	}
}