package tau.cs.wolf.tibet.percentage_apbt.misc;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import com.Ostermiller.util.ArrayIterator;

public class FsUtils {

	public static FsUtils.InputStreamIterator getInputStreamIterator(URI inDirPath, IOFileFilter filter) throws IOException {
		FsUtils.UriProtocol protocol = FsUtils.UriProtocol.parse(inDirPath);
		switch(protocol) {
			case HDFS:
				return new FsUtils.HdfsIterator(inDirPath, filter);
			case FILE:
				return new FsUtils.FileInputStreamIterator(inDirPath, filter);
			default:
				throw new IllegalStateException("Unknown rotocol enum value: "+protocol);
		}
	}
	
	public static enum UriProtocol {
		HDFS, FILE;
		public static UriProtocol parse(URI uri) {
			String protocolStr = uri.getScheme();
			for (UriProtocol protocol: UriProtocol.values()) {
				if (protocol.name().toLowerCase().equals(protocolStr.toLowerCase())) {
					return protocol;
				}
			}
			throw new IllegalArgumentException("Unknown protocol in URI: "+uri);
			
		}
	}

	public static interface InputStreamIterator extends Closeable, Iterator<Entry<String, InputStream>> {}

	public static class FileInputStreamIterator implements InputStreamIterator {
		
		private final Iterator<File> fileIter;
		
		public FileInputStreamIterator(URI uri, final IOFileFilter filter) {
			this.fileIter = FileUtils.listFiles(new File(uri), filter==null ? TrueFileFilter.INSTANCE : filter, TrueFileFilter.INSTANCE).iterator();
		}
		@Override public boolean hasNext() { 
			return fileIter.hasNext(); 
		}
		@Override public Entry<String, InputStream> next() { 
			try {
				File f = fileIter.next();
				return new AbstractMap.SimpleEntry<String, InputStream>(f.getPath(), new FileInputStream(f));
			} catch (FileNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}
		@Override public void remove() { 
			fileIter.remove(); 
		}
		
		@Override
		public void close() throws IOException {
			// do nothing
		}
	}

	public static class HdfsIterator implements InputStreamIterator {
		public final class PathFileFilterAdapter implements PathFilter {
			private final IOFileFilter filter;
			public PathFileFilterAdapter(IOFileFilter filter) {
				this.filter = filter;
			}
			@Override
			public boolean accept(Path path) {
				return filter==null ? true : filter.accept(null, path.getName());
			}
		}

		private final FileSystem fs;
		private final ArrayIterator<FileStatus> hdfsIter;
	
		public HdfsIterator(URI uri, final IOFileFilter filter) throws IOException {
			this.fs = FileSystem.get(uri, new Configuration());
			FileStatus[] fileStatus = fs.listStatus(new Path(uri), new PathFileFilterAdapter(filter));
			this.hdfsIter = new ArrayIterator<FileStatus>(fileStatus);
		}
		
		@Override public boolean hasNext() { 
			return this.hdfsIter.hasNext();
		}
		@Override public Entry<String, InputStream> next() { 
			try {
				Path path = hdfsIter.next().getPath();
				return new AbstractMap.SimpleEntry<String, InputStream>(path.toString(), this.fs.open(path));
			} catch (IOException e) {
				throw new IllegalStateException();
			}
		}
		@Override public void remove() { 
			throw new UnsupportedOperationException(); 
		}
	
		@Override
		public void close() throws IOException {
			this.fs.close();
		}
	}

}
