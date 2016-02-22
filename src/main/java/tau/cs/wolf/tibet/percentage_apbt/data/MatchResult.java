package tau.cs.wolf.tibet.percentage_apbt.data;

import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class MatchResult implements Comparable<MatchResult> {

	private final Interval interval;
	private final double score;

	public MatchResult(Interval workInterval, double score) {
		this.interval = workInterval;
		this.score = score;
	}
	
	public MatchResult(MatchResult match) {
		this.interval = new Interval(match.getInterval());
		this.score = match.getScore();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchResult other = (MatchResult) obj;
		if (getInterval() == null) {
			if (other.getInterval() != null)
				return false;
		} else if (!getInterval().equals(other.getInterval()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String str = getInterval().getStart().getIndex1() + ","
				+ getInterval().getStart().getIndex2() + ","
				+ getInterval().getEnd().getIndex1() + ","
				+ getInterval().getEnd().getIndex2() + "," + getScore();
		return str;
	}

	public static class DefaultFormatter
			implements
				Utils.Formatter<MatchResult> {
		@Override
		public String format(MatchResult match) {
			StringBuilder sb = new StringBuilder();
			sb.append(match.getInterval().getStart().getIndex1());
			sb.append(",");
			sb.append(match.getInterval().getStart().getIndex2());
			sb.append(",");
			sb.append(match.getInterval().getEnd().getIndex1());
			sb.append(",");
			sb.append(match.getInterval().getEnd().getIndex2());
			sb.append(",");
			sb.append(match.getScore());

			return sb.toString();
		}

	}

	@Override
	public int compareTo(MatchResult other) {
		return this.getInterval().compareTo(other.getInterval());
	}

	public Interval getInterval() {
		return interval;
	}

	public double getScore() {
		return score;
	}

}
