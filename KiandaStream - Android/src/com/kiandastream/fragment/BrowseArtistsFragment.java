package com.kiandastream.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiandastream.R;

public class BrowseArtistsFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rooView = inflater.inflate(R.layout.browseartistsfragment, container, false);
		return rooView;
	}
}
