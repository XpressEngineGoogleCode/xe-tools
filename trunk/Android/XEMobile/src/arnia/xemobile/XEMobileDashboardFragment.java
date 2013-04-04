package arnia.xemobile;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.data.XEDatabaseHelper;
import arnia.xemobile.data.XEMobileSite;
import arnia.xemobile.global_settings.XEMobileGlobalSettingsController;
import arnia.xemobile.menu_management.XEMobileMenuController;
import arnia.xemobile.page_management.XEMobilePageAddController;
import arnia.xemobile.page_management.XEMobilePageController;
import arnia.xemobile_textyle_comments.XEMobileTextyleCommentsController;
import arnia.xemobile_textyle_posts.XEMobileTextyleAddPostController;
import arnia.xemobile_textyle_posts.XEMobileTextylePostsController;

//Activity for the Main Menu of XEMobile application
public class XEMobileDashboardFragment extends XEFragment implements
		OnClickListener {

	private Spinner selectSiteSpinner;
	private Cursor siteCursor;
	private ArrayList<XEMobileSite> sites;
	private ArrayList<Object> sitesAndVirtualSites;
	private SiteAdapter siteAdapter;
	private XEMobileSite selectingSite;
	private View view;
	private XEMobileStatisticsController statisticController;

	private TextView newPost;
	private TextView managePosts;
	private TextView newPage;
	private TextView managePages;
	private TextView manageMenus;
	
	private TextView commentCount;
	private TextView quickSetting;
	private TextView userSetting;
	private TextView comment;
	private TextView manageSite;

	private Button backButton;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		sitesAndVirtualSites = new ArrayList<Object>();
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.view = inflater.inflate(R.layout.xemobiledashboardlayout,
				container, false);
		
		
//		LinearLayout layoutHolder = (LinearLayout) view.findViewById(R.id.XEMOBILE_DASHBOARD_FRAGMENT_HOLDER);
		
		statisticController = new XEMobileStatisticsController();
		
		addNestedFragment(R.id.XEMOBILE_DASHBOARD_FRAGMENT_HOLDER, statisticController , "StatisticController");
		
		
		
		// Change action bar according to current fragment
		this.loadActionMenuBar(R.layout.xemobileactionbarlayout);

		newPost = (TextView) this.view
				.findViewById(R.id.XEMOBILE_DASHBOARD_NEW_POST);
		newPost.setOnClickListener(this);

		managePosts=(TextView)view.findViewById(R.id.XEMOBILE_DASHBOARD_MANAGE_POSTS);
		managePosts.setOnClickListener(this);
		
		newPage = (TextView) this.view
				.findViewById(R.id.XEMOBILE_DASHBOARD_NEW_PAGE);
		newPage.setOnClickListener(this);

		managePages = (TextView) this.view
				.findViewById(R.id.XEMOBILE_DASHBOARD_MANAGE_PAGES);
		managePages.setOnClickListener(this);
		
		quickSetting = (TextView)this.view.findViewById(R.id.XEMOBILE_DASHBOARD_QUICK_SETTINGS);
		quickSetting.setOnClickListener(this);
		
		comment = (TextView)this.view.findViewById(R.id.XEMOBILE_DASHBOARD_COMMENTS);
		comment.setOnClickListener(this);
		
		userSetting = (TextView)this.view.findViewById(R.id.XEMOBILE_DASHBOARD_USERS);
		userSetting.setOnClickListener(this);
		
		manageSite = (TextView)this.view.findViewById(R.id.XEMOBILE_DASHBOARD_MANAGE_WEBSITE);
		manageSite.setOnClickListener(this);

		manageMenus = (TextView) this.view
				.findViewById(R.id.XEMOBILE_DASHBOARD_MENU_MANAGER);
		manageMenus.setOnClickListener(this);

		this.selectSiteSpinner = (Spinner) actionBar.getCustomView()
				.findViewById(R.id.XEMOBILE_MENU_SELECT_SITE);

		
		this.backButton = (Button) actionBar.getCustomView().findViewById(R.id.XEMOBILE_BACK_BUTTON);
		this.backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((XEMobileMainActivityController)activity).backwardScreen();
				
			}
		});
		
		this.siteAdapter = new SiteAdapter(sitesAndVirtualSites);		
		this.selectSiteSpinner.setAdapter(siteAdapter);
		
		this.commentCount = (TextView) view.findViewById(R.id.XEMOBILE_DASHBOARD_COMMENT_COUNT);
		
		// Handle login when user change site
		this.selectSiteSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent,View view, int position, long id){
				Log.i("XEMobile", "Handler item selected");
				Object selectedItem = parent.getItemAtPosition(position);
				if(selectedItem.getClass()==XEMobileSite.class){
					XEFragment.startProgress(getActivity(), "Logging...");
					selectingSite =(XEMobileSite)selectedItem;
					((XEMobileMainActivityController)activity).setSelectingSite(selectingSite);
					new LogInInBackground().execute((XEMobileSite)selectedItem);
				}else{
					Log.i("XEMobileVirtualSite","you selected virtual site");
					
					XEMobileMainActivityController xeActivity = (XEMobileMainActivityController) activity;
					Fragment currentDisplayFragment = xeActivity.getCurrentDisplayedFragment();
					if(currentDisplayFragment.getClass()==XEMobileTextylePostsController.class){
						XEMobileTextylePostsController currentPostController = (XEMobileTextylePostsController)currentDisplayFragment;
						currentPostController.setSelectedTextyle((XETextyle)selectedItem);
						currentPostController.refreshContent();
					}else if(currentDisplayFragment.getClass()==XEMobileTextyleCommentsController.class){
						XEMobileTextyleCommentsController currentPostController = (XEMobileTextyleCommentsController)currentDisplayFragment;
						currentPostController.setTextyle((XETextyle)selectedItem);
					}
					
				}

				
				
////						String userSelectingSite = cursor.getString(1);
////						SharedPreferences pref = PreferenceManager
////								.getDefaultSharedPreferences(activity);
////						String loggedInSite = pref.getString("ACTIVE_SITE", "");
////						// if user select different site, login to another site
////						// if(loggedInSite.compareTo(userSelectingSite)!=0){
////						selectingSite = cursor;
//						String userSelectingSite = selectedSite.siteUrl;
//						SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
//						String loggedInSite = pref.getString("ACTIVE_SITE", "");
				// if user select different site, login to another site
				// if(loggedInSite.compareTo(userSelectingSite)!=0){
//						selectingSite = cursor;
				
				// }

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
		
		if (v.getId() == R.id.XEMOBILE_DASHBOARD_NEW_POST) {			
			mainActivity.addMoreScreen(new XEMobileTextyleAddPostController());
		} else if (v.getId() == R.id.XEMOBILE_DASHBOARD_MANAGE_POSTS) {
			XEMobileTextylePostsController textylePostController = new XEMobileTextylePostsController();
			XETextyle textyle = (XETextyle) sitesAndVirtualSites.get(sites.indexOf(selectingSite)+1);
			textylePostController.setSelectedTextyle(textyle);			
			mainActivity.addMoreScreen(textylePostController);			
		} else if (v.getId() == R.id.XEMOBILE_DASHBOARD_NEW_PAGE) {
			mainActivity.addMoreScreen(new XEMobilePageAddController());
		} else if (v.getId() == R.id.XEMOBILE_DASHBOARD_MANAGE_PAGES) {
			mainActivity.addMoreScreen(new XEMobilePageController());
		} else if (v.getId() == R.id.XEMOBILE_DASHBOARD_MENU_MANAGER) {
			mainActivity.addMoreScreen(new XEMobileMenuController());
		} else if(v.getId() == R.id.XEMOBILE_DASHBOARD_QUICK_SETTINGS) {
			mainActivity.addMoreScreen(new XEMobileGlobalSettingsController());
		} else if(v.getId()==R.id.XEMOBILE_DASHBOARD_USERS){
			mainActivity.addMoreScreen(new XEMobileMembersController());
		} else if (v.getId()==R.id.XEMOBILE_DASHBOARD_COMMENTS){
			XETextyle textyle = (XETextyle) sitesAndVirtualSites.get(sites.indexOf(selectingSite)+1);
			XEMobileTextyleCommentsController textyleCommentController = new XEMobileTextyleCommentsController();
			textyleCommentController.setTextyle(textyle);
//			Bundle args = new Bundle();
//			args.putSerializable("textyle", textyle);
//			textyleCommentController.setArguments(args);
			mainActivity.addMoreScreen(textyleCommentController);
		}else if(v.getId()==R.id.XEMOBILE_DASHBOARD_MANAGE_WEBSITE){
			mainActivity.requestToBrowser();
		}

	}
	@Override
	public void onResume() {
		XEFragment.startProgress(getActivity(), "Loading...");
		XEDatabaseHelper dbHelper = XEDatabaseHelper.getDBHelper(this.activity);
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		this.siteCursor = db.rawQuery("SELECT * FROM " + dbHelper.XE_SITES + " ORDER BY _id DESC",null);
//		this.siteAdapter = new SiteAdapter(this.activity, siteCursor);
		
		sitesAndVirtualSites.clear();
		sites = dbHelper.getAllSites();
		
		for(int i=0;i<sites.size();i++){
//				sitesAndVirtualSites.add(sites.get(i).siteUrl);
//				selectingSite = siteCursor;
//			    lock.lock();
				LogInInBackground loginTask = new LogInInBackground(){
					@Override
					protected void onPostExecute(XEMobileSite result) {
						dismissProgress();
						sitesAndVirtualSites.add(site);
						if( array != null && array.textyles != null)
						{	
							sitesAndVirtualSites.addAll(array.textyles);
							
//							for(int i = 0; i<array.textyles.size();i++)
//							{
//								commentCounts += Integer.parseInt(array.textyles.get(i).comment_count);
//								sitesAndVirtualSites.add(array.textyles.get(i));
////								sitesAndVirtualSites.add(site.siteUrl + "/" + array.textyles.get(i).browser_title);
//							}
//							adapter.notifyDataSetChanged();
							siteAdapter.notifyDataSetChanged();
						}else{
							Toast.makeText(activity, R.string.no_textyle, 1000).show();
						}	
					}
				};
				loginTask.execute(sites.get(i));
		
		}
		super.onResume();
	}

	private void refreshContent() {	
		statisticController.refreshStatistic();
	}
	/*
	private class GetCommentsCountAsynTask extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {			
			String count ="";
			try{
				count = XEHost
						.getINSTANCE()
						.getRequest("/index.php?module=mobile_communication&act=procmobile_communicationCommentCount");
			}catch(Exception e){
				e.printStackTrace();
			}
			return count;
		}
		
		@Override
		protected void onPostExecute(String count) {
			
			commentCount.setText(count);
			
			super.onPostExecute(count);
		}
		
	}
	*/
	
	// AsyncTask for LogIn
	private class LogInInBackground extends AsyncTask<XEMobileSite, Void, XEMobileSite> {

		private String xmlData;
		private boolean request_url_error = false;
		protected XEArrayList array;
		protected XEMobileSite site;
		// send the request in background

		@Override
		protected synchronized XEMobileSite doInBackground(XEMobileSite... params){
			
			site = params[0];
			
			try {
				
				// set address in XEHost singleton
				String url = site.siteUrl;
				String userid = site.userName;
				String password = site.password;

				XEHost.getINSTANCE().setURL(url);

				xmlData = XEHost
						.getINSTANCE()
						.getRequest(
								"/index.php?XDEBUG_SESSION_START=netbeans-xdebug&module=mobile_communication&act=procmobile_communicationLogin&user_id="
										+ userid + "&password=" + password);
				
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
				
			} catch (Exception e) {

				e.printStackTrace();
				request_url_error = true;
			} finally {	
			
				return site;
			}
		}

		// verify the response after the request received a response
		@Override
		protected void onPostExecute(XEMobileSite result) {
			super.onPostExecute(result);
			Log.i("XEMObile", xmlData);
			int count =0;
			for(int i=0;i<array.textyles.size();i++){
				count +=  Integer.parseInt(array.textyles.get(i).comment_count);
			}		
			commentCount.setText("(" + String.valueOf(count)+ ")");
			
			refreshContent();
			dismissProgress();
		}		
	}

	public class SiteAdapter extends BaseAdapter{
		private ArrayList<Object> data;
		
		public SiteAdapter(ArrayList<Object>data){
			this.setData(data);
		}
		public void setData(ArrayList<Object> data){
			this.data = data;
		}
		@Override
		public int getCount() {			
			return data.size();
		}

		@Override
		public Object getItem(int position) {			
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {			
			return position;
		}
		
		public int getPositionOfItem(Object item){
			return data.indexOf(item);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView==null){
				LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = layoutInflater.inflate(R.layout.xemobilesitespinneritemlayout, null);
			}
			TextView textRow = (TextView) convertView.findViewById(R.id.SITE_SPINNER_ITEM);
			textRow.setTag(position);
			Object obj = sitesAndVirtualSites.get(position);
			if(obj.getClass()==XETextyle.class){
				textRow.setText(((XETextyle)obj).textyle_title);
			}else{
				textRow.setText(obj.toString());
			}
			return convertView;
		}

		
		
	}
	
}
