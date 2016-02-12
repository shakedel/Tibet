package tau.cs.wolf.tibet.percentage_apbt.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;

import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;

public class Utils {
	public static interface Formatter<T> {
		public String format(T t);
	}

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
	
	public static void writeMatches(File f, List<MatchResult> matches, Formatter<MatchResult> formatter) {
		if (formatter == null) {
			formatter = new MatchResult.DefaultFormatter();
		}
		try (PrintStream out = new PrintStream(f)) {
			for (MatchResult match: matches) {
				out.println(formatter.format(match));
			}
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static void reportComputationTimeByStartTime(Logger logger, long startTime, String msg) {
		logger.info(msg+". Time elapsed: "+Utils.formatDuration(System.currentTimeMillis()-startTime));
	}
	
	public static void reportComputationTimeByDuration(Logger logger, long duration, String msg) {
		logger.info(msg+". Time elapsed: "+Utils.formatDuration(duration));
	}

	public static class OutputStreamGobbler {
		private final ByteArrayOutputStream baos;
		
		public OutputStreamGobbler() {
			this.baos = new ByteArrayOutputStream();
		}
		
		public OutputStream get() {
			return this.baos;
		}
		
		@Override
		public String toString() {
			return baos.toString(); 
		}
		
	}
	
	
}
