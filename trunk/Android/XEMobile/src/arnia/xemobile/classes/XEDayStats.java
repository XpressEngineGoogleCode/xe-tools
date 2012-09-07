package arnia.xemobile.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="day")
public class XEDayStats 
{
	@Element
	public String date;
	
	@Element
	public String unique_visitor;
	
	@Element
	public String pageview;
}
