package tau.cs.wolf.tibet.percentage_apbt.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;



public class SyllableScanner {
	public static void main(String[] args) throws FileNotFoundException {
		File f = new File("src/test/resources/in1.txt");
		new SyllableScanner().scan(f);
	}
	
	Map<String, Integer> sylableCount = new TreeMap<String, Integer>();
	
	public Map<String, Integer> scan(File f) throws FileNotFoundException {
		try (Scanner s = new Scanner(f)) {
			s.useDelimiter(Pattern.compile("([^a-zA-Z'])+"));
			while (s.hasNext()) {
				String sylable = s.next();
				Integer curCount = this.sylableCount.get(sylable);
				if (curCount == null) {
					this.sylableCount.put(sylable, 1);
				} else {
					this.sylableCount.put(sylable, ++curCount);
				}
			}
		}
		return this.sylableCount;
	}
}
