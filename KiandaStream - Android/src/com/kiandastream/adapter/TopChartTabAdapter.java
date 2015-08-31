/*package com.kiandastream.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.kiandastream.fragment.TopChart_DataFragment;
import com.kiandastream.fragment.TopChart_TabFragment;

public class TopChartTabAdapter extends FragmentPagerAdapter 
{
	
	SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

	public TopChartTabAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
		 
	}

	private final String[] TITLES = {"Day","Week","Month","Year","AllTime"};

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position].toUpperCase();
	}

	@Override
	public Fragment getItem(int position) 
	{
		// TODO Auto-generated method stub
 
		switch (position) 
		{
		case 0:
			TopChart_TabFragment.filter="DAY";
			return new TopChart_DataFragment();
		case 1:
			TopChart_TabFragment.filter="WEEK";
			return new TopChart_DataFragment();
		case 2:
			TopChart_TabFragment.filter="MONTH";
			return new TopChart_DataFragment();
		case 3:
			TopChart_TabFragment.filter="YEAR";
			return new TopChart_DataFragment();
		case 4:
			TopChart_TabFragment.filter="ALL";
			return new TopChart_DataFragment();
		}
		return null;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return TITLES.length;
	}
}
*/