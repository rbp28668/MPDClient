/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

/**
 * @author bruce.porteous
 *
 */
public class MPDClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int errorNumber;
	private int commandListNum;
	private String command; 
	private String ackMessage;
	
	
	/**
	 * 
	 */
	public MPDClientException() {
	}

	/**
	 * @param message
	 */
	public MPDClientException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MPDClientException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MPDClientException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param errorNumber
	 * @param commandListNum
	 * @param command
	 * @param message
	 */
	public MPDClientException(int errorNumber, int commandListNum,
			String command, String message) {
		super(command + " : " + message);
		this.errorNumber = errorNumber;
		this.commandListNum = commandListNum;
		this.command = command;
		this.ackMessage = message;
	}

	/**
	 * @return the errorNumber
	 */
	public int getErrorNumber() {
		return errorNumber;
	}

	/**
	 * @return the commandListNum
	 */
	public int getCommandListNum() {
		return commandListNum;
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @return the ackMessage
	 */
	public String getAckMessage() {
		return ackMessage;
	}
	
	

}
