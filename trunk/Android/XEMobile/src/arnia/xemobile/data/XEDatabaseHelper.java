package arnia.xemobile.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class XEDatabaseHelper extends SQLiteOpenHelper {
	
	private final static String DB_NAME = "xemobile";
	
	private final static int VERSION =1;
	
	private static XEDatabaseHelper dbHelper = null;
	
	public final String XE_SITES = "xe_sites";
	public final String XE_SITES_SITEURL = "siteurl";
	public final String XE_SITES_USERNAME = "username";
	public final String XE_SITES_PASSWORD = "password";
	
	public static XEDatabaseHelper getDBHelper(Context context){
		if (dbHelper==null){
			dbHelper = new XEDatabaseHelper(context, DB_NAME, null, VERSION);
		}
		return dbHelper;
	}
	
	private XEDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DB_NAME, factory, VERSION);
		
	}
		
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql;
		sql ="CREATE TABLE "+ XE_SITES +"(_id integer auto increment,siteurl TEXT,username TEXT,password TEXT)";
		db.execSQL(sql);
		
		Log.i("xe_mobile", "Create database");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
