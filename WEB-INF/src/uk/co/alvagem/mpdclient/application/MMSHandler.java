/**
 * 
 */
package uk.co.alvagem.mpdclient.application;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * @author bruce.porteous
 *
 */
public class MMSHandler {

	/**
	 * Disable instances
	 */
	private MMSHandler() {
	}

	/**
	 * Enable support for MMS URLs.  Call once only.
	 */
	public static void enableSupport(){
		// Make sure URL can handle mms:  protocol (albeit incompletely).
		URL.setURLStreamHandlerFactory(new MMSURLStreamHandlerFactory());
	}
	
	/**
	 * Handler factory to allow us to support mms URLs
	 * @author bruce.porteous
	 *
	 */
	static class MMSURLStreamHandlerFactory implements URLStreamHandlerFactory {
		
		@Override
	    public URLStreamHandler createURLStreamHandler(String protocol) { 
	        if ( protocol.equalsIgnoreCase("mms") ) 
	            return new MMSProtocolHandler(); 
	        else 
	            return null; 
	    } 
	}
	
	/**
	 * Minimal handler for MMS streams.  So minimal in fact that we can't open any of them.
	 * Handler just needs to exist for URL to be happy with a URL that includes the mms protocol.
	 * @author bruce.porteous
	 *
	 */
	static class MMSProtocolHandler extends URLStreamHandler {

		/* (non-Javadoc)
		 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
		 */
		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			return null;
		}
		
	}

}
