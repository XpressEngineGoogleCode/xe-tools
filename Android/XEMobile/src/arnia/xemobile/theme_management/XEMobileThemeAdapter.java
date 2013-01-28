package arnia.xemobile.theme_management;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XESkin;
import arnia.xemobile.classes.XETheme;

public class XEMobileThemeAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<XETheme> themes;
	private int userCheckPosition=-1;
	private ArrayList<Bitmap> imgThumbnails;
	
	public XEMobileThemeAdapter(Context context, ArrayList<XETheme> themes) {
		super();
		this.context = context;
		this.themes = themes;
	}
	
	public void setThemes(ArrayList<XETheme> themes){
		this.themes = themes;	
	}
	
	public void setThemesImage(ArrayList<Bitmap> imgThumbnails){
		this.imgThumbnails = imgThumbnails;
	}
	
	public void setUserCheckedPosition(int position){
		this.userCheckPosition =position;
	}
	
	@Override
	public int getCount() {		
		return this.themes.size();
	}

	@Override
	public Object getItem(int position) {		
		return this.themes.get(position);
	}

	@Override
	public long getItemId(int position) {		
		return position;
	}	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater li = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View themeItemView = li.inflate(R.layout.xemobilethemeitemlayout,null);
		
		
		//Get all control in the content xml
		ImageView themeImage =(ImageView) themeItemView.findViewById(R.id.XEMOBILE_THEME_ITEM_IMAGEVIEW);
		TextView themeName =(TextView) themeItemView.findViewById(R.id.XEMOBILE_THEME_ITEM_NAME);
		CheckBox themeCheckBox =(CheckBox) themeItemView.findViewById(R.id.XEMOBILE_THEME_ITEM_CHECKBOX);
		themeCheckBox.setTag(position);
		
		//Fill those content with data		
		XETheme currentTheme = this.themes.get(position);
		themeName.setText(currentTheme.toString());
		if(this.userCheckPosition==-1){
			if(currentTheme.selected_layout.compareTo("1")==0){
				themeCheckBox.setChecked(true);
			}else{
				themeCheckBox.setChecked(false);	
			}
		}else{
			if(this.userCheckPosition==position){
				themeCheckBox.setChecked(true);
			}
		}
		//Handle checkbox click
		
		themeCheckBox.setOnCheckedChangeListener((OnCheckedChangeListener) this.context);
		
		if(this.imgThumbnails.size()>position){
			themeImage.setImageBitmap(this.imgThumbnails.get(position));
		}
		
		
		return themeItemView;
	}
}
