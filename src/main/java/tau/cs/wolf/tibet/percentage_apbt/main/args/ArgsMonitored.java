package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.io.File;
import java.time.Duration;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import tau.cs.wolf.tibet.percentage_apbt.main.AppFactory.AppType;

public class ArgsMonitored extends Args {

	@Option(name = "-t", aliases = {"-type"}, usage = "Type of algorithm to use")
	private AppType appType = AppType.PERCENTAGE;
	public AppType getAppType() {
		return this.appType;
	}
	
	private Duration pollDuration;
	@Option(name = "-pollDuration", usage = "The duration between polls")
	public void setPollDuration(String pollDurationStr) throws CmdLineException {
		this.pollDuration = Duration.parse(pollDurationStr);
	}
	public void setPollDuration(Duration pollDuration) throws CmdLineException {
		this.pollDuration = pollDuration;
	}
	public Duration getPollDuration() {
		return this.pollDuration;
	}

	public ArgsMonitored(File inFile1, File inFile2, File outFile) {
		super(inFile1, inFile2, outFile);
	}

	public ArgsMonitored(String[] args) throws CmdLineException {
		super(args);
	}
}