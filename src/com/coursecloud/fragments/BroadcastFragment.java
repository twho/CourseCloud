package com.coursecloud.fragments;

import com.coursecloud.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BroadcastFragment extends Fragment{
	
	private View rootView;
	
	public BroadcastFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.broadcast_fragment, container, false);
		return rootView;
	}
}
