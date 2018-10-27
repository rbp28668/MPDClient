/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;


/**
 * @author bruce.porteous
 *
 */
public class TableTest {

	private JFrame mainFrame;
	/**
	 * 
	 */
	public TableTest() {
		mainFrame = new JFrame("Table Test");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	
		TableModel model = new TestTableModel();
		JTable table = new JTable(model);
		JScrollPane scroll = new JScrollPane(table);
		
		JPanel tablePanel = new JPanel();
		System.out.println(tablePanel.getLayout().getClass().getName());
		tablePanel.setLayout(new BorderLayout());
		tablePanel.add(scroll,BorderLayout.CENTER);
		
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.add("Table", tablePanel);
		
		mainFrame.getContentPane().add(tabs, BorderLayout.CENTER);
//		mainFrame.getContentPane().add(scroll, BorderLayout.CENTER);
		
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TableTest();

	}

	private class TestTableModel implements TableModel {

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			return 20;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return 4;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int columnIndex) {
			return "Column " + columnIndex;
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
			return "Cell " + rowIndex + "," + columnIndex;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 */
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			throw new UnsupportedOperationException("Can't set value");
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
		 */
		@Override
		public void addTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
		 */
		@Override
		public void removeTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
