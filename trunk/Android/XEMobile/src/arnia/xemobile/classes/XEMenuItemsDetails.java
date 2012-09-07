package arnia.xemobile.classes;

import org.simpleframework.xml.Default;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root (name="response")
public class XEMenuItemsDetails 
{
	@Element
	@Path("menu_item[1]")
	public String name;
	
	@Element
	@Path("menu_item[1]")
	public String menu_item_srl;
	
	@Element
	@Path("menu_item[1]")
	public String open_window;
	
	@Element 
	@Path("menu_item[1]")
	public String url;
	
	@Element
	@Path("menu_item[1]")
	public String moduleType;
}
