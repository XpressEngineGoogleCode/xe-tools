package arnia.xemobile.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="post")
public class XETextylePost 
{
	@Element
	public String document_srl;
	
	@Element
	public String module_srl;
	
	@Element
	public String category_srl;
	
	@Element
	public String title;
	
	@Override
	public String toString() 
	{
		return title;
	}
}
