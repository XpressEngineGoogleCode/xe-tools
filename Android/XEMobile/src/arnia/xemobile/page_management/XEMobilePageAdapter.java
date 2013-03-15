package arnia.xemobile.page_management;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEPage;

public class XEMobilePageAdapter extends BaseAdapter
{
	//array with pages that appear in listview
	private ArrayList<XEPage> arrayWithPages;

	private Activity context;
	
	public void setArrayWithPages(ArrayList<XEPage> arrayWithPages) {
		this.arrayWithPages = arrayWithPages;
	}
	
	public XEMobilePageAdapter(Activity context)
	{
		arrayWithPages = new ArrayList<XEPage>();
		this.context = context;
	}
	
	@Override
	public int getCount() 
	{
		return arrayWithPages.size();
	}

	@Override
	public Object getItem(int arg0) 
	{
		return null;
	}

	@Override
	public long getItemId(int arg0) 
	{
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) 
	{
		//get the page from the array
		XEPage page = arrayWithPages.get(pos);
		
		if( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.xemobilepageitemcellview, null);
		}
		
		//construct the view's elements
		TextView menuItemNameTextView = (TextView) convertView.findViewById(R.id.XEMOBILE_PAGEITEMCELL_TEXTVIEW);
		menuItemNameTextView.setText(page.mid);
		
//		Button editButton = (Button) convertView.findViewById(R.id.XEMOBILE_PAGEITEMCELL_EDITBUTTON);
//		editButton.setTag(pos);
//		editButton.setOnClickListener((OnClickListener) context);
		// Button editButton = (Button)
		// convertView.findViewById(R.id.XEMOBILE_PAGEITEMCELL_EDITBUTTON);
		// editButton.setTag(pos);
		// editButton.setOnClickListener((OnClickListener) context);
		
		Button deleteButton = (Button) convertView.findViewById(R.id.XEMOBILE_PAGEITEMCELL_DELETEBUTTON);
		deleteButton.setTag(pos);
//		deleteButton.setOnClickListener((OnClickListener)context);
		
		//return the view
		return convertView;
	}

}
