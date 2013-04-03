package arnia.xemobile.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="newmodule")
public class XEModule 
{
	@Element
	public String module;
	
	@Element
	public String module_srl;
	
	@Element
	public String page_type;
	
	@Override
	public String toString() {
		return module;
	}
}
