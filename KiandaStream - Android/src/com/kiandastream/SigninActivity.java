package com.kiandastream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.twitter.TwtSocioCallBack;
import com.kiandastream.twitter.TwtSocioInitialize;
import com.kiandastream.twitter.TwtSocioLoginDialog;
import com.kiandastream.twitter.TwtSocioUserDatas;
import com.kiandastream.utils.KiandaCallBack;
import com.kiandastream.utils.KiandaGetRequest;
import com.kiandastream.utils.MyCustomProgressDialog;
import com.kiandastream.utils.ServiceHandler;
import com.kiandastream.utils.Utils;


public class SigninActivity extends Activity 
{
	CallbackManager callbackManager;
	ImageView fblogin,twitterlogin,login,closeapp;
	Profile profile;
	
	public String requestAccessToken_twitter, requestAccessSecret_twitter;
	AccessToken myAccessToken;
	ProfileTracker profileTracker;
	String extendedAccessToken =null;
	boolean callBackConfirm_twitter = false;
	Dialog webDialog,fbdialog;
	EditText username,password;
	WebView webView;
	TextView forgotpass,signup;
	ProgressDialog mProgressDialog,progressdialog;
	private Effectstype effect;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		FacebookSdk.sdkInitialize(SigninActivity.this.getApplicationContext()); //Initialize Facebook SDK  
		setContentView(R.layout.activity_signin);
		
		fblogin=(ImageView)findViewById(R.id.fblogin);
		twitterlogin=(ImageView)findViewById(R.id.twitterlogin);
		login=(ImageView)findViewById(R.id.login);
		forgotpass= (TextView)findViewById(R.id.forgotpasswrdtext);
		username=(EditText)findViewById(R.id.email);
		password=(EditText)findViewById(R.id.password);
		
		username.setText("manishbannur@globussoft.com");
		password.setText("76419c58730d9f35de7ac538c2fd6737");
		
		closeapp = (ImageView) findViewById(R.id.cancelbtn);
		signup = (TextView) findViewById(R.id.signuplogin);
		PackageInfo info;

		try {
			info = getPackageManager().getPackageInfo("com.kiandastream",PackageManager.GET_SIGNATURES);

			for (Signature signature : info.signatures) {

				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String something = new String(Base64.encode(md.digest(), 0));
				Log.e("hash key", something);
				System.out.println("keyy hashhhh "+something);
			}

		}
		catch (NameNotFoundException e1) 
		{
			Log.e("name not found", e1.toString());
		} 
		catch (NoSuchAlgorithmException e) 
		{
			Log.e("no such an algorithm", e.toString());
		}
		catch (Exception e) 
		{
			Log.e("exception", e.toString());
		}
		TwtSocioInitialize.initialize("S3Sjzd7ETMMTBXQfo4jPVdh8i", "0Km3iVLIt3l9LaQNAMRrEfexd7KTDYlaBBUNmK2zIRMdYsX2SZ",
			    "http://groupinion.com/");
		login.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) 
			{
				if(!username.getText().toString().trim().isEmpty())
				{
					if(!password.getText().toString().trim().isEmpty())
					{
						String[] parmeter={username.getText().toString().trim(),password.getText().toString().trim()};
						Login(parmeter);
					}else{	
						password.setError("Please Enter Password");
					}
				}else{	
					username.setError("Please Enter Username");
				}
				
			}
		});
		fblogin.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				fblogin.setEnabled(false);
				LoginWithFB();
				
			}
		});
		
		signup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent in=new Intent(SigninActivity.this,SignUpActivity.class);
				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(in);
				finish();
			}
		});
		
		forgotpass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(username.getText().toString().length()>0){
					String email = username.getText().toString();
					if(Utils.isValidEmail(email)){
						final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(SigninActivity.this);
						 effect=Effectstype.RotateBottom;
						 dialogBuilder
			                .withTitle("Reset Password")                                
			                .withTitleColor("#000000")                                  
			                .withDividerColor("#11000000")                              
			                .withMessage("We will send an otp to "+username.getText().toString())    
			                .withMessageColor("#FF120009")                              
			                .withDialogColor("#FFFFFFFF")                               
			                .withIcon(getResources().getDrawable(R.drawable.ic_launcher))
			                .isCancelableOnTouchOutside(true)                           
			                .withDuration(700)                                          
			                .withEffect(effect)                                         
			                .withButton1Text("OK")                                      
			                .withButton2Text("Cancel")                                  
			                .setCustomView(R.layout.dialog_view,v.getContext())         
			                .setButton1Click(new View.OnClickListener() {
			                    @Override
			                    public void onClick(View v) {
			                    	
			                    	dialogBuilder.cancel();
			                    	forgotPass(username.getText().toString());
			                    }
			                })
			                .setButton2Click(new View.OnClickListener() {
			                    @Override
			                    public void onClick(View v) {
			                    	dialogBuilder.cancel();
			                    }
			                })
			                .show();
					}else{
						Toast.makeText(SigninActivity.this, "Enter Valid Email-Id", Toast.LENGTH_SHORT).show();
					}
					 
				}else{
					Toast.makeText(SigninActivity.this, "Enter Email-Id", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		closeapp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SigninActivity.this.finish();
			}
		});
		
		twitterlogin.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				TwtSocioLoginDialog twtlogin=new TwtSocioLoginDialog(SigninActivity.this, new TwtSocioCallBack() 
				{
					
					@Override
					public void onSuccess(TwtSocioUserDatas twtSocioUserDatas) 
					{
						System.out.println("@@@@@@@@@@@@@@@@@@  "+twtSocioUserDatas);
						twtSocioUserDatas.getUsername();
						twtSocioUserDatas.getUserid();
						
						
					}
					
					@Override
					public void onFailure(Exception exception) {
						
					}
				});
				twtlogin.startLogin();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}
	
	private void Login(String[] url)
	{
		final String username;
		final String password;
		
		mProgressDialog = new MyCustomProgressDialog(SigninActivity.this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		
		username=url[0];
		password=url[1];
		String paramater=MainSingleTon.Main_url+"t=getSignonToken&username="+username+"&password="+password;
		System.out.println("login url "+paramater);
		
		KiandaGetRequest getRequest=new KiandaGetRequest(SigninActivity.this);
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
						saveData("TYPE", "1");
						saveData("USERID",MainSingleTon.userid);
						saveData("USERNAME", username);
						saveData("PASSWORD", password);
						Intent in=new Intent(SigninActivity.this,HomeActivity.class);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(in);
						mProgressDialog.dismiss();
						finish();
					}else 
						if(object.getInt("status_code")==201)
						{
							mProgressDialog.dismiss();
							login.setEnabled(true);
							Toast.makeText(getApplicationContext(), "Invalid Username ,Password", Toast.LENGTH_LONG).show();
						}
						else if(object.getInt("status_code")==205)
						{
							mProgressDialog.dismiss();
							login.setEnabled(true);
							Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_LONG).show();
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
	
	private void forgotPass(final String email){
		
		progressdialog = new MyCustomProgressDialog(SigninActivity.this);
		progressdialog.setCancelable(false);
		progressdialog.show();
		
		KiandaGetRequest getRequest=new KiandaGetRequest(SigninActivity.this);
		
		String paramater=MainSingleTon.Main_url+"t=forgetpassword&email="+email;
		System.out.println("Param "+paramater);
		getRequest.executeRequest(paramater, new KiandaCallBack() {
			@Override
			public void onSuccess(JSONObject result) {
				
				System.out.println("Result to mail = "+result.toString());
					try {
						JSONObject object=result;
						if(object.getInt("status_code")==200){
							
							MainSingleTon.otpmail=object.getString("data");
							MainSingleTon.otpemail=email;
							progressdialog.cancel();
							Intent intent=new Intent(SigninActivity.this, EnterOTPActivity.class);
							startActivity(intent);
							finish();
							
						}else if(object.getInt("status_code")==198){
							
							progressdialog.cancel();
							Toast.makeText(SigninActivity.this, "Email Id does not exist!!", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						progressdialog.cancel();
						Toast.makeText(SigninActivity.this, "Exception in service!!", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
			}
			
			@Override
			public void onFailure(Exception exception) {
				progressdialog.cancel();
				exception.printStackTrace();
				Toast.makeText(SigninActivity.this, "Response Error!!", Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	private void LoginWithFB()
	{
		callbackManager = CallbackManager.Factory.create();

		LoginManager.getInstance().setLoginBehavior(LoginBehavior.SSO_WITH_FALLBACK);

		LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));

		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>(){

			@Override
			public void onSuccess(LoginResult loginResult){
			   
				//connectFacebook.setPublishPermissions(Arrays.asList("publish_actions"));
				fblogin.setEnabled(true);
				System.out.println("777777777");
				 myAccessToken = loginResult.getAccessToken();

				Profile.fetchProfileForCurrentAccessToken();

				MainSingleTon.dummyAccesstoken = myAccessToken;

				System.out.println("Inside the success="+ loginResult.getAccessToken().getToken());
				System.out.println("Inside the success="+ loginResult.getAccessToken().getUserId());
				System.out.println("Inside the success="+ loginResult.getRecentlyGrantedPermissions()); 
				final String fbid=loginResult.getAccessToken().getUserId();
				
					System.out.println("Insdie tht profileTracker1D");
					GraphRequest request = GraphRequest.newMeRequest(myAccessToken,
					new GraphRequest.GraphJSONObjectCallback() {
						@Override
						public void onCompleted(JSONObject object,	GraphResponse response) {
							try {
								String currentUserEmailId="";
								if(object!=null)
								{
								final String name=object.getString("name");
								System.out.println("name get from faceboo "+name);
								if( object.has("email"))
								{
									 currentUserEmailId = object.getString("email");
									 
									System.out.println("@@@@@@@@@@@@email111 " + currentUserEmailId);
									
								}
								 
								FB_Login(fbid,name,currentUserEmailId);
								
								}
								//new GetExtendedAccessTokenFacebook().execute();
							} catch (JSONException e) {

								e.printStackTrace();
							}  
						}

					});

					request.executeAsync();

					//Graph request to get the user profile data like user email, name etc
					/*GraphRequest.newMyFriendsRequest(myAccessToken, new GraphJSONArrayCallback() {

						@Override
						public void onCompleted(JSONArray objects, GraphResponse response) {

							System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$="+response);
						}
					}).executeAsync();*/

				

			}

			@Override
			public void onError(FacebookException error)
			{
				AccessToken.setCurrentAccessToken(null);
				fblogin.setEnabled(true);

			}

			@Override
			public void onCancel()
			{
				AccessToken.setCurrentAccessToken(null);
				fblogin.setEnabled(true);


			}
				});
		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

				if(profileTracker.isTracking())
				{
					LoginManager.getInstance().logInWithPublishPermissions(SigninActivity.this, Arrays.asList("publish_actions"));

					System.out.println("sssssss&*&*&*&*&*&*&*&*&*&*&*&*&*&8787878");

				}else
				{
					System.out.println("&*&*&*&*&*&*&*&*&*&*&*&*&*&8787878");
				}
			}
		};

	}
	
	@Override
	protected void onResume() {

		super.onResume();

		System.out.println("Insdie tht ONRRRRESSSUME");

		//FB profile tracker to get the user current and old profile data
		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

				if(profileTracker.isTracking())
				{

					System.out.println("currentProfilgetName =-------------------------------------"
							+ currentProfile.getName());
					System.out.println("getProfilePictureUri ="
							+ currentProfile.getProfilePictureUri(
									240, 260));
					System.out.println("oldProfile =" + oldProfile);

					profile = currentProfile;

					
				}else
				{
					System.out.println("TRACKER STAOPED");
				}
			}
		};
	}
	
	private void FB_Login(String fbid,String name,String email){
		final String username,useremail,id;
		progressdialog = new MyCustomProgressDialog(SigninActivity.this);
		progressdialog.setCancelable(false);
		progressdialog.show();
		id=fbid;
		username=name;
		useremail=email;
		String parameter=MainSingleTon.Main_url+"t=facebooklogin&fb_id="+id;
		System.out.println("url for fb login  "+parameter);
		
		KiandaGetRequest getRequest=new KiandaGetRequest(this);
		getRequest.executeRequest(parameter, new KiandaCallBack() {
			
			@Override
			public void onSuccess(JSONObject result) {
				try {
					JSONObject object=result;
					if(object.getInt("status_code")==200)//fb login successful
					{
						System.out.println("inside 200 success");
						progressdialog.dismiss();
						JSONObject userdata=object.getJSONObject("user");
						MainSingleTon.userid=userdata.getString("user_id");
						MainSingleTon.username=userdata.getString("name");
						MainSingleTon.accesstokem=userdata.getString("access_token");
						saveData("TYPE", "2");
						saveData("USERID",MainSingleTon.userid);
						saveData("FB_ID", id);
						Intent in=new Intent(SigninActivity.this,HomeActivity.class);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(in);
						finish();
						System.out.println("Login Success@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   ");
					}else
						if(object.getInt("status_code")==201)//user not exist need to signup
						{
							//String[] params ={username,email,password,name,id};
							 progressdialog.dismiss();
							 fbdialog = new Dialog(SigninActivity.this, android.R.style.Theme_Translucent);
							 fbdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

							 fbdialog.setCancelable(false);
							 fbdialog.setContentView(R.layout.fbdialog);
							 
							 ImageView fbdone;
							 final EditText fbusernaem=(EditText)fbdialog.findViewById(R.id.username);
							 final EditText fbemail=(EditText)fbdialog.findViewById(R.id.email);
							 final EditText fbpassword=(EditText)fbdialog.findViewById(R.id.password);
							 fbemail.setText(useremail);
							 fbdone = (ImageView)fbdialog.findViewById(R.id.fbdone);
							 fbdone.setOnClickListener(new OnClickListener() 
								{
									
									@Override
									public void onClick(View v) 
									{
										if(!fbusernaem.getText().toString().trim().isEmpty())
										{
											if(!fbemail.getText().toString().trim().isEmpty())
											{
												if(!fbpassword.getText().toString().trim().isEmpty())
												{
													String[] parmeter={fbusernaem.getText().toString().trim(),fbemail.getText().toString().trim(),fbpassword.getText().toString().trim(),id,username};
													fbdialog.dismiss();
													FB_SignUp(parmeter);
												}else
												{
													Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
												}
											}else
											{
												Toast.makeText(getApplicationContext(), "Please Enter Email", Toast.LENGTH_LONG).show();
											}
										}else
										{
											Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_LONG).show();
										}
										
									}
								});   
							 fbdialog.show();
							
						}else
							if(object.getInt("status_code")==202)//fb login Unsuccessful username already taken
							{
								Toast.makeText(getApplicationContext(), "Username already registered", Toast.LENGTH_LONG).show();
							}
				} catch (JSONException e) {
					progressdialog.dismiss();
					Toast.makeText(getApplicationContext(), "Error in login Please Try again", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onFailure(Exception exception) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	private void FB_SignUp(String[] params){
		final String fbid;
		progressdialog = new MyCustomProgressDialog(SigninActivity.this);
		progressdialog.setCancelable(false);
		// Show progressdialog
		progressdialog.show();
	
		fbid=params[4];
		String parameter=MainSingleTon.Main_url+"t=facebooksignup&username="+params[0]+"&email="+params[1]+"&password="+params[2]+"&name="+params[3]
				+"&fb_id="+fbid;

		KiandaGetRequest getRequest=new KiandaGetRequest(this);
		getRequest.executeRequest(parameter,new KiandaCallBack() {
			@Override
			public void onSuccess(JSONObject result) {
				progressdialog.dismiss();
				
				try {
					JSONObject object=result;
					if(object.getInt("status_code")==200)//fb login successful
					{
						
						System.out.println("Login Success");
						saveData("TYPE", "2");
						saveData("USERID",MainSingleTon.userid);
						saveData("FB_ID", fbid);
						
					}else
						if(object.getInt("status_code")==201)//fb login Unsuccessful email already registered
						{
							Toast.makeText(getApplicationContext(), "Email already registered", Toast.LENGTH_LONG).show();
						}else
							if(object.getInt("status_code")==202)//fb login Unsuccessful username already taken
							{
								Toast.makeText(getApplicationContext(), "Username already registered", Toast.LENGTH_LONG).show();
							}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
			@Override
			public void onFailure(Exception exception) {
				
			}
		});
	}
		
		void saveData(String key,String value)
		{
			SharedPreferences sh=getSharedPreferences("KIANDASTREAM",Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sh.edit();
			editor.putString(key, value);
			editor.commit();
		}
	
		//twitter login 	
	class TwitterLogin extends AsyncTask<String, Void, String>
	{
		String twtid;
		@Override
		protected String doInBackground(String... params) 
		{
			twtid=params[0];
			String parameter=MainSingleTon.Main_url+"t=twitterlogin&twt_id="+twtid;
			ServiceHandler sh=new ServiceHandler();
			String result=sh.makeServiceCall(parameter, ServiceHandler.GET);
			return result;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			if(result!=null)
			{
				JSONObject object;
				try {
					object = new JSONObject(result);
					if(object.getInt("status_code")==200)
					{
						JSONObject userdata=object.getJSONObject("user");
						MainSingleTon.userid=userdata.getString("user_id");
						MainSingleTon.username=userdata.getString("name");
						MainSingleTon.accesstokem=userdata.getString("access_token");
						saveData("TYPE", "3");
						saveData("USERID",MainSingleTon.userid);
						saveData("TWT_ID", twtid);
						Intent in=new Intent(SigninActivity.this,HomeActivity.class);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(in);
						finish();
					}else
						if(object.getInt("status_code")==201)//user not exist need to signup
						{
							//String[] params ={username,email,password,name,id};
							
							 progressdialog.dismiss();
							 fbdialog = new Dialog(SigninActivity.this, android.R.style.Theme_Translucent);
							 fbdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

							 fbdialog.setCancelable(false);
							 fbdialog.setContentView(R.layout.fbdialog);
							 
							 ImageView fbdone;
							 final EditText fbusernaem=(EditText)fbdialog.findViewById(R.id.username);
							 final EditText fbemail=(EditText)fbdialog.findViewById(R.id.email);
							 final EditText fbpassword=(EditText)fbdialog.findViewById(R.id.password);
							// fbemail.setText(email);
							 fbdone = (ImageView)fbdialog.findViewById(R.id.fbdone);
							 fbdone.setOnClickListener(new OnClickListener() 
								{
									
									@Override
									public void onClick(View v) 
									{
										if(!fbusernaem.getText().toString().trim().isEmpty())
										{
											if(!fbemail.getText().toString().trim().isEmpty())
											{
												if(!fbpassword.getText().toString().trim().isEmpty())
												{
													String[] parmeter={fbusernaem.getText().toString().trim(),fbemail.getText().toString().trim(),fbpassword.getText().toString().trim(),twtid};
													fbdialog.dismiss();
													FB_SignUp(parmeter);
												}else
												{
													Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
												}
											}else
											{
												Toast.makeText(getApplicationContext(), "Please Enter Email", Toast.LENGTH_LONG).show();
											}
										}else
										{
											Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_LONG).show();
										}
										
									}
								});   
							 fbdialog.show();
							
						}else
							if(object.getInt("status_code")==202)//fb login Unsuccessful username already taken
							{
								Toast.makeText(getApplicationContext(), "Username already registered", Toast.LENGTH_LONG).show();
							}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			super.onPostExecute(result);
		}
	}
	class TwitterSignUp extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) 
		{
			String parameter=MainSingleTon.Main_url+"";
			ServiceHandler sh=new ServiceHandler();
			String result=sh.makeServiceCall(parameter, ServiceHandler.GET);
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			if(result!=null)
			{
				JSONObject object;
				try {
					object = new JSONObject(result);
					if(object.getInt("status_code")==200)
					{
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			super.onPostExecute(result);
		}
	}
	
}
