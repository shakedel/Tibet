package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

public class ArgsSparkCommon extends ArgsCommon {
	public ArgsSparkCommon(String[] args) throws CmdLineException {
		super(args);
	}

	private static final long serialVersionUID = 1L;
	
	private URI inDir;
	@SuppressWarnings("deprecation")
	@Option(name = "-inDir", required = true, metaVar = "PATH", usage = "input directory")
	public void setInDir(String inDir) throws CmdLineException {
		try {
			this.inDir = new URI(inDir);
		} catch (URISyntaxException e) {
			throw new CmdLineException(e);
		}
	}
	public URI getInDir() {
		return inDir;
	}
	
	@Option(name = "-isAsync", usage = "spark foreach sync")
	private boolean isAsynced;
	public boolean isAsynced() {
		return isAsynced;
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