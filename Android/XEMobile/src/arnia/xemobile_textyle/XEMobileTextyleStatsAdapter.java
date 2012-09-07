package arnia.xemobile_textyle;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.classes.XETextyleStats;

//Adapter for 
public class XEMobileTextyleStatsAdapter extends BaseAdapter
{
	
	private XETextyleStats stats;
	private Context context;
	
	//constructor
	public XEMobileTextyleStatsAdapter(Context context)
	{
		stats = new XETextyleStats();
		this.context = context;
		this.stats = stats;
	}
	
	//setter
	public void setStats(XETextyleStats stats) 
	{
		this.stats = stats;
	}
	
	@Override
	public int getCount() 
	{
		return 7;
	}

	@Override
	public Object getItem(int arg0) 
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
		if( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.xemobiletextylestatsitemcellview, null);
		}
		
		TextView date = (TextView) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_STATISTICSADAPTER_DATE);
		TextView pageVisitors = (TextView) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_STATISTICSADAPTER_PAGEVISITORS);
		
		switch (position) 
		{
		case 0:
			date.setText("Monday");
			pageVisitors.setText(stats.monday);
			break;

		case 1:
			date.setText("Tuesday");
			pageVisitors.setText(stats.tuesday);
			break;
		case 2:
			date.setText("Wednesday");
			pageVisitors.setText(stats.wednesday);
			break;
		case 3:
			date.setText("Thursday");
			pageVisitors.setText(stats.thursday);
			break;
		case 4:
			date.setText("Friday");
			pageVisitors.setText(stats.friday);
			break;
		case 5:
			date.setText("Saturday");
			pageVisitors.setText(stats.saturday);
			break;
		case 6:
			date.setText("Sunday");
			pageVisitors.setText(stats.sunday);
			break;
		default:
			break;
		}
		
		return convertView;
	}

}