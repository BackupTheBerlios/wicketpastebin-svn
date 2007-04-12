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
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

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
    StartPastebin() {
        super();
    }

    /**
     * Main function, starts the jetty server.
     *
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setPort(8080);
        server.setConnectors(new Connector[]{connector});

        WebAppContext ptabs = new WebAppContext();
        ptabs.setServer(server);
        ptabs.setContextPath("/pastebin");
        ptabs.setWar("src/main/webapp");

        server.addHandler(ptabs);

        try {
            server.start();
        } catch (Exception e) {
            log.fatal("Unable to start Jetty server", e);
        }
    }
}
