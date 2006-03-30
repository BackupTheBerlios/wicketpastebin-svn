package com.mysticcoders.pastebin.dao;

import com.mysticcoders.pastebin.model.ImageEntry;

/**
 * com.mysticcoders.pastebin.dao.ImageEntryDAO
 * -
 * Interface to be implemented for storing uploaded image data.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Dec 6, 2005 This interface was created by Philip A. Chapman.</LI>
 * </UL></P>
 *
 * @author pchapman
 */
public interface ImageEntryDAO
{
	/**
	 * Loads an existing image's data.
	 * @param id The unique ID of the image's data.
	 * @return The image's data.
	 */
    public ImageEntry lookupImageEntry( Long id );

    /**
     * Saves the image's data.
     * @param pasteEntry The image's data that is to be saved.
     */
    public void save( ImageEntry pasteEntry );

    /**
     * Delete's the image's data.
     * @param pasteEntry The image data to delete.
     */
    public void delete( ImageEntry pasteEntry );
}
