/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import uk.co.alvagem.mpdclient.application.Application;
import uk.co.alvagem.mpdclient.client.Connection;
import uk.co.alvagem.mpdclient.client.Song;
import uk.co.alvagem.mpdclient.client.Status;
import uk.co.alvagem.mpdclient.client.Subsystem;

/**
 * @author bruce.porteous
 *
 */
public class EventManager {

	private EventDispatcherThread eventDispatcher;
	
	EventManager(Application app, String host, int port) throws Exception{
		
		Connection eventConnection = new Connection(host, port);
		eventConnection.connect();
		
		eventDispatcher = new EventDispatcherThread(app, eventConnection);
		Thread thread = new Thread(eventDispatcher, "MPD Client");
		thread.setDaemon(true);
		thread.start();
		
	}
	
	public void addListener(MPDEventListener listener) {
		eventDispatcher.addListener(listener);
	}
	
	public void removeListener(MPDEventListener listener){
		eventDispatcher.removeListener(listener);
	}

	public void shutdown(){
		eventDispatcher.shutdown();
	}
	
	private static class EventDispatcherThread implements Runnable {

		private Application app;
		private Connection con;
		private List<MPDEventListener> listeners = new LinkedList<MPDEventListener>();
		private volatile boolean runFlag = true;
		
		EventDispatcherThread(Application app, Connection con){
			assert(app != null);
			this.app = app;
			this.con = con;
		}
		
		/**
		 * 
		 */
		public void shutdown() {
			runFlag = false;
		}

		public void addListener(MPDEventListener listener) {
			synchronized (listeners) {
				listeners.add(listener);
			}
		}
		
		public void removeListener(MPDEventListener listener){
			synchronized (listeners) {
				listeners.remove(listener);
			}
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			System.out.println("Event Dispatcher Thread Started");
			while (runFlag) {
				try {
					// TODO - ensure status only read once for multiple subsystems.
					Subsystem[] subsystems = con.idle();
					for (Subsystem subsystem : subsystems) {
						switch (subsystem) {
						case database: // the song database has been modified after update.
							dispatchDatabaseEvent();
							break;

						case update: //  a database update has started or finished. If the database was modified during the update: the database event is also emitted.
							dispatchUpdateEvent();
							break;

						case stored_playlist: //  a stored playlist has been modified: renamed: created or deleted
							dispatchStoredPlaylistEvent();
							break;

						case playlist: //  the current playlist has been modified
							dispatchPlaylistEvent();
							break;

						case player: //  the player has been started: stopped or seeked
							dispatchPlayerEvent();
							break;

						case mixer: //  the volume has been changed
							dispatchMixerEvent();
							break;

						case output: //  an audio output has been enabled or disabled
							dispatchOutputEvent();
							break;

						case options: //  options like repeat: random: crossfade: replay gain
							dispatchOptionsEvent();
							break;

						case sticker: //  the sticker database has been modified.
							dispatchStickerEvent();
							break;

						case subscription: //  a client has subscribed or unsubscribed to a channel
							dispatchSubscriptionEvent();
							break;

						case message: //  a message was received on a channel this client is subscribed to; this event is only emitted when the queue is empty
							dispatchMessageEvent();
							break;

						}
					}

				} catch (Exception e) {
					e.printStackTrace(System.err);
					String msg = e.getMessage();
					if (msg == null) {
						msg = e.getClass().getSimpleName();
					}
					final String theMessage = msg;
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							app.statusMessage(theMessage);
						}

					});
				}
			}

			con.disconnect();

		}

		/**
		 * 
		 */
		private void dispatchDatabaseEvent() throws Exception {
			synchronized(listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(MPDEventListener listener : listeners){
							try {
								listener.onDatabase();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		}

		/**
		 * 
		 */
		private void dispatchUpdateEvent() throws Exception {
			synchronized(listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(MPDEventListener listener : listeners){
							try {
								listener.onUpdate();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		}

		/**
		 * 
		 */
		private void dispatchStoredPlaylistEvent() throws Exception{
			synchronized(listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(MPDEventListener listener : listeners){
							try {
								listener.onStoredPlaylist();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		}

		/**
		 * 
		 */
		private void dispatchPlaylistEvent() throws Exception {
			final Song[] playlist = con.playlistinfo();
			synchronized(listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(MPDEventListener listener : listeners){
							try {
								listener.onPlaylist(playlist);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		}

		/**
		 * 
		 */
		private void dispatchPlayerEvent() throws Exception {
			final Status status = con.status();
			synchronized(listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(MPDEventListener listener : listeners){
							try {
								listener.onPlayer(status);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		}

		/**
		 * 
		 */
		private void dispatchMixerEvent() throws Exception {
			final Status status = con.status();
			synchronized(listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(MPDEventListener listener : listeners){
							try {
								listener.onMixer(status);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		}

		/**
		 * 
		 */
		private void dispatchOutputEvent() throws Exception{
			synchronized(listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(MPDEventListener listener : listeners){
							try {
								listener.onOutput();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		}

		/**
		 * 
		 */
		private void dispatchOptionsEvent() throws Exception{
			final Status status = con.status();
			synchronized(listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(MPDEventListener listener : listeners){
							try {
								listener.onOptions(status);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		}

		/**
		 * 
		 */
		private void dispatchStickerEvent() throws Exception {
			synchronized(listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(MPDEventListener listener : listeners){
							try {
								listener.onSticker();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		}

		/**
		 * 
		 */
		private void dispatchSubscriptionEvent() throws Exception {
			synchronized(listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(MPDEventListener listener : listeners){
							try {
								listener.onSubscription();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		}

		/**
		 * 
		 */
		private void dispatchMessageEvent() throws Exception {
			final String[] messages = con.readmessages();
			
			synchronized(listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(MPDEventListener listener : listeners){
							try {
								listener.onMessage(messages);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		}
		
	}
}
