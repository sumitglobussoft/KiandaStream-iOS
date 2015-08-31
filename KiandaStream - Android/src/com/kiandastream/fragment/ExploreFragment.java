package com.kiandastream.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.kiandastream.R;
import com.kiandastream.adapter.ExploreAdapter;
import com.kiandastream.model.ExploreModel;

public class ExploreFragment extends Fragment
{
	GridView gridview;
	ExploreAdapter adapter;
	ArrayList<ExploreModel> list=new ArrayList<ExploreModel>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View rootview=inflater.inflate(R.layout.trending_fragment, container, false);
		gridview=(GridView)rootview.findViewById(R.id.gridview);
		
		ExploreModel model=new ExploreModel();
		model.setName("Kuduro");
		model.setDrawable_id(R.drawable.kuduro4);
		list.add(model);
		ExploreModel model1=new ExploreModel();
		model1.setName("Kizomba");
		model1.setDrawable_id(R.drawable.kizomba);
		list.add(model1);
		ExploreModel model2=new ExploreModel();
		model2.setName("Rap");
		model2.setDrawable_id(R.drawable.rap3);
		list.add(model2);
		
		ExploreModel model3=new ExploreModel();
		model3.setName(",R&B");
		model3.setDrawable_id(R.drawable.r_b);
		list.add(model3);
		
		ExploreModel model4=new ExploreModel();
		model4.setName("Afro-House");
		model4.setDrawable_id(R.drawable.afro_house);
		list.add(model4);
		
		ExploreModel model5=new ExploreModel();
		model5.setName("Jazz");
		model5.setDrawable_id(R.drawable.jazz1);
		list.add(model5);
		
		ExploreModel model6=new ExploreModel();
		model6.setName("Afro-jazz");
		model6.setDrawable_id(R.drawable.afrojazz);
		list.add(model6);
		
		ExploreModel model7=new ExploreModel();
		model7.setName("Gospel");
		model7.setDrawable_id(R.drawable.gospel);
		list.add(model7);
		
		ExploreModel model8=new ExploreModel();
		model8.setName("Contemporary");
		model8.setDrawable_id(R.drawable.contemp);
		list.add(model8);
		
		ExploreModel model9=new ExploreModel();
		model9.setName("Hip-Hop");
		model9.setDrawable_id(R.drawable.hip2);
		list.add(model9);
		
		ExploreModel model10=new ExploreModel();
		model10.setName("Soul");
		model10.setDrawable_id(R.drawable.soul);
		list.add(model10);
		
		ExploreModel model11=new ExploreModel();
		model11.setName("Semba");
		model11.setDrawable_id(R.drawable.semba4);
		list.add(model11);
		
		adapter=new ExploreAdapter(getActivity(), list);
		gridview.setAdapter(adapter);
		return rootview;
	}
	
}
