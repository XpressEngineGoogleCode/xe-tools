package arnia.xemobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class XEMobileStatisticsController extends Fragment {
	// the ui references
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.xemobilestatisticslayout, container,
				false);
		return view;
	}

	public void refreshStatistic() {
	}

}
