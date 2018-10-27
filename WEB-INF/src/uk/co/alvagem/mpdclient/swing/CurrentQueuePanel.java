/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.co.alvagem.mpdclient.application.Application;
import uk.co.alvagem.mpdclient.client.NamedItem;
import uk.co.alvagem.mpdclient.client.Song;

/**
 * @author bruce.porteous
 *
 */
public class CurrentQueuePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Application app;
	private SongTable table;
	
	/**
	 * @param con
	 */
	public CurrentQueuePanel(Application app) {
		assert(app != null);
		this.app = app;
		
		Song[] playlist= new Song[0];
		try {
			playlist = app.getConnection().playlistinfo();
		} catch (Exception e) {
			app.statusMessage(e.getMessage());
			e.printStackTrace();
		}

		setLayout(new BorderLayout());
		table = new SongTable(playlist);
		table.addActionButton("Delete", getDeleteButton());
		
		table.addMouseListener(new MouseAdapter(){

			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
        		try {
			       int row = table.rowAtPoint(e.getPoint());
			        int col = table.columnAtPoint(e.getPoint());
			        if (row >= 0 && col >= 0) {
			        	row = table.convertRowIndexToModel(row);
			        	col = table.convertColumnIndexToModel(col);
			        	
			        	if(col != 3) { // don't play when delete button selected
				        	Song song = table.getSelectedSong();
				        	
				        	if(song != null) {
				        		app.getConnection().playid(song.getId());
				        	}
				        }
			        }
			 
				} catch (Exception e1) {
					app.statusMessage(e1.getMessage());
					e1.printStackTrace();
				}
			}
			
		});
		
		final JScrollPane scroll = new JScrollPane(table);
		add(scroll, BorderLayout.CENTER);

		app.addListener(new Listener());
	}

	/**
	 * @return
	 */
	private JButton getDeleteButton(){
		JButton btn = new JButton("X");
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Song song = CurrentQueuePanel.this.table.getSelectedSong();
				if(song != null) {
					try {
						app.getConnection().deleteid(song.getId());
						app.statusMessage("Removed " + song.getTitle());
					} catch (Exception e1) {
						app.statusMessage(e1.getMessage());
						e1.printStackTrace();
					}
				}
			}
		});
		return btn;
	}
	
	/**
	 * @author bruce.porteous
	 *
	 */
	private class Listener extends AbstractMPDEventListener {

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.swing.AbstractMPDEventListener#onPlaylist(uk.co.alvagem.mpdclient.client.Song[])
		 */
		@Override
		public void onPlaylist(Song[] playlist) throws Exception {
			table.setSongList(playlist);
		}
		
	}
}
