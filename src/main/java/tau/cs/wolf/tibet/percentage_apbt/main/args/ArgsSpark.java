package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

public class ArgsSpark extends ArgsCommon {
	public ArgsSpark(String[] args) throws CmdLineException {
		super(args);
	}

	private static final long serialVersionUID = 1L;
	
	private String inDir;
	@Option(name = "-inDir", required = true, metaVar = "PATH", usage = "input directory")
	public void setInDir(String inDir) throws CmdLineException {
		this.inDir = inDir;
	}
	public String getInDir() {
		return inDir;
	}
	
	private String outFile;
	@SuppressWarnings("deprecation")
	@Option(name = "-outFile", required = true, metaVar = "PATH", usage = "output file")
	public void setOutFile(String outFileStr) throws CmdLineException {
		this.outFile = outFileStr;
		
		try {
			FileSystem fs = FileSystem.get(new Configuration());
			if (fs.exists(new Path(outFile))) {
				throw new IllegalArgumentException("Output file (-outFile option) already exists");
			}
		} catch (IOException e) {
			throw new CmdLineException(e);
		}
	}
	public String getOutFile() {
		return outFile;
	}
	
//	private Path outDir;
//	@SuppressWarnings("deprecation")
//	@Option(name = "-outDir", metaVar = "PATH", usage = "output directory")
//	public void setOutDir(String outDirStr) throws CmdLineException {
//		outDir = new Path(outDirStr);
//		try {
//			FileSystem fs = FileSystem.get(new Configuration());
//			if (fs.exists(outDir)) {
//				throw new IllegalArgumentException("Output dir (-outDir option) already exists");
//			}
//		} catch (IOException e) {
//			throw new CmdLineException(e);
//		}
//	}
//	public Path getOutDir() {
//		return outDir;
//	}
	
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