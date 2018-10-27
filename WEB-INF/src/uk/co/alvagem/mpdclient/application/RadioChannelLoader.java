/**
 * 
 */
package uk.co.alvagem.mpdclient.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import uk.co.alvagem.mpdclient.client.MPDClientException;

/**
 * Simple parser to import a list of radio stations.
 * Blank lines, or lines that start with # are ignored.
 * Other lines should have the form name | url 
 * @author bruce.porteous
 *
 */
public class RadioChannelLoader {

	/**
	 * 
	 */
	public RadioChannelLoader() {
	}

	public RadioChannel[] loadFromHome() throws Exception {
		String home = System.getProperty("user.home");
		File path = new File(home, "stations.txt");
		return loadChannels(new FileInputStream(path));
	}
	
	public RadioChannel[] loadFromClasspath() throws Exception {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("stations.txt");
		return loadChannels(is);
	}
	
	public RadioChannel[] loadChannels(InputStream is) throws Exception {
		
		List<RadioChannel> channels = new LinkedList<RadioChannel>();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		try {
			
			String line;
			while((line = in.readLine()) != null) {
				line = line.trim();
				if(line.length() == 0 || line.startsWith("#")) {
					continue;	// blank or comment line.
				}
				
				int idx = line.indexOf("|");
				if(idx != -1) {
					String name = line.substring(0, idx).trim();
					String url = line.substring(idx+1).trim();
					RadioChannel channel = new RadioChannel(name, url);
					channels.add(channel);
				} else {
					throw new MPDClientException("Can't find | delimeter in radio channel import line: " + line);
				}
			}
		} finally {
			in.close();
		}
		return channels.toArray(new RadioChannel[channels.size()]);
	}
}
