package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.usfca.cs272.InvertedIndex.SearchResult;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Processor object used to conduct and generate search data upon a given
 * InvertedIndex
 * 
 * @author Grayson Ruehlmann
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2023
 */
public class QueryProcessor implements QueryInterface {

	/**
	 * Initalize results
	 */
	private final Map<String, List<SearchResult>> results;
	/**
	 * Initalize index
	 */
	private final InvertedIndex index;
	/**
	 * Initalize stemmer
	 */
	private final Stemmer stemmer;

	/**
	 * Constructor for using only an InvertedIndex
	 * 
	 * @param index I.I we use to construct the QueryProcessor
	 */
	public QueryProcessor(InvertedIndex index) {
		this.index = index;
		this.results = new TreeMap<String, List<SearchResult>>();
		this.stemmer = new SnowballStemmer(ENGLISH);
	}

	/**
	 * Function that takes a line and searchs processor attached InvertedIndex for
	 * said queries. Then ranks results via a calculated score.
	 * 
	 * @param line        line from which we read
	 * @param partialFlag true = partial, false = exact
	 * @return a list of searchresult data corresponding to the given line
	 */
	@Override
	public List<SearchResult> processQuery(String line, boolean partialFlag) {
		TreeSet<String> uniqueStems = FileStemmer.uniqueStems(line, stemmer);
		if (!uniqueStems.isEmpty()) {
			String cleanedLine = String.join(" ", uniqueStems);
			List<SearchResult> queryResults = results.get(cleanedLine);
			if (queryResults == null) {
				queryResults = index.indexSearch(uniqueStems, partialFlag);
				results.put(cleanedLine, queryResults);
			}
			return Collections.unmodifiableList(queryResults);
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
		JsonWriter.writeSearchMap(results, path);
	}
}
