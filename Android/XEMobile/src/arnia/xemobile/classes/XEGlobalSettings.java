package arnia.xemobile.classes;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="response")
public class XEGlobalSettings extends XESettings
{
	@Element
	public String langs;
	
	@Element 
	public String default_lang;
	
	@Element
	public String timezone;
	
	@Element
	public String mobile;
	
	@Element(required = false)
	public String ips;
	
	@Element
	public String default_url;
	
	@Element
	public String use_ssl;
	
	@Element
	public String rewrite_mode;
	
	@Element
	public String use_sso;
	
	@Element
	public String db_session;
	
	@Element
	public String qmail;
	
	@Element
	public String html5;
	
	public ArrayList<String> getSelectedLanguages()
	{
		String[] array = langs.split(":");
		
		ArrayList<String> returned = new ArrayList<String>();
		
		for(int i = 0;i<array.length;i++)
		{
			returned.add(array[i]);
		}
		
		return returned;
	}
	
}
