/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import uk.co.alvagem.mpdclient.application.Application;

/**
 * See http://www.sauronsoftware.it/projects/cron4j/
 * See http://www.sauronsoftware.it/projects/cron4j/api/it/sauronsoftware/cron4j/SchedulingPattern.html for scheduling pattern.
 * @author bruce.porteous
 *
 */
public class SchedulePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private SwingUI app;
	private ButtonPanel buttonPanel;
	private ScheduleListPanel scheduleListPanel;
	private Scheduler scheduler;
	
	/**
	 * @param con
	 */
	public SchedulePanel(SwingUI app) {
		this.app = app;
		
		scheduler = new Scheduler();
		scheduler.setDaemon(true);
		scheduler.start();

		setLayout(new BorderLayout());
		buttonPanel = new ButtonPanel(app);
		scheduleListPanel = new ScheduleListPanel();
		
		add(buttonPanel, BorderLayout.NORTH);
		add(scheduleListPanel, BorderLayout.SOUTH);
	}

	private class ButtonPanel extends JPanel{
		private static final long serialVersionUID = 1L;
			
		private JButton addButton;
		
		ButtonPanel(Application app) {
			
			addButton = new JButton("Add new Schedule");
			addButton.addActionListener( ae -> {
				addSchedule();
			});
			
			add(addButton);
		}
	}
	
	private void addSchedule() {
		ScheduleEntry entry = new ScheduleEntry();
		
		System.out.println("Add schedule");
		ScheduleItemDialog dlg = new ScheduleItemDialog(app.getMainFrame(),entry);
		dlg.setVisible(true);
		if(dlg.wasEdited()){
			scheduleListPanel.addScheduleEntry(entry);
		}
	}
	
	private class ScheduleListPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private ScheduleTableModel tableModel;
		private JTable table;
		
		ScheduleListPanel(){
			
			setLayout(new BorderLayout());

			tableModel = new ScheduleTableModel();
			table = new JTable(tableModel);
			table.setFillsViewportHeight(true);
			// If need mouse handler on table put here.
			JScrollPane scroll = new JScrollPane(table);
			add(scroll);

		}
		
		void addScheduleEntry(ScheduleEntry entry){
			tableModel.addScheduleEntry(entry);
		}
	}
	
	/**
	 * A table model based around an array of Song.
	 * @author bruce.porteous
	 *
	 */
	private class ScheduleTableModel extends AbstractTableModel implements TableModel {

		private static final long serialVersionUID = 1L;
		private List<ScheduleEntry> schedule = new LinkedList<ScheduleEntry>();

		ScheduleTableModel() {
		}

		
		
		/**
		 * @param entry
		 */
		public void addScheduleEntry(ScheduleEntry entry) {
			int row = schedule.size(); // will add to last row - this is its index.
			schedule.add(entry);
			fireTableRowsInserted(row,row);
		}



		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			return schedule.size();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return 2;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ScheduleEntry entry = schedule.get(rowIndex);
			
			switch(columnIndex){
			case 0: return entry.getSchedule();
			case 1: return entry.getName();
			default: return "???";
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int column) {
			String names[] = {"Schedule","Description"};
			return names[column];
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}
		
	}
	
	class ScheduleEntry implements Runnable {
		private String name;
		private String script;
		private String schedule;
		
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the script
		 */
		public String getScript() {
			return script;
		}
		/**
		 * @param script the script to set
		 */
		public void setScript(String script) {
			this.script = script;
		}
		/**
		 * @return the schedule
		 */
		public String getSchedule() {
			return schedule;
		}
		/**
		 * @param schedule the schedule to set
		 */
		public void setSchedule(String schedule) {
			this.schedule = schedule;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			// The UI thread owns the connection so run the script 
			// on the UI thread
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ScriptEngineManager engineManager = new ScriptEngineManager();
					ScriptEngine engine = engineManager.getEngineByName("nashorn");
					
					engine.put("con", app.getConnection());
					
					try {
						engine.eval(script);
					} catch (ScriptException e) {
						app.statusMessage(e.getMessage());
					}
				}
			});
		}
	}
	
	void test() {
		SchedulingPattern pattern = new SchedulingPattern("* * * * *");
	}

	
}
