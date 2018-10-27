/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

/**
 * @author bruce.porteous
 *
 */
public class Output {

	 private int outputid; // ID of the output. May change between executions
	 private String outputname; // Name of the output. It can be any.
	 private boolean outputenabled; // Status of the output. 0 if disabled, 1 if enabled.
	/**
	 * @return the outputid
	 */
	public int getOutputid() {
		return outputid;
	}
	/**
	 * @param outputid the outputid to set
	 */
	public void setOutputid(int outputid) {
		this.outputid = outputid;
	}
	/**
	 * @return the outputname
	 */
	public String getOutputname() {
		return outputname;
	}
	/**
	 * @param outputname the outputname to set
	 */
	public void setOutputname(String outputname) {
		this.outputname = outputname;
	}
	/**
	 * @return the outputenabled
	 */
	public boolean isOutputenabled() {
		return outputenabled;
	}
	/**
	 * @param outputenabled the outputenabled to set
	 */
	public void setOutputenabled(boolean outputenabled) {
		this.outputenabled = outputenabled;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Output [outputid=");
		builder.append(outputid);
		builder.append(", outputname=");
		builder.append(outputname);
		builder.append(", outputenabled=");
		builder.append(outputenabled);
		builder.append("]");
		return builder.toString();
	}

}
