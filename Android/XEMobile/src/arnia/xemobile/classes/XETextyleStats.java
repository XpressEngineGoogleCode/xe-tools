package arnia.xemobile.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="response")
public class XETextyleStats
{
	@Element
	public String monday;
	
	@Element
	public String tuesday;
	
	@Element
	public String wednesday;
	
	@Element
	public String thursday;

	@Element
	public String friday;
	
	@Element
	public String saturday;
	
	@Element
	public String sunday;
}
