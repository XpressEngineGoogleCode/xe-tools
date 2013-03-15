package arnia.xemobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class XEFragment extends Fragment {
	
	protected FragmentActivity activity=null;
	
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
	public void onCreate(Bundle savedInstanceState) {
		this.activity = getActivity();		
		super.onCreate(savedInstanceState);
	}
	public void loadActionMenuBar(int resource){
		if(this.activity!=null){
			ActionBar actionBar = this.activity.getActionBar();
			actionBar.setCustomView(resource);	
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		}
	}
}
