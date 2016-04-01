//package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;
//
//import org.apache.hadoop.fs.Path;
//
//import scala.Serializable;
//
//public class PathPairsToMatch<R> implements Serializable {
//	private static final long serialVersionUID = 1L;
//	
//	protected final PathContent<R> f1;
//	protected final PathContent<R> f2;
//	protected final Path outFile;
//	
//	public PathPairsToMatch(PathContent<R> f1, PathContent<R> f2, Path outFile) {
//		super();
//		this.f1 = f1;
//		this.f2 = f2;
//		this.outFile = outFile;
//	}
//	
//	public PathContent<R> getF1() {
//		return this.f1;
//	}
//
//	public PathContent<R> getF2() {
//		return this.f2;
//	}
//
//	public Path getOutFile() {
//		return this.outFile;
//	}
//	
//	@Override
//	public String toString() {
//		return String.format("%s (%s, %s)", outFile, f1.getPath(), f2.getPath());
//	}
//	
//}