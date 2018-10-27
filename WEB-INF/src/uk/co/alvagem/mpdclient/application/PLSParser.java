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
 * A slightly rudimentary PLS file parser.
 * Only expects to find 1 URL.  If there are multiple it will take the last.
 * Currently ignores Title and Length tags (silently).  Other unknown tags throw an exception.
 * @author bruce.porteous
 *
 */
public class PLSParser implements Parser {

	public static final String CONTENT_TYPE = "audio/x-scpls";
	
	private String url;
	/**
	 * 
	 */
	public PLSParser() {
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.application.Parser#parse(java.net.URLConnection)
	 */
	@Override
	public void parse(URLConnection connection) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		try {
			String line;
			int state = 0;
			int entries = 0;
			int index = 1;
			while((line = in.readLine()) != null){
				line = line.trim();

				// Skip blank lines.
				if(line.length() == 0){
					continue;
				}
				
				switch(state){
				case 0:  // Looking for [playlist]
					if(line.equals("[playlist]")){
						state = 1;
					} else {
						throw new MPDClientException("Expected PLS file to start with [playlist], found " + line);
					}
					break;
					
				case 1:	// Looking for NumberOfEntries = X
					if(line.startsWith("NumberOfEntries")) {
						int idx = line.indexOf("=");
						if(idx != -1) {
							String num = line.substring(idx + 1).trim();
							entries = Integer.parseInt(num);
						} else {
							throw new MPDClientException("Malformed NumberOfEntries line"); 
						}
						state = 2;
					} else {
						throw new MPDClientException("Missing NumberOfEntries field in PLS file");
					}
					break;
					
				case 2: // looking for one of:
					//FileX : Variable defining location of stream.
					//TitleX : Defines track title.
					//LengthX : Length in seconds of track. Value of -1 indicates indefinite (streaming).
					
					if(line.startsWith("File")) {
						int idx = line.indexOf("=");
						if(idx != -1) {
							url = line.substring(idx + 1).trim();
						} else {
							throw new MPDClientException("Malformed File line"); 
						}
						
					} else if(line.startsWith("Title")) {
						// NOP
					} else if(line.startsWith("Length")){
						// NOP
					} else if(line.startsWith("Version")){
						// NOP
					} else {
						throw new MPDClientException("Unexpected line in PLS file " + line);
					}
					break;
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
