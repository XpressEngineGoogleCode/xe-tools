package arnia.xemobile_textyle_posts;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import arnia.xemobile.R;
import arnia.xemobile.SegmentedRadioGroup;
import arnia.xemobile.XEFragment;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEPagination;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.classes.XETextylePost;

public class XEMobileTextylePostsController extends XEFragment implements
		OnClickListener, OnItemClickListener, OnScrollListener,
		OnCheckedChangeListener {

	// UI references
	private ListView listView;
	private View footerView;
	private XEMobileTextylePostAdapter adapter;
	private SegmentedRadioGroup radioGroup;

	private XETextyle textyle;

	private XEArrayList[] postsArray;
	private boolean[] isTaskLoading;

	private final int POST_TYPE_ALL = 0;
	private final int POST_TYPE_PUBLISHED = 1;
	private final int POST_TYPE_DRAFT = 2;
	private final int POST_TYPE_TRASH = 3;

	private View fragmentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		fragmentView = inflater.inflate(R.layout.xemobiletextylepostslayout,
				container, false);

		postsArray = new XEArrayList[4];
		isTaskLoading = new boolean[4];

		// UI reference
		radioGroup = (SegmentedRadioGroup) fragmentView
				.findViewById(R.id.XEMOBILE_POST_FILTER);
		radioGroup.setOnCheckedChangeListener(this);

		GetTextylesAsyncTask task = new GetTextylesAsyncTask();
		task.execute();

		return fragmentView;
	}

	private void initialListView() {
		listView = (ListView) fragmentView
				.findViewById(R.id.XEMOBILE_TEXTYLE_POSTS_LISTVIEW);
		// Add loading bar to listview footer
		footerView = ((LayoutInflater) getActivity().getSystemService(
				Activity.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.xemobilegloballistviewloadingfooter, null, false);
		listView.addFooterView(footerView);
		adapter = new XEMobileTextylePostAdapter(getActivity());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnScrollListener(this);
	}

	// Async Task for sending the request
	private class GetTextylesAsyncTask extends
			AsyncTask<Void, Void, XEArrayList> {
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startProgress("Loading...");
		}

		@Override
		protected XEArrayList doInBackground(Void... params) {
			// sending the request
			response = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationTextyleList");

			// parsing the response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			try {
				XEArrayList tmpTextTyle = serializer.read(XEArrayList.class,
						reader, false);
				return tmpTextTyle;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		// called when the response came
		@Override
		protected void onPostExecute(XEArrayList result) {
			super.onPostExecute(result);
			dismissProgress();
			if (result != null && result.textyles.size() > 0)
				textyle = result.textyles.get(0);
			initialListView();
			radioGroup.check(R.id.XEMOBILE_TEXTYLE_POSTS_ALLOPTION);
		}
	}

	/**
	 * 
	 * @params: 0=page number
	 * 
	 */
	private class GetPostsAsycTask extends
			AsyncTask<Integer, Void, XEArrayList> {

		private int postType;

		public GetPostsAsycTask(int postType) {
			this.postType = postType;
			footerView.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			isTaskLoading[postType] = true;
		}

		@Override
		protected XEArrayList doInBackground(Integer... params) {
			int pageNumber = params.length == 0 ? 1 : params[0];
			Log.i("leapkh", "Loading post :" + postType + " page: "
					+ pageNumber);
			String requestUrl;
			switch (postType) {
			case POST_TYPE_ALL:
				requestUrl = "/index.php?module=mobile_communication"
						+ "&act=procmobile_communicationTextylePostList&module_srl="
						+ textyle.module_srl + "&page=" + pageNumber;
				break;

			case POST_TYPE_PUBLISHED:
				requestUrl = "/index.php?module=mobile_communication"
						+ "&act=procmobile_communicationTextylePostList&module_srl="
						+ textyle.module_srl + "&published=1&page="
						+ pageNumber;
				break;

			case POST_TYPE_DRAFT:
				requestUrl = "/index.php?module=mobile_communication"
						+ "&act=procmobile_communicationTextylePostList&module_srl="
						+ textyle.module_srl + "&published=-1&page="
						+ pageNumber;
				break;
			default:
				requestUrl = "/index.php?module=mobile_communication"
						+ "&act=procmobile_communicationTextylePostList&module_srl="
						+ textyle.module_srl + "&published=-11&page="
						+ pageNumber;
			}

			String response = XEHost.getINSTANCE().getRequest(requestUrl);

			Serializer serializer = new Persister();

			Reader reader = new StringReader(response);
			try {
				XEArrayList tmpPosts = serializer.read(XEArrayList.class,
						reader, false);
				return tmpPosts;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(XEArrayList result) {
			super.onPostExecute(result);

			isTaskLoading[postType] = false;
			if (postsArray[postType] == null) {
				postsArray[postType] = new XEArrayList();
				postsArray[postType].posts = new ArrayList<XETextylePost>();
				postsArray[postType].pagination = new XEPagination();
			}
			if (result != null && result.posts != null) {
				for (XETextylePost post : result.posts) {
					postsArray[postType].posts.add(post);
				}
				postsArray[postType].pagination = result.pagination;
				adapter.setArrayWithPosts(postsArray[postType].posts);
				adapter.notifyDataSetChanged();
				if (postsArray[postType].pagination.cur_page == postsArray[postType].pagination.total_page)
					footerView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		boolean loadMore = (firstVisibleItem + visibleItemCount >= totalItemCount)
				&& totalItemCount >= 20;
		if (loadMore) {
			int postType;
			switch (radioGroup.getCheckedRadioButtonId()) {
			case R.id.XEMOBILE_TEXTYLE_POSTS_ALLOPTION:
				postType = POST_TYPE_ALL;
				break;

			case R.id.XEMOBILE_TEXTYLE_POSTS_PUBLISHEDOPTION:
				postType = POST_TYPE_PUBLISHED;
				break;

			case R.id.XEMOBILE_TEXTYLE_POSTS_DRAFTSOPTION:
				postType = POST_TYPE_DRAFT;
				break;
			case R.id.XEMOBILE_TEXTYLE_POSTS_TRASHOPTION:
				postType = POST_TYPE_TRASH;
				break;
			default:
				postType = -1;
			}

			if (postType == -1)
				return;

			if (!isTaskLoading[postType]) {
				if (postsArray[postType] != null
						&& postsArray[postType].pagination != null) {
					if (postsArray[postType].pagination.cur_page < postsArray[postType].pagination.total_page) {
						GetPostsAsycTask task = new GetPostsAsycTask(postType);
						task.execute(postsArray[postType].pagination.cur_page + 1);
					}
				}
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int postType;

		switch (checkedId) {
		case R.id.XEMOBILE_TEXTYLE_POSTS_ALLOPTION:
			postType = POST_TYPE_ALL;
			break;

		case R.id.XEMOBILE_TEXTYLE_POSTS_PUBLISHEDOPTION:
			postType = POST_TYPE_PUBLISHED;
			break;

		case R.id.XEMOBILE_TEXTYLE_POSTS_DRAFTSOPTION:
			postType = POST_TYPE_DRAFT;
			break;
		case R.id.XEMOBILE_TEXTYLE_POSTS_TRASHOPTION:
			postType = POST_TYPE_TRASH;
			break;
		default:
			postType = -1;
			return;
		}

		if (postType == -1)
			return;

		Log.i("leapkh", "Post checked: " + postType);
		if (postsArray[postType] == null) {
			if (!isTaskLoading[postType]) {
				adapter.clearData();
				GetPostsAsycTask task = new GetPostsAsycTask(postType);
				task.execute();
			}
		} else {
			Log.i("leapkh", "Use existing data...");
			Log.i("leapkh", "Data count: " + postsArray[postType].posts.size());
			adapter.setArrayWithPosts(postsArray[postType].posts);
			adapter.notifyDataSetChanged();
		}
	}

}