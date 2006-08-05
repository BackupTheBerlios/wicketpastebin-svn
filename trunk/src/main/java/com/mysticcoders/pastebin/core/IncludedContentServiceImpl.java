package com.mysticcoders.pastebin.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mysticcoders.pastebin.util.IOUtils;

/**
 * An implementation of the IncludedContentService interface that will download
 * content from a given URL.  This service does not cache the content, but will
 * download it each time the content is asked for.
 * 
 * @author pchapman
 */
public class IncludedContentServiceImpl implements IncludedContentService
{
	private static Log logger =
		LogFactory.getLog(IncludedContentServiceImpl.class);

	/**
	 * Creates a new instance.
	 */
	public IncludedContentServiceImpl() {
		super();
	}
	
	private String headerContentUrlString;
	public void setHeaderContentUrl(String urlString) {
		this.headerContentUrlString = urlString;
	}

	/**
	 * @see com.mysticcoders.pastebin.core.IncludedContentService#getHeaderContent()
	 */
	public String getHeaderContent() {
		if (
				headerContentUrlString != null &&
				headerContentUrlString.length() > 0
			)
		{
			// Load the content from the url
			InputStream is = null;
			ByteArrayOutputStream os = null;
			try {
				URL url = new URL(headerContentUrlString);
				os = new ByteArrayOutputStream();
				is = new BufferedInputStream(url.openStream());
				IOUtils.copy(is, os);
				is.close();
				os.close();
				return os.toString();
			} catch (IOException ioe) {
				logger.error(ioe);
				if (is != null) {
					try {is.close();}catch (IOException io) {logger.error(io);}
				}
				if (os != null) {
					try {os.close();}catch (IOException io) {logger.error(io);}
				}
			}
		}
		return "";
	}
}
