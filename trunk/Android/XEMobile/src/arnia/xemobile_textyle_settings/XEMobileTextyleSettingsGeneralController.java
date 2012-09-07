package arnia.xemobile_textyle_settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.classes.XETextyleSettings;

//Activity that contains the general settings of a Textyle
public class XEMobileTextyleSettingsGeneralController extends XEActivity
{
	//UI references
	private Spinner languageSpinner;
	private Spinner timezoneSpinner;
	private EditText blogTitleEditText;
	private Button saveButton;
	private Button changePasswordButton;
	
	private XETextyle textyle;
	private XETextyleSettings settings;
	
	//adapters for language's spinner and timezone's spinner
	private ArrayAdapter<String> languagesAdapter;
	private ArrayAdapter<String> timezonesAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobiletextylegeneralsettingslayout);
		
		//get references to UI elements
		languageSpinner = (Spinner) findViewById(R.id.XEMOBILE_TEXTYLE_GENERALSETTINGS_LANGUAGESPINNER);
		timezoneSpinner = (Spinner) findViewById(R.id.XEMOBILE_TEXTYLE_GENERALSETTINGS_TIMEZONESPINNER);
		blogTitleEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTYLE_GENERALSETTINGS_BLOGTITLE);
		saveButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_GENERALSETTINGS_SAVE);
		
		// when the save button is pressed, the settings are saved by calling the onClick method
		saveButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				startProgress("Saving...");
				SaveSettingsAsyncTask task = new SaveSettingsAsyncTask();
				task.execute();
			}
		});
		
		
		changePasswordButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_GENERALSETTINGS_CHANGEPASSWORD);
		changePasswordButton.setOnClickListener(new OnClickListener() 
		{
			//called when the changePasswordButton is pressed
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(XEMobileTextyleSettingsGeneralController.this,XEMobileTextyleSettingsChangePasswordController.class);
				intent.putExtra("textyle", textyle);
				startActivity(intent);
			}
		});
		
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		
		blogTitleEditText.setText(textyle.textyle_title);
		
		settings = new XETextyleSettings();
		
		//create adapters for spinners
		languagesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		timezonesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		
		//set spinners
		languageSpinner.setAdapter(languagesAdapter);
		timezoneSpinner.setAdapter(timezonesAdapter);
		
		//load data in spinners
		loadLanguagesInSpinner();
		loadTimezonesInSpinner();
		
	}
	
	public void loadLanguagesInSpinner()
	{
		ArrayList<String> array = new ArrayList<String>();
		for(Map.Entry<String, String> entry : settings.getLanguages().entrySet())
		{
			array.add(entry.getValue());
		}
		Collections.sort(array);
		
		for(int i=0;i<array.size();i++)
		{
			languagesAdapter.add(array.get(i));
		}
		for(int i=0;i<array.size();i++)
		{
			if( settings.getKeyWithLanguage(array.get(i)).equals(textyle.default_lang) )
				languageSpinner.setSelection(i);
		}
	}
	
	public void loadTimezonesInSpinner()
	{
		ArrayList<String> array = new ArrayList<String>();
		for(Map.Entry<String, String> entry : settings.getZones().entrySet())
		{
			array.add(entry.getValue());
		}
		Collections.sort(array);
		
		for(int i=0;i<array.size();i++)
		{
			timezonesAdapter.add(array.get(i));
		}
		timezonesAdapter.notifyDataSetChanged();
		for(int i=0;i<array.size();i++)
		{
			if( settings.getKeyWithZone(array.get(i)).equals(textyle.timezone) )
				timezoneSpinner.setSelection(i);
		}
	}
	
	//Async Task that save the settings
	private class SaveSettingsAsyncTask extends AsyncTask<Object, Object, Object>
	{

		@Override
		protected Object doInBackground(Object... param) 
		{
			HashMap<String, String> params = new HashMap<String, String>();
			
			params.put("act", "procTextyleInfoUpdate");
			params.put("module", "textyle");
			params.put("mid", "textyle");
			params.put("vid", textyle.domain);
			params.put("textyle_title", blogTitleEditText.getText().toString());
			params.put("language", settings.getKeyWithLanguage((String)languageSpinner.getSelectedItem()));
			params.put("timezone", settings.getKeyWithZone((String) timezoneSpinner.getSelectedItem()));
			params.put("delete_icon", "");
			params.put("textyle_content", "");
			params.put("error_return_url", "/index.php?mid=textyle&act=dispTextyleToolConfigInfo&vid=blog");
			
			//send request
			XEHost.getINSTANCE().postMultipart(params, "/");
			
			return null;
		}
		
		//when the response came, activity is finished
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			dismissProgress();
			finish();
		}
	}
}
