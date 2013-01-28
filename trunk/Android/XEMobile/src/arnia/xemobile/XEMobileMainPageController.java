package arnia.xemobile;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEMember;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.global_settings.XEMobileGlobalSettingsController;
import arnia.xemobile.menu_management.XEMobileMenuController;
import arnia.xemobile.page_management.XEMobilePageController;
import arnia.xemobile.theme_management.XEMobileThemeController;
import arnia.xemobile_textyle.XEMobileTextyleSelectTextyleController;

//Activity for the Main Menu of XEMobile application
public class XEMobileMainPageController extends Activity
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobilemainpagelayout);
		
		//Members button
		Button membersButton = (Button) findViewById(R.id.XEMOBILE_MAINPAGE_MEMBERS);
		membersButton.setOnClickListener(new OnClickListener() 
		{
			//called when the Members button is pressed
			@Override
			public void onClick(View v) 
			{	
					Intent intent = new Intent(XEMobileMainPageController.this,XEMobileMembersController.class);
					startActivity(intent);
			}
		});
		
		//Page button
		Button pageButton = (Button) findViewById(R.id.XEMOBILE_MAINPAGE_PAGE);
		pageButton.setOnClickListener(new OnClickListener() 
		{
			//called when the Page button is pressed
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(XEMobileMainPageController.this,XEMobilePageController.class);
				startActivity(intent);	
			}
		});
		
		//Menu button
		Button menuButton = (Button) findViewById(R.id.XEMOBILE_MAINPAGE_MENU);
		menuButton.setOnClickListener(new OnClickListener() 
		{
			//called when the Menu button is pressed
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(XEMobileMainPageController.this, XEMobileMenuController.class);
				startActivity(intent);
			}
		});
		
		//Stats button
		Button statsButton = (Button) findViewById(R.id.XEMOBILE_MAINPAGE_STATS);
		statsButton.setOnClickListener(new OnClickListener() 
		{
			//called when the Statistics button is pressed
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(XEMobileMainPageController.this,XEMobileStatisticsController.class);
				startActivity(intent);
			}
		});
		
		//Settings button
		Button settingsButton = (Button) findViewById(R.id.XEMOBILE_MAINPAGE_SETTINGS);
		settingsButton.setOnClickListener(new OnClickListener() 
		{
			//called when the Settings button is pressed
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(XEMobileMainPageController.this, XEMobileGlobalSettingsController.class);
				startActivity(intent);
			}
		});
		
		//textyle button
		Button textyleButton = (Button) findViewById(R.id.XEMOBILE_MAINPAGE_TEXTYLE);
		textyleButton.setOnClickListener(new OnClickListener() 
		{
			//called when the Textyle button is pressed
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent (XEMobileMainPageController.this, XEMobileTextyleSelectTextyleController.class);
				startActivity(intent);
			}
		});
		
		//theme button
				Button themeButton = (Button) findViewById(R.id.XEMOBILE_MAINPAGE_THEMES);
				themeButton.setOnClickListener(new OnClickListener() 
				{
					//called when the Themes button is pressed
					@Override
					public void onClick(View v) {
						
						Intent intent = new Intent (XEMobileMainPageController.this, XEMobileThemeController.class);
						startActivity(intent);
					}
				});
	}
	
}
