package com.kiandastream.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kiandastream.R;

public class SettingFragment extends Fragment 
{
	ImageView userimage,fb_connect,twt_connect;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootview=inflater.inflate(R.layout.settingfragment, container, false);
		
		return rootview;
	}
}
