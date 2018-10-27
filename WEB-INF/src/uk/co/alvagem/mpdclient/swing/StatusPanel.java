/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

import uk.co.alvagem.mpdclient.application.Application;
import uk.co.alvagem.mpdclient.client.Song;
import uk.co.alvagem.mpdclient.client.Status;

/**
 * Top - large text - current song
 * Next - albumn, artist
 * Next - Slider and mm:ss / mm:ss
 * @author bruce.porteous
 *
 */
public class StatusPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Application app;
	private JLabel song;
	private JLabel album;
	private JLabel artist;
	private JSlider progressSlider;
	private JLabel songTime;
	
	/**
	 * @param con 
	 * 
	 */
	public StatusPanel(Application app) throws Exception {
		assert(app != null);
		this.app = app;
		
		setBorder(new EmptyBorder(10,20,10,20));
		setLayout(new BorderLayout());
		
		Font songFont = new Font("Arial", Font.PLAIN, 20);
		song = new JLabel();
		song.setFont(songFont);
		add(song, BorderLayout.NORTH);
		
		Font aaFont = new Font("Arial", Font.PLAIN,14);
		
		album = new JLabel();
		album.setFont(aaFont);
		add(album, BorderLayout.WEST);
		
		artist = new JLabel();
		artist.setFont(aaFont);
		add(artist,BorderLayout.EAST);
		
		JPanel progressPanel = new JPanel();
		add(progressPanel, BorderLayout.SOUTH);
		progressSlider = new JSlider();
		progressPanel.add(progressSlider);
		songTime = new JLabel();
		songTime.setFont(aaFont);
		progressPanel.add(songTime);

		// Now set up enough to receive events:
		app.addListener(new Listener());
		
		Song currentSong = app.getConnection().currentsong();
		Status status = app.getConnection().status();
		
		setCurrentSong(currentSong, status);
	}

	/**
	 * @param currentSong
	 * @param status
	 */
	public void setCurrentSong(Song currentSong, Status status) {
		
		String currentTitle = currentSong.getTitle();
		String currentAlbum = currentSong.getAlbum();
		String currentArtist = currentSong.getArtist();

		if(currentAlbum == null){
			currentAlbum = "Unknown";
		}
		
		if(currentArtist == null) {
			currentArtist = "Unknown";
		}
		
		if(currentTitle == null){
			
			currentTitle = currentSong.getFile();
			URL url = null; 
			try {
				url = new URL(currentTitle);
			} catch (MalformedURLException e) {
				// fail silently with null url.
				System.out.println(e.getMessage());
			}
			if(url != null){
				currentAlbum = url.getPath();
			}
		}
		song.setText(currentTitle);
		album.setText(currentAlbum);
		artist.setText(currentArtist);

		int time = currentSong.getTime();
		int elapsed = (int)status.getElapsed();

		progressSlider.setMaximum(time);  // in seconds
		progressSlider.setValue(elapsed);
		
		int elapsedMins = elapsed / 60;
		int elapsedSecs = elapsed % 60;
		
		int timeMins = time / 60;
		int timeSecs = time % 60;
		
		songTime.setText(String.format("%d:%02d / %d:%02d", elapsedMins,elapsedSecs, timeMins, timeSecs));
		
	}
	
	private class Listener extends AbstractMPDEventListener {

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.swing.AbstractMPDEventListener#onPlayer(uk.co.alvagem.mpdclient.client.Status)
		 */
		@Override
		public void onPlayer(Status status) throws Exception {
			Song currentSong = app.getConnection().currentsong();
			setCurrentSong(currentSong, status);
			if(status.getError() != null){
				app.statusMessage(status.getError());
			}
		}
		
	}



}
