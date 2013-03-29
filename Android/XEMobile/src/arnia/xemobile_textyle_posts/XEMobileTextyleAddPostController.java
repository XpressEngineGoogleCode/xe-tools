package arnia.xemobile_textyle_posts;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.XEFragment;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEResponse;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.page_management.XEMobileTextEditor;

public class XEMobileTextyleAddPostController extends XEFragment implements
		OnClickListener {

	private EditText etxtTitle;
	private EditText etxtUrl;
	private XEMobileTextEditor editor;
	private Button btnSave;
	private Button btnSaveAndPublish;

	private XETextyle textyle;
	private View view;

	// array with Textyles
	private XEArrayList array;
	// adapter for array with Textyles
	private ArrayAdapter<XETextyle> adapter;

	private Spinner selectVirtualSiteSpinner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.xemobiletextylenewpostlayout,
				container, false);

		etxtTitle = (EditText) view
				.findViewById(R.id.XEMOBILE_ADD_POST_POST_TITLE);
		etxtUrl = (EditText) view.findViewById(R.id.XEMOBILE_ADD_POST_POST_URL);

		addNestedFragment(R.id.XEMOBILE_HTML_EDITOR, new XEMobileTextEditor(), "add_post_html_editor");
		
		btnSave = (Button) view
				.findViewById(R.id.XEMOBILE_ADD_POST_SAVE_BUTTON);
		btnSave.setOnClickListener(this);
		btnSaveAndPublish = (Button) view
				.findViewById(R.id.XEMOBILE_ADD_POST_SAVE_AND_PUBLISH_BUTTON);
		btnSaveAndPublish.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		GetTextylesAsyncTask task = new GetTextylesAsyncTask();
		task.execute();
	}

	// asynctask for saving the post
	private class SavePostAsyncTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startProgress("Saving...");
		}

		@Override
		protected Object doInBackground(Object... params) {

			String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall>\n<params>\n<act><![CDATA[procTextylePostsave]]></act>\n"
					+ "<vid><![CDATA["
					+ array.textyles.get(0).domain
					+ "]]></vid>\n"
					+ "<publish><![CDATA[N]]></publish>\n"
					+ "<_filter><![CDATA[save_post]]></_filter>\n"
					+ "<mid><![CDATA[textyle]]></mid>\n"
					+ "<title><![CDATA["
					+ etxtTitle.getText().toString()
					+ "]]></title>\n"
					+ getAliasXmlString()
					+ "<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n"
					+ "<content><![CDATA[<p>"
					+ editor.getContent()
					+ "</p>]]></content>\n"
					+ "<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it?"
					+ " The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n"
					+ "<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>";

			// XEHost.getINSTANCE().postRequest(
			// "/index.php?XDEBUG_SESSION_START=netbeans-xdebug",
			// xmlForSaving);
			XEHost.getINSTANCE().postRequest("/index.php", xmlForSaving);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			dismissProgress();
			Toast.makeText(getActivity(), "Save post success.",
					Toast.LENGTH_LONG).show();
		}

	}

	// async task for publishing the post

	// for publish an post, firstly, it must be saved and then published
	private class PublishPostAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startProgress("Publishing...");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// firstly, the post is saved
			String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall>\n<params>\n<act><![CDATA[procTextylePostsave]]></act>\n"
					+ "<vid><![CDATA["
					+ array.textyles.get(0).domain
					+ "]]></vid>\n"
					+ "<publish><![CDATA[N]]></publish>\n"
					+ "<_filter><![CDATA[save_post]]></_filter>\n"
					+ "<mid><![CDATA[textyle]]></mid>\n"
					+ "<title><![CDATA["
					+ etxtTitle.getText().toString()
					+ "]]></title>\n"
					+ getAliasXmlString()
					+ "<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n"
					+ "<content><![CDATA[<p>"
					+ editor.getContent()
					+ "</p>]]></content>\n"
					+ "<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it?"
					+ " The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n"
					+ "<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>";

			String responseAtRequest = XEHost.getINSTANCE().postRequest(
					"/index.php", xmlForSaving);

			Serializer serializer = new Persister();
			Reader reader = new StringReader(responseAtRequest);

			// the response has the document_srl, which is used for publishing
			XEResponse response = null;
			try {
				response = serializer.read(XEResponse.class, reader, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// the xml for publishing
			String xmlForPublishing = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall>\n"
					+ "<params>\n"
					+ "<_filter><![CDATA[publish_post]]></_filter>\n"
					+ "<act><![CDATA[procTextylePostPublish]]></act>\n"
					+ "<document_srl><![CDATA["
					+ response.document_srl
					+ "]]></document_srl>\n"
					+ "<mid><![CDATA[textyle]]></mid>\n"
					+ "<vid><![CDATA["
					+ array.textyles.get(0).domain
					+ "]]></vid>\n"
					+ "<trackback_charset><![CDATA[UTF-8]]></trackback_charset>\n"
					+ "<use_alias><![CDATA[N]]></use_alias>\n"
					+ "<allow_comment><![CDATA[Y]]></allow_comment>\n"
					+ "<allow_trackback><![CDATA[Y]]></allow_trackback>\n"
					+ "<subscription><![CDATA[N]]></subscription>\n"
					+ "<module><![CDATA[textyle]]></module>\n"
					+ "</params>\n</methodCall>";

			XEHost.getINSTANCE().postRequest("/index.php", xmlForPublishing);
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			dismissProgress();
			Toast.makeText(getActivity(), "Publish post success.",
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.XEMOBILE_ADD_POST_SAVE_BUTTON:
			if (validateInput()) {
				SavePostAsyncTask saveTask = new SavePostAsyncTask();
				saveTask.execute();
			}
			break;
		case R.id.XEMOBILE_ADD_POST_SAVE_AND_PUBLISH_BUTTON:
			if (validateInput()) {
				PublishPostAsyncTask publishTask = new PublishPostAsyncTask();
				publishTask.execute();
			}
			break;
		}
	}

	private boolean validateInput() {
		if (etxtTitle.getText().toString().trim().length() == 0) {
			Toast.makeText(getActivity(), "Title is required.",
					Toast.LENGTH_LONG).show();
			etxtTitle.requestFocus();
			return false;
		}
		if (editor.getContent().trim().length() == 0) {
			Toast.makeText(getActivity(), "Body is required.",
					Toast.LENGTH_LONG).show();
			editor.requestFocus();
			return false;
		}
		return true;
	}

	private String getAliasXmlString() {
		String url = etxtUrl.getText().toString().trim();
		if (url.length() == 0)
			return url;

		// if (url.startsWith(mainSite))
		// url = url.replace(mainSite, "");

		if (url.startsWith("http") || url.startsWith("www")) {
			Pattern pattern = Pattern.compile("[a-zA-Z]/");
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				int inx = matcher.start();
				url = url.substring(inx + 1);
			} else
				url = "";
		}

		if (url.startsWith("/"))
			url = url.substring(1);

		if (url.length() > 0)
			url = "<alias><![CDATA[" + url + "]]></alias>\n";

		return url;
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

			// check if the user is logged in
			// isLoggedIn(response,
			// XEMobileTextyleSelectTextyleController.this);

			dismissProgress();
			if (array != null && array.textyles != null) {
				// for (int i = 0; i < array.textyles.size(); i++) {
				// adapter.add(array.textyles.get(i));
				// }
				// adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getActivity(), R.string.no_textyle, 1000).show();
			}
		}
	}

}
