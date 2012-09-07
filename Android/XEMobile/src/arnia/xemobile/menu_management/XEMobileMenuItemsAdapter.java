package arnia.xemobile.menu_management;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.R.id;
import arnia.xemobile.R.layout;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEMenu;
import arnia.xemobile.classes.XEMenuItem;
import arnia.xemobile.classes.XEResponse;

//Adapter for MenuItems list
public class XEMobileMenuItemsAdapter extends BaseAdapter implements OnClickListener
{
	private Context context;
	private ArrayList<XEMenuItem> arrayWithMenuItems;
	private String menuItemSRL;
	
	//setter
	public void setArrayWithMenuItems(ArrayList<XEMenuItem> arrayWithMenuItems) {
		this.arrayWithMenuItems = arrayWithMenuItems;
	}
	
	//constructor
	public XEMobileMenuItemsAdapter(Context context, String menu)
	{
		this.context = context;
		this.arrayWithMenuItems = new ArrayList<XEMenuItem>();
		this.menuItemSRL = menu;
	}
	
	@Override
	public int getCount() 
	{
		if( arrayWithMenuItems == null ) return 0;
		return this.arrayWithMenuItems.size();
	}

	@Override
	public Object getItem(int arg0) 
	{
		return this.arrayWithMenuItems.get(arg0);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		XEMenuItem menuItem = this.arrayWithMenuItems.get(position);
		
		if( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.xemobilemenuitemscellview, null);
		}
		
		TextView menuItemNameTextView = (TextView) convertView.findViewById(R.id.XEMOBILE_MENUITEMCELL_TEXTVIEW);
		menuItemNameTextView.setText(menuItem.menuItemName);
		
		Button editButton = (Button) convertView.findViewById(R.id.XEMOBILE_MENUITEMCELL_EDITBUTTON);
		editButton.setTag(position);
		editButton.setOnClickListener(this);
		
		Button deleteButton = (Button) convertView.findViewById(R.id.XEMOBILE_MENUITEMCELL_DELETEBUTTON);
		deleteButton.setTag(position);
		deleteButton.setOnClickListener(this);
		
		return convertView;
	}

	//method called when one of the buttons is pressed: Edit Button or Delete Button
	@Override
	public void onClick(View v) 
	{
		int index = (Integer) v.getTag();
		XEMenuItem menuItem = arrayWithMenuItems.get(index);
		
		if( v.getId() == R.id.XEMOBILE_MENUITEMCELL_EDITBUTTON )
		{
			Intent intent = new Intent(context, XEMobileMenuItemEditController.class);

			intent.putExtra("menu_parent_srl",menuItemSRL);
			intent.putExtra("menu_item_srl",menuItem.srl);
			context.startActivity(intent);
		}
		else if( v.getId() == R.id.XEMOBILE_MENUITEMCELL_DELETEBUTTON )
		{
			DeleteMenuItemAsyncTask task = new DeleteMenuItemAsyncTask();
			task.execute(new String[]{menuItem.srl,Integer.toString(index)});
			
		}
	}

	// Async Task that deletes a menu item
	private class DeleteMenuItemAsyncTask extends AsyncTask<String, Object, Object>
	{
		String response;
		String menu_item_srl;
		int index;
		
		@Override
		protected Object doInBackground(String... param) 
		{
			menu_item_srl = param[0];
			index = Integer.parseInt(param[1]);
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("module", "mobile_communication");
			params.put("act", "procmobile_communicationDeleteMenuItem");
			params.put("menu_srl", menuItemSRL);
			params.put("menu_item_srl", menu_item_srl);
			
			response = XEHost.getINSTANCE().postMultipart(params, "/index.php");
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
		
			Serializer serializer = new Persister();        
	           
	           Reader reader = new StringReader(response);
	           try {
					 XEResponse confirmation = 
					    serializer.read(XEResponse.class, reader, false);
					
					if ( confirmation.value.equals("true") )
					{
						arrayWithMenuItems.remove(index);
						notifyDataSetChanged();
					}
					
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
		}
	}
}
