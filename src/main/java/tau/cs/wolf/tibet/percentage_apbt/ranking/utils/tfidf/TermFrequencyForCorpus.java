package tau.cs.wolf.tibet.percentage_apbt.ranking.utils.tfidf;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tau.cs.wolf.tibet.percentage_apbt.ranking.utils.histogram.HashMapHistogram;
import tau.cs.wolf.tibet.percentage_apbt.ranking.utils.histogram.IHistogram;
import tau.cs.wolf.tibet.percentage_apbt.ranking.utils.stemming.StemUtils;

public class TermFrequencyForCorpus {
	private static final String mainDir = "C:\\data\\Kangyur_Tenjur-CLEAN-2016-05-14\\enum_stem\\enum_stem";
	private static final String outputDir = "C:\\data\\TFIDF\\";
	private static final String tfDFFileName = "resources/data/TFIDF/tf.txt";
	public static final int DOC_NUM_IN_CORPUS = 4282;

	private static Map<Integer, Integer> tfMap;
	private static Map<Integer, Integer> dfMap;
	static{
		tfMap = new HashMap<>();
		dfMap = new HashMap<>();
		try {
			loadTFDFCounts();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private static void loadTFDFCounts() throws IOException{
		List<String> lines = Files.readAllLines(Paths.get(tfDFFileName), Charset.defaultCharset());
		for (int i = 1; i < lines.size(); i++){
			String[] words = lines.get(i).split(",");
			Integer stemAsInt = Integer.parseInt(words[0]);
			tfMap.put(stemAsInt, Integer.parseInt(words[2]));
			dfMap.put(stemAsInt, Integer.parseInt(words[3]));
		}
	}
	
	public static int getTFInCorpusForStem(int stemAsInt){
		return tfMap.get(stemAsInt);
	}
	
	public static int getDFForStem(int stemAsInt){
		return dfMap.get(stemAsInt);
	}
	

	
	public static void main(String[] args) throws IOException{
		countTFDF();
	}
	
	
	
	

	
	private static void countTFDF() throws IOException{
		List<String> files = getAllTextFilesDirs();
		IHistogram<Integer> stemHist = new HashMapHistogram<>();
		IHistogram<Integer> dfHist = new HashMapHistogram<>();
		int numOfDocuments = 0;
		for (String filePath : files){
			numOfDocuments++;
			Set<Integer> stemsForFile = new HashSet<>();
			List<String> fileContent = Files.readAllLines(Paths.get(filePath), Charset.defaultCharset());
			for (String line : fileContent){
				String[] stemsInLine = line.split(" ");
				for (String stemInLine: stemsInLine){
					Integer intValue = Integer.parseInt(stemInLine);
					stemHist.addItem(intValue);
					stemsForFile.add(intValue);
				}
			}
			//update the df histogram
			for(Integer stemInt : stemsForFile){
				dfHist.addItem(stemInt);
			}
		}
		List<String> stemsOut = new ArrayList<>();
		stemsOut.add("stemId,stem,count,df");
		for(Integer stem: stemHist){
			stemsOut.add(stem + "," + StemUtils.getStemForInt(stem) + ","+ stemHist.getCountForItem(stem) + "," + dfHist.getCountForItem(stem));
		}
		Files.write(Paths.get(outputDir, tfDFFileName), stemsOut, Charset.defaultCharset());
		System.out.println("# of documents in corpus: " + numOfDocuments);
	}
	
	
	
	private static List<String> getAllTextFilesDirs(){
		List<String> filePaths = getAllTextFiles(new File(mainDir));
		return filePaths;
		
	}

	private static List<String> getAllTextFiles(File dir) {
		List<String> paths = new ArrayList<String>();
		if (dir.isFile()){
			if (dir.getAbsolutePath().endsWith(".txt")){
				paths.add(dir.getAbsolutePath());
			}
		}
		else{
			for (File f : dir.listFiles()){
				paths.addAll(getAllTextFiles(f));
			}
		}
		return paths;
		
	}
}
