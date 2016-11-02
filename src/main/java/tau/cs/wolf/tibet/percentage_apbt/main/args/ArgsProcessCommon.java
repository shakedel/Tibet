package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.nio.file.Path;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

public class ArgsProcessCommon extends ArgsCommon {

	private static final long serialVersionUID = 1L;
	
	public ArgsProcessCommon(String[] args) throws CmdLineException {
		super(args);
	}
	
	public ArgsProcessCommon(ArgsCommon argsCommon, Path inDir, long docsCacheSize) {
		super(argsCommon);
		this.inDir = inDir;
		this.docsCacheSize = docsCacheSize;
	}
	
	public ArgsProcessCommon(ArgsProcessCommon other) {
		this(other, other.getInDir(), other.getDocsCacheSize());
	}
	
	private Path inDir;
	@Option(name = "-inDir", required = true, metaVar = "DIR", usage = "input dir")
	public void setInDir(Path dir) throws CmdLineException {
		ArgsUtils.assertDirExists(dir, "-d");
		this.inDir = dir;
	}
	
	public Path getInDir() {
		return this.inDir;
	}

	private long docsCacheSize;
	@SuppressWarnings("deprecation")
	@Option(name = "-cacheSize", required = true, metaVar = "LONG", usage = "docs cache size")
	public void setDocsCacheSize(long cacheSize) throws CmdLineException {
		if (cacheSize < 0) {
			throw new CmdLineException("cache size must be positive!!!");
		}
		this.docsCacheSize = cacheSize;
	}
	
	public long getDocsCacheSize() {
		return this.docsCacheSize;
	}

}
