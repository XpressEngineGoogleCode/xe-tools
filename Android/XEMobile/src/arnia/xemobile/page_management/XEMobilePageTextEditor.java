package arnia.xemobile.page_management;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import arnia.xemobile.classes.XEHost;

public class XEMobilePageTextEditor extends XEMobileTextEditor {
	private String mid;
	private String document_srl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mid = getActivity().getIntent().getStringExtra("mid");
		document_srl = getActivity().getIntent().getStringExtra("document_srl");

		// startProgress("Page content is loading");
//		GetPageContentAndTitleAsyncTask task = new GetPageContentAndTitleAsyncTask();
//		task.execute();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	// @Override
	// protected void onCreate(Bundle savedInstanceState)
	// {
	// super.onCreate(savedInstanceState);
	//
	// mid = getIntent().getStringExtra("mid");
	// document_srl = getIntent().getStringExtra("document_srl");
	//
	// startProgress("Page content is loading");
	// GetPageContentAndTitleAsyncTask task = new
	// GetPageContentAndTitleAsyncTask();
	// task.execute();
	// }

//	@Override
//	public void doneButton(View view) {
//		SavePageAsyncTask task = new SavePageAsyncTask();
//		task.execute();
//	}
//
//	// Async Task that gets the page content and title
//	private class GetPageContentAndTitleAsyncTask extends
//			AsyncTask<Object, Object, Object> {
//		String responseContent;
//		String responseTitle;
//
//		@Override
//		protected Object doInBackground(Object... params) {
//			responseContent = XEHost
//					.getINSTANCE()
//					.getRequest(
//							"/index.php?module=mobile_communication&act=procmobile_communicationArticleContent&srl="
//									+ document_srl);
//			responseTitle = XEHost
//					.getINSTANCE()
//					.getRequest(
//							"/index.php?module=mobile_communication&act=procmobile_communicationArticleTitle&srl="
//									+ document_srl);
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Object result) {
//			super.onPostExecute(result);
//
//			// isLoggedIn(responseContent, XEMobilePageTextEditor.this);
//			// isLoggedIn(responseTitle, XEMobilePageTextEditor.this);
//
//			responseTitle = responseTitle.replace("<br/>", "\n");
//			responseContent = responseContent.replace("<br/>", "\n");
//
//			titleEditText.setText(responseTitle);
//			contentEditText.setText(responseContent);
//			// dismissProgress();
//		}
//	}
//
//	// Async Task that saves the page content and title
//	private class SavePageAsyncTask extends AsyncTask<Object, Object, Object> {
//
//		@Override
//		protected Object doInBackground(Object... params) {
//			String content = getContent();
//
//			String title = titleEditText.getText().toString();
//
//			String xmlRequest = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
//					+ "<methodCall>\n<params>\n<_filter><![CDATA[insert_article]]></_filter>\n"
//					+ "<error_return_url><![CDATA[/index.php?mid="
//					+ "&act=dispPageAdminContentModify]]></error_return_url>\n"
//					+ "<act><![CDATA[procPageAdminArticleDocumentInsert]]></act>\n"
//					+ "<mid><![CDATA[" + mid + "]]></mid>\n"
//					+ "<content><![CDATA[" + content + "]]></content>\n"
//					+ "<document_srl><![CDATA[" + document_srl
//					+ "]]></document_srl>\n" + "<title><![CDATA[" + title
//					+ "]]></title><module><![CDATA[page]]></module><"
//					+ "/params></methodCall>";
//
//			XEHost.getINSTANCE().postRequest("/index.php", xmlRequest);
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Object result) {
//			super.onPostExecute(result);
//
//			// finish();
//		}
//
//	}

}
