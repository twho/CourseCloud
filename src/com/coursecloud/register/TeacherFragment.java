package com.coursecloud.register;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class TeacherFragment extends Fragment {

	protected static final String ADMIN_PWD = "ee303";
	private View rootView;
	private EditText pwd, name;
	private Button btnRegister;

	private void findView() {

		pwd = (EditText) rootView.findViewById(R.id.teacher_fragment_editText1);
		name = (EditText) rootView
				.findViewById(R.id.teacher_fragment_editText2);

		btnRegister = (Button) rootView
				.findViewById(R.id.teacher_fragment_button1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater
				.inflate(R.layout.teacher_fragment, container, false);
		
		findView();
		init();
		return rootView;
	}

	private void init() {
		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!pwd.getText().toString().equalsIgnoreCase(ADMIN_PWD)){
					new AlertDialogManager().showAlertDialog(getActivity(), "Wrong Password", "學生不能亂註冊喲^_^", false);
				} else if(name.getText().toString().equalsIgnoreCase("")){
					new AlertDialogManager().showAlertDialog(getActivity(), "Cannot be null", "Need enter Name", false);
				} else {

					final String gcm_id = MainActivity.GCM.getRegistrationId();
					final String mac = MainActivity.MacAddr;
					new TaskAddTeacher().execute(name.getText().toString(),mac, gcm_id);
				}
			}
		});
	}

	private class TaskAddTeacher extends AsyncTask<String, Void, String> {
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
			//TODO (Ho) addTeacher
			String result = new PHPUtilities().addTeacher(params);
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
						.setMessage("Some Error").show();
			}
			
			pd.dismiss();
		}

	};
	
}
