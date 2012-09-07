package arnia.xemobile.classes;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;



public class XEArrayList
{
	@ElementList(inline=true,required=false)
	public List<XEMember> members;
	
	@ElementList(inline=true,required=false)
	public ArrayList<XEMenu> menus;
	
	@ElementList(inline=true,required=false)
	public ArrayList<XEModule> modules;
	
	@ElementList(inline=true,required=false)
	public ArrayList<XEPage> pages;

	@ElementList(inline=true,required=false)
	public ArrayList<XELayout> layouts;

	@ElementList(inline=true,required=false)
	public ArrayList<XEDayStats> stats;
	
	@ElementList(inline=true,required=false)
	public ArrayList<XETextyle> textyles;
	
	@ElementList(inline=true,required=false)
	public ArrayList<XETextylePost> posts;
	
	@ElementList(inline=true,required=false)
	public ArrayList<XETextylePage> textylePages;
	
	@ElementList(inline=true,required=false)
	public ArrayList<XESkin> skins;
	
	@ElementList(inline=true,required=false)
	public ArrayList<XEComment> comments;
}
