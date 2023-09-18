package edu.usfca.cs272;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import edu.usfca.cs272.InvertedIndex.SearchResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
public class SearchServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * initalize processor
	 */
	private ThreadedQueryProcessor processor;

	/**
	 * @param other QueryProcessor to use for Searching
	 */
	public SearchServlet(ThreadedQueryProcessor other) {
		super();
		this.processor = other;

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String query = request.getParameter("query");

		if (query != null && !query.isEmpty()) {
			String resultsHtml = performSearch(query);
			request.getSession().removeAttribute("query");

			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(resultsHtml);
		} else {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			String filePath = "src/main/resources/welcome.html";
			String htmlContent = readFileContent(filePath);
			response.getWriter().write(htmlContent);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		String query = request.getParameter("query");
		query = query == null ? "" : query;

		if (!query.isEmpty()) {
			Cookie[] cookies = request.getCookies();
			String searchResults = "";

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("searchResults")) {
						searchResults = cookie.getValue();
						break;
					}
				}
			}
			if (searchResults.isEmpty()) {
				searchResults = query;
			} else {
				searchResults += "|" + query;
			}
			Cookie searchResultsCookie = new Cookie("searchResults", searchResults);
			response.addCookie(searchResultsCookie);
		}
		response.sendRedirect(request.getServletPath());
	}

	private String performSearch(String query) {
		List<SearchResult> searchResults = processor.processQuery(query, true);
		StringBuilder resultsHtml = new StringBuilder();
		for (SearchResult result : searchResults) {
			resultsHtml.append("<li data-result-id=\"").append(result.getLocation()).append("\">");

			if (isHttpLink(result.getLocation())) {
				resultsHtml.append("<a href=\"").append(result.getLocation()).append("\">").append(result.getLocation())
						.append("</a>");
			} else {
				resultsHtml.append(result.getLocation());
			}

			resultsHtml.append("<span class=\"arrow\">&#9658;</span>");
			resultsHtml.append("<div class=\"menu\">");
			resultsHtml.append("<button class=\"menu-button\" onclick=\"addFavorite('"
					+ result.getLocation().replace("'", "\\'") + "', this)\">Add to Favorites</button>");
			resultsHtml.append("<button class=\"menu-button\">Button 3</button>");
			resultsHtml.append("</div>");
			resultsHtml.append("</li>");
		}
		String htmlContent;
		try {
			String filePath = "src/main/resources/search-results.html";
			htmlContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
			htmlContent = htmlContent.replace("{query}", query).replace("{results}", resultsHtml.toString());
		} catch (IOException e) {
			htmlContent = "Error loading search results";
		}
		return htmlContent;
	}

	private boolean isHttpLink(String link) {
		return link.startsWith("http://") || link.startsWith("https://");
	}

	private String readFileContent(String filePath) throws IOException {
		byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
		return new String(fileBytes, StandardCharsets.UTF_8);
	}
}
