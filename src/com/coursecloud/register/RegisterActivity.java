package com.coursecloud.register;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

import com.coursecloud.R;
import com.coursecloud.R.id;
import com.coursecloud.R.layout;
import com.coursecloud.R.menu;
import com.coursecloud.tools.AlertDialogManager;

@SuppressWarnings("deprecation")
public class RegisterActivity extends ActionBarActivity implements TabListener {
	protected static final String TAG = "MenuActivity";
	public static final String PREF = "REG_PREF";
	public static final String PREF_NAME = "REG_PREF_NAME";
	public static final String PREF_EMAIL = "REG_PREF_EMAIL";
	public static final String PREF_STUDENTID = "REG_PREF_STUDENTID";
	public static String USER_PORTRAIT_ID = "";

	public static boolean ifRegister = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTab();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void setTab() {

		getSupportFragmentManager().executePendingTransactions();
		final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.

		android.support.v7.app.ActionBar.Tab clientTab = actionBar.newTab()
				.setTabListener(this).setText("Student");
		actionBar.addTab(clientTab);
		android.support.v7.app.ActionBar.Tab adminTab = actionBar.newTab()
				.setTabListener(this).setText("Teacher");
		actionBar.addTab(adminTab);
		// android.support.v7.app.ActionBar.Tab taTab = actionBar.newTab()
		// .setTabListener(this).setText("TA");
		// actionBar.addTab(taTab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
			fragment = new ClientFragment();
			tab.setText("Student");
			break;
		case 1:
			fragment = new TeacherFragment();
			tab.setText("Teacher");
			break;
		case 2:
			fragment = new TAFragment();
			tab.setText("TA");
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
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}

	@Override
	public void onBackPressed() {
		new AlertDialogManager().showExitDialog(RegisterActivity.this);
	}

}
