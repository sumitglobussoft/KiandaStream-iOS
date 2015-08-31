package com.kiandastream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.utils.KiandaCallBack;
import com.kiandastream.utils.KiandaGetRequest;
import com.kiandastream.utils.MyCustomProgressDialog;

public class EnterOTPActivity extends Activity {

	EditText editotp1, editotp2, editotp3, editotp4, editotp5, editotp6;
	TextView invalidotp,resendotptext,checkmail;
	StringBuilder sb = new StringBuilder();
	private String emailotp;
	ProgressDialog progressdialog;
	private Effectstype effect;
	Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_enterotp);
		emailotp=MainSingleTon.otpmail;

		editotp1 = (EditText) findViewById(R.id.otpedit1);
		editotp2 = (EditText) findViewById(R.id.otpedit2);
		editotp3 = (EditText) findViewById(R.id.otpedit3);
		editotp4 = (EditText) findViewById(R.id.otpedit4);
		editotp5 = (EditText) findViewById(R.id.otpedit5);
		editotp6 = (EditText) findViewById(R.id.otpedit6);
		
		invalidotp = (TextView) findViewById(R.id.invalidotptext);
		invalidotp.setVisibility(View.INVISIBLE);
		resendotptext = (TextView) findViewById(R.id.resendotp);
		checkmail = (TextView) findViewById(R.id.checkotpemail);

		checkmail.setText("to "+MainSingleTon.otpemail);
		
		editotp1.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (sb.length() == 0 & editotp1.length() == 1) {
					sb.append(s);
					editotp1.clearFocus();
					editotp2.requestFocus();
					editotp2.setCursorVisible(true);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (sb.length() == 1) {
					sb.deleteCharAt(0);
				}
			}
			public void afterTextChanged(Editable s) {
				if (sb.length() == 0) {
					editotp1.requestFocus();
				}
			}
		});

		editotp2.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (sb.length() == 1 & editotp2.length() == 1) {
					sb.append(s);
					editotp2.clearFocus();
					editotp3.requestFocus();
					editotp3.setCursorVisible(true);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (sb.length() == 2) {
					sb.deleteCharAt(1);
				}
			}
			public void afterTextChanged(Editable s) {
				if (sb.length() == 1) {
					editotp2.requestFocus();
				}
			}
		});

		editotp3.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (sb.length() == 2 & editotp3.length() == 1) {
					sb.append(s);
					editotp3.clearFocus();
					editotp4.requestFocus();
					editotp4.setCursorVisible(true);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (sb.length() == 3) {
					sb.deleteCharAt(2);
				}
			}
			public void afterTextChanged(Editable s) {
				if (sb.length() == 2) {
					editotp3.requestFocus();
				}
			}
		});

		editotp4.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (sb.length() == 3 & editotp4.length() == 1) {
					sb.append(s);
					editotp4.clearFocus();
					editotp5.requestFocus();
					editotp5.setCursorVisible(true);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (sb.length() == 4) {
					sb.deleteCharAt(3);
				}
			}
			public void afterTextChanged(Editable s) {
				if (sb.length() == 3) {
					editotp4.requestFocus();
				}
			}
		});

		editotp5.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (sb.length() == 4 & editotp5.length() == 1) {
					sb.append(s);
					editotp5.clearFocus();
					editotp6.requestFocus();
					editotp6.setCursorVisible(true);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (sb.length() == 5) {
					sb.deleteCharAt(4);
				}
			}
			public void afterTextChanged(Editable s) {
				if (sb.length() == 4) {
					editotp5.requestFocus();
				}
			}
		});

		editotp6.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (sb.length() == 5 & editotp6.length() == 1) {
					sb.append(s);
					editotp6.clearFocus();
					CheckOtp(sb.toString());
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (sb.length() == 6) {
					sb.deleteCharAt(5);
				}
			}
			public void afterTextChanged(Editable s) {
				if (sb.length() == 5) {
					editotp6.requestFocus();
				}
			}
		});
		
		resendotptext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				forgotPass(MainSingleTon.otpemail);
			}
		});
	}
	
	private void CheckOtp(final String otp){
		
		progressdialog = new MyCustomProgressDialog(EnterOTPActivity.this);
		progressdialog.setCancelable(false);
		progressdialog.show();
		
		KiandaGetRequest getRequest=new KiandaGetRequest(EnterOTPActivity.this);
		
		String parameter =  MainSingleTon.Main_url+"t=verifyotp&email="+MainSingleTon.otpemail+"&optmail="
						+otp+"&optresponse="+MainSingleTon.otpmail;
		
		System.out.println("parameter = "+parameter);
		
		getRequest.executeRequest(parameter, new KiandaCallBack() {
			@Override
			public void onSuccess(JSONObject result) {
				
				System.out.println("Result from otp= "+result.toString());
				
				try {
					JSONObject jsonObject=result;
					
					if(jsonObject.getInt("status_code")==200){
					
						progressdialog.cancel();
						MainSingleTon.finalotp = jsonObject.getString("data");
						
						Intent intent=new Intent(EnterOTPActivity.this,ResetPassword.class);
						startActivity(intent);
						finish();
						
					}else if(jsonObject.getInt("status_code")==198){
						
						invalidotp.setVisibility(View.VISIBLE);
						
						editotp1.setText("");
						editotp2.setText("");
						editotp3.setText("");
						editotp4.setText("");
						editotp5.setText("");
						editotp6.setText("");
						
						editotp1.requestFocus();
						
						sb.delete(0, 5);
						
						progressdialog.cancel();
						Toast.makeText(EnterOTPActivity.this, "OTP didn't match!!", Toast.LENGTH_SHORT).show();
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
					progressdialog.cancel();
				}
			}
			
			@Override
			public void onFailure(Exception exception) {
				progressdialog.cancel();
				Toast.makeText(EnterOTPActivity.this, "Error in response!!", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void forgotPass(final String email){
		
		editotp1.setText("");
		editotp2.setText("");
		editotp3.setText("");
		editotp4.setText("");
		editotp5.setText("");
		editotp6.setText("");
		editotp1.requestFocus();
		
		sb.delete(0, 5);
		System.out.println("SB value is = "+sb.toString().trim());
		
		progressdialog = new MyCustomProgressDialog(EnterOTPActivity.this);
		progressdialog.setCancelable(false);
		progressdialog.show();
		
		KiandaGetRequest getRequest=new KiandaGetRequest(EnterOTPActivity.this);
		
		String paramater=MainSingleTon.Main_url+"t=forgetpassword&email="+email;
		
		getRequest.executeRequest(paramater, new KiandaCallBack() {
			@Override
			public void onSuccess(JSONObject result) {
				
					try {
						
						JSONObject object=result;
						
						if(object.getInt("status_code")==200){
							
							MainSingleTon.otpmail=object.getString("data");
							MainSingleTon.otpemail=email;
							progressdialog.cancel();
							
							Toast.makeText(EnterOTPActivity.this, "Check and enter otp sent your mail.", Toast.LENGTH_SHORT).show();
							
						}else if(object.getInt("status_code")==198){
							
							progressdialog.cancel();
							Toast.makeText(EnterOTPActivity.this, "Email Id does not exist!!", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						progressdialog.cancel();
						Toast.makeText(EnterOTPActivity.this, "Exception in service!!", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
			}
			
			@Override
			public void onFailure(Exception exception) {
				progressdialog.cancel();
				exception.printStackTrace();
				Toast.makeText(EnterOTPActivity.this, "Response Error!!", Toast.LENGTH_SHORT).show();
			}
		});
		
	}
}
