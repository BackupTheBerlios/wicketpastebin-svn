package com.mysticcoders.pastebin.core;

import com.mysticcoders.pastebin.model.ImageEntry;
import com.mysticcoders.pastebin.util.IOUtils;

import com.mysticcoders.pastebin.dao.ImageEntryDAO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import java.util.Date;
import java.util.UUID;

import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.geom.AffineTransform;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * com.mysticcoders.pastebin.core.ImageServiceImpl
 * -
 * An implementation of ImageService.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Dec 6, 2005 This class was created by pchapman.</LI>
 * <LI>Dec 9, 2005 Refactored some of the logic from ImageResource into here,
 *                 where it belongs.  The logic moved to here includes
 *                 determining whether an image is available, obtaining an
 *                 image's data, obtaining thumbnail data and substituting a
 *                 not available image when necessary.
 * </UL></P>
 *
 * @author pchapman
 */
public class ImageServiceImpl implements ImageService
{
	// CONSTANTS
	
    private static final Log logger = LogFactory.getLog(ImageServiceImpl.class);
	
	// CONSTRUCTORS

	/**
	 * Creates a new instance.
	 */
	public ImageServiceImpl()
	{
		super();
	}
	
	// MEMBERS

	public byte[] getImage(ImageEntry imageEntry)
		throws IOException
	{
		if (isImageAvailable(imageEntry)) {
			// Open the file, then read it in.
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			InputStream inStream =
				new FileInputStream(new File(imageEntry.getFileName()));
			IOUtils.copy(inStream, outStream);
			inStream.close();
			outStream.close();
			return outStream.toByteArray();
		} else {
			return createNotAvailableImage(imageEntry.getContentType());
		}
	}
	
	public Date getLastModifyTime(ImageEntry imageEntry)
	{
		File f = new File(imageEntry.getFileName());
		return new Date(f.lastModified());
	}

	public boolean isImageAvailable(ImageEntry imageEntry)
	{
		return (
				new File(imageEntry.getFileName()).exists() &&
				new File(imageEntry.getThumbName()).exists()
			);
	}
	
    /* Spring Injected */
    private File imageDir;
    public void setImageDir(String dirName)
    {
    	imageDir = new File(dirName);
    }

    /* Spring Injected */
    private ImageEntryDAO imageEntryDAO;
    /** The DAO implementation that will be used internally. */
    public void setImageEntryDAO(ImageEntryDAO imageEntryDAO)
    {
        this.imageEntryDAO = imageEntryDAO;
    }

    public byte[] getThumbnail(ImageEntry imageEntry)
    	throws IOException
    {
		if (isImageAvailable(imageEntry)) {
			// Open the file, then read it in.
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			InputStream inStream =
				new FileInputStream(new File(imageEntry.getThumbName()));
			IOUtils.copy(inStream, outStream);
			inStream.close();
			outStream.close();
			return outStream.toByteArray();
		} else {
			byte[] imageData = createNotAvailableImage(imageEntry.getContentType());
			return scaleImage(imageData, getThumbnailSize());
		}
    }

    /* Spring Injected */
    private int thumbnailSize;
    public int getThumbnailSize()
    {
    	return this.thumbnailSize;
    }
    public void setThumbnailSize(int size)
    {
    	this.thumbnailSize = size;
    }
    
    // METHODS
    
    private File createImageFile(String suffix)
    {
    	UUID uuid = UUID.randomUUID();
    	File file = new File(imageDir, uuid.toString() + suffix);
    	if (logger.isDebugEnabled()) {
    		logger.debug("File " + file.getAbsolutePath() + " created.");
    	}
    	return file;
    }
    
    private byte[] createNotAvailableImage(String contentType)
    	throws IOException
    {
    	// Load the "Image Not Available"
    	// image from jar, then write it out.
		StringBuffer name = new StringBuffer("com/mysticcoders/resources/ImageNotAvailable.");
		if ("image/jpeg".equalsIgnoreCase(contentType)) {
			name.append("jpg");
		} else if ("image/png".equalsIgnoreCase(contentType)) {
			name.append("png");
		} else {
			name.append("gif");
		}
		URL url = getClass().getClassLoader().getResource(
				name.toString()
			);
        InputStream in = url.openStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(in, out);
        in.close();
        out.close();
        return out.toByteArray();
    }

	/** @see ImageService#save(ImageEntry) */
    public void save(ImageEntry imageEntry, InputStream imageStream)
    	throws IOException
    {
    	// Read in the image data.
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	IOUtils.copy(imageStream, baos);
    	baos.close();
    	byte[] imageData = baos.toByteArray();
    	baos = null;

    	// Get the image's suffix
    	String suffix = null;
    	if ("image/gif".equalsIgnoreCase(imageEntry.getContentType())) {
    		suffix = ".gif";
    	} else if ("image/jpeg".equalsIgnoreCase(imageEntry.getContentType())) {
    		suffix = ".jpeg";
    	}if ("image/png".equalsIgnoreCase(imageEntry.getContentType())) {
    		suffix = ".png";
    	}
    	
    	// Create a unique name for the file in the image directory and
    	// write the image data into it.
    	File newFile = createImageFile(suffix);
    	OutputStream outStream = new FileOutputStream(newFile);
    	outStream.write(imageData);
    	outStream.close();
    	imageEntry.setFileName(newFile.getAbsolutePath());

    	// Create a thumbnail
    	newFile = createImageFile(".jpeg");
    	byte[] thumbnailData = scaleImage(imageData, getThumbnailSize());
    	outStream = new FileOutputStream(newFile);
    	outStream.write(thumbnailData);
    	outStream.close();    	
    	imageEntry.setThumbName(newFile.getAbsolutePath());
    	
    	// Save the image info in the database
        imageEntryDAO.save(imageEntry);
    }
    
    private byte[] scaleImage(byte[] imageData, int maxSize)
    	throws IOException
    {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Scaling image...");
    	}
        // Get the image from a file.
		Image inImage = new ImageIcon(imageData).getImage();

		// Determine the scale.
		double scale = (double) maxSize / (double) inImage.getHeight(null);
		if (inImage.getWidth(null) > inImage.getHeight(null)) {
			scale = (double) maxSize / (double) inImage.getWidth(null);
		}

		// Determine size of new image.
		// One of the dimensions should equal maxSize.
		int scaledW = (int) (scale * inImage.getWidth(null));
		int scaledH = (int) (scale * inImage.getHeight(null));

		// Create an image buffer in
		// which to paint on.
		BufferedImage outImage = new BufferedImage(
				scaledW, scaledH, BufferedImage.TYPE_INT_RGB
			);

		// Set the scale.
		AffineTransform tx = new AffineTransform();

		// If the image is smaller than
		// the desired image size,
		// don't bother scaling.
		if (scale < 1.0d) {
			tx.scale(scale, scale);
		}

		// Paint image.
		Graphics2D g2d = outImage.createGraphics();
		g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON
			);
		g2d.drawImage(inImage, tx, null);
		g2d.dispose();

		// JPEG-encode the image
		// and write to file.
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		encoder.encode(outImage);
        os.close();
    	if (logger.isDebugEnabled()) {
    		logger.debug("Scaling done.");
    	}
        return os.toByteArray();
    }
}
