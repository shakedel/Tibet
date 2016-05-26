package tau.cs.wolf.tibet.percentage_apbt.main.spark.functions;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;

import scala.Tuple2;
import tau.cs.wolf.tibet.percentage_apbt.data.slicable.SlicableEntry;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsCommon;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps;
import tau.cs.wolf.tibet.percentage_apbt.misc.SyslogProps.ClientProps;

public class LogIndices implements VoidFunction<Iterator<Tuple2<Integer, Integer>>> {
	private static final long serialVersionUID = 1L;
	
	private final Broadcast<? extends ClientProps> bcastClientProps;
	
	public LogIndices(Broadcast<? extends ArgsCommon> bcastArgs, Broadcast<? extends Props> bcastProps,
			Broadcast<? extends ClientProps> bcastClientProps, Broadcast<List<SlicableEntry>> bcastEntries) {
		this.bcastClientProps = bcastClientProps;
	}

	
	@Override
	public void call(Iterator<Tuple2<Integer, Integer>> t) throws Exception {
		ClientProps clientProps = bcastClientProps.getValue();
		SyslogIF syslogClient = null;
		try {
			syslogClient = Syslog.getInstance(clientProps.getProtocol());
			SyslogProps.applySyslogClientProps(syslogClient.getConfig(), clientProps);
			while (t.hasNext()) {
				Tuple2<Integer, Integer> pair = t.next();
				syslogClient.info(String.format("(%s, %s)", pair._1, pair._2));
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
