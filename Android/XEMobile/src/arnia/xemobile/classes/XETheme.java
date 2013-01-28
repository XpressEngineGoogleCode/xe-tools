package arnia.xemobile.classes;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="theme")
public class XETheme 
{
	@Element
	public String name;
	
	@Element
	public String thumbnail;
	
	@Element
	public String version;
	
	@Element
	public String date;
	
	@Element
	public String description;
	
	@Element
	public String layout_srl;
	
	@Element
	public String selected_layout;
	
	@ElementList(inline=true,required=false)
	public ArrayList<XEThemePublisher> publishers;
	
	@ElementList(inline=true,required=false)
	public ArrayList<XEThemeSkin> skins;
	
	@Override
	public String toString(){
		return name + "\nBy " + publishers.get(0).name + "  " + version + " " + "\n\n" + description;  
	}
}
