/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

import java.util.Date;

/**
 * @author bruce.porteous
 *
 */
public class Playlist implements NamedItem {

	 private String playlist; // test
     private Date lastModified; // 2014-05-22T07:46:14Z
	/**
	 * @return the playlist
	 */
	public String getPlaylist() {
		return playlist;
	}
	/**
	 * @param playlist the playlist to set
	 */
	public void setPlaylist(String playlist) {
		this.playlist = playlist;
	}
	/**
	 * @return the lastModified
	 */
	public Date getLastModified() {
		return lastModified;
	}
	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	
	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.client.NamedItem#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getPlaylist();
	}
	
	
	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.client.NamedItem#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return "playlist";
	}
	
	
	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.client.NamedItem#getURI()
	 */
	@Override
	public String getURI() {
		return playlist;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PlaylistInfo [playlist=");
		builder.append(playlist);
		builder.append(", lastModified=");
		builder.append(lastModified);
		builder.append("]");
		return builder.toString();
	}

     
}
