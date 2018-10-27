/**
 * 
 */
package uk.co.alvagem.mpdclient.application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import uk.co.alvagem.mpdclient.client.MPDClientException;

/**
 * @author bruce.porteous
 *
 */
public class M3UParser implements Parser {

	public static final String CONTENT_TYPE="application/x-mpegurl";
	private String url = null;
	
	/**
	 * 
	 */
	public M3UParser() {
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.application.Parser#parse(java.net.URLConnection)
	 */
	@Override
	public void parse(URLConnection connection) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		try {
			boolean extended = false;
			String line;
			while((line = in.readLine()) != null) {
				line = line.trim();
				if(line.length() == 0) {
					continue;
				}
				
				if(line.equals("#EXTM3U")) {
					extended = true;
				} else if(line.startsWith("#EXTINF")){
					// #EXTINF:191,Artist Name - Track Title
					if(!extended) {
						throw new MPDClientException("#EXTINF without initial #EXTM3U");
					}
				} else if(line.startsWith("#")) {
					continue;	// a comment line
				} else { // it's just a file
					url = line;
				
				} 
			}
			
		} finally {
			in.close();
		}
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.application.Parser#getTarget()
	 */
	@Override
	public URL getTarget() throws Exception {
		return new URL(url);
	}

}
