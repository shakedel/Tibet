package tau.cs.wolf.tibet.percentage_apbt.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

public class Utils {
	public static String formatDuration(long milliseconds) {
		long seconds = milliseconds / 1000;
		return String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
	}

	public static String readFile(File f) {
		try(FileInputStream stream = new FileInputStream(f)) {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
			//return Charset.defaultCharset().toString();
		} catch(IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String gobbleInputStream(InputStream in) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer);
		return writer.toString();
	}

	public static File urlToFile(URL url) {
		File f;
		try {
			f = new File(url.toURI());
		} catch(URISyntaxException e) {
			f = new File(url.getPath());
		}
		return f;
	}



}
