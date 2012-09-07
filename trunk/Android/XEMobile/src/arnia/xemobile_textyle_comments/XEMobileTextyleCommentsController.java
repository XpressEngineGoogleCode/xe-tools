package arnia.xemobile_textyle_comments;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEComment;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEResponse;
import arnia.xemobile.classes.XETextyle;

//Activity that has a listView that contains XEComments
public class XEMobileTextyleCommentsController extends XEActivity implements OnClickListener
{
	private ListView listView;
	
	private XETextyle textyle;
	
	private XEArrayList array;
	private XEMobileTextyleCommentsAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobiletextylecommentslayout);
		
		listView = (ListView) findViewById(R.id.XEMOBILE_TEXTYLE_COMMENTS_LISTVIEW);
		adapter = new XEMobileTextyleCommentsAdapter(this);
		listView.setAdapter(adapter);
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		
	}
	
	@Override
	protected void onResume() 
	{
		//send request to get the comments
		GetCommentsAsyncTask task = new GetCommentsAsyncTask();
		task.execute();
		
		super.onResume();
	}
	
	//Async Task for getting the comments
	private class GetCommentsAsyncTask extends AsyncTask<Object, Object, Object>
	{
		String response;
		
		@Override
		protected Object doInBackground(Object... params) 
		{
			//send request
			response = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationShowComments&module_srl="+ textyle.module_srl);

			//parse the response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			try
			{
				array = serializer.read(XEArrayList.class, reader , false);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return null;
		}
		
		//method called when the response came
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			
			//check if the user is logged in
			if(isLoggedIn(response, XEMobileTextyleCommentsController.this))
			{
				// if the response is null, a toast appears
				if( array.comments == null ) 
				{
				Toast toast = Toast.makeText(getApplicationContext(), "No comments!", Toast.LENGTH_SHORT);
				toast.show();
				}
			
				adapter.setComments(array.comments);
				adapter.notifyDataSetChanged();
			}
			
		}
	}

	//method called when one of the buttons is clicked: Delete Button, Reply Button and Public Button 
	@Override
	public void onClick(View v) 
	{
		// get the comment
		// the view's tag is the possition in array
		XEComment comment = adapter.getArrayWithComments().get( (Integer) v.getTag() );
		
		//cast the view as button
		Button button = (Button) v;
		
		//check what button was pressed
		if( v.getId() == R.id.XEMOBILE_TEXTYLE_COMMENTS_DELETE )
		{
			DeleteCommentAsyncTask task = new DeleteCommentAsyncTask();
			task.execute(comment);
		}
		else if( v.getId() == R.id.XEMOBILE_TEXTYLE_COMMENTS_REPLY )
		{
			Intent intent = new Intent(XEMobileTextyleCommentsController.this, XEMobileTextyleCommentsReplyController.class);
			intent.putExtra("textyle", textyle);
			intent.putExtra("document_srl", comment.document_srl);
			intent.putExtra("comment_srl", comment.comment_srl);
			startActivity(intent);
		}
		else if( v.getId() == R.id.XEMOBILE_TEXTYLE_COMMENTS_PUBLIC )
		{
			if( button.getText().toString().equals("Public") )
			{
				String  requestXML =  "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
				                      "<methodCall>\n<params>\n<comment_srl><![CDATA["+comment.comment_srl+"]]></comment_srl>\n" +
				                      "<page><![CDATA[1]]></page>\n<is_secret><![CDATA[Y]]></is_secret>\n" +
				                      "<module_srl><![CDATA["+comment.module_srl+"]]></module_srl>\n<module><![CDATA[textyle]]></module>\n"+
				                      "<act><![CDATA[procTextyleCommentItemSetSecret]]></act>\n<vid><![CDATA["+textyle.domain+"]]></vid>\n" +
				                      "</params>\n</methodCall>";
				comment.is_secret="Y";
				button.setText("Private");
				VisibilityChangeAsyncTask task = new VisibilityChangeAsyncTask();
				task.execute(requestXML);
			}
			else if( button.getText().toString().equals("Private") )  
			{
				String requestXML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"+
	              "<methodCall>\n<params>\n<comment_srl><![CDATA["+comment.comment_srl+"]]></comment_srl>\n" +
	              "<page><![CDATA[1]]></page>\n<is_secret><![CDATA[N]]></is_secret>\n" +
	              "<module_srl><![CDATA["+comment.module_srl+"]]></module_srl>\n" +
	              "<module><![CDATA[textyle]]></module>\n"+
	              "<act><![CDATA[procTextyleCommentItemSetSecret]]></act>\n"+
	              "<vid><![CDATA["+textyle.domain+"]]></vid>\n</params>\n</methodCall>";
			
				comment.is_secret="N";
				button.setText("Public");
				VisibilityChangeAsyncTask task = new VisibilityChangeAsyncTask();
				task.execute(requestXML);
			}
		}
	}
	
	//Async Task to remove a comment
	private class DeleteCommentAsyncTask extends AsyncTask<XEComment, String, XEComment>
	{
		XEResponse confirmation;
		
		@Override
		protected XEComment doInBackground(XEComment... params) 
		{
			//the xml that will be sent in request
			String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
					"<methodCall>\n<params>\n" +
					"<comment_srl><![CDATA["+ params[0].comment_srl +"]]></comment_srl>\n" +
					"<module><![CDATA[textyle]]></module>\n" +
					"<act><![CDATA[procTextyleCommentItemDelete]]></act>\n" +
					"<vid><![CDATA["+ textyle.domain +"]]></vid>\n</params>\n</methodCall>";
			
			//send the request
			String response = XEHost.getINSTANCE().postRequest("/index.php", xml);
			
			//parse the response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			try {
				confirmation = serializer.read(XEResponse.class, reader, false);
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return params[0];
		}
		
		//method called when the response came
		@Override
		protected void onPostExecute(XEComment comment) 
		{
			//check if the comment was deleted successfuly
			if( confirmation.message.equals("Deleted successfully.") )
			{
				adapter.getArrayWithComments().remove(comment);
				adapter.notifyDataSetChanged();
			}
			
			super.onPostExecute(comment);
		}
	}
	
	// Async Task for changing the visibility
	private class VisibilityChangeAsyncTask extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... params) 
		{
			XEHost.getINSTANCE().postRequest("/index.php", params[0]);

			return null;
		}
		
	}
}
