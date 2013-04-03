package arnia.xemobile;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class XEFragment extends Fragment {

	protected FragmentActivity activity = null;
	protected ActionBar actionBar = null;

	private static int progressDialogCount;
	private static ProgressDialog progress;

	public static void startProgress(Context context, String message) {
		progressDialogCount++;
		if (progressDialogCount == 1)
			progress = ProgressDialog.show(context, null, message, true,
					false);
	}

	public static void dismissProgress() {
		progressDialogCount--;
		if (progressDialogCount <= 0) {
			progress.dismiss();
			progressDialogCount = 0;
		}
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
