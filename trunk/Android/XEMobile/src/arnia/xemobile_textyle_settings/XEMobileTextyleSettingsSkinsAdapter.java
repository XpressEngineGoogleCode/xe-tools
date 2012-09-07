package arnia.xemobile_textyle_settings;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEPage;
import arnia.xemobile.classes.XESkin;
import arnia.xemobile.classes.XETextylePage;

//Adapter for the ListView with XESkins
public class XEMobileTextyleSettingsSkinsAdapter extends BaseAdapter
{
	private ArrayList<XESkin> skins;
	private ArrayList<Bitmap> images;
	private Context context;
	
	// the id of the selected skin
	private String selectedSkinID;
	
	//setter
	public void setSelectedSkin(String selectedSkin) 
	{
		this.selectedSkinID = selectedSkin;
	}
	
	//setter
	public void setSkins(ArrayList<XESkin> skins) 
	{
		this.skins = skins;
		
		GetBitmapImageAsyncTask task = new GetBitmapImageAsyncTask();
		task.execute();
	}
	
	//constructor
	public XEMobileTextyleSettingsSkinsAdapter(Context context)
	{
		this.context = context;
		skins = new ArrayList<XESkin>();
		images = new ArrayList<Bitmap>();
		

	}
	
	@Override
	public int getCount() 
	{
		return skins.size();
	}

	@Override
	public Object getItem(int arg0) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) 
	{
		XESkin page = skins.get(position);

		if( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.xemobiletextyleskinsitemcellview, null);
		}
		
		ImageView imageView = (ImageView) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_SKINSSETTINGS_IMAGEVIEW);
		if( images.size() > position ) imageView.setImageBitmap(images.get(position));
		
		TextView textView = (TextView) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_SKINSSETTINGS_NAME);
		textView.setText(page.name);
		
		CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_SKINSSETTINGS_CHECKBOX);
		
		checkBox.setTag(position);
		// check if the skin is selected or not
		if( page.id.equals(selectedSkinID) ) 
			{
				checkBox.setChecked(true);
			}
		else checkBox.setChecked(false);
		checkBox.setOnCheckedChangeListener((OnCheckedChangeListener) context);
		
		
		return convertView;
	}
	
	//Async Task for loading all the images for skins
	private class GetBitmapImageAsyncTask extends AsyncTask<String, Object, Bitmap>
	{
		
		@Override
		protected Bitmap doInBackground(String... params) 
		{
			//make the request for each XESkin
			//add the response in the array "images"
			for( XESkin skin : skins )
			{
			
			try {
				 Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(XEHost.getINSTANCE().getURL()+ skin.getSmall_ss()).getContent()); 
				  images.add(bitmap);
				} catch (MalformedURLException e) {
				  e.printStackTrace();
				} catch (IOException e) {
				  e.printStackTrace();
				}
			}
				return null;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) 
		{
			super.onPostExecute(result);
			//notify the adapter that the data has changed
			notifyDataSetChanged();
		}
		
	}
	
}
