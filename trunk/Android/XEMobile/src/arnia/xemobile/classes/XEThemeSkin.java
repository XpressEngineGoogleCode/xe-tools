package arnia.xemobile.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="skin")
public class XEThemeSkin
{
	@Element
	public String module;
	
	@Element
	public String name;	
	
}
