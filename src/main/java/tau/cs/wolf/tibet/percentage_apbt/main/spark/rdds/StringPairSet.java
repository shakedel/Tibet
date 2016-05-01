package tau.cs.wolf.tibet.percentage_apbt.main.spark.rdds;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class StringPairSet implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Map<String, Set<String>> map = new TreeMap<String, Set<String>>();
	
	public boolean add(String a, String b) {
		Set<String> set = map.get(a);
		if (set == null) {
			set = new TreeSet<String>();
			map.put(a, set);
		}
		return set.add(a);
	}
	
	public boolean contains(String a, String b) {
		Set<String> set = map.get(a);
		if (set == null) {
			return false;
		}
		return set.contains(b);
	}
	
}
