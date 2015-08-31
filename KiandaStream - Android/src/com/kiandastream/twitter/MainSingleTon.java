package com.kiandastream.twitter;

public class MainSingleTon {

	public static TwtSocioUserDatas currentUserModel;

	// + + + + + + + + + + URLS + + + + + + + + + + + + + + + TwtBoardPro

	public static String twitterUrls = "";

	public static String keyHash, bearerToken;

	public static String TWITTER_KEY;

	public static String TWITTER_SECRET;

	public static String oauth_callbackURL;

	public static String reqTokenResourceURL = "https://api.twitter.com/oauth/request_token";

	public static boolean signedInStatus = false;

	public static boolean appInitialized = false;

	public static String oauthResourceURL = "https://api.twitter.com/oauth/authenticate";

	public static String signInRequestURL = "https://api.twitter.com/oauth/authenticate?oauth_token=";

	public static String accessTokenPost = "https://api.twitter.com/oauth/access_token";

	public static String userAccountData = "https://api.twitter.com/1.1/users/show.json";

	public static String userShowIds = "https://api.twitter.com/1.1/users/lookup.json";

	public static String userTimeLine = "https://api.twitter.com/1.1/statuses/home_timeline.json";

	public static String users_following_to_me = "https://api.twitter.com/1.1/followers/list.json";

	public static String users_following_to_me_Ids = "https://api.twitter.com/1.1/followers/ids.json";

	public static String i_am_following_to = "https://api.twitter.com/1.1/friends/list.json";

	public static String i_am_following_to_ids = "https://api.twitter.com/1.1/friends/ids.json";

	public static String followUrl = "https://api.twitter.com/1.1/friendships/create.json";

	public static String unFollowUrl = "https://api.twitter.com/1.1/friendships/destroy.json";

	public static String updateTweet = "https://api.twitter.com/1.1/statuses/update.json";

	public static String userSearch = "https://api.twitter.com/1.1/users/search.json";

	public static String twtFavourites = "https://api.twitter.com/1.1/favorites/list.json";

	public static String reTweeting = "https://api.twitter.com/1.1/statuses/retweet/";

	public static String favouritesCreate = "https://api.twitter.com/1.1/favorites/create.json";

	public static String favouritesDestroy = "https://api.twitter.com/1.1/favorites/destroy.json";

	public static String tweetsSearch = "https://api.twitter.com/1.1/search/tweets.json";

	public static String update_with_media = "https://api.twitter.com/1.1/statuses/update_with_media.json";

	public static String uploadMedia = "https://upload.twitter.com/1.1/media/upload.json";

	public static String createMessage = "https://api.twitter.com/1.1/direct_messages/new.json";

}
