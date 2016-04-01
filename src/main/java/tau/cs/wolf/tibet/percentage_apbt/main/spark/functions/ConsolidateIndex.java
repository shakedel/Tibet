package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.File;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;

import scala.Serializable;
import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.AppSpark;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContentPair;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FilePairsToMatch;

public final class ConsolidateIndex<R> implements Function<Tuple2<FileContentPair<R>,Long>, FilePairsToMatch<R>>, Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Broadcast<File> broadcastMatchesDir;

	public ConsolidateIndex(AppSpark<R> appSpark, Broadcast<File> broadcastMatchesDir) {
		this.broadcastMatchesDir = broadcastMatchesDir;
	}

	@Override
	public FilePairsToMatch<R> call(Tuple2<FileContentPair<R>, Long> v1) throws Exception {
		String outFileStr = AppSpark.genOutFileName(v1._2);
		File outFile = new File(this.broadcastMatchesDir.getValue(), outFileStr);
		return new FilePairsToMatch<R>(v1._1.getFileContent1(), v1._1.getFileContent2(), outFile);
	}
}