package arnia.xemobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

//Activity that has a ProgressDialog and one method that verifies if the user is logged id
public class XEActivity extends Activity
{
	protected ProgressDialog progress;
	
	public void startProgress(String message)
	{
		progress = ProgressDialog.show(this, null, message , true,false);
	}
	
	public void dismissProgress()
	{
		progress.dismiss();
	}
	
	//if the user is logged out the Login Controller is pushed
	public boolean isLoggedIn(String response,Context context)
	{
		if( response.equals("logout_error!") )
		{
			Intent intent = new Intent(context,XEMobileLoginController.class);
			startActivity(intent);
			
			return false;
		}
		
		return true;
	}
}
