package tau.cs.wolf.tibet.percentage_apbt.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.skife.config.Config;
import org.skife.config.ConfigurationObjectFactory;
import org.skife.config.Default;
import org.skife.config.DefaultNull;
import org.slf4j.LoggerFactory;

import lombok.Getter;

public class PropsBuilder {
	
	public static final String NUM_THREADS_PROP_NAME = "numThreads";
	public static final String STEP_SIZE_PROP_NAME = "stepSize";
	public static final String READ_SIZE_PROP_NAME = "readSize";
	
	public static final String STEP_SIZE_DEFAULT_VALUE = "12500";
	public static final String READ_SIZE_DEFAULT_VALUE = "25000";
	
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
	}
	
	@Getter
	private final Props props;
	
	
	private PropsBuilder(Properties props) {
		ConfigurationObjectFactory factory = new ConfigurationObjectFactory(props);
		this.props = factory.build(Props.class);
	}
	
	public static Props newProps(Properties props) {
		return new PropsBuilder(props).getProps();
	}
	
	public static Props defaultProps() {
		return newProps(new Properties());
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
	
}
