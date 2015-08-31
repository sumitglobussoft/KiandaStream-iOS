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
import com.kiandastream.fragment.MusicPlayer_Fragment;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.model.PlayingSongListmodel;
import com.kiandastream.model.TrendingModel;
import com.kiandastream.musicplayer.MusicService;
import com.kiandastream.utils.ImageLoader;

public class TrendingAdapter extends BaseAdapter
{
	Context context;
	ArrayList<TrendingModel> list;
	ImageLoader imageloader;
	public TrendingAdapter(Context mcontext,ArrayList<TrendingModel> list) 
	{
		this.context=mcontext;
		this.list=list;
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
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.grid_list_item, parent,
					false);
		}

		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		TextView album_name=(TextView)convertView.findViewById(R.id.song_name);
		TextView artist_name=(TextView)convertView.findViewById(R.id.artist_name);
		album_name.setText(list.get(position).getTitle());
		artist_name.setText(list.get(position).getArtist_name());
		//imageloader.DisplayImage("http://api.kiandastream.globusapps.com/static/albums/"+list.get(position).getId()+"_medium.jpg", image);
		imageloader.DisplayImage(list.get(position).getImageurl(), image);
		System.out.println("url of album image http://api.kiandastream.globusapps.com/static/albums/"+list.get(position).getId()+"_medium.jpg");
		
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				
					PlayingSongListmodel model=new PlayingSongListmodel();
					model.setLoved(false);
					model.setSong_artist(list.get(position).getArtist_name());
					model.setSong_id(list.get(position).getId());
					model.setSong_image(list.get(position).getImageurl());
					model.setSong_name(list.get(position).getTitle());
					model.setSongurl(MainSingleTon.songurl+list.get(position).getId()+".mp3");
					int[] getarray= checkSong(list.get(position).getId());
					if(getarray[0]==0)
					{
						MainSingleTon.song_position=""+getarray[1];
					}else
					{
						MusicService.song_playlist.add(model);
						MainSingleTon.song_position=""+(MusicService.song_playlist.size()-1);
					}
					
					MainSingleTon.isplaysong=true;
					MainSingleTon.current_fragment="player";
					MainSingleTon.previous_fragment="trending";
					System.out.println("postion of song in list "+MainSingleTon.song_position);
					Fragment fragment = new MusicPlayer_Fragment();
					HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				}
				
			
		});
		return convertView;
		
	}
	
	private int[] checkSong(String id)
	{
		for(int i=0;i<MusicService.song_playlist.size();i++)
		{
			if(MusicService.song_playlist.get(i).getSong_id().equals(id))
			{	int[] array={0,i};
				return array;
			}
		}
		int[] array={1,0};
		return array;
	}
	public void cleardata()
	{
		imageloader.clearCache();
	}
}
