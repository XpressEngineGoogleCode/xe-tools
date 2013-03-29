package arnia.xemobile.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="document")
public class XEDocument
{
	@Element
	public String alias;
	
	@Element
	public String content;	
	
}
