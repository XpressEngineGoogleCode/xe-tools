package arnia.xemobile_textyle_pages;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.classes.XETextylePage;

public class XEMobileTextylePagesAdapter extends BaseAdapter
{
	private ArrayList<XETextylePage> pages;
	private Context context;
	
	public void setPages(ArrayList<XETextylePage> pages) 
	{
		if(pages != null) this.pages = pages;
		else this.pages = new ArrayList<XETextylePage>();
	}
	
	public XEMobileTextylePagesAdapter(Context context)
	{
		this.pages = new ArrayList<XETextylePage>();
		this.context = context;
	}
	
	@Override
	public int getCount() 
	{
		return this.pages.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		XETextylePage page = pages.get(position);
		if( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.xemobiletextylepageitemcellview, null);
		}
		
		TextView nameTextView = (TextView) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_PAGESADAPTER_PAGENAME);
		
		nameTextView.setText(page.name);
		
		Button button = (Button) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_PAGESADAPTER_DELETEBUTTON);
		button.setOnClickListener((OnClickListener) context);
		button.setTag(position);
		
		return convertView;
	}

}
