package com.kiandastream.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kiandastream.HomeActivity;
import com.kiandastream.R;
import com.kiandastream.fragment.AlbumInfoFragment;
import com.kiandastream.fragment.ArtistInfoFragment;
import com.kiandastream.fragment.MusicPlayer_Fragment;
import com.kiandastream.model.AlbumModel;
import com.kiandastream.model.ArtistModel;
import com.kiandastream.model.ArtistinfoModel;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.model.PlayingSongListmodel;
import com.kiandastream.musicplayer.MusicService;

public class ArtistInfoAdapter extends BaseAdapter 
{

	Context context;
	ArrayList<ArtistinfoModel> list;
	public ArtistInfoAdapter(Context context, ArrayList<ArtistinfoModel> list) {
		this.context = context;
		this.list = list;
		
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
			convertView = mInflater.inflate(R.layout.song_listitem, parent,
					false);
		}

		
		TextView song_no=(TextView)convertView.findViewById(R.id.song_no);
		TextView song_name=(TextView)convertView.findViewById(R.id.song_name);
		//TextView song_duration=(TextView)convertView.findViewById(R.id.song_duration);
		song_no.setText(""+(position+1)+".");
		song_name.setText(list.get(position).getTitle());
		convertView.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if(list.get(position).getType()==0)//Song list
				{
					PlayingSongListmodel model=new PlayingSongListmodel();
					model.setLoved(false);
					model.setSong_artist(list.get(position).getArtist_name());
					model.setSong_id(list.get(position).getId());
					model.setSong_image(list.get(position).getImageurl());
					model.setSong_name(list.get(position).getTitle());
					model.setSongurl(MainSingleTon.songurl+list.get(position).getId()+".mp3");
					System.out.println("Url of select song "+MainSingleTon.songurl+list.get(position).getId()+".mp3");
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
					MainSingleTon.previous_fragment="artist";
					MainSingleTon.current_fragment="player";
					Fragment fragment = new MusicPlayer_Fragment();
					HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				}else
					if(list.get(position).getType()==1)//album list
					{
						AlbumModel model=new AlbumModel();
						model.setId(list.get(position).getId());
						model.setName(list.get(position).getTitle());
						model.setArtist_id(list.get(position).getArtist_id());
						model.setArtist_name(list.get(position).getArtist_name());
						MainSingleTon.album_model=model;
						MainSingleTon.previous_fragment="artist";
						MainSingleTon.current_fragment="albuminfo";
						Fragment fragment = new AlbumInfoFragment();
						HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
					}else
						if(list.get(position).getType()==2)//Similar list
						{
							ArtistModel model=new ArtistModel();
							model.setArtist_id(list.get(position).getId());
							model.setArtist_name(list.get(position).getTitle());
							MainSingleTon.artist_model=model;
							MainSingleTon.previous_fragment="artist";
							MainSingleTon.current_fragment="artistinfo";
							Fragment fragment = new ArtistInfoFragment();
							HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
						}
				
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
}
