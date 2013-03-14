package arnia.xemobile;

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

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEMember;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.data.XEDatabaseHelper;
import arnia.xemobile.global_settings.XEMobileGlobalSettingsController;
import arnia.xemobile.menu_management.XEMobileMenuController;
import arnia.xemobile.page_management.XEMobilePageAddController;
import arnia.xemobile.page_management.XEMobilePageController;
import arnia.xemobile.theme_management.XEMobileThemeController;
import arnia.xemobile_textyle.XEMobileTextyleSelectTextyleController;

//Activity for the Main Menu of XEMobile application
public class XEMobileDashboardFragment extends XEFragment implements OnClickListener
{
	
	private Spinner selectSiteSpinner;
	private Cursor siteCursor;
	private SiteAdapter siteAdapter ;
	private Cursor selectingSite=null;
	private View view;	
	
	private TextView newPage;
	
	protected ProgressDialog progress;
	
	public void startProgress(String message)
	{
		progress = ProgressDialog.show(this.activity, null, message , true,false);
	}
	
	public void dismissProgress()
	{
		progress.dismiss();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		this.view = inflater.inflate(R.layout.xemobiledashboardlayout,container,false);
		//Change action bar according to current fragment
		this.loadActionMenuBar(R.layout.xemobileactionbarlayout);
		
		newPage = (TextView) this.view.findViewById(R.id.XEMOBILE_DASHBOARD_NEW_PAGE);
		newPage.setOnClickListener(this);
		
		ActionBar actionBar = this.activity.getActionBar();
		actionBar.setCustomView(R.layout.xemobileactionbarlayout);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		this.selectSiteSpinner = (Spinner)  actionBar.getCustomView().findViewById(R.id.XEMOBILE_MENU_SELECT_SITE);
		
//		Handle login when user change site 
		this.selectSiteSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				Log.i("XEMobile","Handler item selected");
				Cursor cursor = (Cursor) parent.getItemAtPosition(position);
				String userSelectingSite = cursor.getString(1);
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
				String loggedInSite = pref.getString("ACTIVE_SITE", "");
				//if user select different site, login to another site
				//if(loggedInSite.compareTo(userSelectingSite)!=0){
					selectingSite = cursor;					
					startProgress("Logging...");
					new LogInInBackground().execute();
				//}
				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		return this.view;
	}
	
	@Override
	public void onClick(View v) {
		XEMobileMainActivityController mainActivity = (XEMobileMainActivityController) this.activity;
		
		if(v.getId()==R.id.XEMOBILE_DASHBOARD_NEW_PAGE){
			mainActivity.pageAdapter.addFragment(new XEMobilePageAddController());
			mainActivity.pager.setCurrentItem(mainActivity.pageAdapter.getCount()-1, true);
		}
		
	}
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {		
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.xemobilemainactivitylayout);
//		ActionBar actionBar = getActionBar();
//		actionBar.setCustomView(R.layout.xemobileactionbarlayout);
//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//		
//		this.selectSiteSpinner = (Spinner)  actionBar.getCustomView().findViewById(R.id.XEMOBILE_MENU_SELECT_SITE);
//		
//		ImageView menuLogo = (ImageView) actionBar.getCustomView().findViewById(R.id.XEMOBILE_MENU_LOGO);
//		menuLogo.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
////				selectSiteSpinner.setVisibility(Spinner.VISIBLE);
//				
//			}
//		});
//		
//		//Handle login when user change site 
//		this.selectSiteSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				
//				Log.i("XEMobile","Handler item selected");
//				Cursor cursor = (Cursor) parent.getItemAtPosition(position);
//				String userSelectingSite = cursor.getString(1);
//				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplication());
//				String loggedInSite = pref.getString("ACTIVE_SITE", "");
//				//if user select different site, login to another site
//				//if(loggedInSite.compareTo(userSelectingSite)!=0){
//					selectingSite = cursor;					
//					startProgress("Logging...");
//					new LogInInBackground().execute();
//				//}
//				
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
//	}	
	
	@Override
	public void onResume() {	
		
		XEDatabaseHelper dbHelper = XEDatabaseHelper.getDBHelper(this.activity);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		this.siteCursor = db.rawQuery("SELECT * FROM " + dbHelper.XE_SITES, null);
		this.siteAdapter = new SiteAdapter(this.activity, siteCursor);
		this.selectSiteSpinner.setAdapter(siteAdapter);
		this.siteAdapter.notifyDataSetChanged();
		super.onResume();
	}
	
	private void refreshContent(){
		//refresh data in statistic fragement
		FragmentManager fm = getFragmentManager();
		XEMobileStatisticsController sc = (XEMobileStatisticsController) fm.findFragmentById(R.id.XEMOBILE_WEBSITE_STATISTIC);
		sc.refreshStatistic();
		//refresh data in comment
		
	}
	
	//AsyncTask for LogIn
    private class LogInInBackground extends AsyncTask<Void, Void, Void> 
    {
    	
    	
    	private String xmlData;
    	private boolean request_url_error=false;
    	
    	//send the request in background
		
		@Override
		protected Void doInBackground(Void... params) 
		{
			try{
				// set address in XEHost singleton
				String url = selectingSite.getString(1) ;
				String userid = selectingSite.getString(2) ;
				String password = selectingSite.getString(3) ;
				
				XEHost.getINSTANCE().setURL(url);
				
				xmlData = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="+ userid + "&password=" + password);
		    	
			}catch(Exception e){
				
				e.printStackTrace();
				request_url_error=true;
			}finally{
				return null;
			}
		}
		
		//verify the response after the request received a response
		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			Log.i("XEMObile",xmlData);
			refreshContent();
			dismissProgress();
		}
	}
	
	
	private class SiteAdapter extends CursorAdapter {

		public SiteAdapter(Context context, Cursor c) {
			super(context, c);			
		}		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView textView  = (TextView) view.findViewById(R.id.SITE_SPINNER_ITEM);
			textView.setText(cursor.getString(1));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater layoutInflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view  = layoutInflater.inflate(R.layout.xemobilesitespinneritemlayout, null);						
			return view;
		}		
	}


	
	

	
	
	
	
}
