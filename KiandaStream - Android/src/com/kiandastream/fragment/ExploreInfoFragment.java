package com.kiandastream.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.kiandastream.R;
import com.kiandastream.adapter.TrendingAdapter;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.model.TrendingModel;
import com.kiandastream.utils.ServiceHandler;

public class ExploreInfoFragment extends Fragment
{ 
	TextView title;
	GridView gridview;
	TrendingAdapter adapter;
	ArrayList<TrendingModel> list=new ArrayList<TrendingModel>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View rootview=inflater.inflate(R.layout.exploreinfofragment, container, false);
		title=(TextView)rootview.findViewById(R.id.exploretitle);
		title.setText(MainSingleTon.selectgenre);
		gridview=(GridView)rootview.findViewById(R.id.gridview);
		new GetData().execute();
		return rootview;
	}
	class GetData extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute() 
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) 
		{
			String paramter=MainSingleTon.Main_url+"t=genre&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem+"&name="+MainSingleTon.selectgenre+"&page=1";
			ServiceHandler sh=new ServiceHandler();
			String result=sh.makeServiceCall(paramter, ServiceHandler.GET);
			return result;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			if(result!=null)
			{
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result);
					if(jsonObject.getInt("status_code")==200)
					{
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
								model.setImageurl(jarray.getJSONObject(i).getJSONObject("image").getString("medium"));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							list.add(model);
						}
						adapter=new TrendingAdapter(getActivity(), list);
						gridview.setAdapter(adapter);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else
			{
				
			}
			super.onPostExecute(result);
		}
	}
	@Override
	public void onStop() {
		adapter.cleardata();
		super.onStop();
	}
}
