package com.coursecloud.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.coursecloud.ChatActivity;
import com.coursecloud.MainActivity;
import com.coursecloud.R;
import com.coursecloud.constants.GCMConstants;
import com.coursecloud.tools.AlertDialogManager;
import com.coursecloud.tools.FileIO;
import com.coursecloud.util.PHPUtilities;
import com.coursecloud.util.TimeUtilities;

public class CourseFragment extends Fragment implements GCMConstants {
	private static final String TAG = "CourseFragment";
	private static FileIO fio;
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.contact_fragment, container, false);
		setListView();
		init();
		return view;
	}

	private Button btn_refresh;
	private TextView tv_time;
	public static String ifSent;
	public static String phpStatus;

	private void init() {
		btn_refresh = (Button) view.findViewById(R.id.contact_fragment_button1);
		btn_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fio.delContact();
				new TaskGetContactList().execute("SELECT * FROM table_reg");
				setListView();
				tv_time.setText(" Last refreshed: "
						+ TimeUtilities.getTimeyyyy_MM_dd_HH_mm());
			}
		});
		tv_time = (TextView) view.findViewById(R.id.contact_fragment_textView1);
		setTimeText();
	}

	@Override
	public void onResume() {
		setTimeText();
		super.onResume();
	}

	private class TaskGetContactList extends AsyncTask<String, Void, String> {
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
			String result = new PHPUtilities().getMemberList(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			// TODO "DEBUG"
			Log.d("DEBUG", result);
			HashMap<String, Object> item = new HashMap<String, Object>();
			try {
				JSONArray jsonArray = new JSONArray(result);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonData = jsonArray.getJSONObject(i);
					if (!jsonData.getString("mac").equalsIgnoreCase(
							MainActivity.MacAddr)) {
						String name = jsonData.getString("Username");
						String gcm_id = jsonData.getString("gcmid");
						String email = jsonData.getString("email");
						String studentId = jsonData.getString("Studentid");
						String addDate = TimeUtilities
								.getTimeyyyy_MM_dd_HH_mm();
						item.put(EXTRA_NAME, name);
						item.put(EXTRA_DATE, addDate);
						item.put(EXTRA_GCMID, gcm_id);
						item.put(EXTRA_STUDENTID, studentId);
						item.put(EXTRA_EMAIL, email);
						if (!fio.checkContactExist(gcm_id)) {
							fio = new FileIO(getActivity());
							fio.addContact(item);
						}
					} else {
						MainActivity.myName = jsonData.getString("Username");
					}

				}
				new AlertDialogManager().showMessageDialog(getActivity(),
						"Success", "The attendance list has been refreshed.");
			} catch (Exception e) {
				Log.e("log_tag", e.toString());
				new AlertDialogManager().showMessageDialog(getActivity(),
						"Error", "The ERROR might be caused by: " + "\n"
								+ "1.The server is offline." + "\n"
								+ "2.Your device is offline.");
			}

			setListView();
			pd.dismiss();
		}

	};

	private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private ListView listview;
	private SimpleAdapter simpleAdapter;

	private void setListView() {

		listview = (ListView) view
				.findViewById(R.id.contact_fragment_listView1);

		fio = new FileIO(getActivity());

		list = fio.getContact();
		Log.d(TAG, "list = " + list);

		listview.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Create Chat File, if it is not existed.
				File f = new File(fio.getChatDir(), (String) list.get(position)
						.get(EXTRA_GCMID));
				if (!f.exists()) {
					fio.writeFileToChat(
							(String) list.get(position).get(EXTRA_GCMID), "");
				}

				// Change to Chat Activity.
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				Bundle b = new Bundle();
				b.putString(EXTRA_GCMID,
						(String) list.get(position).get(EXTRA_GCMID));
				b.putString(EXTRA_NAME,
						(String) list.get(position).get(EXTRA_NAME));
				intent.putExtras(b);
				startActivity(intent);
			}
		});

		// TODO "DEBUG" use
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				new AlertDialogManager()
						.showMessageDialog(getActivity(), "position = "
								+ position, list.get(position).toString());
				return false;
			}
		});

		if (list != null) {
			// TODO [Check] Is it need to fix?
			simpleAdapter = new SimpleAdapter(getActivity(), list,
					R.layout.simple_list_item_3, new String[] { "", EXTRA_NAME,
							EXTRA_DATE, EXTRA_STUDENTID }, new int[] {
							R.id.simple_list_item_3_imageView1,
							R.id.simple_list_item_3_textView1,
							R.id.simple_list_item_3_textView2,
							R.id.simple_list_item_3_textView3 });
			listview.setAdapter(simpleAdapter);
		}

	}

	private void setTimeText() {
		if (list == null) {
			tv_time.setText("");
		} else {
			tv_time.setText(" Last refreshed: "
					+ list.get(list.size() - 1).get(EXTRA_DATE) + "");
		}
	}

	private int getPortrait(String porID) {
		int POR_ID = Integer.valueOf(porID);
		int portraitID = 0;
		switch (POR_ID) {
		case 00:
			portraitID = R.drawable.porknown;
			break;
		case 11:
			portraitID = R.drawable.por011;
			break;
		case 12:
			portraitID = R.drawable.por012;
			break;
		case 13:
			portraitID = R.drawable.por013;
			break;
		case 14:
			portraitID = R.drawable.por014;
			break;
		case 21:
			portraitID = R.drawable.por021;
			break;
		case 22:
			portraitID = R.drawable.por022;
			break;
		case 23:
			portraitID = R.drawable.por023;
			break;
		case 24:
			portraitID = R.drawable.por024;
			break;
		case 31:
			portraitID = R.drawable.por031;
			break;
		case 32:
			portraitID = R.drawable.por032;
			break;
		case 33:
			portraitID = R.drawable.por033;
			break;
		case 34:
			portraitID = R.drawable.por033;
			break;
		}
		return portraitID;
	}

}
