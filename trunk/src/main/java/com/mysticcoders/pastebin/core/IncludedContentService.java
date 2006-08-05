package com.mysticcoders.pastebin.core;

/**
 * A service that will provide static content to be included in pages.  The
 * purpose of this is to allow the server admin to configure whether static
 * content such as advertising or logos are to be included in pastebin pages,
 * and what that content is.
 * 
 * @author pchapman
 */
public interface IncludedContentService
{
	/**
	 * A method that will return the static content to be provided in the
	 * header of the pages.
	 */
	public String getHeaderContent();
}
