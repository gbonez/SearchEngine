package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Grayson Ruehlmann
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2023
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {

		if (args == null || args.length == 0) {
			System.out.println("No arguments found!");
			return;
		}
		ArgumentParser argue = new ArgumentParser(args);
		InvertedIndex index;
		QueryInterface query;
		WorkQueue queue = null;
		ThreadSafeIndex safe = null;
		WebCrawler crawler = null;

		if (argue.hasFlag("-threads") || argue.hasFlag("-html") || argue.hasFlag("-server")) {
			index = new ThreadSafeIndex();
			int threads = argue.getInteger("-threads", 5);
			if (threads <= 0) {
				threads = 5;
			}
			queue = new WorkQueue(threads);
			safe = new ThreadSafeIndex();
			query = new ThreadedQueryProcessor(safe, queue);
			index = safe;
		} else {
			index = new InvertedIndex();
			query = new QueryProcessor(index);
		}

		if (argue.hasFlag("-html")) {
			try {
				String seedUrl = argue.getString("-html");
				int limit = 1;
				limit = argue.getInteger("-crawl", 1);
				if (limit <= 0) {
					limit = 1;
				}
				crawler = new WebCrawler((ThreadSafeIndex) index, queue, limit);
				crawler.crawl(seedUrl);
			} catch (IllegalArgumentException e) {
				System.err.println("-html flag error! Invalid URL provided!");
				return;
			} catch (NullPointerException e) {
				System.err.println("-text flag error! File path missing!");
				return;
			}
		}

		if (argue.hasFlag("-text") && argue.getPath("-text") != null) {
			try {
				if (safe != null && queue != null) {
					ThreadSafeIndexBuilder.build(argue.getPath("-text"), (ThreadSafeIndex) index, queue);
				} else {
					InvertedIndexBuilder.build(argue.getPath("-text"), index);
				}
			} catch (IOException e) {
				System.err.println("Error reading from file! (Path after -text flag)");
				return;
			} catch (IllegalArgumentException e) {
				System.err.println("-text flag error! Invalid file path provided!");
				return;
			}
		}

		if (argue.hasFlag("-query")) {
			Path fileQuery = argue.getPath("-query");
			try {
				query.processQueries(fileQuery, argue.hasFlag("-partial"));
			} catch (IOException e) {
				System.err.println("Error reading search query file. (File after -query flag)");
			} catch (NullPointerException e) {
				System.err.println("Error. No file path found after -query flag!");
			}
		}

		if (argue.hasFlag("-server")) {
			Server server = new Server(argue.getInteger("-port", 8080));
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			context.addServlet(new ServletHolder(new SearchServlet((ThreadedQueryProcessor) query)), "/welcome");
			context.addServlet(new ServletHolder(new CrawlerServlet(crawler)), "/add-url");
			context.addServlet(new ServletHolder(new IndexServlet((ThreadSafeIndex) index)), "/index");
			context.addServlet(new ServletHolder(new DownloadServlet((ThreadSafeIndex) index)), "/download");
			server.setHandler(context);
			try {
				server.start();
				System.out.println("Server started on port " + server.getURI().getPort());
				server.join();
			} catch (java.net.BindException e) {
				System.err.println("Error starting up server: Port already in use");
			} catch (java.net.SocketException e) {
				System.err.println("Error starting up server: Network socket error");
			} catch (java.lang.IllegalStateException e) {
				System.err.println("Error starting up server: Invalid server state");
			} catch (Exception e) {
				System.err.println("Error starting up server");
			} finally {
				try {
					server.stop();
					System.out.println("Server stopped");
				} catch (Exception e) {
					System.err.println("Error shutting down server");
				}
			}
		}
		if (queue != null) {
			queue.finish();
		}

		if (argue.hasFlag("-results")) {
			Path fileResults = argue.getPath("-results", Path.of("results.json"));
			try {
				query.writeResults(fileResults);
			} catch (IOException e) {
				System.err.println("Error writing to results file. (File after -results flag)");
			}
		}

		if (argue.hasFlag("-index")) {
			Path fileIndex = argue.getPath("-index", Path.of("index.json"));
			try {
				index.writeIndex(fileIndex);
			} catch (IOException e) {
				System.err.println("Error writing JSON to provided index file path. (Path after -index flag)");
			}
		}

		if (argue.hasFlag("-counts")) {
			Path fileCount = argue.getPath("-counts", Path.of("counts.json"));
			try {
				index.writeCounts(fileCount);
			} catch (IOException e) {
				System.err.println("Error writing JSON to provided counts file path. (Path after -counts flag)");
			}
		}
	}
}
