package arnia.xemobile;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.ContentValues;
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
import arnia.xemobile.controls.CustomDialog;
import arnia.xemobile.data.XEDatabaseHelper;

import com.google.android.gcm.GCMRegistrar;

public class XEMobileLoginController extends XEActivity implements
		OnClickListener {

	private EditText addressEditText;
	private EditText usernameEditText;
	private EditText passwordEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobileloginlayout);

		// take reference to the UI elements
		addressEditText = (EditText) findViewById(R.id.XEMOBILE_LOGIN_ADDRESS);
		usernameEditText = (EditText) findViewById(R.id.XEMOBILE_LOGIN_USERNAME);
		passwordEditText = (EditText) findViewById(R.id.XEMOBILE_LOGIN_PASSWORD);

		Button loginButton = (Button) findViewById(R.id.XEMOBILE_LOGIN_BUTTON);
		loginButton.setOnClickListener(this);

	}

	// called when login button is pressed
	@Override
	public void onClick(View v) {
		String websiteUrl = addressEditText.getText().toString().trim();
		String username = usernameEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();

		// Validate input
		if (websiteUrl.length() == 0 || username.length() == 0
				|| password.length() == 0) {
			final CustomDialog invalidInputDialog = new CustomDialog(
					XEMobileLoginController.this);
			invalidInputDialog.setIcon(R.drawable.ic_warning);
			invalidInputDialog.setTitle(R.string.login_invalid_input_title);
			invalidInputDialog.setMessage(R.string.login_invalid_input_msg);
			invalidInputDialog.setPositiveButton(getString(R.string.close),
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							invalidInputDialog.dismiss();
						}
					});
			invalidInputDialog.show();
		} else {
			websiteUrl = getURL(websiteUrl);
			LogInInBackground task = new LogInInBackground();
			task.execute(websiteUrl, username, password);
		}
	}

	private String getURL(String url) {
		if (!url.contains("http://")) {
			url = "http://" + url;
		}
		return url;
	}

	// AsyncTask for LogIn
	private class LogInInBackground extends AsyncTask<String, Void, Void> {

		private String websiteUrl;
		private String username;
		private String password;

		private String xmlData;
		private boolean request_url_error = false;
		private CustomDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new CustomDialog(XEMobileLoginController.this);
			dialog.setTitle(R.string.checking_connection_dialog_title);
			dialog.setMessage(R.string.checking_connection_dialog_description);
			dialog.show();
		}

		// send the request in background
		@Override
		protected Void doInBackground(String... params) {
			websiteUrl = params[0];
			username = params[1];
			password = params[2];
			try {
				// set address in XEHost singleton
				XEHost.getINSTANCE().setURL(websiteUrl);

				xmlData = XEHost
						.getINSTANCE()
						.getRequest(
								"/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="
										+ username + "&password=" + password);

			} catch (Exception e) {
				e.printStackTrace();
				request_url_error = true;
			}
			return null;
		}

		// verify the response after the request received a response
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss();

			if (request_url_error || xmlData == null) {
				dialog = new CustomDialog(XEMobileLoginController.this);
				dialog.setIcon(R.drawable.ic_warning);
				dialog.setTitle(R.string.error);
				dialog.setMessage(R.string.login_error_msg);
				dialog.setPositiveButton(getString(R.string.close),
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
				dialog.show();
			}

			try {
				// parse the response
				Serializer serializer = new Persister();

				Reader reader = new StringReader(xmlData);
				XEResponse response = serializer.read(XEResponse.class, reader,
						false);

				registerForPushNotification();

				// check if the response was positive
				if (response.value.equals("true")) {
					// Write site data to database
					XEDatabaseHelper dbHelper = XEDatabaseHelper
							.getDBHelper(getApplicationContext());
					SQLiteDatabase db = dbHelper.getReadableDatabase();
					String[] args = { websiteUrl };
					Cursor cursor = db.rawQuery(
							"SELECT count(*) countUrl FROM "
									+ dbHelper.XE_SITES + " WHERE "
									+ dbHelper.XE_SITES_SITEURL + "=?", args);
					cursor.moveToFirst();
					int urlCount = cursor.getInt(0);
					cursor.close();
					db.close();
					if (urlCount == 0) {
						db = dbHelper.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put(dbHelper.XE_SITES_SITEURL, websiteUrl);
						values.put(dbHelper.XE_SITES_PASSWORD, password);
						values.put(dbHelper.XE_SITES_USERNAME, username);
						long affectedRows = db.insert(dbHelper.XE_SITES, null,
								values);

						db.close();
						Log.i("xemobile", "add logged site to database "
								+ affectedRows);
					}

					// Add site id to default shared preference
					SharedPreferences pref = PreferenceManager
							.getDefaultSharedPreferences(getApplication());
					Editor prefEditor = pref.edit();
					prefEditor.putString("ACTIVE_SITE", websiteUrl);
					prefEditor.commit();

					// call dash board activity
					Intent callDashboard = new Intent(
							XEMobileLoginController.this,
							XEMobileMainActivityController.class);
					startActivity(callDashboard);
					finish();
				} else {
					// Alert wrong password
					dialog = new CustomDialog(XEMobileLoginController.this);
					dialog.setPositiveButton(getString(R.string.close),
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									dialog.dismiss();
								}
							});
					dialog.setIcon(R.drawable.ic_warning);
					dialog.setTitle(R.string.wrong_password_dialog_title);
					dialog.setMessage(R.string.wrong_password_dialog_description);
					dialog.show();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void registerForPushNotification() {
		if (GCMRegistrar.isRegistered(this)) {
			Log.d("info", GCMRegistrar.getRegistrationId(this));
		}

		final String regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) {
			// replace this with the project ID
			GCMRegistrar.register(this, "946091851170");
			Log.d("info", GCMRegistrar.getRegistrationId(this));
		} else {
			Log.d("info", "already registered as" + regId);
		}
	}
}
