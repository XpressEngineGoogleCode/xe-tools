package arnia.xemobile.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="publisher")
public class XEThemePublisher
{
	@Element
	public String name;
	
	@Element
	public String email;	
	
}
