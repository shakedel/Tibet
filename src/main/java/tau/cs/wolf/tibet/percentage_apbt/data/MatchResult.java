package tau.cs.wolf.tibet.percentage_apbt.data;

import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class MatchResult implements Comparable<MatchResult> {

	public Interval workInterval;
	public double score;

	public MatchResult(Interval workInterval, double score) {
		this.workInterval = workInterval;
		this.score = score;
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
		if (workInterval == null) {
			if (other.workInterval != null)
				return false;
		} else if (!workInterval.equals(other.workInterval))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String str = workInterval.getStart().getIndex1() + ","
				+ workInterval.getStart().getIndex2() + ","
				+ workInterval.getEnd().getIndex1() + ","
				+ workInterval.getEnd().getIndex2() + "," + score;
		return str;
	}

	public static class DefaultFormatter
			implements
				Utils.Formatter<MatchResult> {
		@Override
		public String format(MatchResult match) {
			StringBuilder sb = new StringBuilder();
			sb.append(match.workInterval.getStart().getIndex1());
			sb.append(",");
			sb.append(match.workInterval.getStart().getIndex2());
			sb.append(",");
			sb.append(match.workInterval.getEnd().getIndex1());
			sb.append(",");
			sb.append(match.workInterval.getEnd().getIndex2());
			sb.append(",");
			sb.append(match.score);

			return sb.toString();
		}

	}

	@Override
	public int compareTo(MatchResult other) {
		return this.workInterval.compareTo(other.workInterval);
	}
}
