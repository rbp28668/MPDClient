/**
 * 
 */
package uk.co.alvagem.mpdclient.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO - make this handle reconnections gracefully.

/*
 * General parsing notes:
 * objects  playlist, directory, file
attributes:
Last-Modified: 2012-09-13T06:35:57Z
Time: 561
Artist: Amy MacDonald
Track: 12
Album: A Curious Thing
Title: what happiness means to me
Genre: Pop-Folk
Date: 2010

By convention, objects start with lower case, attributes upper case.

 */
/**
 * 
 * @author bruce.porteous
 *
 */
public class Connection {

	private InetAddress host;
	private int port;
	
	private boolean connected = false;
	private Socket connection;
	private BufferedReader input;
	private Writer output; 
	
	static Map<String,Handler> fieldHandlers = new HashMap<String,Handler>();
	
	static Map<String,Class<?>> objectTypes = new HashMap<String, Class<?>>();
	
	static {
		putHandler(new StringFieldHandler());
		putHandler(new IntFieldHandler());
		putHandler(new LongFieldHandler());
		putHandler(new FloatFieldHandler());
		putHandler(new BooleanFieldHandler());
		putHandler(new DateFieldHandler());
		putHandler(new AudioFormatFieldHandler());
		putHandler(new PlayStateFieldHandler());
		
		objectTypes.put("file", Song.class);
		objectTypes.put("playlist", Playlist.class);
		objectTypes.put("directory", Directory.class);
		
	}
	
	private static void putHandler(Handler handler) {
		fieldHandlers.put(handler.getTypeName(), handler);
	}
	
	/**
	 * Allows other client code to get handlers.
	 * @param typeName is the class name we want the handler for.
	 * @return the corresponding handler or null if no matching one.
	 */
	static Handler getHandler(String typeName){
		return fieldHandlers.get(typeName);
	}
	
	public Connection(String host, int port) throws UnknownHostException{
		this.host = InetAddress.getByName(host);
		this.port = port;
	}
	
	public void connect() throws IOException{
		connection = new Socket(host,port);
		input = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
		// Print Writer to auto-flush on a UTF-8 stream
		output = new OutputStreamWriter(connection.getOutputStream(),"UTF-8");
		
		
		String response = input.readLine();
		if(response.startsWith("OK MPD")){  // full response is OK MPD version
			System.out.println(response);
			connected = true;
		}
	}
	
	/**
	 * Tries to close all the connections to MPD regardless of any failure.
	 */
	public void disconnect(){
		try {
			output.close();
		} catch (Exception e){
			System.out.println("Cannot close output" + e.getMessage());
		}
		output = null;
		
		try {
			input.close();
		} catch (Exception e){
			System.out.println("Cannot close input" + e.getMessage());
		}
		input = null;
		
		try {
			connection.close();
		} catch (Exception e){
			System.out.println("Cannot close socket" + e.getMessage());
		}
		connection = null;
			
		connected = false;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private String[] readResponse() throws Exception{
		
		String line;
		List<String> results = new LinkedList<String>();
		
		while((line = input.readLine()) != null) {
			System.out.println(line);
			
			if(line.equals("OK")) {
				return results.toArray(new String[results.size()]);
			}
			
			if(line.startsWith("ACK")){
				processAck(line); // Will throw a MPDClientException
				return null;
			}

			results.add(line);
		}
		throw new MPDClientException("No data received from MPD");
	}


	/**
	 * Parses an error line and throws a MPDClientException with
	 * the details.
	 * ACK [2@0] {status} wrong number of arguments for "status"
	 * @param line
	 */
	private void processAck(String line) throws MPDClientException {
		Pattern pattern = Pattern.compile("ACK \\[(\\d+)@(\\d+)\\] \\{([~}]*)\\} (.*)");
		Matcher matcher = pattern.matcher(line);
		if(matcher.matches()){
			int errorNumber = Integer.parseInt(matcher.group(1));
			int commandListNum = Integer.parseInt(matcher.group(2));
			String command = matcher.group(3);
			String message = matcher.group(4);
			throw new MPDClientException(errorNumber, commandListNum, command, message);
		} else {
			throw new MPDClientException(line);
		}
	}

	/**
	 * Call to get MPD's response when just expecting an OK (or ACK) and no other data.
	 * @throws Exception
	 */
	private void readSimpleResponse() throws Exception{
		String[] lines = readResponse();
		if(lines.length > 0) {
			throw new MPDClientException("Unexpected data from MPD");
		}
	}

	/**
	 * Reads a series of lines that should all start with a given prefix and returns them without the prefix and any leading/trailing spaces.
	 * without the prefix. 
	 * @param prefix
	 * @return the trimmed list.
	 * @throws Exception if there is a line that doesn't start with the given prefix.
	 */
	private String[] readResponseAndTrim(final String prefix) throws Exception{
		String[] lines = readResponse();
		
		for(int idx=0; idx < lines.length; ++idx){
			String line = lines[idx];
			if(line.startsWith(prefix)){
				lines[idx] = line.substring(prefix.length()).trim();
			} else {
				throw new MPDClientException("Expecting lines starting with " + prefix + " but found " + line);
			}
		}
		return lines;

	}

	/**
	 * Captures parameters for a single object as a key/value map.
	 * @return
	 * @throws IOException
	 */
	private Map<String,String> captureSingleResult() throws Exception{
		
		String[] lines = readResponse();
		Map<String,String> results = new HashMap<String, String>();
		
		for(String line : lines) {
		
			int idx = line.indexOf(':');
			if(idx != -1) {
				String key = tidyParamName(line.substring(0,idx));
				String value = line.substring(idx + 1).trim();
				results.put(key, value);
			}
		}
		return results;
	}

	
	/**
	 * Takes a parameter name returned by MPD and strips out any non-letter and makes
	 * it all lower case.  This provides a canonical name for matching with method
	 * names when initialising a return object.
	 * @param value is the parameter name.
	 * @return a lower case, tidied version.
	 */
	private String tidyParamName(String value){
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<value.length(); ++i){
			char ch = value.charAt(i);
			if(Character.isLetter(ch)) {
				builder.append(Character.toLowerCase(ch));
			}
		}
		return builder.toString();
	}

	/**
	 * @param target
	 * @throws Exception
	 */
	private void convert(Object target) throws Exception{
		Map<String,String> values = captureSingleResult();
		convert(target, values);
	}

	/**
	 * @param target
	 * @throws Exception
	 */
	private void convert(Object target, Map<String,String> values) throws Exception{
		
		Method[] methods = target.getClass().getDeclaredMethods();
		for(Method method : methods) {
			String name = method.getName();
			final String setter = "set";
			if(name.startsWith(setter)){
				name = name.substring(setter.length()).toLowerCase();
				String value = values.get(name);
				if(value != null) {
					Class<?>[] parameterTypes = method.getParameterTypes();
					if(parameterTypes.length != 1) {
						throw new IllegalArgumentException("Found put method " + method.getName() + " with multiple arguments");
					}
					Class<?> paramType = parameterTypes[0];
					Handler handler = fieldHandlers.get(paramType.getName());
					if(handler != null) {
						Object[] params = new Object[1];
						params[0] = handler.convert(value);
						
						try {
							method.invoke(target, handler.convert(value));
						} catch (Exception e) {
							throw new MPDClientException("Unable to set value using " + method.getName(), e);
						} 
					} else {
						System.out.println("No handler for " + paramType.getName());
					}
				} else {
					System.out.println("No value for tag: " + name);
				}
			}
		}
	}

	private void send(String cmd) throws IOException{
		
		// First, should be connected
		if(!connected) {
			throw new IOException("Not connected to MPD");
		}
		
		// Underlying socket may have been closed - network issues, MPD restart
		if(connection.isClosed()) {
			reconnect();
		}
		
		System.out.println("****** " + cmd + " *******");
		try {
			rawSend(cmd);
		} catch (java.net.SocketException e) {
			reconnect();
			rawSend(cmd);
		}
	}

	/**
	 * @throws IOException
	 */
	protected void reconnect() throws IOException {
		System.out.println("Reconnecting...");
		disconnect();
		connect();
	}

	/**
	 * @param cmd
	 * @throws IOException
	 */
	protected void rawSend(String cmd) throws IOException {
		output.write(cmd);
		output.write('\n');
		output.flush();
	}

	/**
	 * Splits a list of lines up into a list of objects.  It's assumed that the first field is always the
	 * start of a record - the rest are assumed to be in random order, possibly with missing fields (near enough
	 * for songs in a playlist anyway). 
	 * @param fields
	 * @param targetClass
	 * @return
	 * @throws Exception
	 * @Deprecated	
	 */
	private List<Object> readObjectList(String[] fields, Class<?> targetClass) throws Exception {
		
		String[] lines = readResponse();
		
		List<Object> objects = new LinkedList<Object>();
		
		String delimiter = fields[0] + ":";
		int pos = 0;
		Map<String,String> results = new HashMap<String, String>();
		while(pos < lines.length){
			
			int start = pos;
			if(!lines[start].startsWith(delimiter)){
				throw new MPDClientException("Expected record line to start with " + delimiter + " but found " + lines[start]);
			}
			
			int end = start + 1;
			while(end < lines.length) {
				String line = lines[end];
				if(line.startsWith(delimiter)){
					break;
				}
				++end;
			}
			
			results.clear();
			for(pos=start; pos<end; ++pos){
				String line = lines[pos];
				for(String field : fields) {
					if(line.startsWith(field + ":")) {
						String key = tidyParamName(field);
						String value = line.substring(field.length() + 1).trim();
						results.put(key, value);
						break; // found this field now.
					}
				}
			}
			Object target = targetClass.newInstance();
			convert(target,results);
			objects.add(target);
		}

		return objects;
	}

	/**
	 * @return
	 * @throws Exception
	 * @throws MPDClientException
	 */
	private Song[] readSongList() throws Exception, MPDClientException {
		List<Object> songs = readObjectList();
		return songs.toArray(new Song[songs.size()]);
	}
	

	/**
	 * Splits a list of lines up into a list of objects.  It's assumed that the first field is always the
	 * start of a record - the rest are assumed to be in random order, possibly with missing fields (near enough
	 * for songs in a playlist anyway). 
	 * @param fields
	 * @param targetClass
	 * @return
	 * @throws Exception
	 */
	private List<Object> readObjectList() throws Exception {
		
		String[] lines = readResponse();
		
		List<Object> objects = new LinkedList<Object>();

		Map<String,String> results = new HashMap<String, String>();

		Object currentObject = null;

		for(int pos = 0; pos < lines.length; ++pos) {
		
			String tag;
			String value;
			int idx = lines[pos].indexOf(':');
			if(idx != -1){
				tag = lines[pos].substring(0,idx);
				value = lines[pos].substring(idx+1).trim();
			} else {
				throw new MPDClientException("Missing delimiter (:) in line " + lines[pos]);
			}
			
			Class<?> objectClass = objectTypes.get(tag);
			if(objectClass != null) {  // it's a new object.
				if(currentObject != null) {
					convert(currentObject,results);
					objects.add(currentObject);
				}
				currentObject = objectClass.newInstance();
				results.clear();
			} 

			// Note - object type still has a parameter (usually name or URI)
			String key = tidyParamName(tag);
			results.put(key, value);
		}

		// Any last object.
		if(currentObject != null) {
			convert(currentObject,results);
			objects.add(currentObject);
		}

		return objects;
	}

	/*=================================================================================================
	 * Querying MPD's status
	 ================================================================================================*/

	/**
	 * Clears the current error message in status (this is also accomplished by any command that starts playback).
	 * @throws Exception
	 */
	public void clearerror() throws Exception{
		send("clearerror");
		readSimpleResponse();
	}

	/**
	 * Displays the song info of the current song (same song that is identified in status).
	 * @return
	 * @throws Exception
	 */
	public Song currentsong() throws Exception{
		send("currentsong");
		Song current = new Song();
		convert(current);
		return current;
	}
	
	/**
	 * idle
	 * [1] Waits until there is a noteworthy change in one or more of MPD's subsystems. As soon as there is one, it lists all changed systems in a line in the format changed: SUBSYSTEM, where SUBSYSTEM is one of the following:
	 * database: the song database has been modified after update.
	 * update: a database update has started or finished. If the database was modified during the update, the database event is also emitted.
	 * stored_playlist: a stored playlist has been modified, renamed, created or deleted
	 * playlist: the current playlist has been modified
	 * player: the player has been started, stopped or seeked
	 * mixer: the volume has been changed
	 * output: an audio output has been enabled or disabled
	 * options: options like repeat, random, crossfade, replay gain
	 * sticker: the sticker database has been modified.
	 * subscription: a client has subscribed or unsubscribed to a channel
	 * message: a message was received on a channel this client is subscribed to; this event is only emitted when the queue is empty
	 * While a client is waiting for idle results, the server disables timeouts, allowing a client to wait for events as long as mpd runs. The idle command can be canceled by sending the command noidle (no other commands are allowed). MPD will then leave idle mode and print results immediately; might be empty at this time.
	 * If the optional SUBSYSTEMS argument is used, MPD will only send notifications when something changed in one of the specified subsytems.
	 * Sample conversation:
	 * OK MPD 0.16.0
     * idle
     * changed: player
     * OK
     * idle
	 * changed: playlist
     * changed: player
     * OK
	 * @param subsystems
	 */
	public Subsystem[] idle(Subsystem... subsystems) throws Exception {
		
		StringBuilder msg = new StringBuilder("idle");
		if(subsystems != null){
			boolean isFirst = true;
			for(Subsystem subsystem : subsystems){
				msg.append( isFirst ? ' ' : ',');
				msg.append(subsystem.toString());
				isFirst = false;
			}
		}
		send(msg.toString());
		
		String[] lines = readResponse();
		
		Subsystem[] result = new Subsystem[lines.length];
		final String prefix = "changed: ";
		int idx = 0;
		for(String line : lines){
			if(line.startsWith(prefix)){
				line = line.substring(prefix.length()).trim();
				result[idx++] = Subsystem.valueOf(line);
			}
		}
		return result; 
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public Status status() throws Exception{
		send("status");
		Status status = new Status();
		convert(status);
		return status;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public Stats stats() throws Exception{
		send("stats");
		Stats stats = new Stats();
		convert(stats);
		return stats;
	}
	
	/*=================================================================================================
	 * PLAYBACK OPTIONS
	 ================================================================================================*/
	/**
	 * Sets consume state to STATE, STATE should be 0 or 1. 
	 * When consume is activated, each song played is removed from playlist.
	 * @param state
	 */
	public void consume(boolean state) throws Exception{
		send("consume " + ((state)?"1":"0"));
		readSimpleResponse();
	}

	

	/**
	 * 	Sets crossfading between songs.
	 * @param seconds
	 */
	public void crossfade(float seconds) throws Exception{
		send("crossfade" + seconds);
		readSimpleResponse();
	}


	/**
	 * 	Sets the threshold at which songs will be overlapped. Like crossfading but doesn't 
	 * fade the track volume, just overlaps. The songs need to have MixRamp tags added by 
	 * an external tool. 0dB is the normalized maximum volume so use negative values, 
	 * I prefer -17dB. In the absence of mixramp tags crossfading will be used.
	 *  See http://sourceforge.net/projects/mixramp
	 * @param deciBels
	 */
	public void mixrampdb(float deciBels) throws Exception{
		send("mixrampdb" + deciBels);
		readSimpleResponse();
	}


	/**
	 * 	Additional time subtracted from the overlap calculated by mixrampdb. 
	 * A value of "nan" disables MixRamp overlapping and falls back to crossfading.
	 * @param seconds
	 */
	public void mixrampdelay(float seconds) throws Exception{
		send("mixrampdelay" + seconds);
		readSimpleResponse();
	}


	/**
	 * 	Sets random state to STATE, STATE should be 0 or 1.
	 * @param state
	 */
	public void random(boolean state) throws Exception{
		send("random " + ((state)?"1":"0"));
		readSimpleResponse();
	}


	/**
	 * 	Sets repeat state to STATE, STATE should be 0 or 1.
	 * @param state
	 */
	public void repeat(boolean state) throws Exception{
		send("repeat " + ((state)?"1":"0"));
		readSimpleResponse();
	}


	/**
	 * 	Sets volume to VOL, the range of volume is 0-100.
	 * @param vol
	 */
	public void setvol(int vol) throws Exception{ 
		if(vol < 0 || vol > 100){
			throw new IllegalArgumentException("vol must be in the range 0 - 100");
		}
		send("setvol " + vol);
		readSimpleResponse();
	}
	
 
	

	/**
	 * Sets single state to STATE, STATE should be 0 or 1. When single is activated, 
	 * playback is stopped after current song, or song is repeated if the 'repeat' mode is enabled.
	 * @param state
	 */
	public void single(boolean state) throws Exception{
		send("single " + ((state)?"1":"0"));
		readSimpleResponse();
	}


	/**
	 * Sets the replay gain mode. One of off, track, album, auto[4].
	 * Changing the mode during playback may take several seconds, because the new settings does not affect the buffered data.
	 * This command triggers the options idle event.
	 * @param mode
	 */
	public void replayGainMode(ReplayGainMode mode) throws Exception{
		send("replay_gain_mode " + mode);
		readSimpleResponse();
	}


	/**
	 * 	Prints replay gain options. Currently, only the variable replay_gain_mode is returned.
	 * @return
	 */
	public ReplayGainMode replayGainStatus() throws Exception{
		//replay_gain_mode: off
        //OK
		Map<String,String> values = captureSingleResult();
		String value = values.get("replay_gain_mode");
		if(value == null) {
			throw new MPDClientException("Missing replay_gain_mode value in replayGainStatus response");
		}
        return ReplayGainMode.valueOf(value);
	}


	/**
	 * 	Changes volume by amount CHANGE.
	 * Note
	 * volume is deprecated, use setvol instead.
	 * @param change
	 */
//	public void volume(int change) {
//		
//	}

	/*=================================================================================================
	 * Controlling playback
	 ================================================================================================*/

	/**
	 * Plays next song in the playlist.
	 */
	public void next() throws Exception{
		send("next");
		readSimpleResponse();
	}

	


	/**
	* pause {PAUSE}
	* Toggles pause/resumes playing, PAUSE is 0 or 1.
	* Note
	*  The use of pause command w/o the PAUSE argument is deprecated.
	 * @param pause
	 */
	public void pause(boolean pause) throws Exception{
		send("pause " + (pause ? "1" : 0));
		readSimpleResponse();
	}

	/**
	* play [SONGPOS]
	* Begins playing the playlist at song number SONGPOS.
	 * @param songpos
	 */
	public void play() throws Exception{
		send("play");
		readSimpleResponse();
	}

	/**
	* play [SONGPOS]
	* Begins playing the playlist at song number SONGPOS.
	 * @param songpos
	 */
	public void play(int songpos) throws Exception{
		send("play " + songpos);
		readSimpleResponse();
	}

	

	/**
	* playid [SONGID]
	* Begins playing the playlist at song SONGID.
	 * @param songid
	 */
	public void playid(int songid) throws Exception{
		send("playid " + songid);
		readSimpleResponse();
	}

	/**
	 * Plays previous song in the playlist.
	 */
	public void previous() throws Exception{
		send("previous");
		readSimpleResponse();
	}


	/**
	 * seek {SONGPOS} {TIME}
	 * Seeks to the position TIME (in seconds) of entry SONGPOS in the playlist.
	 * @param songpos
	 * @param time
	 */
	public void seek(int songpos, int time) throws Exception{
		send("seek " + songpos + " " + time);
		readSimpleResponse();
	}
	
	/**
	 * seekid {SONGID} {TIME}
	 * Seeks to the position TIME (in seconds) of song SONGID.
	 * @param songid
	 * @param time
	 */
	public void seekid(int songid, int time) throws Exception{
		send("seekid " + songid + " " + time);
		readSimpleResponse();
	}

	
	/**
	 * seekcur {TIME}
	 * Seeks to the position TIME within the current song. If prefixed by '+' or '-', then the time is relative to the current playing position.
	 * @param time
	 */
	public void seekcur(int time, SeekType seek) throws Exception{
		send("seekcur " + seek.getPrefix() + time);
		readSimpleResponse();

	}
	
	/**
	 * stop
	 * Stops playing.
	 */
	public void stop()  throws Exception{
		send("stop");
		readSimpleResponse();
	}


 	/*=================================================================================================
	 * The current playlist
	 ================================================================================================*/


	/**
	 * add {URI}
     * Adds the file URI to the playlist (directories add recursively). URI can also be a single file.
	 * @param uri
	 * @throws Exception
	 */
	public void add(String uri)  throws Exception{
		send("add " + uri);
		readSimpleResponse();
	}

	/**
	 * addid {URI} [POSITION]
	 * Adds a song to the playlist (non-recursive) and returns the song id.
	 * URI is always a single file or URL. For example:
	 * addid "foo.mp3"
	 * Id: 999
	 * OK
	 * @param uri
	 * @param position
	 * @return
	 */
	public int addid(String uri, int position) throws Exception{
		String cmd = "addid " + uri + ( position != -1 ? " " + position : "");
		send(cmd);
		Map<String,String> result = captureSingleResult();
		String value = result.get("id");
		if(value == null) {
			throw new MPDClientException("Missing Id value from addid");
		}
		return Integer.parseInt(value);
	}

	/**
	 * clear
	 * Clears the current playlist.
	 * @throws Exception
	 */
	public void clear()  throws Exception{
		send("clear");
		readSimpleResponse();
	}


	/**
	 * Deletes a song from the playlist.
	 * delete [{POS} | {START:END}]
	 * @param pos
	 * @throws Exception
	 */
	public void delete(int pos) throws Exception {
		if(pos < 0) {
			throw new IllegalArgumentException("Position cannot be less than 0");
		}
		send("delete " + pos);
		readSimpleResponse();
	}
	
	/**
	 * Deletes a range of songs from the playlist.
	 * delete [{POS} | {START:END}]
	 * @param start
	 * @param end
	 * @throws Exception
	 */
	public void delete(int start, int end) throws Exception {
		if(start < 0 || start > end) {
			throw new IllegalArgumentException("Start cannot be less than 0 and start must not be greater than end");
		}
		send("delete " + start + ":" + end);
		readSimpleResponse();
	}


	/**
	 * deleteid {SONGID}
	 * Deletes the song SONGID from the playlist
	 * @param songid
	 * @throws Exception
	 */
	public void deleteid(int songid) throws Exception {
		send("deleteid " + songid);
		readSimpleResponse();
	}
	
	/**
	 * move [{FROM} | {START:END}] {TO}
	 * Moves the song at FROM or range of songs at START:END to TO in the playlist. [5]
	 * @param from
	 * @param to
	 * @throws Exception
	 */
	public void move(int from, int to) throws Exception {
		send("move " + from + " " + to);
		readSimpleResponse();
	}
	
	/**
	 * move [{FROM} | {START:END}] {TO}
	 * Moves the song at FROM or range of songs at START:END to TO in the playlist. [5]
	 * @param start
	 * @param end
	 * @param to
	 * @throws Exception
	 */
	public void move(int start, int end, int to) throws Exception {
		send("move " + start + ":" + end + " " + to);
		readSimpleResponse();
	}
	
	/**
	 * moveid {FROM} {TO}
	 * Moves the song with FROM (songid) to TO (playlist index) in the playlist. If TO is negative, it is relative to the current song in the playlist (if there is one).
	 * @param from
	 * @param to
	 * @throws Exception
	 */
	public void moveid(int from, int to) throws Exception {
		send("moveid " + from + " " + to);
		readSimpleResponse();
	}

	/**
	 * playlist
	 * Displays the current playlist.
	 * Note Do not use this, instead use playlistinfo.
	 * playlist
	 * 0:file: http://stream-sd.radioparadise.com:8056
     * 1:file: http://ice-sov.musicradio.com:80/ClassicFMMP3
     * 2:file: http://mp3-a9-128.timlradio.co.uk/
     * 3:file: acdc/black_ice/rock_n_roll_train.flac
     * 4:file: yes/the_ultimate_yes_35th_anniversary_collection_2004_cd2_cdda/homeworld_the_ladder_radio_edit.flac
	 * @throws Exception
	 * @Deprecated
	 */
	public PlaylistLine[] playlist()throws Exception {
		send("playlist");
		Pattern pattern = Pattern.compile("(\\d+):([~:]*):(.*)");
		String[] lines = readResponse();
		PlaylistLine[] playlist = new PlaylistLine[lines.length];
		
		int idx = 0;
		for(String line : lines){
			Matcher matcher = pattern.matcher(line);
			if(matcher.matches()){
				int sequence = Integer.parseInt(matcher.group(1));
				String type = matcher.group(2);
				String path = matcher.group(3);
				playlist[idx++] = new PlaylistLine(sequence, type, path);
			} else {
				throw new MPDClientException("Unable to parse playlist line: " + line);
			}
		}
		return playlist;
	}

	/**
	 * playlistfind {TAG} {NEEDLE}
	 * Finds songs in the current playlist with strict matching.
	 * playlistfind Genre Other
	 * file: yes/the_ultimate_yes_35th_anniversary_collection_2004_cd2_cdda/homeworld_the_ladder_radio_edit.flac
     * Last-Modified: 2012-09-10T20:16:01Z
     * Time: 281
     * Artist: Yes
	 * Track: 14
     * Album: The Ultimate Yes [35th Anniversary Collection 2004] CD.2 [CDDA]
     * Title: Homeworld {The Ladder} (Radio Edit)
     * Genre: Other
     * Date: 1972
     * Pos: 4
     * Id: 5696
     * OK
	 * @param tag
	 * @param needle
	 * @throws Exception
	 */
	public Song[] playlistfind(String tag, String needle) throws Exception {
		send("playlistfind " + tag + " " + needle);
		return readSongList();
	}


	/**
	 * playlistid {SONGID}
	 * Displays a list of songs in the playlist. SONGID is optional and specifies a single song to display info for.
	 * @throws Exception
	 */
	public Song[] playlistid() throws Exception {
		send("playlistid");
		return readSongList();
	}

	/**
	 * playlistid {SONGID}
	 * Displays a list of songs in the playlist. SONGID is optional and specifies a single song to display info for.
	 * @throws Exception
	 */
	public Song[] playlistid(int songid) throws Exception {
		send("playlistid " + songid);
		return readSongList();
	}

	/**
	 * playlistinfo [[SONGPOS] | [START:END]]
	 * Displays a list of all songs in the playlist, or if the optional argument is given, 
	 * displays information only for the song SONGPOS or the range of songs START:END [5]
	 * @throws Exception
	 */
	public Song[] playlistinfo() throws Exception {
		send("playlistinfo");
		return readSongList();
	}
	
	/**
	 * playlistinfo [[SONGPOS] | [START:END]]
	 * Displays a list of all songs in the playlist, or if the optional argument is given, 
	 * displays information only for the song SONGPOS or the range of songs START:END [5]
	 * @param songpos
	 * @throws Exception
	 */
	public Song[] playlistinfo(int songpos) throws Exception {
		send("playlistinfo " + songpos);
		return readSongList();
	}
	
	/**
	 * playlistinfo [[SONGPOS] | [START:END]]
	 * Displays a list of all songs in the playlist, or if the optional argument is given, 
	 * displays information only for the song SONGPOS or the range of songs START:END [5]
	 * @param start
	 * @param end
	 * @throws Exception
	 */
	public Song[] playlistinfo(int start, int end) throws  Exception {
		send("playlistinfo " + start + ":" + end);
		return readSongList();
	}

    /**
     * playlistsearch {TAG} {NEEDLE}
	 * Searches case-sensitively for partial matches in the current playlist.
     * @param tag
     * @param needle
     * @throws Exception
     */
    public Song[] playlistsearch(String tag, String needle) throws Exception{
		send("playlistsearch " + tag + " " + needle);
		return readSongList();
    }

    /**
     * plchanges {VERSION}
	 * Displays changed songs currently in the playlist since VERSION.
	 * To detect songs that were deleted at the end of the playlist, use playlistlength returned by status command.
     * @param version
     * @throws Exception
     */
    public Song[] plchanges( int version ) throws Exception {
    	if(version < 0){
    		throw new MPDClientException("plchanges needs a non-negative integer");
    	}
		send("plchanges " + version);
		return readSongList();
    }


    /**
     * plchangesposid {VERSION}
	 * Displays changed songs currently in the playlist since VERSION. This function only returns the position and the id of the changed song, not the complete metadata. This is more bandwidth efficient.
	 * To detect songs that were deleted at the end of the playlist, use playlistlength returned by status command.
     * @param version
     * @throws Exception
     */
    public Song[] plchangesposid( int version ) throws Exception {
    	if(version < 0){
    		throw new MPDClientException("plchangesposid needs a non-negative integer");
    	}
		send("plchangesposid " + version);
		return readSongList(); // TODO get IDs only
    }

    /**
     * prio {PRIORITY} {START:END...}
	 * Set the priority of the specified songs. A higher priority means that it will be played first when "random" mode is enabled.
	 * A priority is an integer between 0 and 255. The default priority of new songs is 0.
     * @param priority
     * @param start
     * @param end
     * @throws Exception
     */
    public void prio(int priority, int start, int end) throws Exception {
    	if(priority < 0 || priority > 255) {
    		throw new IllegalArgumentException("priority must be in the range 0 to 255 inclusive");
    	}
    	if(start > end){
    		throw new IllegalArgumentException("start must be less or equal to end");
    	}
    	send("prio " + priority + " " + start + ":" + end);
    	readSimpleResponse();
    }

    /**
      * prioid {PRIORITY} {ID...}
      * Same as prio, but address the songs with their id.
     * @param priority
     * @param id
     * @throws Exception
     */
    public void prioid( int priority, int id) throws Exception {
    	if(priority < 0 || priority > 255) {
    		throw new IllegalArgumentException("priority must be in the range 0 to 255 inclusive");
    	}
    	send("prioid " + priority + " " + id);
    	readSimpleResponse();
    }

    /**
     * shuffle [START:END]
     * Shuffles the current playlist. START:END is optional and specifies a range of songs.
     * @throws Exception
     */
    public void shuffle() throws Exception {
    	send("shuffle");
    	readSimpleResponse();
    }
    
    /**
     * shuffle [START:END]
     * Shuffles the current playlist. START:END is optional and specifies a range of songs.
     * @param start
     * @param end
     * @throws Exception
     */
    public void shuffle(int start, int end) throws Exception {
    	if(start > end){
    		throw new IllegalArgumentException("start must be less or equal to end");
    	}
    	send("shuffle " + start + ":" + end);
    	readSimpleResponse();
    }

    /**
     * swap {SONG1} {SONG2}
     * Swaps the positions of SONG1 and SONG2.
     * @param song1
     * @param song2
     * @throws Exception
     */
    public void swap(int song1, int song2) throws Exception {
    	send("swap " + song1 + " " + song2);
    	readSimpleResponse();
    }

    /**
     * swapid {SONG1} {SONG2}
	 * Swaps the positions of SONG1 and SONG2 (both song ids).
     * @param song1
     * @param song2
     * @throws Exception
     */
    public void swapid(int song1, int song2) throws Exception {
    	send("swapid " + song1 + " " + song2);
    	readSimpleResponse();
    }

    /**
     * addtagid {SONGID} {TAG} {VALUE}
     * Adds a tag to the specified song. Editing song tags is only possible for remote songs. This change is volatile: it may be overwritten by tags received from the server, and the data is gone when the song gets removed from the queue.
     * @param songid
     * @param tag
     * @param value
     * @throws Exception
     */
    public void addtagid(int songid, String tag, String value) throws Exception {
    	send("addtagid " + songid + " " + tag + " " + value);
    	readSimpleResponse();
    }

    /**
     * cleartagid {SONGID} [TAG]
	 * Removes tags from the specified song. If TAG is not specified, then all tag values will be removed. Editing song tags is only possible for remote songs.
     * @param songid
     * @throws Exception
     */
    public void cleartagid(int songid) throws Exception {
    	send("cleartagid " + songid);
    	readSimpleResponse();
    }
    
    /**
     * cleartagid {SONGID} [TAG]
	 * Removes tags from the specified song. If TAG is not specified, then all tag values will be removed. Editing song tags is only possible for remote songs.
     * @param songid
     * @param tag
     * @throws Exception
     */
    public void cleartagid(int songid, String tag) throws Exception {
    	send("cleartagid " + songid + " " + tag);
    	readSimpleResponse();
    }
	

	/*=================================================================================================
	 * Stored playlists
	 ================================================================================================*/

    /**
     * listplaylist {NAME}
	 * Lists the songs in the playlist. Playlist plugins are supported.
     * @param name
     * @return array of files in the playlist
     * @throws Exception
     */
    public String[] listplaylist(String name) throws Exception {
    	send("listplaylist " + name);
    	String[] lines = readResponseAndTrim("file:");
    	return lines;
    }
    
    /**
     * listplaylistinfo {NAME}
	 * Lists the songs with metadata in the playlist. Playlist plugins are supported.
     * @param name
     * @throws Exception
     */
    public Song[] listplaylistinfo(String name) throws Exception {
    	send("listplaylistinfo " + name);
    	return readSongList();
    }

    /**
     * listplaylists
	 * Prints a list of the playlist directory.
	 * After each playlist name the server sends its last modification time as attribute "Last-Modified" in
	 *  ISO 8601 format. To avoid problems due to clock differences between clients and the server, clients 
	 *  should not compare this value with their local clock.
	 * listplaylists
	 * playlist: test
     * Last-Modified: 2014-05-22T07:46:14Z
     * playlist: test2
     * Last-Modified: 2014-05-22T07:46:30Z
     * OK 
     * @throws Exception
     */
    public Playlist[] listplaylists() throws Exception {
    	send("listplaylists");
		List<Object> playlists = readObjectList();
		return playlists.toArray(new Playlist[playlists.size()]);
    }

    /**
     * load {NAME} [START:END]
	 * Loads the playlist into the current queue. Playlist plugins are supported. A range may be specified to load only a part of the playlist.
     * @param name
     * @throws Exception
     */
    public void load(String name) throws Exception {
    	send("load " + name);
    	readSimpleResponse();
    }
    
    /**
     * load {NAME} [START:END]
	 * Loads the playlist into the current queue. Playlist plugins are supported. A range may be specified to load only a part of the playlist.
     * @param name
     * @param start
     * @param end
     * @throws Exception
     */
    public void load(String name, int start, int end) throws Exception {
    	if(start > end){
    		throw new IllegalArgumentException("start must be less or equal to end");
    	}
    	send("load " + name + " " + start + ":" + end);
    	readSimpleResponse();
    }

    /**
     * playlistadd {NAME} {URI}
	 * Adds URI to the playlist NAME.m3u.
	 * NAME.m3u will be created if it does not exist.
     * @param name
     * @param uri
     * @throws Exception
     */
    public void playlistadd(String name, String uri) throws Exception {
    	send("playlistadd " + name + " " + uri);
    	readSimpleResponse();
    }


	/**
	 * playlistclear {NAME}
	 * Clears the playlist NAME.m3u.
	 * @param name
	 * @throws Exception
	 */
	public void playlistclear(String name) throws Exception {
    	send("playlistclear " + name);
    	readSimpleResponse();
	}


	/**
	 * playlistdelete {NAME} {SONGPOS}
	 * Deletes SONGPOS from the playlist NAME.m3u.
	 * @param name
	 * @param songpos
	 * @throws Exception
	 */
	public void playlistdelete(String name, int songpos) throws Exception {
    	send("playlistdelete " + name + " " + songpos);
    	readSimpleResponse();
	}
	
	/**
	 * playlistmove {NAME} {SONGID} {SONGPOS}
	 * Moves SONGID in the playlist NAME.m3u to the position SONGPOS.
	 * @param name
	 * @param songid
	 * @param songpos
	 * @throws Exception
	 */
	public void playlistmove(String name, int songid, int songpos) throws Exception {
    	send("playlistmove " + name + " " + songid + " " + songpos);
    	readSimpleResponse();
	}
	
	/**
	 * rename {NAME} {NEW_NAME}
	 * Renames the playlist NAME.m3u to NEW_NAME.m3u.
	 * @param name
	 * @param newname
	 * @throws Exception
	 */
	public void rename(String name, String newname) throws Exception {
    	send("rename " + name + " " + newname);
    	readSimpleResponse();
	}
	
	/**
	 * rm {NAME}
	 * Removes the playlist NAME.m3u from the playlist directory.
	 * @param name
	 * @throws Exception
	 */
	public void rm(String name) throws Exception {
    	send("rm " + name);
    	readSimpleResponse();
	}
	
	/**
	 * save {NAME}
	 * Saves the current playlist to NAME.m3u in the playlist directory.
	 * @param name
	 * @throws Exception
	 */
	public void save(String name) throws Exception {
		send("save " + name);
		readSimpleResponse();
	}

	
	
	
	/*=================================================================================================
	 * The music database
	 ================================================================================================*/

	/**
	 * count {TAG} {NEEDLE} [group] [GROUPTYPE]
	 * Counts the number of songs and their total playtime in the db matching TAG exactly.
	 * The group keyword may be used to group the results by a tag. The following prints per-artist counts:
	 * count group artist.
	 * count Genre Other
	 * songs: 957
     * playtime: 215489
     * OK
	 * @param tag
	 * @param needle
	 * @throws Exception
	 */
	public CountResult count(String tag, String needle) throws Exception {
		send("count " + tag + " " + needle);
		Map<String,String> params = captureSingleResult();
		CountResult result = new CountResult();
		convert(result, params);
		return result;
	}

	/**
	 * count {TAG} {NEEDLE} [group] [GROUPTYPE]
	 * Counts the number of songs and their total playtime in the db matching TAG exactly.
	 * The group keyword may be used to group the results by a tag. The following prints per-artist counts:
	 * count group artist
	 * @param tag
	 * @param needle
	 * @param grouptype
	 * @throws Exception
	 */
	public CountResult count(String tag, String needle, String grouptype) throws Exception {
		send("count " + tag + " " + needle + " group " + grouptype);
		Map<String,String> params = captureSingleResult();
		CountResult result = new CountResult();
		convert(result, params);
		return result;
	}


	/**
	 * find {TYPE} {WHAT} [...]
	 * Finds songs in the db that are exactly WHAT. TYPE can be any tag supported by MPD, or one of the three special parameters — file to search by full path (relative to the music directory), base to restrict the search to songs in the given directory (also relative to the music directory) and any to match against all available tags. WHAT is what to find.
	 * @param type
	 * @param what
	 * @return
	 * @throws Exception
	 */
	public Song[] find(String type, String... what) throws Exception {
		StringBuilder cmd = new StringBuilder("find ");
		cmd.append("type");
		if(what != null){
			for(String item : what) {
				cmd.append(' ');
				cmd.append(item);
			}
		} else {
			throw new IllegalArgumentException("Missing search value(s)");
		}
		
		send(cmd.toString());
		return readSongList();
	}

	
	/**
	 * findadd {TYPE} {WHAT} [...]
	 * Finds songs in the db that are exactly WHAT and adds them to current playlist. Parameters have the same meaning as for find.
	 * @param type
	 * @param what
	 * @return
	 * @throws Exception
	 */
	public Song[] findadd(String type, String... what) throws Exception {
		StringBuilder cmd = new StringBuilder("findadd ");
		cmd.append("type");
		if(what != null){
			for(String item : what) {
				cmd.append(' ');
				cmd.append(item);
			}
		} else {
			throw new IllegalArgumentException("Missing search value(s)");
		}
		
		send(cmd.toString());
		return readSongList();
	}

	/**
	 * list {TYPE} [FILTERTYPE] [FILTERWHAT] [...] [group] [GROUPTYPE] [...]
	 * Lists unique tags values of the specified type. TYPE can be any tag supported by MPD or file.
	 * Additional arguments may specify a filter like the one in the find command.
	 * The group keyword may be used (repeatedly) to group the results by one or more tags. The following example lists all album names, grouped by their respective (album) artist:
	 * list album group albumartist
	 * @param type
	 * @param other
	 * @return
	 * @throws Exception
	 */
	public String[] list(String type, String other) throws Exception {

		String cmd = "list " + type;
		if(other != null) {
			cmd += " " + other;
		}
		send(cmd);
		
		String[] lines = readResponse();
		// Note, will be of the format
		//Artist: Don Henley
        //Artist: Gustav Mahler
		// or similar depending on the type parameter.

		String previous = null;
    	for(int i = 0; i < lines.length; ++i){
    		String line = lines[i];
    		int idx = line.indexOf(':');
    		if(idx != -1) {
    			// Assumption that all return of same type so validate:
    			if(previous != null){
    				String tag = line.substring(0, idx);
    				if(!tag.endsWith(previous)){
    					throw new MPDClientException("Parsing list, expected " + previous + " found " + tag);
    				}
    				previous = tag;
    			}
    			
    			// Remove the tag
    			line = line.substring(idx + 1).trim();
    			lines[i] = line;
    			
    		} else {
    			throw new MPDClientException("Can't parse list - no :");
    		}
    	}
    	return lines;
	}

	/**
	 * listall [URI]
	 * Lists all songs and directories in URI.
	 * Do not use this command. Do not manage a client-side copy of MPD's database. That is fragile and adds huge overhead. It will break with large databases. Instead, query MPD whenever you need something.
	 * file: yes/the_ultimate_yes_35th_anniversary_collection_2004_cd2_cdda/leave_it.flac
     * file: yes/the_ultimate_yes_35th_anniversary_collection_2004_cd2_cdda/it_can_happen_single_edit.flac
     * file: yes/the_ultimate_yes_35th_anniversary_collection_2004_cd2_cdda/rhythm_of_love.flac
     * file: yes/the_ultimate_yes_35th_anniversary_collection_2004_cd2_cdda/big_generator_remix.flac
	 * @param uri
	 * @return
	 * @throws Exception
	 * @Deprecated
	 */
	public String[] listall(String uri) throws Exception {
		send("listall " + uri);
		return readResponseAndTrim("file:");
	}
	
	/**
	 * listallinfo [URI]
	 * Same as listall, except it also returns metadata info in the same format as lsinfo.
	 * Do not use this command. Do not manage a client-side copy of MPD's database. That is fragile and adds huge overhead. It will break with large databases. Instead, query MPD whenever you need something.
	 * @param uri
	 * @return
	 * @throws Exception
	 * @Deprecated
	 */
	public NamedItem[] listallinfo(String uri) throws Exception {
		send("listallinfo " + uri);
		List<Object> objects = readObjectList();
		return objects.toArray(new NamedItem[objects.size()]); 
	}
	
	

	/**
	 * listfiles [URI]
	 * Lists the contents of the directory URI, including files are not recognized by MPD. URI can be a path relative to the music directory or an URI understood by one of the storage plugins. The response contains at least one line for each directory entry with the prefix "file: " or "directory: ", and may be followed by file attributes such as "Last-Modified" and "size".
	 * For example, "smb://SERVER" returns a list of all shares on the given SMB/CIFS server; "nfs://servername/path" obtains a directory listing from the NFS server.
	 * @param uri
	 * @return
	 * @throws Exception
	 * @Deprecated
	 */
	public String[] listfiles(String uri) throws Exception {
		throw new UnsupportedOperationException("MPD does not respond to listfiles");
	}
	
	/**
	 * lsinfo [URI]
	 * Lists the contents of the directory URI.
	 * When listing the root directory, this currently returns the list of stored playlists. This behavior is deprecated; use "listplaylists" instead.
	 * This command may be used to list metadata of remote files (e.g. URI beginning with "http://" or "smb://").
	 * Clients that are connected via UNIX domain socket may use this command to read the tags of an arbitrary local file (URI beginning with "file:///").
	 * Note, lsinfo may return multiple types of information. 
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public NamedItem[] lsinfo(String uri) throws Exception {
		send("lsinfo " + uri);
		List<Object> objects = readObjectList();
		return objects.toArray(new NamedItem[objects.size()]); 
	} 
	
	/**
	 * readcomments [URI]
	 * Read "comments" (i.e. key-value pairs) from the file specified by "URI". This "URI" can be a path relative to the music directory or a URL in the form "file:///foo/bar.ogg".
	 * This command may be used to list metadata of remote files (e.g. URI beginning with "http://" or "smb://").
	 * The response consists of lines in the form "KEY: VALUE". Comments with suspicious characters (e.g. newlines) are ignored silently.
	 * The meaning of these depends on the codec, and not all decoder plugins support it. For example, on Ogg files, this lists the Vorbis comments.
	 * @param uri
	 * @return
	 * @throws Exception
	 * @Deprecated
	 */
	public String[] readcomments(String uri) throws Exception {
		throw new UnsupportedOperationException("MPD does not respond to readcomments");
	}
	
	/**
	 * search {TYPE} {WHAT} [...]
	 * Searches for any song that contains WHAT. Parameters have the same meaning as for find, except that search is not case sensitive.
	 * @param type
	 * @param what
	 * @return
	 * @throws Exception
	 */
	public Song[] search(String type, String... what) throws Exception {
		StringBuilder cmd = new StringBuilder("search ");
		cmd.append(type);
		if(what != null){
			for(String item : what) {
				cmd.append(' ');
				cmd.append(item);
			}
		} else {
			throw new IllegalArgumentException("Missing search value(s)");
		}
		
		send(cmd.toString());
		return readSongList();
	}
	
	/**
	 * searchadd {TYPE} {WHAT} [...]
	 * Searches for any song that contains WHAT in tag TYPE and adds them to current playlist.
	 * Parameters have the same meaning as for find, except that search is not case sensitive.
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public Song[] searchadd(String type, String... what) throws Exception {
		StringBuilder cmd = new StringBuilder("search ");
		cmd.append(type);
		if(what != null){
			for(String item : what) {
				cmd.append(' ');
				cmd.append(item);
			}
		} else {
			throw new IllegalArgumentException("Missing search value(s)");
		}
		
		send(cmd.toString());
		return readSongList();
	}
	
	/**
	 * searchaddpl {NAME} {TYPE} {WHAT} [...]
	 * Searches for any song that contains WHAT in tag TYPE and adds them to the playlist named NAME.
	 * If a playlist by that name doesn't exist it is created.
	 * Parameters have the same meaning as for find, except that search is not case sensitive.
	 * @param name
	 * @param type
	 * @param what
	 * @return
	 * @throws Exception
	 */
	public Song[] searchaddpl(String name, String type, String... what) throws Exception {
		StringBuilder cmd = new StringBuilder("searchaddpl ");
		cmd.append(name);
		cmd.append(' ');
		cmd.append(type);
		if(what != null){
			for(String item : what) {
				cmd.append(' ');
				cmd.append(item);
			}
		} else {
			throw new IllegalArgumentException("Missing search value(s)");
		}
		
		send(cmd.toString());
		return readSongList();
	}

	/**
	 * update [URI]
	 * Updates the music database: find new files, remove deleted files, update modified files.
	 * URI is a particular directory or song/file to update. If you do not specify it, everything is updated.
	 * Prints "updating_db: JOBID" where JOBID is a positive number identifying the update job. You can read the current job id in the status response.
	 * @return the JOBID
	 * @throws Exception
	 */
	public int update() throws Exception {
		send("update");
		String[] lines = readResponseAndTrim("updating_db:");
		if(lines.length != 1){
			throw new MPDClientException("Expected only 1 line return from update");
		}
		return Integer.parseInt(lines[0]);
	}

	/**
	 * update [URI]
	 * Updates the music database: find new files, remove deleted files, update modified files.
	 * URI is a particular directory or song/file to update. If you do not specify it, everything is updated.
	 * Prints "updating_db: JOBID" where JOBID is a positive number identifying the update job. You can read the current job id in the status response.
	 * @param uri
	 * @return JOBID
	 * @throws Exception
	 */
	public int update(String uri) throws Exception {
		send("update " + uri);
		String[] lines = readResponseAndTrim("updating_db:");
		if(lines.length != 1){
			throw new MPDClientException("Expected only 1 line return from update");
		}
		return Integer.parseInt(lines[0]);
	}
	
	/**
	 * rescan [URI]
	 * Same as update, but also rescans unmodified files.
	 * @return JOBID
	 * @throws Exception
	 */
	public int rescan() throws Exception {
		send("rescan");
		String[] lines = readResponseAndTrim("updating_db:");
		if(lines.length != 1){
			throw new MPDClientException("Expected only 1 line return from rescan");
		}
		return Integer.parseInt(lines[0]);
	}

	/**
	 * rescan [URI]
	 * Same as update, but also rescans unmodified files.
	 * @param uri
	 * @return JOBID
	 * @throws Exception
	 */
	public int rescan(String uri) throws Exception {
		send("rescan " + uri);
		String[] lines = readResponseAndTrim("updating_db:");
		if(lines.length != 1){
			throw new MPDClientException("Expected only 1 line return from rescan");
		}
		return Integer.parseInt(lines[0]);
	}



	/*=================================================================================================
	 * Stickers
	 * "Stickers"[2] are pieces of information attached to existing MPD objects (e.g. song files, directories, albums). Clients can create arbitrary name/value pairs. MPD itself does not assume any special meaning in them.
	 * The goal is to allow clients to share additional (possibly dynamic) information about songs, which is neither stored on the client (not available to other clients), nor stored in the song files (MPD has no write access).
	 * Client developers should create a standard for common sticker names, to ensure interoperability.
	 * Objects which may have stickers are addressed by their object type ("song" for song objects) and their URI (the path within the database for songs).
	 ================================================================================================*/


	/**
	 * sticker get {TYPE} {URI} {NAME}
	 * Reads a sticker value for the specified object.
	 * @param type - believe that MPD only understands "song"
	 * @param uri
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public String getSticker(String type, String uri, String name) throws Exception {
		send("sticker get " + type + " " + uri + " " + name);
		String[] lines = readResponse();
		return lines[0];
	}
	
	
	/**
	 * sticker set {TYPE} {URI} {NAME} {VALUE}
	 * Adds a sticker value to the specified object. If a sticker item with that name already exists, it is replaced.
	 * @param type
	 * @param uri
	 * @param name
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public void setSticker(String type, String uri, String name, String value) throws Exception {
		send("sticker set " + type + " " + uri + " " + name + " " + value);
		readSimpleResponse();
	}


	 
	/**
	 * sticker delete {TYPE} {URI} [NAME]
	 * Deletes a sticker value from the specified object. If you do not specify a sticker name, all sticker values are deleted.
	 * @param type
	 * @param uri
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public void deleteSticker(String type, String uri) throws Exception {
		send("sticker delete " + type + " " + uri);
		readSimpleResponse();
	}

	/**
	 * sticker delete {TYPE} {URI} [NAME]
	 * Deletes a sticker value from the specified object. If you do not specify a sticker name, all sticker values are deleted.
	 * @param type
	 * @param uri
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public void deleteSticker(String type, String uri, String name) throws Exception {
		send("sticker delete " + type + " " + uri + " " + name);
		readSimpleResponse();
	}


	/**
	 * sticker list {TYPE} {URI}
	 * Lists the stickers for the specified object.
	 * @param type
	 * @param uri
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public String[] listStickers(String type, String uri) throws Exception {
		send("sticker list " + type + " " + uri);
		return readResponse();
	}
	 
	 

	/**
	 * sticker find {TYPE} {URI} {NAME}
	 * Searches the sticker database for stickers with the specified name, below the specified directory (URI). For each matching song, it prints the URI and that one sticker's value.
	 * @param type
	 * @param uri
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public String[] findSticker(String type, String uri, String name) throws Exception {
		send("sticker find " + type + " " + uri + " " + name);
		return readResponse();
	}


	
	
	/*=================================================================================================
	 * Connection settings
	 ================================================================================================*/

	/**
	 * close
	 * Closes the connection to MPD. MPD will try to send the remaining output buffer before it actually closes the connection, but that cannot be guaranteed. This command will not generate a response.
	 * @throws Exception
	 */
	public void close()  throws Exception{
		send("close");
		readSimpleResponse();
	}


	/**
	 * kill
     * Kills MPD.
	 * @throws Exception
	 */
	public void kill()  throws Exception{
		send("kill");
		readSimpleResponse();
	}


	/**
	 * password {PASSWORD}
	 *This is used for authentication with the server. PASSWORD is simply the plaintext password.
	 * @param password
	 * @throws Exception
	 */
	public void password(String password)  throws Exception{
		send("password " + password);
		readSimpleResponse();
	}

	/**
	 * ping
	 * Does nothing but return "OK".
	 * @throws Exception
	 */
	public void ping()  throws Exception{
		send("ping");
		readSimpleResponse();
	}


	/*=================================================================================================
	 * Audio output devices
	 ================================================================================================*/

	/**
	 * disableoutput {ID}
	 * Turns an output off.
	 * @param id
	 * @throws Exception
	 */
	public void disableoutput(int id) throws Exception {
		send("disableoutput " + id);
		readSimpleResponse();
	}

	/**
	 * enableoutput {ID}
	 * Turns an output on.
	 * @param id
	 * @throws Exception
	 */
	public void enableoutput(int id) throws Exception {
		send("enableoutput " + id);
		readSimpleResponse();
	}

	/**
	 * toggleoutput {ID}
	 * Turns an output on or off, depending on the current state.
	 * @param id
	 * @throws Exception
	 */
	public void toggleoutput(int id) throws Exception {
		send("toggleoutput " + id);
		readSimpleResponse();
	}

	/**
	 * outputs
	 * Shows information about all outputs.
	 * outputid: 0
	 * outputname: My ALSA Device
	 * outputenabled: 0
	 * OK
	 * Return information:
	 * outputid: ID of the output. May change between executions
	 * outputname: Name of the output. It can be any.
	 * outputenabled: Status of the output. 0 if disabled, 1 if enabled.
	 * @throws Exception
	 */
	public Output[] outputs() throws Exception {
		send("outputs");
		String[] fields = { "outputid", "outputname", "outputenabled" };
		List<Object> outputs = readObjectList(fields, Output.class);
		return outputs.toArray(new Output[outputs.size()]);
	}




	/*=================================================================================================
	 * Channels
	 * Client to client
	 * Clients can communicate with each others over "channels". A channel is created by a client subscribing to it. More than one client can be subscribed to a channel at a time; all of them will receive the messages which get sent to it.
	 * Each time a client subscribes or unsubscribes, the global idle event subscription is generated. In conjunction with the channels command, this may be used to auto-detect clients providing additional services.
	 * New messages are indicated by the message idle event.
	 ================================================================================================*/

	/**
	 * subscribe {NAME}
	 * Subscribe to a channel. The channel is created if it does not exist already. The name may consist of alphanumeric ASCII characters plus underscore, dash, dot and colon.
	 * @param name
	 * @throws Exception
	 */
	public void subscribe(String name)  throws Exception{
		send("subscribe " + name);
		readSimpleResponse();
	}


	/**
	 * unsubscribe {NAME}
     * Unsubscribe from a channel.
	 * @param name
	 * @throws Exception
	 */
	public void unsubscribe(String name)  throws Exception{
		send("unsubscribe " + name);
		readSimpleResponse();
	}


	/**
	 * channels
	 * Obtain a list of all channels. The response is a list of "channel:" lines.
	 * @throws Exception
	 */
	public void channels()  throws Exception{
		send("channels ");
		readSimpleResponse(); // TODO - parse response
	}


	/**
	 * readmessages
	 * Reads messages for this client. The response is a list of "channel:" and "message:" lines e.g.
	 * sendmessage wombats marsupial
	 * OK
  	 * sendmessage flipper dolphin
     * OK
  	 * sendmessage flipper fish
     * OK
  	 * channels
	 * channel: flipper
     * channel: wombats
     * OK
     * readmessages
	 * channel: wombats
     * message: marsupial
     * channel: flipper
     * message: dolphin
     * channel: flipper
     * message: fish
     * OK
	 * @throws Exception
	 */
	public String[] readmessages()  throws Exception{
		send("readmessages");
		String[] lines = readResponse(); // TODO - parse response
		return lines;
	}

	/**
	 * sendmessage {CHANNEL} {TEXT}
	 * Send a message to the specified channel.
	 * Single words don't needs quotes, otherwise use "" to quote the message and \" to escape any quotes.
	 * @throws Exception
	 */
	public void sendmessage(String channel, String text)  throws Exception {
		// TODO - tidy up string as above.
		send("sendmessage " + channel + " " + text);
		readSimpleResponse(); 
	}


	/*=================================================================================================
	 * Handlers 
	 * Used to parse return values from MPD. 
	 ================================================================================================*/
	
	interface Handler {
		
		public String getTypeName();
		public Object convert(String value) throws Exception; 
		
	}
	
	private static class StringFieldHandler implements Handler {

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return String.class.getName();
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#convert(java.lang.String)
		 */
		@Override
		public Object convert(String value) {
			return value;
		}
		
	}

	private static class IntFieldHandler implements Handler {

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return int.class.getName();
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#convert(java.lang.String)
		 */
		@Override
		public Object convert(String value) {
			return Integer.parseInt(value);
		}
	}

	private static class LongFieldHandler implements Handler {

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return long.class.getName();
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#convert(java.lang.String)
		 */
		@Override
		public Object convert(String value) {
			return Long.parseLong(value);
		}
	}

	private static class BooleanFieldHandler implements Handler {

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return boolean.class.getName();
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#convert(java.lang.String)
		 */
		@Override
		public Object convert(String value) {
			return (value.equals("1") || value.equals("true")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
	}

	
	private static class FloatFieldHandler implements Handler {

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return float.class.getName();
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#convert(java.lang.String)
		 */
		@Override
		public Object convert(String value) {
			if(value.equals("nan")){
				return Float.NaN;
			}
			value = value.replace(':', '.');
			return Float.parseFloat(value);
		}
		
	}
	
	private static class DateFieldHandler implements Handler {

		// 2014-05-11T19:24:17Z
		private DateFormat fmt;
		private DateFormat shortFmt = new SimpleDateFormat("yyyy-MM-dd");
		
		DateFieldHandler(){
			
			// Time zone on date/time format only supported in Java 7 or later so want to make use of it
			// if possible, otherwise fall back to explicit
			String ver = System.getProperty("java.version"); // e.g. 1.6.0_22
			boolean supportsTZ = false; 	// set true if Z form of timezone supported. Java 7 and above.
			int idxMajor = ver.indexOf('.');
			if(idxMajor != -1) {
				int idxMinor = ver.indexOf('.',idxMajor + 1);
				if(idxMinor != -1) {
					String major = ver.substring(0, idxMajor);
					String minor = ver.substring(idxMajor + 1, idxMinor);
					
					int majorVer = Integer.parseInt(major);
					int minorVer = Integer.parseInt(minor);
					
					supportsTZ = majorVer >= 1 && minorVer >= 7;
				}
			}
			
			if(supportsTZ){
				fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"); // Java 7 onwards
			} else {
				fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			}
			
		}
		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return Date.class.getName();
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#convert(java.lang.String)
		 */
		@Override
		public Object convert(String value) throws Exception{
			if(value.contains("T")) {
				return fmt.parse(value);
			} else if(value.length() == 10) {
				return shortFmt.parse(value);
			}
			return new Date(Long.parseLong(value));
		}
		
	}

	private static class AudioFormatFieldHandler implements Handler {

		private Pattern pattern = Pattern.compile("(\\d+):(\\d+):(\\d+)");
		
		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return AudioFormat.class.getName();
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#convert(java.lang.String)
		 */
		@Override
		public Object convert(String value) throws Exception{
			// 44100:16:2
			AudioFormat audio = null;
			Matcher matcher = pattern.matcher(value);
			if(matcher.matches()){
				audio = new AudioFormat();
				audio.setSampleRate( Integer.parseInt(matcher.group(1)));
				audio.setBitsPerSample( Integer.parseInt(matcher.group(2)));
				audio.setChannels( Integer.parseInt(matcher.group(3)));
			}
			return audio;
		}
		
	}
	

	private static class PlayStateFieldHandler implements Handler {

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return PlayState.class.getName();
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.Connection.Handler#convert(java.lang.String)
		 */
		@Override
		public Object convert(String value) throws Exception{
			return PlayState.valueOf(value);
		}
		
	}

}
