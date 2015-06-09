package com.coursecloud.register;

import com.coursecloud.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TAFragment extends Fragment{
	
	private View rootView;

	public TAFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.ta_fragment, container,
				false);
		init();
		return rootView;
	}

	private void init() {

	}
}
