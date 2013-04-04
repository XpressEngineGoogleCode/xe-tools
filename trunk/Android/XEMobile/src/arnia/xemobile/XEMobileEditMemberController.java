package arnia.xemobile;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEMember;
import arnia.xemobile.classes.XEResponse;

public class XEMobileEditMemberController extends XEFragment implements OnClickListener
{
	//ui references
	private TextView emailTextView;
	private EditText nicknameEditText;
	private EditText descriptionEditText;
	private CheckBox allowMailingCheckBox;
	private CheckBox allowMessageCheckBox;
	private CheckBox approveMemberCheckBox;
	private CheckBox isAdminCheckBox;
	private Button saveButton;
	
	//member that is edited
	private XEMember member;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.xemobileeditmemberlayout,container,false);
		
		//take references to UI elements
		emailTextView = (TextView) view.findViewById(R.id.XEMOBILE_EDITMEMBER_EMAIL);
		nicknameEditText = (EditText) view.findViewById(R.id.XEMOBILE_EDITMEMBER_NICKNAME);
		descriptionEditText = (EditText) view.findViewById(R.id.XEMOBILE_EDITMEMBER_DESCRIPTION);
		allowMailingCheckBox = (CheckBox) view.findViewById(R.id.XEMOBILE_EDITMEMBER_ALLOWMAILING);
		allowMessageCheckBox = (CheckBox) view.findViewById(R.id.XEMOBILE_EDITMEMBER_ALLOWMESSAGE);
		approveMemberCheckBox = (CheckBox) view.findViewById(R.id.XEMOBILE_EDITMEMBER_APPROVEMEMBER);
		isAdminCheckBox = (CheckBox) view.findViewById(R.id.XEMOBILE_EDITMEMBER_ISADMIN);
		saveButton = (Button) view.findViewById(R.id.XEMOBILE_EDITMEMBER_SAVE);
		saveButton.setOnClickListener(this);
				
		//get the member object passed between activities
		//Intent intent = getIntent();
		Bundle args = getArguments();
		
		member = (XEMember) args.getSerializable("member");
		
		//load the current settings
		completeSettingsFormWithMemberSettings();
//	}
		
		return view;
	}
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) 
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.xemobileeditmemberlayout);
//		
//		//take references to UI elements
//		emailTextView = (TextView) findViewById(R.id.XEMOBILE_EDITMEMBER_EMAIL);
//		nicknameEditText = (EditText) findViewById(R.id.XEMOBILE_EDITMEMBER_NICKNAME);
//		descriptionEditText = (EditText) findViewById(R.id.XEMOBILE_EDITMEMBER_DESCRIPTION);
//		allowMailingCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_EDITMEMBER_ALLOWMAILING);
//		allowMessageCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_EDITMEMBER_ALLOWMESSAGE);
//		approveMemberCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_EDITMEMBER_APPROVEMEMBER);
//		isAdminCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_EDITMEMBER_ISADMIN);
//		saveButton = (Button) findViewById(R.id.XEMOBILE_EDITMEMBER_SAVE);
//		saveButton.setOnClickListener(this);
//				
//		//get the member object passed between activities
//		Intent intent = getIntent();
//		member = (XEMember) intent.getSerializableExtra("member");
//		
//		//load the current settings
//		completeSettingsFormWithMemberSettings();
//	}
	
	// load the current settings
	private void completeSettingsFormWithMemberSettings()
	{
		emailTextView.setText( member.email );
		nicknameEditText.setText( member.nickname );
		descriptionEditText.setText( member.description );
		
		if( member.allowMailing() ) allowMailingCheckBox.setChecked(true);
			else allowMessageCheckBox.setChecked(false); 
		if( member.allowMessage() ) allowMessageCheckBox.setChecked(true);
			else allowMessageCheckBox.setChecked(false);
		if( member.isAdmin() ) isAdminCheckBox.setChecked(true);
			else isAdminCheckBox.setChecked(false);
		if (member.isApproved() ) approveMemberCheckBox.setChecked(true);
			else approveMemberCheckBox.setChecked(false); 
	}

	//Method called when the save button is pressed
	@Override
	public void onClick(View v) 
	{
			startProgress(activity,"Loading...");
			SaveMemberAsyncTask asyncTask = new SaveMemberAsyncTask();
			asyncTask.execute();
	}
	
	//AsyncTask for saving member details
	private class SaveMemberAsyncTask extends AsyncTask<Object,Object,Object>
	{
		XEResponse responseObj;
		String response;
		
		@Override
		protected Object doInBackground(Object... paramss) 
		{
			//
			//building the request for saving the member
			//
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("ruleset", "insertAdminMember");
			params.put("module", "mobile_communication");
			params.put("act", "procmobile_communicationEditMember");
			params.put("member_srl", member.member_srl);
			params.put("email_address", member.email);
			params.put("password",member.password);
			params.put("nick_name",nicknameEditText.getText().toString());
			params.put("description",descriptionEditText.getText().toString());
			params.put("find_account_answer", (member.secret_answer==null)? "":member.secret_answer);
			params.put("find_account_question", member.find_account_question);
			if(isAdminCheckBox.isChecked()) params.put("is_admin", "Y");
					else params.put("is_admin", "N");
			
			if(allowMailingCheckBox.isChecked()) params.put("allow_mailing", "Y");
					else params.put("allow_mailing", "N");
			
			if(allowMessageCheckBox.isChecked()) params.put("allow_message","Y");
					else params.put("allow_message", "N");
				
			if( !approveMemberCheckBox.isChecked() ) params.put("denied", "Y");
					else params.put("denied", "N"); 
	       
			//sending the request
           try {
        	   response = XEHost.getINSTANCE().postMultipart(params, "/");
    	       
    	       Serializer serializer = new Persister();        
               Reader reader = new StringReader(response);
        	   
			responseObj = 
			       serializer.read(XEResponse.class, reader, false);
			
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
			return responseObj;
		}
		
		//method called when the response came
		@Override
		protected void onPostExecute(Object result)
		{
			//check to see if the user is logged in
//			isLoggedIn(response, XEMobileEditMemberController.this);
			
			XEResponse resp = (XEResponse) result;
			dismissProgress();
			
			super.onPostExecute(result);
//			finish();
			((XEMobileMainActivityController)activity).backwardScreen();
		}
		
	}
}
