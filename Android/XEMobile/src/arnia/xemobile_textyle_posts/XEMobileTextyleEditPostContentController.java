package arnia.xemobile_textyle_posts;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.XEFragment;
import arnia.xemobile.classes.XEDocument;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEResponse;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.classes.XETextyleStats;
import arnia.xemobile.page_management.XEMobileTextEditor;

public class XEMobileTextyleEditPostContentController extends XEFragment implements OnClickListener {
	private String title;
	private XETextyle textyle;
	private String document_srl;
	private String category_srl;
	private String content;
	private Button deleteButton;
	
	private EditText titleEditText;
	private XEMobileTextEditor htmlEditor ;
	private EditText postUrl;
	private Button saveButton;
	private Button saveAndPublishButton;
	

	private String typeOfPost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		htmlEditor = new XEMobileTextEditor();
		View view = inflater.inflate(R.layout.xemobiletextyleeditpostlayout, container, false);
		addNestedFragment(R.id.XEMOBILE_HTML_EDITOR, htmlEditor, "add_post_html_editor");
		
		titleEditText = (EditText) view.findViewById(R.id.XEMOBILE_ADD_POST_POST_TITLE);
		postUrl = (EditText) view.findViewById(R.id.XEMOBILE_ADD_POST_POST_URL);
		
		saveAndPublishButton = (Button) view.findViewById(R.id.XEMOBILE_ADD_POST_SAVE_AND_PUBLISH_BUTTON);
		saveButton = (Button) view.findViewById(R.id.XEMOBILE_ADD_POST_SAVE_BUTTON);
		
		saveAndPublishButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		
		deleteButton = (Button) view.findViewById(R.id.XEMOBILE_ADD_POST_DELETE_BUTTON);
		
		deleteButton.setOnClickListener(this);
		
		
		Bundle args = getArguments();
		title = args.getString("title");
		textyle = (XETextyle) args.getSerializable("textyle");
		document_srl = args.getString("document_srl");
		category_srl =args.getString("category_srl");
		typeOfPost = args.getString("type");
		
		titleEditText.setText(title);
		
		startProgress("Loading...");
		LoadContentAsyncTask task = new LoadContentAsyncTask();
		task.execute();
		
//		document_srl = getActivity().getIntent().getStringExtra("document_srl");
//		category_srl = getActivity().getIntent().getStringExtra("category_srl");
//		typeOfPost = getActivity().getIntent().getStringExtra("type");
		// titleEditText.setText(title);
		
		
//		title = getActivity().getIntent().getStringExtra("title");
//		textyle = (XETextyle) getActivity().getIntent().getSerializableExtra(
//				"textyle");
//		document_srl = getActivity().getIntent().getStringExtra("document_srl");
//		category_srl = getActivity().getIntent().getStringExtra("category_srl");
//		typeOfPost = getActivity().getIntent().getStringExtra("type");
		// titleEditText.setText(title);

//		startProgress("Loading...");
//		LoadContentAsyncTask task = new LoadContentAsyncTask();
//		task.execute();

		return view;
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId()==R.id.XEMOBILE_ADD_POST_SAVE_BUTTON){
			 startProgress("Saving...");
			 SavePostAsyncTask task = new SavePostAsyncTask();
			 task.execute();
		}else if(v.getId()==R.id.XEMOBILE_ADD_POST_SAVE_AND_PUBLISH_BUTTON){
			startProgress("Save and publish...");
			PublishPostAsyncTask task = new PublishPostAsyncTask();
			task.execute();
		}else if(v.getId()==R.id.XEMOBILE_ADD_POST_DELETE_BUTTON){
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			 	builder.setMessage("Do you want to delete this post?")
			 	.setTitle("Attention")
			 	.setCancelable(true)
			 	.setPositiveButton("Yes",	new DialogInterface.OnClickListener() {
					 @Override
					 public void onClick(DialogInterface dialog, int which) {
						 startProgress("Deleting...");
						 DeleteAsyncTask deleteTask = new DeleteAsyncTask();
						 deleteTask.execute();
					 }
			 	})
			 	.setNegativeButton("No", new DialogInterface.OnClickListener() {
			 		@Override
			 		public void onClick(DialogInterface dialog,int id) {
					 
			 		}
			 	}
			 	).show();
			}
	}
	
	// @Override
	// public void doneButton(View view) {
	// textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
	//
	// if (typeOfPost.equals("saved")) {
	// // asking the user if he wants to save or publish
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// builder.setMessage("Delete, save or publish?")
	// .setCancelable(true)
	// .setNeutralButton("Delete",
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// startProgress("Loading...");
	// DeleteAsyncTask deleteTask = new DeleteAsyncTask();
	// deleteTask.execute();
	// }
	// })
	// .setPositiveButton("Save",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int id) {
	// startProgress("Loading...");
	// SavePostAsyncTask task = new SavePostAsyncTask();
	// task.execute();
	// }
	// })
	// .setNegativeButton("Publish",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int id) {
	// startProgress("Loading...");
	// PublishPostAsyncTask task = new PublishPostAsyncTask();
	// task.execute();
	// }
	// });
	// AlertDialog alert = builder.create();
	// alert.show();
	// } else if (typeOfPost.equals("published")) {
	// // asking the user if he wants to save or publish
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// builder.setMessage("Delete or save?")
	// .setCancelable(true)
	// .setPositiveButton("Delete",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int id) {
	// DeleteAsyncTask task = new DeleteAsyncTask();
	// task.execute();
	// }
	// })
	// .setNegativeButton("Save",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int id) {
	// PublishPostAsyncTask task = new PublishPostAsyncTask();
	// task.execute();
	// }
	// });
	// AlertDialog alert = builder.create();
	// alert.show();
	// }
	//
	// }

	
	private String getAliasXmlString() {
		String url = postUrl.getText().toString().trim();
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
	
	// async taks for loading the content of the post
	private class LoadContentAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			content = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?XDEBUG_SESSION_START=netbeans-xdebug&module=mobile_communication&act=procmobile_communicationContentForPost&module_srl="
									+ textyle.module_srl
									+ "&document_srl="
									+ document_srl);
			Serializer serializer = new Persister();
			
			Reader reader = new StringReader(content);
			Log.i("XEDocument",content);
			try
			{
				return serializer.read(XEDocument.class, reader,false);
			}catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

//			 if (isLoggedIn(content,XEMobileTextyleEditPostContentController.this)) {
//			 dismissProgress();
//			
			 if(result!=null){
				 
				 
				 XEDocument document = (XEDocument)result;
				 htmlEditor.setContent(new String(Base64.decode(document.content,Base64.DEFAULT)));
				 postUrl.setText(document.alias);
			 }
//			 if (content.length() > 7)
//				 content = content.substring(3, content.length() - 4);
//			 htmlEditor.setContent(content);
//			 setContent(content);
			 dismissProgress();
			 }
//		}
	}

	// asynctask for saving the post
	 private class SavePostAsyncTask extends AsyncTask<Object, Object, Object>
	 {
	
	 @Override
	 protected Object doInBackground(Object... params) {
		 String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
			 + "<methodCall><params>\n<act><![CDATA[procTextylePostsave]]></act>\n"
			 + "<vid><![CDATA["
			 + textyle.domain
			 + "]]></vid>\n"
			 + "<publish><![CDATA[N]]></publish>\n"
			 + "<_filter><![CDATA[save_post]]></_filter>\n"
			 + "<mid><![CDATA[textyle]]></mid>\n"
			 + "<title><![CDATA["
			 + titleEditText.getText().toString()
			 + "]]></title>\n"
			 + getAliasXmlString()
			 + "<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n"
			 + "<content><![CDATA[<p>"
			 + htmlEditor.getContent()
			 + "</p>]]></content>\n"
			 + "<document_srl><![CDATA["
			 + document_srl
			 + "]]></document_srl>\n"
			 + "<editor_sequence><![CDATA["
			 + document_srl
			 + "]]></editor_sequence>\n"
			 + "<module><![CDATA[textyle]]></module>\n"
			 + "</params>\n</methodCall>\n";
		
		 String responseAtRequest = XEHost.getINSTANCE().postRequest(
		 "/index.php", xmlForSaving);
	
	 return null;
	 }
	
	 @Override
	 protected void onPostExecute(Object result) {
		 super.onPostExecute(result);
	
		 dismissProgress();
//	 finish();
	 }
	
	 }

	// asynctask that deletes the post
	private class DeleteAsyncTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String xmlForDelete = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall>\n<params>\n"
					+ "<document_srl><![CDATA["
					+ document_srl
					+ "]]></document_srl>\n"
					+ "<page><![CDATA[1]]></page>\n"
					+ "<module><![CDATA[textyle]]></module>\n"
					+ "<act><![CDATA[procTextylePostTrash]]></act>\n"
					+ "<vid><![CDATA["
					+ textyle.domain
					+ "]]></vid>\n</params>\n</methodCall>";

			String responseAtRequest = XEHost.getINSTANCE().postRequest(
					"/index.php", xmlForDelete);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			dismissProgress();
			// finish();
		}

	}

	

	// async task for publishing the post

	// publish = save + publish
	 private class PublishPostAsyncTask extends AsyncTask<Object, Object, Object> {
	
	 @Override
	 protected Object doInBackground(Object... params) {
		 // firstly, the post is saved
		 String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
		 + "<methodCall><params>\n<act><![CDATA[procTextylePostsave]]></act>\n"
		 + "<vid><![CDATA["
		 + textyle.domain
		 + "]]></vid>\n"
		 + "<publish><![CDATA[N]]></publish>\n"
		 + "<_filter><![CDATA[save_post]]></_filter>\n"
		 + "<mid><![CDATA[textyle]]></mid>\n"
		 + "<title><![CDATA["
		 + titleEditText.getText().toString()
		 + "]]></title>\n"
		 + getAliasXmlString()
		 + "<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n"
		 + "<content><![CDATA[<p>"
		 + htmlEditor.getContent()
		 + "</p>]]></content>\n"
		 + "<document_srl><![CDATA["
		 + document_srl
		 + "]]></document_srl>\n"
		 + "<editor_sequence><![CDATA["
		 + document_srl
		 + "]]></editor_sequence>\n"
		 + "<module><![CDATA[textyle]]></module>\n"
		 + "</params>\n</methodCall>\n";
	
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
		 + "<methodCall><params><_filter><![CDATA[publish_post]]></_filter>"
		 + "<act><![CDATA[procTextylePostPublish]]></act>"
		 + "<document_srl><![CDATA["
		 + document_srl
		 + "]]></document_srl>"
		 + "<mid><![CDATA[textyle]]></mid>"
		 + "<vid><![CDATA["
		 + textyle.domain
		 + "]]></vid>"
		 + "<category_srl><![CDATA["
		 + category_srl
		 + "]]></category_srl>"
		 + "<trackback_charset><![CDATA[UTF-8]]></trackback_charset>"
		 + "<use_alias><![CDATA[N]]></use_alias>"
		 + "<allow_comment><![CDATA[Y]]></allow_comment>"
		 + "<allow_trackback><![CDATA[Y]]></allow_trackback>"
		 + "<subscription><![CDATA[N]]></subscription>"
		 + "<module><![CDATA[textyle]]></module></params></methodCall>";
	
		 XEHost.getINSTANCE().postRequest("/index.php", xmlForPublishing);
		 return null;
	 }
	
	 @Override
	 protected void onPostExecute(Object result) {
		 super.onPostExecute(result);
		 dismissProgress();
//		 finish();
	 }
	
	 }
}