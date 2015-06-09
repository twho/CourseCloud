package com.coursecloud.fragments;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.coursecloud.MainActivity;
import com.coursecloud.R;
import com.coursecloud.constants.GCMConstants;
import com.coursecloud.tools.AlertDialogManager;
import com.coursecloud.tools.FileIO;
import com.coursecloud.util.ImageUtilities;
import com.coursecloud.util.PHPUtilities;
import com.coursecloud.util.TimeUtilities;

public class SettingsFragment extends Fragment implements GCMConstants {

	private View rootView;

	// settings fragment UI
	private EditText edName;
	private EditText edMail;
	private EditText edId;
	public static ImageView personalPor;
	public ImageView ivNow;
	public String porBuffer;
	// reg_id is the local user id
	private String reg_id;
	// TODO From user's registration
	private String USER_NAME;
	private FileIO fio;

	// photo upload
	public static final int MODE_IMAGE = 1;
	public static final int MODE_IMAGE_GALLERY = 3;
	public static final int REQUEST_CODE_IMAGE = 0;
	// alertDialog
	private AlertDialogManager adm;
	private ImageUtilities iu;

	public SettingsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.settings_fragment, container,
				false);
		init();
		return rootView;
	}

	private void init() {
		reg_id = MainActivity.GCM.getRegistrationId();
		USER_NAME = MainActivity.getUserName(getActivity());
		adm = new AlertDialogManager();
		iu = new ImageUtilities();
		edName = (EditText) rootView.findViewById(R.id.settings_fragment_ed1);
		edMail = (EditText) rootView.findViewById(R.id.settings_fragment_ed2);
		edId = (EditText) rootView.findViewById(R.id.settings_fragment_ed3);
		personalPor = (ImageView) rootView
				.findViewById(R.id.settings_personal_portrait);
		new TaskGetMyPortrait().execute(MainActivity.MacAddr);
		// setSettingPortrait();
		edName.setEnabled(false);
		edMail.setEnabled(false);
		edId.setEnabled(false);
		edName.setText(MainActivity.getUserName(getActivity()));
		edMail.setText(MainActivity.getUserEmail(getActivity()));
		edId.setText(MainActivity.getUserId(getActivity()));
		personalPor.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity());
				dialog.setTitle("Choose by : ")
						.setPositiveButton("From Camera",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent takeIntent;
										takeIntent = new Intent(
												MediaStore.ACTION_IMAGE_CAPTURE);
										startActivityForResult(takeIntent,
												REQUEST_CODE_IMAGE);
									}
								})
						.setNegativeButton("From My Photo",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent takeIntent;
										takeIntent = new Intent();
										takeIntent.setType("image/*");
										takeIntent
												.setAction(Intent.ACTION_GET_CONTENT);
										startActivityForResult(Intent
												.createChooser(takeIntent,
														"data source: "),
												REQUEST_CODE_IMAGE);
									}
								}).show();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_IMAGE) {
				ContentResolver resolver = getActivity().getContentResolver();
				Uri uri = data.getData();
				Bitmap bmp = null;
				try {
					bmp = MediaStore.Images.Media.getBitmap(resolver, uri);
					String path = iu.getPath(getActivity(), uri);
					Log.i("path", path);
					new TaskPostPortrait().execute(MainActivity.MacAddr, path);
					setPortrait(bmp, uri);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void setPortrait(Bitmap bmp, Uri uri) {
		personalPor.setImageBitmap(bmp);
	}

	// if success: CCinsertCCimageCCok
	// if not: CCnotCCreg
	private class TaskPostPortrait extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(getActivity());
			adm.showProgressDialog("Uploading", "Please wait...", dialog);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = new PHPUtilities().postPortrait(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("result1", result);
			if (!"".equalsIgnoreCase(result)) {
				if (result.length() > 11) {
					if ("CC".equalsIgnoreCase(result.substring(0, 2))
							&& result.split("CC")[3].equalsIgnoreCase("ok")) {
						dialog.dismiss();
						adm.showMessageDialog(getActivity(), "Upload finished",
								"Your portrait has been uploaded.");
					}
				}
			} else {
				adm.showMessageDialog(getActivity(), "Error", "Caused by: "
						+ "\n" + "1.No Internet connected." + "\n"
						+ "2.You have not registered yet.");
			}
		}
	}

	private class TaskGetMyPortrait extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(getActivity());
			adm.showProgressDialog("Loading", "Please Wait...", dialog);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = new PHPUtilities().getMyPortrait(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			Log.d("DEBUG", result);
			try {
				JSONArray jsonArray = new JSONArray(result);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonData = jsonArray.getJSONObject(i);
					if (!"".equalsIgnoreCase(jsonData.getString("image"))) {
						String image = jsonData.getString("image");
						Log.d("DEBUG", image);
						personalPor.setImageBitmap(ImageUtilities.decodeBase64(image));
						dialog.dismiss();
					} else {
						adm.showMessageDialog(getActivity(), "Error",
								"You haven't set your personal portrait.");
					}

				}
			} catch (Exception e) {
				Log.e("log_tag", e.toString());
				new AlertDialogManager().showMessageDialog(getActivity(),
						"Error", "The ERROR might be caused by: " + "\n"
								+ "1.The server is offline." + "\n"
								+ "2.Your device is offline.");
			}
		}
	}

}
