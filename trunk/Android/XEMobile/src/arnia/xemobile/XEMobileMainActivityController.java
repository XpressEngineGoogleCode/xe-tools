package arnia.xemobile;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Stack;

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
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.CornerPathEffect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEMember;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.data.XEDatabaseHelper;
import arnia.xemobile.global_settings.XEMobileGlobalSettingsController;
import arnia.xemobile.menu_management.XEMobileMenuController;
import arnia.xemobile.page_management.XEMobilePageController;
import arnia.xemobile.theme_management.XEMobileThemeController;
import arnia.xemobile_textyle.XEMobileTextyleSelectTextyleController;

//Activity for the Main Menu of XEMobile application
public class XEMobileMainActivityController extends FragmentActivity implements OnPageChangeListener
{	
	public ViewPager pager;
	
	public XEMobilePageAdapter pageAdapter; 
	
	private int prevPageIndex=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobilemainactivitylayout);
		
		this.pager = (ViewPager) findViewById(R.id.pager);
		pageAdapter = new XEMobilePageAdapter(getSupportFragmentManager());
		
		pager.setAdapter(pageAdapter);
		pageAdapter.addFragment(new XEMobileDashboardFragment());
		pager.setOnPageChangeListener(this);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}
	
//	private void removeFragment (Fragment fragment){
//		FragmentManager fm = getSupportFragmentManager();
//		fm.beginTransaction().remove(fragment).commit();
//	}

	public class XEMobilePageAdapter extends FragmentStatePagerAdapter{
		
		ArrayList<Fragment> screenStack;
		
		
		public XEMobilePageAdapter(FragmentManager fm) {
			super(fm);	
			screenStack = new ArrayList<Fragment>();
		}

		@Override
		public Fragment getItem(int position) {		
			return screenStack.get(position);
		
		}
		public void addFragment(Fragment screen){
			screenStack.add(screen);
			this.notifyDataSetChanged();
			
		}
		public void removeLastFragment(){
//			removeFragment(screenStack.get(getCount()-1));
			screenStack.remove(getCount()-1);
			this.notifyDataSetChanged();			
		}
		@Override
		public int getCount() {
			return screenStack.size();			
		}	
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int pageIndex){
		if(prevPageIndex>pageIndex){
			this.pageAdapter.removeLastFragment();
		}
		prevPageIndex = pageIndex;
	}
}
