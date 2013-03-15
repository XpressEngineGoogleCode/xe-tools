package arnia.xemobile;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.data.XEDatabaseHelper;
import arnia.xemobile.menu_management.XEMobileMenuController;
import arnia.xemobile.page_management.XEMobilePageAddController;
import arnia.xemobile.page_management.XEMobilePageController;
import arnia.xemobile_textyle_posts.XEMobileTextyleAddPostController;

//Activity for the Main Menu of XEMobile application
public class XEMobileDashboardFragment extends XEFragment implements
		OnClickListener {

	private Spinner selectSiteSpinner;
	private Cursor siteCursor;
	private SiteAdapter siteAdapter;
	private Cursor selectingSite = null;
	private View view;

	private TextView newPost;
	private TextView newPage;
	private TextView managePages;
	private TextView manageMenus;

	protected ProgressDialog progress;

	public void startProgress(String message) {
		progress = ProgressDialog.show(this.activity, null, message, true,
				false);
	}

	public void dismissProgress() {
		progress.dismiss();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.view = inflater.inflate(R.layout.xemobiledashboardlayout,
				container, false);
		// Change action bar according to current fragment
		this.loadActionMenuBar(R.layout.xemobileactionbarlayout);

		newPost = (TextView) this.view
				.findViewById(R.id.XEMOBILE_DASHBOARD_NEW_POST);
		newPost.setOnClickListener(this);

		newPage = (TextView) this.view
				.findViewById(R.id.XEMOBILE_DASHBOARD_NEW_PAGE);
		newPage.setOnClickListener(this);

		managePages = (TextView) this.view
				.findViewById(R.id.XEMOBILE_DASHBOARD_MANAGE_PAGES);
		managePages.setOnClickListener(this);

		manageMenus = (TextView) this.view
				.findViewById(R.id.XEMOBILE_DASHBOARD_MENU_MANAGER);
		manageMenus.setOnClickListener(this);

		ActionBar actionBar = this.activity.getActionBar();
		actionBar.setCustomView(R.layout.xemobileactionbarlayout);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		this.selectSiteSpinner = (Spinner) actionBar.getCustomView()
				.findViewById(R.id.XEMOBILE_MENU_SELECT_SITE);

		// Handle login when user change site
		this.selectSiteSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						Log.i("XEMobile", "Handler item selected");
						Cursor cursor = (Cursor) parent
								.getItemAtPosition(position);
						String userSelectingSite = cursor.getString(1);
						SharedPreferences pref = PreferenceManager
								.getDefaultSharedPreferences(activity);
						String loggedInSite = pref.getString("ACTIVE_SITE", "");
						// if user select different site, login to another site
						// if(loggedInSite.compareTo(userSelectingSite)!=0){
						selectingSite = cursor;
						startProgress("Logging...");
						new LogInInBackground().execute();
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
		} else if (v.getId() == R.id.XEMOBILE_DASHBOARD_NEW_PAGE) {
			mainActivity.addMoreScreen(new XEMobilePageAddController());
		} else if (v.getId() == R.id.XEMOBILE_DASHBOARD_MANAGE_PAGES) {
			mainActivity.addMoreScreen(new XEMobilePageController());
		} else if (v.getId() == R.id.XEMOBILE_DASHBOARD_MENU_MANAGER) {
			mainActivity.addMoreScreen(new XEMobileMenuController());
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		Fragment fragment = fragmentManager
				.findFragmentById(R.id.XEMOBILE_WEBSITE_STATISTIC);
		fragmentManager.beginTransaction().remove(fragment).commit();
	};

	@Override
	public void onResume() {
		XEDatabaseHelper dbHelper = XEDatabaseHelper.getDBHelper(this.activity);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		this.siteCursor = db.rawQuery("SELECT * FROM " + dbHelper.XE_SITES,
				null);
		this.siteAdapter = new SiteAdapter(this.activity, siteCursor);
		this.selectSiteSpinner.setAdapter(siteAdapter);
		this.siteAdapter.notifyDataSetChanged();
		super.onResume();
	}

	private void refreshContent() {
		// refresh data in statistic fragment
		FragmentManager fm = this.activity.getSupportFragmentManager();
		XEMobileStatisticsController sc = (XEMobileStatisticsController) fm
				.findFragmentById(R.id.XEMOBILE_WEBSITE_STATISTIC);
		sc.refreshStatistic();
		// refresh data in comment

	}

	// AsyncTask for LogIn
	private class LogInInBackground extends AsyncTask<Void, Void, Void> {

		private String xmlData;
		private boolean request_url_error = false;

		// send the request in background

		@Override
		protected Void doInBackground(Void... params) {
			try {
				// set address in XEHost singleton
				String url = selectingSite.getString(1);
				String userid = selectingSite.getString(2);
				String password = selectingSite.getString(3);

				XEHost.getINSTANCE().setURL(url);

				xmlData = XEHost
						.getINSTANCE()
						.getRequest(
								"/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="
										+ userid + "&password=" + password);

			} catch (Exception e) {

				e.printStackTrace();
				request_url_error = true;
			} finally {
				return null;
			}
		}

		// verify the response after the request received a response
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.i("XEMObile", xmlData);
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
			TextView textView = (TextView) view
					.findViewById(R.id.SITE_SPINNER_ITEM);
			textView.setText(cursor.getString(1));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(
					R.layout.xemobilesitespinneritemlayout, null);
			return view;
		}
	}

}
