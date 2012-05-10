/**
 * This class provides an Comparator implementation for sorting the command line options (sort by mandatory, name) 
 */
package utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @author Catalin
 *
 */
public class CommandOptionComparator implements Comparator<Object>
{
	HashMap<String, List<String>> options;
	public CommandOptionComparator(HashMap<String, List<String>> options)
	{
		this.options = options;
	}
	public int compare(Object o1, Object o2)
	{
		byte o1Priority = 0;
		byte o2Priority = 0;
		if (o1.toString().indexOf('.') == -1)
			o1Priority = 2;//2 means mandatory parameters have higher priority (should be displayed firsts)
		if (o2.toString().indexOf('.') == -1)
			o2Priority = 2;//2 means mandatory parameters have higher priority (should be displayed firsts)
		
		o1Priority += options.get(o1).get(1).equalsIgnoreCase("true")?1:0;//1 means required parameters have higher priority but less than mandatory
		o2Priority += options.get(o2).get(1).equalsIgnoreCase("true")?1:0;//1 means required parameters have higher priority but less than mandatory 
		
		if (o1Priority != o2Priority)
			return (o2Priority - o1Priority);
		else
			return o1.toString().compareToIgnoreCase(o2.toString());

	}
	
	public boolean equals(Object obj)
	{
		return this.equals(obj);
	}
	
}