package tau.cs.wolf.tibet.percentage_apbt.matching;

import org.apache.commons.lang3.tuple.MutablePair;

import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;

class MatchPair extends MutablePair<MatchResult, MatchResult> {
	private static final long serialVersionUID = -6697198967755333700L;
	
	private MatchPair(MatchResult left, MatchResult right) {
		super(left, right);
	}
	
	public static MatchPair of(MatchResult left, MatchResult right) {
		return new MatchPair(left, right);
	}
}