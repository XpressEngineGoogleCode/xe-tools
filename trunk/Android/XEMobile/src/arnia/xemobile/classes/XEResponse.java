package arnia.xemobile.classes;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root (name="response")
public class XEResponse 
{
	@Element (required = false) 
	public String value;
	
	@Element (required = false)
	public String message;
	
	@Element (required = false)
	public String document_srl;
}
