/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import javax.swing.JTabbedPane;

import uk.co.alvagem.mpdclient.application.Application;

/**
 * @author bruce.porteous
 *
 */
public class MainPanel extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	private CurrentQueuePanel queuePanel;
	private BrowseLibraryPanel browsePanel;
	private SearchPanel searchPanel;
	private PlaylistsPanel playlistsPanel;
	private RadioPanel radioPanel;
	private SchedulePanel schedulePanel;
	
	/**
	 * @param con 
	 * 
	 */
	public MainPanel(SwingUI app) {
		assert(app != null);
		
		queuePanel = new CurrentQueuePanel(app);
		browsePanel = new BrowseLibraryPanel(app);
		searchPanel = new SearchPanel(app);
		playlistsPanel = new PlaylistsPanel(app);
		radioPanel = new RadioPanel(app);
		schedulePanel = new SchedulePanel(app);
		
		addTab("Queue", queuePanel);
		addTab("Browse", browsePanel);
		addTab("Search", searchPanel);
		addTab("Playlists", playlistsPanel);
		addTab("Radio", radioPanel);
		addTab("Schedule", schedulePanel);
	}


}
