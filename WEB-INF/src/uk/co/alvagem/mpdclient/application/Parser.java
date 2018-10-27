/**
 * 
 */
package uk.co.alvagem.mpdclient.application;

import java.net.URL;
import java.net.URLConnection;

/**
 * Interface to be implemented by any parser for metadata files. 
 * @author bruce.porteous
 *
 */
public interface Parser {
	void parse(URLConnection connection) throws Exception;
	URL getTarget() throws Exception;
}