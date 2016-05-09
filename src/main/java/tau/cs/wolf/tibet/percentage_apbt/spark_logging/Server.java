package tau.cs.wolf.tibet.percentage_apbt.spark_logging;

import java.io.IOException;

import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerEventFormatter;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.event.printstream.FileSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.event.printstream.SystemOutSyslogServerEventHandler;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class Server {
	
	public static void main(String args[]) throws IOException {
		SyslogServerEventFormatter formatter = new SyslogServerEventFormatter() {
			@Override
			public String format(SyslogServerEventIF event) {
				return event.getMessage();
			}
		};
		
		SyslogProps.ServerProps serverProps = SyslogProps.serverVmProps();
		SyslogServerIF syslogServer = SyslogServer.getInstance(serverProps.getProtocol());
		
		SyslogServerConfigIF config = syslogServer.getConfig();
		SyslogProps.applySyslogServerProps(config, serverProps);
		
		for (Object obj: config.getEventHandlers()) {
			SyslogServerSessionEventHandlerIF eventHandler = (SyslogServerSessionEventHandlerIF) obj;
			if (eventHandler instanceof FileSyslogServerEventHandler) {
				((FileSyslogServerEventHandler) eventHandler).setFormatter(formatter);
			} else if (eventHandler instanceof SystemOutSyslogServerEventHandler) {
				config.removeEventHandler(eventHandler);
			}
		}
		
		SyslogServer.getThreadedInstance(syslogServer);
		while(true) {
			SyslogUtility.sleep(1000);
		}
	}
	
}
