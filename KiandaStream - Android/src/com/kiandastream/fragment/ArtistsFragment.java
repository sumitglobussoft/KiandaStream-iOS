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
import com.kiandastream.adapter.ArtistAdapter;
import com.kiandastream.model.ArtistModel;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.utils.MyCustomProgressDialog;
import com.kiandastream.utils.ServiceHandler;

public class ArtistsFragment extends Fragment
{
	GridView gridview;
	String pageno;
	ArrayList<ArtistModel> artist_list;
	ArtistAdapter adapter;
	ProgressDialog progressdialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootview=inflater.inflate(R.layout.trending_fragment, container, false);
		HomeActivity.actiobar.setTitle("Artist");
		gridview=(GridView)rootview.findViewById(R.id.gridview);
		artist_list=new ArrayList<ArtistModel>();
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
			String parameter=MainSingleTon.Main_url+"t=artistlist&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem+"&page="+params[0];
			System.out.println("url for Artist "+parameter);
			ServiceHandler sh=new ServiceHandler();
			String result=sh.makeServiceCall(parameter, ServiceHandler.GET);
			System.out.println("response from artist "+result);
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
						if(jsonObject.has("next"))
						{
							pageno=""+jsonObject.getInt("next");
						}
						JSONArray jarray=jsonObject.getJSONArray("artists");
						for(int i=0;i<jarray.length();i++)
						{
							ArtistModel model=new ArtistModel();
							model.setArtist_id(jarray.getJSONObject(i).getString("id"));
							model.setArtist_name(jarray.getJSONObject(i).getString("name"));
							model.setArtist_totalsong(jarray.getJSONObject(i).getString("total_songs"));
							artist_list.add(model);
						}
						adapter=new ArtistAdapter(getActivity(), artist_list);
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
		adapter.cleardata();
		super.onStop();
	}
}
