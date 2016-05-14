package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.util.Iterator;

import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;

import tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds.Matches;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps.ClientProps;

public class SyslogMatchesPartition implements VoidFunction<Iterator<Matches>> {
	private static final long serialVersionUID = 1L;
	
	private final Broadcast<? extends ClientProps> bcastClientProps;
	
	public SyslogMatchesPartition(Broadcast<? extends ClientProps> bcastClientProps) {
		this.bcastClientProps = bcastClientProps;
	}

	@Override
	public void call(Iterator<Matches> t) throws Exception {
		ClientProps clientProps = bcastClientProps.getValue();
		SyslogIF syslogClient = Syslog.getInstance(clientProps.getProtocol());
		SyslogProps.applySyslogClientProps(syslogClient.getConfig(), clientProps);
		try {
			while (t.hasNext()) {
				Matches matches = t.next();
				syslogClient.info(matches.toString());
			}
		} finally {
			// is flush needed?
			syslogClient.flush();
			syslogClient.shutdown();
		}
	}
	
}
