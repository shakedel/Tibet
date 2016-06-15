package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.File;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerEventFormatterIF;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.event.SyslogServerEventFormatterDefault;
import org.productivity.java.syslog4j.server.impl.event.printstream.FileSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.event.printstream.SystemOutSyslogServerEventHandler;

import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsBase;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsUtils;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;

public class SyslogServerDaemon implements Daemon {
	
	public static final class MessageOnlyForamtter implements SyslogServerEventFormatterIF {
		@Override
		public String format(SyslogServerEventIF event) {
			StringBuilder sb = new StringBuilder();
			sb.append(event.getMessage());
			return sb.toString();
		}
	}
	
	public static final class SeverityThresholdFilter implements SyslogServerEventFormatterIF {
		
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

	private final SyslogServerEventFormatterIF messageOnlyFormatter = new MessageOnlyForamtter();
	private final SyslogServerEventFormatterIF warnThrshFormatter = new SeverityThresholdFilter(SyslogConstants.LEVEL_WARN);
	private SyslogProps.ServerProps serverProps;
	private SyslogServerIF syslogServer;
	
	public void init(DaemonContext daemonContext) throws DaemonInitException, Exception {
		ArgsSyslogServerDaemon daemonProps = new ArgsSyslogServerDaemon(daemonContext.getArguments());
		serverProps = SyslogProps.serverProps(daemonProps.getPropsFile());
		
		this.syslogServer = org.productivity.java.syslog4j.server.SyslogServer.getInstance(serverProps.getProtocol());
		SyslogServerConfigIF config = syslogServer.getConfig();
		SyslogProps.applySyslogServerProps(config, serverProps);
		
		for (Object obj: config.getEventHandlers()) {
			SyslogServerSessionEventHandlerIF eventHandler = (SyslogServerSessionEventHandlerIF) obj;
			if (eventHandler instanceof FileSyslogServerEventHandler) {
				// keep default formatter
//				((PrintStreamSyslogServerEventHandler) eventHandler).setFormatter(messageOnlyFormatter);
			} else if (eventHandler instanceof SystemOutSyslogServerEventHandler) {
				// keep default formatter
//				((PrintStreamSyslogServerEventHandler) eventHandler).setFormatter(warnThrshFormatter);
			}
		}
	}

	public static class ArgsSyslogServerDaemon extends ArgsBase {
		
		public ArgsSyslogServerDaemon(String[] args) throws CmdLineException {
			super(args);
		}
		
		private File propsFile;
		@Option(name = "-p", metaVar = "File", required=true, usage = "syslog props file")
		public void setPropsFile(File syslogPropFile) throws CmdLineException {
			ArgsUtils.assertFileExists(syslogPropFile, "-p");
			this.propsFile = propsFile;
		}
		public File getPropsFile() {
			return this.propsFile;
		}
	}
	
	@Override
	public void destroy() {
		this.syslogServer.shutdown();
	}

	@Override
	public void start() throws Exception {
		this.syslogServer.run();
	}

	@Override
	public void stop() throws Exception {
		this.syslogServer.shutdown();
	}
	
}
