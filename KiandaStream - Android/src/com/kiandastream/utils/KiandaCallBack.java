package com.kiandastream.utils;

import org.json.JSONObject;

public interface KiandaCallBack {

	public void onSuccess(JSONObject result);

	public void onFailure(Exception exception);

}
