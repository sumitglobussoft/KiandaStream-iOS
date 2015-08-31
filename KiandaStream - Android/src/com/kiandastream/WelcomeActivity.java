package com.kiandastream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {

	ImageView signin,signup;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_welcome);
		signup=(ImageView)findViewById(R.id.signup);
		signin=(ImageView)findViewById(R.id.login);
		signup.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				Intent in=new Intent(WelcomeActivity.this,SignUpActivity.class);
				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(in);
				finish();
				
			}
		});
		signin.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				Intent in=new Intent(WelcomeActivity.this,SigninActivity.class);
				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(in);
				finish();
				
			}
		});
	}
}
