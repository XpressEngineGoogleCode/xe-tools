package arnia.xemobile_textyle_settings;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import arnia.xemobile.R;
import arnia.xemobile.classes.XETextyle;

//TabActivity for Textyle Settings
public class XEMobileTextyleSettingsTabController extends TabActivity
{
	private XETextyle textyle;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobiletextylesettingstablayout);
		
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		
		TabHost tabHost = getTabHost();

		TabSpec generalSettings = tabHost.newTabSpec("General Settings");
		generalSettings.setIndicator("General");
		Intent generalSettingsIntent = new Intent(this, XEMobileTextyleSettingsGeneralController.class);
		generalSettingsIntent.putExtra("textyle", textyle);
		generalSettings.setContent(generalSettingsIntent);
		
		TabSpec writingSettings = tabHost.newTabSpec("Writing");
		writingSettings.setIndicator("Writing");
		Intent writingSettingsIntent = new Intent(this, XEMobileTextyleSettingsWritingController.class);
		writingSettingsIntent.putExtra("textyle", textyle);
		writingSettings.setContent(writingSettingsIntent);
		
		TabSpec skinsSettings = tabHost.newTabSpec("Skins");
		skinsSettings.setIndicator("Skins");
		Intent skinsSettingsIntent = new Intent(this, XEMobileTextyleSettingsSkinsController.class);
		skinsSettingsIntent.putExtra("textyle", textyle);
		skinsSettings.setContent(skinsSettingsIntent);
		
		tabHost.addTab(generalSettings);
		tabHost.addTab(writingSettings);
		tabHost.addTab(skinsSettings);
	}
}
