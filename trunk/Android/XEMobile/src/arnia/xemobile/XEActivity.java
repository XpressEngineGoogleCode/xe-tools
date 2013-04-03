package arnia.xemobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import arnia.xemobile.utility.XEMobileProgressDialog;

//Activity that has a ProgressDialog and one method that verifies if the user is logged id
public class XEActivity extends Activity {
	protected ProgressDialog progress;

	public void startProgress(String message) {
		XEMobileProgressDialog.startProgress(this, message);
	}

	public void dismissProgress() {
		XEMobileProgressDialog.dismissProgress();
	}

	// if the user is logged out the Login Controller is pushed
	public boolean isLoggedIn(String response, Context context) {
		if (response.equals("logout_error!")) {
			Toast.makeText(this, R.string.session_expired_msg,
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(context, XEMobileLoginController.class);
			startActivity(intent);

			return false;
		}

		return true;
	}
}
