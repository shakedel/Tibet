package tau.cs.wolf.tibet.percentage_apbt.data.slicable;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;

public class SlicableParserCharPath extends SlicableParserChar<Path> {

	@Override
	protected char[] getCharArr(Path path) throws IOException {
		try (FSDataInputStream is = path.getFileSystem(new Configuration()).open(path)) {
			String text = IOUtils.toString(is);
			return text.toCharArray();
		}
	}

}
