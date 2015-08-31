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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.kiandastream.R;
import com.kiandastream.adapter.PlaylistFragmentAdapter;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.model.PlayListFragment_Listmodel;
import com.kiandastream.utils.MyCustomProgressDialog;
import com.kiandastream.utils.ServiceHandler;

public class PlayListFragment extends Fragment
{
	PlaylistFragmentAdapter adapter; 
	ArrayList<PlayListFragment_Listmodel> list=new ArrayList<PlayListFragment_Listmodel>();
	ArrayList<PlayListFragment_Listmodel> searchlist=new ArrayList<PlayListFragment_Listmodel>();
	ListView listview;
	ProgressDialog progressdialog;
	EditText search;
	ImageView searchbtn;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootview=inflater.inflate(R.layout.playlistfragment, container, false);
		listview=(ListView)rootview.findViewById(R.id.listview);
		search=(EditText)rootview.findViewById(R.id.search);
		searchbtn=(ImageView)rootview.findViewById(R.id.searchbtn);
		searchbtn.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if(!search.getText().toString().isEmpty())
				{
					new GetData().execute("1",search.getText().toString());
				}
				
			}
		});
		new GetData().execute("0");
		return rootview;
	}
	
	class GetData extends AsyncTask<String, Void, String>
	{
		String type;
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
			String searchtext;
			type=params[0];
			if(type.equals("0"))
			{
				searchtext="";
			}else
			{
				searchtext=params[1];
				searchlist.clear();
			}
			String parameter=MainSingleTon.Main_url+"t=playlistlist&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem+"&page=1&letter=&string="+searchtext;
			System.out.println("url for get playlist  "+parameter);
			ServiceHandler sh=new ServiceHandler();
			String result=sh.makeServiceCall(parameter, ServiceHandler.GET);
			
			return result;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			searchlist.clear();
			if(result!=null)
			{
				try {
					JSONObject object=new JSONObject(result);
					if(object.getInt("status_code")==200)
					{
						if(!object.isNull("playlists"))
						{
						JSONArray dataarray=object.getJSONArray("playlists");
						for(int i=0;i<dataarray.length();i++)
						{
							PlayListFragment_Listmodel model=new PlayListFragment_Listmodel();
							model.setId(dataarray.getJSONObject(i).getString("id"));
							model.setName(dataarray.getJSONObject(i).getString("name"));
							model.setImageurl("http://kiandastream.globusapps.com/static/playlists/"+dataarray.getJSONObject(i).getString("id")+"_medium.jpg");
							model.setTotalsong(dataarray.getJSONObject(i).getString("total_songs"));
							if(type.equals("0"))
							{
							list.add(model);
							}else
							{
								searchlist.add(model);
							}
						}
						if(type.equals("0"))
						{
						  adapter=new PlaylistFragmentAdapter(getActivity(), list);
						  listview.setAdapter(adapter);
						}else
						{
							 adapter=new PlaylistFragmentAdapter(getActivity(), searchlist);
							  listview.setAdapter(adapter);
						}
						}else
						{
							if(!type.equals("0"))
							{
								adapter=new PlaylistFragmentAdapter(getActivity(), list);
								  listview.setAdapter(adapter);
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
	
	@Override
	public void onStop() {
		if(adapter!=null){
			adapter.cleardata();	
		}
		super.onStop();
	}
	
}
