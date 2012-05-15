/**
 * This is a dispatcher class that will be the main entry point in the jar manifest
 */
package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import utils.DeployXEApp;
import aws.AwsConsoleApp;

/**
 * @author Catalin
 *
 */
public class Dispatcher
{

    private static final Map<String, Class<?>> ENTRY_POINTS = new HashMap<String, Class<?>>();
    public static String VERSION = null;
    
    static
    {
        ENTRY_POINTS.put("setup-aws", AwsConsoleApp.class);
        ENTRY_POINTS.put("deploy-xe", DeployXEApp.class);
        VERSION = Dispatcher.class.getPackage().getImplementationVersion();
        
        if (VERSION == null)//if we are not in the builded jar 
        {
        	try
        	{
        		FileInputStream fstream = new FileInputStream("build.properties");
        		DataInputStream in = new DataInputStream(fstream);
        		BufferedReader br = new BufferedReader(new InputStreamReader(in));
        		String strLine;
        		while ((strLine = br.readLine()) != null)
        		{
        			  if (strLine.startsWith("build.version"))
        			  {
        				  int pos = strLine.indexOf("=");
        				  VERSION = strLine.substring(pos+1);
        				  break;
        			  }
        		}
        		br.close();
        		in.close();
        		fstream.close();
        	}
        	catch(IOException ioe)
        	{
        		System.err.println("Caught Exception: " + ioe.getMessage());
    			ioe.printStackTrace(System.err);
        	}
        }
    }
    
    /**
	 * This method displays the usage of this application
	 * @param entryPoint	The parameters passed to application in the command line
	 */
    public static void displayUsage(String entryPoint)
    {
    	System.out.println("\n");
    	if (entryPoint != null)
    		System.out.println(entryPoint + " is not a valid COMMAND");
    	else
    		System.out.println("You must specify a COMMAND");
    	
    	System.out.println("Usage: java - jar instant-xe.jar COMMAND options");
    	System.out.println("List of available COMMAND(s):");
    	
    	for (int i=0;i < ENTRY_POINTS.size();i++)
    	{
    		System.out.println(ENTRY_POINTS.keySet().toArray()[i]);
    	}
    	
    	System.out.println("\n");
    }

    /**
	 * This is the entry point of the application
	 * @param args	The parameters passed to application in the command line
	 */
    public static void main(final String[] args) throws Exception
    {
    	System.out.println("\r\nInstant-XE version " + VERSION);
        if(args.length < 1)
        {
            displayUsage(null);
            return;
        }
        final Class<?> entryPoint = ENTRY_POINTS.get(args[0]);
        
        if(entryPoint==null)
        {
            displayUsage(args[0]);
            return;
        }
        final String[] argsCopy = (args.length > 1)?Arrays.copyOfRange(args, 1, args.length):new String[0];
        entryPoint.getMethod("main", String[].class).invoke(null,(Object) argsCopy);

    }
}
