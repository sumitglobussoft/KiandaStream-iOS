/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kiandastream.musicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.kiandastream.HomeActivity;
import com.kiandastream.R;
import com.kiandastream.model.PlayingSongListmodel;

/**
 * Service that handles media playback. This is the Service through which we perform all the media
 * handling in our application. Upon initialization, it starts a {@link MusicRetriever} to scan
 * the user's media. Then, it waits for Intents (which come from our main activity,
 * {@link MainActivity}, which signal the service to perform specific operations: Play, Pause,
 * Rewind, Skip, etc.
 */
public class MusicService extends Service implements OnCompletionListener, OnPreparedListener,OnBufferingUpdateListener,
                OnErrorListener {

	// Interface 
	public static ArrayList<PlayingSongListmodel> song_playlist=new ArrayList<PlayingSongListmodel>();
	public static ArrayList<String> songplayed_list=new ArrayList<String>();
	static UpdateMusicPlayer listener;
	PlayerUpdate updatelistner=new HomeActivity();
	public static String positionofsong="0";
	private NotificationManager nManager;
	private NotificationCompat.Builder nBuilder;
	private RemoteViews remoteView;

	public static void setListener(UpdateMusicPlayer lister)
	{
		listener=lister;
	}
	public static void removelistener()
	{
		listener=null;
	}
    // The tag we put on debug messages
    final static String TAG = "KiandaStream";

    // These are the Intent actions that we are prepared to handle. Notice that the fact these
    // constants exist in our class is a mere convenience: what really defines the actions our
    // service can handle are the <action> tags in the <intent-filters> tag for our service in
    // AndroidManifest.xml.
    public static final String ACTION_TOGGLE_PLAYBACK =
            "com.kiandastream.mediaplayer.action.TOGGLE_PLAYBACK";
    public static final String ACTION_PLAY = "com.kiandastream.mediaplayer.action.PLAY";
    public static final String ACTION_RESUME = "com.kiandastream.mediaplayer.action.RESUME";
    public static final String ACTION_PAUSE = "com.kiandastream.mediaplayer.action.PAUSE";
    public static final String ACTION_STOP = "com.kiandastream.mediaplayer.action.STOP";
    public static final String ACTION_SKIP = "com.kiandastream.mediaplayer.action.SKIP";
    public static final String ACTION_NEXT = "com.kiandastream.mediaplayer.action.NEXT";
    public static final String ACTION_REWIND = "com.kiandastream.mediaplayer.action.REWIND";
    public static final String ACTION_URL = "com.kiandastream.mediaplayer.action.URL";

    // The volume we set the media player to when we lose audio focus, but are allowed to reduce
    // the volume instead of stopping playback.
    public static final float DUCK_VOLUME = 0.1f;

    // our media player
   public static MediaPlayer mPlayer = null;

    // our AudioFocusHelper object, if it's available (it's available on SDK level >= 8)
    // If not available, this will be null. Always check for null before using!
    

    // indicates the state our service:
    enum State 
    {
        Retrieving, // the MediaRetriever is retrieving music
        Stopped,
        Complete,
        Starting, // media player is stopped and not prepared to play
        Preparing,  // media player is preparing...
        Playing,    // playback active (media player ready!). (but the media player may actually be
                    // paused in this state if we don't have audio focus. But we stay in this state
                    // so that we know we have to resume playback once we get focus back)
        Paused      // playback paused (media player ready!)
    };

    State mState = State.Starting;

    // if in Retrieving mode, this flag indicates whether we should start playing immediately
    // when we are ready or not.
    boolean mStartPlayingAfterRetrieve = false;

    // if mStartPlayingAfterRetrieve is true, this variable indicates the URL that we should
    // start playing when we are ready. If null, we should play a random song from the device
    Uri mWhatToPlayAfterRetrieve = null;

    enum PauseReason 
    {
        UserRequest,  // paused by user request
        FocusLoss,    // paused because of audio focus loss
    };

    // why did we pause? (only relevant if mState == State.Paused)
    PauseReason mPauseReason = PauseReason.UserRequest;

    // do we have audio focus?
   public static  enum AudioFocus {
        NoFocusNoDuck,    // we don't have audio focus, and can't duck
        NoFocusCanDuck,   // we don't have focus, but can play at a low volume ("ducking")
        Focused           // we have full audio focus
    }
   /**
    * Configures service as a foreground service. A foreground service is a service that's doing
    * something the user is actively aware of (such as playing music), and must appear to the
    * user as a notification. That's why we create the notification here.
    */
   public boolean setNotification(String songname)
   {
	   nBuilder = new NotificationCompat.Builder(this)
	    .setContentTitle("KiandaStream")
	    .setSmallIcon(R.drawable.headphone)
	    .setOngoing(true);

	    remoteView = new RemoteViews(getPackageName(), R.layout.notificationview);

	    //set the button listeners
	    setListeners(remoteView,songname);
	    nBuilder.setContent(remoteView);
	    Notification notification;
	    nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	    notification=nBuilder.build();
	    /*nManager.notify(NOTIFICATION_ID, nBuilder.build());*/
	    startForeground(NOTIFICATION_ID, notification);
	return true;
	   
   }
   public void setListeners(RemoteViews view,String songname)
   {
       //listener 1
       view.setTextViewText(R.id.songname, songname);
       PendingIntent backword = PendingIntent.getService(this, 0, new Intent(MusicService.ACTION_REWIND), 0);
       view.setOnClickPendingIntent(R.id.backword, backword);

       //listener 2
      if(mState==State.Playing)
      {
    	 
       PendingIntent playpause = PendingIntent.getService(this, 1, new Intent(MusicService.ACTION_PAUSE), 0);
       view.setOnClickPendingIntent(R.id.play, playpause);
       view.setImageViewResource(R.id.play, R.drawable.pause);
      }else
      {
    	 if(mState==State.Paused)
    	  {
    		  PendingIntent playpause = PendingIntent.getService(this, 1, new Intent(MusicService.ACTION_RESUME), 0);
              view.setOnClickPendingIntent(R.id.play, playpause);
              view.setImageViewResource(R.id.play, R.drawable.play);
    	  }
    	 
      } 
     //listener 3
       
       PendingIntent forward = PendingIntent.getService(this, 2, new Intent(MusicService.ACTION_NEXT), 0);
       view.setOnClickPendingIntent(R.id.forward, forward);
       
     //listener 4
       
       PendingIntent close = PendingIntent.getService(this, 3, new Intent(MusicService.ACTION_STOP), 0);
       view.setOnClickPendingIntent(R.id.close, close);
   }
   public static  enum SongRepeatStatus 
   {
       Normal,//Song playing normally
       Repeat// Song playing in repeat
   }
   public static  enum SongSuffleStatus 
   {
       Normal,//Song playing normally
       Suffle// Song playing in repeat
   }
   
   public static PlayingSongListmodel getSongdetail()
   {
	  if(mPlayer!=null)
	  {
	   if(song_playlist!=null)
	   {
		   if(song_playlist.size()>Integer.parseInt(positionofsong))
		   {
			   return song_playlist.get(Integer.parseInt(positionofsong));
		   }else
		   {
			   return null;
		   }
	   }else
	   {
		   return null;
	   }
	  }else
	  {
		  return null;
	  }
	
	   
   }
    AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;

    // title of the song we are currently playing
    String mSongTitle = "";

    // whether the song we are playing is streaming from the network
    boolean mIsStreaming = false;

    // Wifi lock that we hold when streaming files from the internet, in order to prevent the
    // device from shutting off the Wifi radio
    WifiLock mWifiLock;

    // The ID we use for the notification (the onscreen alert that appears at the notification
    // area at the top of the screen as an icon -- and as text as well if the user expands the
    // notification area).
    final int NOTIFICATION_ID = 1;

    // Our instance of our MusicRetriever, which handles scanning for media and
    // providing titles and URIs as we need.
   
    // our RemoteControlClient object, which will use remote control APIs available in
    // SDK level >= 14, if they're available.
  

    // Dummy album art we will pass to the remote control (if the APIs are available).
    Bitmap mDummyAlbumArt;

    // The component name of MusicIntentReceiver, for use with media button and remote control
    // APIs
    ComponentName mMediaButtonReceiverComponent;

    AudioManager mAudioManager;
    NotificationManager mNotificationManager;

    Notification mNotification = null;

    /**
     * Makes sure the media player exists and has been reset. This will create the media player
     * if needed, or reset the existing media player if one already exists.
     */
    void createMediaPlayerIfNeeded() 
    {
        if (mPlayer == null) 
        {
            mPlayer = new MediaPlayer();
            System.out.println("i am in creating meida player ");
            // Make sure the media player will acquire a wake-lock while playing. If we don't do
            // that, the CPU might go to sleep while the song is playing, causing playback to stop.
            //
            // Remember that to use this, we have to declare the android.permission.WAKE_LOCK
            // permission in AndroidManifest.xml.
            mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            // we want the media player to notify us when it's ready preparing, and when it's done
            // playing:
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnBufferingUpdateListener(this);
            mPlayer.setOnErrorListener(this);
        }
        else
            mPlayer.reset();
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "debug: Creating service");

        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                        .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Create the retriever and start an asynchronous task that will prepare it.
       

        // create the Audio Focus Helper, if the Audio Focus feature is available (SDK 8 or above)
      
    }

    /**
     * Called when we receive an Intent. When we receive an intent sent to us via startService(),
     * this is the method that gets called. So here we react appropriately depending on the
     * Intent's action, which specifies what is being requested of us.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) 
    {
        String action = intent.getAction();
        System.out.println("i am geeting action "+action);
        if (action.equals(ACTION_TOGGLE_PLAYBACK)) processTogglePlaybackRequest();
        else if (action.equals(ACTION_PLAY)) 
        {	
        	positionofsong=intent.getStringExtra("POSITION");
        	System.out.println("Position get from intent in service "+positionofsong);
        	processPlayRequest(positionofsong);
        }
        else if (action.equals(ACTION_PAUSE))
        {
        	processPauseRequest();
        } 
        else if (action.equals(ACTION_RESUME))
        {
        	processResumeRequest();
        } 
        else if (action.equals(ACTION_NEXT))
        {
        	processNextRequest();
        } 
        else if (action.equals(ACTION_SKIP)) processSkipRequest();
        else if (action.equals(ACTION_STOP)) 
        {
        	processStopRequest();
        }
        else if (action.equals(ACTION_REWIND)) processPreviousRequest();
       // else if (action.equals(ACTION_URL)) processAddRequest(intent);

        return START_NOT_STICKY; // Means we started the service, but don't want it to
                                 // restart in case it's killed.
    }

    void processTogglePlaybackRequest() 
    {
        if (mState == State.Paused || mState == State.Stopped) 
        {
            processPlayRequest("a");
        } else {
            processPauseRequest();
        }
    }
    //this method is for play request
    void processPlayRequest(String url) 
    {
    	if(song_playlist!=null &&song_playlist.size()>Integer.parseInt(url))
    	{
    		
    	
    	System.out.println("i am in processPlayRequest before condition   "+url);
    	if(mState==State.Starting)
    	{	
    		System.out.println("i am in processPlayRequest condition State.Starting");
    		createMediaPlayerIfNeeded();
    		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
            	
            	//String songurl="http://api.kiandastream.globusapps.com/static/songs/"+song_playlist.get(Integer.parseInt(url)).getSong_id()+".mp3";
            	System.out.println("url of song playing is "+song_playlist.get(Integer.parseInt(url)).getSongurl());
				mPlayer.setDataSource(song_playlist.get(Integer.parseInt(url)).getSongurl());
				setNotification("Loading");
				mState = State.Preparing;
				//Setting title in notification bar
	           /* setUpAsForeground(mSongTitle + " (loading)");*/
				setNotification("Loading");
	            if(!mWifiLock.isHeld())
  	              mWifiLock.acquire();
	            
	            mPlayer.prepareAsync();
	            
	        } catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          
            
    	}else
    		if(mState==State.Stopped)
        	{	
        		System.out.println("i am in processPlayRequest condition State.Complete");
        		createMediaPlayerIfNeeded();
        		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                	//String songurl="http://api.kiandastream.globusapps.com/static/songs/"+song_playlist.get(Integer.parseInt(url)).getSong_id()+".mp3";
                	System.out.println("url of song playing is "+song_playlist.get(Integer.parseInt(url)).getSongurl());
    				
                	mPlayer.setDataSource(song_playlist.get(Integer.parseInt(url)).getSongurl());
                	setNotification("Loading");
                	mState = State.Preparing;
    				//Setting title in notification bar
    				
    	            if(!mWifiLock.isHeld())
      	              mWifiLock.acquire();
    	            mPlayer.prepareAsync();
    	           
    	        } catch (NumberFormatException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalStateException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
              
                
        	}else
    	if(mState==State.Paused)
    	{
    		System.out.println("i am in processPlayRequest condition .State.Paused");
    		if(mPlayer!=null)
    		{
    			mPlayer.reset();
    		}else
    		createMediaPlayerIfNeeded();
    		
    		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
            	//String songurl="http://api.kiandastream.globusapps.com/static/songs/"+song_playlist.get(Integer.parseInt(url)).getSong_id()+".mp3";
            	System.out.println("url of song playing is "+song_playlist.get(Integer.parseInt(url)).getSongurl());
				mPlayer.setDataSource(song_playlist.get(Integer.parseInt(url)).getSongurl());
				setNotification("Loading");
				mState = State.Preparing;
				
				//Setting title in notification bar
				
	            mPlayer.prepareAsync();
	            if(!mWifiLock.isHeld())
	              mWifiLock.acquire();
	        } catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		configAndStartMediaPlayer();
    	}else 
    		if(mState==State.Playing)
    		{
    			System.out.println("i am in processPlayRequest condition .State.Playing");
    			if(listener!=null) listener.stopUpdating(mPlayer);
    			mPlayer.pause();
    			mPlayer.reset();
        		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                	//String songurl="http://api.kiandastream.globusapps.com/static/songs/"+song_playlist.get(Integer.parseInt(url)).getSong_id()+".mp3";
                	System.out.println("url of song playing is "+song_playlist.get(Integer.parseInt(url)).getSongurl());
    				mPlayer.setDataSource(song_playlist.get(Integer.parseInt(url)).getSongurl());
    				setNotification("Loading");
    				mState = State.Preparing;
    				//Setting title in notification bar
    	           
    	            mPlayer.prepareAsync();
    	            if(!mWifiLock.isHeld())
    	              mWifiLock.acquire();
    	            
    	            
    	        } catch (NumberFormatException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalStateException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        		configAndStartMediaPlayer();
    		}
    	}
    }
    public void clearNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
    void processPauseRequest() 
    {
        if (mState == State.Starting) 
        {
            // If we are still retrieving media, clear the flag that indicates we should start
            // playing when we're ready
        	relaxResources(true);
            mStartPlayingAfterRetrieve = false;
            return;
        }

        if (mState == State.Playing) 
        {
            // Pause media player and cancel the 'foreground service' state.
            mState = State.Paused;
            mPlayer.pause();
            setNotification(song_playlist.get(Integer.parseInt(positionofsong)).getSong_name());
            if(listener!=null)
            {
            	listener.stopUpdating(mPlayer);
            }
            relaxResources(false); 
           // while paused, we always retain the MediaPlayer
            // do not give up audio focus
        }

        // Tell any remote controls that our playback state is 'paused'.
       
    }
    void processResumeRequest()
    {
    	if(mState == State.Starting)
    	{
    		if(song_playlist!=null && song_playlist.size()>0)
    		{
    			createMediaPlayerIfNeeded();
        		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                	//String songurl="http://api.kiandastream.globusapps.com/static/songs/"+song_playlist.get(0).getSong_id()+".mp3";
                	System.out.println("url of song playing is "+song_playlist.get(0).getSongurl());
    				mPlayer.setDataSource(song_playlist.get(0).getSongurl());
    				setNotification("Loading");
    				mState = State.Preparing;
    				//Setting title in notification bar
    	            
    	            mPlayer.prepareAsync();
    	            if(!mWifiLock.isHeld())
    	              mWifiLock.acquire();
    	        } catch (NumberFormatException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalStateException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    		
    	}else 
    		if(mState == State.Paused)
    		{
    			if(mPlayer!=null)
    			{
    				mPlayer.start();
    				
    				if(listener!=null)listener.startUpdating(mPlayer); 
    				mState=State.Playing;
    				setNotification(song_playlist.get(Integer.parseInt(positionofsong)).getSong_name());
    			}
    			
    		}
    }
    void processNextRequest()
    {
    	if(mState == State.Starting)
    	{
    		if(song_playlist!=null)
    		{
    		if((Integer.parseInt(positionofsong)+1)<song_playlist.size() )
    		{
    			Integer pos=Integer.parseInt(positionofsong)+1;
    			positionofsong=pos.toString();
    			createMediaPlayerIfNeeded();
        		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                	String songurl="http://api.kiandastream.globusapps.com/static/songs/"+song_playlist.get(Integer.parseInt(positionofsong)).getSong_id()+".mp3";
                	System.out.println("url of song playing is "+song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
                	mPlayer.setDataSource(song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
    				mState = State.Preparing;
    				//Setting title in notification bar
    	            
    	            mPlayer.prepareAsync();
    	            if(!mWifiLock.isHeld())
    	              mWifiLock.acquire();
    	        } catch (NumberFormatException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalStateException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}else relaxResources(true);
    		}else relaxResources(true);
    	}else 
    		if(mState == State.Playing)
        	{
        		if(song_playlist!=null)
        		{
        		if((Integer.parseInt(positionofsong)+1)<song_playlist.size() )
        		{
        			Integer pos=Integer.parseInt(positionofsong)+1;
        			positionofsong=pos.toString();
        			if(listener!=null) listener.stopUpdating(mPlayer);
        			mPlayer.pause();
        			
        			mPlayer.reset();
        			
            		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                    	//String songurl="http://api.kiandastream.globusapps.com/static/songs/"+song_playlist.get(Integer.parseInt(positionofsong)).getSong_id()+".mp3";
                    	System.out.println("url of song playing is "+song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
                    	mPlayer.setDataSource(song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
                    	setNotification("Loading");
                    	mState = State.Preparing;
        				//Setting title in notification bar
        	           
        	            mPlayer.prepareAsync();
        	            if(!mWifiLock.isHeld())
        	              mWifiLock.acquire();
        	        } catch (NumberFormatException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IllegalArgumentException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (SecurityException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IllegalStateException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		}
        	}
    		if(mState == State.Paused)
    		{
    			if(song_playlist!=null)
            		{
            		if((Integer.parseInt(positionofsong)+1)<song_playlist.size() )
            		{
            			Integer pos=Integer.parseInt(positionofsong)+1;
            			positionofsong=pos.toString();
            			createMediaPlayerIfNeeded();
            			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                        	//String songurl="http://api.kiandastream.globusapps.com/static/songs/"+song_playlist.get(Integer.parseInt(positionofsong)).getSong_id()+".mp3";
                        	System.out.println("url of song playing is "+song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
                        	mPlayer.setDataSource(song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
                        	setNotification("Loading");
                        	mState = State.Preparing;
            				//Setting title in notification bar
            	           
            	            mPlayer.prepareAsync();
            	            if(!mWifiLock.isHeld())
            	              mWifiLock.acquire();
            	        } catch (NumberFormatException e) {
            				e.printStackTrace();
            			} catch (IllegalArgumentException e) {
            				e.printStackTrace();
            			} catch (SecurityException e) {
            				e.printStackTrace();
            			} catch (IllegalStateException e) {
            				e.printStackTrace();
            			} catch (IOException e) {
            				e.printStackTrace();
            			}
            		}else
            		{
            			Toast.makeText(getApplicationContext(), "Last Song of Playlist", Toast.LENGTH_LONG).show();
            			relaxResources(true);
            		}
            		}
    			
    			
    		}
    }
    void processPreviousRequest() {

    	if(mState == State.Starting)
    	{
    		if(song_playlist!=null)
    		{
    		if(Integer.parseInt(positionofsong)>0)
    		{	
    		if((Integer.parseInt(positionofsong)-1)<song_playlist.size() )
    		{
    			Integer pos=Integer.parseInt(positionofsong)-1;
    			positionofsong=pos.toString();
    			createMediaPlayerIfNeeded();
        		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                	//String songurl="http://api.kiandastream.globusapps.com/static/songs/"+song_playlist.get(Integer.parseInt(positionofsong)).getSong_id()+".mp3";
                	System.out.println("url of song playing is "+song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
                	mPlayer.setDataSource(song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
                	setNotification("Loading");
                	mState = State.Preparing;
    				//Setting title in notification bar
    	            
    	            mPlayer.prepareAsync();
    	            if(!mWifiLock.isHeld())
    	              mWifiLock.acquire();
    	        } catch (NumberFormatException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalStateException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}else relaxResources(true);
    		}
    		}else relaxResources(true);
    	}else 
    		if(mState == State.Playing)
        	{
        		if(song_playlist!=null)
        		{
        		if(Integer.parseInt(positionofsong)>0)
        		{	
        		if((Integer.parseInt(positionofsong)-1)<song_playlist.size() )
        		{
        			Integer pos=Integer.parseInt(positionofsong)-1;
        			positionofsong=pos.toString();
        			if(listener!=null) listener.stopUpdating(mPlayer);
        			mPlayer.pause();
        			
        			mPlayer.reset();
        			
            		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                    	//String songurl="http://api.kiandastream.globusapps.com/static/songs/"+song_playlist.get(Integer.parseInt(positionofsong)).getSong_id()+".mp3";
                    	System.out.println("url of song playing is "+song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
                    	mPlayer.setDataSource(song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
                    	setNotification("Loading");
                    	mState = State.Preparing;
        				//Setting title in notification bar
        	            
        	            mPlayer.prepareAsync();
        	            if(!mWifiLock.isHeld())
        	              mWifiLock.acquire();
        	        } catch (NumberFormatException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IllegalArgumentException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (SecurityException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IllegalStateException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		}
        		}
        	}
    		if(mState == State.Paused)
    		{
    			if(song_playlist!=null)
            		{
    				if(Integer.parseInt(positionofsong)>0)
    				{
            		if((Integer.parseInt(positionofsong)-1)<song_playlist.size() )
            		{
            			Integer pos=Integer.parseInt(positionofsong)-1;
            			positionofsong=pos.toString();
            			createMediaPlayerIfNeeded();
            			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                        	//String songurl="http://api.kiandastream.globusapps.com/static/songs/"+song_playlist.get(Integer.parseInt(positionofsong)).getSong_id()+".mp3";
                        	System.out.println("url of song playing is "+song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
                        	mPlayer.setDataSource(song_playlist.get(Integer.parseInt(positionofsong)).getSongurl());
                        	setNotification("Loading");
                        	mState = State.Preparing;
            				//Setting title in notification bar
            	          
            	            mPlayer.prepareAsync();
            	            if(!mWifiLock.isHeld())
            	              mWifiLock.acquire();
            	        } catch (NumberFormatException e) {
            				e.printStackTrace();
            			} catch (IllegalArgumentException e) {
            				e.printStackTrace();
            			} catch (SecurityException e) {
            				e.printStackTrace();
            			} catch (IllegalStateException e) {
            				e.printStackTrace();
            			} catch (IOException e) {
            				e.printStackTrace();
            			}
            		}else
            		{
            			Toast.makeText(getApplicationContext(), "Last Song of Playlist", Toast.LENGTH_LONG).show();
            			relaxResources(true);
            		}
    				}
            		}
    			
    			
    		}
    
    }

    void processSkipRequest() {
        if (mState == State.Playing || mState == State.Paused) {
           
         //   playNextSong(null);
        }
    }

    void processStopRequest() 
    {
    	clearNotification();
       relaxResources(true);
       stopSelf();
    }

    void processStopRequest(boolean force) {
        if (mState == State.Playing || mState == State.Paused || force) {
            mState = State.Stopped;

            // let go of all resources...
            relaxResources(true);
           

            // Tell any remote controls that our playback state is 'paused'.
            

            // service is no longer necessary. Will be started again if needed.
            stopSelf();
        }
    }

    /**
     * Releases resources used by the service for playback. This includes the "foreground service"
     * status and notification, the wake locks and possibly the MediaPlayer.
     *
     * @param releaseMediaPlayer Indicates whether the Media Player should also be released or not
     */
    void relaxResources(boolean releaseMediaPlayer) {
        // stop being a foreground service
        stopForeground(true);

        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }

        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld()) mWifiLock.release();
    }

    /**/

    /**
     * Reconfigures MediaPlayer according to audio focus settings and starts/restarts it. This
     * method starts/restarts the MediaPlayer respecting the current audio focus state. So if
     * we have focus, it will play normally; if we don't have focus, it will either leave the
     * MediaPlayer paused or set it to a low volume, depending on what is allowed by the
     * current focus settings. This method assumes mPlayer != null, so if you are calling it,
     * you have to do so from a context where you are sure this is the case.
     */
    void configAndStartMediaPlayer() 
    {
    	
        /*if (mAudioFocus == AudioFocus.NoFocusNoDuck) 
        {
            // If we don't have audio focus and can't duck, we have to pause, even if mState
            // is State.Playing. But we stay in the Playing state so that we know we have to resume
            // playback once we get the focus back.
            if (mPlayer.isPlaying()) mPlayer.pause();
            return;
        }
        else if (mAudioFocus == AudioFocus.NoFocusCanDuck)
            mPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME);  // we'll be relatively quiet
        else
            mPlayer.setVolume(1.0f, 1.0f); // we can be loud
*/        
       /* if (!mPlayer.isPlaying())
        {
        	mPlayer.start();
        } else mPlayer.start();*/
    }

    void processAddRequest(Intent intent) {
        // user wants to play a song directly by URL or path. The URL or path comes in the "data"
        // part of the Intent. This Intent is sent by {@link MainActivity} after the user
        // specifies the URL/path via an alert box.
        if (mState == State.Retrieving) {
            // we'll play the requested URL right after we finish retrieving
            mWhatToPlayAfterRetrieve = intent.getData();
            mStartPlayingAfterRetrieve = true;
        }
        else if (mState == State.Playing || mState == State.Paused || mState == State.Stopped) {
            Log.i(TAG, "Playing from URL/path: " + intent.getData().toString());
          
           // playNextSong(intent.getData().toString());
        }
    }

    

    /**
     * Starts playing the next song. If manualUrl is null, the next song will be randomly selected
     * from our Media Retriever (that is, it will be a random song in the user's device). If
     * manualUrl is non-null, then it specifies the URL or path to the song that will be played
     * next.
     */
    void playNextSong(String manualUrl) {
        mState = State.Stopped;
        //relaxResources(false); // release everything except MediaPlayer

        try {
           // MusicRetriever.Item playingItem = null;
        	if(mPlayer!=null)
        	mPlayer.reset();
        	
            if (manualUrl != null) 
            {
                // set the source of the media player to a manual URL or path
                createMediaPlayerIfNeeded();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(manualUrl);
                mIsStreaming = manualUrl.startsWith("http:") || manualUrl.startsWith("https:");
                mState = State.Preparing;
              
                mPlayer.prepareAsync();
                mWifiLock.acquire();
              //  mPlayer.start();
              //  playingItem = new MusicRetriever.Item(0, null, manualUrl, null, 0);
            }
           
        }
        catch (IOException ex) {
            Log.e("MusicService", "IOException playing next song: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /** Called when media player is done playing current song. */
    public void onCompletion(MediaPlayer player) 
    {
    	stopForeground(true);
    	System.out.println("hello i am in onCompletion");
    	if(listener!=null)
    	{ 
    		listener.stopUpdating(player);
    	    listener.onCompletion(player);
    	}
    	if(songplayed_list!=null)
    	{
    		if(!songplayed_list.contains(song_playlist.get(Integer.parseInt(positionofsong)).getSong_id()))
    		{
    			songplayed_list.add(song_playlist.get(Integer.parseInt(positionofsong)).getSong_id());
    		}
    	}
    	
    	SharedPreferences sharedpref=getSharedPreferences("KiandaStream", Context.MODE_PRIVATE);
    	if(sharedpref.getBoolean("REPEAT", false))
    	{
    		processPlayRequest(positionofsong);
    	}else
    	{
    		if(sharedpref.getBoolean("SHUFFLE", false))
        	{
    			System.out.println("i am in shuffle true ");
    				if(songplayed_list.size()<song_playlist.size())
    				{
    					positionofsong=""+checkshuffle();
    					mState=State.Starting;
    	    	        processPlayRequest(positionofsong);
    	    	        
    				}else
    				{
    					mState=State.Stopped;
        	    		relaxResources(true);
    				}
    			   
    	    	
    		}else
        	{
    			Integer pos=Integer.parseInt(positionofsong)+1;
    	    	System.out.println("(pos+1)<list.size()  "+(pos+1)+"@@@"+song_playlist.size());
    	    	if(pos<song_playlist.size())
    	    	{
    	    		positionofsong=pos.toString();
    	    		mState=State.Starting;
    	    		processPlayRequest(positionofsong);
    	    		
    	    	}else
    	    	{
    	    		if(listener!=null)
    	    			listener.playlistend();
    	    		mState=State.Stopped;
    	    		relaxResources(true);
    	    	}
        	}
    	}
    	
        // The media player finished playing the current song, so we go ahead and start the next.
       // playNextSong(null);
    }

    private int checkshuffle()
    {
    	int randomno=Integer.parseInt(positionofsong);
    	
		boolean is_getno=true;
		do
		{
			if(randInt(0, 1)==1)
			{
				if(Integer.parseInt(positionofsong)>1)
				{
					Integer ranno=randInt(0, randomno);
					if(!songplayed_list.contains(song_playlist.get(ranno).getSong_id()))
					{
						System.out.println("new song no "+ranno);
						return ranno;
						
					}else
					{
						randomno=ranno;
					}
				}else
				{
					Integer ranno=randInt(randomno, song_playlist.size()-1);
					if(!songplayed_list.contains(song_playlist.get(ranno).getSong_id()))
					{
						System.out.println("new song no "+ranno);
						return ranno;
					}else
					{
						randomno=ranno;
					}
				}
				
			}else
			{
				Integer ranno=randInt(Integer.parseInt(positionofsong), song_playlist.size()-1);
				if(!songplayed_list.contains(song_playlist.get(ranno).getSong_id()))
				{
					System.out.println("new song no "+ranno);
					return ranno;
				}else
				{
					randomno=ranno;
				}
			}
		}while(is_getno);
		return 0;
    }
    
    private int randInt(int min, int max) 
    {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
    /** Called when media player is done preparing. */
    public void onPrepared(MediaPlayer player) 
    {
    	// The media player is done preparing. That means we can start playing!
    	System.out.println("i am in onprepared method of service");
        mState = State.Playing;
        mPlayer.start();
        setNotification(song_playlist.get(Integer.parseInt(positionofsong)).getSong_name());
        //Setting Title in Notification bar
       // updateNotification(mSongTitle + " (playing)");
        configAndStartMediaPlayer();
        if(listener!=null)
    	{
    	listener.onPreparation(player);
    	listener.startUpdating(player);
    	}
        updatelistner.updatedata();
        
    }
    /**
     * Called when there's an error playing media. When this happens, the media player goes to
     * the Error state. We warn the user about the error and reset the media player.
     */
    public boolean onError(MediaPlayer mp, int what, int extra) 
    {
        Toast.makeText(getApplicationContext(), "Media player error! Resetting.",
            Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: what=" + String.valueOf(what) + ", extra=" + String.valueOf(extra));

        mState = State.Starting;
        relaxResources(true);
        listener.onError(mp, what, extra);
        return true; // true indicates we handled the error
    }

    public void onGainedAudioFocus() 
    {
        Toast.makeText(getApplicationContext(), "gained audio focus.", Toast.LENGTH_SHORT).show();
        mAudioFocus = AudioFocus.Focused;

        // restart media player with new focus settings
        if (mState == State.Playing)
            configAndStartMediaPlayer();
    }

    public void onLostAudioFocus(boolean canDuck) 
    {
        Toast.makeText(getApplicationContext(), "lost audio focus." + (canDuck ? "can duck" :
            "no duck"), Toast.LENGTH_SHORT).show();
        mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck : AudioFocus.NoFocusNoDuck;

        // start/restart/pause media player with new focus settings
        if (mPlayer != null && mPlayer.isPlaying())
            configAndStartMediaPlayer();
    }

    public void onMusicRetrieverPrepared() {
        // Done retrieving!
        mState = State.Stopped;

        // If the flag indicates we should start playing after retrieving, let's do that now.
        /*if (mStartPlayingAfterRetrieve) 
        {
            playNextSong(mWhatToPlayAfterRetrieve == null ?
                    null : mWhatToPlayAfterRetrieve.toString());
        }*/
    }


    @Override
    public void onDestroy() {
        // Service is being killed, so make sure we release our resources
        mState = State.Stopped;
        relaxResources(true);
       
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) 
	{
		if(listener!=null)
		{
			listener.updatebufferingstatus(mp, percent);
		}
		
	}
}
