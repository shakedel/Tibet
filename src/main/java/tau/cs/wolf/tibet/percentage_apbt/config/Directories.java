package tau.cs.wolf.tibet.percentage_apbt.config;

public class Directories {
	public enum Test_Type{stem, syllable, mish_tos}
	
	private static final Test_Type testType = Test_Type.stem;
	/**
	 * this directory should contain the files: tf.txt1, tf.txt2, tf.txt3
	 */
	public static final String TF_DIR;
	
	public static final String TF_MAIN_DIR = System.getProperty("tfDir") + "\\";
	/*
	 * this directory should contains the files:
	 * syllables.txt - mapping from syllable to int representation
	 * stems.txt - mapping from stem in in representation (may be identical to syllables.txt if there is no stemming"
	 * SylToStem.txt - mapping between a syllable and its stem (int to int)
	 */
	public static final String STEM_DIR;
	/**
	 * currently not in use
	 */
	//public static final String CLEAN_TEXT_DIR = "C:/data/Kangyur_Tenjur-CLEAN-2016-05-14";

	static
	{
		switch (testType){
		case stem:
			TF_DIR = TF_MAIN_DIR + "TFIDF";
			STEM_DIR = TF_MAIN_DIR + "SyllableObject-2016-05-14/";
			break ;
		case syllable:
			TF_DIR = TF_MAIN_DIR + "TFIDF_SyllableOnly";
			STEM_DIR = TF_MAIN_DIR + "SyllableOnly/";
			break;
		case mish_tos:
			TF_DIR = TF_MAIN_DIR + "Mish_Tos/";
			STEM_DIR = TF_MAIN_DIR + "Mish_Tos/";
			break;
		default:
			TF_DIR = null;
			STEM_DIR = null;
		}
	}
}