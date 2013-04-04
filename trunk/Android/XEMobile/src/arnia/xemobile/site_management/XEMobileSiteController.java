package arnia.xemobile.site_management;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.XEFragment;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEResponse;
import arnia.xemobile.data.XEDatabaseHelper;
import arnia.xemobile.data.XEMobileSite;

public class XEMobileSiteController extends XEFragment implements
		OnClickListener {

	private ListView lvwSite;
	private XEMobileSiteAdapter adapter;
	private Button btnAdd;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.xemobilesitelayout,
				container, false);

		lvwSite = (ListView) fragmentView
				.findViewById(R.id.XEMOBILE_SITE_LISTVIEW);
		XEDatabaseHelper dbHelper = XEDatabaseHelper.getDBHelper(this.activity);
		adapter = new XEMobileSiteAdapter(getActivity(), dbHelper.getAllSites());

		lvwSite.setAdapter(adapter);

		btnAdd = (Button) fragmentView
				.findViewById(R.id.XEMOBILE_SITE_ADD_BUTTON);
		btnAdd.setOnClickListener(this);

		return fragmentView;
	}

	@Override
	public void onClick(View v) {

		Builder addBox = new Builder(getActivity());
		addBox.setTitle("New Site");
		final LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(10, 0, 10, 0);

		final EditText txtUrl = new EditText(getActivity());
		txtUrl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		txtUrl.setHint("URL");

		final EditText txtUsername = new EditText(getActivity());
		txtUsername.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		txtUsername.setHint("Username");

		final EditText txtPassword = new EditText(getActivity());
		txtPassword.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		txtPassword.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		txtPassword.setHint("Password");

		layout.addView(txtUrl);
		layout.addView(txtUsername);
		layout.addView(txtPassword);

		addBox.setView(layout);
		addBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String url = txtUrl.getText().toString().trim();
				String username = txtUsername.getText().toString().trim();
				String password = txtPassword.getText().toString().trim();
				XEMobileSite site = new XEMobileSite(0, url, username, password);
				LogInInBackground task = new LogInInBackground();
				task.execute(site);
			}
		});
		addBox.show();

	}

	private class LogInInBackground extends AsyncTask<XEMobileSite, Void, Void> {
		String xmlData;
		boolean request_url_error = false;
		XEMobileSite site;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			XEFragment.startProgress(activity, "Validating...");
		}

		// send the request in background
		@Override
		protected Void doInBackground(XEMobileSite... params) {
			site = params[0];
			try {
				// set address in XEHost singleton
				XEHost.getINSTANCE().setURL(site.siteUrl);

				xmlData = XEHost
						.getINSTANCE()
						.getRequest(
								"/index.php?module=mobile_communication&act=procmobile_communicationLogin&user_id="
										+ site.userName
										+ "&password="
										+ site.password);

			} catch (Exception e) {
				e.printStackTrace();
				request_url_error = true;
			} finally {
				return null;
			}
		}

		// verify the response after the request received a response
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// when the response came, remove the loading message
			XEFragment.dismissProgress();

			try {
				// parse the response
				Serializer serializer = new Persister();

				Reader reader = new StringReader(xmlData);
				XEResponse response = serializer.read(XEResponse.class, reader,
						false);

				// check if the response was positive
				if (response.value.equals("true")) {
					// Write site data to database
					XEDatabaseHelper dbHelper = XEDatabaseHelper
							.getDBHelper(getActivity());
					SQLiteDatabase db = dbHelper.getReadableDatabase();
					String[] args = { site.siteUrl };
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
						values.put(dbHelper.XE_SITES_SITEURL, site.siteUrl);
						values.put(dbHelper.XE_SITES_PASSWORD, site.password);
						values.put(dbHelper.XE_SITES_USERNAME, site.userName);
						long affectedRows = db.insert(dbHelper.XE_SITES, null,
								values);
						db.close();
						Log.i("leapkh", "add logged site to database "
								+ affectedRows);
					}
					adapter.addItem(site);
				} else {
					Toast toast = Toast.makeText(getActivity(),
							"Incorrect username or password!",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} catch (Exception e) {
				Log.i("leapkh","Add site error : " + e.getMessage());
				e.printStackTrace();
			}
		}

	}

}
