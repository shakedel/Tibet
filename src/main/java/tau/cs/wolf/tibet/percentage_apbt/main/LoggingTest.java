package tau.cs.wolf.tibet.percentage_apbt.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingTest {
	
	public static Logger logger = LoggerFactory.getLogger(LoggingTest.class);
	public static Logger matchesLogger = LoggerFactory.getLogger("MATCHES");
	
	public static void main(String[] args) {
		
		// testing		
		System.out.println("sysout test");
		
		logger.trace("myLogger trace test");
		logger.debug("myLogger debug test");
		logger.info("myLogger info test");
		logger.warn("myLogger warn test");
		logger.error("myLogger error test");
		
		System.err.println("syserr test");
		
		matchesLogger.trace("matchesLogger trace test");
		matchesLogger.debug("matchesLogger debug test");
		matchesLogger.info("matchesLogger info test");
		matchesLogger.warn("matchesLogger warn test");
		matchesLogger.error("matchesLogger error test");
		
		System.out.println("sysout finished test");
	}
}
