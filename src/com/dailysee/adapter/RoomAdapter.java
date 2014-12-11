package com.dailysee.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.RoomType;
import com.dailysee.util.UiHelper;

public class RoomAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<RoomType> items;

	public RoomAdapter(Context context, ArrayList<RoomType> items) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.items = items;
	}

	@Override
	public int getCount() {
		return items != null ? items.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_room, null);
			holder = new ViewHolder(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final RoomType roomType = (RoomType) getItem(position);

		if (roomType.imgs != null && roomType.imgs.size() > 0) {
			AppController.getInstance().getImageLoader().get(roomType.imgs.get(0).url, ImageLoader.getImageListener(holder.image, R.drawable.ic_image, R.drawable.ic_image));
			holder.image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UiHelper.toBrowseImageList(context, roomType.imgs, 0);
				}
			});
		}

		holder.name.setText(roomType.name);
		holder.desc.setText(roomType.name);
		
		return convertView;
	}

	private static class ViewHolder {

		public ImageView image;
		public TextView name;
		public TextView desc;

		public ViewHolder(View convertView) {
			image = (ImageView) convertView.findViewById(R.id.iv_image);
			name = (TextView) convertView.findViewById(R.id.tv_name);
			desc = (TextView) convertView.findViewById(R.id.tv_desc);

			convertView.setTag(this);
		}

	}

}
