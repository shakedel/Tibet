package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.Function;

import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.data.slicable.Slicable;
import tau.cs.wolf.tibet.percentage_apbt.data.slicable.SlicableParser;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.DataType;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.SrcType;
import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.PathContent;

public class PathContentAdapter<R> implements Function<Tuple2<String, String>, PathContent<R>> {
	private static final long serialVersionUID = 1L;
	
	private final DataType dataType; 

	public PathContentAdapter(DataType dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public PathContent<R> call(Tuple2<String, String> pathAndContent) throws Exception {
		@SuppressWarnings("unchecked")
		SlicableParser<R, String> parser = (SlicableParser<R, String>) AppUtils.getParser(this.dataType, SrcType.STRING);
		Slicable<R> content = parser.parse(pathAndContent._2);
		return new PathContent<R>(pathAndContent._1, content);
	}

}
