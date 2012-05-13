/**
 * This class helps deploying the latest version of XE into a remote host via SSH
 */
package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import utils.CommandOptionComparator;

/**
 * @author Catalin
 *
 */
public class DeployXEApp
{
	HashMap<String, List<String>> options = new HashMap<String, List<String>>();
	HashMap<String, String> parameters = new HashMap<String, String>();
	
	private static final String DEFAULT_USERNAME = "ec2-user";
	private static final String WWW_FOLDER = "/var/www/html/";
	private static final String ZIP_ARCHIVE_NAME = "xecore.zip";
	private static final String DOWNLOAD_LOG_FILE_NAME = "download.out";
	private static final String DOWNLOAD_CONTENT_TYPE = "application/octet-stream";
	private static final String DOTS = "................";
	private static final String OK = "OK";
	private static final String NG = "Failed";
	
	public DeployXEApp()
	{
		List<String> list = null;// will contain the description of the option and a boolean string if mandatory
		// general options
		list = new ArrayList<String>();list.add("The username to use when connecting to the remote host");list.add("false");
		options.put("username",					list);
		list = new ArrayList<String>();list.add("The remote hostname to connect to(where the deploy is made)");list.add("true");
		options.put("hostname",					list);
		list = new ArrayList<String>();list.add("The private key file path");list.add("true");
		options.put("private-key-file",			list);
		list = new ArrayList<String>();list.add("An alternative URL where to download XE-Core from");list.add("false");
		options.put("xe-core-download-url",		list);
		list = new ArrayList<String>();list.add("Setup an Apache virtual host for the provided domain");list.add("false");
		options.put("domain-name",		list);
	}
	
	/**
	* This method is responsible for setting the default values of the commands options 
	*/
	void initializeDefaultOptionValues()
	{
		parameters.put("username", DEFAULT_USERNAME);
	}
	
	/**
	* Displays the usage of this command line tool
	*/
	private void displayUsage()
    {
    	System.out.println("\n");
    	System.out.println("Invalid options");
    	System.out.println("Usage: java -jar instant-xe.jar deploy options");
    	
    	System.out.println("List of options:");
    	Object[] optNames = options.keySet().toArray();
    	CommandOptionComparator comparator = new CommandOptionComparator(options);
    	Arrays.sort(optNames, comparator);
    	//Compute the length of the biggest option
    	int maxLen = 0;
    	for (int i = 0;i < optNames.length;i++)
    	{
    		String value = (String)optNames[i];
    		if (value.length() > maxLen)
    			maxLen = value.length();
    	}
    	for (int i = 0;i < optNames.length;i++)
    	{
    		String value = (String)optNames[i];
    		boolean mandatory = options.get(value).get(1).equalsIgnoreCase("true")?true:false;
    		System.out.print(mandatory?' ':'[');
    		System.out.print(value.substring(value.indexOf('.') + 1));
    		System.out.print(mandatory?' ':']');
    		for (int j = value.substring(value.indexOf('.') + 1).length(); j < maxLen; j++)
    			System.out.print(" ");
    		System.out.print("\t");
    		System.out.println(options.get(value).get(0));
    	}
		
		System.out.println("\n");
    }
    
	/**
	* Parses the command line arguments and outputs a hash map with the values for each parameter
	* @param args	Command line arguments passed to this tool
	*/
    private boolean parseArguments(String[] args)
    {	
    	initializeDefaultOptionValues();
    	
    	for (int i=0;i<args.length-1;i+=2)
    		parameters.put(args[i], args[i+1]);
    	
    	Object[] optNames = options.keySet().toArray();
    	for (int i = 0;i < optNames.length;i++)
		{
			String value = (String)optNames[i];
			boolean mandatory = options.get(value).get(1).equalsIgnoreCase("true")?true:false;
			if (mandatory && !parameters.containsKey(value))
			{
				displayUsage();
				return false;
			}
		}
    	
    	return true;
    }
	
	/**
	 * This is the entry point of the application
	 * @param args	The parameters passed to application in the command line
	 */
	public static void main(String[] args) 
	{
		DeployXEApp app = new DeployXEApp();
		
		if (!app.parseArguments(args))
			return;
		
		//now everything is ready to start the actual deploy operation
		
		//let's first take care about the XE-Core download URL
		if (app.parameters.get("xe-core-download-url") == null)//the user did not provided any URL
		{
			String xeCoreURL = LatestXEVersion.getLatestXELatestVersionURL();
			if (xeCoreURL == null)//there was an error trying to obtain the URL from xpressengine.org
			{
				System.out.println("Error trying to obtain the XE-Core download URL:");
				System.out.println(LatestXEVersion.getLastErrorMessage());
				System.out.println("The script will exit now but you can try again but specifying the 'xe-core-download-url' parameter.");
				return;
			}
			app.parameters.put("xe-core-download-url", xeCoreURL);
		}
		//now we have also the XE-Core download URL, let's move on with downloading it on the remote machine
		SSHCmdHelper sshHelper = new SSHCmdHelper();
		System.out.print("Remote host log-in" + DOTS );
		boolean result = sshHelper.connect(app.parameters.get("username"), app.parameters.get("hostname"), app.parameters.get("private-key-file"));
		if (!result)
		{
			System.out.println(NG);
			System.out.println("Error trying to connect to the remote host for deploying XE.");
			System.out.println(sshHelper.getLastErrorMessage());
			System.out.println("The script will exit now!");
			return;
		}
		System.out.println(OK);
		//now we have connected to the remote host, let's download the XE-Core
		StringBuffer output = new StringBuffer();
		System.out.print("Download XE-Core archive" + DOTS);
		sshHelper.executeCmd("wget \"" + app.parameters.get("xe-core-download-url") + "\" -O " + ZIP_ARCHIVE_NAME + " -o " + DOWNLOAD_LOG_FILE_NAME, output);
		if (sshHelper.getLastExitStatus() != 0)
		{
			System.out.println(NG);
			System.out.println("Downloading XE-Core on the remote host failed.");
			sshHelper.disconnect();
			return;
		}
		System.out.println(OK);
		System.out.print("Checking the type of the downloaded file" + DOTS);
		sshHelper.executeCmd("cat " + DOWNLOAD_LOG_FILE_NAME + " | grep \"Length:\"", output);
		if (output.indexOf(DOWNLOAD_CONTENT_TYPE) == -1)//this means something went bad so we need to quit
		{
			System.out.println(NG);
			System.out.println("Downloading XE-Core on the remote host failed.");
			System.out.println("The script will exit now!");
			sshHelper.disconnect();
			return;
		}
		System.out.println(OK);
		System.out.print("Checking the integrity of the downloaded file" + DOTS);
		sshHelper.executeCmd("cat " + DOWNLOAD_LOG_FILE_NAME + " | grep \"100%\"", output);
		if (output.indexOf("100%") == -1)//this means something went bad so we need to quit
		{
			System.out.println(NG);
			System.out.println("Downloading XE-Core on the remote host failed. The download process was not completed.");
			System.out.println("The script will exit now!");
			sshHelper.disconnect();
			return;
		}
		System.out.println(OK);
		//now remove the temporary download log file 
		sshHelper.executeCmd("rm -rf " + DOWNLOAD_LOG_FILE_NAME, output);
		
		//now we have downloaded the XE-Core zip file, let's unzip it
		System.out.print("Unzip the XE-Core archive" + DOTS);
		sshHelper.executeCmd("unzip " + ZIP_ARCHIVE_NAME + " -d " + WWW_FOLDER, output);
		
		if (sshHelper.getLastExitStatus() != 0)
		{
			System.out.println(NG);
			System.out.println("Unziping XE-Core on the remote host failed.");
			System.out.println("The script will exit now!");
			sshHelper.disconnect();
			return;
		}
		System.out.println(OK);
		
		//now delete the XE-Core archive
		sshHelper.executeCmd("rm -rf " + ZIP_ARCHIVE_NAME, output);
		
		//now we have to setup the virtual host for the provided domain name (in case one was provided)
		if (app.parameters.get("domain-name") != null)
		{
			boolean lastSshCmdFailed = false;
			//first we un-comment the virtual host lines
			System.out.print("Setup the virtual host for the " + app.parameters.get("domain-name") + " domain - step 1" + DOTS);
			sshHelper.executeCmd("sed -i 's/#xe//g' /etc/httpd/conf/httpd.conf", output);
			if (sshHelper.getLastExitStatus() != 0)
			{
				System.out.println(NG);
				System.out.println("Setting up the virtual host - step 1 failed.");
				System.out.println("You will have to manually setup the virtual host.");
				lastSshCmdFailed = true;
			}
			else
				System.out.println(OK);
			
			//now we add the domain name
			if (!lastSshCmdFailed)
			{
				System.out.print("Setup the virtual host for the " + app.parameters.get("domain-name") + " domain - step 2" + DOTS);
				sshHelper.executeCmd("sed -i 's/instant-xe.org/" + app.parameters.get("domain-name") + "/g' /etc/httpd/conf/httpd.conf", output);
				
				if (sshHelper.getLastExitStatus() != 0)
				{
					System.out.println(NG);
					System.out.println("Setting up the virtual host - step 2 failed.");
					System.out.println("You will have to manually setup the virtual host.");
					lastSshCmdFailed = true;
				}
				else
					System.out.println(OK);
			}
			//finally restart the apache service in order to reflect the changes
			if (!lastSshCmdFailed)
			{
				System.out.print("Restarting apache" + DOTS);
				sshHelper.executeCmd("sudo /etc/init.d/httpd restart", output);
				if (sshHelper.getLastExitStatus() != 0)
				{
					System.out.println(NG);
					System.out.println("Restarting apache failed.");
					System.out.println("You will have to manually restart the apache service.");
					lastSshCmdFailed = true;
				}
				else
					System.out.println(OK);
			}
		}
		
		System.out.println("XE has successfully been deployed to the remote machine. You can now proceed with the installation phase.");
		//we have finished our job on the remote host so we disconnect now
		sshHelper.disconnect();
	}

}
