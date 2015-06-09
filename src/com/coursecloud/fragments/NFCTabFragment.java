package com.coursecloud.fragments;

import java.io.IOException;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coursecloud.MainActivity;
import com.coursecloud.R;
import com.coursecloud.constants.GCMConstants;
import com.coursecloud.tools.FileIO;
import com.coursecloud.util.TimeUtilities;

public class NFCTabFragment extends Fragment implements GCMConstants {
	private View rootView;
	private static final int REQUEST_ENABLE_NFC = 1;
	private static final int REQUEST_DISABLE_NFC = 2;
	// nfc params
	private ImageView btnStart;
	private NfcAdapter mNfcAdapter;
	private boolean mResumed = false;
	private boolean mWriteMode = false;
	private TextView tv1;
	private TextView title;
	private Toast toast;
	private ImageView nfcSwitch;
	private TextView status;
	// basic UIs
	private RadioGroup rg;
	private RadioButton rb1, rb2;
	private DatePicker dp1;
	// Strings to deliver
	private String chooseDate1;
	private String strToGen = "";
	private String stFromDevice;

	private FileIO fio1;

	PendingIntent mNfcPendingIntent;
	IntentFilter[] mWriteTagFilters;
	IntentFilter[] mNdefExchangeFilters;

	// reg_id is the local user id
	private String reg_id;
	// TODO From user's registration
	private String USER_NAME;
	private String PORTRAIT_ID;
	private FileIO fio;

	public NFCTabFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.nfc_fragment, container, false);
		init();
		initNFC();
		return rootView;
	}

	private void initNFC() {
		mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		// Handle all of our received NFC intents in this activity.
		if (mNfcAdapter == null) {
			nfcSwitch.setEnabled(false);
			btnStart.setEnabled(false);
			title.setText("Your device does not support NFC");
			title.setTextColor(Color.RED);
			nfcSwitch.setEnabled(false);
		}
		mNfcPendingIntent = PendingIntent.getActivity(getActivity(), 0,
				new Intent(getActivity(), getClass())
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Intent filters for reading a note from a tag or exchanging over p2p.
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
		}
		mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

		// Intent filters for writing to a tag
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		mWriteTagFilters = new IntentFilter[] { tagDetected };
	}

	private void init() {
		USER_NAME = MainActivity.getUserName(getActivity());
		reg_id = MainActivity.GCM.getRegistrationId();
		PORTRAIT_ID = MainActivity.USER_PORTRAIT_ID;
		title = (TextView) rootView.findViewById(R.id.nfcpush_tvTitle);
		status = (TextView) rootView.findViewById(R.id.nfcpush_tvStatus);
		status.setText("");
		nfcSwitch = (ImageView) rootView.findViewById(R.id.nfcpush_switch1);
		nfcSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				turnNFC();
			}
		});
		tv1 = (TextView) rootView.findViewById(R.id.nfcpush_textView2);
		tv1.setText("");
		btnStart = (ImageView) rootView
				.findViewById(R.id.nfcpush_fragment_button1);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				USER_NAME = MainActivity.getUserName(getActivity());
				enableNdefExchangeMode();
				disableTagWriteMode();
				if (mResumed) {
					mNfcAdapter.enableForegroundNdefPush(getActivity(),
							getNoteAsNdef());
				}
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity());
				dialog.setTitle("Waiting...")
						.setMessage("Waiting for the other device")
						.setPositiveButton("Finish",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										if (id == DialogInterface.BUTTON_POSITIVE) {
											dialog.dismiss();
											disableNdefExchangeMode();
											enableTagWriteMode();
										}
									}
								}).show();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		mResumed = true;
		// Sticky notes received from Android
		if (mNfcAdapter != null) {
			if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getActivity()
					.getIntent().getAction())) {
				NdefMessage[] messages = getNdefMessages(getActivity()
						.getIntent());
				byte[] payload = messages[0].getRecords()[0].getPayload();
				setNFCMsg(new String(payload));
				getActivity().setIntent(new Intent()); // Consume this intent.
			}
			enableNdefExchangeMode();
			if (mNfcAdapter.isEnabled()) {
				status.setText("ON");
				status.setTextColor(Color.BLUE);
			} else {
				status.setText("OFF");
				status.setTextColor(Color.RED);
			}
		}
	}

	private void setNFCMsg(String body) {
		stFromDevice = body;
		nfcAddFriend();
		tv1.setText("");
	}

	// stFromDevice format = "FleetChat" + "regID:" + reg_id + "UserName:" +
	// USER_NAME + "PortraitID:" + PORTRAIT_ID;
	private void nfcAddFriend() {
		if (stFromDevice.split("regID:")[0].equalsIgnoreCase("FleetChat")) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			String name = stFromDevice.split("UserName:")[1].split("PortraitID:")[0];
			String gcmidFromOther = stFromDevice.split("regID:")[1]
					.split("UserName:")[0];
			String addDate = timeCreater(TimeUtilities.getTimeyyyyMMddHHmmss());
			String porID = stFromDevice.split("PortraitID:")[1];
			item.put(EXTRA_NAME, name);
			item.put(EXTRA_DATE, addDate);
			item.put(EXTRA_GCMID, gcmidFromOther);
			item.put(EXTRA_PORID, porID);

			fio1 = new FileIO(getActivity());
			if (!fio1.checkContactExist(gcmidFromOther)) {
				fio1.addContact(item);
				MainActivity.GCM.postDataAddFriend(gcmidFromOther, addDate,
						USER_NAME, porID);
				Toast t = Toast.makeText(getActivity(),
						"Friend has been added/updated successfully",
						Toast.LENGTH_SHORT);
				t.show();
			} else {
				Toast t = Toast.makeText(getActivity(),
						"You two are friends already!", Toast.LENGTH_SHORT);
				t.show();
			}
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		mResumed = false;
		if (mNfcAdapter != null) {
			mNfcAdapter.disableForegroundNdefPush(getActivity());
		}
	}

	protected void onNewIntent(Intent intent) {
		// NDEF exchange mode
		if (mNfcAdapter != null) {
			if (!mWriteMode
					&& NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent
							.getAction())) {
				NdefMessage[] msgs = getNdefMessages(intent);
				promptForContent(msgs[0]);
			}

			// Tag writing mode
			if (mWriteMode
					&& NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent
							.getAction())) {
				Tag detectedTag = intent
						.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				writeTag(getNoteAsNdef(), detectedTag);
			}
		}
	}

	private View.OnClickListener mTagWriter = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// Write to a tag for as long as the dialog is shown.
			disableNdefExchangeMode();
			enableTagWriteMode();

			new AlertDialog.Builder(getActivity())
					.setTitle("Touch tag to write")
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									disableTagWriteMode();
									enableNdefExchangeMode();
								}
							}).create().show();
		}
	};

	private void promptForContent(final NdefMessage msg) {
		Log.i("debug_ho", "promptForContent");
		new AlertDialog.Builder(getActivity())
				.setTitle("Replace current content?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								String body = new String(msg.getRecords()[0]
										.getPayload());
								setNFCMsg(body);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).show();
	}

	private NdefMessage getNoteAsNdef() {
		strToGen = "FleetChat" + "regID:" + reg_id + "UserName:" + USER_NAME
				+ "PortraitID:" + PORTRAIT_ID;
		byte[] textBytes = strToGen.getBytes();
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"text/plain".getBytes(), new byte[] {}, textBytes);
		return new NdefMessage(new NdefRecord[] { textRecord });
	}

	NdefMessage[] getNdefMessages(Intent intent) {
		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			getActivity().finish();
		}
		return msgs;
	}

	private void enableNdefExchangeMode() {
		mNfcAdapter.enableForegroundNdefPush(getActivity(), getNoteAsNdef());
		mNfcAdapter.enableForegroundDispatch(getActivity(), mNfcPendingIntent,
				mNdefExchangeFilters, null);
	}

	private void disableNdefExchangeMode() {
		mNfcAdapter.disableForegroundNdefPush(getActivity());
		mNfcAdapter.disableForegroundDispatch(getActivity());
	}

	private void enableTagWriteMode() {
		mWriteMode = true;
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		mWriteTagFilters = new IntentFilter[] { tagDetected };
		mNfcAdapter.enableForegroundDispatch(getActivity(), mNfcPendingIntent,
				mWriteTagFilters, null);
	}

	private void disableTagWriteMode() {
		mWriteMode = false;
		mNfcAdapter.disableForegroundDispatch(getActivity());
	}

	boolean writeTag(NdefMessage message, Tag tag) {
		int size = message.toByteArray().length;

		try {
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();

				if (!ndef.isWritable()) {
					toast("Tag is read-only.");
					return false;
				}
				if (ndef.getMaxSize() < size) {
					toast("Tag capacity is " + ndef.getMaxSize()
							+ " bytes, message is " + size + " bytes.");
					return false;
				}

				ndef.writeNdefMessage(message);
				toast("Wrote message to pre-formatted tag.");
				return true;
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						toast("Formatted tag and wrote message");
						return true;
					} catch (IOException e) {
						toast("Failed to format tag.");
						return false;
					}
				} else {
					toast("Tag doesn't support NDEF.");
					return false;
				}
			}
		} catch (Exception e) {
			toast("Failed to write tag");
		}

		return false;
	}

	private void turnNFC() {
		startActivity(new Intent("android.settings.NFC_SETTINGS"));
	}

	private void toast(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	private String timeCreater(String time) {
		String timeAfterchange;
		timeAfterchange = time.substring(0, 4) + "/" + time.substring(4, 6)
				+ "/" + time.substring(6, 8);
		return timeAfterchange;
	}
}
