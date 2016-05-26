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

public class LogHead implements VoidFunction<Iterator<Tuple2<Integer, Integer>>> {
	private static final long serialVersionUID = 1L;
	
	private final Broadcast<List<SlicableEntry>> bcastEntries;
	private final Broadcast<? extends ClientProps> bcastClientProps;
	
	public LogHead(Broadcast<? extends ArgsCommon> bcastArgs, Broadcast<? extends Props> bcastProps,
			Broadcast<? extends ClientProps> bcastClientProps, Broadcast<List<SlicableEntry>> bcastEntries) {
		this.bcastEntries = bcastEntries;
		this.bcastClientProps = bcastClientProps;
	}

	
	@Override
	public void call(Iterator<Tuple2<Integer, Integer>> t) throws Exception {
		int n = 5;
		ClientProps clientProps = bcastClientProps.getValue();
		SyslogIF syslogClient = null;
		try {
			syslogClient = Syslog.getInstance(clientProps.getProtocol());
			SyslogProps.applySyslogClientProps(syslogClient.getConfig(), clientProps);
			while (t.hasNext()) {
				Tuple2<Integer, Integer> indexPair = t.next();
				SlicableEntry entry1 = bcastEntries.getValue().get(indexPair._1);
				SlicableEntry entry2 = bcastEntries.getValue().get(indexPair._2);
				syslogClient.info(String.format("(%s, %s), %s, %s", entry1.getKey(), entry2.getKey(), entry1.getValue().getStringHead(n), entry2.getValue().getStringHead(n)));
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
