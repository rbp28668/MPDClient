/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import uk.co.alvagem.mpdclient.client.Song;
// http://stackoverflow.com/questions/4951560/jscrollpane-resizing-with-variable-sized-content
// http://tips4java.wordpress.com/2009/12/20/scrollable-panel/
public class SongTable extends JTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Song[] playlist;
	int editRow = -1;
	private SongTableModel tableModel;
	
	public SongTable(Song[] songList) {
		this.playlist = songList;
		tableModel = new SongTableModel();
		setModel(tableModel);

		// Need to set auto resize mode as it controls getScrollableTracksViewportWidth.
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		setFillsViewportHeight(true);
		
		ButtonEditor buttonEditor = new ButtonEditor();
		setDefaultRenderer(JButton.class, buttonEditor);
		setDefaultEditor(JButton.class, buttonEditor);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = getParent().getSize().width;
				resizeTo(width);
			}
		});
	}
	

	/**
	 * Gets the currently selected song.
	 * @return the selected song or null if none selected.
	 */
	public Song getSelectedSong() {
		int selected = getSelectedRow();
		Song song = (selected != -1) ? playlist[selected] : null;
		return song;
	}

	public void setSongList(Song[] songList) {
		this.playlist = songList;
		tableModel.fireTableDataChanged();
	}
	
	public void addActionButton(String title, JButton button){
		tableModel.addButton(title, button);
	}
	
	public void resizeTo(int width) {
		width -= 20;
        final int buttonWidth = 50;
		final int colWidth = (width - buttonWidth) / 3;
		TableColumn column = null;
		for (int i = 0; i<getColumnCount(); i++) {
		    column = getColumnModel().getColumn(i);
		    if (i == 3) {
		        column.setPreferredWidth(buttonWidth);
		    } else {
		        column.setPreferredWidth(colWidth);
		    }
		}
	}
	

	/**
	 * A table model based around an array of Song.
	 * @author bruce.porteous
	 *
	 */
	private class SongTableModel extends AbstractTableModel implements TableModel {

		private static final long serialVersionUID = 1L;
		private final int BASE_COLS = 3;
		private ArrayList<String> columnTitles = new ArrayList<String>(); 
		private ArrayList<JButton> actionButtons = new ArrayList<JButton>();

		SongTableModel() {
			columnTitles.add("Title");
			columnTitles.add("Album");
			columnTitles.add("Artist");
		}

		void addButton(String title, JButton button){
			columnTitles.add(title);
			actionButtons.add(button);
			fireTableStructureChanged();
		}
		
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			return playlist.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return columnTitles.size();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Song song = playlist[rowIndex];
			
			switch(columnIndex){
			case 0:
				String title = song.getTitle();
				if(title == null) {
					title = song.getFile();
				}
				return title;
			case 1: return song.getAlbum();
			case 2: return song.getArtist();
			default: return actionButtons.get(columnIndex-BASE_COLS);
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int column) {
			return columnTitles.get(column);
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return (columnIndex >= BASE_COLS) ? JButton.class : String.class;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex >= BASE_COLS;  // button
		}
		
	}

}