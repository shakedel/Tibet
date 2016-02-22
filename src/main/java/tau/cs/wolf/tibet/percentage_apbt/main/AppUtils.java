package tau.cs.wolf.tibet.percentage_apbt.main;

import tau.cs.wolf.tibet.percentage_apbt.data.ArrChar;
import tau.cs.wolf.tibet.percentage_apbt.data.ArrInt;
import tau.cs.wolf.tibet.percentage_apbt.data.FileParser;
import tau.cs.wolf.tibet.percentage_apbt.data.FileParserChar;
import tau.cs.wolf.tibet.percentage_apbt.data.FileParserInt;
import tau.cs.wolf.tibet.percentage_apbt.data.Slicable;

public class AppUtils {

	@SuppressWarnings("unchecked")
	public static <R> FileParser<? extends R> getFileParser(DataType dataType) {
		switch (dataType) {
			case INT:
				return (FileParser<? extends R>) new FileParserInt();
			case CHAR:
				return (FileParser<? extends R>) new FileParserChar();
			default:
				throw new IllegalArgumentException("Unknown value: "+dataType);
		}
	}
	
	public static enum AppStage {
		APBT, UNION, ALIGNMENT
	}

	public static enum DataType {
		CHAR(new AppClasses<char[]>(ArrChar.class, FileParserChar.class)), 
		INT(new AppClasses<int[]>(ArrInt.class, FileParserInt.class));

		private final AppClasses<?> appClasses;

		private DataType(AppClasses<?> appClasses) {
			this.appClasses = appClasses;
		}

		@SuppressWarnings("unchecked")
		public <R> AppClasses<R> getAppClasses() {
			return (AppClasses<R>) appClasses;
		}

	}

	public static class AppClasses<R> {
		private final Class<? extends Slicable<R>> dataClass;
		private final Class<? extends FileParser<R>> fileParserClass;

		public AppClasses(
				Class<? extends Slicable<R>> dataClass,
				Class<? extends FileParser<R>> fileParserClass) {
			this.dataClass = dataClass;
			this.fileParserClass = fileParserClass;
		}

		public Class<? extends Slicable<?>> getDataClass() {
			return dataClass;
		}

		public FileParser<R> newFileParser() {
			try {
				return (FileParser<R>) fileParserClass.newInstance();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
