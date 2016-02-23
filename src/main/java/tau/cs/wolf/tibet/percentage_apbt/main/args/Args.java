package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.io.File;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.AppStage;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.DataType;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils.OutputStreamGobbler;


public class Args extends ArgsBase {

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
		if (f.isDirectory()) {
			throw new CmdLineException("output file is a directory: "+f.getParent());
		}
		if (!f.getParentFile().isDirectory()) {
			throw new CmdLineException("output file directory does not exist: "+f.getParent());
		}
		this.outFile = f;
	}
	public File getOutFile() {
		return outFile;
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
	
	private Duration pollDuration;
	@Option(name = "-pollDuration", usage = "The duration between polls")
	public void setPollDuration(String pollDurationStr) throws CmdLineException {
		this.pollDuration = Period.parse(pollDurationStr).toStandardDuration();
	}
	public void setPollDuration(Duration pollDuration) throws CmdLineException {
		this.pollDuration = pollDuration;
	}
	public Duration getPollDuration() {
		return this.pollDuration;
	}
	
	public Args(String[] args, Props props) throws CmdLineException {
		this(args);
		ArgsUtils.overrideArgsWithProps(this, props);
	}
	
	public Args(File inFile1, File inFile2, File outFile, AppStage appStage, DataType dataType) {
		super(appStage, dataType);
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
