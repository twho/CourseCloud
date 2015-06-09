package com.coursecloud.score;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.coursecloud.MainActivity;
import com.coursecloud.R;
import com.coursecloud.constants.FileIOConstants;
import com.coursecloud.tools.AlertDialogManager;
import com.coursecloud.tools.ConnectionDetector;
import com.coursecloud.tools.FileIOForTeam;
import com.coursecloud.util.PHPUtilities;

public class ScoreFragment extends Fragment implements FileIOConstants {

	private View _view;
	private ListView _listview;
	private Button btn_refresh;
	private Button btn_rec;
	// show count
	private TextView tv_count5;
	private TextView tv_count4;
	private TextView tv_count3;
	// count if score has been given too much
	private int Dcount5, Ocount5, Ccount5;
	private int Dcount4, Ocount4, Ccount4;
	private int Dcount3, Ocount3, Ccount3;
	private static FileIOForTeam fioForTeam;
	// temp points
	private int tempPointD, tempPointO, tempPointC;
	private ArrayAdapter<String> recAdapter;

	public ScoreFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.score_fragment, container, false);

		// Check if Internet present
		ConnectionDetector cd = new ConnectionDetector(getActivity());
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			AlertDialogManager alert = new AlertDialogManager();
			alert.showAlertDialog(getActivity(), "Internet Connection Error",
					"Please connect to working Internet connection", false);
		}
		init();
		setListView();
		return _view;
	}

	private void init() {
		Dcount5 = 3;
		Dcount4 = 4;
		Dcount3 = 4;
		Ocount5 = 3;
		Ocount4 = 4;
		Ocount3 = 4;
		Ccount5 = 3;
		Ccount4 = 4;
		Ccount3 = 4;
		refreshCount();
		recAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1);
		btn_refresh = (Button) _view.findViewById(R.id.score_fragment_btn1);
		btn_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fioForTeam.delTeamDetail();
				new TaskGetScoreList().execute("SELECT * FROM table_team");
				refreshCount();
				setListView();
			}
		});
		btn_rec = (Button) _view.findViewById(R.id.score_fragment_btn2);
		btn_rec.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new TaskGetRateRec().execute(MainActivity.MacAddr);
				AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
				LayoutInflater li = LayoutInflater.from(getActivity());
				View recV = li.inflate(R.layout.rate_record_dialog, null);
				ad.setView(recV);
				ListView lv = (ListView) recV
						.findViewById(R.id.rate_rec_dialog_lv);
				lv.setAdapter(recAdapter);
				ad.show();
			}
		});
	}

	private void refreshCount() {
		setCount();
		tv_count5 = (TextView) _view.findViewById(R.id.score_fragment_count1);
		tv_count5.setText("You can only rate " + "D: " + Dcount5 + " / O: "
				+ Ocount5 + " / C: " + Ccount5 + " teams 5 points");
		tv_count4 = (TextView) _view.findViewById(R.id.score_fragment_count2);
		tv_count4.setText("You can only rate " + "D: " + Dcount4 + " / O: "
				+ Ocount4 + " / C: " + Ccount4 + " teams 4 points");
		tv_count3 = (TextView) _view.findViewById(R.id.score_fragment_count3);
		tv_count3.setText("You can only rate " + "D: " + Dcount3 + " / O: "
				+ Ocount3 + " / C: " + Ccount3 + " teams 3 points");
	}

	private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private SimpleAdapter simpleAdapter;

	private void setListView() {

		_listview = (ListView) _view.findViewById(R.id.score_fragment_listView);

		fioForTeam = new FileIOForTeam(getActivity());

		list = fioForTeam.getTeamList();

		_listview.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

		// TODO "DEBUG" use
		_listview.setOnItemLongClickListener(new OnItemLongClickListener() {

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
			simpleAdapter = new SimpleAdapter(getActivity(), list,
					R.layout.score_list_item, new String[] { TEAMNAME, TEAMNUM,
							RATE }, new int[] { R.id.score_fragment_title,
							R.id.score_fragment_tv1, R.id.score_fragment_tv2 }) {
				@Override
				public View getView(final int position, View convertView,
						ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					Button btn_rate = (Button) view
							.findViewById(R.id.score_fragment_btn);
					btn_rate.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							list = fioForTeam.getTeamList();
							String teamNum = ((String) list.get(position).get(
									TEAMNUM)).trim().substring(8);
							setRateDialog(position, teamNum);
						}
					});
					Button btn_detail = (Button) view
							.findViewById(R.id.score_fragment_btn_detail);
					btn_detail.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// Create Chat File, if it is not existed.
							File f = new File(fioForTeam.getTeamListDir(),
									(String) list.get(position).get(TEAMNAME));
							if (!f.exists()) {
								fioForTeam.writeFileToList(
										(String) list.get(position).get(
												TEAMNAME), "");
							}
							// show Team detail as a dialog
							// special case, so did not use AlertDialogManager
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							builder.setTitle(
									list.get(position).get(TEAMNUM) + "")
									.setMessage(
											"APP NAME: "
													+ list.get(position).get(
															TEAMNAME)
													+ "\n"
													+ "Member: "
													+ "\n"
													+ list.get(position).get(
															MEMBER1)
													+ "\n"
													+ list.get(position).get(
															MEMBER2)
													+ "\n"
													+ list.get(position).get(
															MEMBER3))
									.setPositiveButton("back", null)
									.setNegativeButton(
											"See Result",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													new TaskGetRateResult()
															.execute("team"
																	+ (position + 1));
												}
											}).show();
						}
					});
					return view;
				}

			};
			_listview.setAdapter(simpleAdapter);

		}

	}

	@Override
	public void onResume() {
		super.onResume();
		setCount();
		Log.i("count", Ocount3 + "");
		refreshCount();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void setCount() {
		new TaskGetRateCount().execute(MainActivity.MacAddr);

	}

	private void setRateDialog(final int position, String teamNum) {
		LayoutInflater li = LayoutInflater.from(getActivity());
		View v = li.inflate(R.layout.rate_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v)
				.setNegativeButton("Confirm",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								new TaskRatePost().execute(
										MainActivity.getUserName(getActivity()),
										MainActivity.MacAddr,
										"team" + String.valueOf((position + 1)),
										String.valueOf(tempPointD)
												+ String.valueOf(tempPointO)
												+ String.valueOf(tempPointC));
								refreshCount();
								setListView();
							}
						}).setPositiveButton("Cancel", null).show();
		TextView itemTitle = (TextView) v.findViewById(R.id.rate_dialog_title);
		if (list != null) {
			itemTitle.setText(list.get(position).get(TEAMNAME) + "");
		} else {
			itemTitle.setText("");
		}

		// TODO Spinners
		ArrayAdapter<CharSequence> adapter_points = ArrayAdapter
				.createFromResource(getActivity(), R.array.rate_activity_score,
						android.R.layout.simple_spinner_item);
		adapter_points
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner dif = (Spinner) v.findViewById(R.id.rate_dialog_spinner1);
		dif.setAdapter(adapter_points);
		dif.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				tempPointD = (position + 1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
		Spinner ori = (Spinner) v.findViewById(R.id.rate_dialog_spinner2);
		ori.setAdapter(adapter_points);
		ori.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				tempPointO = (position + 1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
		Spinner com = (Spinner) v.findViewById(R.id.rate_dialog_spinner3);
		com.setAdapter(adapter_points);
		com.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				tempPointC = (position + 1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
	}

	private class TaskGetScoreList extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(getActivity());
			dialog.setTitle("Loading...");
			dialog.setMessage("Please wait.");
			dialog.setCancelable(true);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = new PHPUtilities().getScoreList(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			try {
				JSONArray jsonArray = new JSONArray(result);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonData = jsonArray.getJSONObject(i);
					String team_num = "Number #"
							+ jsonData.getString("Team_num");
					String team_name = jsonData.getString("Team_name");
					String member1 = jsonData.getString("member1");
					String member2 = jsonData.getString("member2");
					String member3 = jsonData.getString("member3");
					item.put(TEAMNUM, team_num);
					item.put(TEAMNAME, team_name);
					item.put(MEMBER1, member1);
					item.put(MEMBER2, member2);
					item.put(MEMBER3, member3);
					if (!fioForTeam.checkTeamExist(TEAMNAME)) {
						fioForTeam = new FileIOForTeam(getActivity());
						fioForTeam.addTeam(item);
					}
				}
				new AlertDialogManager().showMessageDialog(getActivity(),
						"Success", "The team list has been refreshed.");
				dialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d("DEBUG", result);
		}

	};

	private class TaskRatePost extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;
		File f;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(getActivity());
			dialog.setTitle("Loading...");
			dialog.setMessage("Please wait.");
			dialog.setCancelable(true);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = new PHPUtilities().addRateData(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("result", result);
			if (result.equalsIgnoreCase("insertSuccess")) {
				dialog.dismiss();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Error")
						.setMessage("Please check your internet").show();
			}
		}

	};

	private class TaskGetRateResult extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(getActivity());
			dialog.setTitle("Loading...");
			dialog.setMessage("Please wait.");
			dialog.setCancelable(true);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = new PHPUtilities().getRateResult(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("result", result);
			if (result.substring(0, 9).equalsIgnoreCase("ccsuccess")) {
				dialog.dismiss();
				AlertDialogManager am = new AlertDialogManager();
				String dValue = "Difficulty of Skills: "
						+ format(result.split("CourseCloud")[0].substring(9));
				String oValue = "Originality: "
						+ format(result.split("CourseCloud")[1]);
				String cValue = "Completion: "
						+ format(result.split("CourseCloud")[2]);
				String total = getTotalRate(
						format(result.split("CourseCloud")[0].substring(9)),
						format(result.split("CourseCloud")[1]),
						format(result.split("CourseCloud")[2]));
				am.showMessageDialog(getActivity(), "Rate Result", dValue
						+ "\n" + oValue + "\n" + cValue + "\n"
						+ "Total ability: " + total);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Error")
						.setMessage("Please check your internet").show();
			}
		}
	};

	private class TaskGetRateCount extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = new PHPUtilities().getRateCount(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("resultHere", result);
			if (!result.equals("out")) {
				if (result.length() == 9) {
					if (result.substring(0, 9).equalsIgnoreCase("ccsuccess")) {
						Dcount5 = 3 - Integer.valueOf(result
								.split("CourseCloud")[0].substring(9));
						Dcount4 = 4 - Integer.valueOf(result
								.split("CourseCloud")[1]);
						Dcount3 = 4 - Integer.valueOf(result
								.split("CourseCloud")[2]);
						Ocount5 = 3 - Integer.valueOf(result
								.split("CourseCloud")[3]);
						Ocount4 = 4 - Integer.valueOf(result
								.split("CourseCloud")[4]);
						Ocount3 = 4 - Integer.valueOf(result
								.split("CourseCloud")[5]);
						Ccount5 = 3 - Integer.valueOf(result
								.split("CourseCloud")[6]);
						Ccount4 = 4 - Integer.valueOf(result
								.split("CourseCloud")[7]);
						Ccount3 = 4 - Integer.valueOf(result
								.split("CourseCloud")[8]);
						refreshCount();
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setTitle("Error")
								.setMessage("Please check your internet")
								.show();
					}
				}
			}
		}
	};

	private class TaskGetRateRec extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = new PHPUtilities().getRateRecord(params);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!"noRec".equalsIgnoreCase(result.substring(0, 5))) {
				if ("recordFound".equalsIgnoreCase(result.substring(0, 11))) {
					String rec = result.substring(11);
					if (recAdapter != null) {
						recAdapter.clear();
					}
					for (int i = 0; i < 15; i++) {
						if (rec.split("CC")[i].equalsIgnoreCase("noRec")) {
							recAdapter.add("team " + String.valueOf(i + 1)
									+ " : " + "No Record." + "\n");
						} else {
							recAdapter
									.add("team "
											+ String.valueOf(i + 1)
											+ " : "
											+ "D : "
											+ rec.split("CC")[i]
													.substring(0, 1)
											+ " / O : "
											+ rec.split("CC")[i]
													.substring(1, 2)
											+ " / C : "
											+ rec.split("CC")[i]
													.substring(2, 3) + "\n");
						}
					}

				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Error")
							.setMessage("Please check your internet").show();
				}
			} else {
				recAdapter.clear();
				recAdapter.add("No record is found.");
			}
		}
	};

	private String format(String res) {
		DecimalFormat df = new DecimalFormat("0.00");
		String result = df.format(Double.valueOf(res));
		return result;
	}

	private String getTotalRate(String res1, String res2, String res3) {
		String res = format(String.valueOf(Double.valueOf(res1)
				+ Double.valueOf(res2) + Double.valueOf(res3)));
		return res;
	}
}
