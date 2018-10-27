/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

/**
 * @author bruce.porteous
 *
 */
public class PlaylistLine {
	private int sequence;
	private String type;
	private String path;
	/**
	 * @param sequence
	 * @param type
	 * @param path
	 */
	PlaylistLine(int sequence, String type, String path) {
		super();
		this.sequence = sequence;
		this.type = type;
		this.path = path;
	}
	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	
}
