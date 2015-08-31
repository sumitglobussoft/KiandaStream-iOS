package com.kiandastream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kiandastream.model.MainSingleTon;
import com.kiandastream.utils.KiandaCallBack;
import com.kiandastream.utils.KiandaGetRequest;
import com.kiandastream.utils.MyCustomProgressDialog;

public class ResetPassword extends Activity {

	EditText password;
	Button submit;
	ProgressDialog progressdialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.resetpassword);
		
		password = (EditText) findViewById(R.id.resetpasswordet);
		submit = (Button) findViewById(R.id.donebutton);
		
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!password.getText().toString().trim().isEmpty()){
					ChangePassword(password.getText().toString().trim());
				}
			}
		});
		
	}
	
	private void ChangePassword(final String password){
		
		progressdialog = new MyCustomProgressDialog(ResetPassword.this);
		progressdialog.setCancelable(false);
		progressdialog.show();
		
		KiandaGetRequest getRequest=new KiandaGetRequest(ResetPassword.this);
		
		// t=changepassword&email=vinidubey@globussoft.com&finaloptcode=E1C7XL28OJZKS95H
							//&password=2097ce5207f9ec54f6652d197dba8238b6
		
		String parameter = MainSingleTon.Main_url+"t=changepassword&email="+MainSingleTon.otpemail
				+"&finaloptcode="+MainSingleTon.finalotp+"&password="+password;
		
		System.out.println("resetpass == "+parameter);
		getRequest.executeRequest(parameter, new KiandaCallBack() {
		
			@Override
			public void onSuccess(JSONObject result) {
				
				//System.out.println("result ");
				try {
					JSONObject jsonObject=result;
					
					if(jsonObject.getInt("status_code")==200){
					
						progressdialog.cancel();
						
						Toast.makeText(ResetPassword.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show();
						
						Intent intent=new Intent(ResetPassword.this,SigninActivity.class);
						startActivity(intent);
						finish();
						
					}else if(jsonObject.getInt("status_code")==198){
						
						progressdialog.cancel();
						Toast.makeText(ResetPassword.this, "OTP didn't match!!", Toast.LENGTH_SHORT).show();
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
					progressdialog.cancel();
				}
			}
			
			@Override
			public void onFailure(Exception exception) {
				progressdialog.cancel();
				Toast.makeText(ResetPassword.this, "Error in response!!", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
