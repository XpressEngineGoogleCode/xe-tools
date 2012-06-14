package setup;

import java.util.ArrayList;
import java.util.Vector;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import main.Dispatcher;

/**
 * Runs the commands and displays the output (stdout/stderr) of the commands
 */
public class RunCommand extends JFrameCommon implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	RunProcess run;	//thread that runs the commands
	
	//components to display (a text pane and a scroll pane that contains it)
	JTextPane output;
	StyledDocument text;
	JScrollPane outputPane;
	
	//previous/next buttons
	private JButton previous;
	private JButton finish;
	
	//links to the frames previous to the current frame (either enter options or associate domain)
	EnterOptions enterOptions;
	AssociateDomain associateDomain;
	
	SimpleAttributeSet errorStyle;
	SimpleAttributeSet okStyle;
	SimpleAttributeSet boldStyle;
	
    PipedOutputStream postream;
    PipedOutputStream pestream;
    
    //get its output and errors
    BufferedReader stdInput;
    BufferedReader stdError;
	
	/**
	 * Initializes commands and set properties
	 */
	public RunCommand() {
        postream = new PipedOutputStream();
        pestream = new PipedOutputStream();
        System.setOut(new PrintStream(postream));
        System.setErr(new PrintStream(pestream));
        
        try {
			stdInput = new BufferedReader(new InputStreamReader(new PipedInputStream(postream)));
			stdError = new BufferedReader(new InputStreamReader(new PipedInputStream(pestream)));
		} catch (IOException e) {
			e.printStackTrace();
		}
       
		WIDTH = 700;
		HEIGHT = 600;
		initFrame("Running Commands");
		
		errorStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(errorStyle, Color.RED);
		StyleConstants.setBold(errorStyle, true);
		
		okStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(okStyle, Color.GREEN);
		StyleConstants.setBold(okStyle, true);
		
		boldStyle = new SimpleAttributeSet();
		StyleConstants.setBold(boldStyle,true);
		
		//display text area and set properties
		output = new JTextPane();
		text = output.getStyledDocument();
		outputPane = new JScrollPane(output);
		    
		//initialize buttons
	    previous = new JButton("Main Menu");
		finish = new JButton("Finish");
		
		output.setFont(Fonts.lucida12);
		previous.setFont(Fonts.lucida12);
		finish.setFont(Fonts.lucida12);
		
		//set locations and size of the options
		previous.setBounds(195,540,120,25);
		finish.setBounds(385,540,120,25);
	    outputPane.setBounds(10,10,680,520);
	    
	    //buttons are disabled by default
	    previous.setEnabled(false);
	    finish.setEnabled(false);
	    
	    //add components to container
	    pane.add(outputPane);
		pane.add(previous);
		pane.add(finish);
	}
	
	/**
	 * Appends a string to the output text pane
	 * @param s string to be appended
	 * @param attr attributes for the string (such as bold, red color (errors) or green color (ok))
	 */
	public void append(String s,AttributeSet attr) {
	   try {
	      text.insertString(text.getLength(),s,attr);
	   } catch(BadLocationException exc) {
	      exc.printStackTrace();
	   }
	}
	
	/**
	 * Clears the contents of the text pane
	 */
	public void clear() {
	   try {
		   text.remove(0, text.getLength());
	   } catch(BadLocationException exc) {
	      exc.printStackTrace();
	   }
	}
	
	/**
	 * Set the previous screens
	 * @param enterOptions		screen in which options are entered
	 * @param associateDomain	screen in which domains are given
	 */
	public void setPreviousFrames(EnterOptions enterOptions, AssociateDomain associateDomain) {
		this.enterOptions = enterOptions;
		this.associateDomain = associateDomain;
	}
	
	/**
	 * Runs a given type of command
	 * @param command command type (NEW,PAUSE,RESUME,DELETE)
	 */
	public void runDeployCommands(Deploy command) {
		//disable finish/previous buttons
		disableButtons();

	    //according to command call correct type of command
		switch(command) {
		case NEW:
			newDeployCommand();
			break;
		case PAUSE:
			pauseDeployCommand();
			break;
		case RESUME:
			resumeDeployCommand();
			break;
		case DELETE:
			deleteDeployCommand();
			break;
		}
	}
	
	/**
	 * Removes quotes from within a string
	 * and adds quotes around it if it is not empty
	 */
	public String addQuotes(String str) {
		str = str.replaceAll("\"","");
		str = str.trim();
		return str;
	}
	
	/**
	 * Adds the option to the command string
	 * @param command command to be executed
	 * @param option option value to be added
	 */
	public void addToCommand(ArrayList<String> command, Option option) {
		String value;
		//if it is a select box get selected value
		if(option.isSelectBox) {
			value = option.select.getSelectedItem().toString();
		} else {	//else, get text from text field
			value = addQuotes(option.text.getText());
		}
		if(value.length() > 0) {
			command.add(option.optionName);
			command.add(value);
		}
	}
	
	/**
	 * Returns the default command with access-key,secret-key and endpoint added
	 * (Adds each required field to the command)
	 * @param commandType command type to pass to executing jar (setup-aws or deploy-xe)
	 * @param subCommand secondary command (create_instance, assign_address etc)
	 * @return the default command (command with access key, secret key and endpoint)
	 */
	public ArrayList<String> defaultCommand(String commandType, String subCommand) {
		ArrayList<String> command = new ArrayList<String>();
		command.add(commandType);
		command.add(subCommand);
		addToCommand(command,enterOptions.accessKey);
		addToCommand(command,enterOptions.secretKey);
		addToCommand(command,enterOptions.endpoint);
		return command;
	}
	
	/**
	 * Runs a New Deploy command
	 */
	public void newDeployCommand() {
		//create command has access-key,secret-key,endpoint and any of the 5 optional parameters
		ArrayList<String> createCommand = defaultCommand("setup-aws","create_instance");
		addToCommand(createCommand,enterOptions.tagName);
		addToCommand(createCommand,enterOptions.instanceType);
		addToCommand(createCommand,enterOptions.keyPair);
		addToCommand(createCommand,enterOptions.outputKeyPairFile);
		addToCommand(createCommand,enterOptions.securityGroup);
		runConsoleCommand(createCommand,CommandType.CREATE_INSTANCE);	//run create instance command
		
		//address command has only the access-key,secret-key,endpoint parameters
		ArrayList<String> addressCommand = defaultCommand("setup-aws","assign_address");
		runConsoleCommand(addressCommand,CommandType.ASSIGN_ADDRESS);	//run associate address command
		
		//get number of domains entered by the user
		int nr_domains = associateDomain.domains.size();
		String mainDomain = enterOptions.domainName.text.getText().replaceAll("\"","").trim();
		
		//retain all domains added
		Vector<String> domains = new Vector<String>();
		
		if(mainDomain.length() > 0) {
			//run for domain
			ArrayList<String> mainDomainCommand = defaultCommand("setup-aws","associate_domain");
			mainDomainCommand.add("domain-name");
			mainDomainCommand.add(mainDomain);
			runConsoleCommand(mainDomainCommand,CommandType.ASSOCIATE_DOMAIN);
			
			//run for main domain with "www" before it
			ArrayList<String> mainWWWDomainCommand = defaultCommand("setup-aws","associate_domain");
			mainWWWDomainCommand.add("domain-name");
			mainWWWDomainCommand.add("www."+mainDomain);
			runConsoleCommand(mainWWWDomainCommand,CommandType.ASSOCIATE_DOMAIN);
			
			//add domains to array
			domains.add(mainDomain);
			domains.add("www."+mainDomain);
		}
		
		//for each domain
		for(int i=0;i<nr_domains;i++) {
			//remove all double quotes from the domain and trim it
			String domain = associateDomain.domains.elementAt(i).text.getText().replaceAll("\"","").trim().toLowerCase();
			
			//if domain entered
			if(domain.length() > 0 && !domain.equals(mainDomain) && !domain.equals("www."+mainDomain)) {
				String otherDomain;	//other domain is with www or without depending on the default domain

				//if domain starts with "www." remove it from other domain
				if(domain.startsWith("www.")) {
					otherDomain = domain.substring(4);
				} else {	//otherwise, add "www." to other domain
					otherDomain = "www."+domain;
				}
				
				//add double quotes around each domain and add the domains if they were not added already
				if(!domains.contains(domain) && enterOptions.verifyDomain(domain,false)) {
					domains.add(domain);
					ArrayList<String> domainCommand = defaultCommand("setup-aws","associate_domain");
					domainCommand.add("domain-name");
					domainCommand.add(domain);
					runConsoleCommand(domainCommand,CommandType.ASSOCIATE_DOMAIN);
				}
				if(!domains.contains(otherDomain) && enterOptions.verifyDomain(otherDomain,false)) {
					domains.add(otherDomain);
					ArrayList<String> domainCommand = defaultCommand("setup-aws","associate_domain");
					domainCommand.add("domain-name");
					domainCommand.add(otherDomain);
					runConsoleCommand(domainCommand,CommandType.ASSOCIATE_DOMAIN);
				}
			}
		}
		
		//form and execute deploy XE command
		ArrayList<String> xeCommand = new ArrayList<String>();
		xeCommand.add("deploy-xe");
		xeCommand.add("hostname");
		xeCommand.add("elastic-ip");
		xeCommand.add("private-key-file");
		xeCommand.add("private-key-file-path");
		addToCommand(xeCommand,enterOptions.domainName);
		addToCommand(xeCommand,enterOptions.packageType);
		addToCommand(xeCommand,enterOptions.username);
		addToCommand(xeCommand,enterOptions.xeDownloadUrl);
		runConsoleCommand(xeCommand,CommandType.DEPLOY_XE);	//run deploy xe command
	}
	
	/**
	 * Runs a Pause Deploy command
	 */
	public void pauseDeployCommand() {
		ArrayList<String> pauseCommand = defaultCommand("setup-aws","pause_instance");
		addToCommand(pauseCommand,enterOptions.tagName);
		runConsoleCommand(pauseCommand,CommandType.PAUSE_INSTANCE);
	}
	
	/**
	 * Runs the Resume Deploy command
	 */
	public void resumeDeployCommand() {
		ArrayList<String> resumeCommand = defaultCommand("setup-aws","resume_instance");
		addToCommand(resumeCommand,enterOptions.tagName);
		addToCommand(resumeCommand,enterOptions.skipElasticIpAssociation);
		runConsoleCommand(resumeCommand,CommandType.RESUME_INSTANCE);
	}	
	
	/**
	 * Runs the Delete Deploy command
	 */
	public void deleteDeployCommand() {
		//first, remove domain
		ArrayList<String> removeDomainCommand = defaultCommand("setup-aws","remove_domain");
		addToCommand(removeDomainCommand,enterOptions.tagName);
		runConsoleCommand(removeDomainCommand,CommandType.REMOVE_DOMAIN);
		
		//then, remove associated address
		ArrayList<String> removeAddressCommand = defaultCommand("setup-aws","remove_address");
		addToCommand(removeAddressCommand,enterOptions.tagName);
		runConsoleCommand(removeAddressCommand,CommandType.REMOVE_ADDRESS);
		
		//then delete the instance
		ArrayList<String> deleteCommand = defaultCommand("setup-aws","delete_instance");
		addToCommand(deleteCommand,enterOptions.tagName);
		addToCommand(deleteCommand,enterOptions.deleteKeyPair);
		addToCommand(deleteCommand,enterOptions.deleteSecurityGroup);
		runConsoleCommand(deleteCommand,CommandType.DELETE_INSTANCE);
	}
	
	/**
	 * Runs a command and displays its output in the text area
	 */
	class RunProcess extends Thread {
		Vector<Command> commands;		//list of commands to be executed
		String elasticIp;				//elastic ip obtained
		String keyPairFile;				//output key-pair file full path
		boolean finished;				//true if program has finished
		
		/**
		 * Set the first command to be executed
		 */
		public RunProcess(ArrayList<String> command,CommandType commandType) {
			commands = new Vector<Command>();
			commands.add(new Command(command,commandType));
			finished = false;
		}
		
		/**
		 * Adds a command to be executed
		 */
		public void addCommand(ArrayList<String> command,CommandType commandType) {
			commands.add(new Command(command,commandType));
		}
		
		/**
		 * Returns the string value after the search value given up until the end of the line
		 * @param text		text in which to search
		 * @param search	search string to find
		 * @param delimiter	delimiter between search string and value
		 * @return the value after the search string and delimiter
		 */
		public String getValue(String text, String search, String delimiter) {
			String result = "";
			int pos = text.indexOf(search+delimiter);
			if(pos != -1) {
    			result = text.substring(pos+search.length()+delimiter.length()).trim();
    			if(result.contains("\n")) {
    				result = result.substring(0,result.indexOf("\n")).trim();
    			}
    		}
			return result;
		}
		
		/**
		 * Display the important data from the output
		 * @param output output of the commands that were executed
		 */
		public void parseOutput(String output) {
			String saveToFile = "";
			String lines[] = output.split("\n");	//split each line
			boolean displayRecords = true;			//true if NS/SOA records should be displayed
			
			//for each line
			for(int i=0;i<lines.length;i++) {
				String line = lines[i];
				
				//bold the value after ":" symbol
				if(line.contains("The new allocated IP address is")
					|| line.contains("The random generated password for MySQL is"))
				{
					String parts[] = line.split(":");
					if(parts.length == 2) {
						append(parts[0]+":",null);
						append(parts[1]+"\n",boldStyle);
					} else {
						append(line+"\n",null);
					}
					saveToFile += line+"\n";
		    	}
				// bold the path
				else if(line.contains("The output file for key pair is"))
				{
					line = line.replace("\\.\\","\\");
					String parts[] = line.split(":");
					if(parts.length >= 2) {
						append(parts[0]+":",null);
						append(line.substring(line.indexOf(":")+1)+"\n",boldStyle);
					} else {
						append(line+"\n",null);
					}
					saveToFile += line+"\n";
		    	}
				else if(line.contains("The Name of the new instance is")
					|| line.contains("The public DNS of the new created instance is"))
				{
					append(line+"\n",null);
					saveToFile += line+"\n";
	    		}
				
				//display NS records
				if(displayRecords && line.contains("NS record")) {
					append(line+"\n",null);
					saveToFile += line+"\n";
					do {
						i++;
						line = lines[i];
						if(line.trim().length() > 0) {
							append(line+"\n",boldStyle);
							saveToFile += line+"\n";
						}
					} while(i < lines.length-1 && line.trim().length() > 0);
					append("\n",null);
					saveToFile += "\n";
				}
				//display SOA records
				if(displayRecords && line.contains("SOA record")) {
					displayRecords = false;
					do {
						append(line+"\n",null);
						saveToFile += line+"\n";
						i++;
						line = lines[i];
					} while(i < lines.length-1 && line.trim().length() > 0);
					append("\n",null);
					saveToFile += "\n";
				}
			}
			
			try {
				//save all output to a file
				File out = new File("instant-xe-configuration.txt");
				if(out.exists()) {
					out = File.createTempFile("instant-xe-configuration", ".txt", new File("."));
				}
				String fileName = out.getName();
				BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
				bw.write(saveToFile);
				bw.flush();
				bw.close();

				append("The above information was saved to file: ",null);
				append(out.getAbsolutePath().replace("\\.\\","\\")+"\n\n",boldStyle);
			} catch(Exception err) {
				
			}
		}
		
		/**
		 * Runs a command using Dispatcher
		 */
		class RunDispatcher extends Thread {
			String[] args;

			public RunDispatcher(String args[]) {
				this.args = args;
			}
			
			public void run() {
        		//execute command
	            try {
					Dispatcher.main(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
	            //print finished message (important because it is used to know when a command has finished)
	            System.out.println("Command Execution Finished\n");
			}
		}
				
		/**
		 * Runs the thread that checks for commands in list, then executes the first one and displays output
		 */
		public void run() {
			Command command = null;	//holds the command to be executed
			boolean error;			//true if an error occured (don't execute any other commands)
            String commandOutput = "";
            String errorOutput = "";
            String line = "";
	
	        try {
	        	//while thread has not finished
	        	while(!finished) {
	        		error = false;	//no error occurred so far
	        		
		        	command = null;
		        		
		        	//wait until a command is in the list
		        	if(commands.size() == 0) {
		        		try {
		        			synchronized (commands) {
		        				commands.wait();
		        			}
		        		} catch(Exception ie) {
		        			
		        		}
		        	}
		        	
	        		//try to get a command to execute
		        	synchronized(commands) {
		        		if(commands.size() > 0) {
				        	command = commands.elementAt(0);
				        	commands.removeElementAt(0);
		        		}
		        	}
		        		  
		        	//display message according to command type
		        	if(command != null) {
			            switch(command.commandType) {
			            case CREATE_INSTANCE:
			            	append("Creating instance.........",null);
			            	keyPairFile = "";
			            	break;
			            case ASSIGN_ADDRESS:
			            	append("Assigning address.......",null);
			            	elasticIp = "";
			            	break;
			            case ASSOCIATE_DOMAIN:
			            	if(!commandOutput.contains("Executing associate domain command"))
			            		append("Associating domains...",null);
			            	break;
			            case DEPLOY_XE:
			            	command.cmd.set(2,elasticIp);
			            	command.cmd.set(4,keyPairFile.replace("\\.\\","\\"));
			            	break;
			            case PAUSE_INSTANCE:
			            	append("Pausing instance......",null);
			            	break;
			            case RESUME_INSTANCE:
			            	append("Resuming instance.....",null);
			            	break;
			            case REMOVE_DOMAIN:
			            	append("Removing domains......",null);
			            	break;
			            case REMOVE_ADDRESS:
			            	append("Removing address.......",null);
			            	break;
			            case DELETE_INSTANCE:
			            	append("Deleting instance..........",null);
			            	break;
			            }
			            
			            //get all arguments in a String[] from the command
			            Object[] parameters = command.cmd.toArray();
			            String[] args = new String[parameters.length];
			            for(int i=0;i<parameters.length;i++) {
			            	args[i] = parameters[i].toString();
			            }
			            
		        		//execute command
			            RunDispatcher runCmd = new RunDispatcher(args);
			            runCmd.start();

			            while(true) {
			            	if(stdInput.ready()) {
			            		//read output character with character
			            		while(stdInput.ready()) {
			            			char c = (char)stdInput.read();
			            			line += c;
			            			if(c == '\n') {	//display only after new lines			            				
			            				//retain the elastic ip
	    			            		if(command.commandType == CommandType.ASSIGN_ADDRESS && line.contains("allocated IP address is:")) {
	    			            			elasticIp = getValue(line,"allocated IP address is",":");
	    			            		}
	    			            		//retain the private key file
	    			            		if(command.commandType == CommandType.CREATE_INSTANCE && line.contains("The output file for key pair is:")) {
	    			            			keyPairFile = getValue(line,"The output file for key pair is",":");
	    			            		}
	    			            		//display ok or failed according to output of the current command
	    			            		if(command.commandType == CommandType.DEPLOY_XE && line.contains("...")) {
	    			            			int pos = line.indexOf("OK");
	    			            			if(pos != -1) {
		    			            			append("[OK]",okStyle);
		    			            			append(line.substring(pos+2),null);
	    			            			} else {
	    			            				pos = line.indexOf("FAILED");
	    			            				if(pos != -1) {
	    			            					append("[FAILED]",errorStyle);
		    			            				append(line.substring(pos+6),null);
	    			            				}
	    			            			}
	    			            		}
	    			            		commandOutput += line;
	    			            		line = "";
			            			} else if(!stdInput.ready() && command.commandType == CommandType.DEPLOY_XE) {
			            				if(line.contains(".....")) {
			            					append(line,null);
			            					line = "...";
			            				}
			            			}
			            		}
			            	} else if(stdError.ready()) {	
			            		//read output and error character by character
			            		while(stdInput.ready()) {
			            			char c = (char)stdInput.read();
			            			commandOutput += c;
			            		}
			            		while(stdError.ready()) {
			            			char c = (char)stdError.read();
			            			errorOutput+=c;
			            		}
			            		error = true;	//error occurred
			            	}
			            	//if there is no more output or errors to display and the process has exited, then exit loop
			            	if(!stdError.ready() && !stdInput.ready()) {
			            		if(commandOutput.contains("Command Execution Finished")) {
			            			commandOutput = commandOutput.replace("Command Execution Finished","");
			            			break;
			            		}
			            	}
			            	try {
				        		Thread.sleep(1000);
				        	} catch(Exception err) {
				        		
				        	}
			            }
			            //display if command failed or if it is ok
			        	if(error) {
			        		append("[FAILED]\n",errorStyle);
			        	} else {
			        		if(command.commandType != CommandType.DEPLOY_XE) {
			        			if(command.commandType != CommandType.ASSOCIATE_DOMAIN) {
			        				append("[OK]\n",okStyle);
			            		} else {
			            			if(commands.size() == 0 || commands.elementAt(0).commandType != CommandType.ASSOCIATE_DOMAIN) {
			            				append("[OK]\n",okStyle);
			            			}
			            		}
			        		}
			        		//display final status
			        		switch(command.commandType) {
			        		case DELETE_INSTANCE:
			        			append("\nInstance has been succesfully deleted.",null);
			        			break;
			        		case DEPLOY_XE:
			        			parseOutput(commandOutput);
			        			append("XE has successfully been deployed to the remote machine. You can now proceed with the installation phase.",null);
			        			break;
			        		case PAUSE_INSTANCE:
			        			append("\nInstance is now paused.",null);
			        			break;
			        		case RESUME_INSTANCE:
			        			append("\nInstance has resumed.",null);
			        			break;
			        		}
			        	}
			        	//display error message
	            		String errorMessage = getValue(errorOutput,"AWS Error Code",":");
	            		if(errorMessage.length() == 0) {
	            			errorMessage = getValue(errorOutput,"Caused by",":");
	            			if(errorMessage.length() == 0) {
	            				append(errorOutput,null);
		            		} else {
		            			if(errorMessage.contains("IndexOutOfBoundsException")) {
		            				append("Error: No instance has been deployed.",null);
		            			} else {
		            				append("Error: "+errorMessage+"\n\n",null);
		            			}
		            		}
	            		} else {
	            			//display error in a friendly manner
	            			String awsError = errorMessage.split(",")[0].trim();
	            			if(awsError.equals("AuthFailure")) {
	            				append("Error: Access Key or Secret Key is incorrect.",null);
	            			} else if(awsError.equals("InvalidDomainName")) {
	            				append("Error: Domain Name is incorrect (Example of domain name: malux.org or www.malux.org).",null);
	            			} else {
	            				append("Error: "+errorMessage.split(",")[0].trim()+"\n\n",null);
	            			}
	            		}
			        	
			        	//if an error occurred, ignore other commands
			            if(error) {
			            	synchronized(commands) {
			            		if(command.commandType != CommandType.ASSOCIATE_DOMAIN) {
			            			commands.clear();
			            		} else {
			            			while(commands.size() > 0 && commands.elementAt(0).commandType == CommandType.ASSOCIATE_DOMAIN) {
			            				commands.removeElementAt(0);
			            			}
			            		}
			            	}
			            }
		        	}
		            //if no more commands and previous button is not enabled, then enable buttons
		            if(commands.size() == 0) {
		            	if(previous.isEnabled() == false) {
		            		enableButtons();
		            		commandOutput = "";
		                    errorOutput = "";
		            	}
		            }
	        	}
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
	}
	
	/**
	 * Enable previous/finish buttons
	 */
	public void enableButtons() {
		previous.setEnabled(true);
	    finish.setEnabled(true);
	    
	    previous.addActionListener(this);
	    finish.addActionListener(this);
	}
	
	/**
	 * Disable previous/finish buttons
	 */
	public void disableButtons() {
		previous.setEnabled(false);
	    finish.setEnabled(false);
	    
	    previous.removeActionListener(this);
	    finish.removeActionListener(this);
	}
	
	/**
	 * Runs a console command
	 * @param command command to be executed
	 * @param commandType type of command to execute (from create_instance to delete_instance)
	 */
	public void runConsoleCommand(ArrayList<String> command,CommandType commandType) {
        if(run == null) {	//if run thread is not initialized
        	run = new RunProcess(command,commandType);	//create a new run thread
        	run.start();	//start run thread
        } else {
        	synchronized(run.commands) {	//synchronize based on commands vector
        		run.addCommand(command,commandType);	//add a command to the thread
        		run.commands.notify();
        	}
        }
	}
	
	/**
	 * Handles action performed events
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();	//get source object
		if(source == previous) {			//if previous button clicked
			previousFrame.setVisible(true);	//display previous frame
			setVisible(false);				//hide current frame
			clear();						//clear text
		} else if(source == finish) {		//if finish button clicked
			((JFrameCommon)previousFrame).close();	//close everything
		}
	}
}
