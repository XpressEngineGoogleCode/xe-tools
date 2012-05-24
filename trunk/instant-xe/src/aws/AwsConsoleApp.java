/**
 * This tool helps XpressEngine users to easily deploy an XE in Amazon Cloud
 */
package aws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import main.Dispatcher;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeTagsRequest;
import com.amazonaws.services.ec2.model.DescribeTagsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.ReleaseAddressRequest;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53Client;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeAction;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.CreateHostedZoneRequest;
import com.amazonaws.services.route53.model.DeleteHostedZoneRequest;
import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.HostedZoneConfig;
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.RRType;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import utils.CommandOptionComparator;

/**
 * @author Catalin
 *
 */

public class AwsConsoleApp 
{

	AmazonEC2 ec2;
	HashMap<String, String> commands = new HashMap<String, String>();
	HashMap<String, List<String>> options = new HashMap<String, List<String>>();
	HashMap<String, String> parameters = new HashMap<String, String>();
	
	public static final String KEY_PAIR_NAME = "instant-xe-key-pair";
	public static final String SECURITY_GROUP_NAME = "instant-xe-security-group";
	public static final String OUTPUT_KEY_PAIR_FILE_NAME = "instant-xe.pem";
	public static final String SECURITY_GROUP_DESCRIPTION = "Security group for the XpressEngine installation";
	public static final String XE_DEFAULT_INSTANT_AMI_NAME = "ami-8c5fd3bc";
	public static final String XE_AWS_AMI_URL = "https://xe-tools.googlecode.com/svn/trunk/instant-xe/aws-ami.in";
	public static final String XE_INSTANCE_TAG_NAME = "xe-instant";
	public static final String XE_AWS_ROUTE53_HOSTED_ZONE_COMMENT = "Made by xe-instant";
	public static final Long	XE_AWS_ROUTE53_DEFAULT_TTL = new Long(900);
	public static final String XE_AWS_ROUTE53_CALLER_REFERENCE = "xe-instant-dns";
	public static final String XE_AWS_ROUTE53_REMOVE_RECORDS_BATCH_COMMENT = "Remove instant XE DNS record entries";
	public static final long	XE_WAIT_TERMINATE_INSTANCE_TIMEOUT = 120;//this value is expressed in seconds
	public static final long	XE_WAIT_STARTING_INSTANCE_TIMEOUT = 60;//this value is expressed in seconds
	
	public AwsConsoleApp()
	{
		commands.put("create_instance",	"Helps you create a new XE instance in Amazon Cloud");
		commands.put("pause_instance",	"Helps you pause an XE instance");
		commands.put("resume_instance",	"Helps you to resume an XE instance"); 
		commands.put("delete_instance",	"Helps you remove an XE instance from Amazon Cloud");
		
		commands.put("assign_address", 	"Helps you assign a public IP address to your XE instance");
		commands.put("remove_address", 	"Helps you remove the public IP address associated with your XE instance");
		
		commands.put("associate_domain","Helps you associate a domain name to your XE instance");
		commands.put("remove_domain", 	"Helps you remove the domaine name associated with your XE instance");
		
		
		List<String> list = null;// will contain the description of the option and a boolean string if mandatory
		// general options
		list = new ArrayList<String>();list.add("AWS access key ID");list.add("true");
		options.put("access-key",	list);
		list = new ArrayList<String>();list.add("AWS secret access key");list.add("true");
		options.put("secret-key",	list);
		list = new ArrayList<String>();list.add("AWS endpoint");list.add("false");
		options.put("endpoint",		list);
		list = new ArrayList<String>();list.add("The name of the new instance");list.add("false");
		options.put("tag-name",		list);
		
		// particular (to each command) options
		list = new ArrayList<String>();list.add("The type of Amazon EC2 instance to be created(default: t1.micro)");list.add("false");
		options.put("create_instance.instance-type",					list);
		list = new ArrayList<String>();list.add("Name of the AWS keypair to use (by default it creates a new one)");list.add("false");
		options.put("create_instance.key-pair",							list);
		list = new ArrayList<String>();list.add("Name of the AWS security group to use(by default it creates a new one)");list.add("false");
		options.put("create_instance.security-group",					list);
		list = new ArrayList<String>();list.add("Filename where the returned key-pair is written");list.add("false");
		options.put("create_instance.output-key-pair-file",				list);
		list = new ArrayList<String>();list.add("Delete the default key-pair (yes/no) (default: yes)");list.add("false");
		options.put("delete_instance.delete-key-pair",					list);
		list = new ArrayList<String>();list.add("Delete the default security group (yes/no) (default: yes)");list.add("false");
		options.put("delete_instance.delete-security-group",			list);
		list = new ArrayList<String>();list.add("The name of the domain you want to associate to your XE instance");list.add("true");
		options.put("associate_domain.domain-name",						list);
		list = new ArrayList<String>();list.add("Skip elastic IP association (yes/no) (default: no)");list.add("false");
		options.put("resume_instance.skip-elastic-ip-association",		list);
	}
	
	/**
	* This method is responsible for setting the default values of the commands options 
	*/
	void initializeDefaultOptionValues()
	{
		parameters.put("instance-type", "t1.micro");
		parameters.put("output-key-pair-file", OUTPUT_KEY_PAIR_FILE_NAME);
		parameters.put("tag-name", XE_INSTANCE_TAG_NAME);
		parameters.put("delete-key-pair", "yes");
		parameters.put("delete-security-group", "yes");
		parameters.put("skip-elastic-ip-association", "no");
	}
	
	/**
	* Displays the usage of this tool
	* @param command	Command for which to display usage (can be omitted) 
	*/
	private void displayUsage(String command)
    {
    	System.out.println("\n");
    	System.out.println((command == null)?"Invalid command":"Invalid options");
    	System.out.println("Usage: java -jar instant-xe.jar setup-aws COMMAND options");
    	if (command == null)
    	{
    		System.out.println("List of commands:");
    		Object[] cmdNames = commands.keySet().toArray();
    		Arrays.sort(cmdNames);
    		//Compute the length of the biggest command
    		int maxLen = 0;
    		for (int i = 0;i < cmdNames.length;i++)
    			if (((String)cmdNames[i]).length() > maxLen)
    				maxLen = ((String)cmdNames[i]).length();
    		for (int i = 0;i < cmdNames.length;i++)
    		{
    			System.out.print(cmdNames[i].toString());
    			for (int j = cmdNames[i].toString().length(); j < maxLen; j++)
    				System.out.print(" ");
    			System.out.print("\t");
    			System.out.println(commands.get(cmdNames[i].toString()));
    		}
    	}
    	else
    	{
    		System.out.println("List of options:");
    		Object[] optNames = options.keySet().toArray();
    		CommandOptionComparator comparator = new CommandOptionComparator(options);
    		Arrays.sort(optNames, comparator);
    		//Compute the length of the biggest option
    		int maxLen = 0;
    		for (int i = 0;i < optNames.length;i++)
    		{
    			String value = (String)optNames[i];
    			if ( (value.indexOf('.') == -1 || value.startsWith(command+".")) && value.substring(value.indexOf('.') + 1).length() > maxLen)
    				maxLen = value.substring(value.indexOf('.') + 1).length();
    		}
    		for (int i = 0;i < optNames.length;i++)
    		{
    			String value = (String)optNames[i];
    			if (value.indexOf('.') != -1 && !value.startsWith(command+"."))
    				continue;
    			boolean mandatory = options.get(value).get(1).equalsIgnoreCase("true")?true:false;
    			System.out.print(mandatory?' ':'[');
    			System.out.print(value.substring(value.indexOf('.') + 1));
    			System.out.print(mandatory?' ':']');
    			for (int j = value.substring(value.indexOf('.') + 1).length(); j < maxLen; j++)
    				System.out.print(" ");
    			System.out.print("\t");
    			System.out.println(options.get(value).get(0));
    		}
    	}
		
		System.out.println("\n");
    }
    
	/**
	* Parses the command line arguments and outputs a hash map with the values for each parameter
	* @param args	Command line arguments passed to this tool
	*/
    private boolean parseArguments(String[] args)
    {
    	if (args.length < 1 || !commands.containsKey(args[0]))
    	{
    		displayUsage(null);
    		return false;
    	}
    	
    	initializeDefaultOptionValues();
    	
    	for (int i=1;i<args.length-1;i+=2)
    		parameters.put(args[i], args[i+1]);
    	
    	Object[] optNames = options.keySet().toArray();
    	for (int i = 0;i < optNames.length;i++)
		{
			String value = (String)optNames[i];
			if (value.indexOf('.') != -1 && !value.startsWith(args[0]+"."))
				continue;
			boolean mandatory = options.get(value).get(1).equalsIgnoreCase("true")?true:false;
			if (mandatory && !parameters.containsKey(value.substring(value.indexOf('.') + 1)))
			{
				displayUsage(args[0]);
				return false;
			}
		}
    	
    	return true;
    }
	
	/**
	* Initializes the Amazon Client
	* @param accessKey	AWS access key 
	* @param secretKey	AWS secret key
	*/
    private void init(String accessKey, String secretKey) 
    {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
        ec2 = new AmazonEC2Client(credentials);
        
        if (parameters.containsKey("endpoint"))
        	ec2.setEndpoint(parameters.get("endpoint"));
        
    }
    
    /**
	* Connects to the SVN and obtain the latest AWS AMI for the current version
	*/
    private String getLatestAMI()
    {
    	String latestAMI = XE_DEFAULT_INSTANT_AMI_NAME;
    	
    	try
    	{
    		StringBuffer sb = new StringBuffer();
    		String toolVersion = Dispatcher.VERSION;
    		toolVersion = toolVersion.split("-")[0];//for the cases like '1.0.0-b3' we need only version (not the build no)
    		URL url = new URL(XE_AWS_AMI_URL);
    		InputStream is = url.openStream();
    		int c = is.read();
    		while (c != -1)
    		{
    			sb.append((char)c);
    			c = is.read();
    		}
    		
    		is.close();
    		
    		String[] tokens = new String(sb).split("[ \\t\\n\\x0B\\f\\r]+");
    		for (int i=0;i<tokens.length;i+=2)
    			if (tokens[i+1].compareToIgnoreCase(toolVersion) == 0)
    			{
    				latestAMI = tokens[i];
    				break;
    			}
    	}
    	catch(MalformedURLException mfue)
		{
    		System.err.println("Caught Exception: " + mfue.getMessage());
			mfue.printStackTrace(System.err);
		}
    	catch(IOException ioe)
		{
			System.err.println("Caught Exception: " + ioe.getMessage());
			ioe.printStackTrace(System.err);
		}
    	
    	return latestAMI;
    }
    
    /**
     * Will take a URL such as http://www.stackoverflow.com and return www.stackoverflow.com
     * @param url	The URL to parse
     */
    private static String getHost(String url){
        if(url == null || url.length() == 0)
            return "";

        int doubleSlash = url.indexOf("//");
        if(doubleSlash == -1)
            doubleSlash = 0;
        else
            doubleSlash += 2;

        int end = url.indexOf('/', doubleSlash);
        end = end >= 0 ? end : url.length();

        return url.substring(doubleSlash, end);
    }
    
    /**  
     * Get the base domain for a given host or URL. E.g. mail.google.com will return google.com
     * @param url	The URL to parse 
     */
    private static String getBaseDomain(String url) {
        String host = getHost(url);

        int startIndex = 0;
        int nextIndex = host.indexOf('.');
        int lastIndex = host.lastIndexOf('.');
        while (nextIndex < lastIndex) {
            startIndex = nextIndex + 1;
            nextIndex = host.indexOf('.', startIndex);
        }
        if (startIndex > 0) {
            return host.substring(startIndex);
        } else {
            return host;
        }
    }
    
    /**
	 * The method responsible for processing 'create_instance' commands
	 */
    public void process_create_instance_command()
    {
    	System.out.println("Executing create instance command...");
    	
    	String keyPair = parameters.get("key-pair");
    	if (keyPair == null)// this means we should create a new one
    	{
    		keyPair = KEY_PAIR_NAME;
    		CreateKeyPairResult ckpr = ec2.createKeyPair(new CreateKeyPairRequest().withKeyName(keyPair));
    		
    		File file = new File(parameters.get("output-key-pair-file"));
    		try
    		{
    			if (!file.createNewFile())// the desired file already exits so we will create a temporary one
    			{
    				System.out.println("WARNING: The output file for key pair already exists. We will create a new temporary one:");
    				file = File.createTempFile("instant-xe-key-pair", ".pem");
    				System.out.println(file.getPath());
    			}
    			// now open an output stream to write the new created key-pair
    			FileOutputStream fOut = new FileOutputStream(file.getPath());
    			PrintWriter pOut = new PrintWriter(fOut);
    			pOut.print(ckpr.getKeyPair().getKeyMaterial());
    			System.out.println("The new key-pair created:\n" + ckpr.getKeyPair().toString());
    			pOut.flush();
    			pOut.close();
    			fOut.close();
    		}
    		catch(IOException ioe)
    		{
    			System.err.println("Caught Exception: " + ioe.getMessage());
				ioe.printStackTrace(System.err);
    		}
    		
    	}
    	
    	// so far we concentrated on the key-pair. Next we will concentrate on security-group
    	String securityGroup = parameters.get("security-group");
    	
    	if (securityGroup == null)// this means we should create a new one
    	{
    		securityGroup = SECURITY_GROUP_NAME;
    		ec2.createSecurityGroup(new CreateSecurityGroupRequest()
    										.withGroupName(securityGroup)
    										.withDescription(SECURITY_GROUP_DESCRIPTION));
    		
    		// set permissions for the new created security group  
        	ArrayList<IpPermission> ruleList = new ArrayList<IpPermission>();
        	ruleList.add(new IpPermission()
        						.withIpProtocol("icmp")
        						.withFromPort(-1)
        						.withIpRanges("0.0.0.0/0"));
        	ruleList.add(new IpPermission()
								.withIpProtocol("tcp")
								.withFromPort(22)
								.withToPort(22)
								.withIpRanges("0.0.0.0/0"));
        	ruleList.add(new IpPermission()
								.withIpProtocol("tcp")
								.withFromPort(80)
								.withToPort(80)
								.withIpRanges("0.0.0.0/0"));
        	ec2.authorizeSecurityGroupIngress(new AuthorizeSecurityGroupIngressRequest(securityGroup, ruleList));
        	
    	}
    	
    	//now create the actual ec2 instance
    	RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
												        .withInstanceType(parameters.get("instance-type"))
												        .withImageId(getLatestAMI())
												        .withMinCount(1)
												        .withMaxCount(1)
												        .withSecurityGroups(securityGroup)
												        .withKeyName(keyPair);

    	RunInstancesResult runInstances = ec2.runInstances(runInstancesRequest);
    	
    	//tag the new created instance in order to be able to refer it later
    	System.out.println("The Name of the new instance is: " + parameters.get("tag-name"));
    	List<Instance> instances = runInstances.getReservation().getInstances();
    	CreateTagsRequest createTagsRequest = new CreateTagsRequest()
    	  											.withResources(instances.get(0).getInstanceId())
    	  											.withTags(new Tag("Name", parameters.get("tag-name")));
    	ec2.createTags(createTagsRequest);
    	
    	//now let's wait until the status is changed from pending to running
    	System.out.println("Waiting for the new machine to boot up (entering running state)");
    	System.out.println("(this may take up to 1 minute)");
    	long lInitialTimestamp = System.currentTimeMillis();
    	do
    	{
    		if ( ((System.currentTimeMillis() - lInitialTimestamp)/1000) > XE_WAIT_STARTING_INSTANCE_TIMEOUT )
    			break;
    		Filter filter = new Filter()    								
    								.withName("instance-state-name")
    								.withValues(InstanceStateName.Running.toString());
    		DescribeInstancesResult dir = ec2.describeInstances(new DescribeInstancesRequest()
    																	.withFilters(filter)
    																	.withInstanceIds(instances.get(0).getInstanceId()));
    		
    		if (dir.getReservations().size() > 0)
    		{
    			System.out.println("The new machine is in the running state");
    			System.out.println("The public DNS of the new created instance is: " + dir.getReservations().get(0).getInstances().get(0).getPublicDnsName());
    	    	System.out.println("The public IP of the new created instance is: " + dir.getReservations().get(0).getInstances().get(0).getPublicIpAddress());
    			break;//the instance is in the running state
    		}
    		else
    		{
    			try
    			{
    				Thread.sleep(500);
    			}
    			catch(InterruptedException ie)
    			{
    				System.err.println("Caught Exception: " + ie.getMessage());
    				ie.printStackTrace(System.err);
    			}
    		}
    	}
    	while(true);
    	
    	System.out.println("Executing create instance command...ended successfully!");
    }
    
    /**
	 * The method responsible for processing 'pause_instance' commands
	 */
    public void process_pause_instance_command()
    {
    	System.out.println("Executing pause instance command...");
    	
    	ArrayList<Filter> filterList = new ArrayList<Filter>();
    	filterList.add(new Filter()
    						.withName("resource-type")
    						.withValues("instance"));
    	
    	filterList.add(new Filter()
							.withName("key")
							.withValues("Name"));
    	
    	filterList.add(new Filter()
							.withName("value")
							.withValues(parameters.get("tag-name")));
    	
    	DescribeTagsResult dtr =  ec2.describeTags(new DescribeTagsRequest()
    													.withFilters(filterList));
    	ec2.stopInstances(new StopInstancesRequest().withInstanceIds(dtr.getTags().get(0).getResourceId()));
    	System.out.println("Executing pause instance command...ended successfully!");
    }
    
    /**
	 * The method responsible for processing 'resume_instance' commands
	 */
    public void process_resume_instance_command()
    {
    	System.out.println("Executing resume instance command...");
    	
    	ArrayList<Filter> filterList = new ArrayList<Filter>();
    	filterList.add(new Filter()
    						.withName("resource-type")
    						.withValues("instance"));
    	
    	filterList.add(new Filter()
							.withName("key")
							.withValues("Name"));

    	filterList.add(new Filter()
							.withName("value")
							.withValues(parameters.get("tag-name")));
    	
    	DescribeTagsResult dtr =  ec2.describeTags(new DescribeTagsRequest()
    													.withFilters(filterList));
    	ec2.startInstances(new StartInstancesRequest().withInstanceIds(dtr.getTags().get(0).getResourceId()));
    	
    	//now let's wait until the status is changed from pending to running
    	System.out.println("Waiting for the resumed machine to boot up (entering running state)");
    	System.out.println("(this may take up to 1 minute)");
    	long lInitialTimestamp = System.currentTimeMillis();
    	do
    	{
    		if ( ((System.currentTimeMillis() - lInitialTimestamp)/1000) > XE_WAIT_STARTING_INSTANCE_TIMEOUT )
    			break;
    		Filter filter = new Filter()    								
    								.withName("instance-state-name")
    								.withValues(InstanceStateName.Running.toString());
    		DescribeInstancesResult dir = ec2.describeInstances(new DescribeInstancesRequest()
    																	.withFilters(filter)
    																	.withInstanceIds(dtr.getTags().get(0).getResourceId()));
    		
    		if (dir.getReservations().size() > 0)
    		{
    			System.out.println("The resumed machine is in the running state");
    			System.out.println("The new public DNS of the resumed instance is: " + dir.getReservations().get(0).getInstances().get(0).getPublicDnsName());
    	    	System.out.println("The new public IP of the resumed instance is: " + dir.getReservations().get(0).getInstances().get(0).getPublicIpAddress());
    			break;//the instance is in the running state
    		}
    		else
    		{
    			try
    			{
    				Thread.sleep(500);
    			}
    			catch(InterruptedException ie)
    			{
    				System.err.println("Caught Exception: " + ie.getMessage());
    				ie.printStackTrace(System.err);
    			}
    		}
    	}
    	while(true);
    	
    	//now let's try to re-associate the elastic IP to the instance
    	List<Address> addressList = ec2.describeAddresses().getAddresses();
    	//obtain the allocated public IP address associated with our XE instance
    	String publicIp = null;
    	for (int i=0;i<addressList.size();i++)
    		if (addressList.get(i).getInstanceId().length() == 0)
    		{
    			publicIp = addressList.get(i).getPublicIp();
    			break;
    		}
    	if (publicIp != null && parameters.get("skip-elastic-ip-association") != null && parameters.get("skip-elastic-ip-association").compareToIgnoreCase("no") == 0)
    	{
    		//associate the elastic IP address to the XE instance
        	ec2.associateAddress(new AssociateAddressRequest()
        							.withInstanceId(dtr.getTags().get(0).getResourceId())
        							.withPublicIp(publicIp));
        	System.out.println(publicIp + " elastic IP was associated to the resumed instance");
    	}
    	System.out.println("Executing resume instance command...ended successfully!");
    }
    
    /**
	 * The method responsible for processing 'delete_instance' commands
	 */
    public void process_delete_instance_command()
    {
    	System.out.println("Executing delete instance command...");
    	
    	ArrayList<Filter> filterList = new ArrayList<Filter>();
    	filterList.add(new Filter()
    						.withName("resource-type")
    						.withValues("instance"));
    	
    	filterList.add(new Filter()
							.withName("key")
							.withValues("Name"));

    	filterList.add(new Filter()
							.withName("value")
							.withValues(parameters.get("tag-name")));
    	
    	DescribeTagsResult dtr =  ec2.describeTags(new DescribeTagsRequest()
    													.withFilters(filterList));
    	
    	//if there is no XE instance we should stop this command processing
    	if (dtr.getTags().size() == 0)
    		return;
    	
    	//terminate the instance (delete it more precisely)
    	ec2.terminateInstances(new TerminateInstancesRequest().withInstanceIds(dtr.getTags().get(0).getResourceId()));
    	
    	System.out.println("Waiting for the machine to shutdown (entering terminated state)");
    	System.out.println("(this may take up to 1 minute)");
    	//now let's wait until the status is changed from runnning to terminated (so the terminating process is complete)
    	long lInitialTimestamp = System.currentTimeMillis();
    	do
    	{
    		if ( ((System.currentTimeMillis() - lInitialTimestamp)/1000) > XE_WAIT_TERMINATE_INSTANCE_TIMEOUT )
    			break;
    		Filter filter = new Filter()    								
    								.withName("instance-state-name")
    								.withValues(InstanceStateName.Terminated.toString());
    		DescribeInstancesResult dir = ec2.describeInstances(new DescribeInstancesRequest()
    																	.withFilters(filter)
    																	.withInstanceIds(dtr.getTags().get(0).getResourceId()));
    		
    		if (dir.getReservations().size() > 0)
    			break;//the instance was terminated
    		else
    		{
    			try
    			{
    				Thread.sleep(500);
    			}
    			catch(InterruptedException ie)
    			{
    				System.err.println("Caught Exception: " + ie.getMessage());
    				ie.printStackTrace(System.err);
    			}
    		}
    	}
    	while(true);
    	
    	//delete the security group (if case)
    	String securityGroup = SECURITY_GROUP_NAME;
    	if (parameters.get("delete-security-group") != null && parameters.get("delete-security-group").equalsIgnoreCase("yes"))
    		ec2.deleteSecurityGroup(new DeleteSecurityGroupRequest(securityGroup));
    	
    	//delete the key-pair (if case)
    	String keyPair = KEY_PAIR_NAME;
    	if (parameters.get("delete-key-pair") != null && parameters.get("delete-key-pair").equalsIgnoreCase("yes"))   	
    		ec2.deleteKeyPair(new DeleteKeyPairRequest(keyPair));
    	System.out.println("Executing delete instance command...ended successfully!");
    }
    
    /**
	 * The method responsible for processing 'assign_address' commands
	 */
    public void process_assign_address_command()
    {
    	System.out.println("Executing assign address command...");
    	
    	ArrayList<Filter> filterList = new ArrayList<Filter>();
    	filterList.add(new Filter()
    						.withName("resource-type")
    						.withValues("instance"));
    	
    	filterList.add(new Filter()
							.withName("key")
							.withValues("Name"));

    	filterList.add(new Filter()
							.withName("value")
							.withValues(parameters.get("tag-name")));
    	
    	DescribeTagsResult dtr =  ec2.describeTags(new DescribeTagsRequest()
    													.withFilters(filterList));

    	//allocate a new public IP address to be used for the account
    	String publicIp = ec2.allocateAddress().getPublicIp();
    	
    	//output to the console
    	
    	System.out.println("The new allocated IP address is: " + publicIp);
    	
    	//associate the new allocated IP address to the XE instance
    	ec2.associateAddress(new AssociateAddressRequest()
    							.withInstanceId(dtr.getTags().get(0).getResourceId())
    							.withPublicIp(publicIp));
    	System.out.println("Executing assign address command...ended successfully!");
    }
    
    /**
	 * The method responsible for processing 'remove_address' commands
	 */
    public void process_remove_address_command()
    {
    	System.out.println("Executing remove address command...");
    	
    	ArrayList<Filter> filterList = new ArrayList<Filter>();
    	filterList.add(new Filter()
    						.withName("resource-type")
    						.withValues("instance"));
    	
    	filterList.add(new Filter()
							.withName("key")
							.withValues("Name"));

    	filterList.add(new Filter()
							.withName("value")
							.withValues(parameters.get("tag-name")));
    	
    	DescribeTagsResult dtr =  ec2.describeTags(new DescribeTagsRequest()
    													.withFilters(filterList));

    	List<Address> addressList = ec2.describeAddresses().getAddresses();
    	//obtain the allocated public IP address associated with our XE instance
    	String publicIp = null;
    	for (int i=0;i<addressList.size();i++)
    		if (addressList.get(i).getInstanceId().compareToIgnoreCase( dtr.getTags().get(0).getResourceId()) == 0)
    		{
    			publicIp = addressList.get(i).getPublicIp();
    			break;
    		}
    	if (publicIp == null)
    		return;//the requested command should stop here because there is no associated IP address with our XE instance
    	
    	//now we have obtained the public IP address - let's remove it
    	ec2.releaseAddress(new ReleaseAddressRequest(publicIp));
    	System.out.println("Executing remove address command...ended successfully!");
    	
    }
    
    /**
	 * The method responsible for processing 'associate_domain' commands
	 */
    public void process_associate_domain_command()
    {
    	System.out.println("Executing associate domain command...");
    	
    	ArrayList<Filter> filterList = new ArrayList<Filter>();
    	filterList.add(new Filter()
    						.withName("resource-type")
    						.withValues("instance"));
    	
    	filterList.add(new Filter()
							.withName("key")
							.withValues("Name"));

    	filterList.add(new Filter()
							.withName("value")
							.withValues(parameters.get("tag-name")));
    	
    	DescribeTagsResult dtr =  ec2.describeTags(new DescribeTagsRequest()
    													.withFilters(filterList));
    	
    	List<Address> addressList = ec2.describeAddresses().getAddresses();
    	//obtain the allocated public IP address associated with our XE instance
    	String publicIp = null;
    	for (int i=0;i<addressList.size();i++)
    		if (addressList.get(i).getInstanceId().compareToIgnoreCase(dtr.getTags().get(0).getResourceId()) == 0)
    		{
    			publicIp = addressList.get(i).getPublicIp();
    			break;
    		}
    	if (publicIp == null)
    		return;//the requested command should stop here because there is no associated IP address with our XE instance
    	
    	//now let's instantiate Route 53 client
    	AmazonRoute53 route53 = new AmazonRoute53Client(new BasicAWSCredentials(parameters.get("access-key"), parameters.get("secret-key")));
    	
    	String baseDomain = getBaseDomain(parameters.get("domain-name"));
    	String hostedZoneId = null;
    	//first let's check if there is an already created zone for this domain
    	List<HostedZone> hostedZoneList = route53.listHostedZones().getHostedZones();
    	for (int i=0;i<hostedZoneList.size();i++)
    		if (hostedZoneList.get(i).getName().startsWith((baseDomain)))
    		{
    			hostedZoneId = hostedZoneList.get(i).getId();
    			break;
    		}
    	//if there is no associated hosted zone for the domain - just create a new one
    	if (hostedZoneId == null)
    		hostedZoneId = route53.createHostedZone(new CreateHostedZoneRequest()
    										.withName(baseDomain)
    										.withCallerReference(XE_AWS_ROUTE53_CALLER_REFERENCE + "_" + UUID.randomUUID().toString())
    										.withHostedZoneConfig(new HostedZoneConfig().withComment(XE_AWS_ROUTE53_HOSTED_ZONE_COMMENT))).getHostedZone().getId();
    	
    	//if there is still no hosted zone Id we should stop processing this command
    	if (hostedZoneId == null)
    		return;
    	
    	//let's display the associated NS and SOA records to the user 
    	List<ResourceRecordSet> resourceRecordSetList = route53.listResourceRecordSets(new ListResourceRecordSetsRequest(hostedZoneId)).getResourceRecordSets();
    	for (int i=0;i < resourceRecordSetList.size();i++)
    	{
    		if (resourceRecordSetList.get(i).getType().compareToIgnoreCase(RRType.NS.toString()) == 0)
    		{
    			System.out.println("The NS record:");
    			for (int j=0;j < resourceRecordSetList.get(i).getResourceRecords().size();j++)
    				System.out.println(resourceRecordSetList.get(i).getResourceRecords().get(j).getValue());
    			System.out.println();
    		}
    		if (resourceRecordSetList.get(i).getType().compareToIgnoreCase(RRType.SOA.toString()) == 0)
    		{
    			System.out.println("The SOA record:");
    			for (int j=0;j < resourceRecordSetList.get(i).getResourceRecords().size();j++)
    				System.out.println(resourceRecordSetList.get(i).getResourceRecords().get(j).getValue());
    			System.out.println();
    		}
    	}
    	
    	ChangeBatch changes = new ChangeBatch().withChanges(new Change()
    																.withAction(ChangeAction.CREATE)
    																.withResourceRecordSet(new ResourceRecordSet()
    																								.withName(parameters.get("domain-name"))
    																								.withType(RRType.A)
    																								.withTTL(XE_AWS_ROUTE53_DEFAULT_TTL)
    																								.withResourceRecords(new ResourceRecord().withValue(publicIp))));
    	
    	route53.changeResourceRecordSets(new ChangeResourceRecordSetsRequest(hostedZoneId, changes));
    	System.out.println("Executing associate domain command...ended successfully!");
    	
    }
    
    /**
	 * The method responsible for processing 'remove_domain' commands
	 */
    public void process_remove_domain_command()
    {
    	System.out.println("Executing remove domain command...");
    	
    	//let's instantiate Route 53 client
    	AmazonRoute53 route53 = new AmazonRoute53Client(new BasicAWSCredentials(parameters.get("access-key"), parameters.get("secret-key")));
    	
    	String hostedZoneId = null;
    	//first let's search our Hosted Zone
    	List<HostedZone> hostedZoneList = route53.listHostedZones().getHostedZones();
    	for (int i=0;i<hostedZoneList.size();i++)
    		if (hostedZoneList.get(i).getConfig().getComment().compareToIgnoreCase(XE_AWS_ROUTE53_HOSTED_ZONE_COMMENT) == 0)
    		{
    			hostedZoneId = hostedZoneList.get(i).getId();
    			break;
    		}
    	//if there is no associated hosted zone stop this command processing
    	if (hostedZoneId == null)
    		return;
    	
    	List<ResourceRecordSet> resourceRecordSetList = route53.listResourceRecordSets(new ListResourceRecordSetsRequest(hostedZoneId)).getResourceRecordSets();
    	ChangeBatch changeBatch = new ChangeBatch().withComment(XE_AWS_ROUTE53_REMOVE_RECORDS_BATCH_COMMENT);
    	List<Change> changes = new ArrayList<Change>();
    	for (int i=0;i<resourceRecordSetList.size();i++)
    		if (resourceRecordSetList.get(i).getType().compareToIgnoreCase(RRType.A.toString()) == 0)
    			changes.add(new Change()
    								.withAction(ChangeAction.DELETE )
    								.withResourceRecordSet(resourceRecordSetList.get(i)));
    	
    	changeBatch.setChanges(changes);
    	route53.changeResourceRecordSets(new ChangeResourceRecordSetsRequest()
    											.withHostedZoneId(hostedZoneId)
    											.withChangeBatch(changeBatch));
    	
    	//now that the hosted zone is empty we can actually delete it
    	route53.deleteHostedZone(new DeleteHostedZoneRequest(hostedZoneId));
    	
    	System.out.println("Executing remove domain command...ended successfully!");
    }
	
	/**
	 * Entry point of the application
	 * @param args	Command line arguments 
	 */
	public static void main(String[] args) 
	{
		AwsConsoleApp app = new AwsConsoleApp();
		
		if (!app.parseArguments(args))
			return;
		
		try 
		{
			app.init(app.parameters.get("access-key"), app.parameters.get("secret-key"));
		}
		catch (AmazonServiceException ase) 
		{
			System.err.println("Caught Exception: " + ase.getMessage());
			System.err.println("Reponse Status Code: " + ase.getStatusCode());
			System.err.println("Error Code: " + ase.getErrorCode());
			System.err.println("Request ID: " + ase.getRequestId());
			ase.printStackTrace(System.err);
	    }
		catch (AmazonClientException ace)
		{
			System.err.println("Caught Exception: " + ace.getMessage());			
			ace.printStackTrace(System.err);
		}
		
		Method commandProcessor = null;
		try
		{
			commandProcessor = app.getClass().getMethod("process_"+args[0]+"_command", (Class[])null);
		}
		catch (NoSuchMethodException nsme)
		{
			System.err.println("Caught Exception: The provided command has no implementation");
			nsme.printStackTrace(System.err);
		}
		catch (SecurityException se)
		{
			System.err.println("Caught Exception: " + se.getMessage());
			se.printStackTrace(System.err);
		}
		
		try
		{
			if (commandProcessor != null)
				commandProcessor.invoke(app, (Object[])null);
		}
		catch (IllegalAccessException iae)
		{
			System.err.println("Caught Exception: " + iae.getMessage());
			iae.printStackTrace(System.err);
		}
		catch (IllegalArgumentException iae)
		{
			System.err.println("Caught Exception: " + iae.getMessage());
			iae.printStackTrace(System.err);
		}
		catch (InvocationTargetException ite)
		{
			System.err.println("Caught Exception: " + ite.getMessage());
			ite.printStackTrace(System.err);
		}
		catch (AmazonServiceException ase) 
		{
			System.err.println("Caught Exception: " + ase.getMessage());
			System.err.println("Reponse Status Code: " + ase.getStatusCode());
			System.err.println("Error Code: " + ase.getErrorCode());
			System.err.println("Request ID: " + ase.getRequestId());
			ase.printStackTrace(System.err);
	    }
		catch (AmazonClientException ace)
		{
			System.err.println("Caught Exception: " + ace.getMessage());			
			ace.printStackTrace(System.err);
		}
	}

}
