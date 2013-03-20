package arnia.xemobile_textyle_posts;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import arnia.xemobile.R;
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
		final XETextylePost post = arrayWithPosts.get(pos);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.xemobiletextylepostitemcellview, null);
		}

		// construct the view's elements
		TextView txtPostTitle = (TextView) convertView
				.findViewById(R.id.XEMOBILE_POST_POST_TITLE);
		txtPostTitle.setText(post.title);
		// if(post.status.compareTo("PUBLISHED")==0)
		// txtPostTitle.setTextColor(Color.GREEN);
		// else if(post.status.compareTo("DRAFT")==0)
		// txtPostTitle.setTextColor(Color.YELLOW);
		// else
		// txtPostTitle.setTextColor(Color.RED);
		
		TextView txtCommentCount = (TextView) convertView
				.findViewById(R.id.XEMOBILE_POST_POST_COMMENT);
		txtCommentCount.setText(post.comment_count + " comments");
		
		Button btnViewPost=(Button)convertView.findViewById(R.id.XEMOBILE_POST_VIEW_POST);
		btnViewPost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(post.url));
				context.startActivity(browser);
			}
		});
		
		return convertView;
	}

}
