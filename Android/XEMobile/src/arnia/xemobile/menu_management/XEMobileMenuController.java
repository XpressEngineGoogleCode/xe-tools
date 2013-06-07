package arnia.xemobile.menu_management;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import arnia.xemobile.R;
import arnia.xemobile.XEFragment;
import arnia.xemobile.XEMobileMainActivityController;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEMenu;

public class XEMobileMenuController extends XEFragment implements
		OnItemClickListener, OnClickListener {

	private XEArrayList arrayWithMenus;
	private XEMobileMenuAdapter adapter;
	private Button addMenuButton;

	public XEArrayList getArrayWithMenus() {
		return arrayWithMenus;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.xemobilemenulayout, container,
				false);

		ListView list = (ListView) view
				.findViewById(R.id.XEMOBILE_MENU_LISTVIEW);
		addMenuButton = (Button) view
				.findViewById(R.id.XEMOBILE_MENU_ADDBUTTON);

		adapter = new XEMobileMenuAdapter(this.activity);

		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		addMenuButton.setOnClickListener(this);

		XEFragment.startProgress(getActivity(), getString(R.string.loading));
		GetMenusAsyncTask getAsyncRequest = new GetMenusAsyncTask();
		getAsyncRequest.execute();

		return view;
	}

	// called when an item from list is pressed
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		XEMenu menu = adapter.getArrayWithMenus().get(position);
		XEMobileMainActivityController mainActivity = (XEMobileMainActivityController) activity;
		XEMobileMenuItemsController menuItemController = new XEMobileMenuItemsController();
		Bundle argument = new Bundle();
		argument.putString("menu_srl", menu.menuSrl);
		argument.putString("menu_item_parent_srl", "0");
		menuItemController.setArguments(argument);
		mainActivity.addMoreScreen(menuItemController);
	}

	// called when Add menu button or Delete button are pressed
	@Override
	public void onClick(View v) {
		// Add button
		if (v.getId() == R.id.XEMOBILE_MENU_ADDBUTTON) {
			final Dialog dialog = new Dialog(this.activity);

			dialog.setContentView(R.layout.xemobileaddmenutoastlayout);
			dialog.setTitle("Add menu");

			final Button doneButton = (Button) dialog
					.findViewById(R.id.XEMOBILE_ADDMENUTOAST_BUTTON);
			final EditText textView = (EditText) dialog
					.findViewById(R.id.XEMOBILE_ADDMENUTOAST_EDITTEXT);

			doneButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.cancel();
					XEFragment.startProgress(getActivity(), "Saving menu...");
					AddMenuAsyncTask addMenuAT = new AddMenuAsyncTask();
					addMenuAT.execute(textView.getText().toString());

				}
			});
			dialog.show();
		}
		// delete button
		else {
			Button deleteButtonPressed = (Button) v;
			int position = (Integer) deleteButtonPressed.getTag();
			XEMenu menu = this.arrayWithMenus.menus.get(position);

			DeleteMenuAsyncTask task = new DeleteMenuAsyncTask();
			String[] params = { menu.menuSrl, menu.menuName };
			task.execute(params);
		}
	}

	// async task that deletes a menu
	private class DeleteMenuAsyncTask extends AsyncTask<String, Object, Object> {

		@Override
		protected String doInBackground(String... param) {
			String menuSRL = param[0];
			String menuName = param[1];
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("menu_srl", menuSRL);
			params.put("title", menuName);
			XEHost.getINSTANCE()
					.postMultipart(params,
							"/index.php?module=mobile_communication&act=procmobile_communicationMenuDelete");
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			GetMenusAsyncTask task = new GetMenusAsyncTask();
			task.execute();
			adapter.setArrayWithMenus(arrayWithMenus.menus);
			adapter.notifyDataSetChanged();
		}
	}

	// async task that adds a menu
	private class AddMenuAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... param) {
			String name = param[0];
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("title", name);
			params.put("module", "mobile_communication");
			params.put("act", "procmobile_communicationMenuInsert");
			XEHost.getINSTANCE().postMultipart(params, "/");
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			GetMenusAsyncTask asyncTask = new GetMenusAsyncTask();
			asyncTask.execute();
		}
	}

	// async task that sends a request for menus
	private class GetMenusAsyncTask extends AsyncTask<Object, Object, Object> {
		String xmlData;

		// send the request
		@Override
		protected Object doInBackground(Object... params) {
			xmlData = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?XDEBUG_SESSION_START=netbeans-xdebug&module=mobile_communication&act=procmobile_communicationDisplayMenu");

			Serializer serializer = new Persister();

			Reader reader = new StringReader(xmlData);
			try {
				arrayWithMenus = serializer.read(XEArrayList.class, reader,
						false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		// when the response is received update the adapter
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			dismissProgress();

			// isLoggedIn(xmlData, XEMobileMenuController.this);

			if (arrayWithMenus != null && arrayWithMenus.menus != null) {
				// send the array with menus to adapter and notify that the data
				// has changed
				adapter.setArrayWithMenus(arrayWithMenus.menus);
				adapter.notifyDataSetChanged();
			}
		}

	}
}
