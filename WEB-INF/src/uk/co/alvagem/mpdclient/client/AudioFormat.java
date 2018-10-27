/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

/**
 * @author bruce.porteous
 *
 */
public class AudioFormat {
	private int sampleRate;
	private int bitsPerSample;
	private int channels;
	/**
	 * @return the sampleRate
	 */
	public int getSampleRate() {
		return sampleRate;
	}
	/**
	 * @param sampleRate the sampleRate to set
	 */
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	/**
	 * @return the bitsPerSample
	 */
	public int getBitsPerSample() {
		return bitsPerSample;
	}
	/**
	 * @param bitsPerSample the bitsPerSample to set
	 */
	public void setBitsPerSample(int bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
	}
	/**
	 * @return the channels
	 */
	public int getChannels() {
		return channels;
	}
	/**
	 * @param channels the channels to set
	 */
	public void setChannels(int channels) {
		this.channels = channels;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AudioFormat [sampleRate=");
		builder.append(sampleRate);
		builder.append(", bitsPerSample=");
		builder.append(bitsPerSample);
		builder.append(", channels=");
		builder.append(channels);
		builder.append("]");
		return builder.toString();
	}
	
	
}
