package com.coursecloud;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.coursecloud.fragments.AttendanceFragment;
import com.coursecloud.fragments.BroadcastFragment;
import com.coursecloud.fragments.MessageFragment;
import com.coursecloud.fragments.SettingsFragment;
import com.coursecloud.register.RegisterActivity;
import com.coursecloud.score.ScoreFragment;
import com.coursecloud.tools.AlertDialogManager;
import com.coursecloud.tools.GCMUtilities;
import com.coursecloud.util.PHPUtilities;
import com.coursecloud.util.TimeUtilities;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements TabListener {

	protected static final String TAG = "MainActivity";
	public static final String PREF = "PREF";
	public static final String PREF_NAME = "PREF_NAME";
	public static final String PREF_EMAIL = "PREF_EMAIL";
	public static final String PREF_ID = "PREF_ID";
	public static final String PREF_PORID = "PREF_PORID";
	public static final String PREF_IMAGE = "PREF_IMAGE";
	public static GCMUtilities GCM;
	public static String USER_PORTRAIT_ID = "";
	// Bluetooth params
	public static BluetoothAdapter myBluetoothAdapter;
	public static String MacAddr;
	public static String myName;

	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();

	// Wifi params

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GCM = new GCMUtilities(this);
		setContentView(R.layout.activity_main);
		setTab();
		StrictMode.setThreadPolicy(policy);
		initBT();
		myName = getUserName(this);
	}

	private void setTab() {

		getSupportFragmentManager().executePendingTransactions();
		final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.

		android.support.v7.app.ActionBar.Tab attTab = actionBar.newTab()
				.setTabListener(this).setText("Attendance");
		actionBar.addTab(attTab);
		android.support.v7.app.ActionBar.Tab messTab = actionBar.newTab()
				.setTabListener(this).setText("Message");
		actionBar.addTab(messTab);
		android.support.v7.app.ActionBar.Tab boardTab = actionBar.newTab()
				.setTabListener(this).setText("Broadcast");
		actionBar.addTab(boardTab);
		android.support.v7.app.ActionBar.Tab settingTab = actionBar.newTab()
				.setTabListener(this).setText("Settings");
		actionBar.addTab(settingTab);
		android.support.v7.app.ActionBar.Tab scoreTab = actionBar.newTab()
				.setTabListener(this).setText("Score");
		actionBar.addTab(scoreTab);
	}

	private void initBT() {
		myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		MacAddr = myBluetoothAdapter.getAddress();
		if ("".equalsIgnoreCase(MacAddr)) {
			WifiManager wifiMan = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInf = wifiMan.getConnectionInfo();
			String MacAddr = wifiInf.getMacAddress();
		}
		if ("".equalsIgnoreCase(MacAddr)) {
			adm.showMessageDialog(MainActivity.this, "Error",
					"No mac address detected.");
		}
		try {
			new TaskCheckIP().execute(URLReader(getApplicationContext()));
			Log.d("IPNOW", URLReader(getApplicationContext()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	AlertDialogManager adm = new AlertDialogManager();

	@Override
	public void onResume() {
		super.onResume();
		GCM.onResume();
		new TaskCheckMacExist().execute(MacAddr);
	}

	private class TaskCheckMacExist extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String result = new PHPUtilities().checkMacExist(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (result.equalsIgnoreCase("macOk")) {
				Intent intent = new Intent(MainActivity.this,
						RegisterActivity.class);
				startActivity(intent);
				MainActivity.this.finish();
			}
		}

	};

	@Override
	public void onDestroy() {
		GCM.onDestroy();
		super.onDestroy();
	}

	public static String getUserName(Context context) {
		return context.getSharedPreferences(MainActivity.PREF,
				Context.MODE_PRIVATE).getString(MainActivity.PREF_NAME, "");
	}

	public static String getUserEmail(Context context) {
		return context.getSharedPreferences(MainActivity.PREF,
				Context.MODE_PRIVATE).getString(MainActivity.PREF_EMAIL, "");
	}

	public static String getUserId(Context context) {
		return context.getSharedPreferences(MainActivity.PREF,
				Context.MODE_PRIVATE).getString(MainActivity.PREF_ID, "");
	}

	@Override
	public void onTabReselected(android.support.v7.app.ActionBar.Tab arg0,
			FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(android.support.v7.app.ActionBar.Tab tab,
			FragmentTransaction arg1) {
		Fragment fragment = null;
		switch (tab.getPosition()) {
		case 0:
			fragment = new AttendanceFragment();
			tab.setText("Attendance");
			break;
		case 1:
			fragment = new MessageFragment();
			tab.setText("Message");
			break;
		case 2:
			fragment = new BroadcastFragment();
			tab.setText("Broadcast");
			break;
		case 3:
			fragment = new SettingsFragment();
			tab.setText("Settings");
			break;
		case 4:
			fragment = new ScoreFragment();
			tab.setText("Score");
			break;
		}

		try {
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with
			// this fragment,
			// and add the transaction to the back stack
			transaction.replace(R.id.main_activity_frameLayout, fragment);

			// Commit the transaction
			transaction.commit();

		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab,
			FragmentTransaction arg1) {
		switch (tab.getPosition()) {
		case 0:
			tab.setText("Attendance");
			break;
		case 1:
			tab.setText("Message");
			break;
		case 2:
			tab.setText("Broadcast");
			break;
		case 3:
			tab.setText("Settings");
			break;
		}
	}

	public static String URLReader(Context context) throws Exception {
		URL oracle = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				oracle.openStream()));

		String inputLine = null;
		inputLine = in.readLine();

		String IP = "36.229.4.9";
		if (inputLine.contains(IP))
			Log.d("Success", "IP Correct!");
		else
			Log.d("Fail", "IP Wrong!");
		in.close();
		return inputLine;

	}

	private class TaskCheckIn extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = new PHPUtilities().checkIn(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("DEBUG", result);
			if (!"".equalsIgnoreCase(result)) {
				if ("checkInSuccess".equalsIgnoreCase(result)) {
					adm.showMessageDialog(
							MainActivity.this,
							"Success",
							"You have checked in on "
									+ timeCreater(TimeUtilities
											.getTimeyyyyMMdd()));
				} else if ("notReg".equalsIgnoreCase(result)) {
					adm.showMessageDialog(MainActivity.this, "Error",
							"You have not registered.");
				}
			}
		}
	}

	private class TaskCheckIP extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = new PHPUtilities().checkIP(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("DEBUG", result);
			if (!"".equalsIgnoreCase(result)) {
				if ("IPOk".equalsIgnoreCase(result)) {
					new TaskCheckIn().execute(getUserName(MainActivity.this),
							MacAddr, "20150202");
				}
			}
		}
	}

	// change time string to date format
	private String timeCreater(String time) {
		String timeAfterchange;
		timeAfterchange = time.substring(0, 4) + "/" + time.substring(4, 6)
				+ "/" + time.substring(6, 8);
		return timeAfterchange;
	}

}
