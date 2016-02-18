package tau.cs.wolf.tibet.percentage_apbt.data;

import java.util.List;

public class AppResults {
	
	private final List<MatchResult> apbtMatches;
	private final List<MatchResult> unitedMatches;
	private final List<MatchResult> alignedMatches;
	
	public AppResults(List<MatchResult> apbtMatches, List<MatchResult> unitedMatches, List<MatchResult> alignedMatches) {
		super();
		this.apbtMatches = apbtMatches;
		this.unitedMatches = unitedMatches;
		this.alignedMatches = alignedMatches;
	}
	
	public AppResults(List<MatchResult> apbtMatches) {
		this(apbtMatches, null, null);
	}
	
	
	public List<MatchResult> getApbtMatches() {
		return apbtMatches;
	}
	
	public List<MatchResult> getUnitedMatches() {
		return unitedMatches;
	}
	
	public List<MatchResult> getAlignedMatches() {
		return alignedMatches;
	}
	
}
