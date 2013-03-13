package arnia.xemobile_textyle_posts;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.classes.XEPage;
import arnia.xemobile.classes.XETextylePost;

public class XEMobileTextylePostAdapter extends BaseAdapter {
	// array with pages that appear in listview
	private ArrayList<XETextylePost> arrayWithPosts;

	private Activity context;

	public void setArrayWithPosts(ArrayList<XETextylePost> arrayWithPosts) {
		this.arrayWithPosts = arrayWithPosts;
	}

	public XEMobileTextylePostAdapter(Activity context) {
		arrayWithPosts = new ArrayList<XETextylePost>();
		this.context = context;
	}

	@Override
	public int getCount() {
		return arrayWithPosts.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		// get the page from the array
		XETextylePost post = arrayWithPosts.get(pos);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.xemobiletextylepostitemcellview, null);
		}

		// construct the view's elements
		TextView menuItemTitleTextView = (TextView) convertView
				.findViewById(R.id.XEMOBILE_POST_POST_TITLE);
		menuItemTitleTextView.setText(post.title);

		TextView menuItemCommentTextView = (TextView) convertView
				.findViewById(R.id.XEMOBILE_POST_POST_COMMENT);
		//menuItemCommentTextView.setText(post.title);

//		Button deleteButton = (Button) convertView
//				.findViewById(R.id.XEMOBILE_PAGEITEMCELL_DELETEBUTTON);
//		deleteButton.setTag(pos);
//		deleteButton.setOnClickListener((OnClickListener) context);

		// return the view
		return convertView;
	}

}
