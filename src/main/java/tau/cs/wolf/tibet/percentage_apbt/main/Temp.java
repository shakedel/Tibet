package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import tau.cs.wolf.tibet.percentage_apbt.misc.FsUtils;
import tau.cs.wolf.tibet.percentage_apbt.misc.FsUtils.InputStreamIterator;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

public class Temp {

	public static void main(String[] args) throws IOException, URISyntaxException {
		URI uri = new URI(args[0]);
		try (InputStreamIterator iter = FsUtils.getInputStreamIterator(uri, new Utils.PatternFileFilter(Pattern.compile("in.*")))) {
			while (iter.hasNext()) {
				Entry<String, InputStream> entry = iter.next();
				try (InputStream is = entry.getValue()){
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					System.out.println();
					System.out.println(entry.getKey());
					System.out.println(br.readLine());
					System.out.println();
				}
			}
		}

	}

}
