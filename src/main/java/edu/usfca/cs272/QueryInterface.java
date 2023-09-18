package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import edu.usfca.cs272.InvertedIndex.SearchResult;

/**
 * Interface to set up methods for single threaded Query Processor and
 * multithreaded Query Processor
 * 
 * @author Grayson
 *
 */
public interface QueryInterface {

	/**
	 * Method to process multiple queries from a given path to search an
	 * InvertedIndex
	 * 
	 * @param path        path to read from
	 * @param partialFlag true = partial search / false = exact search
	 * @throws IOException when IOException occurs
	 */
	public default void processQueries(Path path, boolean partialFlag) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				processQuery(line, partialFlag);
			}
		}
	}

	/**
	 * Method to process a single query from a given string to search an
	 * InvertedIndex
	 * 
	 * @param line        the query we wish to garner info from
	 * @param partialFlag true = partial search / false = exact search
	 * @return list of associated index search results for the given query
	 */
	public List<SearchResult> processQuery(String line, boolean partialFlag);

	/**
	 * Getter to return all stored queries
	 * 
	 * @return a set of all stored queries
	 */
	public Set<String> getQueries();

	/**
	 * Method to write out results data to a given path in pretty JSON format
	 * 
	 * @param path path we wish to write to
	 * @throws IOException when IOException occurs
	 */
	public void writeResults(Path path) throws IOException;
}