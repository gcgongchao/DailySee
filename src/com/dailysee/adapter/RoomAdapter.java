package com.dailysee.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Room;
import com.dailysee.bean.RoomType;
import com.dailysee.util.Constants;
import com.dailysee.util.UiHelper;
import com.dailysee.util.Utils;

public class RoomAdapter extends BaseExpandableListAdapter implements OnClickListener {

	private Context context;
	private LayoutInflater mInflater;
	private List<RoomType> mGroupList;
	private Map<Long, List<Room>> mChildrenList;
	private OnRoomClickListener listener;
	private int from;

	public RoomAdapter(Context context, int from, List<RoomType> mGroupList, Map<Long, List<Room>> mChildrenList, OnRoomClickListener listener) {
		this.context = context;
		this.from = from;
		mInflater = LayoutInflater.from(context);
		this.mGroupList = mGroupList;
		this.mChildrenList = mChildrenList;
		this.listener = listener;
	}

	@Override
	public int getGroupCount() {
		return mGroupList != null ? mGroupList.size() : 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		RoomType roomType = (RoomType) getGroup(groupPosition);
		List<Room> roomList = mChildrenList.get(roomType.roomTypeId);
		int size = 0;
		if (roomList != null && roomList.size() > 0 ) {
			size = roomList.size();
//			size = size % 4 == 0 ? (size / 4) : (size / 4 + 1);
			size = size % 3 == 0 ? (size / 3) : (size / 3 + 1);
		}
		return size;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroupList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		RoomType roomType = (RoomType) getGroup(groupPosition);
		List<Room> roomList = mChildrenList.get(roomType.roomTypeId);
		if (roomList != null && childPosition < roomList.size()) {
			return roomList.get(childPosition);
		} else {
			return null;
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		final GroupViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_room_type, null);
			holder = new GroupViewHolder(convertView);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}

		final RoomType roomType = (RoomType) getGroup(groupPosition);

		if (roomType.imgs != null && roomType.imgs.size() > 0) {
			AppController.getInstance().getImageLoader().get(roomType.imgs.get(0).url, ImageLoader.getImageListener(holder.image, R.drawable.ic_noimage, R.drawable.ic_noimage));
			holder.image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UiHelper.toBrowseImageList(context, roomType.imgs, 0);
				}
			});
		}

		holder.name.setText(roomType.name);
		holder.desc.setText(roomType.useDesc);
		if (from == Constants.From.GIFT) {
			holder.desc.setMaxLines(3);
			holder.price.setVisibility(View.GONE);
			holder.salePrice.setVisibility(View.GONE);
		} else {
			holder.desc.setMaxLines(2);
			holder.price.setVisibility(View.GONE);
			holder.salePrice.setVisibility(View.VISIBLE);
			holder.price.setText("原最低消费价    :¥" + Utils.formatTwoFractionDigits(roomType.amt));		
			String title = "最低消费: ¥" + Utils.formatTwoFractionDigits(roomType.ttAmt);
			SpannableStringBuilder style = new SpannableStringBuilder(title);
			style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.orange)), 6, title.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色
			holder.salePrice.setText(style);
		}
		
		return convertView;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		final ChildrenViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_room, null);
			holder = new ChildrenViewHolder(convertView);
		} else {
			holder = (ChildrenViewHolder) convertView.getTag();
		}

		final Room room1 = (Room) getChild(groupPosition, childPosition * 3);
		final Room room2 = (Room) getChild(groupPosition, childPosition * 3 + 1);
		final Room room3 = (Room) getChild(groupPosition, childPosition * 3 + 2);
//		final Room room4 = (Room) getChild(groupPosition, childPosition * 4 + 3);

		setRoomName(holder.room1, room1);
		setRoomName(holder.room2, room2);
		setRoomName(holder.room3, room3);
//		setRoomName(holder.room4, room4);
		
		holder.room1.setOnClickListener(this);
		holder.room2.setOnClickListener(this);
		holder.room3.setOnClickListener(this);
//		holder.room4.setOnClickListener(this);
		
        return convertView;
	}

	private void setRoomName(final TextView tvRoom, final Room room) {
		tvRoom.setText(getRoomName(room));
		tvRoom.setTag(room);
	}

	private String getRoomName(final Room room) {
		String roomName = null;
		if (room != null) {
			roomName = room.name;
		}
		return roomName;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private static class GroupViewHolder {

		public ImageView image;
		public TextView name;
		public TextView desc;
		public TextView price;
		public TextView salePrice;

		public GroupViewHolder(View convertView) {
			image = (ImageView) convertView.findViewById(R.id.iv_image);
			name = (TextView) convertView.findViewById(R.id.tv_name);
			desc = (TextView) convertView.findViewById(R.id.tv_desc);
			price = (TextView) convertView.findViewById(R.id.tv_price);
			salePrice = (TextView) convertView.findViewById(R.id.tv_sale_price);

			convertView.setTag(this);
		}

	}
	
	private static class ChildrenViewHolder {

		public TextView room1;
		public TextView room2;
		public TextView room3;
//		public TextView room4;

		public ChildrenViewHolder(View convertView) {
			room1 = (TextView) convertView.findViewById(R.id.tv_room_1);
			room2 = (TextView) convertView.findViewById(R.id.tv_room_2);
			room3 = (TextView) convertView.findViewById(R.id.tv_room_3);
//			room4 = (TextView) convertView.findViewById(R.id.tv_room_4);

			convertView.setTag(this);
		}

	}

	@Override
	public void onClick(View v) {
		Room room = (Room) v.getTag();
		if (listener != null) {
			listener.onRoomClick(room);
		}
	}
	
	public interface OnRoomClickListener {
		public void onRoomClick(Room room);
	}

}
