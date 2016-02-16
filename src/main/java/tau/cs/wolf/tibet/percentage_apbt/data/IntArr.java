package tau.cs.wolf.tibet.percentage_apbt.data;

import java.util.Arrays;

public class IntArr implements Slicable<IntArr>{

	private final int[] arr;
	
	public IntArr(int[] arr) {
		this.arr = arr;
	}
	
	@Override
	public int length() {
		return this.arr.length;
	}

	@Override
	public IntArr slice(int start, int end) {
		Arrays.copyOfRange(this.arr, start, end);
		return new IntArr(arr);
	}

	@Override
	public boolean compare(int idx, IntArr other, int otherIdx) {
		return this.arr[idx] == other.arr[otherIdx];
	}

}
