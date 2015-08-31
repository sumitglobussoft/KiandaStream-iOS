package com.kiandastream.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import com.kiandastream.adapter.ProfileAdapter;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.model.ProfileListModel;
import com.kiandastream.utils.ImageLoader;
import com.kiandastream.utils.MyCustomProgressDialog;
import com.kiandastream.utils.ParallaxListView;
import com.kiandastream.utils.ServiceHandler;

public class ProfileFragment extends Fragment
{
	ProfileAdapter adapter=null;
	ImageView Loved_song,follower,following,feed,playlist,userimage;
	TextView username,nodata;
	ParallaxListView listView;
	ProgressDialog progressdialog;
	ImageLoader imageloader;
	ArrayList<ProfileListModel> loved_songlist=null;
	ArrayList<ProfileListModel> feed_list=null;
	ArrayList<ProfileListModel> follower_list=null;
	ArrayList<ProfileListModel> following_list=null;
	ArrayList<ProfileListModel> playlist_list=null;
	ArrayList<ProfileListModel> main_list=new ArrayList<ProfileListModel>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View rootview=inflater.inflate(R.layout.profilefragment,container, false);
		HomeActivity.actiobar.setTitle("Profile");
		listView = (ParallaxListView) rootview.findViewById(R.id.list_view);
		imageloader = new ImageLoader(getActivity());
		RelativeLayout lyt = (RelativeLayout) inflater.inflate(R.layout.profilefragmentheader, null);
		userimage = (ImageView) lyt.findViewById(R.id.userimage);
		username=(TextView)lyt.findViewById(R.id.username);
		//imageloader.DisplayImage(MainSingleTon.artistimageurl+ MainSingleTon.artist_model.getArtist_id() + "_large.jpg",userimage);
		Loved_song = (ImageView) lyt.findViewById(R.id.loved_song);
		follower = (ImageView) lyt.findViewById(R.id.follower);
		following = (ImageView) lyt.findViewById(R.id.following);
		feed = (ImageView) lyt.findViewById(R.id.feed);
		playlist = (ImageView) lyt.findViewById(R.id.playlist);
		listView.addParallaxedHeaderView(lyt);
		username.setText(MainSingleTon.username);
		Loved_song.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				Loved_song.setImageResource(R.drawable.loved_menu_active);
				follower.setImageResource(R.drawable.followers_menu_unactive);
				following.setImageResource(R.drawable.following_menu_unactive);
				feed.setImageResource(R.drawable.feed_menu_unactive);
				playlist.setImageResource(R.drawable.playlist_menu_unactive);
				System.out.println("loved song is clecked");
				if(loved_songlist!=null)
				{
					if(adapter!=null)
					{
						System.out.println("adapter is not null");
						main_list.clear();
						main_list.addAll(loved_songlist);
						if(main_list.size()<1)
						{
							ProfileListModel model=new ProfileListModel();
							model.setName("");
							model.setImage("");
							model.setId("");
							model.setSubtext("");
							model.setType(6);
							main_list.add(model);
						}
						
						adapter.notifyDataSetChanged();
					}else
					{
						System.out.println("adapter is null ");
						main_list.clear();
						main_list.addAll(loved_songlist);
						adapter=new ProfileAdapter(getActivity(), main_list);
						listView.setAdapter(adapter);
					}
				}else
				{
					System.out.println("song list is null ");
					new GetLovedSong().execute();
				}
			}
		});
		follower.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				System.out.println("follower song is clecked");
				Loved_song.setImageResource(R.drawable.loved_menu_unactive);
				follower.setImageResource(R.drawable.followers_menu_active);
				following.setImageResource(R.drawable.following_menu_unactive);
				feed.setImageResource(R.drawable.feed_menu_unactive);
				playlist.setImageResource(R.drawable.playlist_menu_unactive);
				
				if(follower_list!=null)
				{
					if(adapter!=null)
					{
						main_list.clear();
						main_list.addAll(follower_list);
						if(main_list.size()>0)
						adapter.notifyDataSetChanged();
						else
						{
							main_list.clear();
							ProfileListModel model=new ProfileListModel();
							model.setName("");
							model.setImage("");
							model.setId("");
							model.setSubtext("");
							model.setType(6);
							main_list.add(model);
							adapter.notifyDataSetChanged();
						}
					}else
					{
						main_list.clear();
						main_list.addAll(follower_list);
						if (main_list.size()<1) 
						{
							ProfileListModel model=new ProfileListModel();
							model.setName("");
							model.setImage("");
							model.setId("");
							model.setSubtext("");
							model.setType(6);
							main_list.add(model);
						}
						adapter=new ProfileAdapter(getActivity(), main_list);
						listView.setAdapter(adapter);
					}
				}else
				{
					new GetFollower().execute();
				}
			}
		});
		following.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				System.out.println("following song is clecked");
				Loved_song.setImageResource(R.drawable.loved_menu_unactive);
				follower.setImageResource(R.drawable.followers_menu_unactive);
				following.setImageResource(R.drawable.following_menu_active);
				feed.setImageResource(R.drawable.feed_menu_unactive);
				playlist.setImageResource(R.drawable.playlist_menu_unactive);
				if(following_list!=null)
				{
					if(adapter!=null)
					{
						main_list.clear();
						main_list.addAll(following_list);
						if(main_list.size()>0)
						adapter.notifyDataSetChanged();
						else
						{
							main_list.clear();
							ProfileListModel model=new ProfileListModel();
							model.setName("");
							model.setImage("");
							model.setId("");
							model.setSubtext("");
							model.setType(6);
							main_list.add(model);
							adapter.notifyDataSetChanged();
						}
					}else
					{
						main_list.clear();
						main_list.addAll(following_list);
						if (main_list.size()<1) 
						{
							ProfileListModel model=new ProfileListModel();
							model.setName("");
							model.setImage("");
							model.setId("");
							model.setSubtext("");
							model.setType(6);
							main_list.add(model);
						}
						adapter=new ProfileAdapter(getActivity(), main_list);
						listView.setAdapter(adapter);
					}
				}else
				{
					new GetFollowing().execute();
				}
			}
		});
		feed.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				System.out.println("feed song is clecked");	
				Loved_song.setImageResource(R.drawable.loved_menu_unactive);
				follower.setImageResource(R.drawable.followers_menu_unactive);
				following.setImageResource(R.drawable.following_menu_unactive);
				feed.setImageResource(R.drawable.feed_menu_active);
				playlist.setImageResource(R.drawable.playlist_menu_unactive);
			}
		});
		playlist.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				System.out.println("playlist song is clecked");
				Loved_song.setImageResource(R.drawable.loved_menu_unactive);
				follower.setImageResource(R.drawable.followers_menu_unactive);
				following.setImageResource(R.drawable.following_menu_unactive);
				feed.setImageResource(R.drawable.feed_menu_unactive);
				playlist.setImageResource(R.drawable.playlist_menu_active);
				
				if(playlist_list!=null)
				{
					if(adapter!=null)
					{
						main_list.clear();
						main_list.addAll(playlist_list);
						if(main_list.size()>0)
						adapter.notifyDataSetChanged();
						else
						{
							main_list.clear();
							ProfileListModel model=new ProfileListModel();
							model.setName("");
							model.setImage("");
							model.setId("");
							model.setSubtext("");
							model.setType(6);
							main_list.add(model);
							adapter.notifyDataSetChanged();
						}
					}else
					{
						main_list.clear();
						main_list.addAll(playlist_list);
						if (main_list.size()<1) 
						{
							ProfileListModel model=new ProfileListModel();
							model.setName("");
							model.setImage("");
							model.setId("");
							model.setSubtext("");
							model.setType(6);
							main_list.add(model);
						}
						adapter=new ProfileAdapter(getActivity(), main_list);
						listView.setAdapter(adapter);
					}
				}else
				{
					new GetPlaylist().execute();
				}
			}
		});
		new GetLovedSong().execute();
		return rootview;
	}
	//Getting loved song of user
	class GetLovedSong extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute() {
			progressdialog = new MyCustomProgressDialog(getActivity());
			progressdialog.setCancelable(false);
			// Show progressdialog
			progressdialog.show();
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(Void... params) 
		{
			String paramter=MainSingleTon.Main_url+"t=member&username="+MainSingleTon.mainusername+"&action=loved&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem;
			ServiceHandler sh=new ServiceHandler();
			String result=sh.makeServiceCall(paramter, ServiceHandler.GET);
			return result;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			System.out.println("i am in getting loved song"+result);
			if(result!=null)
			{
				try {
					JSONObject object=new JSONObject(result);
					if(object.getInt("status_code")==200)
					{
						loved_songlist=new ArrayList<ProfileListModel>();
						if(!object.isNull("songs"))
						{
							JSONArray jarray=object.getJSONArray("songs");
							{
								for(int i=0;i<jarray.length();i++)
								{
									ProfileListModel model=new ProfileListModel();
									model.setName(jarray.getJSONObject(i).getString("title"));
									model.setImage(jarray.getJSONObject(i).getJSONObject("image").getString("small"));
									model.setId(jarray.getJSONObject(i).getString("id"));
									model.setSubtext(jarray.getJSONObject(i).getString("artist"));
									model.setType(1);
									loved_songlist.add(model);
								}
								main_list.clear();
								main_list.addAll(loved_songlist);
								adapter=new ProfileAdapter(getActivity(), main_list);
								listView.setAdapter(adapter);
							}
						}else
						{
							main_list.clear();
							ProfileListModel model=new ProfileListModel();
							model.setName("");
							model.setImage("");
							model.setId("");
							model.setSubtext("");
							model.setType(6);
							main_list.add(model);
							if(adapter==null)
							{
							adapter=new ProfileAdapter(getActivity(), main_list);
							listView.setAdapter(adapter);
							}else
							{
								adapter.notifyDataSetChanged();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			progressdialog.dismiss();
			super.onPostExecute(result);
		}
	}
	//Getting following of user
	class GetFeed extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute() {
			progressdialog = new MyCustomProgressDialog(getActivity());
			progressdialog.setCancelable(false);
			// Show progressdialog
			progressdialog.show();
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) 
		{
			String paramter=MainSingleTon.Main_url+"t=member&username="+MainSingleTon.mainusername+"&action=feedlove&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem;
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
					JSONObject object=new JSONObject(result);
					if(object.getInt("status_code")==200)
					{
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			progressdialog.dismiss();
			super.onPostExecute(result);
		}
	}
	//Getting following  of user
		class GetFollowing extends AsyncTask<String, Void, String>
		{
			@Override
			protected void onPreExecute() 
			{
				progressdialog = new MyCustomProgressDialog(getActivity());
				progressdialog.setCancelable(false);
				// Show progressdialog
				progressdialog.show();
			super.onPreExecute();
			}
			@Override
			protected String doInBackground(String... params) 
			{
				String paramter=MainSingleTon.Main_url+"t=member&username="+MainSingleTon.mainusername+"&action=following&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem;
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
						JSONObject object=new JSONObject(result);
						if(object.getInt("status_code")==200)
						{
							following_list=new ArrayList<ProfileListModel>();
							if(!object.isNull("following"))
							{
								
								JSONArray jarray=object.getJSONArray("following");
								for(int i=0;i<jarray.length();i++)
								{
									ProfileListModel model=new ProfileListModel();
									model.setName(jarray.getJSONObject(i).getString("name"));
									model.setImage(jarray.getJSONObject(i).getJSONObject("image").getString("medium"));
								    model.setUsername(jarray.getJSONObject(i).getString("username"));
									model.setSubtext(jarray.getJSONObject(i).getString("descr"));
									model.setType(3);
									following_list.add(model);
								}
								main_list.clear();
								main_list.addAll(following_list);
								if(adapter!=null)
								{
									adapter.notifyDataSetChanged();
								}else
								{
									adapter=new ProfileAdapter(getActivity(), main_list);
									listView.setAdapter(adapter);
								}
							}else
							{

								main_list.clear();
								ProfileListModel model=new ProfileListModel();
								model.setName("");
								model.setImage("");
								model.setId("");
								model.setSubtext("");
								model.setType(6);
								main_list.add(model);
								if(adapter==null)
								{
								adapter=new ProfileAdapter(getActivity(), main_list);
								listView.setAdapter(adapter);
								}else
								{
									adapter.notifyDataSetChanged();
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				progressdialog.dismiss();
				super.onPostExecute(result);
			}
		}
	//Getting follower of user
	class GetFollower extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute() 
		{
			progressdialog = new MyCustomProgressDialog(getActivity());
			progressdialog.setCancelable(false);
			// Show progressdialog
			progressdialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) 
		{
			String paramter=MainSingleTon.Main_url+"t=member&username="+MainSingleTon.mainusername+"&action=followers&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem;
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
					JSONObject object=new JSONObject(result);
					if(object.getInt("status_code")==200)
					{
						following_list=new ArrayList<ProfileListModel>();
						if(!object.isNull("followers"))
						{
							
							JSONArray jarray=object.getJSONArray("followers");
							for(int i=0;i<jarray.length();i++)
							{
								ProfileListModel model=new ProfileListModel();
								model.setName(jarray.getJSONObject(i).getString("name"));
								model.setImage(jarray.getJSONObject(i).getJSONObject("image").getString("medium"));
							    model.setUsername(jarray.getJSONObject(i).getString("username"));
								model.setSubtext(jarray.getJSONObject(i).getString("descr"));
								model.setType(3);
								following_list.add(model);
							}
							main_list.clear();
							main_list.addAll(following_list);
							if(adapter!=null)
							{
								adapter.notifyDataSetChanged();
							}else
							{
								adapter=new ProfileAdapter(getActivity(), main_list);
								listView.setAdapter(adapter);
							}
						}else
						{
							main_list.clear();
							ProfileListModel model=new ProfileListModel();
							model.setName("");
							model.setImage("");
							model.setId("");
							model.setSubtext("");
							model.setType(6);
							main_list.add(model);
							if(adapter==null)
							{
							adapter=new ProfileAdapter(getActivity(), main_list);
							listView.setAdapter(adapter);
							}else
							{
								adapter.notifyDataSetChanged();
							}
							
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			progressdialog.dismiss();
			super.onPostExecute(result);
		}
	}
	//Getting playlist of user
	class GetPlaylist extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute() 
		{
			progressdialog = new MyCustomProgressDialog(getActivity());
			progressdialog.setCancelable(false);
			// Show progressdialog
			progressdialog.show();
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) 
		{
			String paramter=MainSingleTon.Main_url+"t=member&username="+MainSingleTon.mainusername+"&action=playlist&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem;
			System.out.println("url of play list in profile "+paramter);
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
					JSONObject object=new JSONObject(result);
					if(object.getInt("status_code")==200)
					{
						playlist_list=new ArrayList<ProfileListModel>();
						if(!object.isNull("playlists"))
						{
							
							JSONArray jarray=object.getJSONArray("playlists");
							for(int i=0;i<jarray.length();i++)
							{
								ProfileListModel model=new ProfileListModel();
								model.setName(jarray.getJSONObject(i).getString("name"));
								model.setImage("http://kiandastream.globusapps.com/static/playlists/"+jarray.getJSONObject(i).getString("playlist_id")+"_medium.jpg");
								model.setId(jarray.getJSONObject(i).getString("playlist_id"));
								model.setSubtext(jarray.getJSONObject(i).getString("descr"));
								model.setType(5);
								playlist_list.add(model);
							}
							main_list.clear();
							main_list.addAll(playlist_list);
							if(adapter!=null)
							{
								adapter.notifyDataSetChanged();
							}else
							{
								adapter=new ProfileAdapter(getActivity(), main_list);
								listView.setAdapter(adapter);
							}
						}else
						{
							main_list.clear();
							ProfileListModel model=new ProfileListModel();
							model.setName("");
							model.setImage("");
							model.setId("");
							model.setSubtext("");
							model.setType(6);
							main_list.add(model);
							if(adapter==null)
							{
							adapter=new ProfileAdapter(getActivity(), main_list);
							listView.setAdapter(adapter);
							}else
							{
								adapter.notifyDataSetChanged();
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			progressdialog.dismiss();
			super.onPostExecute(result);
		}
	}
	@Override
	public void onStop() {
		imageloader.clearCache();
		super.onStop();
	}
}
