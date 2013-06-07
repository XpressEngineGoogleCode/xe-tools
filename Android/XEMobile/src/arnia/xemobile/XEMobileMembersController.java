package arnia.xemobile;

import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import arnia.xemobile.classes.XEArrayList;
import arnia.xemobile.classes.XEHost;
import arnia.xemobile.classes.XEMember;

//Activity that has a list of members
public class XEMobileMembersController extends XEFragment implements
		OnItemClickListener {
	// Array with the parsed response
	private XEArrayList arrayWithMembers = new XEArrayList();

	// the ListView
	private ListView listView;

	// adapter for the listView
	private ArrayAdapter<XEMember> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.xemobilememberslayout, container,
				false);

		listView = (ListView) view.findViewById(R.id.XEMOBILE_MEMBERS_LISTVIEW);
		//
		// //adapter for the listView
		adapter = new ArrayAdapter<XEMember>(this.activity,
				android.R.layout.simple_list_item_1);

		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		startProgress(activity, "Loading...");

		// send the request to get the members
		GetMembersAsync asyncRequest = new GetMembersAsync();
		asyncRequest.execute();
	}

	// called when an item in listView is pressed
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		XEMember member = arrayWithMembers.members.get(position);

		XEMobileEditMemberController editMemberController = new XEMobileEditMemberController();
		Bundle args = new Bundle();
		args.putSerializable("member", member);
		editMemberController.setArguments(args);
		((XEMobileMainActivityController) activity)
				.addMoreScreen(editMemberController);
	}

	// AsyncTask to get all members
	private class GetMembersAsync extends AsyncTask<Void, Void, Void> {
		String xmlData;

		// make the request in background
		@Override
		protected Void doInBackground(Void... params) {
			// make the request
			xmlData = XEHost
					.getINSTANCE()
					.getRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationDisplayMembers");

			// parse the response
			Serializer serializer = new Persister();

			Reader reader = new StringReader(xmlData);
			try {
				arrayWithMembers = serializer.read(XEArrayList.class, reader,
						false);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		// when the response is received update the adapter
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// dismiss the loading message
			dismissProgress();

			// add members in adapter
			if (arrayWithMembers.members != null) {
				// clear adapter
				adapter.clear();
				for (int i = 0; i < arrayWithMembers.members.size(); i++) {
					adapter.add(arrayWithMembers.members.get(i));
				}
				adapter.notifyDataSetChanged();
			}
		}

	}

}
