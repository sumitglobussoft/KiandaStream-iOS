package com.kiandastream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.kiandastream.database.LocalData;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.musicplayer.MusicService;
import com.kiandastream.utils.KiandaCallBack;
import com.kiandastream.utils.KiandaGetRequest;

public class SplashActivity extends Activity {

	LocalData local_database;
	public static final String TAG = SplashActivity.class.getSimpleName();
	private RequestQueue mRequestQueue;
	private static SplashActivity mInstance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		mInstance = this;
		local_database=new LocalData(getApplicationContext());
		local_database.CreateTable();
		MainSingleTon.localsong=local_database.getAllSonglist();
		
		if(MainSingleTon.localsong.size()>0)
		{
			for(String key: MainSingleTon.localsong.keySet())
			{
				MusicService.song_playlist.add(MainSingleTon.localsong.get(key));
				System.out.println("hello size of song playlist " +MusicService.song_playlist.size() );
	            System.out.println(key  +" :: "+ MainSingleTon.localsong.get(key).getSong_name() +"@@@   "+MainSingleTon.localsong.get(key).getSongurl());
	            //if you uncomment below code, it will throw java.util.ConcurrentModificationException
	            //studentGrades.remove("Alan");
	        }
		}
		String type=getData("TYPE", "0");
		if(!type.equals("0"))
		{
			if(type.equals("1"))
			{
				String username=getData("USERNAME", null);
				String password=getData("PASSWORD", null);
				
				System.out.println("Username "+username);
				System.out.println("Password "+password);
				if(username!=null && password!=null)
				{
					Login(username,password);//;.execute();
				}else
				{
					Intent in=new Intent(SplashActivity.this,WelcomeActivity.class);
					in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(in);
					finish();
				}
			}else
			{
				if(type.equals("2"))
				{
					String fbid=getData("FB_ID", null);
					if(fbid!=null)
					{
						FB_Login(fbid);
					}else
					{
						Intent in=new Intent(SplashActivity.this,WelcomeActivity.class);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(in);
						finish();
					}
					
				}
			}
		}else
		{
			Intent in=new Intent(SplashActivity.this,WelcomeActivity.class);
			in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(in);
			finish();
		}
		/*new Handler().postDelayed(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				Intent in=new Intent(SplashActivity.this,WelcomeActivity.class);
				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(in);
				finish();
			}
		}, 3000);*/
		
	}
	
	public static synchronized SplashActivity getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	
	/*saveData("TYPE", "1");
	saveData("USERNAME", username);
	saveData("PASSWORD", password);*/
	String getData(String key,String value)
	{
		SharedPreferences sh=getSharedPreferences("KIANDASTREAM",Context.MODE_PRIVATE);
	    String data=sh.getString(key, value);
	    return data;
	}
	
	public void Login(String username,String password) 
	{	
			String paramater=MainSingleTon.Main_url+"t=getSignonToken&username="+username+"&password="+password;
			
			KiandaGetRequest getRequest=new KiandaGetRequest(SplashActivity.this);
			getRequest.executeRequest(paramater, new KiandaCallBack() {
				@Override
				public void onSuccess(JSONObject result) {
					try {
						JSONObject object=result;
						if(object.getInt("status_code")==200)
						{
							JSONObject jsonObject=object.getJSONObject("user");
							
							MainSingleTon.userid=jsonObject.getString("user_id");
							MainSingleTon.username=jsonObject.getString("name");
							MainSingleTon.mainusername=jsonObject.getString("username");
							MainSingleTon.accesstokem=jsonObject.getString("access_token");
							
							Intent in=new Intent(SplashActivity.this,HomeActivity.class);
							in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(in);
							finish();
							
						}else 
							if(object.getInt("status_code")==201)
							{
								Intent in=new Intent(SplashActivity.this,WelcomeActivity.class);
								in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(in);
								finish();
							}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onFailure(Exception exception) {
					exception.printStackTrace();
				}
			});
	}
	
	public void FB_Login(String fbid){
		String id=fbid;
	    String parameter=MainSingleTon.Main_url+"t=facebooklogin&fb_id="+id;
		System.out.println("url for fb login  "+parameter);
		
		KiandaGetRequest kiandaGetRequest=new KiandaGetRequest(SplashActivity.this);
		kiandaGetRequest.executeRequest(parameter, new KiandaCallBack() {
			
			@Override
			public void onSuccess(JSONObject result) {
				try {
					JSONObject object=result;
					if(object.getInt("status_code")==200)//fb login successful
					{
						
						JSONObject userdata=object.getJSONObject("user");
						MainSingleTon.userid=userdata.getString("user_id");
						MainSingleTon.username=userdata.getString("name");
						MainSingleTon.accesstokem=userdata.getString("access_token");
						MainSingleTon.mainusername=userdata.getString("username");
						Intent in=new Intent(SplashActivity.this,HomeActivity.class);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(in);
						finish();
						System.out.println("Login Success@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   ");
					}else
						if(object.getInt("status_code")==201)//user not exist need to signup
						{
							Intent in=new Intent(SplashActivity.this,WelcomeActivity.class);
							in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(in);
							finish();
						}else
						{
							Intent in=new Intent(SplashActivity.this,WelcomeActivity.class);
							in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(in);
							finish();
						}
				} catch (JSONException e) {
					
					Toast.makeText(getApplicationContext(), "Error in login Please Try again", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Exception exception) {
				exception.printStackTrace();
			}
		});
		
	}
}
