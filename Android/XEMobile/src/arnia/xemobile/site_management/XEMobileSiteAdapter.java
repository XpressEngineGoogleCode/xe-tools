package arnia.xemobile.site_management;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.XEFragment;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEResponse;
import arnia.xemobile.data.XEDatabaseHelper;
import arnia.xemobile.data.XEMobileSite;

public class XEMobileSiteAdapter extends BaseAdapter {
	// array with pages that appear in listview
	private ArrayList<XEMobileSite> sites;

	private Activity context;

	public XEMobileSiteAdapter(Activity context) {
		sites = new ArrayList<XEMobileSite>();
		this.context = context;
	}

	public XEMobileSiteAdapter(Activity context, ArrayList<XEMobileSite> sites) {
		this.sites = sites;
		this.context = context;
	}

	@Override
	public int getCount() {
		return sites.size();
	}

	@Override
	public Object getItem(int index) {
		return sites.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		// get the page from the array
		final XEMobileSite site = sites.get(pos);
		final int position = pos;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.xemobilesitecellview, null);
		}

		// construct the view's elements
		Button btnEdit = (Button) convertView
				.findViewById(R.id.XEMOBILE_SITE_EDIT_BUTTON);
		Button btnDelete = (Button) convertView
				.findViewById(R.id.XEMOBILE_SITE_DELETE_BUTTON);

		TextView txtUrl = (TextView) convertView
				.findViewById(R.id.XEMOBILE_SITE_URL);
		txtUrl.setText(site.siteUrl);

		btnEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Builder addBox = new Builder(context);
				addBox.setTitle("Edit Site");
				final LinearLayout layout = new LinearLayout(context);
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setPadding(10, 0, 10, 0);

				final EditText txtUrl = new EditText(context);
				txtUrl.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				txtUrl.setHint("URL");
				txtUrl.setText(site.siteUrl);

				final EditText txtUsername = new EditText(context);
				txtUsername.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				txtUsername.setHint("Username");
				txtUsername.setText(site.userName);

				final EditText txtPassword = new EditText(context);
				txtPassword.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				txtPassword
						.setTransformationMethod(PasswordTransformationMethod
								.getInstance());
				txtPassword.setHint("Password");

				layout.addView(txtUrl);
				layout.addView(txtUsername);
				layout.addView(txtPassword);

				addBox.setView(layout);
				addBox.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String url = txtUrl.getText().toString().trim();
								String username = txtUsername.getText()
										.toString().trim();
								String password = txtPassword.getText()
										.toString().trim();
								XEMobileSite editedSite = new XEMobileSite(
										site.id, url, username, password);
								LogInInBackground task = new LogInInBackground(
										position);
								task.execute(editedSite);
							}
						});
				addBox.show();
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Builder confirmDialog = new Builder(context);
				confirmDialog.setTitle("Delete Site");
				confirmDialog.setMessage("Do you want to delete this site?");
				confirmDialog.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								XEDatabaseHelper dbHelper = XEDatabaseHelper
										.getDBHelper(context);
								SQLiteDatabase db = dbHelper
										.getReadableDatabase();
								db.delete(dbHelper.XE_SITES,
										dbHelper.XE_SITES_ID + "=" + site.id,
										null);
								sites.remove(site);
								notifyDataSetChanged();
							}
						});
				confirmDialog.setNegativeButton("No", null);
				confirmDialog.show();
			}
		});

		return convertView;
	}

	public void addItem(XEMobileSite site) {
		sites.add(site);
	}

	private class LogInInBackground extends AsyncTask<XEMobileSite, Void, Void> {
		private String xmlData;
		private XEMobileSite site;
		private int index;

		public LogInInBackground(int index) {
			this.index = index;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			XEFragment.startProgress(context, "Validating...");
		}

		// send the request in background
		@SuppressWarnings("finally")
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
				Log.i("leapkh", "Response: " + response.value);
				if (response.value.equals("true")) {
					// Write site data to database
					XEDatabaseHelper dbHelper = XEDatabaseHelper
							.getDBHelper(context);
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
					if (urlCount == 1) {
						db = dbHelper.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put(dbHelper.XE_SITES_SITEURL, site.siteUrl);
						values.put(dbHelper.XE_SITES_PASSWORD, site.password);
						values.put(dbHelper.XE_SITES_USERNAME, site.userName);
						long affectedRows = db.update(dbHelper.XE_SITES,
								values, dbHelper.XE_SITES_ID + "=" + site.id,
								null);
						db.close();
						Log.i("xemobile", "add logged site to database "
								+ affectedRows);
						sites.set(index, site);
						notifyDataSetChanged();
					}
				} else {
					Toast toast = Toast.makeText(context,
							"Incorrect username or password!",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
