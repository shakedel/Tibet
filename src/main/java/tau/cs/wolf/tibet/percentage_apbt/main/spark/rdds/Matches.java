package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import scala.Serializable;
import tau.cs.wolf.tibet.percentage_apbt.data.AppResults;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;

public class Matches implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String p1;
	private final String p2;
	private final AppResults appResults;
		
	public Matches(String p1, String p2, AppResults appResults) {
		this.p1 = p1;
		this.p2 = p2;
		this.appResults = appResults;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Comparison between %s and %s\n", this.p1, this.p2));
		sb.append("APBT MATCHES:\n");
		for (MatchResult match: appResults.getApbtMatches()) {
			sb.append(match);
			sb.append("\n");
		}
		sb.append("UNION MATCHES:\n");
		for (MatchResult match: appResults.getUnitedMatches()) {
			sb.append(match);
			sb.append("\n");
		}
		sb.append("ALIGNED MATCHES:\n");
		for (MatchResult match: appResults.getAlignedMatches()) {
			sb.append(match);
			sb.append("\n");
		}
		return sb.toString();
	}

}
