package arnia.xemobile.menu_management;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import arnia.xemobile.R;
import arnia.xemobile.XEFragment;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEMenuItemsDetails;
import arnia.xemobile.classes.XEModule;

public class XEMobileMenuItemEditController extends XEFragment implements
		OnClickListener, android.widget.CompoundButton.OnCheckedChangeListener {
	// interface elements
	private EditText linkTitle;
	private RadioButton createRadioOption;
	private RadioButton selectRadioOption;
	private RadioButton menuURLRadioOption;

	private RadioButton articleRadioOption;
	private RadioButton widgetRadioOption;
	private RadioButton externalRadioOption;

	private TextView moduleIDTextView;
	private EditText moduleIDEditText;
	private CheckBox isNewWindow;

	private TextView menuURLTextView;
	private EditText menuURLEditText;

	private Spinner availablePages;
	private Spinner pageTypes;
	private Button saveButton;

	// menu parent srl
	private String menuItemSRL;

	private XEMenuItemsDetails details;

	// Array with modules for spinner
	private XEArrayList modules;

	// spinner adapter
	private ArrayAdapter<XEModule> adapter;

	private final String PAGE_TYPE_WIDGET = "WIDGET";
	private final String PAGE_TYPE_EXTERNAL = "EXTERNAL";
	private final String PAGE_TYPE_ARTICAL = "ARTICLE";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.xemobilemenuitemeditlayout,
				container, false);
		adapter = new ArrayAdapter<XEModule>(this.activity,
				android.R.layout.simple_spinner_item);

		availablePages = (Spinner) view
				.findViewById(R.id.XEMOBILE_AVAILABLE_PAGES);
		pageTypes = (Spinner) view.findViewById(R.id.XEMOBILE_PAGE_TYPES);

		linkTitle = (EditText) view.findViewById(R.id.XEMOBILE_LINK_TEXT);
		isNewWindow = (CheckBox) view.findViewById(R.id.XEMOBILE_NEW_WINDOW);

		Bundle args = getArguments();
		menuItemSRL = args.getString("menu_item_srl");

		// make request to get a list of modules for spinner
		GetModulesAsyncTask task = new GetModulesAsyncTask();
		task.execute();

		// action for save button
		saveButton = (Button) view
				.findViewById(R.id.XEMOBILE_EDIT_MENU_SAVE_BUTTON);
		saveButton.setOnClickListener(this);

		availablePages.setAdapter(adapter);

		// handle when selected page type
		pageTypes.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				refreshAvailablePageAdapter(returnType());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		return view;
	}

	public void refreshAvailablePageAdapter(String pageType) {
		if (modules != null) {
			if (modules.modules != null) {
				XEModule modulePage;
				adapter.clear();
				for (int i = 0; i < modules.modules.size(); i++) {
					modulePage = modules.modules.get(i);
					if (modulePage.page_type.compareTo(pageType) == 0) {
						adapter.add(modulePage);
					}
				}
				adapter.notifyDataSetChanged();
			}
		}
	}

	// called when the save button is pressed
	@Override
	public void onClick(View v) {
	}

	// the method returns the type selected
	private String returnType() {
		if (((String) pageTypes.getSelectedItem()).compareTo("Widget page") == 0) {
			return this.PAGE_TYPE_WIDGET;
		} else if (((String) pageTypes.getSelectedItem())
				.compareTo("Article page") == 0) {
			return this.PAGE_TYPE_ARTICAL;
		} else {
			return this.PAGE_TYPE_EXTERNAL;
		}

	}

	private String getPageTypeValue(String text) {
		if (text.compareTo("Widget page") == 0) {
			return this.PAGE_TYPE_WIDGET;
		} else if (text.compareTo("Article page") == 0) {
			return this.PAGE_TYPE_ARTICAL;
		} else {
			return this.PAGE_TYPE_EXTERNAL;
		}
	}

	// update the interface when the user change the option
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == menuURLRadioOption) {
			if (isChecked) {
				availablePages.setVisibility(View.INVISIBLE);
				articleRadioOption.setVisibility(View.INVISIBLE);
				widgetRadioOption.setVisibility(View.INVISIBLE);
				externalRadioOption.setVisibility(View.INVISIBLE);

				moduleIDEditText.setVisibility(View.INVISIBLE);
				moduleIDTextView.setVisibility(View.INVISIBLE);

				menuURLTextView.setVisibility(View.VISIBLE);
				menuURLEditText.setVisibility(View.VISIBLE);
			} else {
				menuURLTextView.setVisibility(View.INVISIBLE);
				menuURLEditText.setVisibility(View.INVISIBLE);
			}
		} else if (buttonView == createRadioOption) {
			if (isChecked) {
				articleRadioOption.setVisibility(View.VISIBLE);
				articleRadioOption.setChecked(true);
				widgetRadioOption.setVisibility(View.VISIBLE);
				externalRadioOption.setVisibility(View.VISIBLE);

				moduleIDEditText.setVisibility(View.VISIBLE);
				moduleIDTextView.setVisibility(View.VISIBLE);

				menuURLTextView.setVisibility(View.INVISIBLE);
				menuURLEditText.setVisibility(View.INVISIBLE);

				availablePages.setVisibility(View.VISIBLE);
			} else {
				articleRadioOption.setVisibility(View.INVISIBLE);
				widgetRadioOption.setVisibility(View.INVISIBLE);
				externalRadioOption.setVisibility(View.INVISIBLE);

				moduleIDEditText.setVisibility(View.INVISIBLE);
				moduleIDTextView.setVisibility(View.INVISIBLE);
			}
		} else if (buttonView == selectRadioOption) {
			if (isChecked) {
				articleRadioOption.setVisibility(View.INVISIBLE);
				widgetRadioOption.setVisibility(View.INVISIBLE);
				externalRadioOption.setVisibility(View.INVISIBLE);

				moduleIDEditText.setVisibility(View.INVISIBLE);
				moduleIDTextView.setVisibility(View.INVISIBLE);

				menuURLTextView.setVisibility(View.INVISIBLE);
				menuURLEditText.setVisibility(View.INVISIBLE);

				availablePages.setVisibility(View.VISIBLE);
			} else {
				availablePages.setVisibility(View.INVISIBLE);
			}
		}
	}

	// Async Task that gets details about the current edited menu
	private class GetEditedMenuAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String xmlData = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><methodCall><params><menu_item_srl><![CDATA["
					+ menuItemSRL
					+ "]]></menu_item_srl><module><![CDATA[menu]]></module><act><![CDATA[getMenuAdminItemInfo]]></act></params></methodCall>";

			String response = XEHost.getINSTANCE().postRequest("/index.php",
					xmlData);

			Serializer serializer = new Persister();

			Reader reader = new StringReader(response);

			try {
				details = serializer.read(XEMenuItemsDetails.class, reader,
						false);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			linkTitle.setText(details.name);
			if (details.open_window.equals("Y"))
				isNewWindow.setChecked(true);
			else
				isNewWindow.setChecked(false);

			// moduleType may be null somehow
			if (details.moduleType == null) {
				// createRadioOption.setChecked(true);
			} else {
				if (details.moduleType.equals("page")) {

					int i;
					for (i = 0; i < pageTypes.getAdapter().getCount() - 1; i++) {
						if ((getPageTypeValue((String) pageTypes.getAdapter()
								.getItem(i))).compareTo(details.pageType) == 0) {
							pageTypes.setSelection(i);
							break;
						}
					}

					refreshAvailablePageAdapter(details.pageType);

					// select the correct page in spinner

					for (i = 0; i < adapter.getCount() - 1; i++) {

						// if( details.url.equals(modules.modules.get(i).module)
						// ) break;
						if (details.url.equals(adapter.getItem(i).module))
							break;
					}
					Log.d("i=", i + " ");
					availablePages.setSelection(i);

				} else if (details.moduleType.equals("url")) {
					Log.d("ajunge aici", "dada");
					menuURLRadioOption.setChecked(true);

					menuURLEditText.setText(details.url);
				}
			}

		}

	}

	// Async Task to get a list of modules for the spinner adapter
	private class GetModulesAsyncTask extends AsyncTask<Object, Object, Object> {

		String xmlData;

		@Override
		protected Object doInBackground(Object... params) {
			xmlData = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationListModules");

			Serializer serializer = new Persister();

			Reader reader = new StringReader(xmlData);
			try {
				modules = serializer.read(XEArrayList.class, reader, false);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			// isLoggedIn(xmlData, XEMobileMenuItemEditController.this);

			if (modules != null && modules.modules != null) {
				refreshAvailablePageAdapter(returnType());

				GetEditedMenuAsyncTask editedMenuTask = new GetEditedMenuAsyncTask();
				editedMenuTask.execute();

				Log.i("Finish loading", "Modules");
			}

		}
	}

}
