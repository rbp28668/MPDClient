/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import uk.co.alvagem.mpdclient.application.Application;
import uk.co.alvagem.mpdclient.client.Song;

/**
 * @author bruce.porteous
 *
 */
public class SearchPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Application app;
	private TopPanel topPanel;
	private ResultsPanel resultsPanel;
	/**
	 * @param con
	 */
	public SearchPanel(Application app) {
		this.app = app;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		topPanel = new TopPanel();
		resultsPanel = new ResultsPanel();
		add(topPanel);
		add(resultsPanel);
	}


	private class TopPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private JTextField searchField;
		TopPanel() {
			add(new JLabel("Search:"));
			searchField = new JTextField(40);
			add(searchField);
			JButton go = new JButton("Go");
			go.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						app.statusMessage("Searching for " + searchField.getText());
						Song[] results = app.getConnection().search("any", searchField.getText());
						resultsPanel.setSearchResults(results);
					} catch (Exception e1) {
						app.statusMessage(e1.getMessage());
						e1.printStackTrace();
					}
					
				}
			});
			add(go);
		}
		
	}
	
	private class ResultsPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private SongTable table;
		
		ResultsPanel() {
			setLayout(new BorderLayout());
			table = new SongTable(new Song[0]);
			table.addActionButton("Add", getAddButton());
			JScrollPane scroll = new JScrollPane(table);
			add(scroll, BorderLayout.CENTER);
		}

		private JButton getAddButton(){
			JButton btn = new JButton("+");
			btn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					ResultsPanel.this.addSelectedSong(app);
				}
			});
			return btn;
		}

		/**
		 * @param app
		 * @return
		 */
		public void addSelectedSong(Application app) {
			try {
				Song song = table.getSelectedSong();
				app.getConnection().add(song.getFile());
				app.statusMessage("Added " + song.getTitle());
			} catch (Exception e1) {
				app.statusMessage(e1.getMessage());
				e1.printStackTrace();
			}
		}
		/**
		 * @param results
		 */
		public void setSearchResults(Song[] results) {
			table.setSongList(results);
		}
	}

}
