package com.mysticcoders.pastebin.model;

import java.util.Date;
import java.io.Serializable;

/**
 * com.mysticcoders.pastebin.model.ImageEntry
 * -
 * Holds information related to a pasted image.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Dec 6, 2005 This class was created by Philip A. Chapman.</LI>
 * </UL></P>
 *
 * @author pchapman
 */
public class ImageEntry implements Serializable
{
    private static final long serialVersionUID = 1L;

    
    // CONSTRUCTORS

    /**
     * Constructs a new instance.
     */
    public ImageEntry()
    {
        super();
        setCreated(new Date());
        setId(null);
    }

    /**
     * Creates a new instance of ImageEntry.
     * @param parent The paste entry to which this image belongs.
     */

    // MEMBERS

    private Date created;
    public Date getCreated()
    {
        return created;
    }
    protected void setCreated(Date created)
    {
        this.created = created;
    }

    private String fileName;
    /** The name of the file in the temporary directory. */
    public String getFileName()
    {
        return fileName;
    }
    /** The name of the file in the temporary directory. */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    private Long id;
    /** The unique ID of the image in the database */
    public Long getId()
    {
        return id;
    }
    /** The unique ID of the image in the database */
    protected void setId(Long id)
    {
        this.id = id;
    }

    private String contentType;
    /** The type of file */
    public String getContentType()
    {
        return contentType;
    }
    /** The type of file */
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    private String name;
    /** The name of the image. */
    public String getName()
    {
        return name;
    }
    /** The name of the image. */
    public void setName(String name)
    {
        this.name = name;
    }

    private PasteEntry parent;
    /** The paste to which this image belongs. */
    public PasteEntry getParent()
    {
        return parent;
    }
    /** The paste to which this image belongs. */
    public void setParent(PasteEntry parent)
    {
        this.parent = parent;
    }

    private String thumbName;
    /** The name of the thumb file in the temporary directory. */
    public String getThumbName()
    {
        return thumbName;
    }
    /** The name of the thumb file in the temporary directory. */
    public void setThumbName(String thumbName)
    {
        this.thumbName = thumbName;
    }

    // METHODS
}
