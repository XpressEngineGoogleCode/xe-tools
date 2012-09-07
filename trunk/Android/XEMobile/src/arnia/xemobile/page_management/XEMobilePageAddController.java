package arnia.xemobile.page_management;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XELayout;
import arnia.xemobile.classes.XEResponse;

public class XEMobilePageAddController extends XEActivity implements OnClickListener
{
	//UI references
	private RadioButton widgetButton;
	private RadioButton articleButton;
	private RadioButton externalButton;
	private EditText pageName;
	private EditText browserTitle;
	private EditText cachingTime;
	private Spinner layoutSpinner;
	private Button saveButton;
	
	//adapter for the list with pages
	private ArrayAdapter<String> adapter;
	
	private XEArrayList listLayouts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobilepageaddlayout);
		
		widgetButton = (RadioButton) findViewById(R.id.XEMOBILE_ADDPAGE_RADIO_WIDGET);
		articleButton = (RadioButton) findViewById(R.id.XEMOBILE_ADDPAGE_RADIO_ARTICLE);
		externalButton = (RadioButton) findViewById(R.id.XEMOBILE_ADDPAGE_RADIO_EXTERNAL);
		articleButton.setChecked(true);
		
		pageName = (EditText) findViewById(R.id.XEMOBILE_ADDPAGE_PAGENAME);
		browserTitle = (EditText) findViewById(R.id.XEMOBILE_ADDPAGE_BROWSERTITLE);
		cachingTime = (EditText) findViewById(R.id.XEMOBILE_ADDPAGE_CACHING);
		
		layoutSpinner = (Spinner) findViewById(R.id.XEMOBILE_ADDPAGE_LAYOUTSPINNER);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		
		saveButton = (Button) findViewById(R.id.XEMOBILE_ADDPAGE_SAVEBUTTON);
		
		saveButton.setOnClickListener(this);
			
		GetLayoutsAsyncTask task = new GetLayoutsAsyncTask();
		task.execute();
	}
	
	//method called when the save button is pressed
	@Override
	public void onClick(View v) 
	{
		//start request to add the page
		AddPageAsyncTask task = new AddPageAsyncTask();
		task.execute();
	}
	
	//method that returns the type of the page
	public String getPageType()
	{
		if( widgetButton.isChecked() ) return "WIDGET";
		if( articleButton.isChecked()) return "ARTICLE";
		if( externalButton.isChecked() ) return "EXTERNAL";
		
		return "";
	}
	
	//Async Task that adds a page
	private class AddPageAsyncTask extends AsyncTask<String, Object, Object>
	{

		String response;
		
		@Override
		protected Object doInBackground(String... param) 
		{
			//building the request
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("error_return_url", "/index.php?module=admin&act=dispPageAdminInsert");
			params.put("ruleset", "insertPage");
			params.put("module", "mobile_communication");
			params.put("act","procmobile_communicationPageInsert");
			params.put("page_type",getPageType());
			params.put("page_name", pageName.getText().toString());
			params.put("browser_title",browserTitle.getText().toString());
			params.put("skin", "default");
			params.put("mskin","default");
			
			XELayout layout = listLayouts.layouts.get(layoutSpinner.getSelectedItemPosition());
			params.put("layout_srl",layout.layout_srl);
			
			//sending the request
			response = XEHost.getINSTANCE().postMultipart(params, "/");
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			XEResponse responseConfirmation = null;
			try
			{
				Serializer serializer = new Persister();
				
				Reader reader = new StringReader(response);
				responseConfirmation = serializer.read(XEResponse.class, reader,false);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			if( responseConfirmation.value.equals("true") ) finish();
		}
	}
	
	//Async task that gets the list with Layouts
	private class GetLayoutsAsyncTask extends AsyncTask<Object, Object, Object>
	{
		String response;
		
		@Override
		protected Object doInBackground(Object... params) 
		{
			//make request
			response = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationGetLayout");
			  
			//parse response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			try
			{
				listLayouts = serializer.read(XEArrayList.class, reader,false);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			
			//check to see if user is logged in
			isLoggedIn(response, XEMobilePageAddController.this);
			
			//add layouts to spinner adapter
			if( listLayouts != null && listLayouts.layouts != null )
			{
				for(int i=0;i<listLayouts.layouts.size();i++) adapter.add(listLayouts.layouts.get(i).layout_name);
				layoutSpinner.setAdapter(adapter);
			}
			
		}
	}

}
