package com.kiandastream.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kiandastream.HomeActivity;
import com.kiandastream.R;
import com.kiandastream.fragment.ExploreInfoFragment;
import com.kiandastream.model.ExploreModel;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.utils.ImageLoader;

public class ExploreAdapter extends BaseAdapter {

	Context context;
	ArrayList<ExploreModel> list;
	ImageLoader imageloader;
	public ExploreAdapter(Context context, ArrayList<ExploreModel> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = mInflater.inflate(R.layout.explore_listitem, parent,
					false);
		}

		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		TextView album_name=(TextView)convertView.findViewById(R.id.genre_name);
		image.setImageResource(list.get(position).getDrawable_id());
		album_name.setText(list.get(position).getName());
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				Toast.makeText(context, list.get(position).getName()+" is Clicked", Toast.LENGTH_SHORT).show();
				//MainSingleTon.album_model=list.get(position);
				Toast.makeText(context, list.get(position).getName()+" is Clicked", Toast.LENGTH_SHORT).show();
				MainSingleTon.selectgenre=list.get(position).getName();
				Fragment fragment = new ExploreInfoFragment();
				HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).addToBackStack(null).commit();
				
			}
		});

		return convertView;
	}

}
