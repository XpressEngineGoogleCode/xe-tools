package setup;

/**
 * Class that creates each frame and sets the precedence (previous-next)
 * relationships between them
 */
public class Main {

	public static void main(String[] args) {
		//try to set look and feel
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {

        }
        
        //create each interface class
		ChooseCloud chooseCloud = new ChooseCloud();				//used to choose cloud service
        ChooseOperation chooseOperation = new ChooseOperation();	//used to choose operation to perform
        EnterOptions enterOptions = new EnterOptions();				//used to enter the options for chosen operation
        AssociateDomain associateDomain = new AssociateDomain();	//used to enter the domains to associate
        
        RunCommand runCommand = new RunCommand();					//class that runs a command and displays the output
        
        //set relationships: Choose Cloud -> Choose Operation -> Enter Options [-> Associate Domain] -> Run Command
        chooseCloud.setNext(chooseOperation);
        chooseOperation.setPrevious(chooseCloud);
        chooseOperation.setNext(enterOptions);
        enterOptions.setPrevious(chooseOperation);
        enterOptions.setNext(associateDomain);        
        associateDomain.setPrevious(enterOptions);  
        
        //set Run Command screen to the frames that can run commands
        associateDomain.setRun(runCommand);
        enterOptions.setRun(runCommand);
        chooseOperation.setRun(runCommand);
        
        runCommand.setPreviousFrames(enterOptions,associateDomain);
        runCommand.setPrevious(chooseOperation);
        
        chooseCloud.setVisible(true);	//display first screen
	}
	
}
