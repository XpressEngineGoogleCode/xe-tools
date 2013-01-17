package arnia.xemobile.page_management;


import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import arnia.xemobile.R;
import arnia.xemobile.XEActivity;

public abstract class XEMobileTextEditor extends XEActivity 

{

   protected EditText titleEditText;
   protected EditText contentEditText;
   
   protected WebView  htmlEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobiletexteditor);
		
		titleEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTEDITOR_TITLE);
		contentEditText = (EditText) findViewById(R.id.XEMOBILE_TEXTEDITOR_CONTENT);	
		
	}
	
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
	
	public abstract void doneButton(View view);
	
	private String insertTagAtIndex(String tag,int index)
	{
		String string = contentEditText.getText().toString();
		
		String substring1 = string.substring(0, index);
		String substring2 = string.substring(index);
		
		return substring1 + tag + substring2;
	}
	
	private String insertTagAtSelection(String tag, String closeTag,int start,int end)
	{
		String string = contentEditText.getText().toString();
		
		String substring1 = string.substring(0, start);
		String substring2 = string.substring(start, end);
		String substring3 = string.substring(end,string.length());
		
		return substring1 + tag + substring2 + closeTag + substring3;
	}
	
	public String getContent()
	{
		String string = contentEditText.getText().toString();
		return string.replace("\n", "<br/>");
	}
	
	public void setContent(String string)
	{
		contentEditText.setText( string.replace("<br/>", "\n") );
	}
		
}
