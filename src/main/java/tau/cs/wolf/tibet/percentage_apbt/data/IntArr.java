package tau.cs.wolf.tibet.percentage_apbt.data;

public class IntArr implements Slicable<IntArr>{

	private final int[] arr;
	private final int fromIdx;
	private final int length;
	
	
	public IntArr(int[] arr) {
		this(arr, 0, arr.length);
	}
	
	public IntArr(int[] arr, int fromIdx, int toIdx) {
		this.arr = arr;
		this.fromIdx = fromIdx;
		this.length = toIdx - fromIdx;
	}
	
	@Override
	public int length() {
		return this.length;
	}

	@Override
	public IntArr slice(int start, int end) {
		return new IntArr(this.arr, this.fromIdx+start, this.fromIdx+end);
	}

	@Override
	public boolean compare(int idx, IntArr other, int otherIdx) {
		return this.arr[this.fromIdx+idx] == other.arr[other.fromIdx+otherIdx];
	}
	
	public int[] getArr() {
		return this.arr;
	}
	

}