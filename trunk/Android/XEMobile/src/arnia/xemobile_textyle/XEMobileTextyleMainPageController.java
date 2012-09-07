package arnia.xemobile_textyle;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.classes.XETextyleStats;
import arnia.xemobile_textyle_comments.XEMobileTextyleCommentsController;
import arnia.xemobile_textyle_pages.XEMobileTextylePagesController;
import arnia.xemobile_textyle_posts.XEMobileTextylePostsController;
import arnia.xemobile_textyle_settings.XEMobileTextyleSettingsTabController;

// Main Page for Textyle
public class XEMobileTextyleMainPageController extends XEActivity 
{
	
	private XETextyle textyle;
	private XETextyleStats textyleStats;
	
	
	private ListView statsListView;
	//adapter for statsListView
	private XEMobileTextyleStatsAdapter adapterStats;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobiletextylemainpagelayout);
		
		// creating the adapter for statsListView
		adapterStats = new XEMobileTextyleStatsAdapter(this);
		
		//listView with textyle's statistics
		statsListView = (ListView) findViewById(R.id.XEMOBILE_TEXTYLE_MAIN_PGE_STATSLISTVIEW);
		statsListView.setAdapter(adapterStats);
		
		//get the textyle object that is passed between activities
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		
		
		Button postsButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_MAINPAGE_POSTS);
		postsButton.setOnClickListener(new OnClickListener() 
		{
			//called when the Posts button is pressed
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(XEMobileTextyleMainPageController.this,XEMobileTextylePostsController.class);
				// passing the textyle object between activities
				intent.putExtra("textyle", textyle);
				startActivity(intent);
			}
		});
		
		Button pagesButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_MAINPAGE_PAGES);
		pagesButton.setOnClickListener(new OnClickListener() 
		{
			// called when the Pages button is pressed
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(XEMobileTextyleMainPageController.this,XEMobileTextylePagesController.class);
				// passing the textyle object between activities
				intent.putExtra("textyle", textyle);
				startActivity(intent);
			}
		});
		
		Button settingsButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_MAINPAGE_SETTINGS);
		settingsButton.setOnClickListener(new OnClickListener() 
		{
			//called when the Settings button is pressed
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent (XEMobileTextyleMainPageController.this,XEMobileTextyleSettingsTabController.class);
				// passing the textyle object between activities
				intent.putExtra("textyle", textyle);
				startActivity(intent);
			}
		});
		
		Button commentsButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_MAINPAGE_COMMENTS);
		commentsButton.setOnClickListener(new OnClickListener() 
		{	
			//called when the Comments button is pressed
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(XEMobileTextyleMainPageController.this,XEMobileTextyleCommentsController.class);
				// passing the textyle object between activities
				intent.putExtra("textyle", textyle);
				startActivity(intent);
			}
		});
		
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	
		//making the request for loading the textyle (if changes were made)
		GetTextyleAsyncTask task = new GetTextyleAsyncTask();
		task.execute(this);
	}
	
	
	//Async task to load the textyle's stats
	private class GetTextyleStatsAsyncTask extends AsyncTask<Context, Object, Context>
	{
		String response;
		
		@Override
		protected Context doInBackground(Context... params) 
		{
			//making the request
			response = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationTextyleStats&site_srl="+textyle.site_srl);
			
			//receiving the response
			Serializer serializer = new Persister();
			
			Reader reader = new StringReader(response);
			
			try
			{
				textyleStats = serializer.read(XETextyleStats.class, reader,false);
			}catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return params[0];
		}
		
		//method called when the response came
		@Override
		protected void onPostExecute(Context context) 
		{
			super.onPostExecute(context);
			
			isLoggedIn(response, XEMobileTextyleMainPageController.this);
			
			if( textyleStats != null )
			{
				adapterStats.setStats(textyleStats);
				adapterStats.notifyDataSetChanged();
			}
		}
	}
	
	//async task for loading the current textyle
	private class GetTextyleAsyncTask extends AsyncTask<Context, Object, Context>
	{
		XEArrayList array;
		
		@Override
		protected Context doInBackground(Context... params) 
		{
			//making the request
			String response = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationTextyleList");
			
			//parsing the response
			Serializer serializer = new Persister();
			
			Reader reader = new StringReader(response);
			
			try
			{
				array = serializer.read(XEArrayList.class, reader,false);
			}catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return params[0];
		}
		
		@Override
		protected void onPostExecute(Context context) 
		{
			super.onPostExecute(context);
		
			//
			if( array != null )
			{
				for(int i = 0; i<array.textyles.size();i++)
				{
					if( textyle.domain.equals(array.textyles.get(i).domain) )
						textyle = array.textyles.get(i);
				}
			}

			GetTextyleStatsAsyncTask task = new GetTextyleStatsAsyncTask();
			task.execute(context);
		}
	}
}
