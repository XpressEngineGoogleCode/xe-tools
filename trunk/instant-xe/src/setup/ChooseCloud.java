package setup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

/**
 * Class that displays the "Choose Cloud Service" screen
 * Currently, only AWS option is supported
 */
public class ChooseCloud extends JFrameCommon implements ActionListener {	
	private static final long serialVersionUID = 1L;
	private JButton next;
	private JButton cancel;
	private JRadioButton aws;
	private JLabel choose;
	private JLabel supported;
	
	/**
	 * Constructor that initializes the frame, adds the buttons, labels and radio button(s)
	 */
	public ChooseCloud() {
		initFrame("Choose Cloud Service");
		
		//initialize all components
        choose = new JLabel("Select Cloud Service");
        supported = new JLabel("Currently, AWS is the only supported service");
        aws = new JRadioButton("AWS");
        next = new JButton("Next");
        cancel = new JButton("Cancel");
        
        aws.setSelected(true);	//by default, AWS service is selected    
 
        //set font and horizontal alignment for labels
        choose.setFont(Fonts.lucida14);
        supported.setFont(Fonts.lucida12);
        aws.setFont(Fonts.lucida12);
        next.setFont(Fonts.lucida12);
        cancel.setFont(Fonts.lucida12);
        
        choose.setHorizontalAlignment(JLabel.CENTER);
        supported.setHorizontalAlignment(JLabel.CENTER);
        
        //set components location
        choose.setBounds(0,50,WIDTH,20);
        aws.setBounds((WIDTH-60)/2,90,60,20);
        supported.setBounds(0,130,WIDTH,20);
        next.setBounds(80,200,100,25);
        cancel.setBounds(220,200,100,25);
        
        //add components to pane
        pane.add(choose);
        pane.add(supported);
        pane.add(aws);
        pane.add(next);
        pane.add(cancel);
        
        //add action listener to the 2 buttons
        next.addActionListener(this);
        cancel.addActionListener(this);
	}

	/**
	 * Handles action performed events
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();	//get event source
		if(source == next) {				//if next button clicked
			nextFrame.setVisible(true);		//display next frame
			setVisible(false);				//hide current frame
		} else if(source == cancel) {		//if cancel is clicked
			cancel();						//ask for confirmation and exit if confirmed
		}
	}
}
