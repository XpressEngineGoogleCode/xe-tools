package arnia.xemobile;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import arnia.xemobile.utility.XEMobileProgressDialog;

public class XEFragment extends Fragment {

	protected FragmentActivity activity = null;
	protected ActionBar actionBar = null;

	protected ProgressDialog progress;

	public void startProgress(String message) {
		XEMobileProgressDialog.startProgress(this.activity, message);
	}

	public void dismissProgress() {
		XEMobileProgressDialog.dismissProgress();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.activity = getActivity();
		this.actionBar = this.activity.getActionBar();
		super.onCreate(savedInstanceState);
	}

	public void loadActionMenuBar(int resource) {
		if (this.actionBar != null) {
			actionBar.setCustomView(resource);
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		}
	}

	public void addNestedFragment(int layoutID, Fragment fragment,
			String fragmentName) {
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.add(layoutID, fragment, fragmentName).commit();
	}
}
