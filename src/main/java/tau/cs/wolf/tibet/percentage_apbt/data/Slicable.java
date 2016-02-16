package tau.cs.wolf.tibet.percentage_apbt.data;

public interface Slicable<R> {
	public int length();
	public R slice(int start, int end);
	public boolean compare(int idx, R other, int otherIdx);

}
