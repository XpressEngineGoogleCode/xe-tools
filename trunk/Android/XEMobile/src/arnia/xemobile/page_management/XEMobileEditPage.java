package arnia.xemobile.page_management;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import arnia.xemobile.R;
import arnia.xemobile.XEFragment;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.controls.XEMobileTextEditor;

public class XEMobileEditPage extends XEFragment implements OnClickListener {
	private String mid;
	private String document_srl;
	private XEMobileTextEditor htmlEditor;
	private EditText titleEditText;
	private Button saveButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.xemobilepagetexteditorlayout,
				container, false);
		htmlEditor = new XEMobileTextEditor();

		addNestedFragment(R.id.XEMOBILE_TEXT_EDITOR_HOLDER, htmlEditor,
				"htmlTextEditor");

		Bundle args = getArguments();
		mid = args.getString("mid");
		document_srl = args.getString("document_srl");

		saveButton = (Button) view.findViewById(R.id.XEMOBILE_PAGE_EDITOR_SAVE);
		saveButton.setOnClickListener(this);

		titleEditText = (EditText) view
				.findViewById(R.id.XEMOBILE_PAGE_EDITOR_BROWSER_TITLE);

		XEFragment.startProgress(getActivity(), "Page content is loading");
		GetPageContentAndTitleAsyncTask task = new GetPageContentAndTitleAsyncTask();
		task.execute();

		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.XEMOBILE_PAGE_EDITOR_SAVE) {
			SavePageAsyncTask task = new SavePageAsyncTask();
			task.execute();
		}
	}

	// Async Task that gets the page content and title
	private class GetPageContentAndTitleAsyncTask extends
			AsyncTask<Object, Object, Object> {
		String responseContent;
		String responseTitle;

		@Override
		protected Object doInBackground(Object... params) {
			responseContent = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationArticleContent&srl="
									+ document_srl);
			responseTitle = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationArticleTitle&srl="
									+ document_srl);
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			responseTitle = responseTitle.replace("<br/>", "\n");
			responseContent = responseContent.replace("<br/>", "\n");

			titleEditText.setText(responseTitle);
			htmlEditor.setContent(responseContent);
			dismissProgress();
		}
	}

	// Async Task that saves the page content and title
	private class SavePageAsyncTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String content = htmlEditor.getContent();
			String title = titleEditText.getText().toString();

			String xmlRequest = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
					+ "<methodCall>\n<params>\n<_filter><![CDATA[insert_article]]></_filter>\n"
					+ "<error_return_url><![CDATA[/index.php?mid="
					+ "&act=dispPageAdminContentModify]]></error_return_url>\n"
					+ "<act><![CDATA[procPageAdminArticleDocumentInsert]]></act>\n"
					+ "<mid><![CDATA[" + mid + "]]></mid>\n"
					+ "<content><![CDATA[" + content + "]]></content>\n"
					+ "<document_srl><![CDATA[" + document_srl
					+ "]]></document_srl>\n" + "<title><![CDATA[" + title
					+ "]]></title><module><![CDATA[page]]></module><"
					+ "/params></methodCall>";

			XEHost.getINSTANCE().postRequest("/index.php", xmlRequest);

			return null;
		}

	}

}
