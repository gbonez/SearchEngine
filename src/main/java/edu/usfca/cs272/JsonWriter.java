package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.usfca.cs272.InvertedIndex.SearchResult;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using spaces.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author Grayson Ruehlmann
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2023
 */
public class JsonWriter {
	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write("  ");
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param indent  the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param indent  the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */

	public static void writeArray(Collection<? extends Number> elements, Writer writer, int indent) throws IOException {
		writer.write("[");
		Iterator<? extends Number> iterator = elements.iterator();
		if (iterator.hasNext()) {
			writer.write("\n");
			writeIndent(writer, indent + 1);
			writer.write(iterator.next().toString());
		}
		while (iterator.hasNext()) {
			writer.write(",\n");
			writeIndent(writer, indent + 1);
			writer.write(iterator.next().toString());
		}
		writer.write("\n");
		writeIndent(writer, indent);
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Writer writer, int indent)
			throws IOException {
		writer.write("{");
		var iterator = elements.entrySet().iterator();
		if (iterator.hasNext()) {
			Entry<String, ? extends Number> pair = iterator.next();
			writer.write("\n");
			writeIndent(writer, indent + 1);
			writer.write("\"" + pair.getKey() + "\": " + pair.getValue());
		}
		while (iterator.hasNext()) {
			Entry<String, ? extends Number> entry = iterator.next();
			writer.write(",\n");
			writeIndent(writer, indent + 1);
			writer.write("\"" + entry.getKey() + "\": " + entry.getValue());
		}
		writer.write("\n");
		writeIndent(writer, indent);
		writer.write("}");
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, ? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of number objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeArray(Collection)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Writer writer,
			int indent) throws IOException {
		writer.write("{");
		var iterator = elements.entrySet().iterator();
		if (iterator.hasNext()) {
			writer.write("\n");
			var pair = iterator.next();
			writeIndent(writer, indent + 1);
			writer.write("\"" + pair.getKey() + "\": ");
			writeArray(pair.getValue(), writer, indent + 1);
		}
		while (iterator.hasNext()) {
			var entry = iterator.next();
			writer.write(",\n");
			writeIndent(writer, indent + 1);
			writer.write("\"" + entry.getKey() + "\": ");
			writeArray(entry.getValue(), writer, indent + 1);
		}
		writer.write("\n");
		writeIndent(writer, indent);
		writer.write("}");
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObjectArrays(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObjectArrays(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects. The generic
	 * notation used allows this method to be used for any type of collection with
	 * any type of nested map of String keys to number objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Writer writer,
			int indent) throws IOException {
		writer.write("[");
		var iterator = elements.iterator();
		if (iterator.hasNext()) {
			writer.write("\n");
			writeIndent(writer, indent + 1);
			writeObject(iterator.next(), writer, indent + 1);
		}
		while (iterator.hasNext()) {
			writer.write(",\n");
			writeIndent(writer, indent + 1);
			writeObject(iterator.next(), writer, indent + 1);
		}
		writeIndent(writer, indent);
		writer.write("\n");
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArrayObjects(Collection)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArrayObjects(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArrayObjects(Collection)
	 */
	public static String writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArrayObjects(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 
	 * Writes a Map of nested Maps of String keys to collections of Number objects
	 * as a pretty JSON array with nested objects.
	 * 
	 * @param elements the index object to write
	 * @param writer   the writer object to write with
	 * @param indent   the number of spaces to use for indentation
	 * @throws IOException when theres errors writing to an output file
	 */
	public static void writeInverted(
			Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> elements, Writer writer,
			int indent) throws IOException {
		writer.write("{");
		var iterator = elements.entrySet().iterator();
		if (iterator.hasNext()) {
			var entry = iterator.next();
			writer.write("\n");
			writeIndent(writer, indent + 1);
			writer.write("\"" + entry.getKey() + "\": ");
			writeObjectArrays(entry.getValue(), writer, indent + 1);
		}
		while (iterator.hasNext()) {
			writer.write(",\n");
			var entry = iterator.next();
			writeIndent(writer, indent + 1);
			writer.write("\"" + entry.getKey() + "\": ");
			writeObjectArrays(entry.getValue(), writer, indent + 1);
		}
		writeIndent(writer, indent);
		writer.write("\n}");
	}

	/**
	 * Writes a Map of nested Maps of String keys to collections of Number objects
	 * as a pretty JSON array with nested objects.
	 * 
	 * @param map  invertedindex we want to print to json format
	 * @param path path we wish to print to
	 * @throws IOException when an ioexception occurs
	 */
	public static void writeInverted(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> map,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writeInverted(map, writer, 0);
		}
	}

	/**
	 * Returns a pretty JSON string representation of the given inverted index map.
	 * 
	 * @param map the inverted index map to convert to JSON
	 * @return a pretty JSON string representation of the inverted index map
	 */
	public static String writeInverted(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> map) {
		StringWriter writer = new StringWriter();
		try {
			writeInverted(map, writer, 0);
		} catch (IOException e) {
			return null;
		}
		return writer.toString();
	}

	/**
	 * Method to convert a singular searchresult to a pretty JSON string format
	 * 
	 * @param result the result we want to convert
	 * @param indent the indent level we wish to have
	 * @param writer a writer
	 * @throws IOException when an IOException occurs
	 */
	public static void toJson(SearchResult result, int indent, Writer writer) throws IOException {
		writeIndent("{\n", writer, indent);
		writeIndent("  \"count\": " + result.getCount() + ",\n", writer, indent);
		writeIndent("  \"score\": " + String.format("%.8f", result.getScore()) + ",\n", writer, indent);
		writeIndent("  \"where\": \"" + result.getLocation() + "\"\n", writer, indent);
		writeIndent("}", writer, indent);
	}

	/**
	 * Write a list of SearchResults to JSON Formatting
	 * 
	 * @param results the list of SearchResults we write
	 * @param writer  the writer we use to write
	 * @param indent  the indentation level
	 * @throws IOException when an ioexception occurs
	 */
	public static void writeSearchResults(List<SearchResult> results, Writer writer, int indent) throws IOException {
		writer.write("[");
		Iterator<SearchResult> iterator = results.iterator();
		if (iterator.hasNext()) {
			writer.write("\n");
			SearchResult result = iterator.next();
			toJson(result, indent + 1, writer);
		}
		while (iterator.hasNext()) {
			SearchResult result = iterator.next();
			writer.write(",\n");
			toJson(result, indent + 1, writer);
		}
		writer.write("\n");
		writeIndent(writer, indent);
		writer.write("]");
	}

	/**
	 * Method to write a QueryProcessor to pretty JSON Format
	 * 
	 * @param map    the query processor we write
	 * @param writer the writer we use
	 * @param indent the indentation level
	 * @throws IOException when an ioexception occurs
	 */
	public static void writeSearchMap(Map<String, List<SearchResult>> map, Writer writer, int indent)
			throws IOException {
		writer.write("{");
		var iterator = map.entrySet().iterator();
		if (iterator.hasNext()) {
			var entry = iterator.next();
			writer.write("\n");
			writeIndent(writer, indent + 1);
			writer.write("\"" + entry.getKey() + "\": ");
			writeSearchResults(entry.getValue(), writer, indent + 1);
		}
		while (iterator.hasNext()) {
			writer.write(",\n");
			var entry = iterator.next();
			writeIndent(writer, indent + 1);
			writer.write("\"" + entry.getKey() + "\": ");
			writeSearchResults(entry.getValue(), writer, indent + 1);
		}
		writer.write("\n");
		writeIndent(writer, indent);
		writer.write("}");
	}

	/**
	 * A method that generates a bufferedwriter to write a queryProcessor to
	 * 
	 * @param map  the queryprocessor we are writing
	 * @param path the path we are writing to
	 * @throws IOException when an ioexception occurs
	 */
	public static void writeSearchMap(Map<String, List<SearchResult>> map, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writeSearchMap(map, writer, 0);
		}
	}
}
