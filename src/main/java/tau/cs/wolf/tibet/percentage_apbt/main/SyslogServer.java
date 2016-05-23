package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.IOException;

import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerEventFormatterIF;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.event.SyslogServerEventFormatterDefault;
import org.productivity.java.syslog4j.server.impl.event.printstream.FileSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.event.printstream.PrintStreamSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.event.printstream.SystemOutSyslogServerEventHandler;
import org.productivity.java.syslog4j.util.SyslogUtility;

import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;

public class SyslogServer {
	
	public static void main(String args[]) throws IOException {
		SyslogServerEventFormatterIF messageOnlyFormatter = new MessageOnlyForamtter();
		SyslogServerEventFormatterIF warnThrshFormatter = new SeverityThresholdFilter(SyslogConstants.LEVEL_WARN);
		
		SyslogProps.ServerProps serverProps = SyslogProps.serverVmProps();
		SyslogServerIF syslogServer = org.productivity.java.syslog4j.server.SyslogServer.getInstance(serverProps.getProtocol());
		
		SyslogServerConfigIF config = syslogServer.getConfig();
		SyslogProps.applySyslogServerProps(config, serverProps);
		
		for (Object obj: config.getEventHandlers()) {
			SyslogServerSessionEventHandlerIF eventHandler = (SyslogServerSessionEventHandlerIF) obj;
			if (eventHandler instanceof FileSyslogServerEventHandler) {
				((PrintStreamSyslogServerEventHandler) eventHandler).setFormatter(messageOnlyFormatter);
			} else if (eventHandler instanceof SystemOutSyslogServerEventHandler) {
				// keep default formatter
//				((PrintStreamSyslogServerEventHandler) eventHandler).setFormatter(warnThrshFormatter);
			}
		}
		
		org.productivity.java.syslog4j.server.SyslogServer.getThreadedInstance(syslogServer);
		while(true) {
			SyslogUtility.sleep(1000);
		}
	}

	private static final class MessageOnlyForamtter implements SyslogServerEventFormatterIF {
		@Override
		public String format(SyslogServerEventIF event) {
			StringBuilder sb = new StringBuilder();
			sb.append(event.getMessage());
			return sb.toString();
		}
	}
	
	private static final class SeverityThresholdFilter implements SyslogServerEventFormatterIF {
		
		private SyslogServerEventFormatterIF formatter = new SyslogServerEventFormatterDefault(); 
		private final int level;
		
		public SeverityThresholdFilter(int level) {
			this(level, null);
		}
		public SeverityThresholdFilter(int level, SyslogServerEventFormatterIF formatter) {
			this.level = level;
			if (formatter != null) {
				this.formatter = formatter;
			}
		}
		
		@Override
		public String format(SyslogServerEventIF event) {
			return event.getLevel()>=this.level ? this.formatter.format(event) : null;
		}
	}
	
}
