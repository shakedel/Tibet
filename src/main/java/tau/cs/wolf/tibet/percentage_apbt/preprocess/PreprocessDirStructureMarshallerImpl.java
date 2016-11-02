package tau.cs.wolf.tibet.percentage_apbt.preprocess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tau.cs.wolf.tibet.percentage_apbt.data.slicable.Slicable;
import tau.cs.wolf.tibet.percentage_apbt.main.args.ArgsPreprocess;
import tau.cs.wolf.tibet.percentage_apbt.misc.UnorderedIntPair;

import com.google.common.collect.BiMap;

public class PreprocessDirStructureMarshallerImpl implements PreprocessDirStructureMarshaller {

	private final Path argsBinPath;
	private final Path argsTextPath;
	
	private final Path docsBinDirPath;
	private final Path docsMapBinPath;
	private final Path docsMapTextPath;
	
	private final Path grpsBinDirPath;
	private final Path grpsMapBinPath;
	private final Path grpsMapTextPath;
	private final Path grpsIdsBinPath;
	private final Path grpsIdsTextPath;
	
	public PreprocessDirStructureMarshallerImpl(Path basePath) {
		
		Path configDir = basePath.resolve("config");
		this.argsBinPath = configDir.resolve("args.bin");
		this.argsTextPath = configDir.resolve("args.txt");
		
		Path docsDir = basePath.resolve("docs");
		this.docsBinDirPath = docsDir.resolve("bin");
		Path docsMapDir = docsDir.resolve("map");
		this.docsMapBinPath = docsMapDir.resolve("id_map.bin");
		this.docsMapTextPath = docsMapDir.resolve("id_map.txt");
		
		Path grpsDir = basePath.resolve("groups");
		this.grpsBinDirPath = grpsDir.resolve("bin");
		Path grpsMapDir = grpsDir.resolve("map");
		this.grpsMapBinPath = grpsMapDir.resolve("id_map.bin");
		this.grpsMapTextPath = grpsMapDir.resolve("id_map.txt");
		this.grpsIdsBinPath = grpsMapDir.resolve("id_list.bin");
		this.grpsIdsTextPath = grpsMapDir.resolve("id_list.txt");
	}
	
	@Override
	public void writeArgs(ArgsPreprocess args) throws IOException {
		write(this.argsBinPath, args);
		write(this.argsTextPath, args.toString());
	}

	@Override
	public ArgsPreprocess readArgs() throws IOException {
		return read(argsBinPath);
	}

	@Override
	public void writeDocMap(BiMap<String, Integer> docs) throws IOException {
		write(this.docsMapBinPath, docs);
		write(this.docsMapTextPath, docs.toString());
	}

	@Override
	public BiMap<Path, Integer> readDocsMap() throws IOException {
		return read(this.docsMapBinPath);
	}
	
	@Override
	public void writeGrpMap(Map<Integer, Set<UnorderedIntPair>> grps) throws IOException {
		write(this.grpsMapBinPath, grps);
		write(this.grpsMapTextPath, grps.toString());
	}

	@Override
	public Map<Integer, Set<UnorderedIntPair>> readGrpMap() throws IOException {
		return read(this.grpsMapBinPath);
	}


	@Override
	public void writeDoc(int docId, Slicable<?> content) throws IOException {
		Path path = this.docsBinDirPath.resolve(formatIdBinFile(docId));
		write(path, content);
	}

	@Override
	public Slicable<?> readDoc(int docId) throws IOException {
		Path path = this.docsBinDirPath.resolve(formatIdBinFile(docId));
		return read(path);
	}

	@Override
	public void writeGrp(int grpId, Set<UnorderedIntPair> grp) throws IOException {
		Path path = this.grpsBinDirPath.resolve(formatIdBinFile(grpId));
		write(path, grp);
	}

	@Override
	public Set<UnorderedIntPair> readGrp(int grpId) throws IOException {
		Path path = this.grpsBinDirPath.resolve(formatIdBinFile(grpId));
		return read(path);
	}
	
	
	@Override
	public void writeGrpIds(
			List<Integer> grpIds) throws IOException {
		write(this.grpsIdsBinPath, grpIds);
		write(this.grpsIdsTextPath, grpIds.toString());
	}

	@Override
	public List<Integer> readGrpIds() throws IOException {
		return read(this.grpsIdsBinPath);
	}

	private static void write(Path path, Object obj) throws IOException {
		Files.createDirectories(path.getParent());
		OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
		try {
		    ObjectOutputStream oos = new ObjectOutputStream(os);
	    	oos.writeObject(obj);
		} finally {
			os.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <R> R read(Path path) throws IOException {
		try (FileChannel channel = FileChannel.open(path)) {
			ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(channel));
			R obj;
			try {
				obj = (R) ois.readObject();
			} catch (ClassNotFoundException e) {
				throw new IOException(e);
			}
			return obj;
		}
	}
	
	public static Path formatIdBinFile(int id) {
		return formatId(id, ".bin");
	}
	
	public static Path formatId(int id, String suffix) {
		String dirname = String.format("%08d--", id/100);
		String filename = String.format("%010d", id);
		return Paths.get(dirname, filename+suffix);
	}
	
}
