package com.kiandastream.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiandastream.R;
import com.kiandastream.model.ProfileListModel;
import com.kiandastream.utils.ImageLoader;

public class ProfileAdapter extends BaseAdapter {

	Context context;
	ArrayList<ProfileListModel> list;
	ImageLoader imageloader;
	int type;
	public ProfileAdapter(Context context, ArrayList<ProfileListModel> list) 
	{
		this.context = context;
		this.list=list;
		imageloader=new ImageLoader(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if(list.get(position).getType()==6)
		{
			
				LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.profilenodata_listitem, parent,false);
			
		}else
		{
			
				LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.profile_listitem, parent,false);
			
			ImageView imageview=(ImageView)convertView.findViewById(R.id.image);
			TextView name=(TextView)convertView.findViewById(R.id.name);
			TextView subtext=(TextView)convertView.findViewById(R.id.song_duration);
			System.out.println("list size is "+list.size());
			if(list.get(position).getImage()!=null && !list.get(position).getImage().isEmpty())
				try {
					imageloader.DisplayImage(list.get(position).getImage(), imageview);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			System.out.println("list.get(position).getImage()"+list.get(position).getImage());
			System.out.println("list.get(position).getName()"+list.get(position).getName());
			name.setText(list.get(position).getName());
			subtext.setText(list.get(position).getSubtext());
			convertView.setOnClickListener(new OnClickListener() 
			{
				
				@Override
				public void onClick(View v) 
				{
					System.out.println("type id "+list.get(position).getType());
					
				}
			});
		}
		
		return convertView;
	}

}
