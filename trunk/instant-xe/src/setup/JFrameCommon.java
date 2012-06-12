package setup;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Class that holds the common elements of all the elements displayed
 */
public class JFrameCommon extends JFrame {
	private static final long serialVersionUID = 1L;
	protected int WIDTH = 400;		//default frame width
	protected int HEIGHT = 300;		//default frame height
	
	JFrame nextFrame = null;		//screen that follows after current one
	JFrame previousFrame = null;	//screen that is before current one
	JFrame runCommand = null;		//screen that runs commands and displays their output
	Container pane;					//container that holds the GUI components
	
	/**
	 * Sets the frame properties such as title, not resizable, centered location on screen and no layout
	 * @param title JFrame title
	 */
	public void initFrame(String title) {
		setResizable(false);				//frame is not resizable
		setTitle("Instant XE - "+title);	//set frame title
		setSize(WIDTH,HEIGHT);				//set width,height
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//set close operation
		
		try {
			setIconImage(new ImageIcon(getClass().getResource("/xe.png")).getImage());
		} catch(NullPointerException err) {
			setIconImage(new ImageIcon("photos/xe.png").getImage());
		}
		
		//set the location of the frame in the middle of the screen
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension size = getSize();
	    setLocation((dim.width-size.width)/2, (dim.height-size.height)/2);

	    //get container and set no layout
		pane = getContentPane();
	    pane.setLayout(null);
	}
	
	/**
	 * Sets the screen that follows after the current one
	 * @param nextFrame screen that follows after the current one
	 */
	public void setNext(JFrame nextFrame) {
		this.nextFrame = nextFrame;
	}
	

	/**
	 * Sets the screen that was before the current one
	 * @param previousFrame screen that was before the current one
	 */
	public void setPrevious(JFrame previousFrame) {
		this.previousFrame = previousFrame;
	}

	/**
	 * Sets the screen which runs commands
	 * @param runCommand screen that runs commands
	 */
	public void setRun(JFrame runFrame) {
		this.runCommand = runFrame;
	}
	
	/**
	 * Closes all frames (called on finish or cancel)
	 */
	public void close() {
		//if Run Command screen is created
		if(runCommand != null) {
			//stop the run command thread if it is running
			RunCommand runComm = ((RunCommand)runCommand);
			if(runComm.run != null) {
				runComm.run.finished = true;
				synchronized(runComm.run.commands) {	//synchronize based on commands vector
					runComm.run.commands.notify();		//notify the thread
	        	}
			}
			//dispose of the screen
			runCommand.dispose();
			runCommand = null;
		}
		dispose();	//dispose of the current screen
		
		//dispose of the next screen if any
		if(nextFrame != null) {
			JFrameCommon next = ((JFrameCommon)nextFrame);
			next.setPrevious(null);
			next.close();
		}
		
		//dispose of the previous screen if any
		if(previousFrame != null) {
			JFrameCommon prev = ((JFrameCommon)previousFrame);
			prev.setNext(null);
			prev.close();
		}
	}
	
	/**
	 * Function called when cancel button is clicked
	 */
	public void cancel() {
		int option = JOptionPane.showConfirmDialog(null,"Are you sure you want to cancel?","Cancel Operation",JOptionPane.YES_NO_OPTION);
		if(option == JOptionPane.YES_OPTION) {
			close();
		}
	}
	
	/**
	 * Asks the user for a confirmation message
	 * @param message confirmation message displayed
	 * @param title title of the confirmation message
	 * @return true if the YES option was selected
	 */
	public boolean confirm(String message, String title) {
		int option = JOptionPane.showConfirmDialog(null,message,title, JOptionPane.YES_NO_OPTION);
		if(option == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}
}
