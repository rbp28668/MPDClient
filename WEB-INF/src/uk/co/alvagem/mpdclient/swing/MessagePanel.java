/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.AbstractListModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * @author bruce.porteous
 *
 */
public class MessagePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private DisplayPanel messages;
	private MessageModel model;
	
	public MessagePanel(){

		setLayout(new BorderLayout());
		setBorder(new TitledBorder("Messages"));
		model = new MessageModel();
		messages = new DisplayPanel(model);
		JScrollPane scroll = new JScrollPane(messages);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scroll, BorderLayout.CENTER);
	}
	
	/**
	 * @param message
	 */
	public void showMessage(String message) {
		model.add(message);
		messages.revalidate();
		repaint();
	}

	private static class MessageModel extends AbstractListModel<String> {
		
		private static final long serialVersionUID = 1L;
		private final int MAX = 100;
		private LinkedList<String> messages = new LinkedList<String>();
		
		void add(String msg){
			if(msg == null) {
				throw new NullPointerException("Can't add null message");
			}
			if(messages.size() >= MAX){
				messages.removeFirst();
			}
			messages.addLast(msg);
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.ListModel#getSize()
		 */
		@Override
		public int getSize() {
			return messages.size();
		}

		/* (non-Javadoc)
		 * @see javax.swing.ListModel#getElementAt(int)
		 */
		@Override
		public String getElementAt(int index) {
			return messages.get(index);
		}
		
		
	}
	
	private class DisplayPanel extends JPanel implements Scrollable {

		private static final long serialVersionUID = 1L;
		private MessageModel model;
		private int charHeight;
		private static final int MIN_LINES = 5;
		private static final int VERTICAL_BORDER = 5;
		private static final int HORIZONTAL_BORDER = 10;
		
		DisplayPanel(MessageModel model) {
			this.model = model;
			getDisplayParams();
		}
		
		private void getDisplayParams(){
			 GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			 GraphicsDevice defaultDevice = ge.getDefaultScreenDevice();
			 GraphicsConfiguration dc = defaultDevice.getDefaultConfiguration();
			 BufferedImage img = dc.createCompatibleImage(1,1);
			 Graphics2D g2D = img.createGraphics();
			 FontMetrics fm = g2D.getFontMetrics();
			 charHeight = fm.getHeight();
			 Dimension minSize = new Dimension(100 * charHeight, MIN_LINES * charHeight);
			 setMinimumSize(minSize);
			 setPreferredSize(minSize);
			 
			 g2D.dispose();
			 
//			 DisplayMode dm = defaultDevice.getDisplayMode();
//			 System.out.println("Resolution " + dm.getWidth() + " x " + dm.getHeight());
//			 System.out.println("Bits per pixel " + dm.getBitDepth());
//			 System.out.println("Refresh rate " + dm.getRefreshRate() + "Hz");
			 
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			Graphics2D g2d = (Graphics2D)g;
			int height = g2d.getFontMetrics().getHeight();
			
			int x = HORIZONTAL_BORDER;
			int y = height + VERTICAL_BORDER; 
			for(int i=model.getSize()-1; i>=0; --i){
				g2d.drawString(model.getElementAt(i), x, y);
				y += height;
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.JComponent#getPreferredSize()
		 */
		@Override
		public Dimension getPreferredSize() {
			Dimension minSize = getMinimumSize();
			Dimension size = new Dimension(minSize.width, 2* VERTICAL_BORDER + Math.max(MIN_LINES , model.getSize()) * charHeight);
			return size;
		}


		
		/* (non-Javadoc)
		 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
		 */
		@Override
		public Dimension getPreferredScrollableViewportSize() {
			Dimension minSize = getMinimumSize();
			Dimension size = new Dimension(minSize.width, VERTICAL_BORDER + MIN_LINES * charHeight);
			return size;
		}

		/* (non-Javadoc)
		 * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
		 */
		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect,
				int orientation, int direction) {
			return charHeight;
		}

		/* (non-Javadoc)
		 * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
		 */
		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect,
				int orientation, int direction) {
			return (orientation == SwingConstants.HORIZONTAL) ? visibleRect.width : charHeight * MIN_LINES;
		}

		/* (non-Javadoc)
		 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
		 */
		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		/* (non-Javadoc)
		 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
		 */
		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
		
		
	}

}
