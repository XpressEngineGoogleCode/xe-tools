package arnia.xemobile_textyle_posts;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.XEFragment;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XETextyle;

public class XEMobileTextylePostsController extends XEFragment implements
		OnCheckedChangeListener, OnClickListener, OnItemClickListener {
	// UI references
	private ListView listView;
	private XEMobileTextylePostAdapter adapter;
	private RadioButton rioAllPost;
	private RadioButton rioPublishPost;
	private RadioButton rioDratPost;
	private RadioButton rioTrashPost;

	// private Button addPostButton;

	private XEArrayList allPosts;
	private XEArrayList publishedPosts;
	private XEArrayList draftPosts;
	private XEArrayList trashPosts;

	private XETextyle textyle;

	// array with Textyles
	private XEArrayList array;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.xemobiletextylepostslayout,
				container, false);

		//UI reference
		listView = (ListView) view
				.findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_LISTVIEW);
		adapter = new XEMobileTextylePostAdapter(getActivity());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

		rioAllPost = (RadioButton) view
				.findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_ALLOPTION);
		rioAllPost.setOnCheckedChangeListener(this);
		rioPublishPost = (RadioButton) view
				.findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_PUBLISHEDOPTION);
		rioPublishPost.setOnCheckedChangeListener(this);
		rioDratPost = (RadioButton) view
				.findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_DRAFTSOPTION);
		rioDratPost.setOnCheckedChangeListener(this);
		rioTrashPost = (RadioButton) view
				.findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_TRASHOPTION);
		rioTrashPost.setOnCheckedChangeListener(this);
		
		//Post data
		allPosts=new XEArrayList();
		publishedPosts=new XEArrayList();
		draftPosts=new XEArrayList();
		trashPosts=new XEArrayList();

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		GetTextylesAsyncTask task = new GetTextylesAsyncTask();
		task.execute();
	}

	// the method is called when the "Saved" or "Published" button is pressed
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.XEMOBILE_TEXTYLE_POSTS_ALLOPTION:
			if (isChecked) {
				if (allPosts.posts.size() == 0) {
					Toast toast = Toast.makeText(getActivity(), "No posts!",
							Toast.LENGTH_SHORT);
					toast.show();
				}
				adapter.setArrayWithPosts(allPosts.posts);
			}
			break;

		case R.id.XEMOBILE_TEXTYLE_POSTS_PUBLISHEDOPTION:
			if (isChecked) {
				if (publishedPosts.posts.size() == 0) {
					Toast toast = Toast.makeText(getActivity(),
							"No published posts!", Toast.LENGTH_SHORT);
					toast.show();
				}
				adapter.setArrayWithPosts(publishedPosts.posts);
			}
			break;

		case R.id.XEMOBILE_TEXTYLE_POSTS_DRAFTSOPTION:
			if (isChecked) {
				if (draftPosts.posts.size() == 0) {
					Toast toast = Toast.makeText(getActivity(),
							"No draft posts!", Toast.LENGTH_SHORT);
					toast.show();
				}
				adapter.setArrayWithPosts(draftPosts.posts);
			}
			break;
		case R.id.XEMOBILE_TEXTYLE_POSTS_TRASHOPTION:
			if (isChecked) {
				if (trashPosts.posts.size() == 0) {
					Toast toast = Toast.makeText(getActivity(),
							"No trash posts!", Toast.LENGTH_SHORT);
					toast.show();
				}
				adapter.setArrayWithPosts(trashPosts.posts);
			}
			break;
		}
		adapter.notifyDataSetChanged();
	}

	// async task for saved posts
	private class GetAllPostsAsyncTask extends
			AsyncTask<String, Object, Object> {

		@Override
		protected Object doInBackground(String... params) {

			String response = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication"
									+ "&act=procmobile_communicationTextylePostList&module_srl="
									+ textyle.module_srl);

			Serializer serializer = new Persister();

			Reader reader = new StringReader(response);
			try {
				allPosts = serializer.read(XEArrayList.class, reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			dismissProgress();
			if (allPosts != null && allPosts.posts != null) {
				adapter.setArrayWithPosts(allPosts.posts);
				adapter.notifyDataSetChanged();

				// Load publish post
				GetPublishedPostsAsyncTask task = new GetPublishedPostsAsyncTask();
				task.execute();

				// Load draft post
				GetDraftPostsAsyncTask task2 = new GetDraftPostsAsyncTask();
				task2.execute();

				// Load trash post
				GetTrashPostsAsyncTask task3=new GetTrashPostsAsyncTask();
				task3.execute();
			}
		}
	}

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
	}

	private class GetDraftPostsAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String response = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication"
									+ "&act=procmobile_communicationTextylePostList&module_srl="
									+ textyle.module_srl + "&published=-1");

			Log.d("response", response);

			Serializer serializer = new Persister();

			Reader reader = new StringReader(response);
			try {
				draftPosts = serializer.read(XEArrayList.class, reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	private class GetTrashPostsAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String response = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication"
									+ "&act=procmobile_communicationTextylePostList&module_srl="
									+ textyle.module_srl + "&published=-11");

			Log.d("response", response);

			Serializer serializer = new Persister();

			Reader reader = new StringReader(response);
			try {
				trashPosts = serializer.read(XEArrayList.class, reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	// Async Task for sending the request
	private class GetTextylesAsyncTask extends
			AsyncTask<Object, Object, Object> {
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startProgress("Loading...");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// sending the request
			response = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationTextyleList");

			// parsing the response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			try {
				array = serializer.read(XEArrayList.class, reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		// called when the response came
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			// dismissProgress();
			// async task for saved posts
			textyle = array.textyles.get(0);
			GetAllPostsAsyncTask savedTask = new GetAllPostsAsyncTask();
			savedTask.execute();
		}
	}

}