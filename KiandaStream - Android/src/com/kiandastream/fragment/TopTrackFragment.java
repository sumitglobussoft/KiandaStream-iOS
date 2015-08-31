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
import android.widget.ListView;

import com.kiandastream.R;
import com.kiandastream.adapter.TopChartAdapter;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.model.TrendingModel;
import com.kiandastream.utils.MyCustomProgressDialog;
import com.kiandastream.utils.ServiceHandler;

public class TopTrackFragment extends Fragment
{
	
	String tab_name;
	ImageView day_tab,week_tab,month_tab,year_tab;//alltime_tab;
	ListView listview;
	ProgressDialog progressdialog;
	TopChartAdapter adapter;
	ArrayList<TrendingModel> daylist=new ArrayList<TrendingModel>();
	ArrayList<TrendingModel> weeklist=new ArrayList<TrendingModel>();
	ArrayList<TrendingModel> monthlist=new ArrayList<TrendingModel>();
	ArrayList<TrendingModel> yearlist=new ArrayList<TrendingModel>();
	ArrayList<TrendingModel> alltimelist=new ArrayList<TrendingModel>();
	ArrayList<TrendingModel> mainlist=new ArrayList<TrendingModel>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View rootview = inflater.inflate(R.layout.simple_tabs, container, false);
		listview=(ListView)rootview.findViewById(R.id.listview);
		day_tab=(ImageView)rootview.findViewById(R.id.topchart_day);
		week_tab=(ImageView)rootview.findViewById(R.id.topchart_week);
		month_tab=(ImageView)rootview.findViewById(R.id.topchart_month);
		year_tab=(ImageView)rootview.findViewById(R.id.topchart_year);
		day_tab.setEnabled(false);
		week_tab.setEnabled(false);
		month_tab.setEnabled(false);
		year_tab.setEnabled(false);
		
		day_tab.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{	
				if(adapter!=null)
				{
					mainlist.clear();
					mainlist.addAll(daylist);
					adapter=new TopChartAdapter(getActivity(), mainlist);
					adapter.notifyDataSetChanged();
				}
				
				day_tab.setImageResource(R.drawable.day_new);
				week_tab.setImageResource(R.drawable.week_new);
				month_tab.setImageResource(R.drawable.month_new);
				year_tab.setImageResource(R.drawable.year_new);
				//alltime_tab.setImageResource(R.drawable.alltime_unactiv);
				
			}
		});
		week_tab.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{	
				if(adapter!=null)
				{
					mainlist.clear();
					mainlist.addAll(weeklist);
					adapter.notifyDataSetChanged();
				}
				
				day_tab.setImageResource(R.drawable.day_unactive);
				week_tab.setImageResource(R.drawable.week_new_active);
				month_tab.setImageResource(R.drawable.month_new);
				year_tab.setImageResource(R.drawable.year_new);
			//	alltime_tab.setImageResource(R.drawable.alltime_unactiv);
				
			}
		});
		month_tab.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				if(adapter!=null)
				{
					mainlist.clear();
					mainlist.addAll(monthlist);
					adapter.notifyDataSetChanged();
				}
				
				day_tab.setImageResource(R.drawable.day_unactive);
				week_tab.setImageResource(R.drawable.week_new);
				month_tab.setImageResource(R.drawable.month_new_active);
				year_tab.setImageResource(R.drawable.year_new);
				//alltime_tab.setImageResource(R.drawable.alltime_unactiv);
				
			}
		});
		year_tab.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				if(adapter!=null)
				{
					mainlist.clear();
					mainlist.addAll(yearlist);
					adapter.notifyDataSetChanged();
				}
				
				day_tab.setImageResource(R.drawable.day_unactive);
				week_tab.setImageResource(R.drawable.week_new);
				month_tab.setImageResource(R.drawable.month_new);
				year_tab.setImageResource(R.drawable.year_new_active);
				//alltime_tab.setImageResource(R.drawable.alltime_unactiv);
				
			}
		});
//		alltime_tab.setOnClickListener(new OnClickListener() 
//		{
//			
//			@Override
//			public void onClick(View v) {
//				if(adapter!=null)
//				{
//					mainlist.clear();
//					mainlist.addAll(alltimelist);
//					adapter.notifyDataSetChanged();
//				}
//				
//				day_tab.setImageResource(R.drawable.day_unactive);
//				week_tab.setImageResource(R.drawable.week_new);
//				month_tab.setImageResource(R.drawable.month_new);
//				year_tab.setImageResource(R.drawable.year_new);
//				alltime_tab.setImageResource(R.drawable.alltime_active);
//				
//			}
//		});
		new GetData().execute();
		return rootview;
	}
	class GetData extends AsyncTask<String, Void, String>
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
		protected String doInBackground(String... params) {
			String parameter=MainSingleTon.Main_url+"t=top&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem;
			ServiceHandler sh=new ServiceHandler();
			String result=sh.makeServiceCall(parameter, ServiceHandler.GET);
			return result;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			if(result!=null)
			{
				JSONObject object;
				try {
					object = new JSONObject(result);
					if(object.getInt("status_code")==200)
					{
						JSONArray dayarray=object.getJSONArray("day-songs");
						if(dayarray.length()>0)
						{
							for(int i=0;i<dayarray.length();i++)
							{
								TrendingModel model=new TrendingModel();
								model.setAblum_name(dayarray.getJSONObject(i).getString("album"));
								model.setArtist_id(dayarray.getJSONObject(i).getString("artist_id"));
								model.setArtist_name(dayarray.getJSONObject(i).getString("artist"));
								model.setId(dayarray.getJSONObject(i).getString("id"));
								model.setLoved_count(dayarray.getJSONObject(i).getString("loved_count"));
								model.setTitle(dayarray.getJSONObject(i).getString("title"));
								if(dayarray.getJSONObject(i).isNull("viewer_love"))
								{
									model.setLoved(false);
								}else
								{
									model.setLoved(true);
								}
								try {
									model.setImageurl(dayarray.getJSONObject(i).getJSONObject("image").getString("small"));
								} catch (Exception e) {
									e.printStackTrace();
								}
								daylist.add(model);
							}
							
							
						}
						JSONArray weekarray=object.getJSONArray("week-songs");
						if(weekarray.length()>0)
						{
							for(int i=0;i<weekarray.length();i++)
							{
								TrendingModel model=new TrendingModel();
								model.setAblum_name(weekarray.getJSONObject(i).getString("album"));
								model.setArtist_id(weekarray.getJSONObject(i).getString("artist_id"));
								model.setArtist_name(weekarray.getJSONObject(i).getString("artist"));
								model.setId(weekarray.getJSONObject(i).getString("id"));
								model.setLoved_count(weekarray.getJSONObject(i).getString("loved_count"));
								model.setTitle(weekarray.getJSONObject(i).getString("title"));
								if(weekarray.getJSONObject(i).isNull("viewer_love"))
								{
									model.setLoved(false);
								}else
								{
									model.setLoved(true);
								}
								try {
									model.setImageurl(weekarray.getJSONObject(i).getJSONObject("image").getString("small"));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								weeklist.add(model);
							}
						}
						JSONArray montharray=object.getJSONArray("month-songs");
						if(montharray.length()>0)
						{
							for(int i=0;i<montharray.length();i++)
							{
								TrendingModel model=new TrendingModel();
								model.setAblum_name(montharray.getJSONObject(i).getString("album"));
								model.setArtist_id(montharray.getJSONObject(i).getString("artist_id"));
								model.setArtist_name(montharray.getJSONObject(i).getString("artist"));
								model.setId(montharray.getJSONObject(i).getString("id"));
								model.setLoved_count(montharray.getJSONObject(i).getString("loved_count"));
								model.setTitle(montharray.getJSONObject(i).getString("title"));
								if(montharray.getJSONObject(i).isNull("viewer_love"))
								{
									model.setLoved(false);
								}else
								{
									model.setLoved(true);
								}
								try {
									model.setImageurl(montharray.getJSONObject(i).getJSONObject("image").getString("small"));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								monthlist.add(model);
							}
						}
						JSONArray yeararray=object.getJSONArray("year-songs");
						if(yeararray.length()>0)
						{
							for(int i=0;i<dayarray.length();i++)
							{
								TrendingModel model=new TrendingModel();
								model.setAblum_name(yeararray.getJSONObject(i).getString("album"));
								model.setArtist_id(yeararray.getJSONObject(i).getString("artist_id"));
								model.setArtist_name(yeararray.getJSONObject(i).getString("artist"));
								model.setId(yeararray.getJSONObject(i).getString("id"));
								model.setLoved_count(yeararray.getJSONObject(i).getString("loved_count"));
								model.setTitle(yeararray.getJSONObject(i).getString("title"));
								if(yeararray.getJSONObject(i).isNull("viewer_love"))
								{
									model.setLoved(false);
								}else
								{
									model.setLoved(true);
								}
								try {
									model.setImageurl(yeararray.getJSONObject(i).getJSONObject("image").getString("small"));
								} catch (Exception e) {
									e.printStackTrace();
								}
								yearlist.add(model);
							}
						}
						JSONArray alltimearray=object.getJSONArray("alltime-songs");
						if(alltimearray.length()>0)
						{
							for(int i=0;i<alltimearray.length();i++)
							{
								TrendingModel model=new TrendingModel();
								model.setAblum_name(alltimearray.getJSONObject(i).getString("album"));
								model.setArtist_id(alltimearray.getJSONObject(i).getString("artist_id"));
								model.setArtist_name(alltimearray.getJSONObject(i).getString("artist"));
								model.setId(alltimearray.getJSONObject(i).getString("id"));
								model.setLoved_count(alltimearray.getJSONObject(i).getString("loved_count"));
								model.setTitle(alltimearray.getJSONObject(i).getString("title"));
								if(alltimearray.getJSONObject(i).isNull("viewer_love"))
								{
									model.setLoved(false);
								}else
								{
									model.setLoved(true);
								}
								try {
									model.setImageurl(alltimearray.getJSONObject(i).getJSONObject("image").getString("small"));
								} catch (Exception e) {
									e.printStackTrace();
								}
								alltimelist.add(model);
							}
						}
						
						
					}
				} catch (JSONException e) {	
					e.printStackTrace();
				}
				
				//mainlist.addAll(daylist);
				adapter=new TopChartAdapter(getActivity(), alltimelist);
				listview.setAdapter(adapter);
				day_tab.setEnabled(true);
				week_tab.setEnabled(true);
				month_tab.setEnabled(true);
				year_tab.setEnabled(true);
				//alltime_tab.setEnabled(true);
			}
			progressdialog.dismiss();
			super.onPostExecute(result);
		}
	}
	@Override
	public void onStop() {
		adapter.cleardata();
		super.onStop();
	}
}
