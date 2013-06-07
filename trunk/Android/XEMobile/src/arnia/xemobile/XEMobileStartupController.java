package arnia.xemobile;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import arnia.xemobile.data.XEDatabaseHelper;

public class XEMobileStartupController extends XEActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		XEDatabaseHelper dbHelper = XEDatabaseHelper.getDBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) totalWebsite from "
				+ dbHelper.XE_SITES, null);
		cursor.moveToFirst();
		int recordCount = cursor.getInt(0);
		cursor.close();
		db.close();
		if (recordCount > 0) {
			Intent callDashboard = new Intent(this,
					XEMobileMainActivityController.class);
			startActivity(callDashboard);

		} else {
			Intent callAddNewSite = new Intent(this,
					XEMobileWelcomeScreenController.class);
			startActivity(callAddNewSite);
		}
		finish();
	}
}
