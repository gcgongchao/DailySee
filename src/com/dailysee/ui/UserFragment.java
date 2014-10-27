package com.dailysee.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailysee.R;

public class UserFragment extends HomeFragment {

	private FragmentTabHost mTabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mTabHost = new FragmentTabHost(getActivity());
		mTabHost.setup(getActivity(), getChildFragmentManager(),
				R.id.menu_settings);

		Bundle b = new Bundle();
		b.putString("key", "Simple");
		mTabHost.addTab(mTabHost.newTabSpec("simple").setIndicator("Simple"),
				HomeFragment.class, b);
		//
		b = new Bundle();
		b.putString("key", "Contacts");
		mTabHost.addTab(mTabHost.newTabSpec("contacts")
				.setIndicator("Contacts"), MessageFragment.class, b);
		return mTabHost;
	}
}
