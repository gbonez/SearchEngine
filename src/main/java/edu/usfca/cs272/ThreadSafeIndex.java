package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * index that stores inverted index data. the keys are words, and the values are
 * maps that associate file paths with lists of line numbers containing the word
 * position in said file
 *
 * also stores word counts via a seperate map
 *
 * @author Grayson Ruehlmann
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2023
 */
public class ThreadSafeIndex extends InvertedIndex {

	/**
	 * Initalize lock
	 */
	private final MultiReaderLock lock;

	/**
	 * Default constructor for ThreadedIndex
	 */
	public ThreadSafeIndex() {
		super();
		lock = new MultiReaderLock();
	}

	@Override
	public void addData(String word, String path, Integer value) {
		lock.writeLock().lock();
		try {
			super.addData(word, path, value);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addData(List<String> words, String path) {
		lock.writeLock().lock();
		try {
			super.addData(words, path);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addAll(InvertedIndex other) {
		lock.writeLock().lock();
		try {
			super.addAll(other);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();
		try {
			return super.hasWord(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasPath(String word, String filepath) {
		lock.readLock().lock();
		try {
			return super.hasPath(word, filepath);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasPosition(String word, String filepath, int position) {
		lock.readLock().lock();
		try {
			return super.hasPosition(word, filepath, position);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasCount(String filePath) {
		lock.readLock().lock();
		try {
			return super.hasCount(filePath);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getPaths(String word) {
		lock.readLock().lock();
		try {
			return super.getPaths(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> getPositions(String word, String filePath) {
		lock.readLock().lock();
		try {
			return super.getPositions(word, filePath);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Map<String, Integer> getCounts() {
		lock.readLock().lock();
		try {
			return super.getCounts();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numWords() {
		lock.readLock().lock();
		try {
			return super.numWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numPaths(String word) {
		lock.readLock().lock();
		try {
			return super.numPaths(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numPositions(String word, String filePath) {
		lock.readLock().lock();
		try {
			return super.numPositions(word, filePath);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numCounts(String word) {
		lock.readLock().lock();
		try {
			return super.numCounts(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void writeIndex(Path filePath) throws IOException {
		lock.readLock().lock();
		try {
			super.writeIndex(filePath);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void writeCounts(Path filePath) throws IOException {
		lock.readLock().lock();
		try {
			super.writeCounts(filePath);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<SearchResult> exactSearch(Set<String> queries) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<SearchResult> partialSearch(Set<String> queries) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int size() {
		lock.readLock().lock();
		try {
			return super.size();
		} finally {
			lock.readLock().unlock();
		}
	}

}
