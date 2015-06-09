package com.coursecloud.score;

import com.coursecloud.MainActivity;
import com.coursecloud.R;
import com.coursecloud.tools.AlertDialogManager;
import com.coursecloud.tools.ConnectionDetector;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class ResultFragment extends Fragment {
	private View _view;
	private ListView _listview;

	public ResultFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.result_fragment, container, false);

		// Check if Internet present
		ConnectionDetector cd = new ConnectionDetector(getActivity());
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			AlertDialogManager alert = new AlertDialogManager();
			alert.showAlertDialog(getActivity(), "Internet Connection Error",
					"Please connect to working Internet connection", false);
		}
		return _view;
	}
}
