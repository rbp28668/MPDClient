/**
 * 
 */
package uk.co.alvagem.mpdclient.intercept;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author bruce.porteous
 *
 */
public class Intercept {

	int listenPort = 6661;
	int forwardPort = 6660;
	String forwardHost = "localhost";
	/**
	 * 
	 */
	public Intercept(String[] args) {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Intercept intercept = new Intercept(args);
		intercept.run();
	}
	
	void run() {
		try {
			ServerSocket socket = new ServerSocket(listenPort);
			
			while(true) {
			
				// Wait for connection, when we have one, create a downstream connection.
				Socket upstream = socket.accept();
				Socket downstream = new Socket(forwardHost, forwardPort);
	
				Thread down = passMessages(upstream, downstream);
				Thread up = passMessages(downstream, upstream);
				
				up.join();
				down.join();
				
				downstream.close();
				upstream.close();
			}
			
			//socket.close();

		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	Thread passMessages(Socket upstream, Socket downstream) throws UnsupportedEncodingException, IOException {
		final BufferedReader upstreamInput = new BufferedReader(new InputStreamReader(upstream.getInputStream(),"UTF-8"));
		
		// Print Writer to auto-flush on a UTF-8 stream
		final Writer downstreamOutput = new OutputStreamWriter(downstream.getOutputStream(),"UTF-8");
		
		Thread runner = new Thread(new Runnable() {
			
			public void run() {
				try {
					while(upstream.isConnected() && downstream.isConnected()){
						String line = upstreamInput.readLine();
						System.out.println(line);
						downstreamOutput.write(line);
					}
					
					upstreamInput.close();
					downstreamOutput.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		},"Msg Transfer");
		runner.start();
		return runner;
			
	}

}
