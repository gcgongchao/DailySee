package com.dailysee.widget;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.adapter.CityAdapter;
import com.dailysee.bean.CityEntity;
import com.dailysee.db.CityDb;
import com.dailysee.util.SpUtil;

public class SelectRegionPopupWindow extends BasePopupWindow implements OnItemClickListener, OnClickListener {

	public static final String TAG = SelectRegionPopupWindow.class.getSimpleName();

	private TextView tvTemp;
	private ListView lvRegion;
	private ListView lvArea;

	private CityAdapter mAdapter;
	private CityAdapter mAdapter2;
	private List<CityEntity> cityListData = null;
	private List<CityEntity> regionListData = null;
	private CityDb mCityDb;

	private OnSelectListener mOnSelectListener;

	public int mCurrenDistrictPosition = 0;
	public int mCurrenRegionPosition = 0;
	public int mCurrenTmpDistrictPosition = 0;
	public int mCurrenTmpRegionPosition = 0;

	public SelectRegionPopupWindow(Context context) {
		super(context);
	}

	public SelectRegionPopupWindow(Context context, OnClickListener onclickListener) {
		super(context, onclickListener);
	}

	public SelectRegionPopupWindow(Context context, OnSelectListener onSelectListener) {
		super(context);
		mOnSelectListener = onSelectListener;
	}

	public void init() {
		initPopFindViews();
		initPopViewsValue();
		initPopViewsEvent();
	}

	public void initPopFindViews() {
		tvTemp = (TextView) contentView.findViewById(R.id.tv_temp);

		lvArea = (ListView) contentView.findViewById(R.id.lv_area);
		lvRegion = (ListView) contentView.findViewById(R.id.lv_region);
	}

	public void initPopViewsValue() {
		mCityDb = new CityDb(context);
		initListView();
	}

	private void initListView() {
		int cityId = SpUtil.getInstance(context).getCityId();
		cityListData = mCityDb.findCityRegionInfo(cityId);
		if (cityListData != null && cityListData.size() > 0) {
			mAdapter = new CityAdapter((Activity) context, cityListData);
			lvArea.setAdapter(mAdapter);
			lvArea.setSelection(mCurrenDistrictPosition);
			// 根据当前区域id,get所有的片区信息
			if (cityListData.size() > mCurrenDistrictPosition) {
				if (mCurrenDistrictPosition == 0)
					regionListData = null;
				else {
					CityEntity entity = cityListData.get(mCurrenDistrictPosition);
					int regionId = entity.cityId;
					regionListData = mCityDb.findCityRegionInfo(regionId);
				}
				mAdapter2 = new CityAdapter((Activity) context, regionListData);
				lvRegion.setAdapter(mAdapter2);
			}
		}
	}

	public void initPopViewsEvent() {
		tvTemp.setOnClickListener(this);
		lvArea.setOnItemClickListener(this);
		lvRegion.setOnItemClickListener(this);
	}

	public void show(View v, int x, int y) {
		this.showAsDropDown(v, x, y);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == lvArea) {
			// 更新左侧区域
			mCurrenTmpDistrictPosition = position;
//			mAdapter.setCurrentPosition(mCurrenTmpDistrictPosition);
			if (mCurrenTmpDistrictPosition == 0) {
				mCurrenDistrictPosition = mCurrenTmpDistrictPosition;
				mCurrenRegionPosition = -1;
				regionListData = null;
				mAdapter2.setList(regionListData);
				if (mOnSelectListener != null) {
					mOnSelectListener.onSelectListener("", "");
				}
				return;
			}
			// 更新右侧区域
			CityEntity cityE = cityListData.get(mCurrenTmpDistrictPosition);
			int regionId = cityE.cityId;
			regionListData = mCityDb.findCityRegionInfo(regionId);
			if (mCurrenTmpDistrictPosition == mCurrenDistrictPosition) {
				mCurrenTmpRegionPosition = mCurrenRegionPosition;
			} else {
				mCurrenTmpRegionPosition = -1;
			}
//			mAdapter2.setCurrentPosition(mCurrenTmpRegionPosition);
			mAdapter2.setList(regionListData);
		} else if (parent == lvRegion) {
			// 更新右侧区域
			mCurrenDistrictPosition = mCurrenTmpDistrictPosition;
			mCurrenRegionPosition = position;
//			mAdapter2.setCurrentPosition(mCurrenRegionPosition);
			mAdapter2.notifyDataSetChanged();
			if (mOnSelectListener != null) {
				CityEntity districtE = cityListData.get(mCurrenDistrictPosition);
				CityEntity regionE = regionListData.get(mCurrenRegionPosition);
				if (districtE != null && regionE != null) {
					String name = districtE.name;
					if (mCurrenRegionPosition > 0) {
						name = regionE.name;
					}
					mOnSelectListener.onSelectListener(districtE.name, regionE.name);
				}
			}
		}
	}

	public interface OnSelectListener {
		void onSelectListener(String district, String region);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_temp:
			dismiss();
			break;

		default:
			break;
		}
	}

}
