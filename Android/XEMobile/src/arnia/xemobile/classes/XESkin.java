package arnia.xemobile.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="skin")
public class XESkin
{
	@Element
	public String id;
	
	@Element
	public String name;
	
	@Element(required=false)
	public String description;
	
	@Element
	public String large_ss;
	
	@Element
	public String small_ss;
	
	public String getSmall_ss() 
	{
		int n = small_ss.indexOf("modules/textyle");
		
		small_ss = small_ss.substring(n-1);
		
		return small_ss;
	}
}
