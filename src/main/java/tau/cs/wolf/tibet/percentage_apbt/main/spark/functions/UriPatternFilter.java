package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.io.File;
import java.net.URI;
import java.util.regex.Pattern;

import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

public class UriPatternFilter {

	public static <R> Function<Tuple2<String, R>, Boolean> byTuple2Key(Pattern pattern) {
		return new ByTuple2Key<R>(pattern);
	}
	
	public static Function<String, Boolean> byString(Pattern pattern) {
		return new ByString(pattern);
	}
	
	
	
	private abstract static class Base<R> implements Function<R, Boolean> {
		private static final long serialVersionUID = 1L;
		
		private final Pattern pattern;
		
		public Base(Pattern pattern) {
			this.pattern = pattern;
		}
		
		@Override
		public Boolean call(R v1) throws Exception {
			if (this.pattern == null) {
				return true;
			}
			URI uri = this.getUri(v1);
			File f = new File(uri.getPath());
			return this.pattern.matcher(f.getName()).matches();
		}
		
		protected abstract URI getUri(R r) throws Exception;
	}

	private static class ByTuple2Key<R> extends Base<Tuple2<String, R>> {
		private static final long serialVersionUID = 1L;
		
		public ByTuple2Key(Pattern pattern) {
			super(pattern);
		}

		@Override
		protected URI getUri(Tuple2<String, R> v) throws Exception {
			return new URI(v._1);
		}
	}
	
	private static class ByString extends Base<String> {
		private static final long serialVersionUID = 1L;
		
		public ByString(Pattern pattern) {
			super(pattern);
		}

		@Override
		protected URI getUri(String r) throws Exception {
			return new URI(r);
		}
		
	}
	
}