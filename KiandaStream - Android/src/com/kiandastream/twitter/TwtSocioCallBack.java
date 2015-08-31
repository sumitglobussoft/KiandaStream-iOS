package com.kiandastream.twitter;

public interface TwtSocioCallBack {

	public void onSuccess(TwtSocioUserDatas twtSocioUserDatas);

	public void onFailure(Exception exception);

}
