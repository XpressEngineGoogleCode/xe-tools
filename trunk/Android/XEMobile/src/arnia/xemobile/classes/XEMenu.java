package arnia.xemobile.classes;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="menu")
public class XEMenu 
{
	@Element
	public String menuName;
	
	@Element
	public String menuSrl;
	
	@ElementList(inline=true,required=false)
	public ArrayList<XEMenuItem> menuItems;
}
