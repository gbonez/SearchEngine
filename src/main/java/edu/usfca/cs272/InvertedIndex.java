package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
public class InvertedIndex {

	/**
	 * initalize index
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	/**
	 * initalize count
	 */
	private final TreeMap<String, Integer> counts;

	/**
	 * default constructor
	 */
	public InvertedIndex() {
		index = new TreeMap<>();
		counts = new TreeMap<>();
	}

	/*
	 * returns the index as a string
	 * 
	 * @return string of the index
	 */
	@Override
	public String toString() {
		return index.toString();
	}

	/**
	 * returns the size of the invertedindex
	 * 
	 * @return the size of the invertedindex
	 */
	public int size() {
		return index.size();
	}

	/**
	 * adds information to the index/counts data
	 * 
	 * @param word  word to add
	 * @param path  path to add
	 * @param value numbers to add
	 */
	public void addData(String word, String path, Integer value) {
		index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		index.get(word).putIfAbsent(path, new TreeSet<Integer>());
		index.get(word).get(path).add(value);
		int max = counts.getOrDefault(path, 0);
		if (value > max) {
			counts.put(path, value);
		}
	}

	/**
	 * adds a list of info to the invertedindex
	 * 
	 * @param words list of words to add
	 * @param path  path at which the words are found
	 */
	public void addData(List<String> words, String path) {
		int counter = 1;
		for (String word : words) {
			addData(word, path, counter++);
		}
	}

	/**
	 * Adds all the contents of another InvertedIndex to this index
	 *
	 * @param other The other InvertedIndex to get data from
	 */
	public void addAll(InvertedIndex other) {
		for (var wordEntry : other.index.entrySet()) {
			String word = wordEntry.getKey();
			var otherLocations = wordEntry.getValue();
			var thisLocations = this.index.get(word);
			if (thisLocations == null) {
				this.index.put(word, otherLocations);
			} else {
				for (var locationEntry : otherLocations.entrySet()) {
					String path = locationEntry.getKey();
					TreeSet<Integer> otherPositions = locationEntry.getValue();
					TreeSet<Integer> thisPositions = thisLocations.get(path);
					if (thisPositions == null) {
						thisLocations.put(path, otherPositions);
					} else {
						thisPositions.addAll(otherPositions);
					}
				}
			}
		}
		for (var countEntry : other.counts.entrySet()) {
			String path = countEntry.getKey();
			int otherCount = countEntry.getValue();
			int thisCount = this.counts.getOrDefault(path, 0);
			if (otherCount > thisCount) {
				this.counts.put(path, otherCount);
			}
		}
	}

	/**
	 * checks if the index has data stored for a specific word
	 * 
	 * @param word word we are trying to find in index
	 * @return true if the index contains the given word
	 */
	public boolean hasWord(String word) {
		return index.containsKey(word);
	}

	/**
	 * checks if a word is present in a given file
	 * 
	 * @param word     key we seek info on
	 * @param filePath file in which we seek info on
	 * @return true if the index contains both the word, and the file path is
	 *         associated with the word
	 */
	public boolean hasPath(String word, String filePath) {
		return index.containsKey(word) && index.get(word).containsKey(filePath);
	}

	/**
	 * @param word     word we want to check position of
	 * @param filePath file we want to check position of word in
	 * @param position position we want to see if word is at
	 * @return true if word is present in filePath at position
	 */
	public boolean hasPosition(String word, String filePath, int position) {
		return hasPath(word, filePath) && index.get(word).get(filePath).contains(position);
	}

	/**
	 * checks to see if the word given has its word count stored in the index
	 * 
	 * @param filePath file path we are trying to find word count data for
	 * @return true if word has data in the wordcount structure
	 */
	public boolean hasCount(String filePath) {
		return counts.containsKey(filePath);
	}

	/**
	 * retrieves a set of paths that are associated with a given word in the index
	 * 
	 * @param word word we are trying to find in index
	 * @return set of all filepaths containing the given word
	 */
	public Set<String> getPaths(String word) {
		if (index.containsKey(word)) {
			return Collections.unmodifiableSet(index.get(word).keySet());
		}
		return Collections.emptySet();
	}

	/**
	 * retrieves the positions of a specific word in a specific file
	 * 
	 * @param word     key we seek info on
	 * @param filePath file in which we seek info on
	 * @return a list of positions in the file where the given word is present
	 */
	public Set<Integer> getPositions(String word, String filePath) {
		if (this.hasPath(word, filePath)) {
			return Collections.unmodifiableSet(index.get(word).get(filePath));
		}
		return Collections.emptySet();
	}

	/**
	 * retrieves every word that is stored in the index
	 * 
	 * @return set of all words contained in the index
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(index.keySet());
	}

	/**
	 * retrieves the word count data of the index
	 * 
	 * @return a map containing files and their word counts
	 */
	public Map<String, Integer> getCounts() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * 
	 * retrieves the number of unique words (keys) in the index.
	 * 
	 * @return the number of unique words (keys) in the index.
	 */
	public int numWords() {
		return index.size();
	}

	/**
	 * 
	 * retrieves the number of paths that a specific word appears in.
	 * 
	 * @param word the word to search for.
	 * @return the number of paths that the word appears in.
	 */
	public int numPaths(String word) {
		return getPaths(word).size();
	}

	/**
	 * 
	 * retrieves the number of times a word appears in a specific file.
	 * 
	 * @param word     the word to search for.
	 * @param filePath the path of the file to search in.
	 * @return the number of times the word appears in the file.
	 */
	public int numPositions(String word, String filePath) {
		return getPositions(word, filePath).size();
	}

	/**
	 * 
	 * retrieves the total number of words in a specific file.
	 * 
	 * @param filePath the path of the file to search in.
	 * @return the total number of words in the file.
	 */
	public int numCounts(String filePath) {
		return this.counts.getOrDefault(filePath, 0);
	}

	/**
	 * writes index data to a provided path in pretty JSON format
	 * 
	 * @param filePath file path we want to write to
	 * @throws IOException when an IOException occurs
	 */
	public void writeIndex(Path filePath) throws IOException {
		JsonWriter.writeInverted(index, filePath);
	}

	/**
	 * writes counts data of index to a provided path in pretty JSON format
	 * 
	 * @param filePath file path we want to write to
	 * @throws IOException when an IOException occurs
	 */
	public void writeCounts(Path filePath) throws IOException {
		JsonWriter.writeObject(this.getCounts(), filePath);
	}

	/**
	 * helper method to decide whether or not we conduct an exact or partial search
	 * on the index
	 * 
	 * @param queries queries we search the index for
	 * @param partial true = partial search, false = exact search
	 * @return results based on what search we conduct
	 */
	public List<SearchResult> indexSearch(Set<String> queries, boolean partial) {
		return partial ? partialSearch(queries) : exactSearch(queries);
	}

	/**
	 * conducts an exact search on the index to rank the locations of the given
	 * queries based on their frequency in each location
	 * 
	 * @param queries the queries we search the index for the locations of
	 * @return a list of ranked searchresults the contain the location, word count
	 *         and score data for each query
	 */
	public List<SearchResult> exactSearch(Set<String> queries) {
		Map<String, SearchResult> matches = new HashMap<>();
		List<SearchResult> results = new ArrayList<>();
		for (String query : queries) {
			var locations = index.get(query);
			if (locations != null) {
				resultsMapping(locations, matches, results);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * conducts a partial search on the index to rank the locations of the given
	 * queries based on their frequency in each location
	 * 
	 * @param queries the queries we search the index for the locations of
	 * @return a list of ranked searchresults the contain the location, word count
	 *         and score data for each query
	 */
	public List<SearchResult> partialSearch(Set<String> queries) {
		List<SearchResult> results = new ArrayList<>();
		Map<String, SearchResult> matches = new HashMap<>();
		for (String query : queries) {
			for (var entry : index.tailMap(query).entrySet()) {
				String word = entry.getKey();
				if (word.startsWith(query)) {
					var locations = entry.getValue();
					resultsMapping(locations, matches, results);
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Private helper method to contain duplicate logic for partial/exact search.
	 * Most of the logic generates the results list NOTE: could be cleaned up
	 * probably, but will save that for another time
	 * 
	 * @param locations map containing file location data and associated word
	 *                  locations
	 * @param matches   map to populate with a query + searchresult data
	 * @param results   list of search results to return with the given list of
	 *                  queries
	 */
	private void resultsMapping(TreeMap<String, TreeSet<Integer>> locations, Map<String, SearchResult> matches,
			List<SearchResult> results) {
		for (var entry : locations.entrySet()) {
			String location = entry.getKey();
			int count = entry.getValue().size();
			SearchResult result = matches.computeIfAbsent(location, loc -> {
				SearchResult newResult = new SearchResult(loc);
				results.add(newResult);
				return newResult;
			});
			result.calculate(count);
		}
	}

	/**
	 * Inner class used to store Inverted Index search results
	 * 
	 * @author Grayson Ruehlmann
	 *
	 */
	public class SearchResult implements Comparable<SearchResult> {
		/**
		 * Initalize count
		 */
		private Integer count;
		/**
		 * Initalize score
		 */
		private Double score;
		/**
		 * Initalize location
		 */
		private final String location;

		/**
		 * Constructor for only location
		 * 
		 * @param location file location we search for given query
		 */
		public SearchResult(String location) {
			this.count = 0;
			this.score = 0.0;
			this.location = location;
		}

		/**
		 * Private method to determine the score and count of a searchresult after
		 * construction using a location
		 * 
		 * @param count value to be used for searchresult construction
		 */
		private void calculate(int count) {
			this.count += count;
			this.score = (double) this.count / counts.get(location);
		}

		/**
		 * @return the count value of a SearchResult
		 */
		public Integer getCount() {
			return count;
		}

		/**
		 * @return the score value of a SearchResult
		 */
		public Double getScore() {
			return score;
		}

		/**
		 * @return the location value of a SearchResult
		 */
		public String getLocation() {
			return location;
		}

		@Override
		public String toString() {
			return "{ count: " + count + ", score: " + score + ", location: " + location + " }";
		}

		@Override
		public int compareTo(SearchResult other) {
			int scoreCompare = Double.compare(other.getScore(), this.getScore());
			if (scoreCompare != 0) {
				return scoreCompare;
			}
			int countCompare = Integer.compare(other.getCount(), this.getCount());
			if (countCompare != 0) {
				return countCompare;
			}
			return this.getLocation().compareToIgnoreCase(other.getLocation());
		}
	}

}