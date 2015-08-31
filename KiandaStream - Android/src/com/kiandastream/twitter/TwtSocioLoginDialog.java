package com.kiandastream.twitter;

import java.net.URLEncoder;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.kiandastream.R;


public class TwtSocioLoginDialog {

	Context context;
	public String requestAccessToken, requestAccessSecret;
	Dialog webDialog;
	WebView webView;
	ProgressBar webViewProgress;
	TwtSocioCallBack twtSocioCallBack;
	TwtSocioUserDatas currentUserModel = new TwtSocioUserDatas();
	Handler handler = new Handler();
	private boolean callBackConfirm;

	public TwtSocioLoginDialog(Context context, TwtSocioCallBack twtSocioCallBack) {

		this.context = context;

		this.twtSocioCallBack = twtSocioCallBack;

		CookieSyncManager.createInstance(context);

		CookieManager cookieManager = CookieManager.getInstance();

		cookieManager.removeAllCookie();

	}

	public void startLogin() {

		if (MainSingleTon.appInitialized) {

			twtSocioCallBack.onFailure(new Exception(
					"Application details Not Intialized Properly!!"));

		} else {

			initDialog();

		}
	}

	void initDialog() {

		webDialog = new Dialog(context);

		webDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		webDialog.setCancelable(true);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

		Window window = webDialog.getWindow();

		lp.copyFrom(window.getAttributes());

		// This makes the dialog take up the full width

		lp.width = WindowManager.LayoutParams.MATCH_PARENT;

		lp.height = WindowManager.LayoutParams.MATCH_PARENT;

		window.setAttributes(lp);

		webDialog.setContentView(R.layout.twtsociologindialog);

		webDialog.setCancelable(true);

		webViewProgress = (ProgressBar) webDialog.findViewById(R.id.progressBar1);

		webView = (WebView) webDialog.findViewById(R.id.dialogue_web_view);

		webView.setWebViewClient(new MyWebClient());

		webView.setVerticalScrollBarEnabled(false);

		webView.setHorizontalScrollBarEnabled(false);

		webView.getSettings().setJavaScriptEnabled(true);

		webDialog.show();

		TwitterSignIn twitterSignIn = new TwitterSignIn(
				new TwitterRequestCallBack() {

					@Override
					public void onSuccess(JSONObject jsonObject) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(final String jsonResult) {

						// TODO Auto-generated method stub
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								extractBaseString(jsonResult);

							}
						});

						myprint("onSuccess TwitterSignIn *******************");

					}

					@Override
					public void onFailure(Exception e) {

						myprint("**************************************failure in TwitterSignIn********************");

						twtSocioCallBack.onFailure(new Exception());

					}
				});

		twitterSignIn.executeThisRequest();

	}

	void extractBaseString(String baseString) {

		// Token
		int startInd = baseString.indexOf("=") + 1, endInd = baseString
				.indexOf("&");

		requestAccessToken = baseString.substring(startInd, endInd);

		myprint("requestAccessToken " + requestAccessToken);

		// Secret
		String tmp = baseString.substring(endInd + 2);

		startInd = tmp.indexOf("=") + 1;

		endInd = tmp.indexOf("&");

		requestAccessSecret = tmp.substring(startInd, endInd);

		myprint("requestAccessSecret " + requestAccessSecret);

		callBackConfirm = baseString.contains("=true");

		myprint("callBackConfirm " + callBackConfirm);

		loadWebView();

	}

	void loadWebView() {

		String webLoadSignInUrl = MainSingleTon.signInRequestURL
				+ URLEncoder.encode(requestAccessToken);

		webView.loadUrl(webLoadSignInUrl);

		myprint("webLoadSignInUrl " + webLoadSignInUrl);

	}

	void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	class MyWebClient extends WebViewClient {

		private String TAG = "tag";

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			Log.d(TAG, "Redirecting URL " + url);

			if (url.startsWith(MainSingleTon.oauth_callbackURL)) {

				myprint("final response to get tokens " + url);

				String url1 = url.replace(
						MainSingleTon.oauth_callbackURL + "?", "");

				String[] tokenarray = url1.split("&");

				if (tokenarray.length == 0) {

					webDialog.dismiss();

				}

				String[] oauthtokenrray = tokenarray[0].split("=");

				String[] oauthverifier = tokenarray[1].split("=");

				System.out.println("@@@@@@@@@@@@@   " + oauthtokenrray[1]
						+ "++++++++++++  " + oauthverifier[1]);

				TwitterAccessTokenPost twitterAccessTokenPost = new TwitterAccessTokenPost(
						new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onSuccess(String jsonResult) {
								// TODO Auto-generated method stub

								extractAccesTokenSecret(jsonResult);
								handler.post(new Runnable() {

									@Override
									public void run() {

										webView.destroy();

										webDialog.dismiss();

									}
								});

							}

							@Override
							public void onFailure(Exception e) {

								// TODO Auto-generated method stub

								twtSocioCallBack.onFailure(new Exception());

								handler.post(new Runnable() {

									@Override
									public void run() {

										webView.destroy();

										webDialog.dismiss();

									}
								});
							}
						});

				twitterAccessTokenPost.executeThisRequest(oauthtokenrray[1],
						oauthverifier[1]);

				webView.loadUrl("about:blank");

				webViewProgress.setVisibility(View.VISIBLE);

			}

			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			Log.d(TAG, "Page error: " + description);

			super.onReceivedError(view, errorCode, description, failingUrl);

			webViewProgress.setVisibility(View.INVISIBLE);

			myprint("onReceivedError errorCode  " + errorCode);
			myprint("description description " + description);
			myprint("description failingUrl " + failingUrl);
			twtSocioCallBack.onFailure(new Exception());
			webDialog.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "Loading URL: " + url);

			super.onPageStarted(view, url, favicon);

			myprint("onPageStarted favicon " + favicon);

			webViewProgress.setVisibility(View.VISIBLE);

			if (url.startsWith("https://twitter.com/login/error?")) {

				new AlertDialog.Builder(context)
						.setTitle("SignIn failed!")
						.setMessage(
								"The username and password you entered did not match our records. Please double-check and try again.")
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										twtSocioCallBack
												.onFailure(new Exception());

										webDialog.dismiss();
									}
								}).setIcon(android.R.drawable.ic_dialog_alert)
						.show().setCancelable(false);

			} else {

			}

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			Log.d(TAG, "onPageFinished URL: " + url);

			webViewProgress.setVisibility(View.INVISIBLE);

			myprint("onPageFinished title " + view.getTitle());

		}

	}

	void extractAccesTokenSecret(String baseString) {

		currentUserModel = new TwtSocioUserDatas();

		// ..................................................

		String[] array1 = baseString.split("&");

		String[] arrayaccessToken = array1[0].split("=");
		String[] arrayTokenSecret = array1[1].split("=");
		String[] arrayUserID = array1[2].split("=");
		String[] arrayScreenName = array1[3].split("=");

		currentUserModel.setUserAcessToken(arrayaccessToken[1]);

		currentUserModel.setUsersecretKey(arrayTokenSecret[1]);

		currentUserModel.setUserid(arrayUserID[1]);

		currentUserModel.setUsername(arrayScreenName[1]);

		// .................................................

		myprint(currentUserModel);

		twtSocioCallBack.onSuccess(currentUserModel);

		myprint("Suceesssssssssssssssssss");

	}

}
