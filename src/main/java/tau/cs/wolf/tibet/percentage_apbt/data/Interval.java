package tau.cs.wolf.tibet.percentage_apbt.data;

public class Interval implements java.io.Serializable, Comparable<Interval> {

	private static final long serialVersionUID = -3230705851675333275L;

	public static Interval newIntervalBySpans(IndexPair spanA, IndexPair spanB) {
		IndexPair first = new IndexPair(spanA.getIndex1(), spanB.getIndex1());
		IndexPair last = new IndexPair(spanA.getIndex2(), spanB.getIndex2());
		return new Interval(first, last, spanA, spanB);
	}

	public static Interval newIntervalByStartEnd(IndexPair start, IndexPair end) {
		IndexPair spanA = new IndexPair(start.getIndex1(), end.getIndex1());
		IndexPair spanB = new IndexPair(start.getIndex2(), end.getIndex2());
		return new Interval(start, end, spanA, spanB);

	}

	private final IndexPair first;
	private final IndexPair last;
	private final IndexPair span1;
	private final IndexPair span2;

	private Interval(IndexPair start, IndexPair end, IndexPair span1, IndexPair span2) {
		this.span1 = span1;
		this.span2 = span2;
		this.first = start;
		this.last = end;
	}

	public IndexPair getStart() {
		return first;
	}

	public IndexPair getEnd() {
		return last;
	}

	public IndexPair getSpan1() {
		return this.span1;
	}

	public IndexPair getSpan2() {
		return this.span2;
	}
	
	
	public void shiftSpans(int span1Shift, int span2Shift) {
		this.shiftSpan1(span1Shift);
		this.shiftSpan2(span2Shift);
	}
	
	public void shiftSpan1(int shiftVal) {
		if (shiftVal != 0) {
			this.span1.shift(shiftVal);
			this.first.incrementIndex1(shiftVal);
			this.last.incrementIndex1(shiftVal);
		}
	}
	
	public void shiftSpan2(int shiftVal) {
		if (shiftVal != 0) {
			this.span2.shift(shiftVal);
			this.first.incrementIndex2(shiftVal);
			this.last.incrementIndex2(shiftVal);
		}
	}

	@Override
	public String toString() {
		return first + " - " + last;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interval other = (Interval) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (last == null) {
			if (other.last != null)
				return false;
		} else if (!last.equals(other.last))
			return false;
		return true;
	}

	@Override
	public int compareTo(Interval other) {
		int firstComparison = this.first.compareTo(other.first);
		if (firstComparison != 0) {
			return firstComparison;
		}
		return this.last.compareTo(other.last);
	}

}
