package arnia.xemobile.theme_management;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XETheme;
import arnia.xemobile.classes.XEThemeSkin;

public class XEMobileThemeController extends XEActivity implements OnClickListener, OnCheckedChangeListener{
	
	
	private ListView theme_listview;
	
	private ArrayList<XETheme> themes;
	private ArrayList<Bitmap> imgThumbnails;
	
	private XEMobileThemeAdapter themeAdapter;
	
	private Button saveButton;
	
	private int checkPosition=-1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.xemobilethemelayout);
		
		this.theme_listview = (ListView)findViewById(R.id.XEMOBILE_THEME_LISTVIEW);
		this.saveButton = (Button)findViewById(R.id.XEMOBILE_THEME_SAVEBUTTON);
		
		
		this.themes = new ArrayList<XETheme>();
		this.imgThumbnails = new ArrayList<Bitmap>();

		this.themeAdapter = new XEMobileThemeAdapter(XEMobileThemeController.this,this.themes);
		this.theme_listview.setAdapter(this.themeAdapter);
		
		this.saveButton.setOnClickListener(this);		
		
	}
	 
	@Override
	protected void onResume() {	
		super.onResume();
		startProgress("Loading");
		new GetXEThemes().execute();		
	}
	private class GetXEThemes extends AsyncTask<Void,Void,Void>{
		
		private String xmlData;		
		private XEArrayList lists;
		
		@Override
		protected Void doInBackground(Void... params) {
			//Request to XE mobile communication module to get theme information
			
			xmlData = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationGetThemes&XDEBUG_SESSION_START=netbeans-xdebug");
			
			 Serializer serializer = new Persister();        
	         
	           Reader reader = new StringReader(xmlData);
	           try {
	        	   lists = serializer.read(XEArrayList.class, reader, false);
	        	   themes = lists.themes;
	        	   for( XETheme theme : themes ){			
						 if(theme.thumbnail!=null){
							 Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(XEHost.getINSTANCE().getURL()+ theme.thumbnail).getContent()); 
							 imgThumbnails.add(bitmap);
						 }
					}
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			return null;			
		}
		@Override
		protected void onPostExecute(Void result) {			
			
			//Notify data adapter change			
			themeAdapter.setThemes(themes);
			themeAdapter.setThemesImage(imgThumbnails);
			themeAdapter.notifyDataSetChanged();
			dismissProgress();			
		}
		
	}
	@Override
	public void onClick(View v) {
		//Handle save click
		if(this.checkPosition!=-1){
			startProgress("Saving...");
			new SaveThemeAsyncTask().execute();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			View listItem;
			CheckBox checkItem;
			
			for(int i=0;i<this.theme_listview.getChildCount();i++){
				listItem = theme_listview.getChildAt(i);				
				checkItem = (CheckBox)listItem.findViewById(R.id.XEMOBILE_THEME_ITEM_CHECKBOX);
				if(checkItem.equals(buttonView)){
					continue;
				}
				checkItem.setChecked(false);
			}
			this.checkPosition = (Integer)buttonView.getTag();
			this.themeAdapter.setUserCheckedPosition(checkPosition);
		}else{
			this.checkPosition = -1;
		}
	}
	
	//Async Task for saving the current theme selection
		private class SaveThemeAsyncTask extends AsyncTask<String,Object,Object>
		{

			@Override
			protected Object doInBackground(String ... params)
			{
				XETheme selTheme = themes.get(checkPosition);
				
				//Make post request to save theme
						HttpPost post = new HttpPost(XEHost.getINSTANCE().getURL()+"/index.php?");
						List<NameValuePair> post_params = new ArrayList<NameValuePair>();
						
						for(XEThemeSkin skin : selTheme.skins){							
							post_params.add(new BasicNameValuePair(skin.module, skin.name));
						}	
						
						post_params.add(new BasicNameValuePair("layout", selTheme.layout_srl));
						post_params.add(new BasicNameValuePair("module","admin"));
						post_params.add(new BasicNameValuePair("act","procAdminInsertThemeInfo"));
						post_params.add(new BasicNameValuePair("themeItem",selTheme.name));
						post_params.add(new BasicNameValuePair("ruleset","insertThemeInfo"));
						try {							
							post.setEntity(new UrlEncodedFormEntity(post_params));
							XEHost.getINSTANCE().getThreadSafeClient().execute(post);
						} catch (UnsupportedEncodingException e) {
							
							e.printStackTrace();
						} catch (ClientProtocolException e) {
						
							e.printStackTrace();
						} catch (IOException e) {
							
							e.printStackTrace();
						}
//						
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
