package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.File;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;

import scala.Serializable;
import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.AppSpark;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.FileContentPair;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PairsToMatch;

public final class ConsolidateIndex implements Function<Tuple2<FileContentPair,Long>, PairsToMatch>, Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Broadcast<File> broadcastMatchesDir;

	public ConsolidateIndex(AppSpark appSpark, Broadcast<File> broadcastMatchesDir) {
		this.broadcastMatchesDir = broadcastMatchesDir;
	}

	@Override
	public PairsToMatch call(Tuple2<FileContentPair, Long> v1) throws Exception {
		String outFileStr = AppSpark.genOutFileName(v1._2);
		File outFile = new File(this.broadcastMatchesDir.getValue(), outFileStr);
		return new PairsToMatch(v1._1.getFileContent1(), v1._1.getFileContent2(), outFile);
	}
}