/**
 * 
 */
package uk.co.alvagem.mpdclient.application;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.co.alvagem.mpdclient.client.MPDClientException;

/**
 * Code to find the stream URL given a URL.  URLs typically point to an ASX, m3u or PLS file which contains the URL of the
 * actual stream so the file has to be fetched and parsed.
 * Note that if mms handling fails you've probably forgotten to call MMSHandler.enableSupport() !
 * @author bruce.porteous
 *
 */
public class WebFetch {

	/** Lookup of parsers by file extension e.g. .asx, .pls etc */
	private static Map<String, Parser> parsers = new HashMap<String,Parser>();
	
	/** lookup of parsers by content type e.g. video/x-ms-asf */
	private static Map<String, Parser> parsersByContent = new HashMap<String,Parser>();
	
	/** Content types of audio streams.  URLs that fetch any of these types should be return as is */
	private static Set<String> audioContent = new HashSet<String>();
	
	static {
		// Set up parsers 
		ASXParser asx = new ASXParser();
		parsers.put("asx", asx);
		parsersByContent.put(ASXParser.CONTENT_TYPE, asx);
		
		PLSParser pls = new PLSParser();
		parsers.put("pls", pls);
		parsersByContent.put(PLSParser.CONTENT_TYPE, pls);
		
		M3UParser m3u = new M3UParser();
		parsers.put("m3u", m3u);
		parsersByContent.put(M3UParser.CONTENT_TYPE, m3u);
		
		// Set up audio content types.  Used to identify audio streams from returned metadata.
		audioContent.add("audio/mpeg");
		audioContent.add("audio/x-mpegurl");
		audioContent.add("audio/aacp");
	}
	
	/**
	 * 
	 */
	public WebFetch() {
	}

	/**
	 * Recursively processes a URL until we come up with something that looks like a music stream URL.
	 * Initially it tries to use the protocol to pick out mms streams which it just returns.
	 * Assuming it's http then look at the path suffix to try to figure out what it is.
	 * Again, for music streams (such as mp3) just return the stream, otherwise try to parse it.
	 * If we can't find a file path then we try to fetch it anyway and look at the content type.
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public URL process(URL url) throws Exception {
		
		
		if(url.getProtocol().equalsIgnoreCase("mms")) {
			return url;
		} else if (url.getProtocol().equalsIgnoreCase("http")) {
			
			String path = url.getPath();
			int idx = path.lastIndexOf('.');
			if(idx != -1) {
				String suffix = path.substring(idx + 1);
				if(suffix.equalsIgnoreCase("mp3")){
					return url;
				} else {
					Parser parser = parsers.get(suffix);
					if(parser != null) {
						// if we've got a parser for this suffix should be safe to get the file to parse.
						URLConnection response = fetch(url);
						parser.parse(response);
						return process(parser.getTarget());
					} else {
						throw new MPDClientException("Don't know how to process " + suffix);
					}
				}
			} else { // Couldn't find a suffix like mp3, asx etc.
				// Try to fetch it anyway and look for content type.
				URLConnection response = fetch(url);
				String contentType = response.getHeaderField("Content-Type");
				if(contentType != null) {
					Parser parser = parsersByContent.get(contentType);
					if(parser != null) {
						parser.parse(response);
						return process(parser.getTarget());
					} else { // No parser for it - 
						if(audioContent.contains(contentType)) {
							return url;
						} else {
							throw new MPDClientException("Don't know how to process content type " + contentType);
						}
					}
				} else {
					throw new MPDClientException("Can't determine type of URL: " + url.toString());
				}
			}
		} else  {
			throw new MPDClientException("Unsupported protocol " + url.getProtocol());
		}
				
	}
	
	
	/**
	 * Opens a connection to the given URL.
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private URLConnection fetch(URL url) throws IOException {
		
		URLConnection con = url.openConnection();
		con.connect();
		
//		for(Map.Entry<String,List<String>> field : con.getHeaderFields().entrySet()) {
//			System.out.print(field.getKey() + " --> ");
//			for(String value : field.getValue()) {
//				System.out.print(" " + value);
//			}
//			System.out.println();
//		}
	
        return con;
	}
	
	
}
