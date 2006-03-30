package com.mysticcoders.pastebin.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: Oct 23 2003
 * Time: 9:02:22 PM
 */
public class IOUtils
{
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
