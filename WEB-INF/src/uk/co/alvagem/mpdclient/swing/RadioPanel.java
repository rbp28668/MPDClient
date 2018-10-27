/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import uk.co.alvagem.mpdclient.application.Application;
import uk.co.alvagem.mpdclient.application.RadioChannel;
import uk.co.alvagem.mpdclient.application.RadioChannelLoader;
import uk.co.alvagem.mpdclient.application.WebFetch;

/**
 * @author bruce.porteous
 *
 */
public class RadioPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AddPanel addPanel;
	private ChannelListPanel channelPanel;
	private Application app;
	
	/**
	 * @param con
	 */
	public RadioPanel(Application app) {
		assert(app != null);
		this.app = app;
		
		setLayout(new BorderLayout());
		addPanel = new AddPanel(app);
		channelPanel = new ChannelListPanel();
		
		add(addPanel, BorderLayout.NORTH);
		add(channelPanel, BorderLayout.SOUTH);
	}

	private void addStation(String url) {
		try {
			WebFetch wf = new WebFetch();
			URL target = wf.process(new URL(url));
			app.getConnection().add(target.toString());
		} catch (Exception e) {
			app.statusMessage("Unable to add station: " + e.getMessage());
		}
	}
	
	private class AddPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private JTextField url;
		private JButton addButton;
		
		AddPanel(Application app) {
			
			url = new JTextField(80);
			addButton = new JButton("Add to Queue");
			addButton.addActionListener( ae -> {
				addStation(url.getText());
			});
			
			add(new JLabel("URL:"));
			add(url);
			add(addButton);
		}
		
		
		
	}

	private class ChannelListPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private ChannelTableModel tableModel;
		private JTable table;
		
		ChannelListPanel() {
			setLayout(new BorderLayout());

			tableModel = new ChannelTableModel();
			table = new JTable(tableModel);
			table.setFillsViewportHeight(true);
			loadChannels();

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
				        	
			        		RadioChannel channel = tableModel.item(row);
			        		addStation(channel.getUrl());
				        }
				 
					} catch (Exception e1) {
						app.statusMessage(e1.getMessage());
						e1.printStackTrace();
					}
				}
				
			});

			
			JScrollPane scroll = new JScrollPane(table);
			add(scroll);
		}

		private void loadChannels() {
			RadioChannel[] channels = null;
			RadioChannelLoader loader = new RadioChannelLoader();

			try {
				channels = loader.loadFromHome();
			} catch(Exception e) {
				app.statusMessage("Unable to load radio stations from home: " + e.getMessage());
			}
			
			if(channels == null) {
				try {
					channels = loader.loadFromClasspath();
				} catch(Exception e) {
					app.statusMessage("Unable to load radio stations from classpath: " + e.getMessage());
				}
			}

			if(channels != null) {
				tableModel.setChannels(channels);
			}
			
		}
	}

	private class ChannelTableModel  extends AbstractTableModel implements TableModel {

		private static final long serialVersionUID = 1L;
		private RadioChannel[] channels = new RadioChannel[0];
		private String[] columnNames = {"Name","URL"};
		
		void setChannels(RadioChannel[] channels){
			this.channels = channels;
			fireTableDataChanged();
		}
		
		/**
		 * @param row
		 * @return
		 */
		public RadioChannel item(int row) {
			return channels[row];
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			return channels.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return 2;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex){
			case 0: return channels[rowIndex].getName();
			case 1: return channels[rowIndex].getUrl();
			}
			return null;
		}
	}

}
