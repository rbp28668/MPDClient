/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import uk.co.alvagem.mpdclient.client.Song;
import uk.co.alvagem.mpdclient.client.Status;

/**
 * @author bruce.porteous
 *
 */
public abstract class AbstractMPDEventListener implements MPDEventListener {

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.MPDEventListener#onDatabase()
	 */
	@Override
	public void onDatabase() throws Exception {
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.MPDEventListener#onUpdate()
	 */
	@Override
	public void onUpdate()  throws Exception{
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.MPDEventListener#onStoredPlaylist()
	 */
	@Override
	public void onStoredPlaylist()  throws Exception{
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.MPDEventListener#onPlaylist(uk.co.alvagem.mpdclient.client.Song[])
	 */
	@Override
	public void onPlaylist(Song[] playlist) throws Exception {
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.MPDEventListener#onPlayer(uk.co.alvagem.mpdclient.client.Status)
	 */
	@Override
	public void onPlayer(Status status) throws Exception {
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.MPDEventListener#onMixer(uk.co.alvagem.mpdclient.client.Status)
	 */
	@Override
	public void onMixer(Status status) throws Exception {
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.MPDEventListener#onOutput()
	 */
	@Override
	public void onOutput() throws Exception {
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.MPDEventListener#onOptions(uk.co.alvagem.mpdclient.client.Status)
	 */
	@Override
	public void onOptions(Status status) throws Exception {
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.MPDEventListener#onSticker()
	 */
	@Override
	public void onSticker() throws Exception {
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.MPDEventListener#onSubscription()
	 */
	@Override
	public void onSubscription() throws Exception {
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.MPDEventListener#onMessage(java.lang.String[])
	 */
	@Override
	public void onMessage(String[] messages) throws Exception {
	}

}
