package tau.cs.wolf.tibet.percentage_apbt.main.args;

import java.io.File;

import org.joda.time.Duration;
import org.kohsuke.args4j.CmdLineException;

import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public final class ArgsUtils {

	public static void assertIsFraction(float f, String optionName) throws CmdLineException {
		if (f<0.0 || f>1.0) {
			throwCmdLineException(optionName, "must be a fraction");
		}

	}

	public static void assertFileExists(File f, String optionName) throws CmdLineException {
		if (!f.isFile()) {
			throwCmdLineException(optionName, "file does not exist: "+f.getPath());
		}
	}

	public static void assertIsNonNegative(int num, String optionName) throws CmdLineException {
		if (num < 0) {
			throwCmdLineException(optionName, "must be a non-negative integer");
		}
	}

	@SuppressWarnings("deprecation")
	private static void throwCmdLineException(String optionName, String msg) throws CmdLineException {
		throw new CmdLineException("Option "+optionName+": "+msg);
	}

	public static void overrideArgsWithProps(Args args, Props props) {
		try {
			if (args.getMinLength() == null) {
				args.setMinlength(props.getMinLength());
			}
			if (args.getMaxError() == null) {
				args.setMaxError(props.getMaxError());
			}
			if (args.getTimeout() == null) {
				Duration t = props.getTimeout();
				args.setTimeout(t);
			}
			if (args.getMinDistanceUnion() == null) {
				args.setMinDistanceUnion(props.getMinDistanceUnion());
			}
			if (args.getLocalAlignPadRatio() == null) {
				args.setLocalAlignPadRatio(props.getLocalAlignPadRatio());
			}
			if (args instanceof ArgsMonitored) {
				ArgsMonitored argsMonitored = (ArgsMonitored) args;
				if (argsMonitored.getPollDuration() == null) {
					argsMonitored.setPollDuration(props.getPollDuration());
				}
			}
			
		} catch (CmdLineException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
