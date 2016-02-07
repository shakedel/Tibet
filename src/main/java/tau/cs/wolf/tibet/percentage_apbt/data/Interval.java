package tau.cs.wolf.tibet.percentage_apbt.data;

import general.IndexPair;

public class Interval extends general.Interval {
	
	private static final long serialVersionUID = -2295015314042029972L;
	
	private final IndexPair span1;
	private final IndexPair span2;
	
	public static Interval newIntervalBySpans(IndexPair spanA, IndexPair spanB) {
		return new Interval(spanA, spanB, null);
	}
	
	
	public Interval(IndexPair start, IndexPair end) {
		super(start, end);
		this.span1 = new IndexPair(start.getIndex1(), end.getIndex1());
		this.span2 = new IndexPair(start.getIndex2(), end.getIndex2());
	}
	
	private Interval(IndexPair span1, IndexPair span2, Object dummy) {
		super(new IndexPair(span1.getIndex1(), span2.getIndex1()), new IndexPair(span1.getIndex2(), span2.getIndex2()));
		this.span1 = span1;
		this.span2 = span2;
	}
	
	public Interval(general.Interval interval) {
		this(interval.getStart(), interval.getEnd());
	}
	
	public IndexPair getSpan1() {
		return this.span1;
	}
	
	public IndexPair getSpan2() {
		return this.span2;
	}


}
