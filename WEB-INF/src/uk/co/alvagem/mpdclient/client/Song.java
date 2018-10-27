/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

import java.util.Date;

/**
 * @author bruce.porteous
 *file: vangelis/spiral/spiral.flac
Last-Modified: 2014-05-11T19:24:17Z
Time: 418
Artist: Vangelis
Track: 01
Album: Spiral
Title: Spiral
Genre: Rock
Date: 0
Pos: 72
Id: 119
OK

 */
public class Song implements NamedItem{

	private String file; // vangelis/spiral/spiral.flac
	private Date lastModified; // 2014-05-11T19:24:17Z
	private int time; // 418 - the time in seconds i.e. 6 min 58 secs
	private String artist; // Vangelis
	private int track; // 01
	private String album; // Spiral
	private String title; // Spiral
	private String genre; // Rock
	private Date date; // 0
	private int pos; // 72
	private int id; // 119
	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
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
	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}
	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}
	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}
	/**
	 * @return the track
	 */
	public int getTrack() {
		return track;
	}
	/**
	 * @param track the track to set
	 */
	public void setTrack(int track) {
		this.track = track;
	}
	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}
	/**
	 * @param album the album to set
	 */
	public void setAlbum(String album) {
		this.album = album;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the pos
	 */
	public int getPos() {
		return pos;
	}
	/**
	 * @param pos the pos to set
	 */
	public void setPos(int pos) {
		this.pos = pos;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.client.NamedItem#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		String value = getTitle();
		
		if(value == null) {
			if(file != null){
				int idx = file.lastIndexOf('/');
				if(idx != -1) {
					value = file.substring(idx+1);
				} else {
					value = file;
				}
			} 
		}
		
		if(value == null) {
			if(album != null) {
				value = album + " track " + getTrack();
			}
		}
		
		if(value == null) {
			value = "unknown";  // make sure client doesn't have to cope with NPE
		}
		
		return value;
	}
	
	
	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.client.NamedItem#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return "file";
	}
	
	
	
	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.client.NamedItem#getURI()
	 */
	@Override
	public String getURI() {
		return file;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Song [file=");
		builder.append(file);
		builder.append(", lastModified=");
		builder.append(lastModified);
		builder.append(", time=");
		builder.append(time);
		builder.append(", artist=");
		builder.append(artist);
		builder.append(", track=");
		builder.append(track);
		builder.append(", album=");
		builder.append(album);
		builder.append(", title=");
		builder.append(title);
		builder.append(", genre=");
		builder.append(genre);
		builder.append(", date=");
		builder.append(date);
		builder.append(", pos=");
		builder.append(pos);
		builder.append(", id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}

}
