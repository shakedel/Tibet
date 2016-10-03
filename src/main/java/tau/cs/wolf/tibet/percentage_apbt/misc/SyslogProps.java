package tau.cs.wolf.tibet.percentage_apbt.misc;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.Properties;

import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.event.printstream.FileSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.event.printstream.SystemOutSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfigIF;

public class SyslogProps {
	
	public static final String SYSLOG_CLIENT_CFG_PATH_VM_PROP_NAME = "percentage_apbt.syslogClientCfgPath";
	public static final String SYSLOG_SERVER_CFG_PATH_VM_PROP_NAME = "percentage_apbt.syslogServerCfgPath";
	
	public static final String APPEND_PROP_NAME = "append";
	public static final String SYSOUT_HANDLER_PROP_NAME = "sysoutHandler";
	public static final String OUT_DIR_PROP_NAME = "outDir";
	public static final String TIMEOUT_PROP_NAME = "timeout";
	public static final String HOST_PROP_NAME = "host";
	public static final String PORT_PROP_NAME = "port";
	public static final String FACILITY_PROP_NAME = "facility";
	public static final String PROTOCOL_PROP_NAME = "protocol";
	public static final String MAX_MSG_LEN_PROP_NAME = "maxMsgLen";// in bytes
	
	public static final boolean APPEND_DEFAULT_VALUE = true;
	public static final boolean SYSOUT_HANDLER_DEFAULT_VALUE = false;
	public static final File OUT_DIR_DEFAULT_VALUE = new File(".");	
	public static final int TIMEOUT_DEFAULT_VALUE = 0;	
	public static final String FACILITY_DEFAULT_VALUE = null;
	public static final String PROTOCOL_DEFAULT_VALUE = SyslogConstants.TCP;
	public static final int MAX_MSG_LEN_DEFAULT_VALUE = Utils.toBytes("1M");
	public static final boolean DAEMON_DEFAULT_VALUE = false;
	
	public static interface ClientProps extends Serializable {
		public String getHost();
		public Short getPort();
		public String getFacility();
		public String getProtocol();
		public Integer getMaxMsgLen();
	}
	
	public static interface ServerProps extends Serializable {
		public String getHost();
		public Short getPort();
		public String getProtocol();
		public boolean isAppend();
		public File getOutDir();
		public boolean isSysoutHandler();
		public Integer getTimeout();
		public Boolean isDaemon();
	}
	
	private static class ClientPropsImpl implements ClientProps {
		private static final long serialVersionUID = 1L;
		
		private String host;
		private Short port;
		private String facility = FACILITY_DEFAULT_VALUE;
		private String protocol = PROTOCOL_DEFAULT_VALUE;
		private Integer maxMsgLen = MAX_MSG_LEN_DEFAULT_VALUE;
			
		public ClientPropsImpl(Properties props) {
			this.host = props.getProperty(HOST_PROP_NAME);
			this.port = Short.parseShort(props.getProperty(PORT_PROP_NAME));
			{
				String val = props.getProperty(FACILITY_PROP_NAME);
				if (val != null) {
					this.facility = val;
				}
			}
			{
				String val = props.getProperty(PROTOCOL_PROP_NAME);
				if (val != null) {
					this.protocol = val;
				}
			}
			{
				String val = props.getProperty(MAX_MSG_LEN_PROP_NAME);
				if (val != null) {
					this.maxMsgLen = Integer.parseInt(val);
				}
			}
		}
		
		@Override public String getHost() { return this.host; }
		@Override public Short getPort() { return this.port; }
		@Override public String getFacility() { return this.facility; }
		@Override public String getProtocol() { return this.protocol; }
		@Override public Integer getMaxMsgLen() { return this.maxMsgLen; }
		
	}
	
	private static class ServerPropsImpl implements ServerProps {
		private static final long serialVersionUID = 1L;
		
		private String host;
		private Short port;
		private String protocol = PROTOCOL_DEFAULT_VALUE;
		private boolean append = APPEND_DEFAULT_VALUE;
		private boolean sysoutHandler = SYSOUT_HANDLER_DEFAULT_VALUE;
		private File outDir;
		private int timeout = TIMEOUT_DEFAULT_VALUE;
		private boolean daemon = DAEMON_DEFAULT_VALUE;
		
		public ServerPropsImpl(Properties props) {
			this.host = props.getProperty(HOST_PROP_NAME);
			this.port = Short.parseShort(props.getProperty(PORT_PROP_NAME));
			
			{
				String val = props.getProperty(OUT_DIR_PROP_NAME);
				if (val != null) {
					this.outDir = new File(props.getProperty(OUT_DIR_PROP_NAME));
					if (!this.outDir.isDirectory()) {
						throw new IllegalArgumentException("specified output directory does not exist: "+outDir.getPath());
					}
				}
			}
			{
				String val = props.getProperty(PROTOCOL_PROP_NAME);
				if (val != null) {
					this.protocol = val;
				}
			}
			{
				String val = props.getProperty(APPEND_PROP_NAME);
				if (val != null) {
					this.append = Boolean.parseBoolean(val);
				}
			}
			{
				String val = props.getProperty(SYSOUT_HANDLER_PROP_NAME);
				if (val != null) {
					this.sysoutHandler = Boolean.parseBoolean(val);
				}
			}
			{
				String val = props.getProperty(TIMEOUT_PROP_NAME);
				if (val != null) {
					this.timeout = Integer.parseInt(val);
				}
			}
		}
		
		@Override public String getHost() { return this.host; }
		@Override public Short getPort() { return this.port; }
		@Override public String getProtocol() { return this.protocol; }
		@Override public boolean isAppend() { return this.append; }
		@Override public File getOutDir() { return this.outDir; }
		@Override public boolean isSysoutHandler() { return this.sysoutHandler; }
		@Override public Integer getTimeout() { return this.timeout; }
		@Override public Boolean isDaemon() { return this.daemon; }

		@Override
		public String toString() {
			return "ServerPropsImpl [host=" + host + ", port=" + port + ", protocol=" + protocol + ", append=" + append
					+ ", sysoutHandler=" + sysoutHandler + ", outFile=" + outDir + ", timeout=" + timeout + "]";
		}
	}
	
	public static ServerProps serverProps(Properties props) {
		return new ServerPropsImpl(props);
	}
	
	public static ServerProps serverProps(File file) throws IOException {
		Properties props = new Properties();
		props.load(new FileReader(file));
		return SyslogProps.serverProps(props);
	}
	
	public static ServerProps serverVmProps() {
		return serverProps(tau.cs.wolf.tibet.percentage_apbt.misc.Utils.propsFromVmArg(SYSLOG_SERVER_CFG_PATH_VM_PROP_NAME, true));
		
	}
	
	public static ClientProps clientProps(Properties props) {
		return new ClientPropsImpl(props);
	}
	
	public static ClientProps clientProps(File file) throws IOException {
		Properties props = new Properties();
		props.load(new FileReader(file));
		return SyslogProps.clientProps(props);
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
		
		if (clientProps.getMaxMsgLen() != null) {
			syslogConfig.setMaxMessageLength(clientProps.getMaxMsgLen());
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

		{
			String pid = ManagementFactory.getRuntimeMXBean().getName();
			File outFile = new File(serverProps.getOutDir(), pid+".syslog");
			SyslogServerEventHandlerIF eventHandler = new FileSyslogServerEventHandler(outFile.getPath(), serverProps.isAppend());
			syslogServerConfig.addEventHandler(eventHandler);
		}
			
		if (serverProps.isSysoutHandler()){
			SyslogServerEventHandlerIF eventHandler = SystemOutSyslogServerEventHandler.create();
			syslogServerConfig.addEventHandler(eventHandler);
		}
		
		if (serverProps.isDaemon() != null) {
			syslogServerConfig.setUseDaemonThread(serverProps.isDaemon());
		}
		
	}
}
