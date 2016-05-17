package tau.cs.wolf.tibet.percentage_apbt.ranking.utils.histogram;

public class IllegalKValue extends Exception {
	public IllegalKValue(int k){
		super("Illegal k value: " + k);
	}
}
