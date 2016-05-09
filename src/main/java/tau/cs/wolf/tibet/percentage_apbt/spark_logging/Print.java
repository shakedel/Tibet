package tau.cs.wolf.tibet.percentage_apbt.spark_logging;

import org.apache.spark.api.java.function.VoidFunction;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Print implements VoidFunction<Integer> {
	private static final long serialVersionUID = 1L;
	
	public final static Logger logger = LoggerFactory.getLogger("print");
	public final static SyslogIF syslogClient = initSyslogClient(); 
	
	// init syslogClient
	private static SyslogIF initSyslogClient() {
		SyslogProps.ClientProps clientProps = SyslogProps.clientVmProps();
		SyslogIF syslogClient = Syslog.getInstance(clientProps.getProtocol());
		SyslogProps.applySyslogClientProps(syslogClient.getConfig(), clientProps);
		return syslogClient;
	}
	
	public void call(Integer input) throws Exception {
		Utils.logAllLevels("print-call: "+input, syslogClient, logger);
	}
}