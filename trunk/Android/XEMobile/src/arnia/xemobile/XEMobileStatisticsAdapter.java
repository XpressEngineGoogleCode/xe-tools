package arnia.xemobile;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import arnia.xemobile.classes.XEDayStats;

//Adapter for Statistics ListView
public class XEMobileStatisticsAdapter extends BaseAdapter
{
	//array with XEDayStats
	private ArrayList<XEDayStats> array;
	private Context context;
	
	public XEMobileStatisticsAdapter(ArrayList<XEDayStats> array, Context context)
	{
		this.array = array;
		this.context = context;
	}
	
	@Override
	public int getCount() 
	{
		return this.array.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return null;
	}

	@Override
	public long getItemId(int position) 
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		//get the Day Statistics from the array
		XEDayStats day = array.get(position);
		
		if( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.xemobilestatisticsitemcellview, null);
		}
		
		TextView dayTextView = (TextView) convertView.findViewById(R.id.XEMOBILE_STATISTICSADAPTER_DATE);
		TextView uniqueVisitorsTextView = (TextView) convertView.findViewById(R.id.XEMOBILE_STATISTICSADAPTER_UNIQUEVISITORS);
		TextView pageVisitorsTextView = (TextView) convertView.findViewById(R.id.XEMOBILE_STATISTICSADAPTER_PAGEVISITORS);
		
		
		//set the statistics on TextViews
		dayTextView.setText( day.date );
		uniqueVisitorsTextView.setText( day.unique_visitor );
		pageVisitorsTextView.setText( day.pageview );
		
		return convertView;
	}

}
