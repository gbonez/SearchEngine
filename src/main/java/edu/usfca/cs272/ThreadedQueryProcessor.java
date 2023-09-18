package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.usfca.cs272.InvertedIndex.SearchResult;

/**
 * Thread Safe version for multithreading. This processor object used to conduct
 * and generate search data upon a given ThreadSafeIndex
 * 
 * @author Grayson
 */
public class ThreadedQueryProcessor implements QueryInterface {

	/** Initalize queryResults */
	private final Map<String, List<SearchResult>> results;
	/** initalize index */
	private final ThreadSafeIndex index;
	/** initalize queue */
	private final WorkQueue queue;

	/** initalize logger */
	private final Logger log = LogManager.getLogger("edu.usfca.cs272.ThreadedIndexBuilder");

	/**
	 * Default constructor
	 * 
	 * @param index     ThreadSafeInvertedIndex we wish to populate
	 * @param workQueue WorkQueue to manage multiple threads
	 */
	public ThreadedQueryProcessor(ThreadSafeIndex index, WorkQueue workQueue) {
		this.results = new TreeMap<String, List<SearchResult>>();
		this.index = index;
		this.queue = workQueue;
	}

	@Override
	public void processQueries(Path path, boolean partialFlag) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				String temp = line;
				queue.execute(() -> {
					log.debug("Thread {} started search work on {} in file: {}", Thread.currentThread().getName(), temp,
							path.toString());
					processQuery(temp, partialFlag);
					log.debug("Thread {} finished search work on {} in file {}", Thread.currentThread().getName(), temp,
							path.toString());
				});
			}
		} finally {
			queue.finish();
		}
	}

	@Override
	public List<SearchResult> processQuery(String line, boolean partialFlag) {
		TreeSet<String> uniqueStems = FileStemmer.uniqueStems(line);
		List<SearchResult> queryResults;
		if (!uniqueStems.isEmpty()) {
			String cleanedLine = String.join(" ", uniqueStems);
			synchronized (results) {
				queryResults = results.get(cleanedLine);
				if (queryResults != null) {
					return Collections.unmodifiableList(queryResults);
				} else {
					results.put(cleanedLine, Collections.emptyList());
				}
				queryResults = index.indexSearch(uniqueStems, partialFlag);
				synchronized (results) {
					results.put(cleanedLine, queryResults);
					return Collections.unmodifiableList(queryResults);
				}
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Getter to return all stored queries
	 * 
	 * @return a set of all stored queries
	 */
	@Override
	public Set<String> getQueries() {
		return Collections.unmodifiableSet(results.keySet());
	}

	/**
	 * Helper method to output query processor data into pretty JSON format
	 * 
	 * @param path path we wish to output the JSON data too
	 * @throws IOException when an IOException occurs
	 */
	@Override
	public void writeResults(Path path) throws IOException {
		synchronized (results) {
			JsonWriter.writeSearchMap(results, path);
		}
	}
}
