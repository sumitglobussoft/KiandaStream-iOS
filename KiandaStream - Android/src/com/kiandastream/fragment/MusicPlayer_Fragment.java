package com.kiandastream.fragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kiandastream.HomeActivity;
import com.kiandastream.R;
import com.kiandastream.adapter.PlaylistAdapter;
import com.kiandastream.database.LocalData;
import com.kiandastream.database.ModelUserDatas;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.model.PlayingSongListmodel;
import com.kiandastream.musicplayer.MusicService;
import com.kiandastream.musicplayer.PlayerUpdate;
import com.kiandastream.musicplayer.UpdateMusicPlayer;
import com.kiandastream.utils.ImageLoader;
import com.kiandastream.utils.KiandaCallBack;
import com.kiandastream.utils.KiandaGetRequest;
import com.kiandastream.utils.MyCustomProgressDialog;
import com.kiandastream.utils.ServiceHandler;
import com.kiandastream.utils.Utilities;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

public class MusicPlayer_Fragment extends Fragment implements UpdateMusicPlayer, SeekBar.OnSeekBarChangeListener {

	private ImageButton btnPlay;
	private ImageButton btnAdd_toplaylist;
	private ImageButton btnLove,btndownload;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private ImageView song_image,showplaylist;
	RelativeLayout playlist_relativelayout;
	ListView playlist_listview;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private ImageLoader imageloader;
	// Media Player
	private boolean songstatus=false,isplaylistend=false;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	 Dialog dialog;
	 PlayingSongListmodel playsonglistmodel=new PlayingSongListmodel();
	private Utilities utils;
	private int currentSongIndex = 0; 
	/*private boolean isShuffle = false;
	private boolean isRepeat = false;*/
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private static final String TAG = "paymentExample";
	/**
	 * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
	 * 
	 * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
	 * from https://developer.paypal.com
	 * 
	 * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
	 * without communicating to PayPal's servers.
	 */
	private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

	// note that these credentials will differ between live & sandbox environments.
	private static final String CONFIG_CLIENT_ID = "AVcIuOk3yIrjdqIDvK46eWbR9Bq5VRHlkwbZvrZPnZmnaMK8-yxcYjPVzY9cg4X1zqPiVQLYtoRR43ZB";
	PlayerUpdate updatemain_player;
	private static final int REQUEST_CODE_PAYMENT = 1;
	private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
	private static final int REQUEST_CODE_PROFILE_SHARING = 3;
	
	ProgressDialog mProgressDialog;

	private static PayPalConfiguration config = new PayPalConfiguration()
	.environment(CONFIG_ENVIRONMENT)
	.clientId(CONFIG_CLIENT_ID)
	// The following are only used in PayPalFuturePaymentActivity.
	.merchantName("Example Merchant")
	.merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
	.merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		updatemain_player=new HomeActivity();
		View rootview=inflater.inflate(R.layout.player, container, false);
		//Hiding botton music player
		if(HomeActivity.bottonplayer!=null)
		{
			HomeActivity.bottonplayer.setVisibility(View.GONE);
		}
		
		// All player buttons
		MusicService.setListener(this);
		btnPlay = (ImageButton) rootview.findViewById(R.id.btnPlay);
		btnAdd_toplaylist = (ImageButton) rootview.findViewById(R.id.btnaddplaylist);
		btnLove = (ImageButton) rootview.findViewById(R.id.btnlove);
		btnNext = (ImageButton) rootview.findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) rootview.findViewById(R.id.btnPrevious);
		btndownload=(ImageButton)rootview.findViewById(R.id.btndownload);
		//btnPlaylist = (ImageButton) rootview.findViewById(R.id.btnPlaylist);
		btnRepeat = (ImageButton) rootview.findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) rootview.findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) rootview.findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) rootview.findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) rootview.findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) rootview.findViewById(R.id.songTotalDurationLabel);
		song_image=(ImageView)rootview.findViewById(R.id.songimage);
		//view for showing playlist
		showplaylist=(ImageView)rootview.findViewById(R.id.show_playlist);
		
		showplaylist.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if(dialog!=null)
				{
					if(dialog.isShowing())
					{
						dialog.dismiss();
					}else
					{
						ShowPlaylist();
					}
				}else
				{
					ShowPlaylist();
				}
				
			}
		});
		
		imageloader=new ImageLoader(getActivity());
		System.out.println("(getActivity().getCacheDir()"+getActivity().getCacheDir());
		System.out.println("Environment.getExternalStorageDirectory()+Android/data/dat  "+Environment.getExternalStorageDirectory()+"Android/data/data" );
	
		/*for(int i=0;i<15;i++)
		{
			PlayingSongListmodel model1=new PlayingSongListmodel();
			model1.setLoved(false);
			model1.setSong_ablum("11");
			model1.setSong_artist("Girish");
			model1.setSong_name("Hello Baby");
			model1.setSong_id(""+(28+i));
			model1.setSongurl("http://api.kiandastream.globusapps.com/static/songs/"+(28+i)+".mp3");
			model1.setSong_image("http://movyt.globusapps.com/uploads/songimage.jpg");
			MusicService.song_playlist.add(model1);
		}*/
		if(MainSingleTon.isplaysong)
		{
			MainSingleTon.isplaysong=false;
			playsonglistmodel=MusicService.song_playlist.get(Integer.parseInt(MainSingleTon.song_position));
			songTitleLabel.setText(playsonglistmodel.getSong_name());
			imageloader.DisplayImage(playsonglistmodel.getSong_image(), song_image);
			//starting song play
			
			Intent serviceIntent = new Intent(MusicService.class.getName());
			serviceIntent.setPackage("com.kiandastream.musicplayer");
			//serviceIntent.setAction(MusicService.ACTION_PLAY);
			//boolean bindResult = mContext.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
			
			
			getActivity().startService(serviceIntent.setAction("ACTION_PLAY").putExtra("POSITION", MainSingleTon.song_position));
			songstatus=false;
			if(playsonglistmodel.isLoved())
			{
				btnLove.setImageResource(R.drawable.btn_unlove);
			}else
			{
				btnLove.setImageResource(R.drawable.btn_love);
			}
			btnPlay.setImageResource(R.drawable.btn_pause);
		}else
		if(MusicService.mPlayer!=null)
		{
			PlayingSongListmodel songmodel=MusicService.getSongdetail();
			songTitleLabel.setText(songmodel.getSong_name());
			imageloader.DisplayImage(songmodel.getSong_image(), song_image);
			if(songmodel.isLoved())
			{
				btnLove.setImageResource(R.drawable.btn_unlove);
			}else
			{
				btnLove.setImageResource(R.drawable.btn_love);
			}
			if(MusicService.mPlayer.isPlaying())
			{
				btnPlay.setImageResource(R.drawable.btn_pause);
				updateProgressBar();
			}else
			{
				btnPlay.setImageResource(R.drawable.btn_play);
			}
		}
		if(getplayerSetting("REPEAT"))
		{
			btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
		}else
		{
			btnRepeat.setImageResource(R.drawable.btn_repeat);
		}	
		if(getplayerSetting("SHUFFLE"))
		{
			btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
		}else
		{
			btnShuffle.setImageResource(R.drawable.btn_shuffle);
		}
		// Mediaplayer
		Intent intent = new Intent(getActivity(), PayPalService.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		getActivity().startService(intent);
		btndownload.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				String[] array={MainSingleTon.songurl+playsonglistmodel.getSong_id()+".mp3",playsonglistmodel.getSong_name()
						,playsonglistmodel.getSong_id(),playsonglistmodel.getSong_artist()};
				
				PaymentCheck(array);
				//new DownloadFile().execute(array);
				
			}
		});

		/*btnPlaylist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {



				PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

				
				 * See getStuffToBuy(..) for examples of some available payment options.
				 

				Intent intent = new Intent(getActivity(), PaymentActivity.class);

				// send the same configuration for restart resiliency
				intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

				intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

				startActivityForResult(intent, REQUEST_CODE_PAYMENT);
			}
		});*/
		
		
		utils = new Utilities();

		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		//MusicService.mPlayer.setOnCompletionListener(this); // Important
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{
				// check for already playing
				if(MusicService.mPlayer!=null)
				{
				if(MusicService.mPlayer.isPlaying())
				{
					
						//MusicService.mPlayer.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
						getActivity().startService(new Intent(MusicService.ACTION_PAUSE));
						
				}else
				{
					
					getActivity().startService(new Intent(MusicService.ACTION_RESUME));
					btnPlay.setImageResource(R.drawable.btn_pause);
				}
				}else
				{	System.out.println("size of song playlist id "+MusicService.song_playlist.size());
					if(MusicService.song_playlist!=null && MusicService.song_playlist.size()>0)
					{
						
						Intent serviceIntent = new Intent(MusicService.class.getName());
						serviceIntent.setPackage("com.kiandastream.musicplayer");
						serviceIntent.setAction(MusicService.ACTION_PLAY);
					getActivity().startService(serviceIntent.setAction("ACTION_PLAY").putExtra("POSITION", "0"));
					btnPlay.setImageResource(R.drawable.btn_pause);
					}
				}
			}
		});

		/**
		 * Forward button click event
		 * Forwards song specified seconds
		 * */
		/*btnAdd_toplaylist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{
				// get current song position				
				getActivity().startService(new Intent(MusicService.ACTION_PLAY).putExtra("POSITION", "2"));
			}
		});

		*//**
		 * Backward button click event
		 * Backward song to specified seconds
		 * *//*
		btnLove.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = MusicService.mPlayer.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if(currentPosition - seekBackwardTime >= 0){
					// forward song
					MusicService.mPlayer.seekTo(currentPosition - seekBackwardTime);
				}else{
					// backward to starting position
					MusicService.mPlayer.seekTo(0);
				}

			}
		});*/

		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				/*if(currentSongIndex < (songsList.size() - 1)){
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				}else{
					// play first song
					playSong(0);
					currentSongIndex = 0;
				}*/
				if(!isplaylistend)
				{
					getActivity().startService(new Intent(MusicService.ACTION_NEXT));
				}else
				{
					Toast.makeText(getActivity(), "Last Song", Toast.LENGTH_LONG).show();
				}
			}
		});

		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*if(currentSongIndex > 0){
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				}else{
					// play last song
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}*/
				getActivity().startService(new Intent(MusicService.ACTION_REWIND));
			}
		});

		/**
		 * Button Click event for Repeat button
		 * Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{
				
				if(getplayerSetting("REPEAT"))
				{
					savePlayerSetting("REPEAT", false);
					Toast.makeText(getActivity(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}else{
					// make repeat to true
					savePlayerSetting("REPEAT", true);
					Toast.makeText(getActivity(), "Repeat is ON", Toast.LENGTH_SHORT).show();
					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
					
				}	
			}
		});

		/**
		 * Button Click event for Shuffle button
		 * Enables shuffle flag to true
		 * */
		btnShuffle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{
				if(getplayerSetting("SHUFFLE")){
					savePlayerSetting("SHUFFLE", false);
					Toast.makeText(getActivity(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}else{
					// make repeat to true
					savePlayerSetting("SHUFFLE", true);
					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}	
			}
		});
		
		
		/**
		 * Button Click event for Play list click event
		 * Launches list activity which displays list of songs
		 * */


		//new DownloadFile().execute("http://movyt.globusapps.com/uploads/videos/119/Muskurane.mp3");

		return rootview;
	}
	@Override
	public void onResume() {
		updatemain_player.showplayer(false);
		super.onResume();
	}
	@Override
	public void onDestroyView() 
	{
		if(MusicService.mPlayer!=null){
		if(MusicService.mPlayer.isPlaying())
		{
			updatemain_player.showplayer(true);
		}
		}
		super.onDestroyView();
	}
	
	// Payment related variables, according to items, price
	// TODO
	private PayPalPayment getThingToBuy(String paymentIntent) 
	{
		return new PayPalPayment(new BigDecimal("1.75"), "USD", "sample item",
				paymentIntent);
	}
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public Integer[] add(int firstno,int second)
	{
		Integer[] array={2,5};
		return array;
		
	}
	public void  playSong(int songIndex){
		// Play song
		try {
			System.out.println("");
			String songTitle = "Title";
			/*MusicService.mPlayer.reset();
			MusicService.mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@  "+songsList.get(songIndex).get("songPath"));
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@  "+songTitle);
			MusicService.mPlayer.setDataSource("http://movyt.globusapps.com/uploads/videos/119/Muskurane.mp3");
			//mp.setDataSource(songsList.get(songIndex).get("songPath"));
			MusicService.mPlayer.prepare();
			MusicService.mPlayer.start();*/
			// Displaying Song title
			songTitleLabel.setText(songTitle);

			// Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);

			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);

			// Updating progress bar
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() 
	{	
		mHandler.postDelayed(mUpdateTimeTask, 100);        
	}	

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() 
		{	if(MusicService.mPlayer!=null)
		{
			if(songstatus)
			{
			if(MusicService.mPlayer.isPlaying())
			{
			long totalDuration = MusicService.mPlayer.getDuration();
			long currentDuration = MusicService.mPlayer.getCurrentPosition();

			// Displaying Total Duration time
			songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			// Displaying time completed playing
			songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

			// Updating progress bar
			int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			//Log.d("Progress", ""+progress);
			songProgressBar.setProgress(progress);
			if(songstatus)
			mHandler.postDelayed(this, 100);
			}
			}
		}
			// Running this thread after 100 milliseconds
			
		
		}
	};

	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		if(MusicService.mPlayer!=null){
		int totalDuration = MusicService.mPlayer.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

		// forward or backward to certain seconds
		MusicService.mPlayer.seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();
		}
	}

	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {
		songstatus=false;
		// check for repeat is ON or OFF
		/*if(isRepeat){
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if(isShuffle){
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else{
			// no repeat or shuffle ON - play next song
			if(currentSongIndex < (songsList.size() - 1)){
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}*/
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				PaymentConfirmation confirm =data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
				if (confirm != null) {
					try {
						Log.i(TAG, confirm.toJSONObject().toString(4));
						confirm.getPayment().getAmountAsLocalizedString();
						Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
						Log.i(TAG,confirm.getPayment().getAmountAsLocalizedString());
						/**
						 *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
						 * or consent completion.
						 * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
						 * for more details.
						 *
						 * For sample mobile backend interactions, see
						 * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
						 */
						Toast.makeText(
								getActivity(),
								"PaymentConfirmation info received from PayPal", Toast.LENGTH_LONG)
								.show();

					} catch (JSONException e) {
						Log.e(TAG, "an extremely unlikely failure occurred: ", e);
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) 
			{
				Log.i(TAG, "The user canceled.");
			} else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) 
			{
				Log.i(
						TAG,
						"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
			}
		} else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {

			//future payment

		} else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {

			//REQUEST_CODE_PROFILE_SHARING
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void onDetach() {

		getActivity().stopService(new Intent(getActivity(), PayPalService.class));

		super.onDetach();
	}

	private void PaymentCheck(String[] params)
	{
		String songid;
		final String[] downloadparams = params;
		songid = params[2];

		mProgressDialog = new MyCustomProgressDialog(getActivity());
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();

		String parameter=MainSingleTon.Main_url+"t=download&user_id="+MainSingleTon.userid+"&access_token="
					+MainSingleTon.accesstokem+"&song_id="+songid;
		
		KiandaGetRequest getRequest=new KiandaGetRequest(getActivity());
		getRequest.executeRequest(parameter, new KiandaCallBack() {
			
			@Override
			public void onSuccess(JSONObject result) {
				
				try {
					JSONObject jsonObject=result;
					if(jsonObject.getInt("status_code")==200){
						new DownloadFile().execute(downloadparams);
					}else if(jsonObject.getInt("status_code")==100){
						
						PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
						// * See getStuffToBuy(..) for examples of some available payment options.
						
						Intent intent = new Intent(getActivity(), PaymentActivity.class);
						// send the same configuration for restart resiliency
						intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
						intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
						startActivityForResult(intent, REQUEST_CODE_PAYMENT);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Exception exception) {
				
			}
		});
	}

	private class DownloadFile extends AsyncTask<String, Integer, String>
	{
		String songname,songpath,songfilename,songid,song_artistname;
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
				PackageManager m = getActivity().getPackageManager();
				String s = getActivity().getPackageName();
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
			LocalData localdatabase=new LocalData(getActivity());
			ModelUserDatas model=new ModelUserDatas();
			model.setSongartistname(song_artistname);
			model.setSongid(songid);
			model.setSongimagepath("adsadas");
			model.setSongname(songname);
			model.setSongpath(songpath);
			localdatabase.addNewSong(model);
			}
			super.onPostExecute(result);
		}
	}


	@Override
	public void updatebufferingstatus(MediaPlayer mp, int percent) 
	{
		songProgressBar.setSecondaryProgress(percent);
		
	}
	@Override
	public void onError(MediaPlayer mp, int what, int extra) 
	{
		Toast.makeText(getActivity(), "Error in playing song ", Toast.LENGTH_LONG).show();
		btnPlay.setImageResource(R.drawable.btn_play);
	}
	@Override
	public void onPreparation(MediaPlayer arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void startUpdating(MediaPlayer player) 
	{	
		songstatus=true;
		songProgressBar.setProgress(0);
		songProgressBar.setMax(100);
		playsonglistmodel=MusicService.getSongdetail();
		songTitleLabel.setText(playsonglistmodel.getSong_name());
		imageloader.DisplayImage(playsonglistmodel.getSong_image(), song_image);
		if(playsonglistmodel.isLoved())
		{
			btnAdd_toplaylist.setBackgroundResource(R.drawable.btn_unlove);
		}else
		{
			btnAdd_toplaylist.setBackgroundResource(R.drawable.btn_love);
		}
		updateProgressBar();
		
		
		
	}
	@Override
	public void stopUpdating(MediaPlayer player) {
		songstatus=false;
		
	}
	//Setting repeat and suffle setting from shared preferences
	private void savePlayerSetting(String key,boolean b)
	{
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences("KiandaStream", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, b);
		editor.commit();
	}
	//Getting repeat and suffle setting from shared preferences
	private boolean getplayerSetting(String key)
	{
		SharedPreferences sharedpref=getActivity().getSharedPreferences("KiandaStream", Context.MODE_PRIVATE);
		return sharedpref.getBoolean(key, false);
	}
	@Override
	public void playlistend() {
		isplaylistend=true;
		
	}
	//for loving song
	class LoveSong extends AsyncTask<String, Void, String>
	{
		String type,position;
		@Override
		protected void onPreExecute() 
		{
			
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) 
		{ String parameter;
			type=params[0];//if 0 then song is not loved and if 1 then song is loved
			position=params[1];//position of song in play arraylist
			if(type.equals("0"))//Loving song
			{
				parameter=MainSingleTon.Main_url+"t=love&action=love&songid="+MusicService.song_playlist.get(Integer.parseInt(position)).getSong_id()+"&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem;
			}else//unloving song
				 parameter=MainSingleTon.Main_url+"t=love&action=unlove&songid="+MusicService.song_playlist.get(Integer.parseInt(position)).getSong_id()+"&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem;
			
			ServiceHandler sh=new ServiceHandler();
			String result=sh.makeServiceCall(parameter, ServiceHandler.GET);
			
			return result;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			System.out.println("response from loved songs ");
			if(result!=null)
			{
				JSONObject object;
				try {
					object = new JSONObject(result);
					if(object.getInt("status_code")==200)
					{
						if(type.equals("0"))//Loving song successfull
						{
							MusicService.song_playlist.get(Integer.parseInt(position)).setLoved(true);
						}else//unloving long successfull
						{
							MusicService.song_playlist.get(Integer.parseInt(position)).setLoved(false);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			super.onPostExecute(result);
		}
	}
	void ShowPlaylist()
	{
		    dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
		    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		    dialog.setCancelable(true);
		    dialog.setContentView(R.layout.playlist_dialog);

		    ListView listview;
		    ImageView no_songinplaylist,hideplaylist,clear_queue,saveas_playlist;
		    listview=(ListView)dialog.findViewById(R.id.playlist_listview);
		    no_songinplaylist=(ImageView)dialog.findViewById(R.id.no_songinplaylist);
		    hideplaylist=(ImageView)dialog.findViewById(R.id.close_button);
		    clear_queue=(ImageView)dialog.findViewById(R.id.clear_queue);
		    saveas_playlist=(ImageView)dialog.findViewById(R.id.saveas_playlist);
		    System.out.println("size of playlist "+MusicService.song_playlist.size());
		    if(MusicService.song_playlist!=null && MusicService.song_playlist.size()>0)
		    {
		    	 PlaylistAdapter adapter=new PlaylistAdapter(getActivity(), MusicService.song_playlist);
		    	 listview.setVisibility(View.VISIBLE);
			     no_songinplaylist.setVisibility(View.INVISIBLE);
		    	 listview.setAdapter(adapter);
		    	 
		    }else
		    {
		    	listview.setVisibility(View.INVISIBLE);
		    	no_songinplaylist.setVisibility(View.VISIBLE);
		    }
		    clear_queue.setOnClickListener(new OnClickListener() 
		    {
				
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					
				}
			});
		    saveas_playlist.setOnClickListener(new OnClickListener() 
		    {
				
				@Override
				public void onClick(View v) 
				{
					
					
				}
			});
		    hideplaylist.setOnClickListener(new OnClickListener() 
		    {
				
				@Override
				public void onClick(View v) 
				{
					dialog.dismiss();
					
				}
			});
		    dialog.show();
	}
	@Override
	public void onStop() 
	{
		imageloader.clearCache();
		
		super.onStop();
		
	}
}