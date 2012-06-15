package setup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Class that allows the user to enter options for commands
 * - the required options are without brackets;
 * - the optional options are between brackets such as "[tag-name]"
 */
public class EnterOptions extends JFrameCommon implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JButton previous;
	private JButton next;
	private JButton finish;
	private JButton cancel;
	private JLabel create;
	private JLabel optional;
	public Deploy screenType;	//NEW, PAUSE, RESUME, DELETE options
	
	//all the required and optional options for all the scenarios
	Option accessKey;
	Option secretKey;
	Option endpoint;
	Option tagName;
	Option instanceType;
	Option keyPair;
	Option outputKeyPairFile;
	Option securityGroup;
	Option deleteKeyPair;
	Option deleteSecurityGroup;
	Option skipElasticIpAssociation;
	
	Option domainName;
	Option packageType;
	Option username;
	Option xeDownloadUrl;

	/**
	 * Initializes the frame and creates each option
	 */
	public EnterOptions() {
		WIDTH = 800;
		HEIGHT = 400;
		
		initFrame("Enter Options");
	  
		//initialize each option with its name, description and default value if it has one
	    accessKey = new Option("access-key","AWS access key ID","");
	    secretKey = new Option("secret-key","AWS secret access key","");
	    endpoint = new Option("endpoint","AWS endpoint","https://ec2.us-west-2.amazonaws.com");
	    tagName = new Option("tag-name*","The name of the instance","");
	    instanceType = new Option("instance-type*","The type of Amazon EC2 instance","t1.micro");
	    keyPair = new Option("key-pair*","Name of the AWS keypair to use","");
	    outputKeyPairFile = new Option("output-key-pair-file*","Filename where the returned key-pair is written","");
	    securityGroup = new Option("security-group*","AWS security group to use","");
	    deleteKeyPair = new Option("delete-key-pair*","Delete the default key pair",new Object[]{"yes","no"},"yes");
	    deleteSecurityGroup = new Option("delete-security-group*","Delete the default security group",new Object[]{"yes","no"},"yes");
	    skipElasticIpAssociation = new Option("skip-elastic-ip-association*","Skip elastic IP association (yes/no)",new Object[]{"yes","no"},"no");
		domainName = new Option("domain-name","Setup an Apache virtual host for the domain","");
		packageType = new Option("package-type*","XE package type",new Object[]{"core","blog","forum"},"core");
		username = new Option("username*","Username to use when connecting to the host","");
		xeDownloadUrl = new Option("xe-download-url*","Alternative URL to download XE package from","");
	    
	    //endpoint is not editable currently
	    endpoint.text.setEditable(false);
	    endpoint.text.setEnabled(false);
	    
	    //initialize buttons and labels
	    create = new JLabel("New Deploy");
	    optional = new JLabel("<html><b>Note</b>: Parameters marked with * are optional.</html>");
		previous = new JButton("Previous");
		next = new JButton("Next");
		finish = new JButton("Finish");
		cancel = new JButton("Cancel");
		
		//set font properties
		create.setFont(Fonts.lucida14);
		create.setHorizontalAlignment(JLabel.CENTER);
		previous.setFont(Fonts.lucida12);
		next.setFont(Fonts.lucida12);
		finish.setFont(Fonts.lucida12);
		cancel.setFont(Fonts.lucida12);
		optional.setFont(Fonts.lucida12);

		//set location and sizes to container
		create.setBounds(0,10,WIDTH,20);
		optional.setBounds(10,290,400,20);
		previous.setBounds(220,320,100,25);
		next.setBounds(350,320,100,25);
		finish.setBounds(350,320,100,25);
		cancel.setBounds(480,320,100,25);
		
		//add buttons and labels to container
		pane.add(create);
		pane.add(previous);
		pane.add(next);
		pane.add(finish);
		pane.add(cancel);
		pane.add(optional);
		
		//add options to container
		accessKey.addToPane(pane, 40);
		secretKey.addToPane(pane, 70);
		endpoint.addToPane(pane, 100);
		tagName.addToPane(pane, 130);
		instanceType.addToPane(pane, 160);
		keyPair.addToPane(pane, 190);
		outputKeyPairFile.addToPane(pane, 220);
		securityGroup.addToPane(pane, 250);
		deleteKeyPair.addToPane(pane, 160);
		deleteSecurityGroup.addToPane(pane, 190);
		skipElasticIpAssociation.addToPane(pane, 160);
		domainName.addToPane(pane, 40);
		packageType.addToPane(pane, 70);
		username.addToPane(pane, 100);
		xeDownloadUrl.addToPane(pane, 130);
		
		//add action listeners to each button
		previous.addActionListener(this);
		next.addActionListener(this);
		finish.addActionListener(this);
        cancel.addActionListener(this);
        
        accessKey.text.requestFocus();
	}
	
	/**
	 * Sets the screen to display
	 * @param screenType NEW,PAUSE,RESUME or DELETE screen type
	 */
	public void setScreen(Deploy screenType) {
		//if new screen, then display previous,next,finish and cancel buttons
		if(screenType == Deploy.NEW) {
			next.setVisible(true);
			finish.setVisible(false);
		} else {	//do not display next button (use finish instead)
			next.setVisible(false);
			finish.setVisible(true);
		}
		
		//display deploy XE fields if screen type is XE
		boolean visibility = (screenType == Deploy.XE);
		domainName.setVisible(visibility);
		packageType.setVisible(visibility);
		username.setVisible(visibility);
		xeDownloadUrl.setVisible(visibility);
		
		//access-key,secret-key,endpoint,tag-name options are only available when deploy xe is not
		accessKey.setVisible(!visibility);
		secretKey.setVisible(!visibility);
		endpoint.setVisible(!visibility);
		tagName.setVisible(!visibility);
		
		//according to screen type set label values and display the according buttons
		//the first 4 options (access-key,secret-key,endpoint and tag-name) are always visible
		switch(screenType) {
		case NEW:
			create.setText("New Deploy");
			instanceType.setVisible(true);
			keyPair.setVisible(true);
			outputKeyPairFile.setVisible(true);
			securityGroup.setVisible(true);
			deleteKeyPair.setVisible(false);
			deleteSecurityGroup.setVisible(false);
			skipElasticIpAssociation.setVisible(false);
			optional.setLocation(10,290);
			break;
		case DELETE:
			create.setText("Delete Deploy");
			finish.setText("Delete");
			instanceType.setVisible(false);
			keyPair.setVisible(false);
			outputKeyPairFile.setVisible(false);
			securityGroup.setVisible(false);
			deleteKeyPair.setVisible(true);
			deleteSecurityGroup.setVisible(true);
			skipElasticIpAssociation.setVisible(false);
			optional.setLocation(10,230);
			break;
		case PAUSE:
			create.setText("Pause Deploy");
			finish.setText("Pause");
			instanceType.setVisible(false);
			keyPair.setVisible(false);
			outputKeyPairFile.setVisible(false);
			securityGroup.setVisible(false);
			deleteKeyPair.setVisible(false);
			deleteSecurityGroup.setVisible(false);
			skipElasticIpAssociation.setVisible(false);
			optional.setLocation(10,170);
			break;
		case RESUME:
			create.setText("Resume Deploy");
			finish.setText("Resume");
			instanceType.setVisible(false);
			keyPair.setVisible(false);
			outputKeyPairFile.setVisible(false);
			securityGroup.setVisible(false);
			deleteKeyPair.setVisible(false);
			deleteSecurityGroup.setVisible(false);
			skipElasticIpAssociation.setVisible(true);
			optional.setLocation(10,200);
			break;
		case XE:
			create.setText("Deploy XE");
			finish.setText("Deploy");
			instanceType.setVisible(false);
			keyPair.setVisible(false);
			outputKeyPairFile.setVisible(false);
			securityGroup.setVisible(false);
			deleteKeyPair.setVisible(false);
			deleteSecurityGroup.setVisible(false);
			skipElasticIpAssociation.setVisible(false);
			optional.setLocation(10,170);
			break;
		}
		this.screenType = screenType;	//retain screen type
	}
	
	/**
	 * Checks if access-key and secret-key fields are not empty
	 * @return true if access-key and secret-key fields are not empty
	 */
	public boolean verifyFields() {
		if(accessKey.text.getText().trim().length() == 0) {
			JOptionPane.showMessageDialog(null, "Please Enter The Access Key", "Empty Access Key Field", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(secretKey.text.getText().trim().length() == 0) {
			JOptionPane.showMessageDialog(null, "Please Enter The Secret Key", "Empty Secret Key Field", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	/**
	 * Ask the user to confirm the option selected
	 */
	public void confirm() {
		/*
		 * Ask the user for a confirmation
		 * If the user confirms, then display the Run Command screen and set
		 * the command type to be run and then hide the current frame
		 */
		switch(screenType) {
		case NEW:
			if(confirm("Create instance?", "Create Instance")) {
				runCommand.setVisible(true);
				((RunCommand)runCommand).runDeployCommands(Deploy.NEW);
				setVisible(false);
			}
			break;
		case DELETE:
			if(confirm("Are you sure you want to delete this instance?", "Delete Instance") &&
			   confirm("When you terminate an instance you will lose all data.\nAre you sure you want to continue?","Delete Instance")) {
				runCommand.setVisible(true);
				((RunCommand)runCommand).runDeployCommands(Deploy.DELETE);
				setVisible(false);
			}
			break;
		case PAUSE:
			if(confirm("Are you sure you want to pause this instance?", "Pause Instance")) {
				runCommand.setVisible(true);
				((RunCommand)runCommand).runDeployCommands(Deploy.PAUSE);
				setVisible(false);
			}
			break;
		case RESUME:
			if(confirm("Are you sure you want to resume this instance?","Resume Instance")) {
				runCommand.setVisible(true);
				((RunCommand)runCommand).runDeployCommands(Deploy.RESUME);
				setVisible(false);
			}
			break;
		}
	}
	
	/**
	 * Verifies if the domain given is in the form xpressengine.org or www.xpressengine.org
	 */
	public boolean verifyDomain(String domain, boolean error) {
		if(domain.length() == 0) {
			if(error)
				JOptionPane.showMessageDialog(null, "Please Enter The Domain Name", "Empty Domain Name Field", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		String parts[] = domain.toLowerCase().split("\\.");
		//if the domain has 2 parts, the first must not be "www"
		if(parts.length < 2 || (parts.length == 2 && parts[0].equals("www"))) {
			if(error)
				JOptionPane.showMessageDialog(null, "Domain Name Example: xpressengine.org or www.xpressengine.org", "Incorrect Domain Name", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	/**
	 * Handles action performed events
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();	//get source object
		if(source == previous) {			//if previous button clicked
			if(screenType == Deploy.XE) {
				setScreen(Deploy.NEW);
			} else {
				previousFrame.setVisible(true);	//display previous screen
				setVisible(false);				//hide current screen
			}
		} else if(source == next) {			//if next button clicked
			if(screenType == Deploy.NEW && verifyFields()) {	//check if required fields not empty
				setScreen(Deploy.XE);
			}
		} else if(source == finish) {		//if finish button clicked
			if(screenType == Deploy.XE) {	//if deploy XE screen is displayed
				String domain = domainName.text.getText().trim();
				if(verifyDomain(domain,true)) {
					//verify hostname and ask user to confirm if he wants to add additional domains
					if(confirm("Do you want to add additional domains?", "Additional Domains")) {
						nextFrame.setVisible(true);	//display next screen
						setVisible(false);			//hide current screen
					} else {
						runCommand.setVisible(true);
						((RunCommand)runCommand).runDeployCommands(Deploy.NEW);
						setVisible(false);
					}
				}
			} else {
				if(verifyFields()) {			//check if required fields not empty
					confirm();					//ask for confirmation
				}
			}
		} else if(source == cancel) {		//if cancel button clicked
			cancel();						//close all screens
		}
	}
}
