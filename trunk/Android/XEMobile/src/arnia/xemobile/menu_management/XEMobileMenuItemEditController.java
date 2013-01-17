package arnia.xemobile.menu_management;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEMenu;
import arnia.xemobile.classes.XEMenuItemsDetails;
import arnia.xemobile.classes.XEModule;

public class XEMobileMenuItemEditController extends XEActivity  implements OnClickListener,android.widget.CompoundButton.OnCheckedChangeListener
{
	//interface elements
	private EditText browserTitleEditText;
	private RadioButton createRadioOption;
	private RadioButton selectRadioOption;
	private RadioButton menuURLRadioOption;
	
	private RadioButton articleRadioOption;
	private RadioButton widgetRadioOption;
	private RadioButton externalRadioOption;
	
	private TextView moduleIDTextView;
	private EditText moduleIDEditText;
	private CheckBox newWindow;
	
	private TextView menuURLTextView;
	private EditText menuURLEditText;
	
	private Spinner spinner;
	private Button saveButon;
	
	//menu parent srl
	private String parentSRL;
	private String menuItemSRL;
	
	private XEMenuItemsDetails details;
	
	//Array with modules for spinner
	private XEArrayList modules;
	
	//spinner adapter
	private ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//load interface
		setContentView(R.layout.xemobilemenuitemeditlayout);
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		
		//take reference to interface objects
		spinner = (Spinner) findViewById(R.id.XEMOBILE_MENUITEMEDIT_SPINNER);
		saveButon = (Button) findViewById(R.id.XEMOBILE_MENUITEMEDIT_SAVEBUTTON);
		browserTitleEditText = (EditText) findViewById(R.id.XEMOBILE_MENUITEMEDIT_BROWSERTITLE);
		
		newWindow = (CheckBox)findViewById(R.id.XEMOBILE_MENUITEMEDIT_CHECKBOX);
		
		createRadioOption = (RadioButton) findViewById(R.id.XEMOBILE_MENUITEMEDIT_RADIO_CREATE);
		createRadioOption.setOnCheckedChangeListener(this);
		
		articleRadioOption = (RadioButton) findViewById(R.id.XEMOBILE_MENUITEMEDIT_RADIO_ARTICLE);
		widgetRadioOption = (RadioButton) findViewById(R.id.XEMOBILE_MENUITEMEDIT_RADIO_WIDGET);
		externalRadioOption = (RadioButton) findViewById(R.id.XEMOBILE_MENUITEMEDIT_RADIO_EXTERNAL);
		
		moduleIDEditText = (EditText) findViewById(R.id.XEMOBILE_MENUITEMEDIT_MODULEID_EDITTEXT);
		moduleIDTextView = (TextView) findViewById(R.id.XEMOBILE_MENUITEMEDIT_MODULEID_TEXTVIEW);
		
		menuURLEditText = (EditText) findViewById(R.id.XEMOBILE_MENUITEMEDIT_MENUURL_EDITTEXT);
		menuURLTextView = (TextView) findViewById(R.id.XEMOBILE_MENUITEMEDIT_MENUURL_TEXTVIEW);
		
		selectRadioOption = (RadioButton) findViewById(R.id.XEMOBILE_MENUITEMEDIT_RADIO_SELECT);
		selectRadioOption.setOnCheckedChangeListener(this);
		selectRadioOption.setChecked(true);
		
		
		menuURLRadioOption = (RadioButton) findViewById(R.id.XEMOBILE_MENUITEMEDIT_RADIO_MENUURL);
		menuURLRadioOption.setOnCheckedChangeListener(this);
		
		//get out of intent the parent srl
		parentSRL = getIntent().getStringExtra("menu_parent_srl");
		menuItemSRL = getIntent().getStringExtra("menu_item_srl");
		
		
		//make request to get a list of modules for spinner
		GetModulesAsyncTask task = new GetModulesAsyncTask();
		task.execute();
		
		//action for save button
		saveButon.setOnClickListener(this);
		
		
	}
	
	//called when the save button is pressed
	@Override
	public void onClick(View v) 
	{
		if( createRadioOption.isChecked() )
			{
				CreateMenuAsyncTask task = new CreateMenuAsyncTask();
				task.execute();
			}
		else if( selectRadioOption.isChecked() )
			{
				SelectModuleAsyncTask task = new SelectModuleAsyncTask();
				task.execute();
			}
		else if( menuURLRadioOption.isChecked() )
			{
				MenuURLAsyncTask task = new MenuURLAsyncTask();
				task.execute();
			}
	}
	
	//the method returns the type selected
	private String returnType()
	{
		if( articleRadioOption.isChecked() ) return "ARTICLE";
		else if( widgetRadioOption.isChecked() ) return "WIDGET";
		else if( externalRadioOption.isChecked() ) return "EXTERNAL";
		return "";
	}
	
	private String openInNewWindow()
	{
		if( newWindow.isChecked() ) return "Y";
		else return "N";
	}
	
	// update the interface when the user change the option
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		if( buttonView == menuURLRadioOption )
		{
			if( isChecked )
			{
					spinner.setVisibility(View.INVISIBLE);
					articleRadioOption.setVisibility(View.INVISIBLE);
					widgetRadioOption.setVisibility(View.INVISIBLE);
					externalRadioOption.setVisibility(View.INVISIBLE);
					
					moduleIDEditText.setVisibility(View.INVISIBLE);
					moduleIDTextView.setVisibility(View.INVISIBLE);
					
					menuURLTextView.setVisibility(View.VISIBLE);
					menuURLEditText.setVisibility(View.VISIBLE);
			}
			else
			{
				menuURLTextView.setVisibility(View.INVISIBLE);
				menuURLEditText.setVisibility(View.INVISIBLE);
			}
		}
		else if( buttonView == createRadioOption )
		{
			if( isChecked )
			{
			articleRadioOption.setVisibility(View.VISIBLE);
			articleRadioOption.setChecked(true);
			widgetRadioOption.setVisibility(View.VISIBLE);
			externalRadioOption.setVisibility(View.VISIBLE);
		
			moduleIDEditText.setVisibility(View.VISIBLE);
			moduleIDTextView.setVisibility(View.VISIBLE);
		
			menuURLTextView.setVisibility(View.INVISIBLE);
			menuURLEditText.setVisibility(View.INVISIBLE);
		
			spinner.setVisibility(View.VISIBLE);
			}
			else
			{
				articleRadioOption.setVisibility(View.INVISIBLE);
				widgetRadioOption.setVisibility(View.INVISIBLE);
				externalRadioOption.setVisibility(View.INVISIBLE);
			
				moduleIDEditText.setVisibility(View.INVISIBLE);
				moduleIDTextView.setVisibility(View.INVISIBLE);
			}
		}
		else if( buttonView == selectRadioOption )
		{
			if( isChecked )
			{
					articleRadioOption.setVisibility(View.INVISIBLE);
					widgetRadioOption.setVisibility(View.INVISIBLE);
					externalRadioOption.setVisibility(View.INVISIBLE);
				
					moduleIDEditText.setVisibility(View.INVISIBLE);
					moduleIDTextView.setVisibility(View.INVISIBLE);
				
					menuURLTextView.setVisibility(View.INVISIBLE);
					menuURLEditText.setVisibility(View.INVISIBLE);
				
					spinner.setVisibility(View.VISIBLE);
			}
			else
				{
				spinner.setVisibility(View.INVISIBLE);
				}
		}
	}
	
	//AsyncTask that is executed if the "Create" button is checked
	private class CreateMenuAsyncTask extends AsyncTask<Object, Object, Object>
	{
		@Override
		protected Object doInBackground(Object... param) 
		{
			HashMap<String, String> params = new HashMap<String, String>();
			
			params.put("ruleset", "insertMenuItem");
			params.put("module","mobile_communication");
			params.put("act","procmobile_communicationMenuItem");
			params.put("menu_item_srl",menuItemSRL);
			params.put("menu_srl", parentSRL);
			params.put("menu_name_key",browserTitleEditText.getText().toString());
			params.put("menu_name",browserTitleEditText.getText().toString());
			params.put("cType","CREATE");
			
			params.put("module_type", "ARTICLE");
			params.put("menu_open_window", openInNewWindow());
			params.put("create_menu_url",moduleIDEditText.getText().toString());
			
			XEHost.getINSTANCE().postMultipart(params, "/");
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			finish();
		}
	}
	
	//AsyncTask that is executed if the "Select" button is checked
	private class SelectModuleAsyncTask extends AsyncTask<Object, Object, Object>
	{
		@Override
		protected Object doInBackground(Object... param) 
		{
			HashMap<String, String> params = new HashMap<String, String>();
			
			params.put("ruleset", "insertMenuItem");
			params.put("module","mobile_communication");
			params.put("act","procmobile_communicationMenuItem");
			params.put("menu_srl", parentSRL);
			params.put("menu_item_srl", menuItemSRL);
			params.put("menu_name_key",browserTitleEditText.getText().toString());
			params.put("menu_name",browserTitleEditText.getText().toString());
			params.put("cType","SELECT");
			params.put("module_type", returnType());
			params.put("menu_open_window", openInNewWindow());
			params.put("select_menu_url",(String) spinner.getSelectedItem());
			
			XEHost.getINSTANCE().postMultipart(params, "/");
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			finish();
		}
	}

	//Async Task that is executed if the "Menu URL" button is checked
	private class MenuURLAsyncTask extends AsyncTask<Object, Object, Object>
	{
		@Override
		protected Object doInBackground(Object... param) 
		{
			HashMap<String, String> params = new HashMap<String, String>();
			
			params.put("ruleset", "insertMenuItem");
			params.put("module","mobile_communication");
			params.put("act","procmobile_communicationMenuItem");
			params.put("menu_srl", parentSRL);
			params.put("menu_item_srl", menuItemSRL);
			params.put("menu_name_key",browserTitleEditText.getText().toString());
			params.put("menu_name",browserTitleEditText.getText().toString());
			params.put("cType","URL");
			params.put("module_type", "ARTICLE");
			params.put("menu_open_window", openInNewWindow());
			params.put("menu_url",menuURLEditText.getText().toString());
			
			XEHost.getINSTANCE().postMultipart(params, "/");
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			finish();
		}
	}
	
	//Async Task that gets details about the current edited menu
	private class GetEditedMenuAsyncTask extends AsyncTask<Object, Object, Object>
	{

		@Override
		protected Object doInBackground(Object... params) 
		{
			String xmlData = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><methodCall><params><menu_item_srl><![CDATA[" + menuItemSRL + "]]></menu_item_srl><module><![CDATA[menu]]></module><act><![CDATA[getMenuAdminItemInfo]]></act></params></methodCall>";
			
			String response = XEHost.getINSTANCE().postRequest("/index.php", xmlData);
			
			 Serializer serializer = new Persister();        
	       
	         Reader reader = new StringReader(response);
	       
	         try {
	      	  details = serializer.read(XEMenuItemsDetails.class, reader, false);
	      	 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			
			browserTitleEditText.setText(details.name);
			if( details.open_window.equals("Y") ) newWindow.setChecked(true);
							else newWindow.setChecked(false);
			
			//moduleType may be null somehow			
			if(details.moduleType==null){
				createRadioOption.setChecked(true);
			}else{
				if( details.moduleType.equals("page") )
				{
					//select the correct page in spinner
					int i;
					for(i=0;i<modules.modules.size();i++)
					{	
						if( details.url.equals(modules.modules.get(i).module) ) break;
					}
					Log.d("i=", i+ " ");
					spinner.setSelection(i);
					
				}
				else if( details.moduleType.equals("url") )
				{
					Log.d("ajunge aici", "dada");
					menuURLRadioOption.setChecked(true);
					
					menuURLEditText.setText(details.url);
				}
			}
			
		}
		
	}
	
	//Async Task to get a list of modules for the spinner adapter
	private class GetModulesAsyncTask extends AsyncTask<Object, Object, Object>
	{

		String xmlData;
		
		@Override
		protected Object doInBackground(Object... params) 
		{
			 xmlData = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationListModules");
			
			 Serializer serializer = new Persister();        
	        
	          Reader reader = new StringReader(xmlData);
	          try {
	       	  modules = serializer.read(XEArrayList.class, reader, false); 

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
		
			isLoggedIn(xmlData, XEMobileMenuItemEditController.this);
			
			if( modules != null && modules.modules != null )
			{
				for(int i=0;i<modules.modules.size();i++)
				{
				adapter.add(modules.modules.get(i).module);
				}
			
				spinner.setAdapter(adapter);
				
				GetEditedMenuAsyncTask editedMenuTask = new GetEditedMenuAsyncTask();
				editedMenuTask.execute();
			}
			
			
		}
	}
	
}
