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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.classes.XETextyleSettings;

public class XEMobileTextyleSettingsWritingController extends XEActivity implements OnCheckedChangeListener
{
	private XETextyleSettings textyleSettings;
	
	//UI references
	private Button saveButton;
	private RadioButton paragraphEditorOption;
	private RadioButton xpressEditorOption;
	private EditText fontFamilyEditText;
	private EditText fontSizeEditText;
	private CheckBox prefixCheckBox;
	private EditText prefixEditText;
	private CheckBox suffixCheckBox;
	private EditText suffixEditText;
	
	private XETextyle textyle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.xemobiletextylewritingsettingslayout);
		
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		
		saveButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_WRITINGSETTINGS_SAVEBUTTON);
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				startProgress("Saving...");
				SaveWritingSettingsAsyncTask task = new SaveWritingSettingsAsyncTask();
				task.execute();
			}
		});
		
		paragraphEditorOption = (RadioButton) findViewById(R.id.XEMOBILE_TEXTYLE_WRITINGSETTINGS_PARAGRAPHOPTION);
		paragraphEditorOption.setOnCheckedChangeListener(this);
		
		xpressEditorOption = (RadioButton) findViewById(R.id.XEMOBILE_TEXTYLE_WRITINGSETTINGS_XPRESSEDITOROPTION);
		xpressEditorOption.setOnCheckedChangeListener(this);
		
		fontFamilyEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTYLE_WRITINGSETTINGS_FONTFAMILY);
		fontSizeEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTYLE_WRITINGSETTINGS_FONTSIZE);
		prefixCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_TEXTYLE_WRITINGSETTINGS_PREFIX);
		prefixCheckBox.setOnCheckedChangeListener(this);
		
		prefixEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTYLE_WRITINGSETTINGS_PREFIXEDITTEXT);
		suffixCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_TEXTYLE_WRITINGSETTINGS_SUFFIX);
		suffixCheckBox.setOnCheckedChangeListener(this);
		
		suffixEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTYLE_WRITINGSETTINGS_SUFFIXEDITTEXT);
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	
		startProgress("Loading...");
		GetWritingSettingsAsyncTask task = new GetWritingSettingsAsyncTask();
		task.execute();
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if( buttonView == suffixCheckBox )
		{
			if( isChecked )
			{
				textyleSettings.useSuffix = "Y";
			}
			else 
			{
				textyleSettings.useSuffix = "N";
			}
		}
		if( buttonView == prefixCheckBox )
		{
			if( isChecked )
			{
				textyleSettings.usePrefix = "Y";
			}
			else textyleSettings.usePrefix = "N";
		}
		if( buttonView == xpressEditorOption )
		{
			if( isChecked )
			{
				textyleSettings.editor = "xpresseditor";
			}
		}
		if( buttonView == paragraphEditorOption  )
		{
			if( isChecked )
			{
				textyleSettings.editor = "dreditor";
			}
		}
	}
	
	private class GetWritingSettingsAsyncTask extends AsyncTask<Object, Object, Object>
	{

		@Override
		protected Object doInBackground(Object... params) 
		{
			String response = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationToolConfig&module_srl=" + textyle.module_srl);
			
			Serializer serializer = new Persister();
			
			Reader reader = new StringReader(response);
			
			try
			{
				textyleSettings = serializer.read(XETextyleSettings.class,reader, false);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
	
			dismissProgress();
			if( textyleSettings.editor.equals("xpresseditor") ) xpressEditorOption.setChecked(true);
			else if( textyleSettings.editor.equals("dreditor") ) paragraphEditorOption.setChecked(true);
			
			fontFamilyEditText.setText( textyleSettings.getFontFamily() );
			fontSizeEditText.setText( textyleSettings.getFontSize() );
			
			if( textyleSettings.usePrefix.equals("Y") ) prefixCheckBox.setChecked(true);
			else prefixCheckBox.setChecked(false);
			
			if( textyleSettings.useSuffix.equals("Y") ) suffixCheckBox.setChecked(true);
			else suffixCheckBox.setChecked(false);
			
			suffixEditText.setText( textyleSettings.getSuffix() );
			prefixEditText.setText( textyleSettings.getPrefix() );
		}
		
	}
	
	private class SaveWritingSettingsAsyncTask extends AsyncTask<Object, Object, Object>
	{

		@Override
		protected Object doInBackground(Object... params)
		{
			String saveXMLForXPR = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
					"<methodCall>\n<params>\n" +
					"<_filter><![CDATA[insert_config_postwrite]]></_filter>\n" +
					"<act><![CDATA[procTextyleConfigPostwriteInsert]]></act>\n" +
					"<vid><![CDATA["+ textyle.domain +"]]></vid>\n" +
					"<mid><![CDATA[textyle]]></mid>\n" +
					"<etc_post_editor_skin><![CDATA[xpresseditor]]></etc_post_editor_skin>\n" +
					"<font_family><![CDATA[" + fontFamilyEditText.getText().toString() + "]]></font_family>\n" +
					"<font_size><![CDATA["+ fontSizeEditText.getText().toString() +"]]></font_size>\n" +
					"<post_prefix><![CDATA["+ prefixEditText.getText().toString() +"]]></post_prefix>\n" +
					"<post_use_suffix><![CDATA["+ textyleSettings.useSuffix +"]]></post_use_suffix>\n" +
					"<post_use_prefix><![CDATA["+ textyleSettings.usePrefix + "]]></post_use_prefix>\n" +
					"<post_suffix><![CDATA["+ suffixEditText.getText().toString() +"]]></post_suffix>\n" +
					"<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>";
			
			String saveXMLForParagraph = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
					"<methodCall>\n<params>\n" +
					"<_filter><![CDATA[insert_config_postwrite]]></_filter>\n" +
					"<act><![CDATA[procTextyleConfigPostwriteInsert]]></act>\n" +
					"<vid><![CDATA["+ textyle.domain +"]]></vid>\n" +
					"<mid><![CDATA[textyle]]></mid>\n" +
					"<post_editor_skin><![CDATA[dreditor]]></post_editor_skin>\n" +
					"<etc_post_editor_skin><![CDATA[xpresseditor]]></etc_post_editor_skin>\n" +
					"<font_family><![CDATA[" + fontFamilyEditText.getText().toString() + "]]></font_family>\n" +
					"<font_size><![CDATA["+ fontSizeEditText.getText().toString() +"]]></font_size>\n" +
					"<post_prefix><![CDATA["+ prefixEditText.getText().toString() +"]]></post_prefix>\n" +
					"<post_use_suffix><![CDATA["+ textyleSettings.useSuffix +"]]></post_use_suffix>\n" +
					"<post_use_prefix><![CDATA["+ textyleSettings.usePrefix + "]]></post_use_prefix>\n" +
					"<post_suffix><![CDATA["+ suffixEditText.getText().toString() +"]]></post_suffix>\n" +
					"<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>";
			
			if( textyleSettings.editor.equals("xpresseditor") )
					XEHost.getINSTANCE().postRequest("/index.php",saveXMLForXPR);
			else XEHost.getINSTANCE().postRequest("/index.php",saveXMLForParagraph);
			
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
