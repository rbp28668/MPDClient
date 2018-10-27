/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import uk.co.alvagem.mpdclient.application.Application;
import uk.co.alvagem.mpdclient.client.Directory;
import uk.co.alvagem.mpdclient.client.NamedItem;

/**
 * Top level list: "lsinfo "/""
 * Down one level: "lsinfo "amy_macdonald""
 * Down next level "lsinfo "amy_macdonald/a_curious_thing""
 * @author bruce.porteous
 *
 */
public class BrowseLibraryPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Application app;
	private BreadCrumbPanel breadCrumbPanel;
	private ResultsPanel resultsPanel;
	private Directory top = new Directory("/");
	
	/**
	 * @param con
	 */
	public BrowseLibraryPanel(Application app) {
		this.app = app;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		breadCrumbPanel = new BreadCrumbPanel();
		resultsPanel = new ResultsPanel();
		add(breadCrumbPanel);
		add(resultsPanel);
		
		loadURI(top);
	}

	void loadURI(NamedItem item){
		try {
			NamedItem[] data = app.getConnection().lsinfo(item.getURI()); 
			resultsPanel.setData(data);
			breadCrumbPanel.add(item);
		} catch (Exception e) {
			app.statusMessage(e.getMessage());
			e.printStackTrace();
		}
	}

	
	private  class BreadCrumbPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Stack<JButton> buttons = new Stack<JButton>();
		
		BreadCrumbPanel(){
			setLayout(new FlowLayout(FlowLayout.LEFT));
		}
		
		void add(NamedItem item){
			JButton btn = new JButton(item.getDisplayName());
			
			buttons.push(btn);
			
			btn.addActionListener( ae -> { 
				loadURI(item);
				while(buttons.peek() != ae.getSource()) {
					remove(buttons.pop());
				}
				});
			
			add(btn);
			BreadCrumbPanel.this.revalidate();
			BreadCrumbPanel.this.repaint();
		}
		
	}
	
	/**
	 * @author bruce.porteous
	 *
	 */
	private  class ResultsPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private ResultsTableModel tableModel;
		private JTable table;
		
		
		ResultsPanel(){
			setLayout(new BorderLayout());
			
			tableModel = new ResultsTableModel();
			
			table = new JTable(tableModel);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			table.setFillsViewportHeight(true);		
			table.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					int width = getParent().getSize().width;
					resizeTo(width);
				}
			});

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
				        	
			        		NamedItem value = tableModel.item(row);
			        		System.out.println("Selected " + value);
			        		
				        	if(col == 0 || col == 1){
				        		String type = value.getTypeName();
				        		if(type.equals("directory")){
				        			loadURI(value);
				        		} else if (type.equals("file")){
									app.getConnection().add(value.getURI());
									app.statusMessage("Added " + value.getTypeName() + " " + value.getDisplayName());
				        		} else if (type.equals("playlist")){
									app.getConnection().add(value.getURI());
									app.statusMessage("Added " + value.getTypeName() + " " + value.getDisplayName());
				        		}
				        	} else if (col == 2) {
								app.getConnection().add(value.getURI());
								app.statusMessage("Added " + value.getTypeName() + " " + value.getDisplayName());
				        	}
				        }
				 
					} catch (Exception e1) {
						app.statusMessage(e1.getMessage());
						e1.printStackTrace();
					}
				}
				
			});
			
			ButtonEditor buttonEditor = new ButtonEditor();
			table.setDefaultRenderer(JButton.class, buttonEditor);
			table.setDefaultEditor(JButton.class, buttonEditor);
			
			JScrollPane scroll = new JScrollPane(table);
			add(scroll, BorderLayout.CENTER);
		}
		
		private void resizeTo(int width) {
			width -= 20;
	        final int buttonWidth = 50;
			final int colWidth = (width - buttonWidth) / 1;
			TableColumn column = null;
			for (int i = 0; i<table.getColumnCount(); i++) {
			    column = table.getColumnModel().getColumn(i);
			    if (i == 2) {
			        column.setPreferredWidth(buttonWidth);
			    } else {
			        column.setPreferredWidth(colWidth);
			    }
			}
		}
		
		public void setData(NamedItem[] data){
			tableModel.setData(data);
		}

	}
	
	private class ResultsTableModel extends AbstractTableModel implements TableModel {

		private static final long serialVersionUID = 1L;
		private List<TableModelListener> listeners = new LinkedList<TableModelListener>();
		private NamedItem[] data = new NamedItem[0];
		private JButton add = new JButton("+");
		
		void setData(NamedItem[] data ){
			this.data = data;
			fireTableDataChanged();
		}
		
		NamedItem item(int row){
			return data[row];
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			return data.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return 3;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0: return "Type";
			case 1: return "Title";
			case 2: return "Add";
			default: return null;
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex) {
			case 0: return String.class;
			case 1: return String.class;
			case 2: return JButton.class;
			default: return null;
			}
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
			switch(columnIndex) {
			case 0: return data[rowIndex].getTypeName();
			case 1: return data[rowIndex].getDisplayName();
			case 2: return add;
			default: return null;
			}
			
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 */
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			throw new UnsupportedOperationException("Can't set value");
		}

		
	}
}
