package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

public class ArgsSpark extends ArgsSparkCommon {
	private static final long serialVersionUID = 1L;
	
	public ArgsSpark(String[] args) throws CmdLineException {
		super(args);
	}
	
	
	private String outFile;
	@SuppressWarnings("deprecation")
	@Option(name = "-outFile", required = true, metaVar = "PATH", usage = "output file")
	public void setOutFile(String outFileStr) throws CmdLineException {
		this.outFile = outFileStr;
		
		try {
			FileSystem fs = FileSystem.get(new Configuration());
			if (fs.exists(new Path(outFile))) {
				throw new IllegalArgumentException("Output file (-outFile option) already exists: "+outFile);
			}
		} catch (IOException e) {
			throw new CmdLineException(e);
		}
	}
	public String getOutFile() {
		return outFile;
	}
	

}
