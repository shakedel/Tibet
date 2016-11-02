package tau.cs.wolf.tibet.percentage_apbt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;
import junitx.framework.ListAssert;
import tau.cs.wolf.tibet.percentage_apbt.data.AppResults;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.main.AppMain;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.AppStage;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.DataType;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsMain;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils;
import tau.cs.wolf.tibet.percentage_apbt.misc.Utils.Parser;

/**
 * Unit test for simple App.
 */
@RunWith(Parameterized.class)
public class AppTest extends TestCase {
	
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(getClass());

	protected final List<MatchResult> expectedApbtResults;
	protected final List<MatchResult> expectedUnionResults;
	protected final List<MatchResult> expectedAlignResults;
	
	protected final Props props;
	protected final ArgsMain args;
	
	public AppTest(String in1ResourcePath, String in2ResourcePath, String apbtResourcePath, String unionResourcePath, String alignResourcePath, AppStage appStage, DataType dataType, String cfgResourcePath) throws IOException {
		this.expectedApbtResults = apbtResourcePath==null ? null : parseMatchesFile(Utils.urlToFile(getClass().getResource(apbtResourcePath)));
		this.expectedUnionResults = unionResourcePath==null ? null : parseMatchesFile(Utils.urlToFile(getClass().getResource(unionResourcePath)));
		this.expectedAlignResults = alignResourcePath==null ? null : parseMatchesFile(Utils.urlToFile(getClass().getResource(alignResourcePath)));
		
		File inFile1 = Utils.urlToFile(getClass().getResource(in1ResourcePath));
		File inFile2 = Utils.urlToFile(getClass().getResource(in2ResourcePath));
		
		try (InputStream is = getClass().getResourceAsStream(cfgResourcePath)) {
			this.props = PropsBuilder.newProps(is);
		}
		
		this.args = new ArgsMain(inFile1, inFile2, null, appStage, dataType, true);
	}

	private List<MatchResult> parseMatchesFile(File f) throws IOException {
		Parser<MatchResult> parser = new  MatchResult.DefaultParser();
		List<String> lines = FileUtils.readLines(f);
		List<MatchResult> res = new ArrayList<MatchResult>(lines.size());
		for (String line: lines) {
			if (line.trim().isEmpty()) {
				continue;
			}
			res.add(parser.parse(line));
		}
		return res;
	}

	@Parameters(name = "index: {index}: compare in1: {0}, in2: {1}, out.apbt:{2}, out.union:{3}, out.align:{4}, AppStage: {5}, minLength: {6}, maxError: {7}")
	public static Iterable<Object[]> data1() {
		return Arrays.asList(new Object[][] { 
			{ "/char/in1.txt", "/char/in2.txt", "/char/out.apbt.txt", "/char/out.union.txt", "/char/out.align.txt", AppStage.ALIGNMENT, DataType.CHAR, "/char/cfg.txt" }, 
			{ "/int/in1.txt", "/int/in2.txt", "/int/out.apbt.txt", "/int/out.union.txt", "/int/out.align.txt", AppStage.ALIGNMENT, DataType.INT, "/int/cfg.txt" }, 
		});
	}
	
	
	@Test
	public void compareResults() {
		AppMain app = new AppMain(args, props);
		app.run();
		AppResults results = app.getResults();
		ListAssert.assertEquals(expectedApbtResults, results.getApbtMatches());
		ListAssert.assertEquals(expectedUnionResults, results.getUnitedMatches());
		ListAssert.assertEquals(expectedAlignResults, results.getAlignedMatches());
	}

	
}
