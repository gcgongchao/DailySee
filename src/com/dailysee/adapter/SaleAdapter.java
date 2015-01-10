package com.dailysee.adapter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Preferential;

public class SaleAdapter extends BaseAdapter {

	private Context context;
	private List<Preferential> saleList;
	private LayoutInflater mInflater;

	public SaleAdapter(Context context, List<Preferential> saleList) {
		this.context = context;
		this.saleList = saleList;

		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return saleList.size();
	}

	@Override
	public Object getItem(int position) {
		return saleList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_sale, null);
			holder = new ViewHolder(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Preferential preferential = saleList.get(position);

		if (!TextUtils.isEmpty(preferential.logoUrl)) {
			AppController.getInstance().getImageLoader()
					.get(preferential.logoUrl, ImageLoader.getImageListener(holder.ivImage, R.drawable.ic_noimage, R.drawable.ic_noimage));
		}
		int remainDays = getDaysBetween(preferential.startDate, preferential.endDate);
		holder.tvTime.setText(context.getResources().getString(R.string.sale_remain_days, Integer.toString(remainDays)));
		holder.tvName.setText(preferential.title);
		// holder.tvSale.setText(preferential.title);

		return convertView;
	}

	public static int getDaysBetween(long beginDate, long endDate) {
		Calendar d1 = new GregorianCalendar();
		d1.setTimeInMillis(beginDate);

		Calendar d2 = new GregorianCalendar();
		d2.setTimeInMillis(endDate);
		int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
		int y2 = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			d1 = (Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);// 得到当年的实际天数
				d1.add(Calendar.YEAR, 1);
			} while (d1.get(Calendar.YEAR) != y2);
		}
		return days;
	}

	private static class ViewHolder {

		private ImageView ivImage;
		private TextView tvName;
		private TextView tvTime;
		private TextView tvSale;

		public ViewHolder(View convertView) {
			ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
			tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			tvName = (TextView) convertView.findViewById(R.id.tv_name);
			tvSale = (TextView) convertView.findViewById(R.id.tv_sale);
			convertView.setTag(this);
		}

	}

}
