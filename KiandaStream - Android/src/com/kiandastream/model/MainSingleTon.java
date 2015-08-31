package com.kiandastream.model;

import java.util.HashMap;

import android.media.MediaPlayer;

import com.facebook.AccessToken;

public class MainSingleTon 
{
	public static final String TWITTER_KEY = "EEUDJ9596kp6nu4ceeEbmXNZw";
	public static final String TWITTER_SECRET = "N948E7NW6KhcJOpoM8XAN4CxQIM8EZwfR8FCVZzIoD3Ot9ls3M";
	public static AccessToken dummyAccesstoken;
	public static String reqTokenResourceURL = "https://api.twitter.com/oauth/request_token";
	public static MediaPlayer mp;
	public static String oauth_callbackURL = "http://www.twtboardpro.com/";
	public static String Main_url = "http://api.kiandastream.globusapps.com/?";
	public static String oauthResourceURL = "https://api.twitter.com/oauth/authenticate";
	public static String signInRequestURL = "https://api.twitter.com/oauth/authenticate?oauth_token=";
	public static String accessTokenPost = "https://api.twitter.com/oauth/access_token";
	public static String artistimageurl="http://kiandastream.globusapps.com//static/artists/";
	public static String songurl="http://104.199.130.203/";
	public static String playlistimageurl="http://kiandastream.globusapps.com/static/playlists/";
	public static String albumimageurl="http://kiandastream.globusapps.com/static/albums/";
	public static String userid;
	public static String username;
	public static String mainusername;
	public static String accesstokem;
	public static String selectgenre;
	public static boolean isplaysong=false;
	public static String song_position;
	public static AlbumModel album_model;
	public static ArtistModel artist_model;
	public static String previous_fragment="";
	public static String current_fragment="";
	public static String otpmail="";
	public static String otpemail="";
	public static String finalotp="";
	public static HashMap<String, PlayingSongListmodel> localsong=new HashMap<String, PlayingSongListmodel>();
}
