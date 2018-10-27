/**
 * 
 */
package uk.co.alvagem.mpdclient.application;

import uk.co.alvagem.mpdclient.client.Connection;
import uk.co.alvagem.mpdclient.swing.MPDEventListener;

/**
 * @author bruce.porteous
 *
 */
public interface Application {

	public Connection getConnection();
	
	public void statusMessage(String message);
	
	public void addListener(MPDEventListener listener);
	
	public void removeListener(MPDEventListener listener);
}
