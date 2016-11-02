package tau.cs.wolf.tibet.percentage_apbt.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.kohsuke.args4j.CmdLineException;

import tau.cs.wolf.tibet.percentage_apbt.data.slicable.Slicable;
import tau.cs.wolf.tibet.percentage_apbt.data.slicable.SlicableParser;
import tau.cs.wolf.tibet.percentage_apbt.main.AppUtils.SrcType;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsPreprocess;
import tau.cs.wolf.tibet.percentage_apbt.misc.IdGenerator;
import tau.cs.wolf.tibet.percentage_apbt.misc.Props;
import tau.cs.wolf.tibet.percentage_apbt.misc.UnorderedIntPair;
import tau.cs.wolf.tibet.percentage_apbt.preprocess.PreprocessDirStructureMarshaller;
import tau.cs.wolf.tibet.percentage_apbt.preprocess.PreprocessDirStructureMarshallerImpl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class AppPreprocess extends AppBase {

	protected final ArgsPreprocess args;
	private final List<Integer> inDocIds1 = new ArrayList<Integer>();
	private final List<Integer> inDocIds2 = new ArrayList<Integer>();
	
	private final IdGenerator<Set<UnorderedIntPair>> grpIdGenerator = new IdGenerator.SequentialIdGenerator<>(0, 1);
	
	private final PreprocessDirStructureMarshaller marshaller;

	public AppPreprocess(ArgsPreprocess args, Props _props) {
		super(args, _props);
		this.args = args;
		this.marshaller = new PreprocessDirStructureMarshallerImpl(args.getOutDir());
	}

	@Override
	public void run() {
		try {
			runWithIOException();
		} catch(IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void runWithIOException() throws IOException {
		
		marshaller.writeArgs(this.args);
		
		// create docs dir
		Path docsDir = this.args.getOutDir().resolve("docs");
		try {
			Files.createDirectories(docsDir);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		
		// create docs map
		{
			IdGenerator<Path> docIdGenerator = new IdGenerator.SequentialIdGenerator<>(0, 1);
			BiMap<String, Integer> docs = HashBiMap.create();
			addDocs(docs, this.args.calcInPaths1(), this.inDocIds1, docIdGenerator);
			addDocs(docs, this.args.calcInPaths2(), this.inDocIds2, docIdGenerator);
			marshaller.writeDocMap(docs);
		}
		
		// create grps map
		{
			Map<Integer, Set<UnorderedIntPair>> grps = addGrps();
			List<Integer> grpsList = new ArrayList<>(grps.keySet());
			this.marshaller.writeGrpMap(grps);
			this.marshaller.writeGrpIds(grpsList);
		}

	}

	private Map<Integer, Set<UnorderedIntPair>> addGrps() throws IOException {
		Map<Integer, Set<UnorderedIntPair>> grps = new TreeMap<>();
		
		int maxGrpSize = this.args.getMaxGrpSize();
		int halfGrpSize = maxGrpSize/2;
		int len = (int) Math.floor(Math.sqrt(maxGrpSize));
		
		Set<UnorderedIntPair> allPairs = new UnorderedIntPair.UnorderedIntPairSet();
		Set<UnorderedIntPair> partialGrp = null;
		
		for (int startRow=0; startRow<this.inDocIds1.size(); startRow+=len) {
			for (int startCol=0; startCol<this.inDocIds2.size(); startCol+=len) {
				
				Set<UnorderedIntPair> grp = new UnorderedIntPair.UnorderedIntPairSet();
				UnorderedIntPair pair = new UnorderedIntPair(Integer.MAX_VALUE, Integer.MAX_VALUE);
				
				for (int row=startRow; row<startRow+len && row<this.inDocIds1.size(); row++) {
					for (int col=startCol; col<startCol+len && col<this.inDocIds2.size(); col++) {
						int docId1 = this.inDocIds1.get(row);
						int docId2 = this.inDocIds2.get(col);
						if (docId1 == docId2) {
							continue;
						}
						pair.set(docId1, docId2);
						boolean isNewPair = allPairs.add(pair);
						if (isNewPair) {
							grp.add(pair);
						}
					}
				}
				
				if (grp.size() < halfGrpSize) {
					if (partialGrp == null) {
						partialGrp = grp;
						continue;
					} else {
						grp.addAll(partialGrp);
						partialGrp = null;
					}
				}
				int grpId = writeGrp(grp);
				grps.put(grpId, grp);
			}
			
		}
		
		if (partialGrp != null) {
			int grpId = writeGrp(partialGrp);
			grps.put(grpId, partialGrp);
		}
		
		return grps;
	}

	private int writeGrp(Set<UnorderedIntPair> grp) throws IOException {
		int grpId = this.grpIdGenerator.nextId(grp);
		this.marshaller.writeGrp(grpId, grp);
		return grpId;
	}

	private void addDocs(BiMap<String,Integer> docs, SortedSet<Path> inPaths,
			List<Integer> inDocIds, IdGenerator<Path> idGenerator) throws IOException {
		@SuppressWarnings("unchecked")
		SlicableParser<?, Path> parser = (SlicableParser<?, Path>) AppUtils.getParser(args.getDataType(), SrcType.PATH);
		for (Path path: inPaths) {
			Integer docId = docs.get(path.toAbsolutePath().toString());
			if (docId == null) {
				docId = idGenerator.nextId(path);
				logger.info("assigning ID: "+docId+" to doc: "+path);
				docs.put(path.toAbsolutePath().toString(), docId);
				Slicable<?> content = parser.parse(path);
				this.marshaller.writeDoc(docId, content);
			}
			inDocIds.add(docId);
		}
	}

	public static void main(String[] args) {
		try {
			AppPreprocess app = new AppPreprocess(new ArgsPreprocess(args), null);
			app.run();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
}
