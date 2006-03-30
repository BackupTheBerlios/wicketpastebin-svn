package com.mysticcoders.pastebin.core;

import java.io.InputStream;
import java.io.IOException;
import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import com.mysticcoders.pastebin.model.ImageEntry;

/**
 * com.mysticcoders.imagebin.core.ImageService
 * -
 * A service that allows for the saving of image data.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Dec 6, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 *
 * @author pchapman
 */
public interface ImageService
{
    public byte[] getImage(ImageEntry imageEntry)
    	throws IOException;
    
    public boolean isImageAvailable(ImageEntry imageEntry);
    
    public Date getLastModifyTime(ImageEntry imageEntry);
    
    public byte[] getThumbnail(ImageEntry imageEntry)
    	throws IOException;

    @Transactional
	public void save(ImageEntry imageEntry, InputStream imageStream)
    	throws IOException;
}
