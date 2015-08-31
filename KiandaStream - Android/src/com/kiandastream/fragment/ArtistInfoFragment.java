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

import com.kiandastream.HomeActivity;
import com.kiandastream.R;
import com.kiandastream.adapter.ArtistInfoAdapter;
import com.kiandastream.model.ArtistinfoModel;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.utils.ImageLoader;
import com.kiandastream.utils.MyCustomProgressDialog;
import com.kiandastream.utils.ParallaxListView;
import com.kiandastream.utils.ServiceHandler;

public class ArtistInfoFragment extends Fragment {
	
	ImageView topsong, album, bio, similar, artistimage;
	String currentselection = "song";
	static public ArtistInfoAdapter adapter;
	ParallaxListView listView;
	ProgressDialog progressdialog;
	ImageLoader imageloader;
	ArrayList<ArtistinfoModel> songlist = new ArrayList<ArtistinfoModel>();
	ArrayList<ArtistinfoModel> albumlist = new ArrayList<ArtistinfoModel>();
	ArrayList<ArtistinfoModel> similarartist = new ArrayList<ArtistinfoModel>();
	static ArrayList<ArtistinfoModel> listarray = new ArrayList<ArtistinfoModel>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootview = inflater.inflate(R.layout.artistinfo_fragment,container, false);
		HomeActivity.actiobar.setTitle("Artist Info");
		listView = (ParallaxListView) rootview.findViewById(R.id.list_view);
		imageloader = new ImageLoader(getActivity());
		LayoutInflater layoutinflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout lyt = (RelativeLayout) inflater.inflate(R.layout.artistinfo_header, null);
		artistimage = (ImageView) lyt.findViewById(R.id.userimage);
		imageloader.DisplayImage(MainSingleTon.artistimageurl+ MainSingleTon.artist_model.getArtist_id() + "_large.jpg",artistimage);
		topsong = (ImageView) lyt.findViewById(R.id.artistinfo_topsong);
		album = (ImageView) lyt.findViewById(R.id.artistinfo_album);
		bio = (ImageView) lyt.findViewById(R.id.artistinfo_bio);
		similar = (ImageView) lyt.findViewById(R.id.artistinfo_similar);
		listView.addParallaxedHeaderView(lyt);
		
		new GetData().execute();

		topsong.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				listarray.clear();
				/*System.out.println("size of list@@@ "+songlist.size());
				listarray=(ArrayList<ArtistinfoModel>) songlist.clone();*/
				listarray.addAll(songlist);
				System.out.println("size of list "+listarray.size());
				topsong.setEnabled(true);
				System.out.println("click of top song in artistinfo");
				topsong.setImageResource(R.drawable.top_tracks_active);
				album.setImageResource(R.drawable.albums_menu);
				bio.setImageResource(R.drawable.biography_menu);
				similar.setImageResource(R.drawable.relate_menu);
				currentselection = "song";

				//adapter = new ArtistInfoAdapter(getActivity(), listarray);
				//listView.setAdapter(adapter);
				//adapter.notifyDataSetInvalidated();
				adapter.notifyDataSetChanged();
				
				
			}
		});
		album.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{	
				listarray.clear();
				listarray.addAll(albumlist);
				System.out.println("size of list "+listarray.size());
				System.out.println("click of album in artistinfo");
				topsong.setImageResource(R.drawable.top_tracks);
				album.setImageResource(R.drawable.albums_menu_active);
				bio.setImageResource(R.drawable.biography_menu);
				similar.setImageResource(R.drawable.relate_menu);
				currentselection = "album";
				//adapter = new ArtistInfoAdapter(getActivity(), albumlist);
				//listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		});
		bio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{	
				
				System.out.println("click of bio in artistinfo");
				topsong.setImageResource(R.drawable.top_tracks);
				album.setImageResource(R.drawable.albums_menu);
				bio.setImageResource(R.drawable.biography_menu_active);
				similar.setImageResource(R.drawable.relate_menu);
				currentselection = "";

			}
		});
		similar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				listarray.clear();
				listarray.addAll(similarartist);
				System.out.println("size of list "+listarray.size());
				System.out.println("click of similar in artistinfo");
				topsong.setImageResource(R.drawable.top_tracks);
				album.setImageResource(R.drawable.albums_menu);
				bio.setImageResource(R.drawable.biography_menu);
				similar.setImageResource(R.drawable.relate_menu_active);
				currentselection = "similar";
				//adapter = new ArtistInfoAdapter(getActivity(), similarartist);
				//listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		});

		return rootview;
	}

	class GetData extends AsyncTask<String, Void, String> {
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
		protected String doInBackground(String... params) {
			String parameter = MainSingleTon.Main_url + "t=artist&user_id="
					+ MainSingleTon.userid + "&access_token="
					+ MainSingleTon.accesstokem + "&id="
					+ MainSingleTon.artist_model.getArtist_id();
			ServiceHandler sh = new ServiceHandler();
			String result = sh.makeServiceCall(parameter, ServiceHandler.GET);

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				try {
					JSONObject object = new JSONObject(result);
					if (object.getInt("status_code") == 200) {
						JSONArray songarray = object.getJSONArray("songs");
						for (int i = 0; i < songarray.length(); i++) {
							ArtistinfoModel model = new ArtistinfoModel();
							model.setAblum_name(songarray.getJSONObject(i).getString("album"));
							model.setArtist_id(songarray.getJSONObject(i).getString("artist_id"));
							model.setArtist_name(songarray.getJSONObject(i).getString("artist"));
							model.setId(songarray.getJSONObject(i).getString("id"));
							if(songarray.getJSONObject(i).isNull("viewer_love"))
							{
								model.setIsloved(false);
							}else
							{
								model.setIsloved(true);
							}
							model.setImageurl(songarray.getJSONObject(i).getJSONObject("image").getString("medium"));
							model.setLoved_count(songarray.getJSONObject(i).getString("loved_count"));
							model.setTitle(songarray.getJSONObject(i).getString("title"));
							model.setType(0);
							songlist.add(model);
						}
						JSONArray albumarray = object.getJSONArray("albums");
						for (int i = 0; i < albumarray.length(); i++) {
							ArtistinfoModel model = new ArtistinfoModel();
							model.setArtist_id(albumarray.getJSONObject(i).getString("artist_id"));
							model.setArtist_name(albumarray.getJSONObject(i).getString("artist"));
							model.setId(albumarray.getJSONObject(i).getString("id"));
							model.setTitle(albumarray.getJSONObject(i).getString("name"));
							model.setType(1);
							albumlist.add(model);
						}
						JSONArray smilararray = object
								.getJSONArray("similar_artist");
						for (int i = 0; i < smilararray.length(); i++) {
							ArtistinfoModel model = new ArtistinfoModel();
							model.setId(smilararray.getJSONObject(i).getString("id"));
							model.setTitle(smilararray.getJSONObject(i).getString("name"));
							model.setType(2);
							similarartist.add(model);
						}

						if (currentselection.equals("song")) 
						{
							listarray.clear();
							listarray=(ArrayList<ArtistinfoModel>) songlist.clone();
							adapter = new ArtistInfoAdapter(getActivity(),
									listarray);
							listView.setAdapter(adapter);
						}
						if (currentselection.equals("album")) {
							listarray.clear();
							listarray=(ArrayList<ArtistinfoModel>) albumlist.clone();
							adapter = new ArtistInfoAdapter(getActivity(),
									listarray);
							listView.setAdapter(adapter);
						}
						if (currentselection.equals("similar")) {
							listarray.clear();
							listarray=(ArrayList<ArtistinfoModel>) similarartist.clone();
							adapter = new ArtistInfoAdapter(getActivity(),
									listarray);
							listView.setAdapter(adapter);
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
	@Override
	public void onStop() {
		ImageLoader image=new ImageLoader(getActivity());
		image.clearCache();
		super.onStop();
	}
}
