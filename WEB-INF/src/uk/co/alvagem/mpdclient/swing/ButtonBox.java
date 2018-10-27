/*
 * ButtonBox.java
 *
 * Created on 02 February 2002, 21:25
 */

package uk.co.alvagem.mpdclient.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.Box;
import javax.swing.BoxLayout;
/**
 * ButtonBox is a container class to provide a vertical strip of buttons.  
 * It is set up to make buttons drop to the bottom and is normally used 
 * to setup the OK and Cancel buttons in the bottom right corner of a
 * dialog.
 * @author  rbp28668
 */
public class ButtonBox extends Box {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Creates new and empty ButtonBox */
    public ButtonBox() {
        super(BoxLayout.Y_AXIS);
        add(Box.createVerticalGlue()); // buttons drop to bottom of box
    }
    
	/**
	 * Method add adds a button with a strut to space it from the button
	 * above.  Buttons are added from top to bottom.
	 * @param btn
	 */
    public Component add(Component btn) {
        super.add(Box.createVerticalStrut(strutSize));
        super.add(btn);
        return btn;
    }

	/**
	 * @see java.awt.Component#doLayout().  
	 * Causes this container to lay out its components
	 */
    public void doLayout() {
        super.doLayout();

        int nButtons = getComponentCount();
        int width = 0;
        for(int i=0; i<nButtons; ++i){
            Component c = getComponent(i);
            Dimension d = c.getSize();
            if(d.width > width) width = d.width;
        }
        
        for(int i=0; i<nButtons; ++i) {
            Component c = getComponent(i);
            Dimension d = c.getSize();
            d.width = width;
            c.setSize(d);
            
            Rectangle r = c.getBounds();
            r.x = 0; 
            c.setBounds(r);
        }
    }
    
    /** Size to leave between buttons */
    private int strutSize = 5;

}
