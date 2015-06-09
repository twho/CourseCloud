package com.coursecloud.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.coursecloud.ChatActivity;
import com.coursecloud.R;
import com.coursecloud.constants.GCMConstants;
import com.coursecloud.tools.AlertDialogManager;
import com.coursecloud.tools.ContactListAdapter;
import com.coursecloud.tools.FileIO;
import com.coursecloud.tools.MessageAdapter;
import com.coursecloud.util.TimeUtilities;

public class MessageFragment extends Fragment implements GCMConstants {
	protected static final String TAG = "Fragment2";
	public static final String CHAT_TITLE = "CHAT_TITLE";

	// UI
	private View _view;
	private TextView _Display;
	private ListView _listview;

	// Class
	private FileIO fio;

	// Variables
	private String[] chatList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.chat_list_fragment, container, false);
		fio = new FileIO(getActivity());
		setView();
		return _view;
	}

	private void setView() {
		_Display = (TextView) _view
				.findViewById(R.id.chat_list_fragment_display);
		_listview = (ListView) _view
				.findViewById(R.id.chat_list_fragment_listView1);
		Button _btn = (Button) _view
				.findViewById(R.id.chat_list_fragment_button1);
		_btn.setVisibility(View.GONE);

		// Button
		_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				fio.writeFileToChat(TimeUtilities.getTimehhmm(), "");

				setAdatper();
			}
		});

		// ListView
		setAdatper();
		_listview.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				Bundle b = new Bundle();
				b.putString(GCMConstants.EXTRA_GCMID, chatList[position]);
				intent.putExtras(b);
				startActivity(intent);
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
	}

	private ArrayList<HashMap<String, Object>> list;
	private MessageAdapter messageAdapter;

	private void setAdatper() {
		list = new ArrayList<HashMap<String, Object>>();
		chatList = fio.getChatDirList();
		if (chatList.length > 0) {
			if (fio.getContactName(chatList[0]) != null) {
				_Display.setText("");
				for (int i = 0; i < chatList.length; i++) {
					HashMap<String, Object> item = new HashMap<String, Object>();
					// TODO [Check] Is it need to fix?
					item.put(IMAGE, chatList[i]);
					//put gcmid to get image
					item.put(NAME, fio.getContactName(chatList[i]));
					item.put(CONTENT, fio.getLastChatDetail(chatList[i]));
					item.put(MESSAGE_TIME,
							fio.getChatDetailModifiedTime(chatList[i]));
					list.add(item);
				}
			}
		} else {
			_Display.setText("No message record.");
		}

		messageAdapter = new MessageAdapter(getActivity(), list);

		_listview.setAdapter(messageAdapter);
	}

	@Override
	public void onResume() {
		setAdatper();
		super.onResume();
	}
}
