package tau.cs.wolf.tibet.percentage_apbt.data.slicable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;

public class SlicableParserCharFile extends SlicableParserChar<File> {

	@Override
	protected char[] getCharArr(File src) throws IOException {
		try (InputStream is = new FileInputStream(src)) {
			String text = IOUtils.toString(is);
			return text.toCharArray();
		}
//		try(FileInputStream stream = new FileInputStream(src)) {
//			FileChannel fc = stream.getChannel();
//			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//			/* Instead of using default, pass in a decoder. */
//			return Charset.defaultCharset().decode(bb).array();
//		}
	}

}
