package edu.usfca.cs272;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet to allow users to add a webpage to the database using a web browser
 * 
 * @author Grayson
 */
public class CrawlerServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * initalize webcrawler
	 */
	private WebCrawler crawler;

	/**
	 * @param webCrawler
	 * 
	 */
	public CrawlerServlet(WebCrawler webCrawler) {
		super();
		this.crawler = webCrawler;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		String filePath = "src/main/resources/add-url.html";
		String htmlContent = readFileContent(filePath);
		response.getWriter().write(htmlContent);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		String url = request.getParameter("url");
		String number = request.getParameter("number");
		url = url == null ? "" : url;
		if (crawler.hasUrl(url)) {

		}
		number = number == null ? "1" : number;
		Integer max = Integer.parseInt(number);
		if (crawler.hasUrl(url)) {
			response.getWriter().write("URL found in database. Please enter a different URL");
		} else if (url.isEmpty()) {
			response.getWriter().write("No URL provided. Please enter a URL");
		} else {
			crawler.crawl(url, max);
			response.getWriter().write("Crawling and indexing completed for URL: " + url + " with number: " + number);
		}
	}

	private String readFileContent(String filePath) throws IOException {
		byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
		return new String(fileBytes, StandardCharsets.UTF_8);
	}
}
