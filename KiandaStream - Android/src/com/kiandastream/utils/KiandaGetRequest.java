package com.kiandastream.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
/*
 * this class is used for get JSON data from given web services url
 */
import com.kiandastream.SplashActivity;

public class KiandaGetRequest {

	// get JSON from given url

	KiandaCallBack vcunnectCallBack;
	
	private String tag_json_obj = "jobj_req";

	Context activity;

	public KiandaGetRequest(Context activity) {

		this.activity = activity;
	}

	public void executeRequest(String url, final KiandaCallBack vcunnectCallBack) {

		this.vcunnectCallBack = vcunnectCallBack;

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject arg0) {
						
						JSONObject jsonObject = arg0;
						
						if (jsonObject.has("status_code")) {
							try {
								if (jsonObject.getString("status_code").contains("200")) {
									vcunnectCallBack.onSuccess(jsonObject);
								} else if(jsonObject.getString("status_code").contains("201")) {
									vcunnectCallBack.onSuccess(jsonObject);
								}else if(jsonObject.getString("status_code").contains("198")){
									vcunnectCallBack.onSuccess(jsonObject);
								}else{
									vcunnectCallBack.onFailure(new Exception(jsonObject.toString()));
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							vcunnectCallBack.onFailure(new Exception(jsonObject.toString()));
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						vcunnectCallBack.onFailure(arg0);
					}
				}){
			@Override
			public Map<String, String> getHeaders()
					throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/json");
				return super.getHeaders();
			}
		};
		
		jsonObjectRequest.setRetryPolicy((RetryPolicy) new DefaultRetryPolicy(
               5000, 
               DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
               DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));   
		
		SplashActivity.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);

	}

	
}
