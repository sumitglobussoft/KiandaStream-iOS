package com.kiandastream.twitter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;

public class TwitterAccessTokenPost {

	TwitterRequestCallBack twitterRequestCallBack;

	public TwitterAccessTokenPost(TwitterRequestCallBack twitterRequestCallBack) {

		this.twitterRequestCallBack = twitterRequestCallBack;

	}

	public void executeThisRequest(String oauthToken, String oauthVerifier) {

		new RequestAsync().execute(oauthToken,oauthVerifier);

	}

	public class RequestAsync extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String oauthToken, oauthVerifier;

			oauthToken = params[0];

			oauthVerifier = params[1];

			postForAccessToken(oauthToken, oauthVerifier);

			return null;
		}

	}

	public String postForAccessToken(String oauthToken, String oauthVerifier) {

		String response = null;

		try {

			// perams

			String urlTimeline = MainSingleTon.accessTokenPost
					+ "?oauth_verifier=" + oauthVerifier;

			// String urlTimeline = MainSingleTon.accessTokenPost ;

			String authData = getAuthDAta(urlTimeline, oauthToken,
					oauthVerifier);

			myprint("url : " + urlTimeline);

			// myprint("authData : " + authData);

			URL obj = new URL(urlTimeline);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("POST");

			con.addRequestProperty("Authorization", authData);

			con.addRequestProperty("Host", "api.twitter.com");

			con.addRequestProperty("User-Agent", "twtboardpro");

			con.addRequestProperty("Accept", "*/*");

			con.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			response = readResponse(con);

			myprint("jsonString response = " + response);

			String string = response;

			string = "[" + response + "]";

			if (response == null) {

				twitterRequestCallBack.onFailure(new Exception());

			} else {

				twitterRequestCallBack.onSuccess(response);
			}

		} catch (Exception e) {

			e.printStackTrace();

			twitterRequestCallBack.onFailure(new Exception());

			myprint("Exception = =    " + e);

		}

		return response;

	}

	// Reads a response for a given connection and returns it as a string.
	public String readResponse(HttpsURLConnection connection) {

		try {

			int responseCode = connection.getResponseCode();

			myprint("readResponse connection.getResponseCode()   "
					+ responseCode);

			String jsonString = null;

			if (responseCode == HttpURLConnection.HTTP_OK) {

				InputStream linkinStream = connection.getInputStream();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				int j = 0;

				while ((j = linkinStream.read()) != -1) {

					baos.write(j);

				}

				byte[] data = baos.toByteArray();

				jsonString = new String(data);

			}

			// myprint("readResponse jsonString   " + jsonString);

			return jsonString;

		} catch (IOException e) {

			// twitterRequestCallBack.onFailure(e);

			e.printStackTrace();

			myprint("readResponse IOExceptionException   " + e);

			return null;
		}

	}

	void myprint(Object msg) {

		// System.out.println(msg.toString());

	}

	private String getAuthDAta(String urlTimeline, String oauthToken,
			String oauthVerifier) {

		OAuthSignaturesGeneratorForAccessToken oAuthSignaturesGenerator = new OAuthSignaturesGeneratorForAccessToken(
				oauthToken, MainSingleTon.TWITTER_KEY,
				MainSingleTon.TWITTER_SECRET, "POST");

		oAuthSignaturesGenerator.setUrl(MainSingleTon.accessTokenPost);

		String GeneratedPerams = null;

		try {

			GeneratedPerams = "OAuth "
					+ oAuthSignaturesGenerator.OAUTH_CONSUMER_KEY
					+ "=\""
					+ URLEncoder.encode(oAuthSignaturesGenerator.getcKey(),
							"ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_NONCE
					+ "=\""
					+ URLEncoder.encode(oAuthSignaturesGenerator.currentOnonce,
							"ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_SIGNATURE
					+ "=\""
					+ URLEncoder.encode(
							oAuthSignaturesGenerator.getOauthSignature(),
							"ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_SIGNATURE_METHOD
					+ "=\""
					+ URLEncoder.encode(oAuthSignaturesGenerator.HMAC_SHA1,
							"ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_TIMESTAMP
					+ "=\""
					+ URLEncoder.encode(
							oAuthSignaturesGenerator.currentTimeStamp,
							"ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_TOKEN
					+ "=\""
					+ URLEncoder.encode(oauthToken, "ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_VERSION
					+ "=\""
					+ URLEncoder.encode(oAuthSignaturesGenerator.VERSION_1_0,
							"ISO-8859-1") + "\"";

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

			myprint("GeneratedPerams UnsupportedEncodingException " + e);

		}

		String authenticateString = GeneratedPerams;

		String authData = authenticateString;

		return authData;

	}

}
