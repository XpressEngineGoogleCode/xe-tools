package arnia.xemobile_textyle_posts;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import arnia.xemobile.R;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEResponse;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.menu_management.XEMobileAddMenuItemController;
import arnia.xemobile.page_management.XEMobileTextEditor;

public class XEMobileTextyleEditPostContentController extends XEMobileTextEditor
{
	private String title;
	private XETextyle textyle;
	private String document_srl;
	private String category_srl;
	private String content;
	private Button deleteButton;
	
	private String typeOfPost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		
		title = getIntent().getStringExtra("title");
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		document_srl = getIntent().getStringExtra("document_srl");
		category_srl = getIntent().getStringExtra("category_srl");
		typeOfPost = getIntent().getStringExtra("type");
		titleEditText.setText(title);
		
		startProgress("Loading...");
		LoadContentAsyncTask task = new LoadContentAsyncTask();
		task.execute();
	}
	
	@Override
	public void doneButton(View view) 
	{
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		
		if( typeOfPost.equals("saved") )
		{
			// asking the user if he wants to save or publish
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Delete, save or publish?")
			       .setCancelable(false)
			        .setNeutralButton("Delete", new DialogInterface.OnClickListener() 
			       {
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						startProgress("Loading...");
						DeleteAsyncTask deleteTask = new DeleteAsyncTask();
						deleteTask.execute();
					}
				})	
			       .setPositiveButton("Save", new DialogInterface.OnClickListener() 
			       	   {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	   startProgress("Loading...");
			        	   SavePostAsyncTask task = new SavePostAsyncTask();
			        	   task.execute();
			           }
			       })
			       .setNegativeButton("Publish", new DialogInterface.OnClickListener() 
			       	   {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	   startProgress("Loading...");
			                PublishPostAsyncTask task = new PublishPostAsyncTask();
			                task.execute();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
		else if( typeOfPost.equals("published") )
		{
			// asking the user if he wants to save or publish
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Delete or save?")
			       .setCancelable(false)
			       .setPositiveButton("Delete", new DialogInterface.OnClickListener() 
			       	   {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	   DeleteAsyncTask task = new DeleteAsyncTask();
			        	   task.execute();
			           }
			       })
			       .setNegativeButton("Save", new DialogInterface.OnClickListener() 
			       	   {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			                PublishPostAsyncTask task = new PublishPostAsyncTask();
			                task.execute();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
		

		
	}

	//async taks for loading the content of the post
	private class LoadContentAsyncTask extends AsyncTask<Object,Object,Object>
	{

		@Override
		protected Object doInBackground(Object... params) 
		{
			content = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationContentForPost&module_srl="+ textyle.module_srl +"&document_srl=" + document_srl);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			
			if(isLoggedIn(content, XEMobileTextyleEditPostContentController.this))
			{
				dismissProgress();
			
				if(content.length() > 7)
					content = content.substring(3, content.length()-4);
				setContent(content);
			}
		}
	}
	
	//asynctask for saving the post
	private class SavePostAsyncTask extends AsyncTask<Object, Object, Object>	
	{

		@Override
		protected Object doInBackground(Object... params) 
		{
			String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
					"<methodCall><params>\n<act><![CDATA[procTextylePostsave]]></act>\n" +
					"<vid><![CDATA["+ textyle.domain +"]]></vid>\n" +
					"<publish><![CDATA[N]]></publish>\n" +
					"<_filter><![CDATA[save_post]]></_filter>\n" +
					"<mid><![CDATA[textyle]]></mid>\n" +
					"<title><![CDATA["+ titleEditText.getText().toString() +"]]></title>\n" +
					"<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n" +
					"<content><![CDATA[<p>"+ getContent() +"</p>]]></content>\n" +
					"<document_srl><![CDATA["+document_srl+"]]></document_srl>\n" +
					"<editor_sequence><![CDATA["+document_srl+"]]></editor_sequence>\n" +
					"<module><![CDATA[textyle]]></module>\n" +
					"</params>\n</methodCall>\n";
			
			String responseAtRequest = XEHost.getINSTANCE().postRequest("/index.php", xmlForSaving);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			
			dismissProgress();
			finish();
		}
		
	}
	
	//asynctask that deletes the post
	private class DeleteAsyncTask extends AsyncTask<Object, Object, Object>	
	{

		@Override
		protected Object doInBackground(Object... params) 
		{
			String xmlForDelete = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
					"<methodCall>\n<params>\n" +
					"<document_srl><![CDATA["+ document_srl +"]]></document_srl>\n" +
					"<page><![CDATA[1]]></page>\n" +
					"<module><![CDATA[textyle]]></module>\n" +
					"<act><![CDATA[procTextylePostTrash]]></act>\n" +
					"<vid><![CDATA["+ textyle.domain +"]]></vid>\n</params>\n</methodCall>";
			
			String responseAtRequest = XEHost.getINSTANCE().postRequest("/index.php", xmlForDelete);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			dismissProgress();
			finish();
		}
		
	}
	
	//async task for publishing the post
	
	// publish = save + publish
	private class PublishPostAsyncTask extends AsyncTask<Object,Object,Object>
	{

		@Override
		protected Object doInBackground(Object... params) 
		{
			//firstly, the post is saved
			String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
			"<methodCall><params>\n<act><![CDATA[procTextylePostsave]]></act>\n" +
			"<vid><![CDATA["+ textyle.domain +"]]></vid>\n" +
			"<publish><![CDATA[N]]></publish>\n" +
			"<_filter><![CDATA[save_post]]></_filter>\n" +
			"<mid><![CDATA[textyle]]></mid>\n" +
			"<title><![CDATA["+ titleEditText.getText().toString() +"]]></title>\n" +
			"<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n" +
			"<content><![CDATA[<p>"+ getContent() +"</p>]]></content>\n" +
			"<document_srl><![CDATA["+document_srl+"]]></document_srl>\n" +
			"<editor_sequence><![CDATA["+document_srl+"]]></editor_sequence>\n" +
			"<module><![CDATA[textyle]]></module>\n" +
			"</params>\n</methodCall>\n";
			
			String responseAtRequest = XEHost.getINSTANCE().postRequest("/index.php", xmlForSaving);
			
			Serializer serializer = new Persister();
			
			Reader reader = new StringReader(responseAtRequest);
			
			//the response has the document_srl, which is used for publishing
			XEResponse response = null;
			try
			{
				response = serializer.read(XEResponse.class,reader, false);
			}catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			//the xml for publishing
			String xmlForPublishing = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
					"<methodCall><params><_filter><![CDATA[publish_post]]></_filter>" +
					"<act><![CDATA[procTextylePostPublish]]></act>" +
					"<document_srl><![CDATA["+ document_srl +"]]></document_srl>" +
					"<mid><![CDATA[textyle]]></mid>" +
					"<vid><![CDATA["+ textyle.domain +"]]></vid>" +
					"<category_srl><![CDATA["+ category_srl +"]]></category_srl>" +
					"<trackback_charset><![CDATA[UTF-8]]></trackback_charset>" +
					"<use_alias><![CDATA[N]]></use_alias>" +
					"<allow_comment><![CDATA[Y]]></allow_comment>" +
					"<allow_trackback><![CDATA[Y]]></allow_trackback>" +
					"<subscription><![CDATA[N]]></subscription>" +
					"<module><![CDATA[textyle]]></module></params></methodCall>";
			
			XEHost.getINSTANCE().postRequest("/index.php", xmlForPublishing);
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			dismissProgress();
			finish();
		}

	}
}