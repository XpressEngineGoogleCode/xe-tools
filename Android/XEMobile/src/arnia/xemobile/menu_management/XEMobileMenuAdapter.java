package arnia.xemobile.menu_management;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.classes.XEMenu;

//Adapter for Menu list
public class XEMobileMenuAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<XEMenu> arrayWithMenus;
	
	//getter
	public ArrayList<XEMenu> getArrayWithMenus() 
	{
		return arrayWithMenus;
	}
	
	//setter
	public void setArrayWithMenus(ArrayList<XEMenu> arrayWithMenus) {
		this.arrayWithMenus = arrayWithMenus;
	}
	
	//constructor
	public XEMobileMenuAdapter(Context context )
	{
		
		this.arrayWithMenus = new ArrayList<XEMenu>();
		this.context = context;
	}
	
	@Override
	public int getCount() 
	{
		if( arrayWithMenus == null ) return 0;
			return arrayWithMenus.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return this.arrayWithMenus.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		XEMenu menu = this.arrayWithMenus.get(position);
		
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.xemobilemenucellview, null);
		}
		
		TextView textView = (TextView) convertView.findViewById(R.id.XEMOBILE_MENUCELL_TEXTVIEW);
		textView.setText(menu.menuName);
		
		Button deleteButton = (Button) convertView.findViewById(R.id.XEMOBILE_MENUCELL_DELETEBUTTON);
		
//		deleteButton.setOnClickListener((OnClickListener) context);
		deleteButton.setTag(position);
		
		return convertView;
	}	
	
}
