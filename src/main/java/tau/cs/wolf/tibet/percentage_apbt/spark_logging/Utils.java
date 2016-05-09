package tau.cs.wolf.tibet.percentage_apbt.spark_logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.productivity.java.syslog4j.SyslogIF;
import org.slf4j.Logger;

public class Utils {

	public static void logAllLevels(String msg, SyslogIF syslogClient, Logger... loggers) {
		for (Logger logger: loggers) {
			logAllLevels(logger, syslogClient, msg);
		}
		
	}
	private static void logAllLevels(Logger logger, SyslogIF syslogClient, String msg) {
		System.out.println("SYSOUT: "+msg);
		System.err.println("SYSERR: "+msg);
		
		if (logger != null) {
			logger.trace(logger.getName()+", TRACE: "+msg);
			logger.debug(logger.getName()+", DEBUG: "+msg);
			logger.info(logger.getName()+", INFO: "+msg);
			logger.warn(logger.getName()+", WARN: "+msg);
			logger.error(logger.getName()+", ERROR: "+msg);
		}
		
		if (syslogClient != null) {
			syslogClient.debug("syslog, DEBUG: "+msg);
			syslogClient.info("syslog, INFO: "+msg);
			syslogClient.warn("syslog, WARN: "+msg);
			syslogClient.error("syslog, ERROR: "+msg);
			syslogClient.flush();
		}
	}
	
	public static Properties propsFromVmArg(String vmArgName, boolean mandatory) {
		String pathStr = System.getProperty(vmArgName);
		if (pathStr == null) {
			if (mandatory) {
				throw new IllegalArgumentException("no JVM property: "+vmArgName);
			}
			return new Properties();
		}
		File propsFile = new File(pathStr);
		if (!propsFile.isFile()) {
			throw new IllegalArgumentException("vm argument '"+vmArgName+"' does not point to an existing file!");
		}
		try {
			try (InputStream is = new FileInputStream(propsFile)) {
				Properties props = new Properties();
				props.load(is);
				return props;
			}
		} catch (IOException e) {
			throw new IllegalStateException("Should not have reached this code line");
		}
		
	}

}
