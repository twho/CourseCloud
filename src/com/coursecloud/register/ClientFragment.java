package com.coursecloud.register;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.coursecloud.MainActivity;
import com.coursecloud.R;
import com.coursecloud.tools.AlertDialogManager;
import com.coursecloud.util.PHPUtilities;

public class ClientFragment extends Fragment {

	private View rootView;

	// basic UIs
	private EditText nameToReg;
	private EditText studentIdToReg;
	private EditText emailToReg;
	private Button Login;
	private Button Register;
	// php recall
	public static String ifSent;
	public static String phpStatus;
	// Error message
	private String ErrorMessage;

	public ClientFragment() {
	}

	private void findView() {
		nameToReg = (EditText) rootView
				.findViewById(R.id.client_fragment_editText1);
		studentIdToReg = (EditText) rootView
				.findViewById(R.id.client_fragment_editText2);
		emailToReg = (EditText) rootView
				.findViewById(R.id.client_fragment_editText3);
		Login = (Button) rootView.findViewById(R.id.client_fragment_button1);
		Register = (Button) rootView.findViewById(R.id.client_fragment_button2);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.client_fragment, container, false);
		findView();
		init();
		return rootView;
	}

	private Boolean userNameCheck() {
		if (nameToReg.getText().toString() != null
				&& !nameToReg.getText().toString().equals("")) {
			return true;
		} else {
			ErrorMessage += "Name";
			return false;
		}
	}

	private Boolean studentidCheck() {
		if (studentIdToReg.getText().toString() != null
				&& !studentIdToReg.getText().toString().equals("")
				&& studentIdToReg.getText().toString().length() == 9) {
			return true;
		} else {
			ErrorMessage += " " + "studentid";
			return false;
		}
	}

	private Boolean mailCheck() {
		if (emailToReg.getText().toString() != null
				&& !emailToReg.getText().toString().equals("")
				&& emailToReg
						.getText()
						.toString()
						.matches(
								"(([A-Za-z0-9]+\\.?)+([A-Za-z0-9]+_?)+)+[A-Za-z0-9]+@([a-zA-Z0-9]+\\.)+(com|edu|gov)(\\.(tw|ch|hk))?")) {
			return true;
		} else {
			ErrorMessage += " " + "Email";
			return false;
		}
	}

	private Boolean macidCheck() {
		final String mac = MainActivity.MacAddr;
		if (mac != null && !mac.equals("")) {
			return true;
		} else {
			ErrorMessage += " " + "macid";
			return false;
		}
	}

	private Boolean gcmidCheck() {
		final String gcm_id = MainActivity.GCM.getRegistrationId();
		if (gcm_id != null && !gcm_id.equals("")) {
			return true;
		} else {
			ErrorMessage += " " + "gcmid";
			return false;
		}
	}

	private void init() {
		Login.setEnabled(false);
		Register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Put "final" in this layer, otherwise, getRegistrationId()
				// cannot work.
				final String gcm_id = MainActivity.GCM.getRegistrationId();
				final String mac = MainActivity.MacAddr;
				ErrorMessage = "";
				if (userNameCheck() && studentidCheck() && mailCheck()
						&& macidCheck() && gcmidCheck()) {

					new TaskAddMember().execute(nameToReg.getText().toString(),
							studentIdToReg.getText().toString(), emailToReg
									.getText().toString(), mac, gcm_id);
					SharedPreferences sp = getActivity().getSharedPreferences(
							MainActivity.PREF, Context.MODE_PRIVATE);
					SharedPreferences.Editor se = sp.edit();
					se.putString(MainActivity.PREF_NAME, nameToReg.getText()
							.toString().trim());
					se.putString(MainActivity.PREF_EMAIL, emailToReg.getText()
							.toString().trim());
					se.putString(MainActivity.PREF_ID, studentIdToReg.getText()
							.toString().trim());
					se.commit();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Error")
							.setMessage(ErrorMessage + " " + "Error").show();
				}
			}
		});
	}

	private class TaskAddMember extends AsyncTask<String, Void, String> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(getActivity());
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = new PHPUtilities().addMember(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			// TODO "DEBUG"
			Log.d("DEBUG", result);
			if (result.equalsIgnoreCase("insertSuccess")) {

				startActivity(new Intent(getActivity(), MainActivity.class));
				getActivity().finish();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Error")
						.setMessage("Please check your internet").show();
			}

			pd.dismiss();
		}

	};
}
