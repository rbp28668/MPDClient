/**
 * 
 */
package uk.co.alvagem.mpdclient.application;

/**
 * @author bruce.porteous
 *
 */
public class RadioChannel {

	private String name;
	private String url;
	
	/**
	 * 
	 */
	public RadioChannel() {
	}

	/**
	 * @param name
	 * @param url
	 */
	RadioChannel(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RadioChannel [name=");
		builder.append(name);
		builder.append(", url=");
		builder.append(url);
		builder.append("]");
		return builder.toString();
	}

	
}
