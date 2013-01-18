package arnia.xemobile_textyle_pages;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEResponse;
import arnia.xemobile.classes.XETextyle;


public class XEMobileTextyleAddPageController extends XEActivity
{

	protected XETextyle textyle;
	
	// UI References
	protected EditText contentEditText;
	protected EditText urlEditText;
	protected EditText menuNameEditText;
	protected TextView urlTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobiletextyleaddpagelayout);
		
		//get references to the UI elements
		contentEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTYLE_ADDPAGE_CONTENT);
		urlEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTYLE_ADDPAGE_URLEDITTEXT);
		menuNameEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTYLE_ADDPAGE_MENUNAME);
		urlTextView = (TextView) findViewById(R.id.XEMOBILE_TEXTYLE_ADDPAGE_URLTEXTVIEW);
		
		//get the textyle that is passed between activities
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
	}
	
	// method called when the doneButton is pressed
	public void doneButton(View view)
	{
		//request for saving the page
		SavePageAsyncTask task = new SavePageAsyncTask();
		task.execute();
	}
	
	// Async Task for saving the page 
	private class SavePageAsyncTask extends AsyncTask<Object, Object, Object>
	{
		String response;

		@Override
		protected Object doInBackground(Object... params) 
		{
			String xmlForSave = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
					"<methodCall>\n<params>\n" +
					"<publish><![CDATA[N]]></publish>\n" +
					"<_filter><![CDATA[insert_extra_menu]]></_filter>\n" +
					"<mid><![CDATA[textyle]]></mid>\n" +
					"<vid><![CDATA["+textyle.domain+"]]></vid>\n" +
					"<menu_mid><![CDATA["+urlEditText.getText().toString()+"]]></menu_mid>\n" +
					"<menu_name><![CDATA["+menuNameEditText.getText().toString()+"]]></menu_name>\n" +
					"<content><![CDATA[<p>"+contentEditText.getText().toString()+"</p>]]></content>\n<" +
					"msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n" +
					"<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it? The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n" +
					"<hx><![CDATA[h3]]></hx>\n" +
					"<hr><![CDATA[hline]]></hr>\n" +
					"<module><![CDATA[textyle]]></module>\n" +
					"<act><![CDATA[procTextyleToolExtraMenuInsert]]></act>\n</params>\n" +
					"</methodCall>";
			
			//sending the request
			response = XEHost.getINSTANCE().postRequest("/index.php", xmlForSave);
			
			return response;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			
			//parsing the response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			XEResponse response = null;
			try
			{
				response = serializer.read(XEResponse.class,reader, false);
			}catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			//if the confirmation
			if( response.message.equals("success"))
				{
					Toast.makeText(getApplicationContext(), "Saved new page", 1000).show();
					finish();
				}
		}
		
	}
	

	// method called when the bold button is pressed
	public void boldButton(View view)
	{
		int selectionStart = contentEditText.getSelectionStart();
		int selectionEnd = contentEditText.getSelectionEnd();
		
		if(contentEditText.isFocused())
		{
			if( selectionStart == selectionEnd )
				{
					contentEditText.setText(insertTagAtIndex("<b></b>", selectionStart));
					Selection.setSelection(contentEditText.getText(), selectionStart + 3);
				}
			else
			{
				contentEditText.setText(insertTagAtSelection("<b>", "</b>", selectionStart, selectionEnd));
			}
		}
	}
	
	// method called when the italic button is pressed
	public void italicButton(View view)
	{
		int selectionStart = contentEditText.getSelectionStart();
		int selectionEnd = contentEditText.getSelectionEnd();
		
		if(contentEditText.isFocused())
		{
			
			if( selectionStart == selectionEnd )
			{
				contentEditText.setText(insertTagAtIndex("<i></i>", selectionStart));
				Selection.setSelection(contentEditText.getText(), selectionStart + 3);
			}
			else
			{
				contentEditText.setText(insertTagAtSelection("<i>", "</i>", selectionStart, selectionEnd));
			}
		}
		
	}

	public void underlineButton(View view)
	{
		int selectionStart = contentEditText.getSelectionStart();
		int selectionEnd = contentEditText.getSelectionEnd();
		
		if(contentEditText.isFocused())
		{
			
			if( selectionStart == selectionEnd )
			{
				contentEditText.setText(insertTagAtIndex("<u></u>", selectionStart));
				Selection.setSelection(contentEditText.getText(), selectionStart + 3);
			}
			else
			{
				contentEditText.setText(insertTagAtSelection("<u>", "</u>", selectionStart, selectionEnd));
			}
		}
		
	}
	
	public void ulButton(View view)
	{
		int selectionStart = contentEditText.getSelectionStart();
		int selectionEnd = contentEditText.getSelectionEnd();
		
		if(contentEditText.isFocused())
		{
			
			
			if( selectionStart == selectionEnd )
			{
				contentEditText.setText(insertTagAtIndex("<ul></ul>", selectionStart));
				Selection.setSelection(contentEditText.getText(), selectionStart + 4);
			}
			else
			{
				contentEditText.setText(insertTagAtSelection("<ul>", "</ul>", selectionStart, selectionEnd));
			}
		}
	}
	
	public void liButton(View view)
	{
		int selectionStart = contentEditText.getSelectionStart();
		int selectionEnd = contentEditText.getSelectionEnd();
	
		if(contentEditText.isFocused())
		{
			
			if( selectionStart == selectionEnd )
			{
				contentEditText.setText(insertTagAtIndex("<li></li>", selectionStart));
				Selection.setSelection(contentEditText.getText(), selectionStart + 4);
			}
			else
			{
				contentEditText.setText(insertTagAtSelection("<u>", "</u>", selectionStart, selectionEnd));
			}
		}		

	}
	
	public void strikeButton(View view)
	{
		int selectionStart = contentEditText.getSelectionStart();
		int selectionEnd = contentEditText.getSelectionEnd();
		
	
		if(contentEditText.isFocused())
		{
			
			if( selectionStart == selectionEnd )
			{
				contentEditText.setText(insertTagAtIndex("<strike></strike>", selectionStart));
				Selection.setSelection(contentEditText.getText(), selectionStart + 8);
			}
			else
			{
				contentEditText.setText(insertTagAtSelection("<strike>", "</strike>", selectionStart, selectionEnd));
			}
		}

	}
		
	public void cancelButton(View view)
	{
		finish();
	}
	
	// insert the tag at the index
	private String insertTagAtIndex(String tag,int index)
	{
		String string = contentEditText.getText().toString();
		
		String substring1 = string.substring(0, index);
		String substring2 = string.substring(index);
		
		return substring1 + tag + substring2;
	}
	
	// insert a tag when the text is selected
	private String insertTagAtSelection(String tag, String closeTag,int start,int end)
	{
		String string = contentEditText.getText().toString();
		
		String substring1 = string.substring(0, start);
		String substring2 = string.substring(start, end);
		String substring3 = string.substring(end,string.length());
		
		return substring1 + tag + substring2 + closeTag + substring3;
	}
	
	//getter for content
	public String getContent()
	{
		String string = contentEditText.getText().toString();
		return string.replace("\n", "<br/>");
	}
	
	//setter for content
	public void setContent(String string)
	{
		contentEditText.setText( string.replace("<br/>", "\n") );
	}
}
