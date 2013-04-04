package arnia.xemobile_textyle_comments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.XEFragment;
import arnia.xemobile.XEMobileMainActivityController;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XETextyle;

public class XEMobileTextyleCommentsReplyController extends XEFragment
{
	private TextView replyTextView;
	private String document_srl;
	private String comment_srl;
	private XETextyle textyle;
	private Button postItButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view  = inflater.inflate(R.layout.xemobiletextylecommentreplylayout,container,false);
		replyTextView = (TextView) view.findViewById(R.id.XEMOBILE_TEXTYLE_COMMENT_REPLYTEXTVIEW);
		postItButton = (Button) view.findViewById(R.id.XEMOBILE_TEXTYLE_COMMENT_POSTBUTTON);
		postItButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				PostReplyAsyncTask task = new PostReplyAsyncTask();
				task.execute();
			}
		});
		Bundle args = getArguments();
		textyle = (XETextyle) args.getSerializable("textyle");
		document_srl = args.getString("document_srl");
		comment_srl = args.getString("comment_srl");
		
		return view;
	}
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) 
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.xemobiletextylecommentreplylayout);
//		
//		replyTextView = (TextView) findViewById(R.id.XEMOBILE_TEXTYLE_COMMENT_REPLYTEXTVIEW);
//		postItButton = (Button) findViewById(R.id.XEMOBILE_TEXTYLE_COMMENT_POSTBUTTON);
//		postItButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) 
//			{
//				PostReplyAsyncTask task = new PostReplyAsyncTask();
//				task.execute();
//			}
//		});
//		
//		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
//		document_srl = getIntent().getStringExtra("document_srl");
//		comment_srl = getIntent().getStringExtra("comment_srl");
//		
//	}
	
	 private class PostReplyAsyncTask extends AsyncTask<Object, Object, Object>
	 {

		@Override
		protected Object doInBackground(Object... params) 
		{
			String postCommentXML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
					"<methodCall>\n<params>\n" +
					"<_filter><![CDATA[insert_comment]]></_filter>\n" +
					"<act><![CDATA[procTextyleInsertComment]]></act>\n" +
					"<vid><![CDATA["+ textyle.domain +"]]></vid>\n" +
					"<mid><![CDATA[textyle]]></mid>\n" +
					"<document_srl><![CDATA["+ document_srl +"]]></document_srl>\n" +
					"<content><![CDATA[<p>" +replyTextView.getText().toString()+ "</p>]]></content>\n" +
					"<parent_srl><![CDATA["+ comment_srl +"]]></parent_srl>\n" +
					"<module><![CDATA[textyle]]></module>\n</params>\n" +
					"</methodCall>";
			
			XEHost.getINSTANCE().postRequest("/index.php",postCommentXML);
			
			return null;
		}
		 
		@Override
		protected void onPostExecute(Object result) 
		{
//			finish();
			((XEMobileMainActivityController)activity).backwardScreen();
			super.onPostExecute(result);
		}
		 
	 }
}
