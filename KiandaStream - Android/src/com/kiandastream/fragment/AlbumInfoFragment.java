package com.kiandastream.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiandastream.HomeActivity;
import com.kiandastream.R;
import com.kiandastream.adapter.AlbumInfoAdapter;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.model.PlayingSongListmodel;
import com.kiandastream.model.TrendingModel;
import com.kiandastream.musicplayer.MusicService;
import com.kiandastream.utils.ImageLoader;
import com.kiandastream.utils.MyCustomProgressDialog;
import com.kiandastream.utils.ParallaxListView;
import com.kiandastream.utils.ServiceHandler;

public class AlbumInfoFragment extends Fragment
{
	ImageView albuminage,playall;
	TextView album_name,album_artist,album_count;
	ImageLoader imageloader;
	String totalcount;
	AlbumInfoAdapter adapter;
	ProgressDialog progressdialog;
	ParallaxListView listView;
	ArrayList<TrendingModel> albumsonglist=new ArrayList<TrendingModel>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View rootview=inflater.inflate(R.layout.albuminfo_fragment, container, false);
		
		HomeActivity.actiobar.setTitle("Album Info");
		imageloader=new ImageLoader(getActivity());
		listView = (ParallaxListView)rootview. findViewById(R.id.list_view);
		LayoutInflater layoutinflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout lyt = (RelativeLayout) layoutinflater.inflate(R.layout.albuminfo_header, null);
	    listView.addParallaxedHeaderView(lyt);
	    
	    album_artist=(TextView)lyt.findViewById(R.id.ablum_artistname);
	    album_count=(TextView)lyt.findViewById(R.id.ablum_songcount);
	    album_name=(TextView)lyt.findViewById(R.id.ablum_name);
	    albuminage=(ImageView)lyt.findViewById(R.id.userimage);
	    playall=(ImageView)lyt.findViewById(R.id.playall);
	    album_artist.setText(MainSingleTon.album_model.getArtist_name());
	    
	    album_name.setText(MainSingleTon.album_model.getName());
	    imageloader.DisplayImage(MainSingleTon.albumimageurl+MainSingleTon.album_model.getId()+"_medium.jpg", albuminage);
		/*String[]  list={"aaaa","aaaa","aaaa","aaaa","aaaa","aaaa","aaaa","aaaa","aaaa","aaaa","aaaa","aaaa","aaaa","aaaa","aaaa"};
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
		listView.setAdapter(adapter);*/
	    playall.setOnClickListener(new OnClickListener() 
	    {
			@Override
			public void onClick(View v) 
			{
				for(int i=0;i<albumsonglist.size();i++)
				{
					PlayingSongListmodel model=new PlayingSongListmodel();
					model.setLoved(false);
					model.setSong_artist(albumsonglist.get(i).getArtist_name());
					model.setSong_id(albumsonglist.get(i).getId());
					model.setSong_image(albumsonglist.get(i).getImageurl());
					model.setSong_name(albumsonglist.get(i).getTitle());
					model.setSongurl(MainSingleTon.songurl+albumsonglist.get(i).getId()+".mp3");
					System.out.println("Url of select song "+MainSingleTon.songurl+albumsonglist.get(i).getId()+".mp3");
					int[] getarray= checkSong(albumsonglist.get(i).getId());
					if(getarray[0]==0)
					{	
						if(i==0)
						MainSingleTon.song_position=""+getarray[1];
						
					}else
					{
						MusicService.song_playlist.add(model);
						if(i==0)
						MainSingleTon.song_position=""+(MusicService.song_playlist.size()-1);
					}
					
					
				}
				MainSingleTon.isplaysong=true;
				MainSingleTon.previous_fragment="album";
				MainSingleTon.current_fragment="player";
				Fragment fragment = new MusicPlayer_Fragment();
				HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
			}
		});
	    new GetAlbumData().execute();
		return rootview;
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
	class GetAlbumData extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute() 
		{
			progressdialog = new MyCustomProgressDialog(getActivity());
			progressdialog.setCancelable(false);
			progressdialog.show();
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) 
		{
			String paramter=MainSingleTon.Main_url+"t=album&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem+"&action=song&id="+MainSingleTon.album_model.getId();
			ServiceHandler sh=new ServiceHandler();
			String result=sh.makeServiceCall(paramter, ServiceHandler.GET);
			
			return result;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			if(result!=null)
			{
				try {
					JSONObject jsonObject=new JSONObject(result);
					if(jsonObject.getInt("status_code")==200)
					{
						totalcount=jsonObject.getString("total");
						JSONArray jarray=jsonObject.getJSONArray("songs");
						for(int i=0;i<jarray.length();i++)
						{
							TrendingModel model=new TrendingModel();
							model.setAblum_name(jarray.getJSONObject(i).getString("album"));
							model.setArtist_id(jarray.getJSONObject(i).getString("artist_id"));
							model.setArtist_name(jarray.getJSONObject(i).getString("artist"));
							model.setId(jarray.getJSONObject(i).getString("id"));
							model.setLoved_count(jarray.getJSONObject(i).getString("loved_count"));
							model.setTitle(jarray.getJSONObject(i).getString("title"));
							if(jarray.getJSONObject(i).isNull("viewer_love"))
							{
								model.setLoved(false);
							}else
							{
								model.setLoved(true);
							}
							try {
								model.setImageurl(jarray.getJSONObject(i).getJSONObject("image").getString("small"));
							} catch (Exception e) {
								e.printStackTrace();
							}
							albumsonglist.add(model);
						}
						adapter=new AlbumInfoAdapter(getActivity(), albumsonglist);
						listView.setAdapter(adapter);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			progressdialog.dismiss();
			super.onPostExecute(result);
		}
	}
	/*@Override
	public void onStop() 
	{	
		//imageloader.clearCache();
		
		super.onStop();
	//	onDestroy();
	}*/
}
