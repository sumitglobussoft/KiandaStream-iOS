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

import com.kiandastream.HomeActivity;
import com.kiandastream.R;
import com.kiandastream.fragment.AlbumInfoFragment;
import com.kiandastream.model.AlbumModel;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.utils.ImageLoader;

public class AlbumAdapter extends BaseAdapter {

	Context context;
	ArrayList<AlbumModel> list;
	ImageLoader imageloader;
	public AlbumAdapter(Context context, ArrayList<AlbumModel> list) {
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
			convertView = mInflater.inflate(R.layout.grid_list_item, parent,
					false);
		}

		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		TextView album_name=(TextView)convertView.findViewById(R.id.song_name);
		TextView artist_name=(TextView)convertView.findViewById(R.id.artist_name);
		album_name.setText(list.get(position).getName());
		artist_name.setText(list.get(position).getArtist_name());
		imageloader.DisplayImage(MainSingleTon.albumimageurl+list.get(position).getId()+"_small.jpg", image);
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				MainSingleTon.album_model=list.get(position);
				MainSingleTon.previous_fragment="album";
				MainSingleTon.current_fragment="albuminfo";
				Fragment fragment = new AlbumInfoFragment();
				HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				cleardata();
			}
		});

		return convertView;
	}
	
	public void cleardata()
	{
		imageloader.clearCache();
		
	}

}
