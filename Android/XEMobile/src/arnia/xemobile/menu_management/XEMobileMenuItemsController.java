package arnia.xemobile.menu_management;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;

import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEMenu;
import arnia.xemobile.classes.XEMenuItem;

public class XEMobileMenuItemsController extends XEActivity
{
	private String menuItemParentSRL;
	private ArrayList<XEMenuItem> arrayWithMenuItems;
	private XEMobileMenuItemsAdapter adapter;
	
	private Button addMenuItemButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobilemenuitemslayout);
		ListView listView = (ListView) findViewById(R.id.XEMOBILE_MENUITEMS_LISTVIEW);
		addMenuItemButton = (Button) findViewById(R.id.XEMOBILE_MENUITEMS_ADDMENUITEM);
		
		addMenuItemButton.setOnClickListener(new OnClickListener() 
		{
			//method called when the Add Button is pressed 
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(XEMobileMenuItemsController.this,XEMobileAddMenuItemController.class);
				intent.putExtra("menu_parent_srl", menuItemParentSRL);
				startActivity(intent);
			}
		});
		
		Intent intent = getIntent();
		menuItemParentSRL = intent.getStringExtra("menu_item_parent_srl");
		
		adapter = new XEMobileMenuItemsAdapter(this, menuItemParentSRL);
		listView.setAdapter(adapter);
	}
	
	boolean isOnPause;
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		startProgress("Loading...");
		GetMenusAsyncTask getAsyncTask = new GetMenusAsyncTask();
		getAsyncTask.execute();
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		isOnPause = true;
	}
	
	//Async Task request to get a list of XEMenuItems
	private class GetMenusAsyncTask extends AsyncTask<Object, Object, Object>
	{
		XEArrayList arrayWithMenus = null;
		String xmlData;
		
		@Override
		protected Object doInBackground(Object... params) 
		{
			//send request
			xmlData = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationDisplayMenu");
			
			//parse response
			Serializer serializer = new Persister();        
			Reader reader = new StringReader(xmlData);
			try {
				arrayWithMenus = 
					serializer.read(XEArrayList.class, reader, false); 
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			
			isLoggedIn(xmlData, XEMobileMenuItemsController.this);
			
			dismissProgress();
			
			 if( arrayWithMenus != null && arrayWithMenus.menus != null)
			   {
				   for(int i = 0 ; i<arrayWithMenus.menus.size() ; i++)
				   {
					   XEMenu menu = arrayWithMenus.menus.get(i);
					   if( menu.menuSrl.equals(menuItemParentSRL) )
					   {
						   arrayWithMenuItems =  menu.menuItems;
						   adapter.setArrayWithMenuItems(arrayWithMenuItems);
						   adapter.notifyDataSetChanged();
						   break;
					   }
				   }
			   }
		}
	}

}
