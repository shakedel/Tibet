package tau.cs.wolf.tibet.percentage_apbt.data.slicable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import tau.cs.wolf.tibet.percentage_apbt.data.ArrChar;

public class SlicableParserCharFile extends SlicableParserChar<File> {

	@Override
	public Slicable<char[]> parse(File f) {
		try(FileInputStream stream = new FileInputStream(f)) {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return new ArrChar(Charset.defaultCharset().decode(bb).array());
		} catch(IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected char[] getCharArr(File src) throws IOException {
		try(FileInputStream stream = new FileInputStream(src)) {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).array();
		}
	}

}