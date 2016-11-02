package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedSet;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.DataType;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;

public class ArgsPreprocess extends ArgsBase {

	private static final long serialVersionUID = 1L;
	
	public ArgsPreprocess(String[] args) throws CmdLineException {
		super(args);
	}
	
	@Override
	public String toString() {
		return "ArgsPreprocess [inDir=" + inDir + ", inPath1=" + inPath1
				+ ", inPath2=" + inPath2 + ", dataType=" + dataType
				+ ", outDir=" + outDir + ", maxGrpSize=" + maxGrpSize + "]";
	}

	private String inDir;
	@Option(name = "-inRootDir", required = true, metaVar = "DIR", usage = "root input dir")
	public void setInDir(Path dir) throws CmdLineException {
		ArgsUtils.assertDirExists(dir, "-d");
		this.inDir = dir.toString();
	}
	
	@Option(name = "-inPath1", required = true, metaVar = "path", usage = "1st input path pattern") 
	private String inPath1;
	public SortedSet<Path> calcInPaths1() {
		return ArgsUtils.getAllPaths(Paths.get(this.inDir), this.inPath1);
	}
	
	@Option(name = "-inPath2", required = true, metaVar = "path", usage = "2nd input path pattern") 
	private String inPath2;
	public SortedSet<Path> calcInPaths2() {
		return ArgsUtils.getAllPaths(Paths.get(this.inDir), this.inPath2);
	}
	
	private DataType dataType;
	@Option(name = "--dataType", required = true, usage = "Type of input file data")
	public void setDataType(String dataTypeStr) throws CmdLineException {
		this.dataType = ArgsUtils.parseEnum(DataType.class, dataTypeStr);
	}
	public DataType getDataType() {
		return this.dataType;
	}
	
	private String outDir;
	@SuppressWarnings("deprecation")
	@Option(name = "-outDir", required = true, metaVar = "DIR", usage = "output dir")
	public void setOutDir(Path dir) throws CmdLineException {
		try {
			Files.createDirectories(dir);
			try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
				if (Files.isDirectory(dir)) {
					if (ds.iterator().hasNext()) {
						throw new CmdLineException("output dir is not empty!!! "+dir);
					}
				}
			}
		} catch (IOException e) {
			throw new CmdLineException(e);
		}
		this.outDir = dir.toString();
	}
	public Path getOutDir() {
		return Paths.get(this.outDir);
	}
	
	@Option(name = "-groupSize", required = true, metaVar = "INT", usage = "maximal group size")
	private int maxGrpSize;
	public int getMaxGrpSize() {
		return this.maxGrpSize;
	}
	
	@Override
	public void fillWithProps(Props props) throws CmdLineException {
		// no additional args to override
		super.fillWithProps(props);
	}

}
