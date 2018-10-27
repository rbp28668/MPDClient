/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

/**
 * @author bruce.porteous
 *
 */
public class Stats {

	private int artists; // number of artists
	private int songs; // number of albums
	private int uptime; // daemon uptime in seconds
	private int dbPlaytime; // sum of all song times in the db
	private long dbUpdate; // last db update in UNIX time
	private int playtime; // time length of music played
	/**
	 * @return the artists
	 */
	public int getArtists() {
		return artists;
	}
	/**
	 * @param artists the artists to set
	 */
	public void setArtists(int artists) {
		this.artists = artists;
	}
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
	 * @return the uptime
	 */
	public int getUptime() {
		return uptime;
	}
	/**
	 * @param uptime the uptime to set
	 */
	public void setUptime(int uptime) {
		this.uptime = uptime;
	}
	/**
	 * @return the dbPlaytime
	 */
	public int getDbPlaytime() {
		return dbPlaytime;
	}
	/**
	 * @param dbPlaytime the dbPlaytime to set
	 */
	public void setDbPlaytime(int dbPlaytime) {
		this.dbPlaytime = dbPlaytime;
	}
	/**
	 * @return the dbUpdate
	 */
	public long getDbUpdate() {
		return dbUpdate;
	}
	/**
	 * @param dbUpdate the dbUpdate to set
	 */
	public void setDbUpdate(long dbUpdate) {
		this.dbUpdate = dbUpdate;
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
		builder.append("Stats [artists=");
		builder.append(artists);
		builder.append(", songs=");
		builder.append(songs);
		builder.append(", uptime=");
		builder.append(uptime);
		builder.append(", dbPlaytime=");
		builder.append(dbPlaytime);
		builder.append(", dbUpdate=");
		builder.append(dbUpdate);
		builder.append(", playtime=");
		builder.append(playtime);
		builder.append("]");
		return builder.toString();
	}

}
