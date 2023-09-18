package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Helper method to construct an Inverted Index data structure to be used with a
 * search engine
 * 
 * @author Grayson Ruehlmann
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2023
 */
public class InvertedIndexBuilder {
	/**
	 * Building code to construct an InvertedIndex
	 * 
	 * @param start file path to read data from
	 * @param index inverted index we wish to construct
	 * @throws IOException when IOException occurs
	 */
	public static void build(Path start, InvertedIndex index) throws IOException {
		List<Path> files = FileFinder.listText(start, start);
		for (Path file : files) {
			parseFile(file, index);
		}
	}

	/**
	 * Parsing File to construct an inverted index
	 * 
	 * @param file  path from which we collect data for the invertedindex
	 * @param index inverted index we are constructing
	 * @throws IOException when an IOException occurs
	 */
	public static void parseFile(Path file, InvertedIndex index) throws IOException {
		int indexCount = 0;
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		String location = file.toString();
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				for (String word : FileStemmer.parse(line)) {
					String stemmed = stemmer.stem(word).toString();
					indexCount++;
					index.addData(stemmed, location, indexCount);
				}
			}
		}
	}
}
