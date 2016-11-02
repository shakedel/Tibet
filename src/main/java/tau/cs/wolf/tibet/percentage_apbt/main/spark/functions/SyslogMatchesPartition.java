package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.util.Iterator;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;

import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.ApbtMatches;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps.ClientProps;

public class SyslogMatchesPartition implements VoidFunction<Iterator<ApbtMatches>> {
	private static final long serialVersionUID = 1L;
	
	private final Broadcast<? extends ClientProps> bcastClientProps;
	
	public SyslogMatchesPartition(Broadcast<? extends ClientProps> bcastClientProps) {
		this.bcastClientProps = bcastClientProps;
	}

	@Override
	public void call(Iterator<ApbtMatches> t) throws Exception {
		ClientProps clientProps = bcastClientProps.getValue();
		SyslogIF syslogClient = null;
		try {
			syslogClient = Syslog.getInstance(clientProps.getProtocol());
			SyslogProps.applySyslogClientProps(syslogClient.getConfig(), clientProps);
			while (t.hasNext()) {
				ApbtMatches matches = t.next();
				syslogClient.info(matches.toString());
			}
		} catch(Exception e) {
			throw new IllegalStateException("exception thrown: "+e.getMessage()+"\nstack trace:\n"+ExceptionUtils.getStackTrace(e));
			
		} finally {
			// is flush needed?
			if (syslogClient != null) {
				syslogClient.flush();
				syslogClient.shutdown();
			}
		}
	}
	
}
