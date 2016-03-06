package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

public class ArgsSpark extends ArgsBase {
	public ArgsSpark(String[] args) throws CmdLineException {
		super(args);
	}

	private static final long serialVersionUID = 1L;
	
	private URI inDir;
	@SuppressWarnings("deprecation")
	@Option(name = "-d", required = true, metaVar = "URI", usage = "input dirirectory")
	public void setInDir(String inDir) throws CmdLineException {
		try {
			this.inDir = new URI(inDir);
		} catch (URISyntaxException e) {
			new CmdLineException("Error parsing URI for -d option", e);
		}
	}
	public URI getInDir() {
		return inDir;
	}
	
	private URI outFile;
	@SuppressWarnings("deprecation")
	@Option(name = "-outFile", required = true, metaVar = "URI", usage = "output file")
	public void setOutFile(String uriStr) throws CmdLineException {
		try {
			outFile = new URI(uriStr);
		} catch (URISyntaxException e) {
			throw new CmdLineException("Error parsing URI for -outFile option", e);
		}
	}
	public URI getOutFile() {
		return outFile;
	}
	
	private URI outDir;
	@SuppressWarnings("deprecation")
	@Option(name = "-outDir", metaVar = "URI", usage = "output directory")
	public void setOutDir(String uriStr) throws CmdLineException {
		try {
			outDir = new URI(uriStr);
		} catch (URISyntaxException e) {
			throw new CmdLineException("Error parsing URI for -outDir option", e);
		}
	}
	public URI getOutDir() {
		return outDir;
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