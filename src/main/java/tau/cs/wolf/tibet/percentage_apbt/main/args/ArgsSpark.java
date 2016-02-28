package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.io.File;
import java.util.regex.Pattern;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.AppStage;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.DataType;

public class ArgsSpark extends ArgsCommon {

	private static final long serialVersionUID = 1L;
	
	private File inDir;
	@SuppressWarnings("deprecation")
	@Option(name = "-d", required = true, metaVar = "DIR", usage = "input dir")
	public void setInDir(File f) throws CmdLineException {
		if (!f.isDirectory()) {
			throw new CmdLineException("input dir is not a directory: "+f.getPath());
		}
		ArgsUtils.assertFileExists(f, "-f2");
		this.inDir = f;
	}
	public File getInDir() {
		return inDir;
	}
	
	private File outFile;
	@SuppressWarnings("deprecation")
	@Option(name = "-out", required = true, metaVar = "FILE", usage = "output file")
	public void setOutFile(File f) throws CmdLineException {
		if (f.isFile()) {
			throw new CmdLineException("output file already exists: "+f.getParent());
		}
		if (!f.getParentFile().isDirectory()) {
			throw new CmdLineException("output file directory does not exist: "+f.getParent());
		}
		this.outFile = f;
	}
	public File getOutFile() {
		return outFile;
	}
	
	private Pattern filenamePattern;
	@Option(name = "-p", aliases={"--patern"}, required = true, metaVar = "FILE", usage = "output file")
	public void setFilenamePattern(String patternStr) throws CmdLineException {
		this.filenamePattern = Pattern.compile(patternStr);
	}
	public Pattern getFilenamePattern() {
		return this.filenamePattern;
	}
	
	
	public ArgsSpark(AppStage appStage, DataType dataType) {
		super(appStage, dataType);
	}

	public ArgsSpark(String[] args) throws CmdLineException {
		super(args);
	}

}
