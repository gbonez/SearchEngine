package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet to allow users to download the inverted index as a JSON file
 * 
 * @author Grayson
 */
public class DownloadServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * initalize index
	 */
	private ThreadSafeIndex index;

	/**
	 * @param current index to link the servlet to
	 */
	public DownloadServlet(ThreadSafeIndex current) {
		super();
		this.index = current;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String fileType = request.getParameter("type");
		fileType = fileType == null ? "json" : fileType;
		String fileName = "database." + fileType;
		Path filePath = Paths.get(fileName);
		try {
			if (fileType.equals("json")) {
				index.writeIndex(filePath);
			} else {
				throw new IllegalArgumentException("Unsupported file type: " + fileType);
			}
		} catch (IOException e) {
			throw new ServletException("Error writing index to file", e);
		}
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		Files.copy(filePath, response.getOutputStream());
		response.getOutputStream().flush();
	}
}
