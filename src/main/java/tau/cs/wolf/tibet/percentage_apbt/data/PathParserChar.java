package tau.cs.wolf.tibet.percentage_apbt.data;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;

public class PathParserChar extends PathParser<char[]> {

	@Override
	public Slicable<char[]> parse(Path path) {
		try {
			try (FSDataInputStream is = path.getFileSystem(new Configuration()).open(path)) {
				String text = IOUtils.toString(is);
				return new ArrChar(text.toCharArray());
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
