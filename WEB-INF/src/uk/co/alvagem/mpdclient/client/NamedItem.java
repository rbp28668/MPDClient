/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

/**
 * @author bruce.porteous
 *
 */
public interface NamedItem {

	public String getDisplayName();
	
	public String getTypeName();

	/**
	 * @return
	 */
	public String getURI();
}
