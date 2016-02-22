package tau.cs.wolf.tibet.percentage_apbt.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FileParserInt extends FileParser<int[]> {

	private final Pattern delimiter;
	
	public FileParserInt(Pattern delimiter) {
		this.delimiter = delimiter;
	}
	
	public FileParserInt() {
		this(Pattern.compile("\\s+"));
	}

	@Override
	public ArrInt parse(File f) {
		List<Integer> res = new ArrayList<Integer>();
		try (Scanner s = new Scanner(f)) {
			s.useDelimiter(this.delimiter);
			while (s.hasNextInt()) {
				res.add(s.nextInt());
			}
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
		return new ArrInt(res);
	}

}
