/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

/**
 * @author bruce.porteous
 * volume: 0-100
repeat: 0 or 1
random: 0 or 1
single: [2] 0 or 1
consume: [2] 0 or 1
playlist: 31-bit unsigned integer, the playlist version number
playlistlength: integer, the length of the playlist
state: play, stop, or pause
song: playlist song number of the current song stopped on or playing
songid: playlist songid of the current song stopped on or playing
nextsong: [2] playlist song number of the next song to be played
nextsongid: [2] playlist songid of the next song to be played
time: total time elapsed (of current playing/paused song)
elapsed: [3] Total time elapsed within the current song, but with higher resolution.
bitrate: instantaneous bitrate in kbps
xfade: crossfade in seconds
mixrampdb: mixramp threshold in dB
mixrampdelay: mixrampdelay in seconds
audio: sampleRate:bits:channels
updating_db: job id
error: if there is an error, returns message here
 *
 */
public class Status {

	 private int volume; // 0-100
	 private boolean repeat; // 0 or 1
	 private boolean random; // 0 or 1
	 private boolean single; // [2] 0 or 1
	 private boolean consume; // [2] 0 or 1
	 private int playlist; // 31-bit unsigned integer, the playlist version number
	 private int playlistlength; // integer, the length of the playlist
	 private PlayState state; // play, stop, or pause
	 private int song; // playlist song number of the current song stopped on or playing
	 private int songid; // playlist songid of the current song stopped on or playing
	 private int nextsong; // [2] playlist song number of the next song to be played
	 private int nextsongid; // [2] playlist songid of the next song to be played
	 private float time; // total time elapsed (of current playing/paused song)
	 private float elapsed; // [3] Total time elapsed within the current song, but with higher resolution.
	 private int bitrate; // instantaneous bitrate in kbps
	 private int xfade; // crossfade in seconds
	 private float mixrampdb; // mixramp threshold in dB
	 private float mixrampdelay; // mixrampdelay in seconds
	 private AudioFormat audio; // sampleRate; //bits; //channels
	 private int updatingDb; // job id
	 private String error; // if there is an error, returns message here
	 
	 /**
	 * @return the volume
	 */
	public int getVolume() {
		return volume;
	}

	/**
	 * @param volume the volume to set
	 */
	public void setVolume(int volume) {
		this.volume = volume;
	}

	/**
	 * @return the repeat
	 */
	public boolean isRepeat() {
		return repeat;
	}

	/**
	 * @param repeat the repeat to set
	 */
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	/**
	 * @return the random
	 */
	public boolean isRandom() {
		return random;
	}

	/**
	 * @param random the random to set
	 */
	public void setRandom(boolean random) {
		this.random = random;
	}

	/**
	 * @return the single
	 */
	public boolean isSingle() {
		return single;
	}

	/**
	 * @param single the single to set
	 */
	public void setSingle(boolean single) {
		this.single = single;
	}

	/**
	 * @return the consume
	 */
	public boolean isConsume() {
		return consume;
	}

	/**
	 * @param consume the consume to set
	 */
	public void setConsume(boolean consume) {
		this.consume = consume;
	}

	/**
	 * @return the playlist
	 */
	public int getPlaylist() {
		return playlist;
	}

	/**
	 * @param playlist the playlist to set
	 */
	public void setPlaylist(int playlist) {
		this.playlist = playlist;
	}

	/**
	 * @return the playlistlength
	 */
	public int getPlaylistlength() {
		return playlistlength;
	}

	/**
	 * @param playlistlength the playlistlength to set
	 */
	public void setPlaylistlength(int playlistlength) {
		this.playlistlength = playlistlength;
	}

	/**
	 * @return the state
	 */
	public PlayState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(PlayState state) {
		this.state = state;
	}

	/**
	 * @return the song
	 */
	public int getSong() {
		return song;
	}

	/**
	 * @param song the song to set
	 */
	public void setSong(int song) {
		this.song = song;
	}

	/**
	 * @return the songid
	 */
	public int getSongid() {
		return songid;
	}

	/**
	 * @param songid the songid to set
	 */
	public void setSongid(int songid) {
		this.songid = songid;
	}

	/**
	 * @return the nextsong
	 */
	public int getNextsong() {
		return nextsong;
	}

	/**
	 * @param nextsong the nextsong to set
	 */
	public void setNextsong(int nextsong) {
		this.nextsong = nextsong;
	}

	/**
	 * @return the nextsongid
	 */
	public int getNextsongid() {
		return nextsongid;
	}

	/**
	 * @param nextsongid the nextsongid to set
	 */
	public void setNextsongid(int nextsongid) {
		this.nextsongid = nextsongid;
	}

	/**
	 * @return the time
	 */
	public float getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(float time) {
		this.time = time;
	}

	/**
	 * @return the elapsed
	 */
	public float getElapsed() {
		return elapsed;
	}

	/**
	 * @param elapsed the elapsed to set
	 */
	public void setElapsed(float elapsed) {
		this.elapsed = elapsed;
	}

	/**
	 * @return the bitrate
	 */
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * @param bitrate the bitrate to set
	 */
	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

	/**
	 * @return the xfade
	 */
	public int getXfade() {
		return xfade;
	}

	/**
	 * @param xfade the xfade to set
	 */
	public void setXfade(int xfade) {
		this.xfade = xfade;
	}

	/**
	 * @return the mixrampdb
	 */
	public float getMixrampdb() {
		return mixrampdb;
	}

	/**
	 * @param mixrampdb the mixrampdb to set
	 */
	public void setMixrampdb(float mixrampdb) {
		this.mixrampdb = mixrampdb;
	}

	/**
	 * @return the mixrampdelay
	 */
	public float getMixrampdelay() {
		return mixrampdelay;
	}

	/**
	 * @param mixrampdelay the mixrampdelay to set
	 */
	public void setMixrampdelay(float mixrampdelay) {
		this.mixrampdelay = mixrampdelay;
	}

	/**
	 * @return the audio
	 */
	public AudioFormat getAudio() {
		return audio;
	}

	/**
	 * @param audio the audio to set
	 */
	public void setAudio(AudioFormat audio) {
		this.audio = audio;
	}

	/**
	 * @return the updatingDb
	 */
	public int getUpdatingDb() {
		return updatingDb;
	}

	/**
	 * @param updatingDb the updatingDb to set
	 */
	public void setUpdatingDb(int updatingDb) {
		this.updatingDb = updatingDb;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Status [volume=");
		builder.append(volume);
		builder.append(", repeat=");
		builder.append(repeat);
		builder.append(", random=");
		builder.append(random);
		builder.append(", single=");
		builder.append(single);
		builder.append(", consume=");
		builder.append(consume);
		builder.append(", playlist=");
		builder.append(playlist);
		builder.append(", playlistlength=");
		builder.append(playlistlength);
		builder.append(", state=");
		builder.append(state);
		builder.append(", song=");
		builder.append(song);
		builder.append(", songid=");
		builder.append(songid);
		builder.append(", nextsong=");
		builder.append(nextsong);
		builder.append(", nextsongid=");
		builder.append(nextsongid);
		builder.append(", time=");
		builder.append(time);
		builder.append(", elapsed=");
		builder.append(elapsed);
		builder.append(", bitrate=");
		builder.append(bitrate);
		builder.append(", xfade=");
		builder.append(xfade);
		builder.append(", mixrampdb=");
		builder.append(mixrampdb);
		builder.append(", mixrampdelay=");
		builder.append(mixrampdelay);
		builder.append(", audio=");
		builder.append(audio);
		builder.append(", updatingDb=");
		builder.append(updatingDb);
		builder.append(", error=");
		builder.append(error);
		builder.append("]");
		return builder.toString();
	}

	
	 
}
