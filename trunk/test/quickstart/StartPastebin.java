/*
 * $Id$
 * $Revision$
 * $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package quickstart;

import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;

/**
 * Seperate startup class for people that want to run the examples directly.
 */
public class StartPastebin
{
    /**
     * Used for logging.
     */
    private static Log log = LogFactory.getLog(StartPastebin.class);

    /**
     * Construct.
     */
    StartPastebin()
    {
        super();
    }

    /**
     * Main function, starts the jetty server.
     *
     * @param args
     */
    public static void main(String[] args)
    {
/*
        try {
            Map argsWeb = new HashMap();
            argsWeb.put("webroot", "src/webapp/"); // or any other command line args, eg port
            Launcher.initLogger(argsWeb);
            Launcher winstone = new Launcher(argsWeb); // spawns threads, so your application doesn't block
            winstone.run();

            // before shutdown
            winstone.shutdown();
        } catch(IOException e) {
            e.printStackTrace();

        }
*/

        Server jettyServer = null;
		try
		{
			URL jettyConfig = new URL("file:test/jetty-config.xml");
			if (jettyConfig == null)
			{
				log.fatal("Unable to locate jetty-test-config.xml on the classpath");
			}
			jettyServer = new Server(jettyConfig);
			jettyServer.start();
		}
		catch (Exception e)
		{
			log.fatal("Could not start the Jetty server: " + e);
			if (jettyServer != null)
			{
				try
				{
					jettyServer.stop();
				}
				catch (InterruptedException e1)
				{
					log.fatal("Unable to stop the jetty server: " + e1);
				}
			}
		}


    }
}
