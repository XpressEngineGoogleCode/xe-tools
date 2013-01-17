package arnia.xemobile.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="menuItem")
public class XEMenuItem 
{
	@Element
	public String menuItemName;
	
	@Element
	public String srl;
	
	@Element
	public String open_window;
	
	@Element(required=false)
	public String url;
}
