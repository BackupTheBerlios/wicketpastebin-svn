package com.mysticcoders.pastebin.web.resource;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysticcoders.pastebin.core.ImageService;
import com.mysticcoders.pastebin.dao.ImageEntryDAO;

import com.mysticcoders.pastebin.model.ImageEntry;
import com.mysticcoders.pastebin.web.PastebinApplication;

import org.apache.wicket.markup.html.DynamicWebResource;

import org.apache.wicket.util.time.Time;
import org.apache.wicket.util.value.ValueMap;

/**
 * A resource that provides images from the holding dir.  An image can either
 * be a full-size image that has been uploaded, or a thumbnail.
 *
 * @author pchapman
 */
public class ImageResource extends DynamicWebResource
{
	// CONSTANTS

	public static final Logger logger = LoggerFactory.getLogger(ImageResource.class);

	private static ImageService imageService = (ImageService)PastebinApplication.getInstance().getBean("imageService");
	
	private static final long serialVersionUID = 1L;

	// CONSTRUCTORS

	public ImageResource()
	{
		super();
	}

	public ImageResource(Locale local)
	{
		super(local);
	}

	// METHODS

	// MEMBERS

    /**
     * Loads the image entry by the Id stored in the parameters, or null if
     * no Id was provided.
     * @return The image entry.
     */
    private ImageEntry getImageEntry(ValueMap params)
    {
    	ImageEntry imageEntry = null;
    	try {
	    	if (params.containsKey("imageEntryId")) {
	    		ImageEntryDAO imageEntryDAO =
	    			(ImageEntryDAO) PastebinApplication.getInstance()
	    				.getBean("imageEntryDAO");
	    		imageEntry =
	    			(ImageEntry) imageEntryDAO.lookupImageEntry(
	    					new Long(params.getLong("imageEntryId"))
	    				);
		    	if (logger.isDebugEnabled()) {
		        	logger.debug("imageEntry.name:" + imageEntry.getName());
		        }
	    	}
	    	return imageEntry;
        } catch (Exception e) {
        	logger.error("Error retrieving the image entry.", e);
        	return null;
        }
    }

	@Override
	protected ResourceState getResourceState()
	{
		ValueMap params = getParameters();

    	ImageEntry imageEntry = getImageEntry(params);
    	if (imageEntry == null) {
    		return new ResourceState() {

                public byte[] getData() {
                    return new byte[0];
                }

                public String getContentType() {
                    return null;
                }
            };
    	}
		ImageResourceState state =
			new ImageResourceState(
					Time.valueOf(
							imageService.getLastModifyTime(imageEntry)
						)
			);
        if (imageEntry != null) {
            try {
            	if (isThumbnail(params)) {
            		state.setContentType("image/jpeg");
            		state.setData(imageService.getThumbnail(imageEntry));
            	} else {
            		state.setContentType(imageEntry.getContentType());
            		state.setData(imageService.getImage(imageEntry));
            	}
            } catch (Exception e) {
            	logger.error("Error parsing parameters", e);
            }
        }

		return state;
	}

	public boolean isThumbnail(ValueMap params)
	{
		return params.containsKey("thumbnail");
	}
	
	class ImageResourceState extends ResourceState
	{
		// CONSTRUCTORS
		
		ImageResourceState(Time lastModified)
		{
			super();
			this.lastModified = lastModified;
		}
		
		// MEMBERS
		
		private String contentType;
		@Override
		public String getContentType()
		{
			return contentType;
		}
		void setContentType(String contentType)
		{
			this.contentType = contentType;
		}

		private byte[] data;
		@Override
		public byte[] getData()
		{
			return data;
		}
		void setData(byte[] data)
		{
			this.data = data;
		}

		@Override
		public int getLength()
		{
			return data.length;
		}

		private Time lastModified;
		@Override
		public Time lastModifiedTime()
		{
			return lastModified;
		}
		
		// METHODS
	}
}
