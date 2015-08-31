package com.kiandastream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kiandastream.model.MainSingleTon;
import com.kiandastream.utils.KiandaCallBack;
import com.kiandastream.utils.KiandaGetRequest;
import com.kiandastream.utils.MyCustomProgressDialog;

public class SignUpActivity extends Activity {

	EditText username,email,password,retypepass;
	ImageView login,cancelsignup;
	TextView signin;
	ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_sign_up);
		username=(EditText)findViewById(R.id.username);
		email=(EditText)findViewById(R.id.email);
		password=(EditText)findViewById(R.id.password);
		login=(ImageView)findViewById(R.id.login);
		retypepass = (EditText) findViewById(R.id.confirmpassword);
		
		cancelsignup = (ImageView) findViewById(R.id.cancelregisterbtn);
		signin = (TextView) findViewById(R.id.signuplogin);
	
		login.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(!username.getText().toString().trim().isEmpty())
				{
					if(!email.getText().toString().trim().isEmpty())
					{
						if(!password.getText().toString().trim().isEmpty()&&password.getText().toString().trim().length()>=6)
						{
							if(!retypepass.getText().toString().trim().isEmpty()){
								if(retypepass.getText().toString().trim().equals(password.getText().toString().trim())){
									String[] parmeter={username.getText().toString().trim(),email.getText().toString().trim(),password.getText().toString().trim()};
									Signup(parmeter);
								}else{
									retypepass.setError("Password doesn't match!!");
								}
							}else{
								retypepass.setError("Re-type password cannot be empty");
							}
						}else if(password.getText().toString().trim().length()<6){
							password.setError("Password too short!!");
						}else if(password.getText().toString().trim().isEmpty()){
							password.setError("Please Enter Password");
						}
					}else{
						email.setError("Please Enter Email");
					}
				}else{
					username.setError("Please Enter Username");
				}
			}
		});
		
		cancelsignup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SignUpActivity.this.finish();
			}
		});
		signin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent in=new Intent(SignUpActivity.this,SigninActivity.class);
				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(in);
				finish();
			}
		});
	}
	
	private void Signup(String[] params){
		final String username,password;
		
		mProgressDialog = new MyCustomProgressDialog(SignUpActivity.this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		
		username=params[0];
		password=params[2];
		String parameter=MainSingleTon.Main_url+"t=createAccount&username="+username+"&email="+params[1]+"&password="+params[2];
		
		KiandaGetRequest getRequest=new KiandaGetRequest(SignUpActivity.this);
		getRequest.executeRequest(parameter, new KiandaCallBack() {
			
			@Override
			public void onSuccess(JSONObject result) {
				JSONObject object;
				try {
					object = result;
					
					if(object.getInt("status_code")==200)//Login Successful
					{
						JSONObject jsonObject=object.getJSONObject("user");
						MainSingleTon.userid=jsonObject.getString("user_id");
						MainSingleTon.username=jsonObject.getString("name");
						MainSingleTon.mainusername=jsonObject.getString("username");
						MainSingleTon.accesstokem=jsonObject.getString("access_token");
						
						saveData("TYPE", "1");
						saveData("USERNAME", username);
						saveData("PASSWORD", password);
						
						Intent in=new Intent(SignUpActivity.this,HomeActivity.class);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(in);
						mProgressDialog.dismiss();
					}
					else if(object.getInt("status_code")==201)//Login Unsucessful, email already in use
					{
						mProgressDialog.dismiss();
						Toast.makeText(SignUpActivity.this, "Email already in use !!",Toast.LENGTH_SHORT).show();
						
					}else if(object.getInt("status_code")==400)//Username contains special characters
					{
						
						mProgressDialog.dismiss();
						Toast.makeText(SignUpActivity.this, "Username shouldn't contain special word, or symbol",Toast.LENGTH_SHORT).show();
					
					}else if(object.getInt("status_code")==202)//Username already used
					{
						
						mProgressDialog.dismiss();
						Toast.makeText(SignUpActivity.this, "Username already in use !!",Toast.LENGTH_SHORT).show();
						
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
	
	void saveData(String key,String value)
	{
		SharedPreferences sh=getSharedPreferences("KIANDASTREAM",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sh.edit();
		editor.putString(key, value);
		editor.commit();
	}
}
