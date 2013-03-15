 package arnia.xemobile.page_management;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.XEFragment;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEPage;

public class XEMobilePageController extends XEFragment implements OnClickListener, OnItemClickListener
{
	private ListView listView;
	private XEMobilePageAdapter adapter;
	private XEArrayList list;
	private Button addPageButton;
	
//		@Override
//		protected void onCreate(Bundle savedInstanceState)
//		{
//			super.onCreate(savedInstanceState);
//			setContentView(R.layout.xemobilepagelayout);
//			
//			listView = (ListView) findViewById(R.id.XEMOBILE_PAGE_LISTVIEW);
//			addPageButton = (Button) findViewById(R.id.XEMOBILE_PAGE_ADDBUTTON);
//			addPageButton.setOnClickListener(this);
//			
//			adapter = new XEMobilePageAdapter(this);
//			listView.setAdapter(adapter);
//			listView.setOnItemClickListener(this);
//		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.xemobilepagelayout, container,false);
					
			listView = (ListView) view.findViewById(R.id.XEMOBILE_PAGE_LISTVIEW);
			addPageButton = (Button) view.findViewById(R.id.XEMOBILE_PAGE_ADDBUTTON);
			addPageButton.setOnClickListener(this);
			
			adapter = new XEMobilePageAdapter(this.activity);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
			return view;
		}
		@Override
		public void onResume() 
		{
			super.onResume();

			//send request to get pages
//			startProgress("Loading...");
			GetPagesAsyncTask asyncRequest = new GetPagesAsyncTask();
			asyncRequest.execute();

		}
		
		
		//method called when one of the 
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) 
		{
			XEPage page = list.pages.get(position);
			
			if(page.page_type.equals("ARTICLE"))
			{
			Intent intent = new Intent(this.activity,XEMobilePageTextEditor.class);
			
			intent.putExtra("mid", page.mid);
			intent.putExtra("document_srl", page.document_srl); 
			
			startActivity(intent);
			}
			else if( page.page_type.equals("WIDGET") )
				{
					Intent intent = new Intent(this.activity, XEMobileWidgetController.class);
					intent.putExtra("mid", page.mid);
					if(page.virtual_site!=null){
						intent.putExtra("vid", page.virtual_site);
					}
					startActivity(intent);
				
				}
		}

		//called when a button in listview is pressed
		@Override
		public void onClick(View v) 
		{

			
//			//edit button pressed
//			if( v.getId() == R.id.XEMOBILE_PAGEITEMCELL_EDITBUTTON )
//			{
//				int index = (Integer) v.getTag();
//				//page where the user clicked
//				XEPage page = list.pages.get(index);
//				
//				Intent intent = new Intent(XEMobilePageController.this,XEMobilePageEditController.class);
//				intent.putExtra("browser_title", page.browser_title);
//				intent.putExtra("mid", page.mid);
//				intent.putExtra("page type", page.page_type);
//				intent.putExtra("layout", page.layout_srl);
//				intent.putExtra("module_srl", page.module_srl);
//				
//				startActivity(intent);
//			}
//			//delete button pressed
//			else if( v.getId() == R.id.XEMOBILE_PAGEITEMCELL_DELETEBUTTON )
//			{
//				int index = (Integer) v.getTag();
//				//page where the user clicked
//				XEPage page = list.pages.get(index);
//				
//				startProgress("Loading...");
//				DeletePageAsyncTask task = new DeletePageAsyncTask();
//				task.execute(new String[]{page.module_srl});
//			}
//			else if( v.getId() == R.id.XEMOBILE_PAGE_ADDBUTTON)
//			{
//				Intent intent = new Intent(XEMobilePageController.this,XEMobilePageAddController.class);
//				startActivity(intent);
//			}
			//edit button pressed
//			if( v.getId() == R.id.XEMOBILE_PAGEITEMCELL_EDITBUTTON )
//			{
//				int index = (Integer) v.getTag();
//				//page where the user clicked
//				XEPage page = list.pages.get(index);
//				
//				Intent intent = new Intent(XEMobilePageController.this,XEMobilePageEditController.class);
//				intent.putExtra("browser_title", page.browser_title);
//				intent.putExtra("mid", page.mid);
//				intent.putExtra("page type", page.page_type);
//				intent.putExtra("layout", page.layout_srl);
//				intent.putExtra("module_srl", page.module_srl);
//				
//				startActivity(intent);
//			}
			//delete button pressed
			if( v.getId() == R.id.XEMOBILE_PAGEITEMCELL_DELETEBUTTON )
			{
				int index = (Integer) v.getTag();
				//page where the user clicked
				XEPage page = list.pages.get(index);
				
//				startProgress("Loading...");
				DeletePageAsyncTask task = new DeletePageAsyncTask();
				task.execute(new String[]{page.module_srl});
			}
			else if( v.getId() == R.id.XEMOBILE_PAGE_ADDBUTTON)
			{
				Intent intent = new Intent(this.activity,XEMobilePageAddController.class);
				startActivity(intent);
			}
		}
		
		//AsyncTask that deletes a page
		private class DeletePageAsyncTask extends AsyncTask<String, Object, Object>
		{

			@Override
			protected Object doInBackground(String... param) 
			{
				String moduleSRL = param[0];
				
				HashMap<String, String> params = new HashMap<String, String>();
				
				params.put("ruleset", "deletePage");
				params.put("module", "page");
				params.put("act","procPageAdminDelete");
				params.put("module_srl", moduleSRL);
				
				XEHost.getINSTANCE().postMultipart(params, "/");
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Object result)
			{
				super.onPostExecute(result);
//				dismissProgress();
//				startProgress("Loading...");
				GetPagesAsyncTask task = new GetPagesAsyncTask();
				task.execute();
			}
		}

		//AsyncTask that makes a request to obtain all pages
		private class GetPagesAsyncTask extends AsyncTask<Object,Object,Object>
		{
			String xmlData;
			
			@Override
			protected Object doInBackground(Object... params) 
			{
				 xmlData = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationDisplayPages");
				
				 Serializer serializer = new Persister();        
		         
		           Reader reader = new StringReader(xmlData);
		           try {
		        	  list = 
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
				
//				isLoggedIn(xmlData, XEMobilePageController.this);
				
//				dismissProgress();
				if( list != null && list.pages != null )
				{
				adapter.setArrayWithPages(list.pages);
				adapter.notifyDataSetChanged();
				}
			}
			
		}

		
}
