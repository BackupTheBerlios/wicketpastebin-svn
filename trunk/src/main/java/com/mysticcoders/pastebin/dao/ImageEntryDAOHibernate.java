package com.mysticcoders.pastebin.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mysticcoders.pastebin.model.ImageEntry;

/**
 * com.mysticcoders.pastebin.dao.ImageEntryDAOHibernate
 * -
 * An implementation of ImageEntryDAO that loads and stores uploaded image
 * data into a database using Hibernate.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Dec 6, 2005 This class was created by Philip A. Chapman.</LI>
 * </UL></P>
 *
 * @author pchapman
 */
public class ImageEntryDAOHibernate implements ImageEntryDAO
{
	// CONSTRUCTORS
	
	/**
	 * Creates a new intance of ImageEntryDAOHibernate.
	 */
	public ImageEntryDAOHibernate()
	{
		super();
	}
	
	// MEMBERS
	
	private SessionFactory factory;
	/** The hibernate session factory used to access the data */
	public Session getSession()
	{
		return factory.getCurrentSession();
	}
	/** The hibernate session factory used to access the data */
	public void setSessionFactory(SessionFactory factory)
	{
		this.factory=factory;
	}

	// METHODS
	
	/**
	 * @see ImageEntryDAO#lookupImageEntry(Long)
	 */
	public ImageEntry lookupImageEntry(Long id)
	{
        return (ImageEntry)getSession().load( ImageEntry.class, id );
    }

	/**
	 * @see ImageEntryDAO#save(ImageEntry)
	 */
    public void save(ImageEntry pasteEntry)
    {
        getSession().saveOrUpdate( pasteEntry );
    }

	/**
	 * @see ImageEntryDAO#delete(ImageEntry)
	 */
    public void delete(ImageEntry pasteEntry)
    {
        getSession().delete( pasteEntry );
    }
}
