package arnia.xemobile;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEResponse;


public class XEMobileLoginController extends XEActivity implements OnClickListener{
    //ui elements
    private EditText addressEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xemobileloginlayout);
    
        //take reference to the UI elements
        addressEditText = (EditText) findViewById(R.id.XEMOBILE_LOGIN_ADDRESS);
        usernameEditText = (EditText) findViewById(R.id.XEMOBILE_LOGIN_USERNAME);
        passwordEditText = (EditText) findViewById(R.id.XEMOBILE_LOGIN_PASSWORD);
        rememberMeCheckBox = (CheckBox) findViewById(R.id.XEMOBILE_LOGIN_REMEMBERME);
        
//        //ONLY for testing!
//        addressEditText.setText("smilingmouse.ro/xe2");
//        usernameEditText.setText("vlad.bogdan@me.com");
//        passwordEditText.setText("prince");
        
        //if "Remember me" is checked, the username, password and url are saved in SharedPreferences
        //when the Activity is created they are putted on EditTexts. 
        
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
       
        addressEditText.setText(pref.getString("url", ""));
        usernameEditText.setText(pref.getString("username", ""));
        passwordEditText.setText(pref.getString("password", ""));
        
        rememberMeCheckBox.setChecked(pref.getBoolean("checked", false));
        
        
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
		if(rememberMeCheckBox.isChecked())
		{
			prefsEditor.putBoolean("checked", true);
			prefsEditor.putString("username", usernameEditText.getText().toString());
			prefsEditor.putString("password", passwordEditText.getText().toString());
			prefsEditor.putString("url", addressEditText.getText().toString());
			prefsEditor.commit();
		}
		else
		{
			prefsEditor.putBoolean("checked", false);
			prefsEditor.putString("username", "");
			prefsEditor.putString("password", "");
			prefsEditor.putString("url", "");
			prefsEditor.commit();
		}
		
        startProgress("Waiting...");
		LogInInBackground task = new LogInInBackground();
		task.execute();
	}
    
	//AsyncTask for LogIn
    private class LogInInBackground extends AsyncTask<Void, Void, Void> 
    {
    	String xmlData;
    	
    	//send the request in background
		@Override
		protected Void doInBackground(Void... params) 
		{
			// set address in XEHost singleton
			XEHost.getINSTANCE().setURL("http://" + addressEditText.getText().toString());
			
			xmlData = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="+ usernameEditText.getText().toString() + "&password=" + passwordEditText.getText().toString());
	    	
			return null;
		}
		
		//verify the response after the request received a response
		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			//when the response came, remove the loading message
			dismissProgress();
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
	            	Intent intent = new Intent(XEMobileLoginController.this,XEMobileMainPageController.class);
	            	startActivity(intent);
	            }
	            else
	            {
	            	Toast toast = Toast.makeText(getApplicationContext(), "Wrong password!", Toast.LENGTH_SHORT);
	            	toast.show();
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
