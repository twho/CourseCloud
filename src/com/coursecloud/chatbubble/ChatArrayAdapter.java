package com.coursecloud.chatbubble;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coursecloud.ChatActivity;
import com.coursecloud.R;
import com.coursecloud.util.ImageUtilities;

public class ChatArrayAdapter extends ArrayAdapter {

	private ChatActivity ca;
	private TextView chatText;
	private TextView userName;
	private TextView userTime;
	private TextView clientName;
	private TextView clientTime;
	private List chatMessageList = new ArrayList();
	private LinearLayout singleMessageContainer;
	public static ImageView clientPortrait;

	public void add(ChatMessage object) {
		chatMessageList.add(object);
		super.add(object);
	}

	public ChatArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.chatMessageList.size();
	}

	public ChatMessage getItem(int index) {
		return (ChatMessage) this.chatMessageList.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.activity_chat_singlemessage,
					parent, false);
		}
		singleMessageContainer = (LinearLayout) row
				.findViewById(R.id.singleMessageContainer);
		ChatMessage chatMessageObj = getItem(position);
		chatText = (TextView) row.findViewById(R.id.singleMessage);
		userName = (TextView) row.findViewById(R.id.bubble_usr_name);
		userTime = (TextView) row.findViewById(R.id.bubble_usr_time);
		clientName = (TextView) row.findViewById(R.id.bubble_client_name);
		clientTime = (TextView) row.findViewById(R.id.bubble_client_time);
		clientPortrait = new ImageView(getContext());
		clientPortrait = (ImageView) row.findViewById(R.id.bubble_client_por);
		chatText.setText(chatMessageObj.message);
		chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_a
				: R.drawable.bubble_b);
		userName.setText(chatMessageObj.left ? "" : "Me");
		userName.setTextColor(Color.BLACK);
		userTime.setText(chatMessageObj.left ? "" : chatMessageObj.time);
		userTime.setTextColor(Color.BLACK);
		clientName.setText(chatMessageObj.left ? chatMessageObj.user : "");
		clientTime.setText(chatMessageObj.left ? chatMessageObj.time : "");
		clientName.setTextColor(Color.BLACK);
		clientTime.setTextColor(Color.BLACK);
		if (chatMessageObj.left) {
			clientPortrait.setVisibility(View.VISIBLE);
			if (!chatMessageObj.porID.equalsIgnoreCase("noImage")) {
				clientPortrait.setImageBitmap(ImageUtilities
						.decodeBase64(chatMessageObj.porID));
			} else {
				clientPortrait.setImageDrawable(getContext().getResources()
						.getDrawable(R.drawable.porknown));
			}
		} else {
			clientPortrait.setVisibility(View.GONE);
		}
		singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT
				: Gravity.RIGHT);
		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);
	}
}
