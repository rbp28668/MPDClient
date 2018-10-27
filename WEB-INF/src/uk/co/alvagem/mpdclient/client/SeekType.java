/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

/**
 * Enumeration used for seekcur that allows an absolute seek or relative to the current position - forward and back.
 * @author bruce.porteous
 *
 */
public enum SeekType {
	
	absolute(""),
	forward("+"),
	back("-");

	private String prefix;

	private SeekType(String prefix){
		this.prefix = prefix;
	}
	
	String getPrefix() {
		return prefix;
	}
}
