package edu.usfca.cs272;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper method to construct an Inverted Index data structure to be used with a
 * search engine
 * 
 * @author Grayson Ruehlmann
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2023
 */
public class ThreadSafeIndexBuilder {

	/**
	 * Initalize Logger for debugging
	 */
	public static Logger log = LogManager.getLogger("edu.usfca.cs272.ThreadedIndexBuilder");

	/**
	 * Building code to construct a thread-safe version of an InvertedIndex
	 * 
	 * @param start file path to read data from
	 * @param index the thread-safe inverted index to populate
	 * @param queue work queue to use
	 * @throws IOException when an IOException occurs
	 * 
	 */
	public static void build(Path start, ThreadSafeIndex index, WorkQueue queue) throws IOException {
		try {
			List<Path> files = FileFinder.listText(start, start);
			for (Path file : files) {
				queue.execute(() -> {
					try {
						InvertedIndex local = new InvertedIndex();
						log.debug("Thread {} started index build work on {}", Thread.currentThread().getName(),
								file.toString());
						InvertedIndexBuilder.parseFile(file, local);
						index.addAll(local);
						log.debug("Thread {} finished index build work on {}", Thread.currentThread().getName(),
								file.toString());
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
			}
		} finally {
			queue.finish();
		}

	}
}
