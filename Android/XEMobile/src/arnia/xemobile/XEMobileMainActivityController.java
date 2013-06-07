package arnia.xemobile;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import arnia.xemobile.data.XEMobileSite;
import arnia.xemobile.site_management.XEMobileSiteController;

//Activity for the Main Menu of XEMobile application
public class XEMobileMainActivityController extends FragmentActivity implements
		OnPageChangeListener {

	private ViewPager pager;
	private XEMobilePageAdapter pageAdapter;
	private int prevPageIndex;
	private XEMobileSite selectingSite;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobilemainactivitylayout);

		actionBar = getActionBar();
		actionBar.setCustomView(R.layout.xemobileactionbarlayout);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_action_bar));

		pager = (ViewPager) findViewById(R.id.pager);
		pageAdapter = new XEMobilePageAdapter(getSupportFragmentManager());

		pager.setAdapter(pageAdapter);
		pageAdapter.addFragment(new XEMobileDashboardFragment());
		pager.setOnPageChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

	public void setSelectingSite(XEMobileSite site) {
		selectingSite = site;
	}

	public void requestToBrowser() {
		if (selectingSite != null) {
			Intent browser = new Intent(Intent.ACTION_VIEW,
					Uri.parse(selectingSite.siteUrl));
			this.startActivity(browser);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings_website_manager:
			addMoreScreen(new XEMobileSiteController());
			break;
		case R.id.menu_settings_help:
			addMoreScreen(new XEMobileHelpController());
			break;
		case R.id.menu_settings_about:
			addMoreScreen(new XEMobileAboutController());
			break;
		case android.R.id.home:
			backwardScreen();
			break;
		default:
			break;
		}

		return true;
	}

	public Fragment getCurrentDisplayedFragment() {
		return this.pageAdapter.getItem(this.pager.getCurrentItem());
	}

	public void addMoreScreen(Fragment screen) {
		pageAdapter.addFragment(screen);
		pager.setCurrentItem(pageAdapter.getCount() - 1, true);
	}

	public void backwardScreen() {
		// Fragment oldScreen = pageAdapter.getItem(pageAdapter.getCount()-1);
		pager.setCurrentItem(pageAdapter.getCount() - 2, true);
	}

	private class XEMobilePageAdapter extends FragmentStatePagerAdapter {

		ArrayList<Fragment> screenStack;

		public XEMobilePageAdapter(FragmentManager fm) {
			super(fm);
			screenStack = new ArrayList<Fragment>();
		}

		@Override
		public Fragment getItem(int position) {
			return screenStack.get(position);

		}

		public void addFragment(Fragment screen) {
			screenStack.add(screen);
			this.notifyDataSetChanged();

		}

		public void removeLastFragment() {
			// removeFragment(screenStack.get(getCount()-1));
			screenStack.remove(prevPageIndex);
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
	public void onPageSelected(int pageIndex) {
		if (prevPageIndex > pageIndex) {
			this.pageAdapter.removeLastFragment();
		}
		prevPageIndex = pageIndex;
		boolean displayHomeAsUp = pageIndex == 0 ? false : true;
		getActionBar().setDisplayHomeAsUpEnabled(displayHomeAsUp);
	}

	@Override
	public void onBackPressed() {
		if (pager.getChildCount() > 1) {
			backwardScreen();
		} else {
			super.onBackPressed();
		}
	}
}
