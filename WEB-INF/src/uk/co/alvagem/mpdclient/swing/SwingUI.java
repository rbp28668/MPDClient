/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JFrame;

import uk.co.alvagem.mpdclient.application.Application;
import uk.co.alvagem.mpdclient.application.MMSHandler;
import uk.co.alvagem.mpdclient.client.Connection;

/**
 * @author bruce.porteous
 *
 */
public class SwingUI implements Application{

	private String host = "192.168.0.102"; //"localhost";
	private int port = 6600;
	private Connection con;
	private EventManager eventManager;
	private JFrame mainFrame;
	private StatusPanel statusPanel;
	private MainPanel mainPanel;
	private MessagePanel messagePanel;
	private ButtonPanel buttonPanel;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
			MMSHandler.enableSupport();  // allow mms: urls.
			
			SwingUI ui = new SwingUI();

			int idx = 0;
			while(idx < args.length){
				String arg = args[idx++];
				if(arg.equals("--host")) {
					idx = ui.parseHost(args,idx);
				} else if(arg.equals("--port")) {
					idx = ui.parsePort(args,idx);
				}
			}
			ui.build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @param idx
	 * @return
	 */
	private int parsePort(String[] args, int idx) {
		if(idx < args.length){
			port = Integer.parseInt(args[idx++]);
		} else {
			throw new IllegalArgumentException("Missing port parameter to --port");
		}
		return idx;
	}

	/**
	 * @param args
	 * @param idx
	 * @return
	 */
	private int parseHost(String[] args, int idx) {
		if(idx < args.length){
			host = args[idx++];
		} else {
			throw new IllegalArgumentException("Missing host parameter to --host");
		}
		return idx;
	}

	void build() throws Exception {
		con = new Connection(host, port);
		con.connect();
		
		eventManager = new EventManager(this, host, port);
		
		mainFrame = new JFrame("MPD Client");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		messagePanel = new MessagePanel();
		statusPanel = new StatusPanel(this);
		mainPanel = new MainPanel(this);
		buttonPanel = new ButtonPanel(this);
		
		mainFrame.getContentPane().add(statusPanel, BorderLayout.NORTH);
		mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainFrame.getContentPane().add(buttonPanel, BorderLayout.EAST);
		mainFrame.getContentPane().add(messagePanel, BorderLayout.SOUTH);
		
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.Application#getConnection()
	 */
	@Override
	public Connection getConnection() {
		return con;
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.Application#statusMessage(java.lang.String)
	 */
	@Override
	public void statusMessage(String message) {
		messagePanel.showMessage(message);
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.Application#addListener(uk.co.alvagem.mpdclient.swing.MPDEventListener)
	 */
	@Override
	public void addListener(MPDEventListener listener) {
		eventManager.addListener(listener);
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.mpdclient.swing.Application#removeListener(uk.co.alvagem.mpdclient.swing.MPDEventListener)
	 */
	@Override
	public void removeListener(MPDEventListener listener) {
		eventManager.removeListener(listener);
	}
	
	/**
	 * Get the main frame as a container for dialogs.
	 * @return
	 */
	Frame getMainFrame(){
		return mainFrame;
	}
}
