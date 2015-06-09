/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.coursecloud.fragments;

import static com.coursecloud.tools.CommonUtilities.SENDER_ID;
import static com.coursecloud.tools.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.coursecloud.MainActivity;
import com.coursecloud.R;
import com.coursecloud.tools.AlertDialogManager;
import com.coursecloud.tools.ConnectionDetector;
import com.coursecloud.util.TimeUtilities;
import com.google.android.gcm.GCMRegistrar;

/**
 * Main UI for the demo app.
 */
@SuppressWarnings("deprecation")
public class DemoFragment extends Fragment {

	protected static final String TAG = "DemoActivity";
	// UI
	View _view;
	public static TextView mDisplay;

	// class
	AlertDialogManager alert = new AlertDialogManager();
	ConnectionDetector cd;
	AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkNotNull(SERVER_URL, "SERVER_URL");
		checkNotNull(SENDER_ID, "SENDER_ID");

		// Initailize
		cd = new ConnectionDetector(getActivity());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(getActivity(), "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

	}

	private void temp() {

		final String regId = MainActivity.GCM.getRegistrationId();
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(getActivity(), SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(getActivity())) {
				// Skips registration.
				mDisplay.append(getString(R.string.already_registered) + "\n");
			} else {

				// // Try to register again, but not in the UI thread.
				// // It's also necessary to cancel the thread onDestroy(),
				// // hence the use of AsyncTask instead of a raw thread.
				// final Context context = this;
				// mRegisterTask = new AsyncTask<Void, Void, Void>() {
				//
				// @Override
				// protected Void doInBackground(Void... params) {
				// ServerUtilities.register(context, regId);
				// return null;
				// }
				//
				// @Override
				// protected void onPostExecute(Void result) {
				// mRegisterTask = null;
				// }
				//
				// };
				// mRegisterTask.execute(null, null, null);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.demo_fragment, container, false);
		setView();
		temp();
		return _view;
	}

	private void setView() {
		mDisplay = (TextView) _view.findViewById(R.id.display);
		Button btn = (Button) _view.findViewById(R.id.demo_fragment_button1);
		btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				List<String> regIds = new ArrayList<String>();

				Log.w(TAG, "onClick");
				Log.w(TAG, MainActivity.GCM.getRegistrationId());
				regIds.add(MainActivity.GCM.getRegistrationId());
				// regIds.add(CommonUtilities.tempID2);
//				MainActivity.GCM.postDataSendMessage(regIds, "Welcome",
//						"Hello GCM");

			}
		});

		Button btn2 = (Button) _view.findViewById(R.id.demo_fragment_button2);
		btn2.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.w(TAG, "onClick");
				Log.w(TAG, MainActivity.GCM.getRegistrationId());

				// List<String> regIds = new ArrayList<String>();
				// // change to someone's gcmid
				// regIds.add(MainActivity.GCM.getRegistrationId());
				//
				//
				// MainActivity.GCM.postDataAddFriends(regIds,
				// TimeUtilities.getTimeyyyyMMddhhmmss(),
				// MainActivity.GCM.getRegistrationId());

				MainActivity.GCM.postDataAddFriend(
						MainActivity.GCM.getRegistrationId(),
						TimeUtilities.getTimeyyyyMMddhhmmss(), "Annoymous",
						"00");

			}
		});
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name));
		}
	}

}