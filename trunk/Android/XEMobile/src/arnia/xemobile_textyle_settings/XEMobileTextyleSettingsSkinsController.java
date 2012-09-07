package arnia.xemobile_textyle_settings;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XESkin;
import arnia.xemobile.classes.XETextyle;

public class XEMobileTextyleSettingsSkinsController extends XEActivity implements OnCheckedChangeListener, OnClickListener
{
	private XETextyle textyle;
	private XEArrayList array;
	private XEMobileTextyleSettingsSkinsAdapter adapter;
	private ListView listView;
	private Button saveButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobiletextyleskinssettingslayout);
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		listView = (ListView) findViewById(R.id.XEMOBILE_TEXTYLE_SKINSSETTINGS_LISTVIEW);
		saveButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_SKINSSETTINGS_SAVEBUTTON);
		saveButton.setOnClickListener(this);
		
		adapter = new XEMobileTextyleSettingsSkinsAdapter(this);
		listView.setAdapter(adapter);
			
		startProgress("Loading...");
		GetSkinsAsyncTask task = new GetSkinsAsyncTask();
		task.execute();
	}

	//method called when one CheckBox is clicked
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		if( isChecked )
		{
			XESkin skin = array.skins.get( (Integer) buttonView.getTag() );
			adapter.setSelectedSkin(skin.id);
			adapter.notifyDataSetChanged();
			textyle.skin = skin.id;
		}
	}
	
	//method called when the button is pressed
	@Override
	public void onClick(View v) 
	{
		startProgress("Saving...");
		SaveSkinAsyncTask task = new SaveSkinAsyncTask();
		task.execute(textyle.skin);
	}
	
	//Async task for getting the skins
	private class GetSkinsAsyncTask extends AsyncTask<Object, Object, Object>
	{
		String response;
		
		@Override
		protected Object doInBackground(Object... params) 
		{
			response = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationGetSkins");
			
			Serializer serializer = new Persister();
			
			Reader reader = new StringReader(response);
			try
			{
				array = serializer.read(XEArrayList.class,reader , false);
			}catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return null;
		}
		
		//method called when the response came
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
		
			//check if the user is logged in
			if( isLoggedIn(response, XEMobileTextyleSettingsSkinsController.this) )
			{
				dismissProgress();
				adapter.setSelectedSkin(textyle.skin);
				adapter.setSkins(array.skins);
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	//Async Task for saving the current skin selection
	private class SaveSkinAsyncTask extends AsyncTask<String,Object,Object>
	{

		@Override
		protected Object doInBackground(String ... params)
		{
			String xmlForSkinSave = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
					"<methodCall>\n<params>\n" +
					"<skin><![CDATA["+ params[0] +"]]></skin>\n" +
					"<mid><![CDATA[textyle]]></mid>\n" +
					"<module><![CDATA[textyle]]></module>\n" +
					"<act><![CDATA[procTextyleToolLayoutConfigSkin]]></act>\n" +
					"<vid><![CDATA["+ textyle.domain +"]]></vid>\n</params>\n" +
					"</methodCall>";
			
			XEHost.getINSTANCE().postRequest("/index.php", xmlForSkinSave);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			dismissProgress();
			finish();
		}
	}

	
}
