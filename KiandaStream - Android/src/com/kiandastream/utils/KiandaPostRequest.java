package com.kiandastream.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/*
 * this class is used for get Json data from given web services url
 */

public class KiandaPostRequest {

	// get Json from given url

	KiandaCallBack vcunnectCallBack;

	private String tag_json_obj = "jobj_req";
	
	Context activity;

	public KiandaPostRequest(Context activity) {

		this.activity = activity;
	}

	public void executeRequest(String url,
			 final java.util.Map<String,String> nameValuePair,
			final KiandaCallBack vcunnectCallBack) {

		this.vcunnectCallBack = vcunnectCallBack;

		RequestQueue queue = Volley.newRequestQueue(activity);
		StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(response);
					vcunnectCallBack.onSuccess(jsonObject);
				} catch (JSONException e) {
					vcunnectCallBack.onFailure(e);
				}
			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				vcunnectCallBack.onFailure(error);
			}
		}){
			protected java.util.Map<String,String> getParams() throws AuthFailureError {
	            return nameValuePair;
			};
		};
		sr.setRetryPolicy((RetryPolicy) new DefaultRetryPolicy(
                2500, 
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));   
		
		queue.add(sr);

	}

	// check whether network is available or not
	public boolean isNetworkAvailable(Context activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
}
