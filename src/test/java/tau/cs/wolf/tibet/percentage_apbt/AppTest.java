package tau.cs.wolf.tibet.percentage_apbt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.AppStage;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.DataType;
import tau.cs.wolf.tibet.percentage_apbt.main.AppMain;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;

/**
 * Unit test for simple App.
 */
@RunWith(Parameterized.class)
public class AppTest extends TestCase {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	protected final File expectedOutFile;
	protected final Props props;
	protected final Args args;
	
	public AppTest(String in1ResourcePath, String in2ResourcePath, String outResourcePath, AppStage appStage, DataType dataType, String cfgResourcePath) throws IOException {
		this.expectedOutFile = Utils.urlToFile(getClass().getResource(outResourcePath));
		
		File inFile1 = Utils.urlToFile(getClass().getResource(in1ResourcePath));
		File inFile2 = Utils.urlToFile(getClass().getResource(in2ResourcePath));
		
		try (InputStream is = getClass().getResourceAsStream(cfgResourcePath)) {
			this.props = PropsBuilder.newProps(is);
		}
		
		this.args = new Args(inFile1, inFile2, null, appStage, dataType);
	}

	@Parameters(name = "index: {index}: compare in1: {0}, in2: {1}, out:{2}, AppStage: {3}, minLength: {4}, maxError: {5}")
	public static Iterable<Object[]> data1() {
		return Arrays.asList(new Object[][] { 
			{ "/char/in1.txt", "/char/in2.txt", "/char/out.apbt.txt", AppStage.APBT, DataType.CHAR, "/char/cfg.txt" }, 
			{ "/char/in1.txt", "/char/in2.txt", "/char/out.union.txt", AppStage.UNION, DataType.CHAR, "/char/cfg.txt" }, 
			{ "/char/in1.txt", "/char/in2.txt", "/char/out.txt", AppStage.ALIGNMENT, DataType.CHAR, "/char/cfg.txt" }, 
		});
	}
	
	@Test
	public void compareResults() throws IOException {
		logger.info("Testing with parameters: ");
		File tempFile = null;
		try {
			tempFile = File.createTempFile("apbt", null);
			this.args.setOutFile(tempFile);
			new AppMain(args, props, true).run();
			FileAssert.assertEquals(this.expectedOutFile, tempFile);
		} catch (CmdLineException e) {
			throw new IOException(e);
		} finally {
			if (tempFile != null) {
				tempFile.deleteOnExit();
			}
		}
	}
	
}
