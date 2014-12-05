package com.dailysee.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dailysee.R;

public class HomeFragment extends Fragment {

	protected static final String TAG = HomeFragment.class.getSimpleName();
	private ImageView ivMerchant;

	public HomeFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
		ivMerchant = (ImageView) v.findViewById(R.id.iv_merchant);
		ivMerchant.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				request();
			}
		});
		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void request() {
	}

}
