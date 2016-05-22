package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Temp {
	public static void main(String[] args) throws IOException, URISyntaxException {
		URI hdfsURI = new URI(args[0]);
		Path hdfsPath = new Path(args[1]);
		
		FileSystem fs = FileSystem.get(hdfsURI, new Configuration());
		try (FSDataInputStream is = fs.open(hdfsPath)) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			System.out.println(br.readLine());
		}
	}
}
