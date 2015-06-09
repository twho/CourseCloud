package com.coursecloud.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coursecloud.R;
import com.coursecloud.constants.FileIOConstants;
import com.coursecloud.constants.GCMConstants;
import com.coursecloud.util.ImageUtilities;

public class ContactListAdapter extends BaseAdapter implements GCMConstants {

	private Context context;
	private ArrayList<HashMap<String, Object>> list;
	private LayoutInflater layoutInflater;
	private FileIO fio;

	public ContactListAdapter(Context context,
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
		TextView tvId;
		TextView tvTime;
	}

	@SuppressLint({ "InflateParams", "NewApi" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		layoutInflater = LayoutInflater.from(context);
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.simple_list_item_3,
					null);
			viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) convertView
					.findViewById(R.id.simple_list_item_3_textView1);
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.simple_list_item_3_textView2);
			viewHolder.tvId = (TextView) convertView
					.findViewById(R.id.simple_list_item_3_textView3);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.simple_list_item_3_imageView1);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Log.i("data", list.get(position).get(EXTRA_PORID) + "");
		if (!"noImage".equalsIgnoreCase(fio.getContactImage((String) list.get(
				position).get(EXTRA_GCMID)))) {
			String bitmapStr = fio.getContactImage((String) list.get(position)
					.get(EXTRA_GCMID));
			Bitmap bmp = ImageUtilities.decodeBase64(bitmapStr);
			viewHolder.imageView.setImageBitmap(bmp);
		} else {
			viewHolder.imageView.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.porknown));
		}
		viewHolder.tvName.setText((String) list.get(position).get(EXTRA_NAME));
		viewHolder.tvId.setText((String) list.get(position)
				.get(EXTRA_STUDENTID));
		viewHolder.tvTime.setText((String) list.get(position).get(EXTRA_DATE));
		return convertView;
	}
}
