package com.kiandastream.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiandastream.R;
import com.kiandastream.model.PlayListFragment_Listmodel;
import com.kiandastream.utils.ImageLoader;

public class PlaylistFragmentAdapter extends BaseAdapter {

	Context context;
	ArrayList<PlayListFragment_Listmodel> list;
	ImageLoader imageloader;
	public PlaylistFragmentAdapter(Context context, ArrayList<PlayListFragment_Listmodel> list) {
		this.context = context;
		this.list = list;
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

		
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.playlistfragment_listitem, parent,
					false);
		}
		ImageView image=(ImageView)convertView.findViewById(R.id.playlistimage);
		
		TextView songname=(TextView)convertView.findViewById(R.id.playlistname);
		TextView songcount=(TextView)convertView.findViewById(R.id.playlistsongcount);
		songname.setText(list.get(position).getName());
		songcount.setText(list.get(position).getTotalsong()+" Songs");
		imageloader.DisplayImage(list.get(position).getImageurl(), image);
		return convertView;
	}
	
	public void cleardata()
	{
		imageloader.clearCache();
	}

}
