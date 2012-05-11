/**
 * This class provides a wrapper for ssh protocol and supports shell command lines
 */
package utils;

import java.io.IOException;
import java.io.InputStream;
import com.jcraft.jsch.*;

/**
 * @author Catalin
 *
 */

public class SSHCmdHelper
{
	JSch jsch = null;
	Session session = null;
	String lastErrorMessage = null;
	int lastExitStatus = 0;
	
	public SSHCmdHelper()
	{
		jsch = new JSch();
		lastErrorMessage = "";
	}
	
	/**
	* Start the SSH connection to the remote host
	* @param username			The username to use when log in
	* @param host				The name of the remote host we want to connect at
	* @param privateKeyFileName	The path to the private key file to be used when connecting to the remote host(pem) 
	*/
	public boolean connect(String username, String host, String privateKeyFileName)
	{
		lastErrorMessage = "";
		try
		{
			session = jsch.getSession(username, host);
			jsch.addIdentity(privateKeyFileName);
			UserInfo ui=new XEUserInfo();
		    session.setUserInfo(ui);
			session.connect();
			
		}
		catch(JSchException jsche)
		{
			lastErrorMessage = "Caught Exception: " + jsche.getMessage();
			System.err.println(lastErrorMessage);
			jsche.printStackTrace(System.err);
			return false;
		}
		
		return true;
	}
	
	/**
	* Checks we are connected to the remote host
	*/
	public boolean isConnected()
	{
		if (session == null || !session.isConnected())
			return false;
		
		return true;
	}
	
	/**
	* Disconnect from the remote host 
	*/
	public void disconnect()
	{
		//if we are not connected just return
		if (!isConnected())
			return;
		
		//we are connected so disconnect
		session.disconnect();
	}
	
	/**
	* Executes a shell command on the remote host
	* @param sshCmmd	The command line to be executed remotely
	* @param outputText	The variable that will be filled with the response of the command line execution  
	*/
	public boolean executeCmd(String sshCmd, StringBuffer outputText)
	{
		lastErrorMessage = "";
		outputText.delete(0, outputText.length());
		if (!isConnected())
		{
			lastErrorMessage = "You are trying to execute a command while you are not connected";
			return false;
		}
		
		try
		{
			Channel channel = session.openChannel("exec");
			((ChannelExec)channel).setCommand(sshCmd);
			((ChannelExec)channel).setPty(true);
			
			channel.setInputStream(null);
			((ChannelExec)channel).setErrStream(System.err);
			
			InputStream in = channel.getInputStream();
			
			channel.connect();
			
			byte[] buf=new byte[1024];
		    while(true)
		    {
		    	while(in.available()>0)
		        {
		    		int i = in.read(buf, 0, 1024);
		    		if(i < 0)
		    			break;
		    		outputText.append(new String(buf, 0, i));
		        }
		    	
		        if(channel.isClosed())
		        {
		        	lastExitStatus = channel.getExitStatus();
		        	outputText.append("exit-status: "+channel.getExitStatus());
		        	outputText.append("\r\n");
		        	break;
		        }
		        
		        try
		        {
		        	Thread.sleep(1000);
		        }
		        catch(InterruptedException ie)
		        {
		        	lastErrorMessage = "Caught Exception: " + ie.getMessage();
					System.err.println(lastErrorMessage);
					ie.printStackTrace(System.err);
					return false;
		        }
		    }
		    
		    channel.disconnect();
			
		}
		catch(JSchException jsche)
		{
			lastErrorMessage = "Caught Exception: " + jsche.getMessage();
			System.err.println(lastErrorMessage);
			jsche.printStackTrace(System.err);
			return false;
		}
		catch(IOException ioe)
		{
			lastErrorMessage = "Caught Exception: " + ioe.getMessage();
			System.err.println(lastErrorMessage);
			ioe.printStackTrace(System.err);
			return false;
		}
		
		return true;
	}
	
	/**
	* Retrieve the last error message
	*/
	public String getLastErrorMessage()
	{
		return lastErrorMessage;
	}
	
	/**
	* Retrieve the last exit status
	*/
	public int getLastExitStatus()
	{
		return lastExitStatus;
	}
	
	/**
	 * Main entry point in the test application for this class (to be deleted in the release version)
	 * @param args
	 */
	public static void main(String[] args) 
	{
		SSHCmdHelper ssh = new SSHCmdHelper();
		ssh.connect("ec2-user","ec2-50-112-76-235.us-west-2.compute.amazonaws.com","C:\\Documents and Settings\\user\\Desktop\\xpressengine.org.pem");
		StringBuffer output = new StringBuffer();
		boolean result = ssh.executeCmd("wget", output);
		if (result)
			System.out.println("OK");
		result = ssh.executeCmd("pwd", output);
		if (result)
			System.out.println("OK");
		ssh.disconnect();
	}
	
	
	public static class XEUserInfo implements UserInfo, UIKeyboardInteractive
	{
	    public String getPassword(){ return null; }
	    public boolean promptYesNo(String str){return true;}
	    
	    public String getPassphrase(){ return ""; }
	    public boolean promptPassphrase(String message){return false;}
	    public boolean promptPassword(String message){ return true; }
	    public void showMessage(String message){System.out.println(message);}
	    public String[] promptKeyboardInteractive(String destination, String name, String instruction,String[] prompt,boolean[] echo){return null;  /*cancel*/}
	}
}
