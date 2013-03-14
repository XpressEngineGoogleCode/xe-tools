package arnia.xemobile_textyle_posts;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.classes.XETextylePost;

public class XEMobileTextylePostsController extends XEActivity implements
		OnCheckedChangeListener, OnClickListener, OnItemClickListener {
	// UI references
//	private RadioButton savedPostsOption;
	private RadioButton publishedPostsOption;
	private ListView listView;
	private XEMobileTextylePostAdapter adapter;
//	private Button addPostButton;

	// array lists with saved posts and published posts
	private XEArrayList savedPosts;
	private XEArrayList publishedPosts;

	private XETextyle textyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobiletextylepostslayout);
		
		//take references to UI elements
//		savedPostsOption = (RadioButton) findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_SAVEDOPTION);
		publishedPostsOption= (RadioButton) findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_PUBLISHEDOPTION);
//		addPostButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_ADDPOST);
//		addPostButton.setOnClickListener(this);
		
		
//		savedPostsOption.setChecked(true);
//		savedPostsOption.setOnCheckedChangeListener(this);
		publishedPostsOption.setOnCheckedChangeListener(this);
		

		// take references to UI elements
//		savedPostsOption = (RadioButton) findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_SAVEDOPTION);
//		publishedPostsOption = (RadioButton) findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_PUBLISHEDOPTION);
//		addPostButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_ADDPOST);
//		addPostButton.setOnClickListener(this);

//		savedPostsOption.setChecked(true);
//		savedPostsOption.setOnCheckedChangeListener(this);
//		publishedPostsOption.setOnCheckedChangeListener(this);

		listView = (ListView) findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_LISTVIEW);

		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		adapter = new XEMobileTextylePostAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		
//		savedPostsOption.setOnCheckedChangeListener(this);

//		savedPostsOption.setOnCheckedChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		startProgress("Loading...");

		// async task for saved posts
		GetSavedPostsAsyncTask savedTask = new GetSavedPostsAsyncTask();
		savedTask.execute();
	}

	// the method is called when the "Saved" or "Published" button is pressed
	@Override	
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//		if (buttonView == savedPostsOption && savedPosts != null) {
//			if (isChecked) {
//				setTheSavedPostsInAdapter();
//			}
//		} else if (buttonView == publishedPostsOption && publishedPosts != null) {
//			if (isChecked) {
//				setThePublishedPostsInAdapter();
//			}
//		}
	}

	// async task for saved posts
	private class GetSavedPostsAsyncTask extends
			AsyncTask<String, Object, Object> {
		String response;

		@Override
		protected Object doInBackground(String... params) {

			response = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication"
									+ "&act=procmobile_communicationTextylePostList&module_srl="
									+ textyle.module_srl + "&published=-1");

			Serializer serializer = new Persister();

			Reader reader = new StringReader(response);
			try {
				savedPosts = serializer.read(XEArrayList.class, reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			
			if(isLoggedIn(response, XEMobileTextylePostsController.this))
			{
				if( savedPosts.posts == null )
				{
					savedPosts.posts = new ArrayList<XETextylePost>();
				
				}
//				if( savedPostsOption.isChecked() ) setTheSavedPostsInAdapter();
			

			if (isLoggedIn(response, XEMobileTextylePostsController.this))
				dismissProgress();
			if (savedPosts != null && savedPosts.posts != null) {
				adapter.setArrayWithPosts(savedPosts.posts);
				adapter.notifyDataSetChanged();
			}

		}
	}

	// async task for published posts
	private class GetPublishedPostsAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String response = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication"
									+ "&act=procmobile_communicationTextylePostList&module_srl="
									+ textyle.module_srl + "&published=1");

			Log.d("response", response);

			Serializer serializer = new Persister();

			Reader reader = new StringReader(response);
			try {
				publishedPosts = serializer.read(XEArrayList.class, reader,
						false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if (publishedPosts.posts == null)
				publishedPosts.posts = new ArrayList<XETextylePost>();
			if (publishedPostsOption.isChecked())
				setThePublishedPostsInAdapter();
		}
	}

	public void setTheSavedPostsInAdapter() {
		// adapter.clear();

		if (savedPosts.posts.size() == 0) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No saved posts!", Toast.LENGTH_SHORT);
			toast.show();
		}

		for (int i = 0; i < savedPosts.posts.size(); i++) {
			// adapter.add(savedPosts.posts.get(i));
		}
		adapter.notifyDataSetChanged();
	}

	public void setThePublishedPostsInAdapter() {
		// adapter.clear();

		if (publishedPosts.posts.size() == 0) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No published posts!", Toast.LENGTH_SHORT);
			toast.show();
		}

		for (int i = 0; i < publishedPosts.posts.size(); i++) {
			// adapter.add(publishedPosts.posts.get(i));
		}
		adapter.notifyDataSetChanged();
	}

	// called when the "Add post" button is pressed
//	@Override
//	public void onClick(View v) {
//		Intent intent = new Intent(XEMobileTextylePostsController.this,
//				XEMobileTextyleAddPostController.class);
//		intent.putExtra("textyle", textyle);
//		startActivity(intent);
//	}
//
//	// called when an item form ListView is pressed
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		XETextylePost post = null;
//		Intent intent = new Intent(XEMobileTextylePostsController.this, XEMobileTextyleEditPostContentController.class);
//		
////		if( savedPostsOption.isChecked() )
////		{
////			post = savedPosts.posts.get(arg2);
////			intent.putExtra("type", "saved");
////			
////		}
////		else if( publishedPostsOption.isChecked() )
////		{
////			post = publishedPosts.posts.get(arg2);
////			intent.putExtra("type", "published");
////		}
//		Intent intent = new Intent(XEMobileTextylePostsController.this,
//				XEMobileTextyleEditPostContentController.class);
//
//		if (savedPostsOption.isChecked()) {
//			post = savedPosts.posts.get(arg2);
//			intent.putExtra("type", "saved");
//
//		} else if (publishedPostsOption.isChecked()) {
//			post = publishedPosts.posts.get(arg2);
//			intent.putExtra("type", "published");
//		}
//
//		intent.putExtra("textyle", textyle);
//		intent.putExtra("document_srl", post.document_srl);
//		intent.putExtra("title", post.title);
//		intent.putExtra("category_srl", post.category_srl);
//		startActivity(intent);
//	}
}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}