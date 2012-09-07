package arnia.xemobile.global_settings;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.james.mime4j.io.LimitedInputStream;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.XEMobileLoginController;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEGlobalSettings;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XESettings;

public class XEMobileGlobalSettingsController extends XEActivity implements OnClickListener
{
	//UI references
	private Button selectedLanguagesButton;
	private Spinner defaultLanguagesSpinner;
	private Spinner localTimeSpinner;
	private EditText adminAccesIPEditText;
	private EditText defaultURLEditText;
	private RadioButton sslNeverOptionRadioButton;
	private  RadioButton sslOptionalOptionRadioButton;
	private RadioButton sslAlwaysOptionRadioButton;
	private CheckBox mobileTemplateCheckBox;
	private  CheckBox rewriteModeCheckBox;
	private CheckBox enableSSOCheckBox;
	private CheckBox sessionDBCheckBox;
	private CheckBox qmailCheckBox;
	private CheckBox htmlDTDCheckBox;
	private Button saveButton;
	
	private XEGlobalSettings settings;
	
	protected String[] languages = { "English", "한국어", "日本語", "中文(中国)", "中文(臺灣)", "Francais",
			"Deutsch","Русский","Español","Türkçe","Tiếng Việt","Mongolian"};
	protected ArrayList<String> selectedLanguages = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobileglobalsettingslayout);
		
		//take reference to UI elements
		selectedLanguagesButton = (Button) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_SPINNER_SELECTEDLANGS);
		selectedLanguagesButton.setOnClickListener(this);
		selectedLanguagesButton = (Button) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_SPINNER_SELECTEDLANGS);
		defaultLanguagesSpinner = (Spinner) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_SPINNER_DEFAULTLANG);
		localTimeSpinner = (Spinner) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_SPINNER_LOCAL);
		adminAccesIPEditText = (EditText) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_EDITTEXT_LIMITIP);
		defaultURLEditText = (EditText) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_EDITTEXT_DEFAULTURL);
		sslNeverOptionRadioButton = (RadioButton) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_RADIO_SSL_NEVER);
		sslOptionalOptionRadioButton = (RadioButton) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_RADIO_SSL_OPTIONAL);
		sslAlwaysOptionRadioButton = (RadioButton) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_RADIO_SSL_ALWAYS);
		mobileTemplateCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_CHECKBOX_MOBILETEMPL);
		rewriteModeCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_CHECKBOX_REWRITEMODE);
		enableSSOCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_CHECKBOX_ENABLESSO);
		sessionDBCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_CHECKBOX_SESSIONDB);
		qmailCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_CHECKBOX_QMAIL);
		htmlDTDCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_CHECKBOX_HTMLDTD);
		saveButton = (Button) findViewById(R.id.XEMOBILE_GLOBALSETTINGS_SAVEBUTTON);
		saveButton.setOnClickListener(this);
		
		startProgress("Loading settings");
				
		//start the request to get the current setting configuration
		GetSettingsAsyncTask task = new GetSettingsAsyncTask();
		task.execute();
	}

	//method called when one of the buttons is pressed: save button or selected languages button
	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			// save the new setting configuration request
			case R.id.XEMOBILE_GLOBALSETTINGS_SAVEBUTTON:
				startProgress("Saving...");
				SaveSettingsAsyncTask task = new SaveSettingsAsyncTask();
				task.execute();
			break;
			
			//the selected languages button is pressed
			case R.id.XEMOBILE_GLOBALSETTINGS_SPINNER_SELECTEDLANGS:
			showSelectLanguagesDialog();
			break;
		}
	}
	
	protected void showSelectLanguagesDialog() 
	{
		boolean[] checkedLanguage = new boolean[languages.length];
		int count = languages.length;

		for(int i = 0; i < count; i++)
			checkedLanguage[i] = selectedLanguages.contains(languages[i]);

		DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if(isChecked)
					selectedLanguages.add(languages[which]);
				else
					selectedLanguages.remove(languages[which]);
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Colours");
		builder.setMultiChoiceItems(languages, checkedLanguage, coloursDialogListener);

		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	//Async task that gets the current settings
	private class GetSettingsAsyncTask extends AsyncTask<Object, Object, Object>
	{
		String xmlResponse;	
		@Override
		protected Object doInBackground(Object... params) 
		{
			//make request
			xmlResponse = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationLoadSettings");
			
			//parse response
			Serializer serializer = new Persister();
	        Reader reader = new StringReader(xmlResponse);
	        try {
	        	settings = 
					    serializer.read(XEGlobalSettings.class, reader, false);
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
			
			//check if user is logged
			isLoggedIn(xmlResponse, XEMobileGlobalSettingsController.this);
			
			dismissProgress();
			if( settings != null )
			{
				setSelectedLanguages();
				//load the current settings
				setDefaultLanguageOption();
				setTimezoneOption();
				adminAccesIPEditText.setText(settings.ips);
				defaultURLEditText.setText(settings.default_url);
				setSLLOption();
				setMobileTemplateOption();
				setSSOOption();
				setRewriteModeOption();
				setSessionDBOption();
				setQmailOption();
				setHtmlDTDOption();
			}
			else 
			{
				Intent intent = new Intent(XEMobileGlobalSettingsController.this,XEMobileLoginController.class);
				startActivity(intent);
			}
		}
	}
	
	public void setSelectedLanguages()
	{
		// set the selected languages in spinner with multiple choices
		ArrayList<String> selectedLangsValues = new ArrayList<String>();
		ArrayList<String> selectedLangsKeys = settings.getSelectedLanguages();
		
		for(int i = 0;i<selectedLangsKeys.size();i++)
		{
			selectedLangsValues.add( settings.getLanguageWithKey(selectedLangsKeys.get(i)) );
		}
		selectedLanguages = selectedLangsValues;
	}
	
	public void setSLLOption()
	{
		if( settings.use_ssl.equals("none") )
		{
			sslNeverOptionRadioButton.setChecked(true);
		}
		else if( settings.use_ssl.equals("optional") )
			{
				sslOptionalOptionRadioButton.setChecked(true);
			}
		else if(settings.use_ssl.equals("always"))
			{
				sslAlwaysOptionRadioButton.setChecked(true);
			}
	}
	public String getSSLOption()
	{
		if( sslNeverOptionRadioButton.isChecked() ) return "none";
		else if( sslOptionalOptionRadioButton.isChecked() ) return "optional";
		else if( sslAlwaysOptionRadioButton.isChecked() ) return "always";
		
		return "";
	}
	
	public void setMobileTemplateOption()
	{
		if( settings.mobile.equals("Y") ) mobileTemplateCheckBox.setChecked(true);
		else mobileTemplateCheckBox.setChecked(false);
	}
	
	public String getMobileTemplateOption()
	{
		if( mobileTemplateCheckBox.isChecked() ) return "Y";
		else return "N";
	}
	
	public void setRewriteModeOption()
	{
		if( settings.rewrite_mode.equals("Y") ) rewriteModeCheckBox.setChecked(true);
		else rewriteModeCheckBox.setChecked(false);
	}
	
	public String getRewriteModeOption()
	{
		if( rewriteModeCheckBox.isChecked() ) return "Y";
		else return "N";
	}
	
	public void setSSOOption()
	{
		if( settings.use_sso.equals("Y") ) enableSSOCheckBox.setChecked(true);
		else enableSSOCheckBox.setChecked(false);
	}
	
	public String getSSOOption()
	{
		if( enableSSOCheckBox.isChecked() ) return "Y";
		else return "N";
	}
	
	public void setSessionDBOption()
	{
		if( settings.db_session.equals("Y") ) sessionDBCheckBox.setChecked(true);
		else sessionDBCheckBox.setChecked(false);
	}
	
	public String getSessionDBOption()
	{
		if( sessionDBCheckBox.isChecked() ) return "Y";
		else return "N";
	}
	
	public void setQmailOption()
	{
		if( settings.qmail.equals("Y") ) qmailCheckBox.setChecked(true);
		else qmailCheckBox.setChecked(false);
	}
	
	public String getQmailOption()
	{
		if( qmailCheckBox.isChecked() ) return "Y";
		else return "N";
	}
	
	public void setHtmlDTDOption()
	{
		if( settings.html5.equals("Y") ) htmlDTDCheckBox.setChecked(true);
		else htmlDTDCheckBox.setChecked(false);
	}
	
	public String getHtmlDTDOption()
	{
		if( htmlDTDCheckBox.isChecked() ) return "Y";
		else return "N";
	}
	
	public void setDefaultLanguageOption()
	{
		ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		
		ArrayList<String> languages = new ArrayList<String>();
		for(Map.Entry<String, String> entry : settings.getLanguages().entrySet())
		{
			languages.add(entry.getValue());
			languageAdapter.add(entry.getValue());
		}
		defaultLanguagesSpinner.setAdapter(languageAdapter);
		
		String defaultLang = settings.default_lang;
		
		for(int i = 0 ;i< languages.size();i++) 
			if( settings.getLanguageWithKey(defaultLang).equals(languages.get(i)) ) 
			{
			defaultLanguagesSpinner.setSelection(i);
			break;
			}
	}
	
	public String getDefaultLanguageOption()
	{
		String selected = (String) defaultLanguagesSpinner.getSelectedItem();
		Log.d("LANG", selected);
		return settings.getKeyWithLanguage(selected);
	}
	
	public void setTimezoneOption()
	{
		ArrayAdapter<String> localAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
		
		ArrayList<String> localTimezones = new ArrayList<String>();
		for(Map.Entry<String, String> entry : settings.getZones().entrySet())
		{
			localTimezones.add(entry.getValue());
		}
		
		Collections.sort(localTimezones);
		for(int i=0;i<localTimezones.size();i++) localAdapter.add(localTimezones.get(i));
		
		localTimeSpinner.setAdapter(localAdapter);
		
		String timezone = settings.timezone;
		
		for(int i = 0 ;i< localTimezones.size();i++) 
			if( settings.getZoneWithKey(timezone).equals(localTimezones.get(i)) ) 
			{
				localTimeSpinner.setSelection(i);
			break;
			}
	}
	
	public String getTimezoneOption()
	{
		String selected = (String) localTimeSpinner.getSelectedItem();
		
		return settings.getKeyWithZone(selected);
	}
	
	public String getAdminIPList()
	{
		String list = adminAccesIPEditText.getText().toString();
		
		list = list.replace(" ", "");
		list = list.replace(",", "\n");
		return list;
	}
	
	//Async Task for saving the settings
	private class SaveSettingsAsyncTask extends AsyncTask<String, String, String>
	{
		
		@Override
		protected String doInBackground(String... param) 
		{
			//build the request
			
			HashMap params = new HashMap();
			
			params.put("module", "install");
			params.put("act", "procInstallAdminConfig");
			params.put("admin_ip_list", getAdminIPList());
			
			ArrayList<String> selectedLangsKeys = new ArrayList<String>();
			for(int i = 0 ;i< selectedLanguages.size();i++)
			{
				selectedLangsKeys.add(settings.getKeyWithLanguage(selectedLanguages.get(i)));
				
			}
			params.put("selected_lang[]", selectedLangsKeys);
			Log.d("LANG", "TEST");
			Log.d("LANG", getDefaultLanguageOption() + " ");
			
			params.put("change_lang_type",getDefaultLanguageOption());
			params.put("time_zone", getTimezoneOption());
			params.put("use_mobile_view", getMobileTemplateOption());
			params.put("default_url",defaultURLEditText.getText().toString());
			params.put("use_ssl",getSSLOption());
			params.put("use_rewrite", getRewriteModeOption());
			params.put("use_sso", getSSOOption());
			params.put("use_db_session", getSessionDBOption());
			params.put("qmail_compatibility", getQmailOption());
			params.put("use_html5", getHtmlDTDOption());
			
			//send the request
			XEHost.getINSTANCE().postMultipart(params, "/index.php?module=admin&act=dispAdminConfigGeneral");
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			dismissProgress();
			finish();
		}
		
	}
	

}
