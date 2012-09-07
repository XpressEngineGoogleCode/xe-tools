package arnia.xemobile_textyle_pages;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.classes.XETextylePage;

//Activity that has a ListView with XEPage
public class XEMobileTextylePagesController extends XEActivity implements OnItemClickListener, OnClickListener
{
	private XETextyle textyle;
	private XEArrayList array;
	
	//UI elements references
	private Button addPageButton;
	private ListView listView;
	
	//adapter for ListView
	private XEMobileTextylePagesAdapter adapter;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.xemobiletextylepageslayout);
			
			//take references to UI elements
			addPageButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_PAGES_ADDBUTTON);
			listView = (ListView) findViewById(R.id.XEMOBILE_TEXTYLE_PAGES_LISTVIEW);

			// make the adapter and set it to the list
			adapter = new XEMobileTextylePagesAdapter(this);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
			
			//get the textyle object passed between activities
			textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
			
			// when the add page button is pressed, another activity is started
			addPageButton.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(XEMobileTextylePagesController.this,XEMobileTextyleAddPageController.class);
					intent.putExtra("textyle", textyle);
					startActivity(intent);
				}
			});
			
		}
		
		@Override
		protected void onResume() 
		{
			super.onResume();
		
			startProgress("Loading...");
			
			// send request to get the XEPages
			GetPagesAsyncTask task = new GetPagesAsyncTask();
			task.execute();
		}
		
		public void loadPagesInAdapter()
		{
			if(  array == null || array.textylePages == null )
			{
				adapter.setPages(null);
				adapter.notifyDataSetChanged();
				Toast toast = Toast.makeText(getApplicationContext(), "No pages!", Toast.LENGTH_SHORT);
				toast.show();
			}
			else
			{
				adapter.setPages(array.textylePages);
				adapter.notifyDataSetChanged();
			}
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) 
		{
			XETextylePage page = array.textylePages.get(arg2);
			
			Intent intent = new Intent(XEMobileTextylePagesController.this,XEMobileTextyleEditPageController.class);
			intent.putExtra("menu_mid", page.mid);
			intent.putExtra("menuName",page.name);
			intent.putExtra("textyle",textyle);
			startActivity(intent);
		}

		@Override
		public void onClick(View v) 
		{
			XETextylePage page = array.textylePages.get( (Integer) v.getTag() );
			
			DeletePageAsyncTask delete = new DeletePageAsyncTask();
			delete.execute(page);
		}
		
		private class GetPagesAsyncTask extends AsyncTask<Object, Object, Object>
		{
			String response;

			@Override
			protected Object doInBackground(Object... params) 
			{
			    response = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationExtraMenuList&site_srl=" + textyle.site_srl);
				
			    Serializer serializer = new Persister();
			    
			    Reader reader = new StringReader(response);
			    try
			    {
			    	array = serializer.read(XEArrayList.class,reader, false);
			    	
			    }catch (Exception e) 
			    {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Object result) 
			{
				super.onPostExecute(result);
				
				if (isLoggedIn(response, XEMobileTextylePagesController.this))
				{
					dismissProgress();
					loadPagesInAdapter();
				}
			}
			
		}
		
		private class DeletePageAsyncTask extends AsyncTask<XETextylePage, Object, Object>
		{

			@Override
			protected Object doInBackground(XETextylePage... params) 
			{
				String xmlRequest = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
						"<methodCall>\n<params>\n" +
						"<menu_mid><![CDATA["+ params[0].mid +"]]></menu_mid>\n" +
						"<module><![CDATA[textyle]]></module>\n" +
						"<act><![CDATA[procTextyleToolExtraMenuDelete]]></act>\n" +
						"<vid><![CDATA["+ textyle.domain +"]]></vid>\n</params>\n</methodCall>";
				
				XEHost.getINSTANCE().postRequest("/index.php", xmlRequest);
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Object result) 
			{
				super.onPostExecute(result);
			
				GetPagesAsyncTask task = new GetPagesAsyncTask();
				task.execute();
			}
			
		}
}
