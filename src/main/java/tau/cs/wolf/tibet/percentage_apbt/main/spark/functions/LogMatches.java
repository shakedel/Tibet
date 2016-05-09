package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import org.apache.spark.api.java.function.VoidFunction;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;

import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;

public class LogMatches implements VoidFunction<Matches> {

	private static final long serialVersionUID = 1L;
	
	public final static SyslogIF syslogClient = initSyslogClient(); 
	private static SyslogIF initSyslogClient() {
		SyslogProps.ClientProps clientProps = SyslogProps.clientVmProps();
		SyslogIF syslogClient = Syslog.getInstance(clientProps.getProtocol());
		SyslogProps.applySyslogClientProps(syslogClient.getConfig(), clientProps);
		return syslogClient;
	}
	
	@Override
	public void call(Matches matches) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("START\n").append(matches.toString()).append("\nEND");
		syslogClient.info(sb.toString());
	}

}
