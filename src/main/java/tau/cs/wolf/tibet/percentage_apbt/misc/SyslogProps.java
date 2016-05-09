package tau.cs.wolf.tibet.percentage_apbt.misc;

import java.io.IOException;
import java.util.Properties;

import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.event.printstream.FileSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.event.printstream.SystemOutSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfigIF;
import org.skife.config.Config;
import org.skife.config.ConfigurationObjectFactory;
import org.skife.config.Default;
import org.skife.config.DefaultNull;

public class SyslogProps {
	public static final String SYSLOG_CLIENT_CFG_PATH_VM_PROP_NAME = "percentage_apbt.syslogClientCfgPath";
	public static final String SYSLOG_SERVER_CFG_PATH_VM_PROP_NAME = "percentage_apbt.syslogServerCfgPath";
	
	public static final String APPEND_PROP_NAME = "append";
	public static final String SYSOUT_HANDLER_PROP_NAME = "sysoutHandler";
	public static final String OUT_FILE_PROP_NAME = "outFile";
	public static final String TIMEOUT_PROP_NAME = "timeout";
	public static final String HOST_PROP_NAME = "host";
	public static final String PORT_PROP_NAME = "port";
	public static final String FACILITY_PROP_NAME = "facility";
	public static final String PROTOCOL_PROP_NAME = "protocol";
	
	public static final String PROTOCOL_DEFAULT_VALUE = SyslogConstants.TCP;
	public static final String APPEND_DEFAULT_VALUE = "true";
	public static final String TIMEOUT_DEFAULT_VALUE = "0";	
	public static final String SYSOUT_HANDLER_DEFAULT_VALUE = "false";
	
	
	public static interface ClientProps {
		@Config(HOST_PROP_NAME)
		@DefaultNull
		public String getHost();
		
		@Config(PORT_PROP_NAME)
		@DefaultNull
		public Short getPort();
		
		@Config(FACILITY_PROP_NAME)
		@DefaultNull
		public String getFacility();
		
		@Config(PROTOCOL_PROP_NAME)
		@Default(PROTOCOL_DEFAULT_VALUE)
		public String getProtocol();
	}
	
	public static interface ServerProps {
		@Config(HOST_PROP_NAME)
		@DefaultNull
		public String getHost();
		
		@Config(PORT_PROP_NAME)
		@DefaultNull
		public Short getPort();
		
		@Config(PROTOCOL_PROP_NAME)
		@Default(PROTOCOL_DEFAULT_VALUE)
		public String getProtocol();
		
		@Config(APPEND_PROP_NAME)
		@Default(APPEND_DEFAULT_VALUE)
		public boolean isAppend();
		
		@Config(OUT_FILE_PROP_NAME)
		@DefaultNull
		public String getOutFile();
		
		
		@Config(SYSOUT_HANDLER_PROP_NAME)
		@Default(SYSOUT_HANDLER_DEFAULT_VALUE)
		public boolean isSysoutHandler();
		
		
		@Config(TIMEOUT_PROP_NAME)
		@DefaultNull
		public Integer getTimeout();
	}
	
	public static ServerProps serverProps(Properties props) {
		ConfigurationObjectFactory factory = new ConfigurationObjectFactory(props);
		return factory.build(ServerProps.class);
	}
	
	public static ServerProps serverVmProps() {
		return serverProps(tau.cs.wolf.tibet.percentage_apbt.misc.Utils.propsFromVmArg(SYSLOG_SERVER_CFG_PATH_VM_PROP_NAME, true));
		
	}
	
	public static ClientProps clientProps(Properties props) {
		ConfigurationObjectFactory factory = new ConfigurationObjectFactory(props);
		return factory.build(ClientProps.class);
	}
	
	
	
	public static ClientProps clientVmProps() {
		return clientProps(tau.cs.wolf.tibet.percentage_apbt.misc.Utils.propsFromVmArg(SYSLOG_CLIENT_CFG_PATH_VM_PROP_NAME, true));
		
	}
	
	public static void applySyslogClientProps(SyslogConfigIF syslogConfig, ClientProps clientProps) {
		if (clientProps.getHost() != null) {
			syslogConfig.setHost(clientProps.getHost());
		}

		if (clientProps.getPort() != null) {
			syslogConfig.setPort(clientProps.getPort());
		}
		
		syslogConfig.setFacility(clientProps.getFacility());
	}
	
	public static void applySyslogServerProps(SyslogServerConfigIF syslogServerConfig, ServerProps serverProps) throws IOException {
		if (serverProps.getHost() != null) {
			syslogServerConfig.setHost(serverProps.getHost());
		}

		if (serverProps.getPort() != null) {
			syslogServerConfig.setPort(serverProps.getPort());
		}

		if (serverProps.getTimeout() != null) {
			if (syslogServerConfig instanceof TCPNetSyslogServerConfigIF) {
				((TCPNetSyslogServerConfigIF) syslogServerConfig).setTimeout(serverProps.getTimeout());
			} else {
				System.err.println("Timeout not supported for protocol \"" + serverProps.getProtocol() + "\" (ignored)");
			}
		}

		if (serverProps.getOutFile() != null) {
			SyslogServerEventHandlerIF eventHandler = new FileSyslogServerEventHandler(serverProps.getOutFile(), serverProps.isAppend());
			syslogServerConfig.addEventHandler(eventHandler);
		}
		if (serverProps.isSysoutHandler()){
			SyslogServerEventHandlerIF eventHandler = SystemOutSyslogServerEventHandler.create();
			syslogServerConfig.addEventHandler(eventHandler);
		}
	}
}
