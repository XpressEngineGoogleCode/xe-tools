package arnia.xemobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class XEMobileWelcomeScreenController extends Activity implements
		OnClickListener {

	private Button addWebsite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobilewelcomescreenlayout);

		this.addWebsite = (Button) findViewById(R.id.XEMOBILE_ADD_NEW_WEBSITE);
		this.addWebsite.setClickable(true);
		this.addWebsite.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		Intent callLogin = new Intent(this, XEMobileLoginController.class);
		startActivity(callLogin);
		finish();
	}

}
