package arnia.xemobile_textyle;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XETextyle;

// Activity with a listView of Textyles
public class XEMobileTextyleSelectTextyleController extends XEActivity implements OnItemClickListener
{
	// listView
	private ListView listView;
	
	// array with Textyles
	private XEArrayList array;

	//adapter for array with Textyles
	private ArrayAdapter<XETextyle> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobiletextyleselectlayout);
		
		
		listView = (ListView) findViewById(R.id.XEMOBILE_TEXTYLE_SELECT_LISTVIEW);
		adapter = new ArrayAdapter<XETextyle>(this, android.R.layout.simple_list_item_1);
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	
		adapter.clear();
		
		//send request to get the array with textyles
		startProgress("Loading...");
		GetTextylesAsyncTask task = new GetTextylesAsyncTask();
		task.execute();
	}
	
	// Async Task for sending the request 
	private class GetTextylesAsyncTask extends AsyncTask<Object, Object, Object>
	{
		String response;
		
		@Override
		protected Object doInBackground(Object... params) 
		{
			//sending the request
			response = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationTextyleList");
			
			//parsing the response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			try
			{
				array = serializer.read(XEArrayList.class, reader,false);
			}catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return null;
		}
		
		//called when the response came
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			
			//check if the user is logged in
			isLoggedIn(response, XEMobileTextyleSelectTextyleController.this);
			
			dismissProgress();
			if( array != null && array.textyles != null)
			{
				for(int i = 0; i<array.textyles.size();i++)
				{
					adapter.add(array.textyles.get(i));
				}
				adapter.notifyDataSetChanged();
			}else{
				Toast.makeText(getApplicationContext(), R.string.no_textyle, 1000).show();
			}
		}
	}

	// method called when an item of list view is clicked
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) 
	{
		XETextyle selectedTextyle = array.textyles.get(pos);
	
		//starting new activity
		Intent intent = new Intent(XEMobileTextyleSelectTextyleController.this,XEMobileTextyleMainPageController.class);
		//passing the textyle object
		intent.putExtra("textyle", selectedTextyle);
		startActivity(intent);
	}
}
