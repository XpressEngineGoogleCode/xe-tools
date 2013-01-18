package arnia.xemobile_textyle_pages;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEResponse;
import arnia.xemobile.classes.XETextyle;

// Activity for editing a page 
// inherits XEMobileTextyleAddPageController
public class XEMobileTextyleEditPageController extends XEMobileTextyleAddPageController
{
	//details about page
	private String menu_mid;
	private String content;
	private String menuName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//get the details about page
		menu_mid = getIntent().getStringExtra("menu_mid");
		menuName = getIntent().getStringExtra("menuName");
		textyle = (XETextyle) getIntent().getSerializableExtra("textyle");
		
		//hide the urlEditText that is declared in XEMobileTextyleAddPageController
		//the url can't be edited
		urlEditText.setVisibility(View.GONE);
		
		//show the url of the page
		urlTextView.setText( XEHost.getINSTANCE().getURL() + "/index.php?vid="+ textyle.domain + "&mid="  + menu_mid);
		menuNameEditText.setText(menuName);
		
		//sending the request for loading the page content
		LoadPageAsyncTask task = new LoadPageAsyncTask();
		task.execute();
	}
	
	//called when the Done button is pressed
	@Override
	public void doneButton(View view) 
	{
		SavePageAsyncTask task = new SavePageAsyncTask();
		task.execute();
	}
	
	private class LoadPageAsyncTask extends AsyncTask<Object,Object,Object>
	{

		@Override
		protected Object doInBackground(Object... params) 
		{
			//send the request
			content = XEHost.getINSTANCE().getRequest("/index.php?module=mobile_communication&act=procmobile_communicationContentPage&menu_mid="+ menu_mid +"&site_srl=" + textyle.site_srl);
			
			return null;
		}
		
		//method called when the response came
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
			
			//check if the user is logged in
			if( isLoggedIn(content, XEMobileTextyleEditPageController.this) )
			{
				//the content of page in textlye may empty
				if(!content.isEmpty()){
					content = content.substring(3,content.length() - 4);
					setContent(content);
				}else{
					setContent(content);				
				}
			}
		}
	}
	
	//Async Task for saving the page
	private class SavePageAsyncTask extends AsyncTask<Object, Object, Object>
	{
		String response;

		@Override
		protected Object doInBackground(Object... params) 
		{
			String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
					"<methodCall>\n<params>\n" +
					"<error_return_url><![CDATA[/xe2/index.php?act=dispTextyleToolExtraMenuInsert&menu_mid=qwqwe&vid=blog]]></error_return_url>\n" +
					"<act><![CDATA[procTextyleToolExtraMenuUpdate]]></act>\n" +
					"<menu_mid><![CDATA["+ menu_mid +"]]></menu_mid>\n" +
					"<publish><![CDATA[N]]></publish>\n" +
					"<_filter><![CDATA[modify_extra_menu]]></_filter>\n" +
					"<mid><![CDATA[textyle]]></mid>\n" +
					"<vid><![CDATA["+ textyle.domain +"]]></vid>\n" +
					"<menu_name><![CDATA["+ menuNameEditText.getText().toString() +"]]></menu_name>\n" +
					"<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n" +
					"<content><![CDATA[<p>"+ getContent() +"</p>]]></content>\n" +
					"<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it? The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n" +
					"<hx><![CDATA[h3]]></hx>\n<hr><![CDATA[hline]]></hr>\n" +
					"<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>";

			//sending the request for saving the page
			response = XEHost.getINSTANCE().postRequest("/index.php", xml);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			super.onPostExecute(result);
	
			// parsing the response
			Serializer serializer = new Persister();
			Reader reader = new StringReader(response);
			XEResponse responseParsed = null;
			try
			{
				responseParsed = serializer.read(XEResponse.class,reader,false);
			}catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			if( responseParsed.message.equals("success") ) finish();
		}
		
	}
}
