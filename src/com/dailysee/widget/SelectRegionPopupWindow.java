package com.dailysee.widget;

import java.util.ArrayList;
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

	private static final String OTHER = "其他";

	public static final String TAG = SelectRegionPopupWindow.class.getSimpleName();

	private TextView tvTemp;
	private ListView lvRegion;
	private ListView lvArea;

	private CityAdapter mAreaAdapter;
	private CityAdapter mRegionAdapter;
	private List<CityEntity> mAreaList = null;
	private List<CityEntity> mRegionList = null;
	private CityDb mCityDb;

	private OnSelectListener mOnSelectListener;

	public int mCurAreaPosition = 0;
	public int mCurRegionPosition = 0;
	public int mCurTmpAreaPosition = 0;
	public int mCurrenTmpRegionPosition = 0;

	public SelectRegionPopupWindow(Context context) {
		super(context, R.layout.dialog_select_region);
	}

	public SelectRegionPopupWindow(Context context, OnClickListener onclickListener) {
		super(context, onclickListener, R.layout.dialog_select_region);
	}

	public SelectRegionPopupWindow(Context context, OnSelectListener onSelectListener) {
		super(context, R.layout.dialog_select_region); 
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
		mAreaList = mCityDb.findCityRegionInfo(cityId);
		if (mAreaList != null && mAreaList.size() > 0) {
			mAreaAdapter = new CityAdapter((Activity) context, mAreaList);
			lvArea.setAdapter(mAreaAdapter);
			lvArea.setSelection(mCurAreaPosition);
			// 根据当前区域id,get所有的片区信息
			if (mAreaList.size() > mCurAreaPosition) {
				CityEntity entity = mAreaList.get(mCurAreaPosition);
				mRegionList = getCityRegionInfo(entity);
				
				mRegionAdapter = new CityAdapter((Activity) context, mRegionList);
				lvRegion.setAdapter(mRegionAdapter);
			}
		}
	}

	private List<CityEntity> getCityRegionInfo(CityEntity entity) {
		List<CityEntity> mRegionList = mCityDb.findCityRegionInfo(entity.cityId);
		
		if (mRegionList == null) {
			mRegionList = new ArrayList<CityEntity>();
		}
		if (mRegionList.size() == 0) {
			CityEntity city = new CityEntity();
			city.name = OTHER;
			mRegionList.add(city);
		}
		return mRegionList;
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
			mCurTmpAreaPosition = position;
//			mAdapter.setCurrentPosition(mCurrenTmpDistrictPosition);
			// 更新右侧区域
			CityEntity area = mAreaList.get(mCurTmpAreaPosition);
			mRegionList = getCityRegionInfo(area);
			if (mCurTmpAreaPosition == mCurAreaPosition) {
				mCurrenTmpRegionPosition = mCurRegionPosition;
			} else {
				mCurrenTmpRegionPosition = -1;
			}
//			mAdapter2.setCurrentPosition(mCurrenTmpRegionPosition);
			mRegionAdapter.setList(mRegionList);
			lvRegion.setSelection(0);
		} else if (parent == lvRegion) {
			// 更新右侧区域
			mCurAreaPosition = mCurTmpAreaPosition;
			mCurRegionPosition = position;
//			mAdapter2.setCurrentPosition(mCurrenRegionPosition);
//			mRegionAdapter.notifyDataSetChanged();
			if (mOnSelectListener != null) {
				CityEntity area = mAreaList.get(mCurAreaPosition);
				CityEntity region = mRegionList.get(mCurRegionPosition);
				if (area != null && region != null) {
					if (OTHER.equals(region.name)) {
						mOnSelectListener.onSelectListener(area.name, area.name, "");
					} else {
						mOnSelectListener.onSelectListener(region.name, area.name, region.name);
					}
				}
			}
		}
	}

	public interface OnSelectListener {
		void onSelectListener(String title, String area, String region);
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
