package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Object to assist with parsing links and HTML data. Uses data to construct
 * InvertedIndex
 * 
 * @author Grayson Ruehlmann
 *
 */
public class WebCrawler {
	/**
	 * Initalize visitedUrls
	 */
	private Set<String> visitedUrls;
	/**
	 * Initalize index
	 */
	private ThreadSafeIndex index;
	/**
	 * Initaize queue
	 */
	private WorkQueue queue;
	/**
	 * Initaize limit
	 */
	private int limit;
	/**
	 * Initaize log
	 */
	private final Logger log = LogManager.getLogger("edu.usfca.cs272.WebCrawler");

	/**
	 * Constructor, using an InvertedIndex
	 * 
	 * @param inverted InvertedIndex to populate with WebCrawler data
	 * @param work     WorkQueue to use for WebCrawler
	 * @param max      Maximum amount of urls that crawler can crawl
	 */
	public WebCrawler(ThreadSafeIndex inverted, WorkQueue work, int max) {
		visitedUrls = new HashSet<String>();
		index = inverted;
		queue = work;
		limit = max;
	}

	/**
	 * Crawls the web starting with the provided seed URL, indexing all pages
	 * encountered along the way.
	 * 
	 * @param seedUrl The seed URL to start crawling from.
	 */
	public void crawl(String seedUrl) {
		try {
			String cleanedUrl = cleanUrl(seedUrl);
			queue.execute(() -> {
				crawlUrl(cleanedUrl);
			});
		} catch (MalformedURLException | URISyntaxException e) {
			System.err.println("Failed to crawl: " + seedUrl);
		} finally {
			queue.finish();
		}

	}

	/**
	 * Crawls the provided URL, indexing the page content and discovering new links.
	 * 
	 * @param url The URL to crawl.
	 */
	private void crawlUrl(String url) {
		try {
			log.debug("Thread {} started crawler work on {}", Thread.currentThread().getName(), url);
			int indexCount = 0;
			Stemmer stemmer = new SnowballStemmer(ENGLISH);
			synchronized (visitedUrls) {
				visitedUrls.add(url);
			}

			URL currentUrl = new URL(url);
			String html = HtmlFetcher.fetch(currentUrl, 3);

			if (html != null) {
				String cleanedLinkHtml = HtmlCleaner.stripBlockElements(html);
				List<URL> links = new ArrayList<>();
				LinkFinder.findUrls(currentUrl, cleanedLinkHtml, links);
				String cleanedTextHtml = HtmlCleaner.stripHtml(html);
				InvertedIndex local = new InvertedIndex();
				for (String word : FileStemmer.parse(cleanedTextHtml)) {
					String stemmed = stemmer.stem(word).toString();
					indexCount++;
					local.addData(stemmed, url, indexCount);
				}

				index.addAll(local);
				synchronized (visitedUrls) {
					for (URL link : links) {
						String cleanedLink = cleanUrl(link.toString());
						if (!visitedUrls.contains(cleanedLink) && visitedUrls.size() < limit) {
							visitedUrls.add(cleanedLink);
							queue.execute(() -> crawlUrl(cleanedLink));
						}
					}
				}
				log.debug("Thread {} finished crawler work on {}", Thread.currentThread().getName(), url);
			}
		} catch (MalformedURLException e) {
			System.err.println("Failed to crawl: " + url);
		} catch (URISyntaxException e) {
			System.err.println("Failed to crawl: " + url);
		}
	}

	/**
	 * Crawls the web starting with the provided seed URL, indexing all pages
	 * encountered along the way. Allows the user to update the max links crawled by
	 * the crawler
	 * 
	 * @param seedUrl URL to crawl
	 * @param max     Number to change the limit into
	 */
	public void crawl(String seedUrl, Integer max) {
		limit = max;
		crawl(seedUrl);
	}

	/**
	 * 
	 * Cleans the provided URL by removing any query parameters, fragment
	 * identifiers, or relative paths
	 * 
	 * @param url The URL to clean.
	 * @return The cleaned URL as a string.
	 * @throws URISyntaxException    If the provided URL syntax is invalid.
	 * @throws MalformedURLException If the provided URL is malformed.
	 */
	private String cleanUrl(String url) throws URISyntaxException, MalformedURLException {
		URL cleanedUrl = new URL(url);
		cleanedUrl = new URL(cleanedUrl.getProtocol(), cleanedUrl.getHost(), cleanedUrl.getPort(),
				cleanedUrl.getPath());
		cleanedUrl = new URL(cleanedUrl.toExternalForm());
		return cleanedUrl.toURI().normalize().toString();
	}

	/**
	 * Checks if the given URL is present in the visitedUrls set.
	 *
	 * @param url The URL to check.
	 * @return true if the URL is present in visitedUrls, false otherwise.
	 */
	public boolean hasUrl(String url) {
		synchronized (visitedUrls) {
			return visitedUrls.contains(url);
		}
	}

}