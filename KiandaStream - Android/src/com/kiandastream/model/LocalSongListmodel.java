package com.kiandastream.model;

import java.util.Random;

public class LocalSongListmodel {

	String  userID;

	long feedtime;

	int feedId;

	String feedImagePath;

	String feedText;


	public LocalSongListmodel() {
	}


	public LocalSongListmodel(String userID,  String feedText, String feedImagePath, long feedtime) {

		this.userID = userID;
		this.feedtime = feedtime;

		this.feedId = new Random().nextInt();

		if (this.feedId < 0) {

			this.feedId = -this.feedId;
		}

		this.feedImagePath = feedImagePath;

		this.feedText =feedText;

	}

	public LocalSongListmodel(int feedId , String userID,  String feedText, String feedImagePath, long feedtime)
	{	
		this.feedId = feedId;
		this.userID = userID;
		this.feedText =feedText;
		this.feedImagePath = feedImagePath;
		this.feedtime = feedtime;		
	}


	public String getUserID() {
		return userID;
	}


	public void setUserID(String userID) {
		this.userID = userID;
	}


	public long getFeedtime() {
		return feedtime;
	}


	public void setFeedtime(long feedtime) {
		this.feedtime = feedtime;
	}


	public int getFeedId() {
		return feedId;
	}


	public void setFeedId(int feedId) {
		this.feedId = feedId;
	}


	public String getFeedImagePath() {
		return feedImagePath;
	}


	public void setFeedImagePath(String feedImagePath) {
		this.feedImagePath = feedImagePath;
	}


	public String getFeedText() {
		return feedText;
	}


	public void setFeedText(String feedText) {
		this.feedText = feedText;
	}

}
