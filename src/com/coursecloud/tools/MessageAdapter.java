package com.coursecloud.tools;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coursecloud.R;
import com.coursecloud.constants.GCMConstants;
import com.coursecloud.util.ImageUtilities;

public class MessageAdapter extends BaseAdapter implements GCMConstants {
	private Context context;
	private ArrayList<HashMap<String, Object>> list;
	private LayoutInflater layoutInflater;
	private FileIO fio;

	public MessageAdapter(Context context,
			ArrayList<HashMap<String, Object>> list) {
		this.context = context;
		this.list = list;
		fio = new FileIO(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		ImageView imageView;
		TextView tvName;
		TextView tvContent;
		TextView tvTime;
	}

	@SuppressLint({ "InflateParams", "NewApi" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		layoutInflater = LayoutInflater.from(context);
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.chat_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) convertView
					.findViewById(R.id.chat_list_item_tv_name);
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.chat_list_item_tv_time);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.chat_list_item_tv_content);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.chat_list_item_imageView1);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Log.i("data", list.get(position).get(IMAGE) + "");
		if (!"noImage".equalsIgnoreCase(fio.getContactImage((String) list.get(
				position).get(IMAGE)))) {
			String bitmapStr = fio.getContactImage((String) list.get(position)
					.get(IMAGE));
			Bitmap bmp = ImageUtilities.decodeBase64(bitmapStr);
			viewHolder.imageView.setImageBitmap(bmp);
		} else {
			viewHolder.imageView.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.porknown));
		}
		viewHolder.tvName.setText((String) list.get(position).get(NAME));
		viewHolder.tvContent.setText((String) list.get(position).get(CONTENT));
		viewHolder.tvTime
				.setText((String) list.get(position).get(MESSAGE_TIME));
		return convertView;
	}
}
