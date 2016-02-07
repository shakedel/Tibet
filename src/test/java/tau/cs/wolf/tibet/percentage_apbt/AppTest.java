package tau.cs.wolf.tibet.percentage_apbt;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;
import junitx.framework.FileAssert;
import tau.cs.wolf.tibet.percentage_apbt.main.AppFactory;
import tau.cs.wolf.tibet.percentage_apbt.main.AppFactory.AppType;
import tau.cs.wolf.tibet.percentage_apbt.main.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

/**
 * Unit test for simple App.
 */
@RunWith(Parameterized.class)
public class AppTest extends TestCase {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final AppType type;
	private final File outFile;
	private final Args args;
	
	public AppTest(String in1ResourcePath, String in2ResourcePath, String outResourcePath, AppType type, int minLength, int maxError) {
		this.type = type;
		this.outFile = Utils.urlToFile(getClass().getResource(outResourcePath));
		
		File inFile1 = Utils.urlToFile(getClass().getResource(in1ResourcePath));
		File inFile2 = Utils.urlToFile(getClass().getResource(in2ResourcePath));
		
		this.args = new Args(inFile1, inFile2, null, minLength, maxError);
	}

	@Parameters(name = "index: {index}: compare in1: {0}, in2: {1}, out:{2}, Type: {3}, minLength: {4}, maxError: {5}")
	public static Iterable<Object[]> data1() {
		return Arrays.asList(new Object[][] { 
			{ "/in1.txt", "/in2.txt", "/absolute_out.txt", AppType.ABSOLUTE, 20, 3 }, 
			{ "/in1.txt", "/in2.txt", "/absolute_out.txt", AppType.CHUNKS, 20, 3 }, 
		});
	}
	
	@Test
	public void compareResults() throws IOException {
		logger.info("Testing with parameters: ");
		File tempFile = null;
		try {
			tempFile = File.createTempFile("apbt", null);
			this.args.setOutFile(tempFile);
			AppFactory.getMain(type, args).run();;
			FileAssert.assertEquals(this.outFile, tempFile);
		} catch (CmdLineException e) {
			throw new IOException(e);
		} finally {
			if (tempFile != null) {
				tempFile.deleteOnExit();
			}
		}
	}
	
}
