package arnia.xemobile;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;

public class XEMobileStatisticsController extends Fragment
{
	//the ui references
	private XEArrayList array;
	private ListView listView;
	
	//adapter for listView
	private XEMobileStatisticsAdapter adapter;
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) 
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.xemobilestatisticslayout);
//		
//		
//		listView = (ListView) findViewById(R.id.XEMOBILE_STATISTICS_LISTVIEW);
//				
//		//start loading the statistics
//		startProgress("Loading...");
//		GetStatisticsAsyncTask task = new GetStatisticsAsyncTask();
//		task.execute(this);
//		
//	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.xemobilestatisticslayout,container,false);
		listView = (ListView) view.findViewById(R.id.XEMOBILE_STATISTICS_LISTVIEW);
		return view;
	}
		
	public void refreshStatistic(){
		GetStatisticsAsyncTask task = new GetStatisticsAsyncTask();
		task.execute(getActivity());
	}
	
	//Async task for loading the statistics
	private class GetStatisticsAsyncTask extends AsyncTask<Object, Object, Object>
	{
		String response;
		
		@Override
		protected Object doInBackground(Object... params) 
		{
			Context context = (Context) params[0];
			//making the request
			response = XEHost.getINSTANCE().getRequest("/index.php/index.php?module=mobile_communication&act=procmobile_communicationViewerData");
			
			//parsing the response
			Serializer serializer = new Persister();
			
			try
			{
				array = serializer.read(XEArrayList.class, response,false);
			}catch (Exception e) 
			{
			  e.printStackTrace();
			}
			
			return context;
		}
		
		@Override
		protected void onPostExecute(Object context) 
		{
			//check if the user is still logged in
//			isLoggedIn(response, XEMobileStatisticsController.this);
//			
//			dismissProgress();
			
			
			if( array != null && array.stats != null )
			{
			
			//when the response came and was parsed we set the adapter
			adapter = new XEMobileStatisticsAdapter(array.stats, (Context) context);
			listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			}

			super.onPostExecute(context);
		}
	}
}
