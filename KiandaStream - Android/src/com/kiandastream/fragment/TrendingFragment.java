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
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.kiandastream.HomeActivity;
import com.kiandastream.R;
import com.kiandastream.adapter.TrendingAdapter;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.model.TrendingModel;
import com.kiandastream.utils.MyCustomProgressDialog;
import com.kiandastream.utils.ServiceHandler;

public class TrendingFragment extends Fragment
{
	GridView gridview;
	ArrayList<TrendingModel> trend_list;
	String pageno="1";
	ProgressDialog progressdialog;
	TrendingAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View rootview=inflater.inflate(R.layout.trending_fragment, container, false);
		HomeActivity.actiobar.setTitle("Trending");
		gridview=(GridView)rootview.findViewById(R.id.gridview);
		trend_list=new ArrayList<TrendingModel>();
		new GetData().execute(pageno);
		return rootview;
	}
	class GetData extends AsyncTask<String, Void, String>
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
			String parameter=MainSingleTon.Main_url+"t=trending&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem+"&page="+params[0];
			System.out.println("url for treanding "+parameter);
			
			ServiceHandler sh=new ServiceHandler();
			String result=sh.makeServiceCall(parameter, ServiceHandler.GET);
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
						System.out.println("status 200");
						if(jsonObject.has("next")&& !jsonObject.isNull("next"))
						{
							
							pageno=""+jsonObject.getInt("next");
						}
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
							trend_list.add(model);
						}
						adapter=new TrendingAdapter(getActivity(), trend_list);
						gridview.setAdapter(adapter);
					}else
					{
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}else
			{
				Toast.makeText(getActivity(), "Error in getting data", Toast.LENGTH_LONG).show();
			}
			progressdialog.dismiss();
			super.onPostExecute(result);
		}
		
	}
	@Override
	public void onStop() 
	{
		if(adapter!=null)
		{
			adapter.cleardata();	
		}
		
		super.onStop();
	}
}
