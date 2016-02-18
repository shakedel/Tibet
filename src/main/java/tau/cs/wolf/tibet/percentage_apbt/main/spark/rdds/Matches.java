package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import tau.cs.wolf.tibet.percentage_apbt.data.AppResults;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;

public class Matches extends PairsToMatch {
	private static final long serialVersionUID = 1L;
	
	private final AppResults appResults;
	
	public Matches(FileContent f1, FileContent f2, AppResults appResults) {
		super(f1, f2, null);
		this.appResults = appResults;
	}

	public AppResults getAppResults() {
		return appResults;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("\n Comparison between %s and %s\n", getF1().getFile().getPath(), getF2().getFile().getPath()));
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
