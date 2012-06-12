package setup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

/**
 * Class that displays the screen that allows to choose amongst the 4 operations:
 * - New Deploy
 * - Pause Deploy
 * - Resume Deploy
 * - Delete Deploy
 */
public class ChooseOperation extends JFrameCommon implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JButton previous;
	private JButton next;
	private JButton cancel;
	private JLabel choose;
	
	//radio buttons with the options and a button group for them
	private JRadioButton newDeploy;
	private JRadioButton pauseDeploy;
	private JRadioButton resumeDeploy;
	private JRadioButton deleteDeploy;
	private ButtonGroup buttonGroup;
	
	public ChooseOperation() {
		initFrame("Choose Operation");
		
		//initialize components
		choose = new JLabel("Select Operation to Perform");
		newDeploy = new JRadioButton("New Deploy");
		pauseDeploy = new JRadioButton("Pause Deploy");
		resumeDeploy = new JRadioButton("Resume Deploy");
		deleteDeploy = new JRadioButton("Delete Deploy");

		previous = new JButton("Previous");
		next = new JButton("Next");
		cancel = new JButton("Cancel");

		//set label font and horizontal alignment
        choose.setHorizontalAlignment(JLabel.CENTER);
        
        choose.setFont(Fonts.lucida14);
        previous.setFont(Fonts.lucida12);
        next.setFont(Fonts.lucida12);
        cancel.setFont(Fonts.lucida12);
        newDeploy.setFont(Fonts.lucida12);
        pauseDeploy.setFont(Fonts.lucida12);
        resumeDeploy.setFont(Fonts.lucida12);
        deleteDeploy.setFont(Fonts.lucida12);
		
        //add each button to the button group
		buttonGroup = new ButtonGroup();
		buttonGroup.add(newDeploy);
		buttonGroup.add(pauseDeploy);
		buttonGroup.add(resumeDeploy);
		buttonGroup.add(deleteDeploy);
		
		newDeploy.setSelected(true);	//new deploy is checked by default
		
		//set positions for each component
		choose.setBounds(0,50,WIDTH,20);
		newDeploy.setBounds(150,90,150,20);
		pauseDeploy.setBounds(150,110,150,20);
		resumeDeploy.setBounds(150,130,150,20);
		deleteDeploy.setBounds(150,150,150,20);
		previous.setBounds(20,200,100,25);
		next.setBounds(150,200,100,25);
		cancel.setBounds(280,200,100,25);
		
		//add each component to the container
		pane.add(choose);
		pane.add(newDeploy);
		pane.add(pauseDeploy);
		pane.add(resumeDeploy);
		pane.add(deleteDeploy);
		pane.add(previous);
		pane.add(next);
		pane.add(cancel);
		
		//add action listeners to each button
		previous.addActionListener(this);
		next.addActionListener(this);
        cancel.addActionListener(this);
	}

	/**
	 * Handles action performed events
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();	//get event source
		if(source == previous) {			//if previous button clicked
			previousFrame.setVisible(true);	//display previous screen
			setVisible(false);				//hide current screen
		} else if(source == next) {			//if next button clicked
			//check which option is selected and display screen according to action
			if(newDeploy.isSelected()) {
				((EnterOptions)nextFrame).setScreen(Deploy.NEW);
			} else if(pauseDeploy.isSelected()) {
				((EnterOptions)nextFrame).setScreen(Deploy.PAUSE);
			} else if(resumeDeploy.isSelected()) {
				((EnterOptions)nextFrame).setScreen(Deploy.RESUME);
			} else if(deleteDeploy.isSelected()) {
				((EnterOptions)nextFrame).setScreen(Deploy.DELETE);
			}
			nextFrame.setVisible(true);
			setVisible(false);
		} else if(source == cancel) {		//if cancel is clicked
			cancel();						//ask for confirmation and exit if confirmed
		}
	}
}
