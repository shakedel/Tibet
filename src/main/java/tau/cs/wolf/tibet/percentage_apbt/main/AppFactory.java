package tau.cs.wolf.tibet.percentage_apbt.main;

public class AppFactory {
	
	public static BaseApp getMain(AppType type, Args args) {
		switch(type) {
		case ABSOLUTE: return new AppAbsolute(args);
		case CHUNKS: return new AppChunks(args);
		default: throw new IllegalArgumentException("unknown type: "+type);
		}
	}
	
	public static enum AppType {
		CHUNKS, ABSOLUTE
	}
}
