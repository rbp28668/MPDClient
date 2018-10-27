/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

/**
 * @author bruce.porteous
 *
 */
public class Directory implements NamedItem {

	private String path;
	
	public Directory() {
	}
	
	/**
	 * 
	 */
	public Directory(String path) {
		this.path = path;
	}

	/**
	 * @return the path
	 */
	public String getDirectory() {
		return path;
	}

	
	/**
	 * @param path the path to set
	 */
	public void setDirectory(String path) {
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.client.NamedItem#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		if(path.length() == 1){
			return path;
		}
		
		int idx = path.lastIndexOf('/');
		if(idx != -1) {
			return path.substring(idx + 1);
		} else {
			return path;
		}
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.client.NamedItem#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return "directory";
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.client.NamedItem#getURI()
	 */
	@Override
	public String getURI() {
		return path;
	}
	

}
