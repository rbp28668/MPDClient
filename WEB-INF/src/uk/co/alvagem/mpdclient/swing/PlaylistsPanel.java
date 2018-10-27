/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.co.alvagem.mpdclient.application.Application;
import uk.co.alvagem.mpdclient.client.Playlist;
import uk.co.alvagem.mpdclient.client.Song;


/**
 * This panel is all about being able to edit playlists.
 * Create a new playlist, 
 * View the contents of a playlist,
 * Clear the contents of a playlist,
 * Delete a playlist
 * Rename a playlist 
 * add and remove songs & directories to and from a playlist.  
 * Change the song order in a playlist.
 *  
 * @author bruce.porteous
 *
 */
public class PlaylistsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ManagePanel managePanel;
	private PlaylistPanel playlist;
	private Application app;
	
	/**
	 * @param con
	 */
	public PlaylistsPanel(Application app) {
		assert(app != null);
		this.app = app;
		
		setLayout(new BorderLayout());
		playlist = new PlaylistPanel();
		managePanel = new ManagePanel();
		add(managePanel, BorderLayout.NORTH);
		add(playlist, BorderLayout.CENTER);
		
	}

	private class ManagePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private JLabel currentPlaylistName;
		private JButton newPlaylist;
		private JButton openPlaylist;
		private JButton clearPlaylist;
		private JButton deletePlaylist;
		private JButton renamePlaylist;
		private JButton playPlaylist;
		
		ManagePanel() {
			currentPlaylistName = new JLabel("");
			newPlaylist = new JButton("New");
			openPlaylist = new JButton("Open");
			clearPlaylist = new JButton("Clear");
			deletePlaylist = new JButton("Delete");
			renamePlaylist = new JButton("Rename");
			playPlaylist = new JButton("Play");
			
			newPlaylist.addActionListener( ae -> {
				String name = JOptionPane.showInputDialog(this, "Name for new playlist?");
				if(name != null) {
					currentPlaylistName.setText(name);
					playlist.updatePlaylist(new Song[0]);
					enableButtons(true);
				}
			});
			
			openPlaylist.addActionListener( ae -> {
				try {
					Playlist[] playlists = app.getConnection().listplaylists();
					Playlist selected = (Playlist)JOptionPane.showInputDialog(this, "Select Playlist", "MPD Client", JOptionPane.QUESTION_MESSAGE, null, playlists, null);
					if(selected != null) {
						currentPlaylistName.setText(selected.getPlaylist());
						Song[] songs = app.getConnection(). listplaylistinfo(selected.getPlaylist());
						playlist.updatePlaylist(songs);
						enableButtons(true);
					}
				} catch (Exception e) {
					app.statusMessage(e.getMessage());
					e.printStackTrace();
				}
			});
			
			clearPlaylist.addActionListener( ae -> {
				try {
					if(JOptionPane.showConfirmDialog(this, "Clear Playlist?", "MPD Client", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						app.getConnection().playlistclear(currentPlaylistName.getText());
						playlist.updatePlaylist(new Song[0]);
					}
				} catch (Exception e) {
					app.statusMessage(e.getMessage());
					e.printStackTrace();
				}
				
			});
			
			deletePlaylist.addActionListener( ae -> {
				try {
					if(JOptionPane.showConfirmDialog(this, "Delete Playlist?", "MPD Client", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						app.getConnection().rm(currentPlaylistName.getText());
						enableButtons(false);
						currentPlaylistName.setText("");
					}
				} catch (Exception e) {
					app.statusMessage(e.getMessage());
					e.printStackTrace();
				}
			});
			
			renamePlaylist.addActionListener( ae -> {
				try {
					String newname = JOptionPane.showInputDialog(this, "New name for playlist?");
					if(newname != null) {
						app.getConnection().rename(currentPlaylistName.getText(), newname);
						currentPlaylistName.setText(newname);
					}
				} catch (Exception e) {
					app.statusMessage(e.getMessage());
					e.printStackTrace();
				}
			});
			
			playPlaylist.addActionListener( ae -> {
				try {
					app.getConnection().load(currentPlaylistName.getText());
				} catch (Exception e) {
					app.statusMessage(e.getMessage());
					e.printStackTrace();
				}
			});
			

			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

			JPanel namePanel = new JPanel();
			namePanel.setBorder(BorderFactory.createTitledBorder("Current Playlist"));
			namePanel.add(currentPlaylistName);
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(newPlaylist);
			buttonPanel.add(openPlaylist);
			buttonPanel.add(clearPlaylist);
			buttonPanel.add(deletePlaylist);
			buttonPanel.add(renamePlaylist);
			buttonPanel.add(playPlaylist);
			
			enableButtons(false); // as no current playlist.
			
			add(namePanel);
			add(buttonPanel);
		}
		
		private void enableButtons(boolean enabled) {
			clearPlaylist.setEnabled(enabled);
			deletePlaylist.setEnabled(enabled);
			renamePlaylist.setEnabled(enabled);
			playPlaylist.setEnabled(enabled);
		}
	}
	
	private class PlaylistPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private SongTable table;
		
		PlaylistPanel() {
			setLayout(new BorderLayout());

			table = new SongTable(new Song[0]);
			
			JScrollPane scroll = new JScrollPane(table);
			add(scroll, BorderLayout.CENTER);
		}
		
		private void updatePlaylist(Song[] songs) {
			table.setSongList(songs);
		}

	}
	
	
	
}
