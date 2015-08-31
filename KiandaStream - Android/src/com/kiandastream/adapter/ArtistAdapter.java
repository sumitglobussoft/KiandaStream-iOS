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
import com.kiandastream.fragment.ArtistInfoFragment;
import com.kiandastream.model.ArtistModel;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.utils.ImageLoader;

public class ArtistAdapter extends BaseAdapter {

	Context context;
	ArrayList<ArtistModel> list;
	ImageLoader imageloader;
	public ArtistAdapter(Context context, ArrayList<ArtistModel> list) {
		this.context = context;
		this.list = list;
		imageloader=new ImageLoader(context);
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
			convertView = mInflater.inflate(R.layout.artist_listitem, parent,
					false);
		}

		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		TextView artist_name=(TextView)convertView.findViewById(R.id.song_name);
		TextView songcount=(TextView)convertView.findViewById(R.id.artist_name);
		artist_name.setText(list.get(position).getArtist_name());
		songcount.setText(list.get(position).getArtist_name()+" Song(s)");
		imageloader.DisplayImage(MainSingleTon.artistimageurl+list.get(position).getArtist_id()+"_medium.jpg", image);
		//System.out.println("url of album image http://api.kiandastream.globusapps.com/static/albums/"+list.get(position).getArtist_id()+"_medium.jpg");
		
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				MainSingleTon.artist_model=list.get(position);
				MainSingleTon.previous_fragment="artist";
				MainSingleTon.current_fragment="artistinfo";
				Fragment fragment = new ArtistInfoFragment();
				HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
			}
		});

		return convertView;
	}
	public void cleardata()
	{
		imageloader.clearCache();
	}
}
