package arnia.xemobile_textyle_comments;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.classes.XEComment;

//Adapter for the listView with XEComments
public class XEMobileTextyleCommentsAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<XEComment> comments;

	private ArrayList<XEComment> arrayWithReplies;
	private ArrayList<XEComment> arrayWithComments;
	
	//getter
	public ArrayList<XEComment> getArrayWithComments() 
	{
		return arrayWithComments;
	}
	
	//constructor
	public XEMobileTextyleCommentsAdapter(Context context)
	{
		this.context = context;
		this.comments = new ArrayList<XEComment>();
		

		arrayWithReplies = new ArrayList<XEComment>();
		arrayWithComments = new ArrayList<XEComment>();
	}
	
	//prepare the XEComments array for the indentation of reply comments
	private void prepareArrayWithCommentsAndArrayWithReplies()
	{
		//sort the array
		Collections.sort(comments);
		
		//put the comments in two arrays: one with replies and one with simple comments
		for(XEComment comment : this.comments)
		{
			if( comment.parent_srl.equals("0") ) arrayWithComments.add(comment);
			else arrayWithReplies.add(comment);
		}
		
		//add the reply comments after parent comment
		for(XEComment reply : this.arrayWithReplies)
		{
			int index = indexInArrayForCommentWithModuleSRL(reply.parent_srl);
			arrayWithComments.add(index, reply);
		}
	}
	
	//returns the index where the reply comment should be introduced
	private int indexInArrayForCommentWithModuleSRL(String document_srl)
	{
		for(int i=0;i<arrayWithComments.size();i++)
		{
			XEComment comment = arrayWithComments.get(i);
			
			if( comment.comment_srl.equals(document_srl) ) return i+1;
			
		}
		
		return 0;
	}
	
	// setter
	public void setComments(ArrayList<XEComment> comments) 
	{
		arrayWithComments.clear();
		arrayWithReplies.clear();
		if( comments != null ) this.comments = comments;
		prepareArrayWithCommentsAndArrayWithReplies();
	}

	@Override
	public int getCount() 
	{
		return arrayWithComments.size();
	}

	@Override
	public Object getItem(int arg0) 
	{
		return null;
	}

	@Override
	public long getItemId(int arg0) 
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2)
	{
		XEComment comment = arrayWithComments.get(position);
		
		
		if( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.xemobilecommentitemcellview, null);
		}
		
		TextView nickname = (TextView) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_COMMENTS_NICKNAMETEXTVIEW);
		nickname.setText( comment.nickname );
		
		// reply comments indentation
		if( !comment.parent_srl.equals("0") ) nickname.setPadding(25, 0, 0, 0);
		else nickname.setPadding(0, 0, 0, 0);
		
		WebView webView = (WebView) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_COMMENTS_CONTENTWEBVIEW);
		webView.loadData(comment.content, "text/html", null);
		
		Button reply = (Button) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_COMMENTS_REPLY);
		reply.setTag( position );
		reply.setOnClickListener((OnClickListener) context);
		
		Button delete = (Button) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_COMMENTS_DELETE);
		delete.setTag( position );
		delete.setOnClickListener((OnClickListener)context);
		
		Button visibility = (Button) convertView.findViewById(R.id.XEMOBILE_TEXTYLE_COMMENTS_PUBLIC);
		visibility.setTag( position );
		visibility.setOnClickListener((OnClickListener)context);
		
		if( comment.is_secret.equals("N") ) visibility.setText("Public");
		else visibility.setText("Private");
		
		return convertView;
	}
}
