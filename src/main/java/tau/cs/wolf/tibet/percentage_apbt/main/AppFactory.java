package tau.cs.wolf.tibet.percentage_apbt.main;

import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public class AppFactory {
	
	public static BaseApp getMain(AppType type, Args args, Props props, boolean writeResults) {
		switch(type) {
		case ABSOLUTE: return new AppAbsolute(args, props, writeResults);
		case CHUNKS: return new AppChunks(args, props, writeResults);
		case PERCENTAGE: return new AppPercentage(args, props, writeResults);
		default: throw new IllegalArgumentException("unknown type: "+type);
		}
	}
	
	public static enum AppType {
		CHUNKS, ABSOLUTE, PERCENTAGE
	}
}
