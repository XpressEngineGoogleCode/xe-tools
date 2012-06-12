package setup;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Class that allows the user to enter up to MAX_DOMAINS domains
 */
public class AssociateDomain extends JFrameCommon implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final int MAX_DOMAINS = 5;	//maximum number of domains
	private JButton previous;
	private JButton finish;
	private JButton cancel;
	private JLabel domain;
	
	private JButton extraDomain;	//button used to add another domain
	Vector<Domain> domains;			//current domains
	
	/**
	 * Class that holds the data of a domain
	 * name - Label that displays Domain #
	 * text - the value given for the domain
	 */
	class Domain {
		JLabel name;
		JTextField text;
		
		/**
		 * Initializes components
		 * @param name value to display alongside text field
		 */
		public Domain(String name) {
			this.name = new JLabel(name);
			this.text = new JTextField(20);
		}
		
		/**
		 * Adds the domain to the container
		 * @param pane	container where to add the components
		 * @param y at what height should the components be added
		 */
		public void addToPane(Container pane, int y) {
			//set locations and width/height
			name.setBounds(175,y,90,30);
			text.setBounds(275,y,250,30);
			//add components to container
			pane.add(name);
			pane.add(text);
		}
	}

	/**
	 * Initializes components, sets their properties and adds them to the frame container
	 */
	public AssociateDomain() {
		WIDTH = 700;
		domains = new Vector<Domain>();
		
		//set frame properties
		initFrame("Associate Additional Domain(s)");

		//initialize buttons and labels
		domain = new JLabel("Add Additional Domains");
	    previous = new JButton("Previous");
	    finish = new JButton("Deploy");
		cancel = new JButton("Cancel");
		extraDomain = new JButton("+");
		
		//set font properties
	    domain.setFont(Fonts.lucida14);
	    domain.setHorizontalAlignment(JLabel.CENTER);
	    
	    extraDomain.setFont(Fonts.lucida12);
	    previous.setFont(Fonts.lucida12);
	    finish.setFont(Fonts.lucida12);
	    cancel.setFont(Fonts.lucida12);
	
		//set positions and sizes for components
	    domain.setBounds(0,10,WIDTH,20);
		previous.setBounds(170,220,90,25);
		finish.setBounds(290,220,90,25);
		cancel.setBounds(410,220,90,25);
		extraDomain.setBounds(540,50,40,30);
		
		//add first domain
		Domain d = new Domain("Domain 1");
		domains.add(d);
		
		//add components to container
		d.addToPane(pane, 50);
		pane.add(extraDomain);
		pane.add(domain);
		pane.add(previous);
		pane.add(finish);
		pane.add(cancel);
		
		//add listeners to buttons
		extraDomain.addActionListener(this);
		previous.addActionListener(this);
		finish.addActionListener(this);
        cancel.addActionListener(this);
	}
	
	/**
	 * Handles action performed events
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();	//get event source
		if(source == previous) {			//if next button clicked
			((EnterOptions)previousFrame).setScreen(Deploy.XE);
			previousFrame.setVisible(true);	//display previous frame
			setVisible(false);				//hide current frame
		} else if(source == finish) {		//if finish button selected
			runCommand.setVisible(true);
			((RunCommand)runCommand).runDeployCommands(Deploy.NEW);
			setVisible(false);
		} else if(source == cancel) {	//if cancel button clicked
			cancel();					//close everything	
		} else if(source == extraDomain) {	//if the user wants to add an extra domain
			if(domains.size() < MAX_DOMAINS) {	//if limit not reached
				//create another domain
				Domain d = new Domain("Domain "+(domains.size()+1));
				domains.add(d);
				
				//add domain to pane and request focus
				d.addToPane(pane, 20+(domains.size()*30));
				d.text.requestFocus();
		
				if(domains.size() == MAX_DOMAINS) {
					//if limit was reached, then hide add domain button
					extraDomain.setVisible(false);
				} else {
					//set new location for add domain button
					extraDomain.setLocation(540,20+(domains.size()*30));
				}
				pane.repaint();	//redisplay components
			}
		}
	}
}
