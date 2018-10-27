/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

/**
 * @author bruce.porteous
 *
 */
public class CountResult {

	 private int songs; // 957
     private int playtime; // 215489
	/**
	 * @return the songs
	 */
	public int getSongs() {
		return songs;
	}
	/**
	 * @param songs the songs to set
	 */
	public void setSongs(int songs) {
		this.songs = songs;
	}
	/**
	 * @return the playtime
	 */
	public int getPlaytime() {
		return playtime;
	}
	/**
	 * @param playtime the playtime to set
	 */
	public void setPlaytime(int playtime) {
		this.playtime = playtime;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CountResult [songs=");
		builder.append(songs);
		builder.append(", playtime=");
		builder.append(playtime);
		builder.append("]");
		return builder.toString();
	}

}
