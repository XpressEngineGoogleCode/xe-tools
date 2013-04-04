package arnia.xemobile;


import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEResponse;
import arnia.xemobile.data.XEDatabaseHelper;

import com.google.android.gcm.GCMRegistrar;


public class XEMobileLoginController extends XEActivity implements OnClickListener{
    //ui elements
    private EditText addressEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    
    private final int WRONG_PASSWORD_DIALOG=1;
    private final int CHECK_CONNECTION_DIALOG=2;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xemobileloginlayout);        
    
        //take reference to the UI elements
        addressEditText = (EditText) findViewById(R.id.XEMOBILE_LOGIN_ADDRESS);
        usernameEditText = (EditText) findViewById(R.id.XEMOBILE_LOGIN_USERNAME);
        passwordEditText = (EditText) findViewById(R.id.XEMOBILE_LOGIN_PASSWORD);
       
        
//        //ONLY for testing!
//        addressEditText.setText("192.168.10.250:8080/xe.mobile.ARNIA/xe");
//        usernameEditText.setText("darayong@fsi.com.kh");
//        passwordEditText.setText("admin");
        
        //if "Remember me" is checked, the username, password and url are saved in SharedPreferences
        //when the Activity is created they are putted on EditTexts. 
        
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
       
        addressEditText.setText(pref.getString("url", ""));
        usernameEditText.setText(pref.getString("username", ""));
        passwordEditText.setText(pref.getString("password", ""));
        
        Button loginButton = (Button) findViewById(R.id.XEMOBILE_LOGIN_BUTTON);
        loginButton.setOnClickListener(this);
      
        
    }
  
    //called when login button is pressed
	@Override
	public void onClick(View v) 
	{	
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor prefsEditor = pref.edit();
		
		//check if the "Remember me" is checked
//		if(rememberMeCheckBox.isChecked())
//		{
//			prefsEditor.putBoolean("checked", true);
//			prefsEditor.putString("username", usernameEditText.getText().toString());
//			prefsEditor.putString("password", passwordEditText.getText().toString());
//			prefsEditor.putString("url", addressEditText.getText().toString());
//			prefsEditor.commit();
//		}
//		else
//		{
//			prefsEditor.putBoolean("checked", false);
//			prefsEditor.putString("username", "");
//			prefsEditor.putString("password", "");
//			prefsEditor.putString("url", "");
//			prefsEditor.commit();
//		}
		
        startProgress("Waiting...");
		LogInInBackground task = new LogInInBackground();
		task.execute();
	}

		
	//do wrong password dialog click
	
	public void doWrongPasswordCloseClicked(){
		Log.i("close","close clicked");
	}
	//do checking connection click
	public void doCheckingConnectionCloseClicked(){
		Log.i("check connection dialog","close clicked");
	}
	
	private class WrongPasswordDialog extends DialogFragment{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			//Custom dialog view
			
//			LayoutInflater layoutInflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			View view = layoutInflater.inflate(R.layout.xemobilewrongpasswordlayout, null);
//			
//			Dialog dialog = new Dialog(getActivity());
//			dialog.setCancelable(false);
//			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//			dialog.setContentView(view);
//			
//			return dialog;
			
			AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
						  
						  .setCancelable(true)
						  .setTitle(R.string.wrong_password_dialog_title)
						  .setInverseBackgroundForced(true)
						  .setMessage(R.string.wrong_password_dialog_description)	
						  .setNeutralButton(R.string.wrong_password_dialog_close_button, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								((XEMobileLoginController) getActivity()).doWrongPasswordCloseClicked();
							
							 }
						    })				  
						  .create();

			return alertDialog;
						
		}
		
	}
	
	private class CheckingConnectionDialog extends DialogFragment{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			//Custom dialog view
			
//			LayoutInflater layoutInflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			View view = layoutInflater.inflate(R.layout.xemobilewrongpasswordlayout, null);
//			
//			Dialog dialog = new Dialog(getActivity());
//			dialog.setCancelable(false);
//			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//			dialog.setContentView(view);
//			
//			return dialog;
			
			AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
						  
						  .setCancelable(true)
						  .setTitle(R.string.checking_connection_dialog_title)
						  .setInverseBackgroundForced(true)
						  .setMessage(R.string.checking_connection_dialog_description)	
						  .setNeutralButton(R.string.checking_connection_dialog_close_button, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								((XEMobileLoginController) getActivity()).doWrongPasswordCloseClicked();
							
							 }
						    })				  
						  .create();

			return alertDialog;
						
		}
		
	}
	private String getURL(){
		String url = addressEditText.getText().toString().trim();
		if(!url.contains("http://")){
			url = "http://" + url;
		}
		return url;
	}
    
	//AsyncTask for LogIn
    private class LogInInBackground extends AsyncTask<Void, Void, Void> 
    {
    	String xmlData;
    	boolean request_url_error=false;
    	
    	//send the request in background
		
		@Override
		protected Void doInBackground(Void... params) 
		{
			try{
				// set address in XEHost singleton
				
				
				XEHost.getINSTANCE().setURL(getURL());
				
				xmlData = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="+ usernameEditText.getText().toString() + "&password=" + passwordEditText.getText().toString());
		    	
			}catch(Exception e){		
				e.printStackTrace();
				request_url_error=true;
			}finally{
				return null;
			}
		}
		
		//verify the response after the request received a response
		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			//when the response came, remove the loading message
			dismissProgress();
			
			if(request_url_error || xmlData==null){
//				Toast.makeText(getApplicationContext(), R.string.invalid_url, 1000).show();
				CheckingConnectionDialog ccd = new CheckingConnectionDialog();
				ccd.show(getFragmentManager(), "checking_connection_dialog");
				
			}			
			
			try {
	            //parse the response
	            Serializer serializer = new Persister();
	            
	            Reader reader = new StringReader(xmlData);
	            XEResponse response = 
	                serializer.read(XEResponse.class, reader, false);
	            
	            registerForPushNotification();
	            
	            //check if the response was positive
	            if( response.value.equals("true") )
	            {	            	
	            	//Write site data to database 
	            	XEDatabaseHelper dbHelper = XEDatabaseHelper.getDBHelper(getApplicationContext());
	            	SQLiteDatabase db = dbHelper.getReadableDatabase();
	            	String[] args = {getURL()};
	            	Cursor cursor = db.rawQuery("SELECT count(*) countUrl FROM " + dbHelper.XE_SITES + " WHERE " + dbHelper.XE_SITES_SITEURL + "=?" , args);
	            	cursor.moveToFirst();
	            	int urlCount = cursor.getInt(0);
	            	cursor.close();
	            	db.close();	            	
	            	if(urlCount==0){	
		            	db = dbHelper.getWritableDatabase();	
		            	ContentValues values = new ContentValues();
						values.put(dbHelper.XE_SITES_SITEURL, getURL());
						values.put(dbHelper.XE_SITES_PASSWORD, passwordEditText.getText().toString());
						values.put(dbHelper.XE_SITES_USERNAME, usernameEditText.getText().toString());						
						long affectedRows = db.insert(dbHelper.XE_SITES, null, values);
						
//						values = new ContentValues();
//						values.put(dbHelper.XE_SITES_SITEURL, "http://192.168.10.175/xe-core2/trunk");
//						values.put(dbHelper.XE_SITES_PASSWORD, "12345");
//						values.put(dbHelper.XE_SITES_USERNAME, "leapkh");
//						
//						affectedRows = db.insert(dbHelper.XE_SITES, null, values);
						
						db.close();
						Log.i("xemobile","add logged site to database "+affectedRows);
	            	}	
				
					//call dash board activity 
					Intent callDashboard = new Intent(XEMobileLoginController.this,XEMobileMainActivityController.class);
            		startActivity(callDashboard);
					//Add site id to default shared preference 
            		
            		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplication());
            		Editor prefEditor = pref.edit();
            		prefEditor.putString("ACTIVE_SITE", getURL());
            		prefEditor.commit();
            		
//	            	Intent intent = new Intent(XEMobileLoginController.this,XEMobileMainPageController.class);
//	            	startActivity(intent);
	            }
	            else
	            {
	            	//Toast toast = Toast.makeText(getApplicationContext(), "Wrong password!", Toast.LENGTH_SHORT);
	            	//toast.show();
	            	
	            	
	            	WrongPasswordDialog wpd = new WrongPasswordDialog();
	            	wpd.show(getFragmentManager(),"wrong_password_dialog");
	            	
	            	
	            }
	        } 
	        catch (Exception e) 
	        {
	        	e.printStackTrace();
	        }
		}

	  }
    
    public void registerForPushNotification()
    {
    	if (GCMRegistrar.isRegistered(this)) 
    	{
			Log.d("info", GCMRegistrar.getRegistrationId(this));
		}
		    	
		final String regId = GCMRegistrar.getRegistrationId(this);
		
		if (regId.equals("")) 
		{
			// replace this with the project ID
			GCMRegistrar.register(this, "946091851170");
			Log.d("info", GCMRegistrar.getRegistrationId(this));
		} else 
		{
			Log.d("info", "already registered as" + regId);
		}
    }
}
