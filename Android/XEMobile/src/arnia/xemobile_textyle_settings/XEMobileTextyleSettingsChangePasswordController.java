package arnia.xemobile_textyle_settings;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XETextyle;

//Activity for changing the password
public class XEMobileTextyleSettingsChangePasswordController extends Activity implements OnClickListener
{
	//UI references
	private EditText oldPasswordEditText;
	private EditText newPasswordEditText;
	private EditText confPasswordEditText;
	private Button saveButton;
	
	private XETextyle textyle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobiletextylesettingschangepasswordlayout);
	
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		
		//get references to UI elements
		oldPasswordEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTYLE_CHANGEPASSWORD_OLDPASS);
		newPasswordEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTYLE_CHANGEPASSWORD_NEWPASS);
		confPasswordEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTYLE_CHANGEPASSWORD_CONFPASS);
		saveButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_CHANGEPASSWORD_SAVEBUTTON);
		
		saveButton.setOnClickListener(this);
		
	}

	//method called when the save button is pressed
	@Override
	public void onClick(View v) 
	{
		String newPassword = newPasswordEditText.getText().toString();
		String confPassword = confPasswordEditText.getText().toString();
		
		if( !newPassword.equals(confPassword) ) 
		{
			Toast toast = Toast.makeText(getApplicationContext(), "The confirmation isn't correct!", Toast.LENGTH_SHORT);
			toast.show();
		}
		else
		{
			new ChangePasswordAsyncTask().execute();
		}
	}
	
	//Async Task for changing the password
	private class ChangePasswordAsyncTask extends AsyncTask<Object, Object, Object>
	{

		@Override
		protected Object doInBackground(Object... params) 
		{
			String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
					"<methodCall>\n<params>\n" +
					"<_filter><![CDATA[modify_password]]></_filter>\n" +
					"<act><![CDATA[procMemberModifyPassword]]></act>\n" +
					"<vid><![CDATA["+textyle.domain+"]]></vid>\n" +
					"<mid><![CDATA[textyle]]></mid>\n" +
					"<current_password><![CDATA["+ oldPasswordEditText.getText().toString() +"]]></current_password>\n" +
					"<password><![CDATA["+ newPasswordEditText.getText().toString() +"]]></password>\n" +
					"<password2><![CDATA["+ confPasswordEditText.getText().toString() +"]]></password2>\n" +
					"<module><![CDATA[member]]></module>\n" +
					"</params>\n</methodCall>";
			
			//send request 
			String response = XEHost.getINSTANCE().postRequest("/index.php", xml);
			
			return null;
		}
		
		//finish activity when the response came
		@Override
		protected void onPostExecute(Object result) 
		{
			finish();
			super.onPostExecute(result);
		}
	}
}	
