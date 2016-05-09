package tau.cs.wolf.tibet.percentage_apbt.main;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;

import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;

public class SyslogClient {
	
	public static void main(String args[]) throws InterruptedException {
		SyslogProps.ClientProps clientProps = SyslogProps.clientVmProps();
		SyslogIF syslog = Syslog.getInstance(clientProps.getProtocol());
		SyslogProps.applySyslogClientProps(syslog.getConfig(), clientProps);
		syslog.info(args[0]);
		syslog.flush();
		// needed to let the syslog send the messages!
		Thread.sleep(1000);
	}
	
}
