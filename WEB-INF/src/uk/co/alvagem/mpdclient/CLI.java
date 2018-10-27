/**
 * 
 */
package uk.co.alvagem.mpdclient;

import uk.co.alvagem.mpdclient.client.Connection;
import uk.co.alvagem.mpdclient.client.Song;
import uk.co.alvagem.mpdclient.client.Stats;
import uk.co.alvagem.mpdclient.client.Status;


/**
 * @author bruce.porteous
 *
 */
public class CLI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			Connection con = new Connection("192.168.0.120",6600);
			con.connect();
			
			Song current = con.currentsong();
			Stats stats = con.stats();
			Status status = con.status();

			System.out.println(current);
			System.out.println(stats);
			System.out.println(status);

			Song[] songs = con.playlistfind("Genre","Other");
			for(Song song : songs) {
				System.out.println(song);
			}
			
			
			con.disconnect();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
