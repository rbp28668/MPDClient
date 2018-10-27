/*
 * ScheduleItemDialog.java
 *
 * Created on 09 February 2002, 14:24
 */

package uk.co.alvagem.mpdclient.swing;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import uk.co.alvagem.mpdclient.swing.SchedulePanel.ScheduleEntry;
/**
 * ScheduleItemDialog provides a base class from which all the dialogs
 * in the tool are derived from.  This gives 2 aspects - a common
 * point for changing the basic dialog type and common services 
 * and layout.
 * @author  rbp28668
 */
public class ScheduleItemDialog extends JDialog{

	private static final long serialVersionUID = 1L;
	protected final static Border dialogBorder = new EmptyBorder(15,10,15,10);
	protected final static Border componentBorder = new EmptyBorder(7,5,7,5);

	/** Standard OK/Cancel panel */
    private ButtonBox box = new ButtonBox();    
    /** true if the OK button was clicked, false if cancelled*/
    private boolean edited = false;
    private JButton btnOK;
    private JButton btnCancel;
    
    /** set true if the ok, cancel panel is fully initialised.*/
    private boolean okCancelInit = false;

    /** Creates new ScheduleItemDialog */
    ScheduleItemDialog(Frame parentFrame, ScheduleEntry entry ) {
        super(parentFrame, true); // modal
        setLocationRelativeTo(parentFrame); 
        getRootPane().setBorder(dialogBorder);
        build();
    }
    
    private void build(){
    	setLayout(new BorderLayout());
    	
    	add(initBox(), BorderLayout.EAST);
    	
    }
    
    /**
	 * Method initBox does the full initialisation of the
	 * OK/Cancel panel. This is delayed until the panel is
	 * accessed via getOKCancelPanel() to allow extra buttons
	 * to be added above OK & Cancel.
	 */
    private ButtonBox initBox() {
        btnOK = new JButton("OK");
        btnCancel = new JButton("Cancel");
        box.add(btnOK);
        box.add(btnCancel);
        
        // OK Button
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
				fireOK();
            }
        });
        

        // Cancel Button
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeDialog();
            }
        });

        okCancelInit = true;
        return box;
    }

	/**
	 * Method wasEdited returns whether the user clicked on OK and 
	 * the dialog contents were valid.
	 * @return boolean
	 */
    public boolean wasEdited() {
        return edited;
    }
    
	/**
	 * Method onOK is called when the user clicks the OK button and
	 * the dialog contains valid input (determined by validateInput()).
	 * This must be over-ridden in the sub-class.
	 */
    protected void onOK(){
    	// TODO
    }
    
	/**
	 * Method validateInput checks whether the dialog contains
	 * valid input or not. It must be implemented by the sub-class.
	 * @return boolean, true if the input is valid, false otherwise.
	 */
    protected  boolean validateInput() {
    	return true; // TODO
    };
    
    /** Closes the dialog */
    protected void closeDialog() {
        setVisible(false);
        dispose();
    }

	/**
	 * allows subclasses to simulate pressing the OK button.  This
	 * allows dialogs to implement double click for select & close.
	 */
	protected void fireOK() {
		if(validateInput()) {
			onOK();
			edited = true;
			closeDialog();
		}
	}


    
    
}
