package arnia.xemobile_textyle_posts;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEResponse;
import arnia.xemobile.classes.XETextyle;
import arnia.xemobile.page_management.XEMobileTextEditor;

public class XEMobileTextyleAddPostController extends XEMobileTextEditor
{
	
	private XETextyle textyle;

	@Override
	public void doneButton(View view) 
	{
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		
		// asking the user if he wants to save or publish
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Save or publish?")
		       .setCancelable(true)
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
	
	//asynctask for saving the post
	private class SavePostAsyncTask extends AsyncTask<Object, Object, Object>	
	{

		@Override
		protected Object doInBackground(Object... params) 
		{
			String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
			"<methodCall>\n<params>\n<act><![CDATA[procTextylePostsave]]></act>\n" +
			"<vid><![CDATA[%@]]></vid>\n" +
			"<publish><![CDATA[N]]></publish>\n" +
			"<_filter><![CDATA[save_post]]></_filter>\n" +
			"<mid><![CDATA[textyle]]></mid>\n" +
			"<title><![CDATA[" + titleEditText.getText().toString() + "]]></title>\n" +
			"<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n" +
			"<content><![CDATA[<p>"+ getContent() +"</p>]]></content>\n" +
			"<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it?" +
			" The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n" +
			"<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>";
			
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
	
	
	//async task for publishing the post
	
	//for publish an post, firstly, it must be saved and then published
	private class PublishPostAsyncTask extends AsyncTask<Object,Object,Object>
	{

		@Override
		protected Object doInBackground(Object... params) 
		{
			//firstly, the post is saved
			String xmlForSaving = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
			"<methodCall>\n<params>\n<act><![CDATA[procTextylePostsave]]></act>\n" +
			"<vid><![CDATA["+ textyle.domain +"]]></vid>\n" +
			"<publish><![CDATA[N]]></publish>\n" +
			"<_filter><![CDATA[save_post]]></_filter>\n" +
			"<mid><![CDATA[textyle]]></mid>\n" +
			"<title><![CDATA[" + titleEditText.getText().toString() + "]]></title>\n" +
			"<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n" +
			"<content><![CDATA[<p>"+ contentEditText.getText().toString() +"</p>]]></content>\n" +
			"<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it?" +
			" The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n" +
			"<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>";
			
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
					"<methodCall>\n" +
					"<params>\n" +
					"<_filter><![CDATA[publish_post]]></_filter>\n" +
					"<act><![CDATA[procTextylePostPublish]]></act>\n" +
					"<document_srl><![CDATA["+ response.document_srl + "]]></document_srl>\n" +
					"<mid><![CDATA[textyle]]></mid>\n" +
					"<vid><![CDATA["+ textyle.domain +"]]></vid>\n" +
					"<trackback_charset><![CDATA[UTF-8]]></trackback_charset>\n" +
					"<use_alias><![CDATA[N]]></use_alias>\n" +
					"<allow_comment><![CDATA[Y]]></allow_comment>\n" +
					"<allow_trackback><![CDATA[Y]]></allow_trackback>\n" +
					"<subscription><![CDATA[N]]></subscription>\n" +
					"<module><![CDATA[textyle]]></module>\n" +
					"</params>\n</methodCall>";
			
			XEHost.getINSTANCE().postRequest("/index.php", xmlForPublishing);
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			finish();
			dismissProgress();
			super.onPostExecute(result);
		}
		
	}

}
