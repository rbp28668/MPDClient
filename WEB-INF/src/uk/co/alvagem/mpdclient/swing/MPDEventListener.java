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
public interface MPDEventListener {

	public void onDatabase() throws Exception; // the song database has been modified after update.
	public void onUpdate() throws Exception;   //  a database update has started or finished. If the database was modified during the update(); the database event is also emitted.
	public void onStoredPlaylist() throws Exception;   //  a stored playlist has been modified(); renamed(); created or deleted
	public void onPlaylist(Song[] playlist) throws Exception;   //  the current playlist has been modified
	public void onPlayer(Status status) throws Exception;   //  the player has been started(); stopped or seeked. Includes next track in playlist.
	public void onMixer(Status status) throws Exception;   //  the volume has been changed
	public void onOutput() throws Exception;   //  an audio output has been enabled or disabled
	public void onOptions(Status status) throws Exception;   //  options like repeat(); random(); crossfade(); replay gain
	public void onSticker() throws Exception;   //  the sticker database has been modified.
	public void onSubscription() throws Exception;   //  a client has subscribed or unsubscribed to a channel
	public void onMessage(String[] messages) throws Exception;   //  a message was received on a channel this client is subscribed to; this event is only emitted when the queue is empty

}
