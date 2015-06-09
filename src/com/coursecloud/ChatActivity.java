package com.coursecloud;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.coursecloud.chatbubble.ChatArrayAdapter;
import com.coursecloud.chatbubble.ChatMessage;
import com.coursecloud.constants.FileIOConstants;
import com.coursecloud.constants.GCMConstants;
import com.coursecloud.tools.FileIO;
import com.coursecloud.util.StringUtilities;
import com.coursecloud.util.TimeUtilities;

public class ChatActivity extends ActionBarActivity implements GCMConstants,
		FileIOConstants {
	private static final String TAG = "ChatActivity";
	// UI
	private EditText _etMessage;
	private Button _btnSend;
	private ListView _lvContent;

	// Class
	private FileIO fio;
	private String _contact = "temp";

	// bubble params
	private ChatArrayAdapter chatArrayAdapter;
	private String _contactName;
	public String _portraitID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fio = new FileIO(getApplicationContext());

		setContentView(R.layout.chat_activity);
		Initialize();
		setView();

	}

	private void Initialize() {
		// Initialize ActionBar
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		try {
			// Get contact's GCM id.
			_contact = getIntent().getStringExtra(EXTRA_GCMID);
			
			Log.i("_contact", _contact);

			// actionBar.setTitle()
			ArrayList<HashMap<String, Object>> list = fio.getContact();
			for (HashMap<String, Object> item : list) {
				Log.i("item.get", item.get(EXTRA_GCMID)+"");
				if (item.get(EXTRA_GCMID).equals(_contact)) {
					_contactName = (String) item.get(EXTRA_NAME);
					_portraitID = (String) item.get(EXTRA_PORID);
					Log.i("_contactName", _contactName);
					actionBar.setTitle(_contactName);
					break;
				}
			}

			// Create File
			fio.addChatDetail(_contact, new HashMap<String, Object>());

		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	private void setView() {
		_lvContent = (ListView) findViewById(R.id.chat_activity_listView1);
		_lvContent.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		_lvContent.setDividerHeight(0);
		_etMessage = (EditText) findViewById(R.id.chat_activity_editText_msg);
		_btnSend = (Button) findViewById(R.id.chat_activity_btn_send);

		_btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// message handler
				fio.addChatDetail(_contact, _etMessage.getText().toString(),
						true);
				MainActivity.GCM.postDataSendMessage(_contact, _contactName,
						_etMessage.getText().toString());
				Log.i("_contactName", _contactName);


				// view handler
				setAdapter(getApplicationContext());
				_etMessage.setText("");
				moveListViewToBottom();
			}
		});

		// If _etMessage is empty, set _btnSend disabled.
		_etMessage.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				_btnSend.setEnabled(!StringUtilities.isEmpty(_etMessage
						.getText().toString()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		setAdapter(getApplicationContext());
		moveListViewToBottom();
	}

	/**
	 * Set/update adapter.
	 */
	private void setAdapter(Context context) {
		chatArrayAdapter = new ChatArrayAdapter(context,
				R.layout.activity_chat_singlemessage);
		chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				moveListViewToBottom();
			}
		});
		setContent();
		_lvContent.setAdapter(chatArrayAdapter);
	}

	public void setContent() {
		// Get data.
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		list = fio.getChatDetail(_contact);
		// TODO "DEBUG"
		Log.d("DEBUG", "fio.getChatDetail(_contact) = " + list);
		// TODO (Xu)get Name from the fio
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				addBubble((Boolean) list.get(i).get(WHO_POST), (String) list
						.get(i).get(MESSAGE), _contactName,
						timeCreater((String) list.get(i).get(TIME)),
						_portraitID);

			}
		}
		;
		// Refresh View.
		chatArrayAdapter.clear();
		chatArrayAdapter.addAll(list);
		_lvContent.setAdapter(chatArrayAdapter);
	}

	private void addBubble(Boolean side, String message, String user,
			String time, String porID) {
		chatArrayAdapter
				.add(new ChatMessage(!side, message, user, time, porID));
	}

	private void moveListViewToBottom() {
		_lvContent.setSelection(chatArrayAdapter.getCount() - 1);
	}

	@Override
	protected void onResume() {

		IntentFilter filter = new IntentFilter(INTENT_NOTICE_CHAT);
		registerReceiver(mBroadcastReceiver, filter);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		try {
			unregisterReceiver(mBroadcastReceiver);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		super.onDestroy();
	}

	public static final String INTENT_NOTICE_CHAT = "com.fleetchat.INTENT_NOTICE_CHAT";

	public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(INTENT_NOTICE_CHAT)) {
				// TODO "DEBUG"
				Log.e("DEBUG", "ChatBroadcastReceiver onReceive");
				String user = intent.getStringExtra(EXTRA_NAME);
				String porID = intent.getStringExtra(EXTRA_PORID);
				String msg = intent.getStringExtra(EXTRA_MESSAGE);
				String time = timeCreater(TimeUtilities.getTimeyyyyMMddHHmmss());
				addBubble(true, msg, user, time, porID);
				setAdapter(context);
				moveListViewToBottom();
			}

		}
	};

	public static void sendChat(Context context, Bundle bundle) {
		Intent intent = new Intent(INTENT_NOTICE_CHAT);
		context.sendBroadcast(intent);
	}

	// change time string to date format
	private String timeCreater(String time) {
		String timeAfterchange;
		timeAfterchange = time.substring(4, 6) + "/" + time.substring(6, 8)
				+ " " + time.substring(8, 10) + ":" + time.substring(10, 12);
		return timeAfterchange;
	}

}