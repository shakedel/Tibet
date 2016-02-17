package tau.cs.wolf.tibet.percentage_apbt.data;

public class CharArr implements Slicable<CharArr>{

	private final char[] arr;
	private final int fromIdx;
	private final int length;
	
	
	public CharArr(char[] arr) {
		this(arr, 0, arr.length);
	}
	
	public CharArr(char[] arr, int fromIdx, int toIdx) {
		this.arr = arr;
		this.fromIdx = fromIdx;
		this.length = toIdx - fromIdx;
	}
	
	@Override
	public int length() {
		return this.length;
	}

	@Override
	public CharArr slice(int start, int end) {
		return new CharArr(this.arr, this.fromIdx+start, this.fromIdx+end);
	}

	@Override
	public boolean compare(int idx, CharArr other, int otherIdx) {
		return this.arr[this.fromIdx+idx] == other.arr[other.fromIdx+otherIdx];
	}
	
	public char[] getArr() {
		return this.arr;
	}
	

}
