package edu.usfca.cs272;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet to load and generate search results on a browser. Allows users to
 * type in a query and loads search data from database
 * 
 * @author Grayson
 *
 */
public class IndexServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * initalize index
	 */
	private ThreadSafeIndex index;

	/**
	 * @param current InvertedIndex to use for Database
	 */
	public IndexServlet(ThreadSafeIndex current) {
		super();
		this.index = current;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String word = request.getParameter("word");

		if (word != null && !word.isEmpty()) {
			String resultsHtml = generateWordPage(word);
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(resultsHtml);
		} else {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			String htmlContent = generateIndexPage();
			response.getWriter().write(htmlContent);
		}
	}

	private String generateIndexPage() {
		Set<String> words = index.getWords();
		StringBuilder resultsHtml = new StringBuilder();

		for (String word : words) {
			resultsHtml.append("<li><a href=\"/index?word=").append(word).append("\">").append(word)
					.append("</a></li>");
		}
		String htmlContent;
		try {
			String filePath = "src/main/resources/index.html";
			htmlContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
			htmlContent = htmlContent.replace("{results}", resultsHtml.toString());
		} catch (IOException e) {
			htmlContent = "Error loading index results";
		}
		return htmlContent;
	}

	private String generateWordPage(String word) {
		Set<String> paths = index.getPaths(word);
		StringBuilder resultsHtml = new StringBuilder();
		for (String path : paths) {
			resultsHtml.append("<li><a href=\"").append(path).append("\">").append(path).append("</a>");
			resultsHtml.append("<span class=\"path-count\"> | ");
			resultsHtml.append(word).append(" was found on this page ");
			int size = index.getPositions(word, path).size();
			if (size == 1) {
				resultsHtml.append(size).append(" time</span>");
			} else {
				resultsHtml.append(size).append(" times</span>");
			}
			resultsHtml.append("</li>");
		}
		String htmlContent;
		try {
			String filePath = "src/main/resources/index.html";
			htmlContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
			htmlContent = htmlContent.replace("{word}", word).replace("{results}", resultsHtml.toString());
		} catch (IOException e) {
			htmlContent = "Error loading word results";
		}
		return htmlContent;
	}

}
