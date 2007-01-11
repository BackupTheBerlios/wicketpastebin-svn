package com.mysticcoders.pastebin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: Oct 23 2003
 * Time: 9:02:22 PM
 * @author Philip Chapman (pchapman@pcsw.us)
 * Date: Aug 4, 2006
 * Time: 9:48:00 PM
 */
public class IOUtils
{
	private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);
	
    /**
     * Copies data from src into dst.  Neither of the streams are opened or
     * closed.  The calling method should tend to that.
     */
    public static void copy(InputStream source, OutputStream destination)
    	throws IOException
    {
    	try {
	        // Transfer bytes from source to destination
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = source.read(buf)) > 0) {
	            destination.write(buf, 0, len);
	        }
	        source.close();
	        destination.close();
    	} catch (IOException ioe) {
    		logger.error("Error copying data from source to destination", ioe);
    		throw ioe;
    	}
    }

  public static byte[] readStream(InputStream in) throws IOException
  {
    byte[] buffer = new byte[1024];

    int bytesRead = 0;
    while(true)
    {
      int byteReadThisTurn = in.read(buffer, bytesRead, buffer.length - bytesRead);
      if(byteReadThisTurn < 0)
        break;

      bytesRead += byteReadThisTurn;

      if(bytesRead >= buffer.length - 256)
      {
        byte[] newBuffer = new byte[buffer.length * 2];
        System.arraycopy(buffer, 0, newBuffer, 0, bytesRead);
        buffer = newBuffer;
      }
    }

    if(buffer.length == bytesRead)
    {
      return buffer;
    }
    else
    {
      byte[] response = new byte[bytesRead];
      System.arraycopy(buffer, 0, response, 0, bytesRead);

      return response;
    }
  }

  public static byte[] readStream(InputStream in, int size) throws IOException
  {
    if(in == null) return null;
    if(size == 0) return new byte[0];
    int currentTotal = 0;
    int bytesRead;
    byte[] data = new byte[size];
    while(currentTotal < data.length && (bytesRead = in.read(data, currentTotal, data.length - currentTotal)) >= 0)
      currentTotal += bytesRead;

    in.close();
    return data;
  }
}
