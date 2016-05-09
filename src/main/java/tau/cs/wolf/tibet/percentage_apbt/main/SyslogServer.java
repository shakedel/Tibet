package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.IOException;

import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerEventFormatterIF;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.event.printstream.FileSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.event.printstream.SystemOutSyslogServerEventHandler;
import org.productivity.java.syslog4j.util.SyslogUtility;

import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;

public class SyslogServer {
	
	public static void main(String args[]) throws IOException {
		SyslogServerEventFormatterIF formatter = new SyslogServerEventFormatterIF() {
			@Override
			public String format(SyslogServerEventIF event) {
				return event.getMessage();
			}
		};
		
		SyslogProps.ServerProps serverProps = SyslogProps.serverVmProps();
		SyslogServerIF syslogServer = org.productivity.java.syslog4j.server.SyslogServer.getInstance(serverProps.getProtocol());
		
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
		
		org.productivity.java.syslog4j.server.SyslogServer.getThreadedInstance(syslogServer);
		while(true) {
			SyslogUtility.sleep(1000);
		}
	}
	
}
