package tau.cs.wolf.tibet.percentage_apbt.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.skife.config.Coercer;
import org.skife.config.Coercible;
import org.skife.config.Config;
import org.skife.config.ConfigurationObjectFactory;
import org.skife.config.Default;
import org.skife.config.DefaultNull;
import org.slf4j.LoggerFactory;


public class PropsBuilder {
	
	public static final String CFG_PATH_VM_PROP_NAME = "percentage_apbt.cfgPath";
	
	public static final String NUM_THREADS_PROP_NAME = "numThreads";
	public static final String STEP_SIZE_PROP_NAME = "stepSize";
	public static final String READ_SIZE_PROP_NAME = "readSize";
	public static final String MIN_LENGTH_PROP_NAME = "minLength";
	public static final String MAX_ERROR_PROP_NAME = "maxError";
	public static final String TIMEOUT_PROP_NAME = "timeout";
	public static final String MIN_DISTANCE_UNION_PROP_NAME = "minDistanceUnion";
	public static final String LOCAL_ALIGN_PAD_RATIO_PROP_NAME = "localAlignPadRatio";
	public static final String MATCH_AWARD_PROP_NAME = "matchAward";
	public static final String MISMATCH_PENALTY_PROP_NAME = "mismatchPenalty";
	public static final String GAP_PENALTY_PROP_NAME = "gapPenalty";
	public static final String CHUNK_SIZE_PROP_NAME = "chunkSize";
	public static final String MAX_MATCH_LENGTH_PROP_NAME = "maxMatchLength";
	public static final String POLL_DURATION_PROP_NAME = "pollDuration";
	public static final String COMPUTE_LEVENSHTEIN_DISTANCE_PROP_NAME = "computeLevenshteinDistance";
	
	public static final String STEP_SIZE_DEFAULT_VALUE = "12500";
	public static final String READ_SIZE_DEFAULT_VALUE = "25000";
	public static final String MIN_LENGTH_DEFAULT_VALUE = "10";
	public static final String MAX_ERROR_DEFAULT_VALUE = "5";
	public static final String TIMEOUT_DEFAULT_VALUE = "PT10M"; // 10 minutes
	public static final String MIN_DISTANCE_UNION_DEFAULT_VALUE = "10";
	public static final String LOCAL_ALIGN_PAD_RATIO_DEFAULT_VALUE = "0.12";
	public static final String MATCH_AWARD_DEFAULT_VALUE = "1";
	public static final String MISMATCH_PENALTY_DEFAULT_VALUE = "-1";
	public static final String GAP_PENALTY_DEFAULT_VALUE = "-1";
	public static final String CHUNK_SIZE_DEFAULT_VALUE = "1000";
	public static final String MAX_MATCH_LENGTH_DEFAULT_VALUE = "500";
	public static final String POLL_DURATION_DEFAULT_VALUE = "PT5M"; // 5 minutes
	public static final String COMPUTE_LEVENSHTEIN_DISTANCE_DEFAULT_VALUE = "true";
	
	public static interface Props {
		@Config(NUM_THREADS_PROP_NAME)
		@DefaultNull
		public Integer getNumThreads();
		
		@Config(STEP_SIZE_PROP_NAME)
		@Default(STEP_SIZE_DEFAULT_VALUE)
		public int getStepSize();
		
		@Config(READ_SIZE_PROP_NAME)
		@Default(READ_SIZE_DEFAULT_VALUE)
		public int getReadSize();
		
		@Config(MIN_LENGTH_PROP_NAME)
		@Default(MIN_LENGTH_DEFAULT_VALUE)
		public int getMinLength();
		
		@Config(MAX_ERROR_PROP_NAME)
		@Default(MAX_ERROR_DEFAULT_VALUE)
		public int getMaxError();
		
		@Config(TIMEOUT_PROP_NAME)
		@Default(TIMEOUT_DEFAULT_VALUE)
		public Duration getTimeout();

		@Config(MIN_DISTANCE_UNION_PROP_NAME)
		@Default(MIN_DISTANCE_UNION_DEFAULT_VALUE)
		public int getMinDistanceUnion();
		
		@Config(LOCAL_ALIGN_PAD_RATIO_PROP_NAME)
		@Default(LOCAL_ALIGN_PAD_RATIO_DEFAULT_VALUE)
		public float getLocalAlignPadRatio();

		@Config(MISMATCH_PENALTY_PROP_NAME)
		@Default(MISMATCH_PENALTY_DEFAULT_VALUE)
		public int getMismatchPenalty();
		
		@Config(MATCH_AWARD_PROP_NAME)
		@Default(MATCH_AWARD_DEFAULT_VALUE)
		public int getMatchAward();
		
		@Config(GAP_PENALTY_PROP_NAME)
		@Default(GAP_PENALTY_DEFAULT_VALUE)
		public int getGapPenalty();
		
		@Config(CHUNK_SIZE_PROP_NAME)
		@Default(CHUNK_SIZE_DEFAULT_VALUE)
		public int getChunkSize();
		 
		@Config(MAX_MATCH_LENGTH_PROP_NAME)
		@Default(MAX_MATCH_LENGTH_DEFAULT_VALUE)
		public int getMaxMatchLength();
		
		@Config(POLL_DURATION_PROP_NAME)
		@Default(POLL_DURATION_DEFAULT_VALUE)
		public Duration getPollDuration();
		
		@Config(COMPUTE_LEVENSHTEIN_DISTANCE_PROP_NAME)
		@Default(COMPUTE_LEVENSHTEIN_DISTANCE_DEFAULT_VALUE)
		public boolean getComputeLevenshteinDistance();
		
	}
	
	public Props getProps() {
		return props;
	}

	private final Props props;
	
	
	private PropsBuilder(Properties props) {
		ConfigurationObjectFactory factory = new ConfigurationObjectFactory(props);
		factory.addCoercible(new DurationCoercible());
		this.props = factory.build(Props.class);
	}
	
	public static Props newProps(Properties props) {
		return new PropsBuilder(props).getProps();
	}

	
	public static Props defaultProps() {
		String cfgPathStr = System.getProperty(CFG_PATH_VM_PROP_NAME);
		if (cfgPathStr == null) {
			return newProps(new Properties());
		}
		File propsFile = new File(cfgPathStr);
		if (!propsFile.isFile()) {
			throw new IllegalArgumentException("vm argument '"+CFG_PATH_VM_PROP_NAME+"' does not point to an existing file!");
		}
		try {
			return PropsBuilder.newProps(propsFile);
		} catch (IOException e) {
			throw new IllegalStateException("Should not have reached this code line");
		}
	}
	
	public static Props newProps(InputStream is) throws IOException {
		Properties props = new Properties();
		props.load(is);
		return newProps(props);
	}
	
	public static Props newProps(File f) throws IOException {
		try (InputStream is = new FileInputStream(f)) {
			Properties props = new Properties();
			props.load(is);
			return newProps(props);
		}
	}
	
	public static Props newProps(String vmArgName) throws FileNotFoundException, IOException {
		String pathToProps = System.getProperty(vmArgName);
		if (pathToProps == null) {
			LoggerFactory.getLogger(Props.class).warn("Missing properties file, using default settings");
			return defaultProps();
		}
		return newProps(new File(pathToProps));
	}
	
	public static class DurationCoercible implements Coercible<Duration> {
		
		@Override
		public Coercer<Duration> accept(Class<?> clazz) {
			return new Coercer<Duration>() {
				public Duration coerce(String value) {
					return Period.parse(value).toStandardDuration();
				};
			};
		};
	}
	

}