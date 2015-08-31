package com.kiandastream.twitter;


public class TwtSocioInitialize {

	public static void initialize(String TWITTER_KEY, String TWITTER_SECRET,
			String oauth_callbackURL) {

		MainSingleTon.TWITTER_KEY = TWITTER_KEY;

		MainSingleTon.TWITTER_SECRET = TWITTER_SECRET;

		MainSingleTon.oauth_callbackURL = oauth_callbackURL;

	}
}
