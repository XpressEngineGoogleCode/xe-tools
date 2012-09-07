package arnia.xemobile.page_management;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XELayout;
import arnia.xemobile.classes.XEResponse;

public class XEMobilePageEditController extends XEActivity implements OnClickListener
{
	//UI references
	private TextView pageTypeTextView;
	private EditText moduleNameEditText;
	private EditText browserTitleEditText;
	private Spinner layoutSpinner;
	private Button saveButton;
	
	private XEArrayList listLayouts;
	
	//properties of the page that we are editing
	private String browserTitle;
	private String mid;
	private String pageType;
	private String layoutSRL;
	private String moduleSRL;
	
	//spinner adapter
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobilepageeditlayout);
		
		//get reference to UI elements
		pageTypeTextView = (TextView) findViewById(R.id.XEMOBILE_PAGEEDIT_PAGETYPE);
		moduleNameEditText = (EditText) findViewById(R.id.XEMOBILE_PAGEEDIT_MODULENAME);
		browserTitleEditText = (EditText) findViewById(R.id.XEMOBILE_PAGEEDIT_BROWSERTITLE);
		layoutSpinner = (Spinner) findViewById(R.id.XEMOBILE_PAGEEDIT_LAYOUTSPINNER);
		saveButton = (Button) findViewById(R.id.XEMOBILE_PAGEEDIT_SAVEBUTTON);
		saveButton.setOnClickListener(this);
		
		browserTitle = getIntent().getStringExtra("browser_title");
		mid = getIntent().getStringExtra("mid");
		pageType = getIntent().getStringExtra("page type");
		layoutSRL = getIntent().getStringExtra("layout");
		moduleSRL = getIntent().getStringExtra("module_srl");
		
		pageTypeTextView.setText("Page Type: " + pageType);
		moduleNameEditText.setText(mid);
		browserTitleEditText.setText(browserTitle);
				
		//make async request to get all layouts
		GetLayoutsAsyncTask layoutsAsyncTask = new GetLayoutsAsyncTask();
		layoutsAsyncTask.execute();
		
		//prepare the spinner
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	}
	
	private class GetLayoutsAsyncTask extends AsyncTask<Object, Object, Object>
	{
		String response;
		
		@Override
		protected Object doInBackground(Object... params) 
		{
			 response =	XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationGetLayout");
			  
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
			
			isLoggedIn(response, XEMobilePageEditController.this);
			
			if(listLayouts != null && listLayouts.layouts != null)
			{
				for(int i=0;i<listLayouts.layouts.size();i++) adapter.add(listLayouts.layouts.get(i).layout_name);
				layoutSpinner.setAdapter(adapter);
			
				//select the correct layout in spinner
				int i;
				for(i=0;i<listLayouts.layouts.size();i++)
				{
					XELayout layout = listLayouts.layouts.get(i);
					if( layoutSRL.equals(layout.layout_srl) ) 
					{
					layoutSpinner.setSelection(i);
					break;
					}
				}
			}
			
			
		}
	}


	//called when the save button is pressed
	@Override
	public void onClick(View v) 
	{
		SavePageAsyncTask saveAsyncTask = new SavePageAsyncTask();
		saveAsyncTask.execute();
	}
	
	private class SavePageAsyncTask extends AsyncTask<Object, Object, Object>
	{
		XEResponse confirmation = null;

		@Override
		protected Object doInBackground(Object... paramss) 
		{
			HashMap<String, String> params = new HashMap<String, String>();
			
			params.put("error_return_url", "/index.php?module=admin&act=dispPageAdminInsert");
			params.put("ruleset", "updatePage");
			params.put("module", "mobile_communication");
			params.put("module_srl", moduleSRL);
			params.put("act", "procmobile_communicationPageInsert");
			params.put("page_name",moduleNameEditText.getText().toString());
			params.put("browser_title", browserTitleEditText.getText().toString());
			params.put("skin","default");
			params.put("mskin","default");
			
			int i = layoutSpinner.getSelectedItemPosition();
			
			params.put("layout_srl", listLayouts.layouts.get(i).layout_srl);
			
			String response = XEHost.getINSTANCE().postMultipart(params, "/");
			
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			
			try
			{
				confirmation = serializer.read(XEResponse.class, reader, false);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return confirmation;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			
			super.onPostExecute(result);
			
			if( confirmation != null && confirmation.value.equals("true") )
			{
				finish();
			}
		}
		
	}

	
}
