/**
 * 
 */
package uk.co.alvagem.mpdclient.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.alvagem.mpdclient.application.Application;
import uk.co.alvagem.mpdclient.client.Connection;
import uk.co.alvagem.mpdclient.client.PlayState;
import uk.co.alvagem.mpdclient.client.Song;
import uk.co.alvagem.mpdclient.client.Status;

/**
 * @author bruce.porteous
 *
 */
public class ButtonPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Application app;
	private ControlPanel controlPanel;
	private VolumePanel volumePanel;
	 private JToggleButton random;
	 private JToggleButton consume;
	 private JToggleButton single;
	 private JToggleButton repeat;
	 private JButton updateDB;
	 private JButton clearQueue;
	 private JButton saveAsPlaylist;
	 private boolean isPaused = false;
	 private boolean isPlaying = false; //might be paused

	/**
	 * Random
	 * Consume
	 * Single
	 * Repeat
	 * UpdateDB
	 * Clear Queue
	 * @param con
	 */
	public ButtonPanel(Application app) {
		assert(app != null);
		this.app = app;
		ButtonBox buttons = new ButtonBox();
		
		controlPanel = new ControlPanel(app);
		volumePanel = new VolumePanel(app);
		
		random = new JToggleButton("Random");
		random.addActionListener(e -> {
			try {
				JToggleButton button = (JToggleButton)e.getSource();
				ButtonPanel.this.app.getConnection().random( button.isSelected());
			} catch (Exception e1) {
				ButtonPanel.this.app.statusMessage(e1.getMessage());
				e1.printStackTrace();
			}
		});

		consume = new JToggleButton("Consume");
		consume.addActionListener(e -> {
			try {
				JToggleButton button = (JToggleButton)e.getSource();
				ButtonPanel.this.app.getConnection().consume( button.isSelected());
			} catch (Exception e1) {
				ButtonPanel.this.app.statusMessage(e1.getMessage());
				e1.printStackTrace();
			}
		});
		
		single = new JToggleButton("Single");
		single.addActionListener(e -> {
			try {
				JToggleButton button = (JToggleButton)e.getSource();
				ButtonPanel.this.app.getConnection().single( button.isSelected());
			} catch (Exception e1) {
				ButtonPanel.this.app.statusMessage(e1.getMessage());
				e1.printStackTrace();
			}
		});

		
		repeat = new JToggleButton("Repeat");
		repeat.addActionListener(e -> {
			try {
				JToggleButton button = (JToggleButton)e.getSource();
				ButtonPanel.this.app.getConnection().repeat( button.isSelected());
			} catch (Exception e1) {
				ButtonPanel.this.app.statusMessage(e1.getMessage());
				e1.printStackTrace();
			}
		});

		updateDB = new JButton("Update DB");
		updateDB.addActionListener(e -> {
			try {
				ButtonPanel.this.app.getConnection().update();
			} catch (Exception e1) {
				ButtonPanel.this.app.statusMessage(e1.getMessage());
				e1.printStackTrace();
			}
		});
		
		clearQueue = new JButton("Clear Queue");
		clearQueue.addActionListener( e-> {
			try {
				ButtonPanel.this.app.getConnection().clear();
			} catch (Exception e1) {
				ButtonPanel.this.app.statusMessage(e1.getMessage());
				e1.printStackTrace();
			}
		});
		
		saveAsPlaylist = new JButton("Save as playlist");
		saveAsPlaylist.addActionListener( ae -> {
			String name = JOptionPane.showInputDialog(this, "Name for new playlist?");
			if(name != null) {
				try {
					app.getConnection().save(name);
				} catch (Exception e) {
					app.statusMessage(e.getMessage());
				}
			}

		});
		buttons.add(controlPanel);
		buttons.add(volumePanel);
		buttons.add(random);
		buttons.add(consume);
		buttons.add(single);
		buttons.add(repeat);
		buttons.add(updateDB);
		buttons.add(clearQueue);
		buttons.add(saveAsPlaylist);
		
		add(buttons);
		
		app.addListener(new Listener());
	}

	void setPausedButton(){
		controlPanel.setPausedButton();
	}

	private class ControlPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		JButton prev;
		JButton stop;
		JButton playPause;
		JButton next;

		// prev
		// stop
		// play-pause
		// next
		
		ControlPanel(Application app) {
			prev = new JButton("<<");
			stop = new JButton("[]");
			playPause = new JButton("PP");
			next = new JButton(">>");

			prev.addActionListener( e-> {
				try {
					ButtonPanel.this.app.getConnection().previous();
				} catch (Exception e1) {
					ButtonPanel.this.app.statusMessage(e1.getMessage());
					e1.printStackTrace();
				}
			});
			
			stop.addActionListener( e-> {
				try {
					ButtonPanel.this.app.getConnection().stop();
				} catch (Exception e1) {
					ButtonPanel.this.app.statusMessage(e1.getMessage());
					e1.printStackTrace();
				}
			});
			
			playPause.addActionListener( e-> {
				try {
					Connection con = ButtonPanel.this.app.getConnection();
					if(!isPlaying) {
						con.play();
					} else {
						// It's playing so just pause.
						con.pause(!isPaused);
					}
				} catch (Exception e1) {
					ButtonPanel.this.app.statusMessage(e1.getMessage());
					e1.printStackTrace();
				}
			});

			next.addActionListener( e-> {
				try {
					ButtonPanel.this.app.getConnection().next();
				} catch (Exception e1) {
					ButtonPanel.this.app.statusMessage(e1.getMessage());
					e1.printStackTrace();
				}
			});

			setPausedButton();
			
			add(prev);
			add(stop);
			add(playPause);
			add(next);
		}
		
		void setPausedButton() {
			if(isPaused){
				ControlPanel.this.playPause.setText("PL");
			} else {
				ControlPanel.this.playPause.setText("PA");
			}
		}
		
	}
	
	private class VolumePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private JSlider slider;
		
		VolumePanel(Application app){
	
			slider = new JSlider(0,100);
			slider.addChangeListener(new ChangeListener() {
				
				public void stateChanged(ChangeEvent e) {
				    JSlider source = (JSlider)e.getSource();
				    if (!source.getValueIsAdjusting()) {
				        try {
							ButtonPanel.this.app.getConnection().setvol(source.getValue());
						} catch (Exception e1) {
							ButtonPanel.this.app.statusMessage(e1.getMessage());
							e1.printStackTrace();
						}
				    }
				}
			});
			add(slider);
			
		}
		
		void setVolume(int vol){
			slider.setValue(vol);
		}
	}
	
	private class Listener extends AbstractMPDEventListener {

		
		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.swing.AbstractMPDEventListener#onOptions(uk.co.alvagem.mpdclient.client.Status)
		 */
		@Override
		public void onOptions(Status status) throws Exception {
			consume.setSelected(status.isConsume());
			random.setSelected(status.isRandom());
			repeat.setSelected(status.isRepeat());
			single.setSelected(status.isSingle());
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.swing.AbstractMPDEventListener#onPlayer(uk.co.alvagem.mpdclient.client.Status)
		 */
		@Override
		public void onPlayer(Status status) throws Exception {
			PlayState playstate = status.getState();
			switch(playstate){
			case pause:  // can play
				isPlaying = true;
				isPaused = true;
				break;
				
			case play:	// can pause or stop
				isPaused = false;
				isPlaying = true;
				break;
				
			case stop:	// can play
				isPaused = false;
				isPlaying = false;
				break;
			}
			setPausedButton();
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.mpdclient.swing.AbstractMPDEventListener#onMixer(uk.co.alvagem.mpdclient.client.Status)
		 */
		@Override
		public void onMixer(Status status) throws Exception {
			volumePanel.setVolume(status.getVolume());
		}
		
		
		
	}
}
