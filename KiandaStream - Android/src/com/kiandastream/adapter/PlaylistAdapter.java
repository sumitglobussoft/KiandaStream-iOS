package com.kiandastream.adapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kiandastream.R;
import com.kiandastream.database.LocalData;
import com.kiandastream.database.ModelUserDatas;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.model.PlayingSongListmodel;
import com.kiandastream.musicplayer.MusicService;
import com.kiandastream.utils.ImageLoader;

public class PlaylistAdapter extends BaseAdapter {

	Context context;
	ArrayList<PlayingSongListmodel> list;
	ImageLoader imageloader;
	public PlaylistAdapter(Context context, ArrayList<PlayingSongListmodel> list) {
		this.context = context;
		this.list = list;
		imageloader=new ImageLoader(context);
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
			convertView = mInflater.inflate(R.layout.playlist_listitem, parent,
					false);
		}
		ImageView share=(ImageView)convertView.findViewById(R.id.playlist_share);
		ImageView download=(ImageView)convertView.findViewById(R.id.playlist_download);
		ImageView isplaying=(ImageView)convertView.findViewById(R.id.playlist_isplaying);
		TextView songname=(TextView)convertView.findViewById(R.id.playlist_songname);
		TextView artistname=(TextView)convertView.findViewById(R.id.playlist_artistname);
		if(MusicService.positionofsong!=null && !MusicService.positionofsong.isEmpty())
		{
			if(Integer.parseInt(MusicService.positionofsong)==position)
			{
				isplaying.setVisibility(View.VISIBLE);
			}else
			{
				isplaying.setVisibility(View.GONE);
			}
		}else
		{
			isplaying.setVisibility(View.GONE);
		}
		songname.setText(list.get(position).getSong_name());
		artistname.setText(list.get(position).getSong_artist());
		
		share.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				
			}
		});
		download.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if(MainSingleTon.localsong.containsKey(list.get(position).getSong_id()))
				{
					Toast.makeText(context, "Song Already Downloaded", Toast.LENGTH_LONG).show();
				}else
				{
					
					String[] array={MainSingleTon.songurl+list.get(position).getSong_id()+".mp3",list.get(position).getSong_name()
							,list.get(position).getSong_id(),list.get(position).getSong_artist()};
					new DownloadFile().execute(array);
				}
				
				
			}
		});
		return convertView;
	}
	
	private class DownloadFile extends AsyncTask<String, Integer, String>
	{
		String songname,songpath,songfilename,songid,song_artistname;
		@Override
		protected void onPreExecute() 
		{
			Toast.makeText(context, "Song Downloading...", Toast.LENGTH_LONG).show();
			super.onPreExecute();
		}
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			//System.out.println("update progress "+values);
			super.onProgressUpdate(values);
		}
		protected String doInBackground(String... params) 
		{
			songname=params[1];
			songid=params[2];
			song_artistname=params[3];
			System.out.println("i am in doinbackgroung");
			int count;
			try {
				URL url = new URL(params[0]);
				URLConnection conexion = url.openConnection();
				conexion.connect();
				// this will be useful so that you can show a tipical 0-100% progress bar
				int lenghtOfFile = conexion.getContentLength();
				PackageManager m = context.getPackageManager();
				String s = context.getPackageName();
				try {
					PackageInfo p = m.getPackageInfo(s, 0);
					s = p.applicationInfo.dataDir;
					System.out.println("private folder path "+s);
				} catch (PackageManager.NameNotFoundException e) {
					Log.w("yourtag", "Error Package name not found ", e);
				}
				// downlod the file
				// create a File object for the parent directory
				File wallpaperDirectory = new File(Environment.getExternalStorageDirectory()+"/Android/data/com.kiandastream/tmp");
				// have the object build the directory structure, if needed.
				wallpaperDirectory.mkdirs();
				// create a File object for the output file
				songfilename=""+System.currentTimeMillis();
				File outputFile = new File(wallpaperDirectory, songfilename);
				songpath=outputFile.getAbsolutePath();
				System.out.println("outputFile.getAbsoluteFile()  "+outputFile.getAbsoluteFile());
				//getting input strean from url 
				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(outputFile);



				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) 
				{
					total += count;
					// publishing the progress....
					publishProgress((int)(total*100/lenghtOfFile));
					output.write(data, 0, count);
				}

				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  data save suceessfully");
				output.flush();
				output.close();
				input.close();
				return "0";
			} catch (Exception e) 
			{
				e.printStackTrace();
				return "1";
			}
			
		}
		@Override
		protected void onPostExecute(String result) 
		{
			if(result.equals("0"))
			{
			LocalData localdatabase=new LocalData(context);
			ModelUserDatas model=new ModelUserDatas();
			model.setSongartistname(song_artistname);
			model.setSongid(songid);
			model.setSongimagepath("adsadas");
			model.setSongname(songname);
			model.setSongpath(songpath);
			localdatabase.addNewSong(model);
			Toast.makeText(context, "Song Sucessfully Downloaded", Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}
	}

}
