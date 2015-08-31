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
import com.kiandastream.adapter.AlbumAdapter;
import com.kiandastream.model.AlbumModel;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.utils.MyCustomProgressDialog;
import com.kiandastream.utils.ServiceHandler;

public class AlbumsFragment extends Fragment
{
	GridView gridview;
	String pageno="1";
	ProgressDialog progressdialog;
	AlbumAdapter adapter;
	ArrayList<AlbumModel> albumlist;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View rootview=inflater.inflate(R.layout.trending_fragment, container, false);
		HomeActivity.actiobar.setTitle("Album");
		gridview=(GridView)rootview.findViewById(R.id.gridview);
		albumlist=new ArrayList<AlbumModel>();
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
			String parameter=MainSingleTon.Main_url+"t=albumlist&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem+"&page="+params[0];
			System.out.println("url for albums "+parameter);
			ServiceHandler sh=new ServiceHandler();
			String result=sh.makeServiceCall(parameter, ServiceHandler.GET);
			System.out.println("response from album  "+result);
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
						JSONArray array=object.getJSONArray("albums");
						for(int i=0;i<array.length();i++)
						{
							AlbumModel model=new AlbumModel();
							model.setArtist_id(array.getJSONObject(i).getString("artist_id"));
							model.setArtist_name(array.getJSONObject(i).getString("artist"));
							model.setId(array.getJSONObject(i).getString("id"));
							model.setName(array.getJSONObject(i).getString("name"));
							model.setView(array.getJSONObject(i).getString("view"));
							albumlist.add(model);
						}
						adapter=new AlbumAdapter(getActivity(), albumlist);
						gridview.setAdapter(adapter);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}else
			{
				Toast.makeText(getActivity(), "Error in geting data", Toast.LENGTH_LONG).show();
			}
			progressdialog.dismiss();
			super.onPostExecute(result);
		}
		
	}
	@Override
	public void onStop() 
	{
		if(adapter!=null)
		adapter.cleardata();
		super.onStop();
	}
}
